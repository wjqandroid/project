package com.visionvera.psychologist.c.module.counselling.activity;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gyf.immersionbar.ImmersionBar;
import com.visionvera.library.base.BaseMVPLoadActivity;
import com.visionvera.library.base.Constant;
import com.visionvera.library.base.activity.ProtocolActivity;
import com.visionvera.library.base.bean.BaseResponseBean2;
import com.visionvera.library.base.mvp.view.IBaseView;
import com.visionvera.library.util.OneClickUtils;
import com.visionvera.library.widget.view.CircleImageView;
import com.visionvera.psychologist.account_module.util.AccountManager;
import com.visionvera.psychologist.c.R;
import com.visionvera.psychologist.c.R2;
import com.visionvera.psychologist.c.module.counselling.adapter.ConsultingEvaluationListAdapter;
import com.visionvera.psychologist.c.module.counselling.bean.CounselorDetailBean;
import com.visionvera.psychologist.c.module.counselling.contract.OrderConsultContract;
import com.visionvera.psychologist.c.module.counselling.presenter.CounselorDetailPresenter;
import com.visionvera.psychologist.c.module.ordertreatment.bean.EvaluateDetailBean;
import com.visionvera.psychologist.c.module.ordertreatment.bean.EvaluateListBean;
import com.visionvera.psychologist.c.module.ordertreatment.bean.EvaluateListRequest;
import com.visionvera.psychologist.c.utils.AdjustableTextView;
import com.visionvera.psychologist.c.widget.tencentIm.textchat.ChatActivity;
import com.visionvera.psychologist.c.widget.tencentIm.voicevideo.VoiceVideoCallActivity;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @Classname:CounselorDetailActivity
 * @author:haohuizhao
 * @Date:2021/10/28 15:59
 * @Version???v1.0
 * @describe??? ??????:???????????????????????????????????????    ????????????????????????????????????
 */

public class CounselorDetailActivity extends BaseMVPLoadActivity<OrderConsultContract.CounselorDetail.CounselorDetailView, CounselorDetailPresenter> {





    RelativeLayout rlTitleBar;
    //?????????????????????
    TextView tvAdvisoryBody;
    TextView tvIntroduce;
    CircleImageView header;
    TextView counselor_name;
    TextView counselor_level;
    TextView counselor_field;
    CheckBox checkBox;
    //??????????????????
    TextView tv_commit;
    //????????????
    TextView tvTextPrice;
    TextView tvVoicePrice;
    TextView tvVideoPrice;
    //??????????????????
    TextView btnTextMake;
    TextView btnVoiceMake;
    TextView btnVideoMake;
    //????????????
    RecyclerView reCommentList;
    LinearLayout llEvaluateLayout;
    TextView tvEvaluateNumber;
    TextView btnEvaluateLookAll;
    View llEvaluateLine;


    private CounselorDetailIntentBean mIntentBean;
    private CounselorDetailBean.ResultDTO mResult;
    //??????Adapter
    private ConsultingEvaluationListAdapter consultingEvaluationListAdapter;
    //??????List
    private EvaluateListBean evaluateListBean;
    //????????????????????????    ??????????????????????????????
    private Boolean is_textOpenService = true;
    private Boolean is_videoOpenService = true;
    private Boolean is_voiceOpenService = true;
    private TextView orderTreatmentProtocol;


    public static void startActivity(Context context, CounselorDetailIntentBean intentBean) {
        context.startActivity(new Intent(context, CounselorDetailActivity.class).putExtra("intentBean", intentBean));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.evaluation_module_activity_counselor_detail;
    }

    @Override
    protected void doYourself() {

        initView();
        initIntentData();
        initRequestData();
    }

