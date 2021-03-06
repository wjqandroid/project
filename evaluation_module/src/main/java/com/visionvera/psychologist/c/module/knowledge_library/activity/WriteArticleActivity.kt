package com.visionvera.psychologist.c.module.knowledge_library.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lei.lib.java.rxcache.RxCache
import com.lei.lib.java.rxcache.entity.CacheResponse
import com.lei.lib.java.rxcache.util.RxUtil
import com.lxj.xpopup.XPopup
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.lzy.imagepicker.ui.ImageGridActivity
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.cos.xml.exception.CosXmlClientException
import com.tencent.cos.xml.exception.CosXmlServiceException
import com.tencent.cos.xml.listener.CosXmlResultListener
import com.tencent.cos.xml.model.CosXmlRequest
import com.tencent.cos.xml.model.CosXmlResult
import com.visionvera.library.base.BaseMVPActivity
import com.visionvera.library.base.Constant
import com.visionvera.library.base.bean.BaseResponseBean3
import com.visionvera.library.base.mvp.view.IBaseView
import com.visionvera.psychologist.c.R
import com.visionvera.psychologist.c.databinding.ActivityWriteArticalBinding
import com.visionvera.psychologist.c.module.knowledge_library.bean.AddArticleRequestBean
import com.visionvera.psychologist.c.module.knowledge_library.bean.DraftBean
import com.visionvera.psychologist.c.module.knowledge_library.contract.IContract
import com.visionvera.psychologist.c.module.knowledge_library.event.PublishArticleEvent
import com.visionvera.psychologist.c.module.knowledge_library.event.SaveDraftEvent
import com.visionvera.psychologist.c.module.knowledge_library.pop.CommonCenterTipsPopup
import com.visionvera.psychologist.c.module.knowledge_library.presenter.WriteArticleActivityPresenter
import com.visionvera.psychologist.c.module.knowledge_library.richeditor.CommonPopupWindow
import com.visionvera.psychologist.c.module.knowledge_library.richeditor.KeyBoardUtils
import com.visionvera.psychologist.c.module.knowledge_library.richeditor.RichEditor
import com.visionvera.psychologist.c.module.knowledge_library.richeditor.RichUtils
import com.visionvera.psychologist.c.module.usercenter.bean.FeedBackImgBean
import com.visionvera.psychologist.c.utils.cos.TencentCosManager
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import io.reactivex.functions.Consumer
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author: ?????????
 * @date: 2020/6/29 14:19
 * QQ:1052374416
 * ??????:?????????
 * ????????????:
 */
class WriteArticleActivity : BaseMVPActivity<IContract.WriteArticleActivity.View, WriteArticleActivityPresenter>() {
    private var currentUrl = ""

    //???????????????pop
    private var popupWindow: CommonPopupWindow? = null
    lateinit var rxPermissions: RxPermissions

    //????????????????????????????????????null????????????????????????????????????
    var intentBean = IntentBean()
    lateinit var viewBinding: ActivityWriteArticalBinding
    private val selectImages = java.util.ArrayList<ImageItem?>()
    var picPathList = java.util.ArrayList<FeedBackImgBean>()

    companion object {
        fun startActivity(context: Context, intentBean: IntentBean) {
            val intent = Intent(context, WriteArticleActivity::class.java)
            intent.putExtra(Constant.IntentKey.IntentBean, intentBean)
            context.startActivity(intent)
        }

        data class IntentBean(/*??????id*/var id: Int = -1,/*????????????*/var title: String = "",/*???????????????????????? 0????????? 1????????? 2????????????*/var comeType: Int = 0,/*??????id*/var draftId: Int = -1,/*????????????*/var content: String = "") : Serializable {
        }
    }


