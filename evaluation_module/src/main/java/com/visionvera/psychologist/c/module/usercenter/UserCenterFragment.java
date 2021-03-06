package com.visionvera.psychologist.c.module.usercenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.visionvera.library.arouter.ARouterConstant;
import com.visionvera.library.arouter.commonbean.AccountBean;
import com.visionvera.library.arouter.service.IAccountService;
import com.visionvera.library.base.BaseMVPFragment;
import com.visionvera.library.base.Constant;
import com.visionvera.library.base.activity.ProtocolActivity;
import com.visionvera.library.base.bean.BaseResponseBean4;
import com.visionvera.library.eventbus.commonbean.LoginEventBus;
import com.visionvera.library.util.GlideImageLoader;
import com.visionvera.library.util.OneClickUtils;
import com.visionvera.library.widget.view.CircleImageView;
import com.visionvera.psychologist.account_module.util.AccountManager;

import com.visionvera.psychologist.c.R;
import com.visionvera.psychologist.c.eventbus.CommitToMyEvaluationBus;
import com.visionvera.psychologist.c.module.collect.MyCollectsActivity;
import com.visionvera.psychologist.c.module.counselling.activity.OrderConsultListActivity;
import com.visionvera.psychologist.c.module.healthreport.HealthReoprtActivity;
import com.visionvera.psychologist.c.module.myevaluation.MyEvaluationActivity;
import com.visionvera.psychologist.c.module.ordertreatment.activity.OrderTreatmentListActivity;
import com.visionvera.psychologist.c.module.usercenter.activity.ApplySettledActivity;
import com.visionvera.psychologist.c.module.usercenter.activity.EditInfoActivity;
import com.visionvera.psychologist.c.module.usercenter.activity.FeedBackActivity;
import com.visionvera.psychologist.c.module.usercenter.activity.MessageCenterActivity;
import com.visionvera.psychologist.c.module.usercenter.activity.SettingActivity;
import com.visionvera.psychologist.c.module.usercenter.bean.UserBasicInfoBean;
import com.visionvera.psychologist.c.module.usercenter.contract.IContract;
import com.visionvera.psychologist.c.module.usercenter.presenter.UserInfoPresenter;
import com.visionvera.psychologist.c.widget.commonview.SettingItemView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @Desc ??????????????????
 * @Author yemh
 * @Date 2019-10-29 15:32
 */
public class UserCenterFragment extends BaseMVPFragment<IContract.UserInfo.View, UserInfoPresenter>implements View.OnClickListener {
    @Autowired(name = ARouterConstant.Account.accountModuleSetvice)
    IAccountService accountService;
    CircleImageView ivHeader;
    TextView tvUserName;
    TextView tvHeartBeans;
    ImageView ivEdit;
    TextView tvCheckDays;

    public static UserCenterFragment newInstance() {
        return new UserCenterFragment();
    }

    @Override
    protected void lazyLoadData() {
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.evaluation_module_fragment_usercenter;
    }

    @Override
    protected void initYourself() {
        EventBus.getDefault().register(this);
        ARouter.getInstance().inject(this);
        updateStatuBar();
        tvCheckDays = getActivity().findViewById(R.id.tv_check_days);
        ivEdit = getActivity().findViewById(R.id.iv_edit);
        tvHeartBeans = getActivity().findViewById(R.id.tv_heartBeans);
        tvUserName = getActivity().findViewById(R.id.tv_username);
        ivHeader = getActivity().findViewById(R.id.iv_header);
        LinearLayout xindou_layout = getActivity().findViewById(R.id.xindou_layout);
        SettingItemView rl_collecs = getActivity().findViewById(R.id.rl_collecs);
        SettingItemView rl_health_report = getActivity().findViewById(R.id.rl_health_report);
        ImageView rl_my_test = getActivity().findViewById(R.id.rl_my_test);
        SettingItemView rl_message_center = getActivity().findViewById(R.id.rl_message_center);
        SettingItemView rl_feedback = getActivity().findViewById(R.id.rl_feedback);
        SettingItemView rl_setting = getActivity().findViewById(R.id.rl_setting);
        ImageView rl_my_ordertreat = getActivity().findViewById(R.id.rl_my_ordertreat);
        LinearLayout ll_settled = getActivity().findViewById(R.id.ll_settled);
        ImageView rl_my_counsel = getActivity().findViewById(R.id.rl_my_counsel);
        xindou_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProtocolActivity.startActivity(getActivity(), Constant.Url.request_base_url + Constant.WebUrl.xindouRule, "??????????????????");

            }
        });