    private void initView() {

        rlTitleBar = (RelativeLayout) findViewById(R.id.rlTitleBar);
        tvAdvisoryBody = (TextView) findViewById(R.id.evaluation_module_advisory_body);
        tvIntroduce = (TextView) findViewById(R.id.tv_introduce);
        header = (CircleImageView) findViewById(R.id.evaluation_module_counselor_detail_header);
        counselor_name = (TextView) findViewById(R.id.evaluation_module_counselor_name);
        counselor_level = (TextView) findViewById(R.id.evaluation_module_counselor_level);
        counselor_field = (TextView) findViewById(R.id.evaluation_module_counselor_field);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        tv_commit = (TextView) findViewById(R.id.evaluation_module_order_consult_btn);
        tvTextPrice = (TextView) findViewById(R.id.tv_text_Price);
        tvVoicePrice = (TextView) findViewById(R.id.tv_voice_Price);
        tvVideoPrice = (TextView) findViewById(R.id.tv_video_Price);
        btnTextMake = (TextView) findViewById(R.id.btn_text_make);
        btnVoiceMake = (TextView) findViewById(R.id.btn_voice_make);
        btnVideoMake = (TextView) findViewById(R.id.btn_video_make);
        reCommentList = (RecyclerView) findViewById(R.id.re_comment_list);
        llEvaluateLayout = (LinearLayout) findViewById(R.id.ll_evaluate);
        tvEvaluateNumber = (TextView) findViewById(R.id.tv_evaluate_number);
        btnEvaluateLookAll = (TextView) findViewById(R.id.btn_evaluate_look_all);
        orderTreatmentProtocol = (TextView) findViewById(R.id.order_treatment_protocol);
        llEvaluateLine = (View) findViewById(R.id.ll_evaluate_line);


        ImmersionBar.with(this)
                //???????????????
                .transparentStatusBar()
                //?????????????????????
                .statusBarDarkFont(true)
                //?????????????????????????????????????????????????????????????????????????????????. false??????????????????????????????true???????????????????????????
                .fitsSystemWindows(false)
                .init();
        //??????????????????????????????w
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlTitleBar.getLayoutParams();
        params.setMargins(0, BarUtils.getStatusBarHeight(), 0, 0);
        rlTitleBar.setLayoutParams(params);
        //????????????
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                tv_commit.setBackground(getResources().getDrawable(R.drawable.bg_logout_unselect));
            } else {
                tv_commit.setBackground(getResources().getDrawable(R.drawable.bg_logout));
            }
        });