    /**
     * ??????view?????????????????????????????????new???????????????????????????
     * ??????presenter
     * ???????????????ui?????????????????????????????????????????????mvp???????????????????????????
     */
    override fun initMVP() {
        mView = object : IContract.WriteArticleActivity.View {
            override fun onAddArticle(responseBean: BaseResponseBean3<*>?, resultType: IBaseView.ResultType, errorMsg: String?) {
                hideDialog()

                when (resultType) {
                    IBaseView.ResultType.NET_ERROR -> {
                        //???????????????
                        ToastUtils.showLong(getString(R.string.base_module_net_error))
                    }
                    IBaseView.ResultType.SERVER_ERROR -> {
                        ToastUtils.showShort(errorMsg)
                    }
                    IBaseView.ResultType.SERVER_NORMAL_DATAYES, IBaseView.ResultType.SERVER_NORMAL_DATANO -> {
                        ToastUtils.showShort("????????????")
                        //??????????????????????????????.????????????????????????????????????
                        val busEvent = PublishArticleEvent()
                        LiveEventBus
                                .get(PublishArticleEvent::class.java)
                                .post(busEvent)


                        val cacheKey = Constant.Cache.draftsKey
                        //????????????
                        val type = object : TypeToken<java.util.ArrayList<DraftBean>>() {}.type
                        RxCache.getInstance()
                                .get<java.util.ArrayList<DraftBean>>(cacheKey, false, type) //key:?????????key update:???????????????????????????????????????NULL
                                .compose(RxUtil.io_main())
                                .subscribe(Consumer<CacheResponse<java.util.ArrayList<DraftBean>>> { listCacheResponse ->
                                    listCacheResponse.data?.let {
                                        //???????????????
                                        Logger.i("??????????????????  ${cacheKey}:" + listCacheResponse.data.toString())
                                        for (index in it.size - 1 downTo 0) {
                                            if (it.get(index).id == intentBean.draftId) {
                                                it.removeAt(index)
                                                finalSaveSoft(cacheKey, it)
                                                break
                                            }
                                        }

                                    } ?: let {
                                        //??????????????????
                                        Logger.i("??????????????????:" + cacheKey)
                                    }

                                }, Consumer { throwable ->
                                    throwable.printStackTrace()
                                    Logger.i("?????????????????? ${cacheKey}:$throwable")
                                })

                        finish()
                    }
                }
            }


            override fun onComplete() {}
        }
        mPresenter = WriteArticleActivityPresenter(activity, mView)
    }

    private fun finalSaveSoft(cacheKey: String, list: java.util.ArrayList<DraftBean>) {
        RxCache.getInstance()
                .put(cacheKey, list, -1) //key:?????????key data:??????????????? time:?????????????????????
                .compose(RxUtil.io_main()) //????????????
                .subscribe({ aBoolean ->
                    if (aBoolean) {
                        Logger.i("????????????  ${cacheKey}" + list)
                        ToastUtils.showShort("??????????????????")
                        //??????????????????????????????.????????????????????????????????????
                        val busEvent = SaveDraftEvent()
                        LiveEventBus
                                .get(SaveDraftEvent::class.java)
                                .post(busEvent)
                        finish()

                    }
                    hideDialog()
                }) { throwable ->
                    throwable.printStackTrace()
                    hideDialog()
                    ToastUtils.showShort("??????????????????")
                }
    }

    private fun checkPicAndAddArticle() {
        if (checkIfUpAllPic()) {
            //????????????????????????
            picPathList?.let {
                var newHtml: String = viewBinding.richEditor.getHtml()
                for (i in picPathList.indices) {

                    picPathList.get(i).setUrl(picPathList.get(i).url)
                    //??????????????????????????????????????????
                    newHtml = newHtml.replace(picPathList.get(i).picPath, picPathList.get(i).url)

                }
                if (picPathList.size > 0) {
                    viewBinding.richEditor.html = newHtml
                }

            }

            var title = viewBinding.editName.text.toString().trim()
            var summary = getSummary()
            var picUrls = ""
            if (picPathList.size > 0) {
                picUrls = picPathList.get(0).getUrl() + ""
            }
            var htmlHead = "<html>\n" +
                    "<head>\n" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                    "<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">\n" +
                    "<title></title>\n" +
                    "<meta name=\"Generator\" content=\"Cocoa HTML Writer\">\n" +
                    "<style type=\"text/css\">\n" +
                    "p.p1 {margin: 0.0px 0.0px 0.0px 0.0px; font: 16.0px '.AppleSimplifiedChineseFont'}\n" +
                    "span.s1 {font-family: '.PingFangSC-Regular'; font-weight: normal; font-style: normal; font-size: 16.00px; letter-spacing: 1.2px}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>"
            var htmlFoot = "</body>\n" +
                    "</html>"
            var content = htmlHead + viewBinding.richEditor.html + htmlFoot
            showLoadingDialog()
            net_addArticle(title, summary, picUrls, content)
        } else {
            uploadAllPicOneByOne()
        }
    }