//        xindou_layout.setOnClickListener(this);
        ivHeader.setOnClickListener(this);
        tvUserName.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        rl_collecs.setOnClickListener(this);
        rl_health_report.setOnClickListener(this);
        rl_my_test.setOnClickListener(this);
        rl_message_center.setOnClickListener(this);
        rl_feedback.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        rl_my_ordertreat.setOnClickListener(this);
        rl_my_counsel.setOnClickListener(this);
        ll_settled.setOnClickListener(this);


        //????????????????????????????????????????????????????????????????????????????????????
        if (accountService != null && accountService.getGetAccountInfo() != null && accountService.getGetAccountInfo().isLogin) {
            requestUserBasicInfo();
            ivEdit.setVisibility(View.VISIBLE);
        } else {
            tvUserName.setText("??????/??????");
            tvHeartBeans.setText("??????");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.i("onResume");
        //???????????????????????????????????????????????????
        //???????????????home?????????????????????activity????????????????????????
        //?????????????????????tab???
        requestUserBasicInfo();
        mPresenter.getSignDays(this);
    }

    public void updateStatuBar() {
        if (getActivity() != null) {
            ImmersionBar.with(this)
                    .statusBarDarkFont(true)
                    .transparentStatusBar()
                    .init();
        }
    }

    @Override
    public void onClick(View view) {
        if (OneClickUtils.isFastClick()) {
            return;
        }
        int id = view.getId();//            case R.id.xindou_layout:
//                ProtocolActivity.startActivity(getActivity(), Constant.Url.request_base_url + Constant.WebUrl.xindouRule, "??????????????????");
//                break;
        if (id == R.id.iv_header || id == R.id.tv_username || id == R.id.iv_edit) {
            if (!isGoLogin()) {
                EditInfoActivity.startActivity(getActivity());     //????????????
            }
        } else if (id == R.id.rl_collecs) {
            if (!isGoLogin()) { //????????????
                startActivity(new Intent(getActivity(), MyCollectsActivity.class));
            }
        } else if (id == R.id.rl_health_report) {
            if (!isGoLogin()) {//????????????
                startActivity(new Intent(getActivity(), HealthReoprtActivity.class));
            }
        } else if (id == R.id.rl_my_test) {
            if (!isGoLogin()) {                //????????????
//                startActivity(new Intent(getActivity(), MyEvaluationActivity.class));
                MyEvaluationActivity.MyEvaluationIntentBean myEvaluationIntentBean = new MyEvaluationActivity.MyEvaluationIntentBean(1);
                MyEvaluationActivity.startActivity(getActivity(), myEvaluationIntentBean);
            }
        } else if (id == R.id.rl_message_center) {
            if (!isGoLogin()) {    //????????????
                startActivity(new Intent(getActivity(), MessageCenterActivity.class));
            }
        } else if (id == R.id.rl_feedback) {
            if (!isGoLogin()) {
                //????????????
                startActivity(new Intent(getActivity(), FeedBackActivity.class));
            }
        } else if (id == R.id.rl_setting) {
            if (!isGoLogin()) {
                //????????????
                SettingActivity.startActivityForResult(getActivity());
            }
        } else if (id == R.id.rl_my_ordertreat) {
            if (!isGoLogin()) {
                //????????????
                OrderTreatmentListActivity.startActivity(getActivity());

            }
        } else if (id == R.id.rl_my_counsel) {
            if (!isGoLogin()) {
                //????????????
                OrderConsultListActivity.startActivity(getActivity());

            }
        } else if (id == R.id.ll_settled) {
            if (!isGoLogin()) {
                //????????????
                ApplySettledActivity.startActivity(getActivity());

            }
        }

    }

    private boolean isGoLogin() {
        if (accountService != null && accountService.getGetAccountInfo() != null && accountService.getGetAccountInfo().isLogin) {
            return false;
        } else {
            ARouter.getInstance()
                    .build(ARouterConstant.Account.AccountLoginActivity)
                    .navigation(getActivity());
            return true;
        }

    }

    @Override
    protected void initMVP() {
        mView = new IContract.UserInfo.View() {
            @Override
            public void onGetUserBasicInfo(UserBasicInfoBean bean, ResultType resultType, String errorMsg) {
                switch (resultType) {
                    case NET_ERROR:
                        //???????????????
                        ToastUtils.showLong(getString(R.string.base_module_net_error));
                        break;
                    case SERVER_ERROR:
                        ToastUtils.showShort(errorMsg);
                        break;
                    case SERVER_NORMAL_DATANO:
                    case SERVER_NORMAL_DATAYES:

                        UserBasicInfoBean.ResultBean result = bean.getResult();
                        String nickname = result.getNickname();
                        if (!TextUtils.isEmpty(nickname)) {
                            tvUserName.setText(nickname);
                        } else {
                            tvUserName.setText("");
                        }

                        String benasNums = result.getBenasNums();
                        if (!TextUtils.isEmpty(benasNums)) {
                            tvHeartBeans.setText(benasNums + "??????");
                        } else {
                            tvHeartBeans.setText("0");
                        }

                        String photoUrl = result.getPhotoUrl();
                        if (TextUtils.isEmpty(photoUrl)) {
                            ivHeader.setImageResource(R.drawable.base_module_default_man);
                        } else {
                            //??????????????????
                            AccountBean accountBean = accountService.getGetAccountInfo();
                            accountBean.photoUrl = photoUrl + "";
                            AccountManager.getInstence().updateAccountInfo(accountBean);
                            accountService.updateAccountInfo(accountBean);
                            Glide.with(getActivity()).load(photoUrl)
                                    .placeholder(R.drawable.base_module_default_man)
                                    .dontAnimate()
                                    .into(ivHeader);
                        }
                        break;
                }

            }

            @Override
            public void onGetSignDays(BaseResponseBean4 bean, ResultType resultType, String errorMsg) {
                switch (resultType) {
                    case NET_ERROR:
                        //???????????????
                        ToastUtils.showLong(getString(R.string.base_module_net_error));
                        break;
                    case SERVER_ERROR:
                        ToastUtils.showShort(errorMsg);
                        break;
                    case SERVER_NORMAL_DATANO:
                    case SERVER_NORMAL_DATAYES:
                        String result = bean.getResult();
                        tvCheckDays.setText("???????????????" + result + "???");
                        break;
                }
            }

            @Override
            public void onComplete() {

            }
        };
        mPresenter = new UserInfoPresenter(getActivity(), mView);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void fromMyEvaluaionToTested(CommitToMyEvaluationBus evaluationBus) {
        if (evaluationBus.type == 10) {
            String url = evaluationBus.photo;
            if (!TextUtils.isEmpty(url)) {
                GlideImageLoader.loadImage(getActivity(), url, ivHeader);
            }

            String name = evaluationBus.name;
            if (!TextUtils.isEmpty(name)) {
                tvUserName.setText(name);
            }


            AccountBean accountBean = accountService.getGetAccountInfo();

            Logger.i("?????????????????????" + new Gson().toJson(accountBean));

            accountBean.userName = name;
            AccountManager.getInstence().updateAccountInfo(accountBean);

            Logger.i("?????????????????????" + new Gson().toJson(accountService.getGetAccountInfo()));


        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onLoginEventBus(LoginEventBus busBean) {
        //???????????????????????????
        if (busBean != null) {
            if (busBean.getAccountBean() != null) {
                if (busBean.getAccountBean().isLogin) {
                    //???????????????
                    requestUserBasicInfo();
                } else {
                    //????????????????????????
                    clearUserBasicInfo();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * ??????????????????????????????
     */
    public void requestUserBasicInfo() {
        if (accountService != null && accountService.getGetAccountInfo() != null && accountService.getGetAccountInfo().isLogin) {
            mPresenter.getUserBasicInfo(this);
        }

    }

    /**
     * ????????????????????????
     */
    public void clearUserBasicInfo() {
        tvUserName.setText("??????/??????");
        tvHeartBeans.setText("??????");
        ivHeader.setImageResource(R.drawable.base_module_default_man);
    }

}