//        checkBox.setChecked(true);
    }

    private void initIntentData() {
        mIntentBean = (CounselorDetailIntentBean) getIntent().getSerializableExtra("intentBean");
    }

    private void initRequestData() {
        showLoading();


        //????????????
        //?????????????????????
        mPresenter.getCounselorDetailByUserId(mIntentBean.userId, this);
        //????????????List
        EvaluateListRequest evaluateListRequest = new EvaluateListRequest();
        evaluateListRequest.setServiceUserId(mIntentBean.userId);
        mPresenter.getUserEvaluateList(evaluateListRequest, this);


        btnEvaluateLookAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????????????????
                evaluateListBean.getResult().setImgUrl(mResult.getPhotoUrl());
                AllEvaluationListActivity.startActivity(CounselorDetailActivity.this, evaluateListBean);
            }
        });
        btnVoiceMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????????????????
                if (is_voiceOpenService) {
                    setJumpLogic("????????????");
                } else {
                    ToastUtils.showShort("????????????");
                }
            }
        });
        btnVideoMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????????????????
                if (is_videoOpenService) {
                    setJumpLogic("????????????");
                } else {
                    ToastUtils.showShort("????????????");
//                AccountManager accountService = AccountManager.getInstence();
//                VoiceVideoCallActivity.VideoActivityIntentBean intentBean = new VoiceVideoCallActivity.VideoActivityIntentBean(
//                        "mhsptrunkdev12608",
//                        "",
//                        accountService.getGetAccountInfo().userId,
//                        accountService.getGetAccountInfo().userName,
//                        "",
//                        "0"); //0?????? 1??????
//
//                startActivity(new Intent(activity, VoiceVideoCallActivity.class).putExtra("intentBean", intentBean));
                }
            }
        });
        btnTextMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????????????????
                if (is_textOpenService) {
                    setJumpLogic("????????????");
                } else {
                    ToastUtils.showShort("????????????");
//                AccountManager accountService = AccountManager.getInstence();
//                ChatActivity.ChatActivityIntentBean intentBean = new ChatActivity.ChatActivityIntentBean(
//                        "mhsptrunkdev12607" + "",
//                        "?????????",
//                        accountService.getGetAccountInfo().userId,
//                        accountService.getGetAccountInfo().userName,
//                        "");
//
//                startActivity(new Intent(activity, ChatActivity.class).putExtra("intentBean", intentBean));

                }
            }
        });
        orderTreatmentProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProtocolActivity.startActivity(CounselorDetailActivity.this, Constant.Url.request_base_url + Constant.WebUrl.consult, "???????????????????????????");

            }
        });
    }

    @OnClick({R2.id.evaluation_module_order_consult_btn,
            R2.id.evaluation_module_counselor_iv_back,})
    public void onClick(View view) {
        if (OneClickUtils.isFastClick()) {
            return;
        }
        int viewId = view.getId();
        if (viewId == R2.id.evaluation_module_order_consult_btn) {
            //????????????????????????
            if (is_textOpenService || is_videoOpenService || is_voiceOpenService) {
                setJumpLogic("");
            } else {
                ToastUtils.showShort("??????????????????,???????????????");
            }
        } else if (viewId == R2.id.evaluation_module_counselor_iv_back) {
            finish();
        }
//        else if (viewId == R2.id.evaluation_module_counselor_share) {
//            ToastUtils.showLong(getResources().getString(R.string.evaluation_module_not_develop));
//        }
////        else if (viewId == R2.id.order_informed_consent) {
//            ProtocolActivity.startActivity(this, Constant.Url.request_base_url + Constant.WebUrl.consult, "???????????????");
//
//        }
    }

    //????????????????????????
    private void setJumpLogic(String typeName) {
        if (checkBox.isChecked()) {
            //??????????????????    4????????????   5????????????   6????????????   ??????
            String priceCount = String.valueOf(mResult.getConsultingFee());
            String voicePriceCount = mResult.getConsultingFeeVoice();
            String videoPriceCount = mResult.getConsultingFeeVideo();

            OrderConsultActivity.OrderConsultIntentBean intentBean = new OrderConsultActivity.OrderConsultIntentBean(mIntentBean.userId, "", "", "", "", typeName, "", priceCount, voicePriceCount, videoPriceCount);
            intentBean.name = counselor_name.getText().toString().trim();
            intentBean.title = counselor_level.getText().toString().trim();
            intentBean.tags = counselor_field.getText().toString().trim();
            intentBean.advisoryBody = tvAdvisoryBody.getText().toString().trim();
            intentBean.imgUri = mResult.getPhotoUrl();
            if (mResult != null) {
                intentBean.consultingFee = String.valueOf(mResult.getConsultingFee());
            }
            OrderConsultActivity.startActivity(this, intentBean);

        } else {
            ToastUtils.showShort("??????????????????????????????");
        }
    }


    @Override
    protected void initMVP() {
        mView = new OrderConsultContract.CounselorDetail.CounselorDetailView() {
            @Override
            public void onCounselorDetailByPsyInfoId(CounselorDetailBean bean, ResultType resultType, String errorMsg) {
                parseNetDetailResponse(bean, resultType);
            }

            @Override
            public void onCounselorDetailByUserId(CounselorDetailBean bean, ResultType resultType, String errorMsg) {
                parseNetDetailResponse(bean, resultType);
            }

            //??????list
            @Override
            public void onEvaluateList(EvaluateListBean bean, ResultType resultType, String errorMsg) {
                if (bean != null) {
                    parseNetEvaluateList(bean, resultType);
                } else {
                    ToastUtils.showShort(errorMsg);
                }
            }

            //add ??????
            @Override
            public void onAddEvaluate(BaseResponseBean2 bean, ResultType resultType, String errorMsg) {

            }

            @Override
            public void onEvaluateDetail(EvaluateDetailBean bean, ResultType resultType, String errorMsg) {

            }

            @Override
            public void onComplete() {

            }
        };
        mPresenter = new CounselorDetailPresenter(this, mView);
    }

    private void parseNetDetailResponse(CounselorDetailBean bean, IBaseView.ResultType resultType) {
        switch (resultType) {
            case NET_ERROR:
                //??????????????????????????????????????????????????????????????????
                showError(getString(R.string.base_module_net_error));
                ToastUtils.showLong(getString(R.string.base_module_net_error));
                break;
            case SERVER_ERROR:
                //?????????????????????????????????code?????????
                showError(getString(R.string.base_module_data_load_error));
                ToastUtils.showLong(getString(R.string.base_module_data_load_error));
                break;
            case SERVER_NORMAL_DATANO:
            case SERVER_NORMAL_DATAYES:
                //??????????????????????????????code?????????result??????null?????????result???????????????????????????????????????
                showNormal();
                //?????????????????????
                showData(bean.getResult());
                break;
            default:
        }
    }


    private void parseNetEvaluateList(EvaluateListBean bean, IBaseView.ResultType resultType) {
        switch (resultType) {
            case NET_ERROR:
                //??????????????????????????????????????????????????????????????????
//                showError(getString(R.string.base_module_net_error));
//                ToastUtils.showLong(getString(R.string.base_module_net_error));
                break;
            case SERVER_ERROR:
                //?????????????????????????????????code?????????
//                showError(getString(R.string.base_module_data_load_error));
//                ToastUtils.showLong(getString(R.string.base_module_data_load_error));
                break;
            case SERVER_NORMAL_DATANO:
            case SERVER_NORMAL_DATAYES:
                //??????????????????????????????code?????????result??????null?????????result???????????????????????????????????????
                showNormal();
//                ToastUtils.showLong(bean.toString());

                evaluateListBean = bean;
                //????????????
                setEvaluateListAdapter(bean);

                break;
            default:
        }
    }

    //??????LIst   ????????? Adapter
    private void setEvaluateListAdapter(EvaluateListBean bean) {
        List<EvaluateListBean.ResultDTO.DataListDTO> dataList = bean.getResult().getDataList();
        if (dataList.size() > 0) {
            llEvaluateLayout.setVisibility(View.VISIBLE);
            llEvaluateLine.setVisibility(View.VISIBLE);
            //????????????
            tvEvaluateNumber.setText("??????????????????" + dataList.size() + "???");
            if (dataList.size() > 2) {
                btnEvaluateLookAll.setVisibility(View.VISIBLE);
                ArrayList<EvaluateListBean.ResultDTO.DataListDTO> list = new ArrayList<>();
                list.add(dataList.get(0));
                list.add(dataList.get(1));
                dataList = list;
            } else {
                btnEvaluateLookAll.setVisibility(View.GONE);
            }
            reCommentList.setLayoutManager(new LinearLayoutManager(CounselorDetailActivity.this, LinearLayoutManager.VERTICAL, false));
            //reCommentList.addItemDecoration(new SpacesItemDecoration(20));
            consultingEvaluationListAdapter = new ConsultingEvaluationListAdapter(CounselorDetailActivity.this, dataList);
            reCommentList.setAdapter(consultingEvaluationListAdapter);
            consultingEvaluationListAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {

                }
            });
        } else {
            //??????????????????
            llEvaluateLayout.setVisibility(View.GONE);
            llEvaluateLine.setVisibility(View.GONE);
        }
    }

    //?????????????????????
    private void showData(CounselorDetailBean.ResultDTO result) {


        if (result != null) {
            mResult = result;
            //???????????????????????????????????????,???????????????.????????????????????????
            mIntentBean.id = mResult.getPsyInfoId();
            counselor_name.setText(result.getUsername());
            counselor_level.setText(result.getTitleName());
            //??????
            Glide.with(this).load(result.getPhotoUrl())
                    .placeholder(R.drawable.base_module_doctor_header)
                    .error(R.drawable.base_module_doctor_header)
                    .into(header);
            //????????????
            tvAdvisoryBody.setText(result.getHospitalName());
            //??????
            StringBuilder allLable = new StringBuilder();
            for (String lable : result.getLableName()) {
                allLable.append("#" + lable + "  ");
            }
            counselor_field.setText(allLable.toString());
            //????????????
            //????????????
            String introduction = !TextUtils.isEmpty(result.getIntroduce()) ? result.getIntroduce() : "??????";
            tvIntroduce.setText(introduction);
            AdjustableTextView adjustableTextView = new AdjustableTextView(tvIntroduce, 2);
            adjustableTextView.hiddenText();


            //??????   ??????
            String priceCount = String.valueOf(mResult.getConsultingFee());
            String priceStr = priceCount + "???/??????";
            SpannableString spannableString = new SpannableString(priceStr);
            spannableString.setSpan(new AbsoluteSizeSpan(18, true), 0, priceCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(12, true), priceCount.length(), priceStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTextPrice.setText(spannableString);


            //??????
            String voicePriceCount = mResult.getConsultingFeeVoice();
            String voicePriceStr = voicePriceCount + "???/??????";
            SpannableString voicepriceStrspannableString = new SpannableString(voicePriceStr);
            voicepriceStrspannableString.setSpan(new AbsoluteSizeSpan(18, true), 0, voicePriceCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            voicepriceStrspannableString.setSpan(new AbsoluteSizeSpan(12, true), voicePriceCount.length(), voicePriceStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvVoicePrice.setText(voicepriceStrspannableString);


            //??????
            String videoPriceCount = mResult.getConsultingFeeVideo();
            String videopriceStr = videoPriceCount + "???/??????";
            SpannableString videopriceStrspannableString = new SpannableString(videopriceStr);
            videopriceStrspannableString.setSpan(new AbsoluteSizeSpan(18, true), 0, videoPriceCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            videopriceStrspannableString.setSpan(new AbsoluteSizeSpan(12, true), videoPriceCount.length(), videopriceStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvVideoPrice.setText(videopriceStrspannableString);


            //????????????????????????
            //??????????????????    4????????????   5????????????   6????????????     //0?????? 1??????
            if (mResult.getConsultingFeeStatus() == 0) {
                is_textOpenService = false;
                tvTextPrice.setVisibility(View.GONE);
                btnTextMake.setText("????????????");
                btnTextMake.setBackground(getResources().getDrawable(R.drawable.bg_ffcccccc_r4));
            }

            if (mResult.getConsultingFeeVoiceStatus() == 0) {
                is_voiceOpenService = false;
                tvVoicePrice.setVisibility(View.GONE);
                btnVoiceMake.setText("????????????");
                btnVoiceMake.setBackground(getResources().getDrawable(R.drawable.bg_ffcccccc_r4));
            }

            if (mResult.getConsultingFeeVideoStatus() == 0) {
                is_videoOpenService = false;
                tvVideoPrice.setVisibility(View.GONE);
                btnVideoMake.setText("????????????");
                btnVideoMake.setBackground(getResources().getDrawable(R.drawable.bg_ffcccccc_r4));
            }
            if (!is_textOpenService && !is_videoOpenService && !is_voiceOpenService) {
                tv_commit.setBackground(getResources().getDrawable(R.drawable.bg_ffcccccc_r4));
            }
        }

    }


    @Override
    protected void onReload() {
        initRequestData();
    }


    public static class CounselorDetailIntentBean implements Serializable {

        private int id;
        private int userId;

        /**
         * @param id     ???Id???????????????????????????????????????????????????
         * @param userId ???userId????????????????????????????????????
         */

        public CounselorDetailIntentBean(int id, int userId) {
            this.id = id;
            this.userId = userId;
        }
    }


}