    override fun setContentView(): Boolean {
        viewBinding = ActivityWriteArticalBinding.inflate(layoutInflater)
        val view: View = viewBinding.getRoot()
        setContentView(view)
        return true
    }


    /**
     * ?????????????????????initview???initdata??????????????????????????????????????????
     * ??????????????????
     */
    override fun doYourself() {
        parseIntent()
        rxPermissions = RxPermissions(this@WriteArticleActivity)
        initView()
        initListener()
        initData()
    }

    private fun initView() {
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true)
                //??????????????????????????????,?????????????????????,????????????????????????
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                .keyboardEnable(true)
                .init()

        if (intentBean.comeType == 0) {
            //?????????
        } else if (intentBean.comeType == 1) {
            //?????????
            if (!intentBean.title.isNullOrBlank()) {
                //??????
                viewBinding.editName.setText(intentBean.title)
                viewBinding.editName.isEnabled = false
            }
        } else if (intentBean.comeType == 2) {
            //????????????
            viewBinding.editName.setText(intentBean.title)
            viewBinding.richEditor.setHtml(intentBean.content)
        }
        initRichEditor()
        initPop()
    }


    /**
     * ???????????????????????????
     */
    private fun initRichEditor() {
        //??????????????????????????????
        viewBinding.richEditor.setEditorFontSize(16)
        //??????????????????????????????
        viewBinding.richEditor.setEditorFontColor(resources.getColor(R.color.common_text))
        //?????????????????????
        viewBinding.richEditor.setEditorBackgroundColor(resources.getColor(R.color.white))
        //???????????????padding
        viewBinding.richEditor.setPadding(0, 10, 0, 0)

        //??????????????????
        viewBinding.richEditor.setPlaceholder("????????????????????????")

        //???????????????????????????
        viewBinding.richEditor.setOnTextChangeListener(object : RichEditor.OnTextChangeListener {
            override fun onTextChange(text: String) {
                Logger.i("?????????????????????:" + text)
                var html2Text = RichUtils.Html2Text(text)

//                if (html2Text.length < 2000) {
//                    viewBinding.tvInputCount.setText("" + html2Text.length + "/2000")
//                    viewBinding.richEditor.setInputEnabled(true)
//                } else {
//                    viewBinding.tvInputCount.setText("2000/2000")
//                    viewBinding.richEditor.setInputEnabled(false)
//                }
            }
        })
        viewBinding.editName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {

            }
        })
        viewBinding.richEditor.setOnDecorationChangeListener(object : RichEditor.OnDecorationStateListener {
            override fun onStateChangeListener(text: String?, types: List<RichEditor.Type>) {
                val flagArr = ArrayList<String>()
                for (i in types.indices) {
                    flagArr.add(types[i].name)
                }
                Logger.i("flagArr:" + flagArr)
                if (flagArr.contains("BOLD")) {

                    viewBinding.buttonBold.setImageResource(R.drawable.article_bold);
                    viewBinding.buttonBold.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.base_module_theme)));
                } else {
                    viewBinding.buttonBold.setImageResource(R.drawable.article_bold);
                    viewBinding.buttonBold.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.bgtint)));
                }
                if (flagArr.contains("ITALIC")) {
                    viewBinding.buttonXieti.setImageResource(R.drawable.article_xieti);
                    viewBinding.buttonXieti.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.base_module_theme)));
                } else {
                    viewBinding.buttonXieti.setImageResource(R.drawable.article_xieti);
                    viewBinding.buttonXieti.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.bgtint)));
                }
                if (flagArr.contains("UNDERLINE")) {
                    viewBinding.buttonUnderline.setImageResource(R.drawable.article_underline);
                    viewBinding.buttonUnderline.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.base_module_theme)));
                } else {
                    viewBinding.buttonUnderline.setImageResource(R.drawable.article_underline);
                    viewBinding.buttonUnderline.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.bgtint)));
                }
                if (flagArr.contains("ORDEREDLIST")) {

                    viewBinding.buttonListUl.setImageResource(R.drawable.article_order_dian);
                    viewBinding.buttonListUl.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.bgtint)));
                    viewBinding.buttonListOl.setImageResource(R.drawable.article_order_number);
                    viewBinding.buttonListOl.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.base_module_theme)));
                } else {
                    viewBinding.buttonListOl.setImageResource(R.drawable.article_order_number);
                    viewBinding.buttonListOl.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.bgtint)));
                }
                if (flagArr.contains("UNORDEREDLIST")) {
                    viewBinding.buttonListOl.setImageResource(R.drawable.article_order_number)
                    val up = ContextCompat.getDrawable(baseContext, R.drawable.article_order_dian)
                    val drawableUp = DrawableCompat.wrap(up!!)
                    DrawableCompat.setTint(drawableUp, ContextCompat.getColor(baseContext, R.color.base_module_theme))
                    viewBinding.buttonListUl.setImageDrawable(drawableUp)

                    viewBinding.buttonListOl.setImageResource(R.drawable.article_order_number);
                    viewBinding.buttonListOl.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.bgtint)));
                    viewBinding.buttonListUl.setImageResource(R.drawable.article_order_dian);
                    viewBinding.buttonListUl.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.base_module_theme)));
                } else {
                    viewBinding.buttonListUl.setImageResource(R.drawable.article_order_dian);
                    viewBinding.buttonListUl.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(baseContext, R.color.bgtint)));
                }

            }
        })
        //????????????
        viewBinding.richEditor.setImageClickListener(object : RichEditor.ImageClickListener {
            override fun onImageClick(imageUrl: String) {
                currentUrl = imageUrl
                popupWindow?.showBottom(viewBinding.llRoot, 0.5f)
            }
        })
    }

    private fun initPop() {
        val view: View = LayoutInflater.from(this@WriteArticleActivity).inflate(R.layout.newapp_pop_picture, null)
        view.findViewById<View>(R.id.linear_cancle).setOnClickListener { v: View? -> popupWindow!!.dismiss() }
        view.findViewById<View>(R.id.linear_editor).setOnClickListener { v: View? ->
            //????????????
            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(Consumer { aBoolean: Boolean ->
                if (aBoolean) {
                    if (ActivityCompat.checkSelfPermission(this@WriteArticleActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@WriteArticleActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        val intent = Intent(this@WriteArticleActivity, UCropActivity::class.java)
                        intent.putExtra("filePath", currentUrl)
                        val destDir = filesDir.absolutePath.toString()
                        val fileName = "SampleCropImage" + System.currentTimeMillis() + ".jpg"
                        intent.putExtra("outPath", destDir + fileName)
                        startActivityForResult(intent, 11)
                        popupWindow!!.dismiss()
                    }
                } else {
                    Toast.makeText(this@WriteArticleActivity, "?????????????????????", Toast.LENGTH_SHORT).show()
                }
            })
        }
        view.findViewById<View>(R.id.linear_delete_pic).setOnClickListener { v: View? ->
            //????????????
            val removeUrl = "<img src=\"$currentUrl\" alt=\"dachshund\" width=\"100%\"><br>"
            val newUrl: String = viewBinding.richEditor.getHtml().replace(removeUrl, "")
            currentUrl = ""
            viewBinding.richEditor.setHtml(newUrl)
            if (RichUtils.isEmpty(viewBinding.richEditor.getHtml())) {
                viewBinding.richEditor.setHtml("")
            }
            popupWindow!!.dismiss()
        }
        popupWindow = CommonPopupWindow.Builder(this@WriteArticleActivity)
                .setView(view)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOutsideTouchable(true) //???????????????????????????
                .setAnimationStyle(R.style.pop_animation) //??????popWindow???????????????
                .create()
        popupWindow?.setOnDismissListener(PopupWindow.OnDismissListener { viewBinding.richEditor.setInputEnabled(true) })
    }

    private fun initData() {
    }


    private fun initListener() {
        viewBinding.tvCancel.setOnClickListener {
            if (viewBinding.editName.text.isNullOrBlank() && viewBinding.richEditor.html.isNullOrBlank()) {
                finish()
            } else {
                showTipsPop()
            }
        }
        viewBinding.llPublish.setOnClickListener {
            //??????
            Logger.i("html:" + viewBinding.richEditor.getHtml())
            if (viewBinding.editName.text.isNullOrBlank()) {
                ToastUtils.showShort("???????????????")
                return@setOnClickListener
            }
            if (viewBinding.richEditor.html.isNullOrBlank()) {
                ToastUtils.showShort("?????????????????????")
                return@setOnClickListener
            }

            val arrayList = RichUtils.returnImageUrlsFromHtml(viewBinding.richEditor.getHtml())
            picPathList.clear()
            arrayList.forEachIndexed { index, s ->
                var imageBean = FeedBackImgBean(FeedBackImgBean.IMG, s)
                if (s.startsWith("http") || s.startsWith("https")) {
                    imageBean.url = s
                }
                picPathList.add(imageBean)
            }
            checkPicAndAddArticle()

        }
        viewBinding.buttonImage.setOnClickListener {
            //?????????
            if (!TextUtils.isEmpty(viewBinding.richEditor.getHtml())) {
                val arrayList = RichUtils.returnImageUrlsFromHtml(viewBinding.richEditor.getHtml())
                if (arrayList != null && arrayList.size >= 9) {
                    Toast.makeText(this@WriteArticleActivity, "????????????9?????????~", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe { aBoolean: Boolean ->
                if (aBoolean) {
                    if (ActivityCompat.checkSelfPermission(this@WriteArticleActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this@WriteArticleActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectImage(104, selectImages)
                        KeyBoardUtils.closeKeybord(viewBinding.editName, this@WriteArticleActivity)
                    }
                } else {
                    Toast.makeText(this@WriteArticleActivity, "?????????????????????~", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewBinding.buttonBold.setOnClickListener {
            //??????
            againEdit()
            viewBinding.richEditor.setBold()
        }
        viewBinding.buttonXieti.setOnClickListener {
            //??????
            againEdit()
            viewBinding.richEditor.setItalic()
        }
        viewBinding.buttonUnderline.setOnClickListener {
            //????????????
            againEdit()
            viewBinding.richEditor.setUnderline()
        }
        viewBinding.buttonListUl.setOnClickListener {
            //?????????????????????
            againEdit()
            viewBinding.richEditor.setBullets()
        }
        viewBinding.buttonListOl.setOnClickListener {
            //????????????????????????
            againEdit()
            viewBinding.richEditor.setNumbers()
        }
        viewBinding.buttonRichUndo.setOnClickListener {
            //??????
            viewBinding.richEditor.undo()
        }
        viewBinding.buttonRichDo.setOnClickListener {
            //?????????
            viewBinding.richEditor.redo()
        }


    }

    private fun showTipsPop() {
        XPopup.Builder(this)
                .moveUpToKeyboard(false)//????????????????????????????????????????????????????????????
                .asCustom(CommonCenterTipsPopup(this, "?????????????????????", "??????", "?????????", "??????", object : CommonCenterTipsPopup.ResultListener {
                    override fun onCkecked(action: String?) {
                        when (action) {
                            "cancel" -> {
                                //???
                                finish()
                            }
                            "confirm" -> {
                                //???
                                val softBean = DraftBean()
                                softBean.id = intentBean.draftId
                                softBean.title = viewBinding.editName.text.toString().trim()
                                softBean.content = viewBinding.richEditor.html
                                softBean.summary = getSummary()
                                softBean.time = System.currentTimeMillis()
                                saveSoft(softBean)

                            }

                        }
                    }

                    override fun onCreated() {

                    }

                }))
                .show()
    }

    /**
     * ?????????????????????
     */
    private fun saveSoft(draftBean: DraftBean) {
        showLoadingDialog("??????????????????", false)
        val cacheKey = Constant.Cache.draftsKey
        //????????????
        val type = object : TypeToken<java.util.ArrayList<DraftBean>>() {}.type
        RxCache.getInstance()
                .get<java.util.ArrayList<DraftBean>>(cacheKey, false, type) //key:?????????key update:???????????????????????????????????????NULL
                .compose(RxUtil.io_main())
                .subscribe(Consumer<CacheResponse<java.util.ArrayList<DraftBean>>> { listCacheResponse ->
                    listCacheResponse.data?.let {
                        //???????????????
                        Logger.i("??????????????????  ${cacheKey}:" + listCacheResponse.data.toString())
                        var isNewDraft = true
                        it.forEachIndexed { index, bean ->
                            if (bean.id == draftBean.id && draftBean.id != -1) {
                                //???????????????
                                bean.id = draftBean.id
                                bean.title = draftBean.title
                                bean.content = draftBean.content
                                bean.summary = draftBean.summary
                                bean.time = draftBean.time
                                isNewDraft = false
                                return@forEachIndexed
                            }
                        }
                        //??????????????????
                        it.sortByDescending { softBean -> softBean.time }
                        if (isNewDraft) {
                            draftBean.id = it.get(0).id + 1
                            //?????????
                            it.add(0, draftBean)
                        }
                        finalSaveSoft(cacheKey, it)
                    } ?: let {
                        //??????????????????
                        Logger.i("??????????????????:" + cacheKey)
                        hideDialog()
                        var arrayList = java.util.ArrayList<DraftBean>()
                        arrayList.add(draftBean)
                        finalSaveSoft(cacheKey, arrayList)
                    }

                }, Consumer { throwable ->
                    throwable.printStackTrace()
                    Logger.i("?????????????????? ${cacheKey}:$throwable")
                    hideDialog()
                })
    }

    /**
     * ????????????????????????
     * ??????????????????.??????????????????????????????????????????.?????????????????????
     */
    fun uploadAllPicOneByOne() {
        if (!checkIfUpAllPic()) {
            //????????????????????????
            for (i in picPathList.indices) {
                val imgBean: FeedBackImgBean = picPathList.get(i)
                if (imgBean.itemType == FeedBackImgBean.IMG) {
                    if (!TextUtils.isEmpty(imgBean.picPath)) {
                        if (TextUtils.isEmpty(imgBean.url) && imgBean.id == 0) {
                            //??????????????????????????????????????????
                            //??????????????????cos,?????????????????????id,id?????????list,?????????id???????????????
                            uploadPicByCos(imgBean.picPath, i)
                            return
                        }
                    }
                }
            }
        }
    }

    /**
     * @param picPath
     * @param position
     */
    private fun uploadPicByCos(picPath: String, position: Int) {
        showLoadingDialog("?????????...", false)
        var houzhui = "png"
        val strArr = picPath.split("\\.").toTypedArray()
        if (strArr.size > 0) {
            houzhui = strArr[strArr.size - 1]
        }
        val cosPath = System.currentTimeMillis().toString() + "." + houzhui
        TencentCosManager.getInstance(this).upload(cosPath, picPath, { complete, target -> }, object : CosXmlResultListener {
            override fun onSuccess(request: CosXmlRequest, result: CosXmlResult) {
                activity.runOnUiThread { // ???????????????


                    //??????????????????
                    picPathList[position].url = result.accessUrl
                    checkPicAndAddArticle()
                }

            }

            override fun onFail(request: CosXmlRequest, exception: CosXmlClientException, serviceException: CosXmlServiceException) {
                activity.runOnUiThread {
                    hideDialog()
                    ToastUtils.showShort("????????????")
                }
            }
        }) {
            activity.runOnUiThread {
                hideDialog()
                ToastUtils.showShort("????????????")
            }
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????id
     */
    private fun checkIfUpAllPic(): Boolean {
        var all = true
        for (i in picPathList.indices) {
            val imgBean: FeedBackImgBean = picPathList.get(i)
            if (imgBean.itemType == FeedBackImgBean.IMG) {
                if (!TextUtils.isEmpty(imgBean.picPath)) {
                    if (TextUtils.isEmpty(imgBean.url) && imgBean.id == 0) {
                        all = false
                    }
                }
            }
        }
        return all
    }


    private fun getSummary(): String {
        var summary = ""
        var html2Text = RichUtils.Html2Text(viewBinding.richEditor.html + "")
        if (html2Text.length >= 50) {
            summary = html2Text.subSequence(0, 50).toString()
        } else {
            summary = html2Text
        }
        return summary
    }

    private fun net_addArticle(title: String, summary: String, coverImageUrls: String, content: String) {
        var requestBean = AddArticleRequestBean()
        requestBean.title = title.trim()
        requestBean.summary = summary.trim()
        requestBean.coverImageUrls = coverImageUrls
        requestBean.content = content
        if (intentBean.comeType == 0) {
            //?????????
        } else if (intentBean.comeType == 1) {
            //?????????
            requestBean.parentId = intentBean.id
        } else if (intentBean.comeType == 2) {
            //????????????

        }
        mPresenter.addArticle(requestBean, this)
    }

    fun selectImage(requestCode: Int, imageItems: java.util.ArrayList<ImageItem?>?) {
        val imagePicker = ImagePicker.getInstance()
        imagePicker.isCrop = false
        imagePicker.isMultiMode = false
        imagePicker.isShowCamera = true
        val intent = Intent(this, ImageGridActivity::class.java)
        intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, imageItems)
        startActivityForResult(intent, requestCode)
    }

    private fun againEdit() {
        //????????????????????????????????????????????????????????????????????????????????????
        viewBinding.richEditor.focusEditor()
        KeyBoardUtils.openKeybord(viewBinding.editName, this@WriteArticleActivity)
    }

    private fun parseIntent() {
        //????????????
        val bean = intent.getSerializableExtra(Constant.IntentKey.IntentBean) as IntentBean?
        if (bean != null) {
            intentBean = bean
        }
        Logger.i(intentBean.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //??????????????????
            if (requestCode == 104) {
                selectImages.clear()
                val selects = data!!.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as java.util.ArrayList<ImageItem>
                selectImages.addAll(selects)
                againEdit()
//                selectImages[0]!!.path = "http://slyl-rmcp-cloud-1301295327.cos.ap-beijing.myqcloud.com/upload/images/MedicalCircle/1604544221341-6975-973.jpeg"
                viewBinding.richEditor.insertImage(selectImages[0]!!.path, "dachshund")
                KeyBoardUtils.openKeybord(viewBinding.editName, this@WriteArticleActivity)
                viewBinding.richEditor.postDelayed(Runnable {
                    if (viewBinding.richEditor != null) {
                        viewBinding.richEditor.scrollToBottom()
                    }
                }, 200)
            }
        } else if (resultCode == -1) {
            //?????????????????????
            if (requestCode == 11) {
                val outPath = data!!.getStringExtra(UCrop.EXTRA_OUTPUT_URI)
                if (!TextUtils.isEmpty(outPath)) {
                    val newHtml: String = viewBinding.richEditor.getHtml().replace(currentUrl, outPath)
                    viewBinding.richEditor.setHtml(newHtml)
                    currentUrl = ""
                }
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    override fun getLayoutId(): Int {
        return 0
    }

}