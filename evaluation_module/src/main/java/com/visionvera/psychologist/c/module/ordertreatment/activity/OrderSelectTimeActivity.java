//package com.visionvera.psychologist.c.module.ordertreatment.activity;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.text.Editable;
//import android.text.InputFilter;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.text.style.ForegroundColorSpan;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.alibaba.android.arouter.facade.annotation.Autowired;
//import com.alibaba.android.arouter.launcher.ARouter;
//import com.blankj.utilcode.util.SPUtils;
//import com.blankj.utilcode.util.ToastUtils;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.Target;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.chad.library.adapter.base.listener.OnItemChildClickListener;
//import com.chad.library.adapter.base.listener.OnItemClickListener;
//import com.google.gson.Gson;
//import com.lxj.xpopup.XPopup;
//import com.lxj.xpopup.core.BasePopupView;
//import com.lxj.xpopup.core.ImageViewerPopupView;
//import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener;
//import com.lxj.xpopup.interfaces.XPopupImageLoader;
//import com.orhanobut.logger.Logger;
//import com.tencent.cos.xml.exception.CosXmlClientException;
//import com.tencent.cos.xml.exception.CosXmlServiceException;
//import com.tencent.cos.xml.listener.CosXmlProgressListener;
//import com.tencent.cos.xml.listener.CosXmlResultListener;
//import com.tencent.cos.xml.model.CosXmlRequest;
//import com.tencent.cos.xml.model.CosXmlResult;
//import com.visionvera.library.arouter.ARouterConstant;
//import com.visionvera.library.arouter.service.IAccountService;
//import com.visionvera.library.base.BaseActivity;
//import com.visionvera.library.base.BaseMVPActivity;
//import com.visionvera.library.base.Constant;
//import com.visionvera.library.eventbus.commonbean.LoginEventBus;
//import com.visionvera.library.util.EmojiFilter;
//import com.visionvera.library.util.OneClickUtils;
//import com.visionvera.library.widget.dialog.CameraAlbumPopup1;
//import com.visionvera.library.widget.dialog.CenterPopup;
//import com.visionvera.library.widget.dialog.ProblemTypePopup;
//import com.visionvera.library.widget.dialog.TreatmentTypePopup;
//import com.visionvera.psychologist.c.R;
//import com.visionvera.psychologist.c.R2;
//import com.visionvera.psychologist.c.module.counselling.activity.OrderConsultSelectTimeActivity;
//import com.visionvera.psychologist.c.module.counselling.bean.TimeCalendarBean;
//import com.visionvera.psychologist.c.module.ordertreatment.bean.SubmitOrderRequestBean;
//import com.visionvera.psychologist.c.module.ordertreatment.bean.SubmitOrderResponseBean;
//import com.visionvera.psychologist.c.module.ordertreatment.contract.IContract;
//import com.visionvera.psychologist.c.module.ordertreatment.presenter.OrderSelectTimeActivityPresenter;
//import com.visionvera.psychologist.c.module.usercenter.activity.EditInfoActivity;
//import com.visionvera.psychologist.c.module.usercenter.adapter.FeedBackImgAdapter;
//import com.visionvera.psychologist.c.module.usercenter.bean.FeedBackImgBean;
//import com.visionvera.psychologist.c.module.usercenter.bean.InforMationBean;
//import com.visionvera.psychologist.c.module.usercenter.bean.UploadPicResponseBean;
//import com.visionvera.psychologist.c.utils.cos.SavePathRequestBean;
//import com.visionvera.psychologist.c.utils.cos.SavePathResponseBean;
//import com.visionvera.psychologist.c.utils.cos.TencentCosManager;
//import com.visionvera.psychologist.c.utils.photo.MyTakePhotoActivity;
//import com.visionvera.psychologist.c.widget.PerfectInformationPopup;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import rxhttp.wrapper.param.RxHttp;
//import top.zibin.luban.Luban;
//import top.zibin.luban.OnCompressListener;
//
//import static com.visionvera.library.base.Constant.RequestReturnCode.OrderConsultActivity_To_OrderConsultSelectTimeActivity_Code;
//
///**
// * @author: ?????????
// * @date: 2020-01-03 09:43
// * QQ:1052374416
// * ??????:????????????----???????????????????????????????????????
// * ????????????:
// */
//public class OrderSelectTimeActivity extends BaseMVPActivity<IContract.OrderSelectTimeActivity.View, OrderSelectTimeActivityPresenter> implements OnItemClickListener, OnItemChildClickListener {
//
//    @Autowired(name = ARouterConstant.Account.accountModuleSetvice)
//    IAccountService accountService;
//
//    @BindView(R2.id.evaluation_module_upload_appendix_recyclerview)
//    RecyclerView mRecy;
//
//    @BindView(R2.id.evaluation_module_upload_appendix_notice)
//    TextView upload_appendix_notice;
//
//    @BindView(R2.id.evaluation_module_select_time)
//    TextView select_time;
//
//    @BindView(R2.id.evaluation_module_select_type_tv)
//    TextView select_type_tv;
//
//    @BindView(R2.id.tv_title)
//    TextView tv_title;
//    @BindView(R.id.et_content)
//    EditText etContent;
//    @BindView(R.id.tv_text_count)
//    TextView tvTextCount;
//    @BindView(R.id.evaluation_module_select_problem_type_tv)
//    TextView select_problem_type_tv;
//    private List<FeedBackImgBean> picPathList = new ArrayList<>();
//    private FeedBackImgAdapter feedBackImgAdapter;
//    private BasePopupView cameraAlbumPopup;
//    private BasePopupView typePopup;
//    /**
//     * ?????????????????????
//     */
//    private BasePopupView basePopupView;
//
//    private TimeCalendarBean.ResultBean.DateListBean returnTimeBean;
//    /**
//     * ??????????????????
//     */
//    private int mType;
//
//    //????????????????????????????????????null????????????????????????????????????
//    public IntentBean intentBean = new IntentBean(0, 0, "????????????(??????)", "", "");
//
//
//    public static void startActivity(Context context, IntentBean intentBean) {
//
//        Intent intent = new Intent(context, OrderSelectTimeActivity.class);
//        intent.putExtra(Constant.IntentKey.IntentBean, intentBean);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.evaluation_module_activity_order_consult;
//    }
//
//    @Override
//    protected void doYourself() {
//        ARouter.getInstance().inject(this);
//        EventBus.getDefault().register(this);
//        parseIntent();
//
//        initView();
//
//    }
//
//
//    private void parseIntent() {
//        //????????????
//        IntentBean bean = (IntentBean) getIntent().getSerializableExtra(Constant.IntentKey.IntentBean);
//        if (bean != null) {
//            intentBean = bean;
//        }
//        Logger.i(intentBean.toString());
//    }
//
//
//    private void initView() {
//        tv_title.setText("????????????");
//        //??????????????????      ????????????(??????) 3   ????????????  4   ???????????? 2
//        //???????????? ????????????????????????
//        if (intentBean.selecTypeName.equals("????????????(??????)")) {
//            SpannableString mSpannableString = new SpannableString(intentBean.selecTypeName);
//            mSpannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.COLOR_BLACK_333333)), 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mSpannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.COLOR_ORANGE_FF782E)), 4, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            select_type_tv.setText(mSpannableString);
//            mType = 3;
//        } else if (intentBean.selecTypeName.equals("????????????")) {
//            mType = 4;
//            select_type_tv.setText(intentBean.selecTypeName);
//        } else if (intentBean.selecTypeName.equals("????????????")) {
//            mType = 2;
//            select_type_tv.setText(intentBean.selecTypeName);
//        }
//
//
//        mRecy.setLayoutManager(new GridLayoutManager(this, 4));
//        picPathList.add(new FeedBackImgBean(FeedBackImgBean.CAMERA, ""));
//
//        feedBackImgAdapter = new FeedBackImgAdapter(picPathList);
//        feedBackImgAdapter.setOnItemClickListener(this);
//        feedBackImgAdapter.addChildClickViewIds(R.id.evaluation_module_feedback_iv_delete);
//        feedBackImgAdapter.setOnItemChildClickListener(this);
//        mRecy.setAdapter(feedBackImgAdapter);
//        notifyPicTips();
//
//        basePopupView = new XPopup.Builder(this)
//                .asCustom(new PerfectInformationPopup(this, getString(R.string.evaluation_module_input_sex_birthday_idcard), () -> {
//                    //?????????????????????
//                    EditInfoActivity.startActivity(activity);
//                    basePopupView.dismiss();
//                }));
//        etContent.setFilters(new InputFilter[]{new EmojiFilter(), new InputFilter.LengthFilter(200)});
//        etContent.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                tvTextCount.setText(s.length() + "/200");
//            }
//        });
//
//    }
//
//    private void notifyPicTips() {
//        //???????????????????????????,?????????????????????,???????????????
//        if (picPathList != null) {
//            if (picPathList.size() <= 1) {
//                upload_appendix_notice.setVisibility(View.VISIBLE);
//                return;
//            }
//        }
//        upload_appendix_notice.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//        FeedBackImgBean bean = picPathList.get(position);
//        String picPath = bean.getPicPath();
//        if (TextUtils.isEmpty(picPath)) {
//            //????????????????????????????????????
//            selectPhotoMethod(adapter, position);
//        } else {
//            //?????????????????????????????????
//            ImageView imageView = view.findViewById(R.id.evaluation_module_feedback_iv);
//            ArrayList<Object> list = new ArrayList<>();
//            for (int i = 0; i < picPathList.size(); i++) {
//                if (picPathList.get(i).getItemType() != FeedBackImgBean.CAMERA) {
//                    list.add(picPathList.get(i).getPicPath());
//                }
//            }
//
//            //????????????
//            // ???????????????
//            //srcView??????????????????????????????ImageView??????????????????????????????????????????????????????
//            //???????????????????????????ImageView???scaleType???centerCrop???????????????????????????????????????Original_Size?????????Glide???????????????
//
//            new XPopup.Builder(activity).asImageViewer(imageView, position, list, false, false, -1, -1, -1, false, new OnSrcViewUpdateListener() {
//                @Override
//                public void onSrcViewUpdate(ImageViewerPopupView popupView, int position) {
//                    // ????????????Pager?????????????????????????????????View
//                    popupView.updateSrcView((ImageView) mRecy.getChildAt(position).findViewById(R.id.evaluation_module_feedback_iv));
//                }
//            }, new ImageLoader())
//                    .show();
//        }
//    }
//
//    @Override
//    protected void initMVP() {
//        mView = new IContract.OrderSelectTimeActivity.View() {
//            @Override
//            public void onSubmit(SubmitOrderResponseBean responseBean, ResultType resultType, String errorMsg) {
//                hideDialog();
//                switch (resultType) {
//                    case NET_ERROR:
//                        //???????????????
//                        ToastUtils.showLong(getString(R.string.base_module_net_error));
//                        break;
//                    case SERVER_ERROR:
//                        ToastUtils.showShort(errorMsg);
//                        break;
//                    case SERVER_NORMAL_DATANO:
//                    case SERVER_NORMAL_DATAYES:
//                        ToastUtils.showShort("????????????");
//
//                        OrderTreatmentSuccessActivity.IntentBean intentBean
//                                = new OrderTreatmentSuccessActivity.IntentBean(responseBean.getResult());
//                        OrderTreatmentSuccessActivity.startActivity(activity, intentBean);
//                        break;
//                }
//            }
//
//            @Override
//            public void onGetInforMation(InforMationBean inforMationBean, ResultType resultType, String errorMsg) {
//                hideDialog();
//                switch (resultType) {
//                    case NET_ERROR:
//                        //??????????????????????????????????????????????????????????????????
//                        ToastUtils.showLong(getString(R.string.base_module_net_error));
//                        break;
//                    case SERVER_ERROR:
//                        //?????????????????????????????????code?????????
//                        ToastUtils.showLong(getString(R.string.base_module_data_load_error));
//                        break;
//                    case SERVER_NORMAL_DATANO:
//                    case SERVER_NORMAL_DATAYES:
//                        //??????????????????????????????code?????????result??????null?????????result???????????????????????????????????????
//                        InforMationBean.ResultBean result = inforMationBean.getResult();
//
//                        if (TextUtils.isEmpty(result.getBirthday()) || result.getSex() == 0 || TextUtils.isEmpty(result.getCardId())) {
//                            basePopupView.show();
//                        } else {
//                            //?????????????????????
//                            showLoadingDialog();
//                            if (checkIfUpAllPic()) {
//                                http_submit();
//                            } else {
//                                uploadAllPicOneByOne();
//                            }
//                        }
//                        break;
//                    default:
//                }
//            }
//
//            @Override
//            public void onSavePath(SavePathResponseBean responseBean, ResultType resultType, String errorMsg, int position) {
//                Logger.i("onSavePath" + responseBean + "position" + position);
//                switch (resultType) {
//                    case NET_ERROR:
//                    case SERVER_ERROR:
//                    case SERVER_NORMAL_DATANO:
//                        ToastUtils.showShort(errorMsg);
//                        break;
//                    case SERVER_NORMAL_DATAYES:
//                        if (responseBean.result != null) {
//                            //??????????????????
//                            picPathList.get(position).setUrl(responseBean.result.fileUrl + "");
//                            picPathList.get(position).setId(responseBean.result.fileId);
//                            if (checkIfUpAllPic()) {
//                                //????????????????????????
//                                http_submit();
//                            } else {
//                                uploadAllPicOneByOne();
//                            }
//                        }
//                        break;
//                    default:
//                }
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };
//        mPresenter = new OrderSelectTimeActivityPresenter(this, mView);
//    }
//
//    /**
//     * ?????????????????????????????????????????????????????????????????????id
//     */
//    private boolean checkIfUpAllPic() {
//        boolean all = true;
//        for (int i = 0; i < picPathList.size(); i++) {
//            FeedBackImgBean imgBean = picPathList.get(i);
//            if (imgBean.getItemType() == FeedBackImgBean.IMG) {
//                if (!TextUtils.isEmpty(imgBean.getPicPath())) {
//                    if (TextUtils.isEmpty(imgBean.getUrl()) && imgBean.getId() == 0) {
//                        all = false;
//                    }
//                }
//            }
//        }
//        return all;
//    }
//
//
//    class ImageLoader implements XPopupImageLoader {
//        @Override
//        public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
//            //????????????Target.SIZE_ORIGINAL??????????????????????????????????????????????????????????????????
//            Glide.with(imageView).load(url).apply(new RequestOptions().placeholder(R.mipmap.logo).override(Target.SIZE_ORIGINAL)).into(imageView);
//        }
//
//        @Override
//        public File getImageFile(@NonNull Context context, @NonNull Object uri) {
//            try {
//                return Glide.with(context).downloadOnly().load(uri).submit().get();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
//
//    @Override
//    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//        if (view.getId() == R.id.evaluation_module_feedback_iv_delete) {
//            if (picPathList.size() == 4) {
//                FeedBackImgBean bean = picPathList.get(3);
//                if (TextUtils.isEmpty(bean.getPicPath())) {
//                    picPathList.remove(position);
//                } else {
//                    bean.setItemType(FeedBackImgBean.CAMERA);
//                    bean.setPicPath("");
//                }
//            } else {
//                picPathList.remove(position);
//            }
//            feedBackImgAdapter.notifyDataSetChanged();
//            notifyPicTips();
//        }
//    }
//
//
//    /**
//     * ????????????
//     */
//    private void selectPhotoMethod(BaseQuickAdapter adapter, int position) {
//        cameraAlbumPopup = new XPopup.Builder(this)
//                .asCustom(new CameraAlbumPopup1(this,
//                        () -> MyTakePhotoActivity.getPicPath(activity, MyTakePhotoActivity.photoAlbum, picPath -> {
//                            cameraAlbumPopup.dismiss();
//                            parseImgData(picPath, position);
//                        }), new CameraAlbumPopup1.CameraListener() {
//                    @Override
//                    public void cameraClickListener() {
//                        MyTakePhotoActivity.getPicPath(activity, MyTakePhotoActivity.takePhoto, picPath -> {
//                            cameraAlbumPopup.dismiss();
//                            parseImgData(picPath, position);
//                        });
//                    }
//                }));
//        cameraAlbumPopup.show();
//    }
//
//    /**
//     * ???????????????????????????
//     *
//     * @param picPath
//     */
//    private void parseImgData(String picPath, int position) {
//        //???????????????.??????????????????,???????????????????????????????????????
//        if (!TextUtils.isEmpty(picPath)) {
//            int size = picPathList.size();
//            if (size == 4) {
//                //???????????????????????????,??????????????????????????????
//                FeedBackImgBean bean = picPathList.get(3);
//                bean.setItemType(FeedBackImgBean.IMG);
//                bean.setPicPath(picPath);
//            } else {
//                //??????????????????????????????.?????????
//                picPathList.add(size - 1, new FeedBackImgBean(FeedBackImgBean.IMG, picPath));
//            }
////            uploadPic(activity, picPath, position);
//
////            uploadPicByCos(picPath,position);
//            feedBackImgAdapter.notifyDataSetChanged();
//            notifyPicTips();
//        }
//    }
//
//    //???????????????
//    private void uploadPicByCos(String picPath, int position) {
//        showLoadingDialog("?????????...", false);
//        String houzhui = "png";
//        String[] strArr = picPath.split("\\.");
//        if (strArr.length > 0) {
//            houzhui = strArr[strArr.length - 1];
//        }
//
//        String cosPath = System.currentTimeMillis() + "." + houzhui;
//        TencentCosManager.getInstance(this).upload(cosPath, picPath, new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long complete, long target) {
//
//            }
//        }, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // ???????????????
//                        SavePathRequestBean requestBean = new SavePathRequestBean();
//                        requestBean.prefix = "ordertreatment";
//                        requestBean.description = "C???????????????Android";
//                        requestBean.type = 10;
//                        //?????????????????????????????????.??????????????????
//                        String str1 = result.accessUrl.substring(0, result.accessUrl.indexOf("myqcloud.com"));
//                        String str2 = result.accessUrl.substring(str1.length() + 12, result.accessUrl.length());
//                        requestBean.filePath = str2;
//                        requestBean.originalFilename = picPath;
//                        mPresenter.savePath(requestBean, activity, position);
//                        Logger.i("uploadPicByCosSuccess" + str2);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
//
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideDialog();
//                        ToastUtils.showShort("????????????");
//                    }
//                });
//            }
//        }, new TencentCosManager.InnerListener() {
//            @Override
//            public void onFailed(String errMsg) {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideDialog();
//                        ToastUtils.showShort("????????????");
//                    }
//                });
//            }
//        });
//    }
//
//    /**
//     * ??????????????????????????????
//     *
//     * @param activity
//     * @param picPath
//     * @param position
//     */
//    private void uploadPic(BaseActivity activity, String picPath, int position) {
//        String spToken = SPUtils.getInstance(Constant.SP.UserInfo.USER_INFO).getString(Constant.SP.UserInfo.token, "");
//        //????????????????????????????????????
//        Luban.with(activity)
//                .load(new File(picPath))
//                .ignoreBy(300)
//                .setCompressListener(new OnCompressListener() {
//                    @Override
//                    public void onStart() {
//
//                    }
//
//
//                    // TODO: 2019/12/20 ?????????????????????type???
//                    @Override
//                    public void onSuccess(File compressedFile) {
//                        showLoadingDialog("?????????...", false);
//                        //??????debug????????????????????????????????????
//                        RxHttp.setDebug(true);
//                        RxHttp.postForm("/gateway/commonapi/uploadFile/uploadImg")
//                                .addHeader("Authorization", spToken)
//                                .add("file", compressedFile)
//                                .add("prefix", "feedbackUpImg")//?????????????????????,??????????????????????????????????????????doctorIcon followUpImg???????????????
//                                .add("type", "9")//??????????????????  1???????????? 6????????????7????????????8???????????? 9 ??????????????????
//                                .asUpload(progress -> {
//                                    //??????????????????,0-100???????????????????????????????????????,????????????101????????????????????????Http????????????
//                                   /* int currentProgress = progress.getProgress(); //???????????? 0-100
//                                    long currentSize = progress.getCurrentSize(); //??????????????????????????????
//                                    long totalSize = progress.getTotalSize();     //???????????????????????????
//                                    Logger.i("????????????"+currentProgress);*/
//                                }, AndroidSchedulers.mainThread())     //????????????(??????/??????/??????)??????,?????????,?????????????????????????????????
//                                .subscribe(s -> {             //??????s???String??????,?????????asUpload(Parser,Progress,Scheduler)????????????????????????
//                                    //????????????
//                                    hideDialog();
//                                    ToastUtils.showShort("????????????");
//                                    Logger.i("????????????" + s);
//                                    Gson g = new Gson();
//                                    //?????????list??????
//                                    UploadPicResponseBean uploadPicResponseBean = g.fromJson(s, UploadPicResponseBean.class);
//                                    if (uploadPicResponseBean.getCode() == 0) {
//                                        picPathList.get(position).setUrl(uploadPicResponseBean.getResult().getFileUrl());
//                                        picPathList.get(position).setId(uploadPicResponseBean.getResult().getFileId());
//                                        feedBackImgAdapter.notifyDataSetChanged();
//                                        notifyPicTips();
//                                    }
//
//
//                                }, throwable -> {
//                                    //????????????
//                                    hideDialog();
//                                    ToastUtils.showShort("????????????");
//                                    Logger.i("????????????" + throwable);
//                                });
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//                }).launch();
//    }
//
//    @OnClick({R2.id.evaluation_module_order_consult_select_time,
//            R2.id.evaluation_module_order_consult_select_type,
//            R2.id.evaluation_module_order_consult_commit,
//            R2.id.evaluation_module_order_consult_problem_type,
//            R2.id.rl_back, R2.id.tv_right})
//    public void onClick(View view) {
//        if (OneClickUtils.isFastClick()) {
//            return;
//        }
//
//        int viewId = view.getId();
//        if (viewId == R.id.evaluation_module_order_consult_select_time) {
//            //??????????????????
//            OrderConsultSelectTimeActivity.OrderConsultSelectTimeIntentBean bean
//                    = new OrderConsultSelectTimeActivity.OrderConsultSelectTimeIntentBean(intentBean.doctorUserId);
//            OrderConsultSelectTimeActivity.startActivityForResult(this, bean);
//        } else if (viewId == R.id.evaluation_module_order_consult_select_type) {
//            showTypePopup();
//        } else if (viewId == R.id.evaluation_module_order_consult_commit) {
//            //??????
//            submitAllData();
//
//        } else if (viewId == R.id.rl_back) {
//            finish();
//        } else if (viewId == R.id.tv_right) {
//            //????????????   pop
//            showAdvisoryInstructionsPopup();
//        } else if (viewId == R.id.evaluation_module_order_consult_problem_type) {
//            //????????????   pop
//            showProblemTypePopup();
//        }
//    }
//
//    //??????????????????
//    private void submitAllData() {
//        //??????
//        if (returnTimeBean == null) {
//            ToastUtils.showShort("???????????????");
//            return;
//        }
//        if (mType == 0) {
//            ToastUtils.showShort("???????????????");
//            return;
//        }
//        if (select_problem_type_tv.getText().toString().isEmpty()) {
//            ToastUtils.showShort("?????????????????????");
//            return;
//        }
//        boolean hasPic = false;
//        for (int i = 0; i < picPathList.size(); i++) {
//            FeedBackImgBean imgBean = picPathList.get(i);
//            if (imgBean.getItemType() == FeedBackImgBean.IMG) {
//                hasPic = true;
//                break;
//            }
//        }
//
//        if (accountService != null && accountService.getGetAccountInfo() != null) {
//            //??????????????????
//            if (!accountService.getGetAccountInfo().isLogin) {
//
//                new XPopup.Builder(activity)
//                        .dismissOnBackPressed(false)
//                        .dismissOnTouchOutside(false)
//                        .asCustom(new CenterPopup(activity,
//                                CenterPopup.CenterPopupType.twoButton,
//                                activity.getString(R.string.evaluation_module_commit_confirm),
//                                activity.getString(R.string.evaluation_module_not_login),
//                                activity.getString(R.string.evaluation_module_cancle),
//                                activity.getString(R.string.evaluation_module_to_login),
//                                centerPopup -> centerPopup.dismiss(), centerPopup -> {
//                            centerPopup.dismiss();
//                            ARouter.getInstance()
//                                    .build(ARouterConstant.Account.AccountLoginActivity)
//                                    .navigation(activity);
//                        }))
//
//                        .show();
//
//            } else {
//                //??????????????????,??????????????????????????????????????????
//                showLoadingDialog();
//                mPresenter.getInforMation(this);
//
//            }
//        }
//    }
//
//    /**
//     * ????????????????????????
//     * ??????????????????.??????????????????????????????????????????.?????????????????????
//     */
//    public void uploadAllPicOneByOne() {
//        if (!checkIfUpAllPic()) {
//            //????????????????????????
//            for (int i = 0; i < picPathList.size(); i++) {
//                FeedBackImgBean imgBean = picPathList.get(i);
//                if (imgBean.getItemType() == FeedBackImgBean.IMG) {
//                    if (!TextUtils.isEmpty(imgBean.getPicPath())) {
//                        if (TextUtils.isEmpty(imgBean.getUrl()) && imgBean.getId() == 0) {
//                            //??????????????????????????????????????????
//                            //??????????????????cos,?????????????????????id,id?????????list,?????????id???????????????
//                            uploadPicByCos(imgBean.getPicPath(), i);
//                            return;
//                        }
//                    }
//
//                }
//            }
//        }
//    }
//
//
//    //????????????
//    public void http_submit() {
//        //????????????  1????????????:?????????(?????????)???2?????????(????????????),3?????????(????????????)',
//        String uploadIds = "";
//        for (int i = 0; i < picPathList.size(); i++) {
//            if (picPathList.get(i).getItemType() == FeedBackImgBean.IMG) {
//                if (i == 0) {
//                    uploadIds = picPathList.get(0).getId() + "";
//                } else {
//                    uploadIds += "," + picPathList.get(i).getId();
//                }
//            }
//        }
//        SubmitOrderRequestBean requestBean = new SubmitOrderRequestBean(
//                Integer.parseInt(accountService.getGetAccountInfo().userId),
//                0,
//                intentBean.doctorUserId,
//                0,
//                returnTimeBean.getId(),
//                16,
//                intentBean.doctorUserId + "",
//                mType + "",
//                uploadIds,
//                etContent.getText().toString().trim(),
//                select_problem_type_tv.getText().toString().trim()//?????????????????? ????????????  ??????????????????
//        );
//        mPresenter.submit(requestBean, this);
//    }
//
//    private void showTypePopup() {
//
//        typePopup = new XPopup.Builder(this)
//                .asCustom(new TreatmentTypePopup(this, new TreatmentTypePopup.OnCheckListener() {
//                    @Override
//                    public void onCheck(TreatmentTypePopup.TreatmentType type, String tips) {
//                        switch (type) {
//                            case VIDEO:
//                                mType = 3;
//                                typePopup.dismiss();
//                                SpannableString mSpannableString = new SpannableString("????????????(??????)");
//                                mSpannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.COLOR_BLACK_333333)), 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//                                mSpannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.COLOR_ORANGE_FF782E)), 4, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//                                select_type_tv.setText(mSpannableString);
//                                break;
//                            case WORD:
//                                mType = 4;
//                                typePopup.dismiss();
//                                select_type_tv.setText("????????????");
//                                break;
//                            case VOICE:
//                                break;
//                            case OFFLINE:
//                                mType = 2;
//                                typePopup.dismiss();
//                                select_type_tv.setText("????????????");
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                }));
//        typePopup.show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == OrderConsultActivity_To_OrderConsultSelectTimeActivity_Code && resultCode == OrderConsultActivity_To_OrderConsultSelectTimeActivity_Code) {
//            returnTimeBean = (TimeCalendarBean.ResultBean.DateListBean) data.getSerializableExtra("data");
//            String time = returnTimeBean.getStart().substring(0, 16) + "-" + returnTimeBean.getEnd().substring(11, 16);
//            select_time.setText(time);
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
//    public void onLoginEventBus(LoginEventBus busBean) {
//        //???????????????????????????
//       /* if (busBean != null) {
//            if (busBean.getAccountBean() != null) {
//                if (busBean.getAccountBean().isLogin) {
//                    //???????????????
//                    //????????????????????????????????????,?????????????????????
//                    showLoadingDialog();
//                    http_submit();
//                } else {
//                    //????????????????????????
//                }
//            }
//        }*/
//    }
//
//    public static class IntentBean implements Serializable {
//        private int doctorId;
//        private int doctorUserId;
//        //????????????
//        private String selecTypeName = "";
//        //??????
//        public String advisoryBody = "";
//        //??????
//        public String imgUri = "";
//
//        public IntentBean(int doctorId, int doctorUserId,  String selecTypeName,String advisoryBody, String imgUri) {
//            this.doctorId = doctorId;
//            this.doctorUserId = doctorUserId;
//            this.selecTypeName = selecTypeName;
//            this.advisoryBody = advisoryBody;
//            this.imgUri = imgUri;
//        }
//
//        @Override
//        public String toString() {
//            return "IntentBean{" +
//                    "doctorId=" + doctorId +
//                    ", doctorUserId=" + doctorUserId +
//                    ", advisoryBody='" + advisoryBody + '\'' +
//                    ", selecTypeName='" + selecTypeName + '\'' +
//                    ", imgUri='" + imgUri + '\'' +
//                    '}';
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//
//    //????????????   pop
//    private void showAdvisoryInstructionsPopup() {
//        TextView title = new TextView(this);
//        title.setText("????????????");
//        title.setPadding(0, 25, 0, 0);
//        title.setGravity(Gravity.CENTER);
//        title.getPaint().setFakeBoldText(true);
//        title.setTextSize(18);
//        title.setTextColor(Color.BLACK);
//        AlertDialog alertDialog1 = new AlertDialog.Builder(this)
//                .setCustomTitle(title)
//                .setMessage("?????????\" +\n" +
//                        "                                    \"???????????????????????????????????????????????????????????????????????????????????????????????????\" +\n" +
//                        "                                    \"????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")//???
//                .create();
//        alertDialog1.show();
//    }
//
//    //????????????
//    private void showProblemTypePopup() {
//        typePopup = new XPopup.Builder(this)
//                .asCustom(new ProblemTypePopup(this, new ProblemTypePopup.ResultListener() {
//                    @Override
//                    public void onPick(String name) {
//                        select_problem_type_tv.setText(name);
//                    }
//                }));
//        typePopup.show();
//    }
//
//}
