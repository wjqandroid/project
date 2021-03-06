package com.visionvera.psychologist.c.module.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.visionvera.library.arouter.ARouterConstant;
import com.visionvera.library.arouter.service.IAccountService;
import com.visionvera.library.base.BaseMVPFragment;
import com.visionvera.library.base.Constant;
import com.visionvera.library.base.activity.ProtocolActivity;
import com.visionvera.library.base.bean.BaseResponseBean3;
import com.visionvera.library.eventbus.commonbean.LoginEventBus;
import com.visionvera.library.util.OneClickUtils;
import com.visionvera.library.util.RecycleUtil;
import com.visionvera.library.widget.decoration.SpaceItemDecoration;
import com.visionvera.library.widget.dialog.ShilianwangCallingPopup;
import com.visionvera.live.module.home.WatchLiveActivity;
import com.visionvera.live.module.home.bean.IntentArouterBean;
import com.visionvera.live.module.home.bean.IntentBean;
import com.visionvera.psychologist.c.R;
import com.visionvera.psychologist.c.R2;
import com.visionvera.psychologist.c.databinding.EvaluationModuleFragmentHomeBinding;
import com.visionvera.psychologist.c.module.EvaluationGauge.activity.SelfAssessmentGaugeActivity;
import com.visionvera.psychologist.c.module.MainActivity;
import com.visionvera.psychologist.c.module.allevaluation.fragment.AllEvaluationActivity;
import com.visionvera.psychologist.c.module.counselling.activity.CounsellingMainActivity;
import com.visionvera.psychologist.c.module.counselling.activity.CounselorDetailActivity;
import com.visionvera.psychologist.c.module.counselling.activity.SearchCounselorActivity;
import com.visionvera.psychologist.c.module.counselling.bean.SuggestListBean;
import com.visionvera.psychologist.c.module.home.activity.EmergencyRescueActivity;
import com.visionvera.psychologist.c.module.home.activity.EveryDaySignInActivity;
import com.visionvera.psychologist.c.module.home.activity.PolicyAssemblyActivity;
import com.visionvera.psychologist.c.module.home.adapter.BannerImageNetAdapter;
import com.visionvera.psychologist.c.module.home.adapter.HeadAdapter;
import com.visionvera.psychologist.c.module.home.adapter.HomeArticleRecommendationAdapter;
import com.visionvera.psychologist.c.module.home.adapter.HomeConsultingAdapter;
import com.visionvera.psychologist.c.module.home.adapter.HomeHotCategoryAdapter;
import com.visionvera.psychologist.c.module.home.adapter.HomeSelectedEvaluationAdapter;
import com.visionvera.psychologist.c.module.home.adapter.HomeSuggestConsultantAdapter;
import com.visionvera.psychologist.c.module.home.adapter.HomeSuggestStaffAdapter;
import com.visionvera.psychologist.c.module.home.adapter.HomeWonderfulCourseAdapter;
import com.visionvera.psychologist.c.module.home.bean.BannerBean;
import com.visionvera.psychologist.c.module.home.bean.LocalCustomBean;
import com.visionvera.psychologist.c.module.home.bean.HomeConsultingBean;
import com.visionvera.psychologist.c.module.home.bean.HotCategoriesBean;
import com.visionvera.psychologist.c.module.home.bean.HotEvaluationBean;
import com.visionvera.psychologist.c.module.home.bean.LiveBannerBean;
import com.visionvera.psychologist.c.module.home.bean.MeetingRoomBean;
import com.visionvera.psychologist.c.module.home.bean.RecommendArticleBean;
import com.visionvera.psychologist.c.module.home.bean.RecommendCourseBean;
import com.visionvera.psychologist.c.module.home.bean.SelectedEvaluationBean;
import com.visionvera.psychologist.c.module.home.contract.IContract;
import com.visionvera.psychologist.c.module.home.presenter.HomePresenter;
import com.visionvera.psychologist.c.module.knowledge_library.activity.ArticleDetailActivity;
import com.visionvera.psychologist.c.module.ordertreatment.activity.NewOrderTreatmentDetailActivity;
import com.visionvera.psychologist.c.module.ordertreatment.activity.OrderTreatmentMainActivity;
import com.visionvera.psychologist.c.module.ordertreatment.activity.TypeRecommendActivity;
import com.visionvera.psychologist.c.module.ordertreatment.bean.DoctorResponseBean;
import com.visionvera.psychologist.c.module.search.activity.SearchEvaluationActivity;
import com.visionvera.psychologist.c.module.usercenter.activity.MessageCenterActivity;
import com.visionvera.psychologist.c.utils.RxPartMapUtils;
import com.visionvera.zong.api.VVClient;
import com.visionvera.zong.model.ListItemBean;
import com.visionvera.zong.model.http.CovertBean;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.util.BannerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.webrtcold.SurfaceViewRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.OnClick;


public class HomeFragment extends BaseMVPFragment<IContract.Home.View, HomePresenter> {
    @Autowired(name = ARouterConstant.Account.accountModuleSetvice)
    IAccountService accountService;

    EvaluationModuleFragmentHomeBinding viewBinding;

    private SelectedEvaluationFragment selectedEvaluationFragment;
    private MyCollectsEvaluationFragment myCollectsEvaluationFragment;

    private List<MeetingRoomBean.ResultBean> meetingRoomList = new ArrayList<>();
    //???????????????????????????device?????????????????????
    private int devicePosition = 0;
    //??????????????????????????????XXXXX???
    private String tempSelectDeviceId;

    private static final String TAG = "HomeFragment";
    private boolean shilianwangSDKinit;
    private BasePopupView basePopupView;

    private HeadAdapter rv_headAdapter;
    private List<HeadItem> headDatalist = new ArrayList<>();
    private List<String> headStrlist = new ArrayList<>();
    private List<Integer> headIconList = new ArrayList<>();
    private List<HotEvaluationBean.ResultBean.ScaleDictListBean> mHotCepingList = new ArrayList<>();
    private int pageIndex = 1;

    //???????????????adapter
    private BannerImageNetAdapter mBannerAdapter;
    private List<BannerBean.ResultBean> bannerList = new ArrayList<>();
    private List<Integer> customBannerList = new ArrayList<Integer>();

    //????????????adapter
    private HomeSelectedEvaluationAdapter evaluationAdapter;
    private List<SelectedEvaluationBean.DataBean> evaluationBeanList = new ArrayList<>();

    //????????????adapter
    private HomeHotCategoryAdapter categoryAdapter;
    private List<HotCategoriesBean> categoriesBeanList = new ArrayList<>();
    private String[] types = new String[]{"????????????", "????????????", "????????????", "????????????", "????????????", "????????????"};
    private int[] typeId = {53, 31, 28, 35, 34, 32};

    //????????????adapter
    private HomeWonderfulCourseAdapter courseAdapter;
    private List<RecommendCourseBean> courseBeanList = new ArrayList<>();

    //????????????adapter
    private HomeArticleRecommendationAdapter articleRecommendationAdapter;
    private List<RecommendArticleBean> articleBeanList = new ArrayList<>();

    //??????????????????adapter
    private HomeConsultingAdapter consultingAdapter;
    private List<HomeConsultingBean> consultingList = new ArrayList<>();
    private String[] askTitles = new String[]{"??????????????????", "?????????????????"};

    //?????????????????????adapter
    private HomeSuggestConsultantAdapter suggestConsultantAdapter;
    private List<SuggestListBean.ResultBean> suggestConsultantList = new ArrayList<>();

    //????????????adapter
    private HomeSuggestStaffAdapter staffAdapter;
    //List<DoctorResponseBean>  List<DoctorListResponseBean.ResultBean>
    private List<DoctorResponseBean> staffList = new ArrayList<>();

    /*
     * 1:???????????????????????????
     * 2?????????????????????
     * */
    private int recommendFlag = 1;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected void lazyLoadData() {
    }

    @Override
    protected int getLayoutResID() {
        return 0;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        viewBinding = EvaluationModuleFragmentHomeBinding.inflate(inflater, container, false);
        View view = viewBinding.getRoot();
        return view;
    }

    @Override
    protected void initYourself() {
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);
        updateStatuBar();
        initView();



//        requestEvaluationType();
//        requestMeetingRoomList(false);
//        initCallPopup();
    }

    /**
     * ?????????????????????
     */
    private void initView() {

//            viewBinding.rlEvaluationSearch.setOnClickListener(this);
//            viewBinding.ivMessage.setOnClickListener(this);
//            viewBinding.homeLookAll.setOnClickListener(this);
//            viewBinding.homeCourseLayoutMore.setOnClickListener(this);
//            viewBinding.homeArticleLayoutMore.setOnClickListener(this);
//            viewBinding.homeArticleLayoutMore.setOnClickListener(this);
//            viewBinding.btnPublicWelfare.setOnClickListener(this);
//            viewBinding.btnTreeHole.setOnClickListener(this);
//            viewBinding.btnPunchClock.setOnClickListener(this);

        viewBinding.fragmentHomeSmartrefresh.setEnableLoadMore(false);
        viewBinding.fragmentHomeSmartrefresh.setOnRefreshListener(refreshLayout -> {
            getArticleRecommendation();
            getRecommendCourse();
            requestSelectedData();
            getSuggestList();
        });

        initBanner();
        initHeadRecyclerView();
        initSelectedEvaluationRecycle();
        initCategoryRecycle();
        initWonderfulCourseRecycle();
        initHomeArticleRecycle();
        initConsulting();
        initSuggestConsultant();
        initSuggestStaff();

//        requestBanner();
        getArticleRecommendation();
        getRecommendCourse();
        requestSelectedData();
        getSuggestList();
    }


    //???????????????banner??????
    private void initBanner() {
        List<LocalCustomBean> customBannerList = new ArrayList<>();
//        customBannerList.add(new LocalCustomBean("", "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 3, R.mipmap.img_banner4));
//        customBannerList.add(new LocalCustomBean("", "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 1, R.mipmap.img_banner3));
        customBannerList.add(new LocalCustomBean("", "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", 4, R.mipmap.img_banner5));
//        customBannerList.add(new LocalCustomBean("", "https://img.zcool.cn/community/01639a56fb62ff6ac725794891960d.jpg", 2, R.mipmap.img_banner1));
//        customBannerList.add(new LocalCustomBean("", "https://img.zcool.cn/community/01270156fb62fd6ac72579485aa893.jpg", 3, R.mipmap.img_banner2));


        //????????????????????????????????????
        mBannerAdapter = new BannerImageNetAdapter(customBannerList);
        //????????????????????????
        viewBinding.homeBanner.setDelayTime(3000);
        viewBinding.homeBanner.addBannerLifecycleObserver(HomeFragment.this)//???????????????????????????
                .setAdapter(mBannerAdapter)
                .setIndicator(new CircleIndicator(getContext()))
                .setIndicatorSpace((int) BannerUtils.dp2px(5))
                .setBannerGalleryMZ(16)
                .start();

        viewBinding.homeBanner.setOnBannerListener(new com.youth.banner.listener.OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {

                if (customBannerList.get(position).getViewType() == 1) {
                    //???????????????????????????????????????????????????
                    //?????????????????????????????????????????????????????????????????????????????????
                    ArticleDetailActivity.Companion.IntentBean intentBean = new ArticleDetailActivity.Companion.IntentBean(95, "4");
                    ArticleDetailActivity.Companion.startActivity(getActivity(), intentBean);
                }
//                else if (customBannerList.get(position).getViewType() == 2) {
//                    //????????????????????????????????????banner???
//                    //???????????????
//                    new XPopup.Builder(getContext())
////                            .asImageViewer((ImageView) viewBinding.homeBanner.getAdapter().getViewHolder().itemView, "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", new ImageLoader())
////                            .asImageViewer(null, "https://img.zcool.cn/community/013de756fb63036ac7257948747896.jpg", new ImageLoader())
//                            .asImageViewer(null, getResources().getDrawable(R.mipmap.img_poster_sunshine), new ImageLoader())
//                            .isShowSaveButton(false)
//                            .show();// ??????????????????XPopup?????????????????????????????????????????????????????????????????????????????????Glide???OkGo???????????????????????????????????????:
//                }
                else if (customBannerList.get(position).getViewType() == 2) {
                    //??????????????????????????????????????????
                    //??????WEB?????????????????????????????????
                    ProtocolActivity.startActivity(getActivity(), "https://slyl-mhsp-1301295327.cos.ap-beijing.myqcloud.com/fronted/huizhou/mobile/workdyNamicsArticle.html", "????????????????????????????????????????????????");
                }
//                if (bannerList.get(position).getType() == 1) {
//                    //??????
//                    if (bannerList.get(position).getParamsObj().size() > 0) {
//                        int courseId = bannerList.get(position).getParamsObj().get(0).getParamsValue();
//                        SelfAssessmentGaugeActivity.GaugeIntentBean gaugeIntentBean =
//                                new SelfAssessmentGaugeActivity.GaugeIntentBean(courseId, 2, 0, 0);
//                        SelfAssessmentGaugeActivity.startActivity(getActivity(), gaugeIntentBean);
//                    }
//
//                } else if (bannerList.get(position).getType() == 2) {
//                    //??????
//                    if (accountService != null && accountService.getGetAccountInfo() != null && accountService.getGetAccountInfo().isLogin) {
//                        ((MainActivity) getActivity()).showLoadingDialog();
//                        if (bannerList.get(position).getParamsObj().size() > 0) {
//                            int courseId = bannerList.get(position).getParamsObj().get(0).getParamsValue();
//                            mPresenter.getLiveBanner(courseId, HomeFragment.this);
//                        }
//                    } else {
//                        ToastUtils.showShort("?????????????????????");
//                        ARouter.getInstance()
//                                .build(ARouterConstant.Account.AccountLoginActivity)
//                                .navigation(getActivity());
//                    }
//                }
            }
        });
    }

    /**
     * ???????????????banner????????????????????????
     */
    private void initHeadRecyclerView() {
        headStrlist.clear();
        headStrlist.add("????????????");
        headStrlist.add("????????????");
        headStrlist.add("????????????");
//        headStrlist.add("????????????");
        headStrlist.add("????????????");
//        headStrlist.add("????????????");
        headIconList.clear();
        headIconList.add(R.mipmap.img_psychology);
        headIconList.add(R.mipmap.img_consult);
        headIconList.add(R.mipmap.img_appointment);
        headIconList.add(R.mipmap.img_assistance);
//        headIconList.add(R.mipmap.img_clock);
//        headIconList.add(R.drawable.evaluation_module_emergency_help);
//        headIconList.add(R.drawable.evaluation_module_checkin_daily);
        headDatalist.clear();
        for (int i = 0; i < headIconList.size(); i++) {
            HeadItem headItem = new HeadItem();
            headItem.name = headStrlist.get(i);
            headItem.iconId = headIconList.get(i);
            headDatalist.add(headItem);
        }

        RecycleUtil.initGridRecycleNoScroll(viewBinding.homeTopFourRecycle, getActivity(), false, 4);

        rv_headAdapter = new HeadAdapter(headDatalist, getActivity());
        viewBinding.homeTopFourRecycle.setAdapter(rv_headAdapter);
        rv_headAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TextView tv_name = view.findViewById(R.id.tv_name);

                switch (tv_name.getText().toString()) {
                    case "????????????":
//                        ToastUtils.showShort("????????????");
                        startActivity(new Intent(getActivity(), OrderTreatmentMainActivity.class));
                        break;
                    case "????????????":
                        startActivity(new Intent(getActivity(), AllEvaluationActivity.class));
                        break;
                    case "????????????":
                        startActivity(new Intent(getActivity(), CounsellingMainActivity.class));
                        break;
                    case "????????????":
                        ToastUtils.showShort("????????????");
                        break;
                    case "????????????":
                        startActivity(new Intent(getActivity(), EmergencyRescueActivity.class));
                        break;
                    case "????????????":
                        if (accountService != null && accountService.getGetAccountInfo() != null && !accountService.getGetAccountInfo().isLogin) {
                            ARouter.getInstance()
                                    .build(ARouterConstant.Account.AccountLoginActivity)
                                    .navigation(getActivity());
                        } else {
                            EveryDaySignInActivity.startActivity(getActivity());
                        }
                        break;
                }
            }
        });
    }

    //???????????????????????????
    private void initSelectedEvaluationRecycle() {
        RecycleUtil.initRecycle(viewBinding.homeSelectedEvaluationRecycle, getActivity(), true, 1);

        evaluationAdapter = new HomeSelectedEvaluationAdapter(evaluationBeanList, getActivity());
        viewBinding.homeSelectedEvaluationRecycle.setAdapter(evaluationAdapter);

        evaluationAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                SelectedEvaluationBean.DataBean dataBean =
                        (SelectedEvaluationBean.DataBean) adapter.getData().get(position);
                SelfAssessmentGaugeActivity.GaugeIntentBean gaugeIntentBean =
                        new SelfAssessmentGaugeActivity.GaugeIntentBean(dataBean.id, 2, 0, 0);
                SelfAssessmentGaugeActivity.startActivity(getActivity(), gaugeIntentBean);
            }
        });
    }

    //???????????????????????????
    private void initCategoryRecycle() {
        RecycleUtil.initGridRecycleNoScroll(viewBinding.homeClassificationRecycle, getActivity(), false, 3);

        for (int i = 0; i < types.length; i++) {
            HotCategoriesBean categoriesBean = new HotCategoriesBean();
            categoriesBean.id = typeId[i];
            categoriesBean.name = types[i];
            categoriesBeanList.add(categoriesBean);
        }

        categoryAdapter = new HomeHotCategoryAdapter(categoriesBeanList, getActivity());
        viewBinding.homeClassificationRecycle.setAdapter(categoryAdapter);

        categoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                HotCategoriesBean evaluationTypeBean = categoryAdapter.getItem(position);
                String name = evaluationTypeBean.name;
                //??????????????????????????????  ?????????????????????????????????  ????????????
                if ("????????????".equals(name) || "????????????".equals(name) || "????????????".equals(name)) {
                    SearchCounselorActivity.MakeAnAppointmentIntentBean intentBean = new SearchCounselorActivity.
                            MakeAnAppointmentIntentBean(evaluationTypeBean.name, evaluationTypeBean.id);
                    SearchCounselorActivity.startActivity(getActivity(), intentBean);
                } else {
                    TypeRecommendActivity.IntentBean intentBean = new TypeRecommendActivity.IntentBean(evaluationTypeBean.id + "", name, position);
                    TypeRecommendActivity.startActivity(getActivity(), intentBean);
                }

            }
        });

    }

    //???????????????????????????
    private void initWonderfulCourseRecycle() {
        RecycleUtil.initRecycle(viewBinding.homeCourseRecycle, getActivity(), false, 2);

        courseAdapter = new HomeWonderfulCourseAdapter(courseBeanList, getActivity());
        viewBinding.homeCourseRecycle.setAdapter(courseAdapter);

        courseAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                RecommendCourseBean bean = (RecommendCourseBean) adapter.getItem(position);

                IntentBean intentBean = new IntentBean();
                intentBean.courseId = bean.courseId;
                intentBean.playModel = bean.playModel;
                intentBean.url = bean.videoUrl;
                intentBean.title = bean.courseName;
                intentBean.imageUrl = bean.masterPicUrl;
                intentBean.isCollect = bean.isCollectCourse;
                intentBean.type = bean.courseTypeType;

                intentBean.startTime = bean.startTime;
                intentBean.endTime = bean.endTime;
                intentBean.duration = bean.duration;
                intentBean.hospital = bean.expertHospital;
                intentBean.description = bean.description;
                intentBean.expertId = bean.expertId;
                intentBean.liveStatus = bean.liveStatus;

                Intent intent = new Intent(getContext(), WatchLiveActivity.class);
                intent.putExtra("IntentBean", intentBean);
                startActivity(intent);

            }
        });

    }

    //???????????????????????????
    private void initHomeArticleRecycle() {
        viewBinding.homeArticleRecycle.addItemDecoration(new SpaceItemDecoration(2, getResources().getColor(R.color.color_f5f7fb)));

        RecycleUtil.initNoScrollRecycle(viewBinding.homeArticleRecycle, getActivity(), false);
        articleRecommendationAdapter = new HomeArticleRecommendationAdapter(articleBeanList, getActivity());
        viewBinding.homeArticleRecycle.setAdapter(articleRecommendationAdapter);

        articleRecommendationAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                RecommendArticleBean articleBean = (RecommendArticleBean) adapter.getItem(position);
                ArticleDetailActivity.Companion.IntentBean intentBean = new ArticleDetailActivity.Companion.IntentBean(articleBean.id, "4");
                ArticleDetailActivity.Companion.startActivity(getActivity(), intentBean);
            }
        });
    }

    //???????????????????????????recycle
    private void initConsulting() {
        RecycleUtil.initRecycle(viewBinding.homeAskTitleRecycle, getActivity(), false, 2);

        consultingList.clear();
        for (int i = 0; i < askTitles.length; i++) {
            HomeConsultingBean bean = new HomeConsultingBean();
            bean.name = askTitles[i];
            if (i == 0) {
                bean.isSelect = true;
            } else {
                bean.isSelect = false;
            }
            consultingList.add(bean);
        }

        consultingAdapter = new HomeConsultingAdapter(consultingList, getActivity());
        viewBinding.homeAskTitleRecycle.setAdapter(consultingAdapter);

        consultingAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                HomeConsultingBean bean = (HomeConsultingBean) adapter.getItem(position);
                String name = bean.name;
                for (int i = 0; i < consultingList.size(); i++) {
                    consultingList.get(i).isSelect = false;
                    if (name.equals(consultingList.get(i).name)) {
                        consultingList.get(i).isSelect = true;
                    }
                }

                if ("??????????????????".equals(name)) {
                    recommendFlag = 1;
                    viewBinding.homeLookAll.setText("?????????????????????");
                    viewBinding.homeAskStaffRecycle.setVisibility(View.GONE);
                    viewBinding.homeAskRecycle.setVisibility(View.VISIBLE);
                    getSuggestList();
                } else {
                    recommendFlag = 2;
                    viewBinding.homeLookAll.setText("??????????????????????????");
                    viewBinding.homeAskStaffRecycle.setVisibility(View.VISIBLE);
                    viewBinding.homeAskRecycle.setVisibility(View.GONE);
                    getRecommendStaffList();
                }

                consultingAdapter.notifyDataSetChanged();
            }
        });


    }

    //????????????????????????????????????
    private void initSuggestConsultant() {
        RecycleUtil.initNoScrollRecycle(viewBinding.homeAskRecycle, getActivity(), false);

        suggestConsultantAdapter = new HomeSuggestConsultantAdapter(suggestConsultantList, getActivity());
        viewBinding.homeAskRecycle.setAdapter(suggestConsultantAdapter);

        suggestConsultantAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                SuggestListBean.ResultBean resultBean = (SuggestListBean.ResultBean) adapter.getItem(position);

                CounselorDetailActivity.CounselorDetailIntentBean intentBean
                        = new CounselorDetailActivity.CounselorDetailIntentBean(resultBean.getPsyInfoId(), resultBean.getUserId());

                CounselorDetailActivity.startActivity(getActivity(), intentBean);
            }
        });
    }

    //???????????????????????????
    private void initSuggestStaff() {
        RecycleUtil.initNoScrollRecycle(viewBinding.homeAskStaffRecycle, getActivity(), false);

        staffAdapter = new HomeSuggestStaffAdapter(staffList, getActivity());
        viewBinding.homeAskStaffRecycle.setAdapter(staffAdapter);

        staffAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                DoctorResponseBean resultBean = (DoctorResponseBean) adapter.getItem(position);
                NewOrderTreatmentDetailActivity.IntentBean intentBean
                        = new NewOrderTreatmentDetailActivity.IntentBean(resultBean.getId(), resultBean.getUserId());
                NewOrderTreatmentDetailActivity.startActivity(getActivity(), intentBean);
            }
        });
    }

    /**
     * ????????????????????????
     */
    public void requestSelectedData() {
        Map<String, Integer> params = new HashMap<>();
        params.put("pageIndex", pageIndex);
        mPresenter.getHotEvaluation(RxPartMapUtils.toRequestBodyOfIntegerMap(params), this);
//        if (pageIndex == 1) {
//            pageIndex = 2;
//        } else {
//            pageIndex = 1;
//        }
    }

    /*
     * ??????????????????
     * */
    private void getRecommendCourse() {
        mPresenter.getRecommendCourse(this);
    }

    /*
     * ????????????????????????
     * */
    private void getArticleRecommendation() {
        mPresenter.getArticleRecommendation(this);
    }

    /*
     * ???????????????????????????
     * */
    private void getSuggestList() {
        mPresenter.getSuggestList(this);
    }

    /*
     * ??????????????????
     * */
    private void getRecommendStaffList() {
        mPresenter.getRecommendStaffList(this);
    }

    public void updateStatuBar() {
        if (getActivity() != null) {
             ImmersionBar.with(this)
                    .transparentStatusBar()
                    .statusBarDarkFont(true)
                    .fitsSystemWindows(false)
                    .init();
        }
        {
            viewBinding.rlEvaluationSearch.setOnClickListener(this);
            viewBinding.ivMessage.setOnClickListener(this);
            viewBinding.homeLookAll.setOnClickListener(this);
            viewBinding.homeCourseLayoutMore.setOnClickListener(this);
            viewBinding.homeArticleLayoutMore.setOnClickListener(this);
            viewBinding.homeArticleLayoutMore.setOnClickListener(this);
            viewBinding.btnPublicWelfare.setOnClickListener(this);
            viewBinding.btnTreeHole.setOnClickListener(this);
            viewBinding.btnPunchClock.setOnClickListener(this);
        }
    }

    /**
     * ???????????????
     */
    private void requestBanner() {
        Map<String, Integer> params = new HashMap<>();
        params.put("type", 1);
        mPresenter.getSystemBanner(RxPartMapUtils.toRequestBodyOfIntegerMap(params), this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {

        }
    }

    public class HeadItem {
        public String name;
        public int iconId;
    }

//
//    /**
//     * ?????????????????????????????????
//     */
//    private void initCallPopup() {
//        basePopupView = new XPopup.Builder(getActivity())
//                .dismissOnBackPressed(false)
//                .dismissOnTouchOutside(false)
//                .asCustom(new ShilianwangCallingPopup(Objects.requireNonNull(getActivity()), () -> {
//                    endCall();
//                }));
//
//    }

    /**
     * ????????????????????????????????????????????????
     */
    private void requestMeetingRoomList(boolean isCallVideo) {
        if (accountService != null && accountService.getGetAccountInfo() != null && !accountService.getGetAccountInfo().isLogin) {
//            ToastUtils.showLong("?????????????????????");
//            ARouter.getInstance()
//                    .build(ARouterConstant.Account.AccountLoginActivity)
//                    .navigation(getActivity());
        } else {
            mPresenter.getHeartHelpMeetingRoomInfo(isCallVideo, this);
        }

    }

    @Override
    protected void initMVP() {
        mView = new IContract.Home.View() {
            @Override
            public void onGetSystemBanner(BannerBean responseBean, ResultType resultType, String errorMsg) {
                if (viewBinding.fragmentHomeSmartrefresh != null) {
                    viewBinding.fragmentHomeSmartrefresh.finishRefresh();
                }

                switch (resultType) {
                    case NET_ERROR:
                        //???????????????
                        ToastUtils.showLong(getString(R.string.base_module_net_error));
                        break;
                    case SERVER_ERROR:
                    case SERVER_NORMAL_DATANO:
                        ToastUtils.showShort(errorMsg);
                        break;
                    case SERVER_NORMAL_DATAYES:
                        bannerList.clear();
                        bannerList.addAll(responseBean.getResult());
                        mBannerAdapter.notifyDataSetChanged();
                        break;
                }
            }

         /*   @Override
            public void onGetEvaluationType(EvaluationTypeBean responseBean, ResultType resultType, String errorMsg) {
            }*/

            @Override
            public void onHeartHelpMeetingRoomInfo(boolean isCallVideo, MeetingRoomBean meetingRoomBean, ResultType resultType, String errorMsg) {
                switch (resultType) {
                    case NET_ERROR:
                        //???????????????
                        break;
                    case SERVER_ERROR:
                    case SERVER_NORMAL_DATANO:
                        break;
                    case SERVER_NORMAL_DATAYES:
                        if (meetingRoomBean != null && meetingRoomBean.getResult() != null && meetingRoomBean.getResult().size() > 0) {
                            meetingRoomList.clear();
                            meetingRoomList.addAll(meetingRoomBean.getResult());
                        }

                        if (isCallVideo) {
                            //????????????????????????????????????
//                            CallShilianwang();
                        } else {
                            //?????????????????????????????????
                        }
                        break;
                }
            }

            @Override
            public void onLiveBanner(int courseId, LiveBannerBean liveBannerBean, ResultType resultType, String errorMsg) {
                ((MainActivity) getActivity()).hideDialog();
                switch (resultType) {
                    case NET_ERROR:
                        //???????????????
                        if (liveBannerBean.getCode() != 401) {
                            ToastUtils.showLong(getString(R.string.base_module_net_error));
                        }
                        break;
                    case SERVER_ERROR:
                    case SERVER_NORMAL_DATANO:
                        ToastUtils.showShort(errorMsg);
                        break;
                    case SERVER_NORMAL_DATAYES:
                        //???????????????????????????????????????????????????????????????
                        boolean isExistCourse = false;
                        for (int i = 0; i < liveBannerBean.getResult().size(); i++) {
                            if (courseId == liveBannerBean.getResult().get(i).getCourseId()) {
                                IntentArouterBean intentBean = new IntentArouterBean();
                                LiveBannerBean.ResultBean bean = liveBannerBean.getResult().get(i);
                                intentBean.courseId = bean.getCourseId();
                                intentBean.playModel = bean.getPlayModel();
                                intentBean.url = bean.getVideoUrl();
                                intentBean.title = bean.getCourseName();
                                intentBean.imageUrl = bean.getMasterPicUrl();
//                        intentBean.isCollect = bean.isCollectCourse();
                                intentBean.type = bean.getCourseTypeType();

                                intentBean.startTime = bean.getStartTime();
                                intentBean.endTime = bean.getEndTime();
                                intentBean.duration = bean.getDuration();
                                intentBean.hospital = bean.getExpertHospital();
                                intentBean.description = bean.getDescription();
                                intentBean.expertId = bean.getExpertId();
                                intentBean.liveStatus = bean.getLiveStatus();

                                ARouter.getInstance().build(ARouterConstant.Live.watchLiveActivity)
                                        .withObject("intentBean", intentBean).navigation();
                                isExistCourse = true;
                                break;
                            } else {
                                isExistCourse = false;
                            }
                        }
                        if (!isExistCourse) {
                            ToastUtils.showShort("?????????????????????");
                        }

                        break;
                }
            }

            @Override
            public void onGetHotEvaluation(BaseResponseBean3<SelectedEvaluationBean> responseBean, ResultType resultType, String errorMsg) {
                if (viewBinding.fragmentHomeSmartrefresh != null) {
                    viewBinding.fragmentHomeSmartrefresh.finishRefresh();
                }
                switch (resultType) {
                    case NET_ERROR:
                    case SERVER_ERROR:
                        break;
                    case SERVER_NORMAL_DATANO:
                        viewBinding.homeSelectedPsychologyLayout.setVisibility(View.GONE);
                        break;
                    case SERVER_NORMAL_DATAYES:
                        viewBinding.homeSelectedPsychologyLayout.setVisibility(View.VISIBLE);
                        if (evaluationBeanList != null && evaluationBeanList.size() > 0) {
                            evaluationBeanList.clear();
                        }

                        evaluationBeanList.addAll(responseBean.getResult().scaleDictList);
                        evaluationAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onGetArticleRecommendation(BaseResponseBean3<List<RecommendArticleBean>> responseBean, ResultType resultType, String errorMsg) {
                if (viewBinding.fragmentHomeSmartrefresh != null) {
                    viewBinding.fragmentHomeSmartrefresh.finishRefresh();
                }
                switch (resultType) {
                    case NET_ERROR:
                    case SERVER_ERROR:
                        break;
                    case SERVER_NORMAL_DATANO:
                        viewBinding.homeArticleLayout.setVisibility(View.GONE);
                        break;
                    case SERVER_NORMAL_DATAYES:
                        viewBinding.homeArticleLayout.setVisibility(View.VISIBLE);
                        if (articleBeanList != null && articleBeanList.size() > 0) {
                            articleBeanList.clear();
                        }

                        int size = responseBean.getResult().size();
                        if (size > 3) {
                            articleBeanList.addAll(responseBean.getResult().subList(0, 3));
                        } else {
                            articleBeanList.addAll(responseBean.getResult());
                        }
                        articleRecommendationAdapter.notifyDataSetChanged();

                        break;
                }
            }

            @Override
            public void onGetRecommendCourse(BaseResponseBean3<List<RecommendCourseBean>> responseBean, ResultType resultType, String errorMsg) {
                if (viewBinding.fragmentHomeSmartrefresh != null) {
                    viewBinding.fragmentHomeSmartrefresh.finishRefresh();
                }
                switch (resultType) {
                    case NET_ERROR:
                    case SERVER_ERROR:
                        break;
                    case SERVER_NORMAL_DATANO:
                        viewBinding.homeCourseLayout.setVisibility(View.GONE);
                        break;
                    case SERVER_NORMAL_DATAYES:
                        viewBinding.homeCourseLayout.setVisibility(View.VISIBLE);
                        if (courseBeanList != null && courseBeanList.size() > 0) {
                            courseBeanList.clear();
                        }

                        int size = responseBean.getResult().size();
                        if (size > 4) {
                            courseBeanList.addAll(responseBean.getResult().subList(0, 4));
                        } else {
                            courseBeanList.addAll(responseBean.getResult());
                        }
                        courseAdapter.notifyDataSetChanged();

                        break;
                }
            }

            @Override
            public void onGetSuggestList(SuggestListBean responseBean, ResultType resultType, String errorMsg) {
                if (viewBinding.fragmentHomeSmartrefresh != null) {
                    viewBinding.fragmentHomeSmartrefresh.finishRefresh();
                }
                switch (resultType) {
                    case NET_ERROR:
                    case SERVER_ERROR:
                        //?????????????????????????????????code?????????
                        ToastUtils.showShort(getString(R.string.base_module_net_error));
//                        showError(getString(R.string.base_module_net_error));
                        break;
                    case SERVER_NORMAL_DATANO:
                        //?????????result???null
                        ToastUtils.showShort(errorMsg);
//                        showError(getString(R.string.base_module_no_more_data));
                        break;
                    case SERVER_NORMAL_DATAYES:
                        List<SuggestListBean.ResultBean> result = responseBean.getResult();
                        if (result.size() == 0) {
//                            showEmpty();
                        } else {
                            suggestConsultantList.clear();
                            if (result.size() > 3) {
                                suggestConsultantList.addAll(result.subList(0, 3));
                            } else {
                                suggestConsultantList.addAll(result);
                            }

                            suggestConsultantAdapter.notifyDataSetChanged();

                        }
                        break;
                }
            }

            @Override
            public void onGetRecommendStaffList(BaseResponseBean3<List<DoctorResponseBean>> responseBean, ResultType resultType, String errorMsg) {
                if (viewBinding.fragmentHomeSmartrefresh != null) {
                    viewBinding.fragmentHomeSmartrefresh.finishRefresh();
                }
                switch (resultType) {
                    case NET_ERROR:
                    case SERVER_ERROR:
                        break;
                    case SERVER_NORMAL_DATANO:
                        break;
                    case SERVER_NORMAL_DATAYES:
                        if (staffList != null && staffList.size() > 0) {
                            staffList.clear();
                        }

                        int size = responseBean.getResult().size();
                        if (size > 4) {
                            staffList.addAll(responseBean.getResult().subList(0, 3));
                        } else {
                            staffList.addAll(responseBean.getResult());
                        }
                        staffAdapter.notifyDataSetChanged();

                        break;
                }
            }

            @Override
            public void onComplete() {

            }
        };
        mPresenter = new HomePresenter(getActivity(), mView);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (OneClickUtils.isFastClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.rl_evaluation_search) {//????????????
            startActivity(new Intent(getActivity(), SearchEvaluationActivity.class));
        } else if (id == R.id.iv_message) {//????????????
            if (accountService != null && !accountService.getGetAccountInfo().isLogin) {
                ToastUtils.showShort("?????????????????????");
                ARouter.getInstance()
                        .build(ARouterConstant.Account.AccountLoginActivity)
                        .navigation(getActivity());
                return;
            }
            startActivity(new Intent(getActivity(), MessageCenterActivity.class));
        } else if (id == R.id.home_look_all) {
            if (recommendFlag == 1) { //???????????????????????????
                startActivity(new Intent(getActivity(), CounsellingMainActivity.class));
            } else { //??????????????????
                startActivity(new Intent(getActivity(), OrderTreatmentMainActivity.class));
            }
        } else if (id == R.id.home_course_layout_more) { //??????????????????
            if (getActivity() instanceof setChecked) {
                if (accountService != null && accountService.getGetAccountInfo() != null && !accountService.getGetAccountInfo().isLogin) {
                    ARouter.getInstance()
                            .build(ARouterConstant.Account.AccountLoginActivity)
                            .navigation(getActivity());
                } else {
                    ((setChecked) getActivity()).onSetChecked(2);
                }
            }
        } else if (id == R.id.home_article_layout_more) { //????????????
            if (getActivity() instanceof setChecked) {
                ((setChecked) getActivity()).onSetChecked(1);
            }
        } else if (id == R.id.home_psychology_layout_more) { //??????????????????
            startActivity(new Intent(getActivity(), AllEvaluationActivity.class));
        } else if (id == R.id.btn_public_welfare) { //????????????
            //????????????????????????
            //??????WEB???????????????????????????
            ProtocolActivity.startActivity(getActivity(), "http://www.huizhou.gov.cn/zjhz/qyxx/content/post_220639.html", "????????????");
//                ProtocolActivity.startActivity(getActivity(), "http://www.huiyang.gov.cn/hygk/hyjj/index.html", "????????????");


//                ProtocolActivity.startActivity(getActivity(),Constant.Url.request_base_url + Constant.WebUrl.about_us, "????????????");
        } else if (id == R.id.btn_tree_hole) { //????????????

            //????????????????????????
            //???????????????????????????????????????????????????
            Intent intent = new Intent(getContext(), PolicyAssemblyActivity.class);
            startActivity(intent);

//                ProtocolActivity.startActivity(getActivity(), Constant.Url.request_base_url + Constant.WebUrl.about_us, "????????????");
        } else if (id == R.id.btn_curriculum) { //????????????
            if (getActivity() instanceof setChecked) {
                if (accountService != null && accountService.getGetAccountInfo() != null && !accountService.getGetAccountInfo().isLogin) {
                    ARouter.getInstance()
                            .build(ARouterConstant.Account.AccountLoginActivity)
                            .navigation(getActivity());
                } else {
                    ((setChecked) getActivity()).onSetChecked(2);
                }
            }
        } else if (id == R.id.btn_punch_clock) { //??????
            if (accountService != null && accountService.getGetAccountInfo() != null && !accountService.getGetAccountInfo().isLogin) {
                ARouter.getInstance()
                        .build(ARouterConstant.Account.AccountLoginActivity)
                        .navigation(getActivity());
            } else {
                EveryDaySignInActivity.startActivity(getActivity());
            }
        }
    }

    //    @OnClick({R2.id.rl_evaluation_search, R2.id.iv_message,
//            R2.id.home_look_all, R2.id.home_course_layout_more,
//            R2.id.home_article_layout_more, R2.id.home_psychology_layout_more,
//            R2.id.btn_public_welfare, R2.id.btn_tree_hole,
//            R2.id.btn_curriculum, R2.id.btn_punch_clock})
//    public void onViewClicked(View view) {
//
//
//        int viewId = view.getId();
//        switch (viewId) {
//        }
//    }

    private void checkPermissions() {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions
                .request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .subscribe(granted -> {
                    if (granted) {
                        if (shilianwangSDKinit) {

                            requestMeetingRoomList(true);

                        } else {
                            //??????????????????????????????????????????
                            ToastUtils.showLong("???????????????????????????");
                        }
                    } else {
                        // Oups permission denied
                        Toast.makeText(getActivity(), "??????????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                });

    }

//    private void CallShilianwang() {
//        if (meetingRoomList.size() == 0) {
//            ToastUtils.showShort("????????????????????????");
//            return;
//        }
//        if (devicePosition < meetingRoomList.size()) {
//            tempSelectDeviceId = autoGenericCode(meetingRoomList.get(devicePosition).getDeviceCode(), 5);
//        }
//
//
//        Log.e(TAG, "onSuccess: " + tempSelectDeviceId);
//        VVClient.getInstance().makeVideoCall(VVClient.CALL_MODE.CALL_SLW, tempSelectDeviceId, "0", tempSelectDeviceId, tempSelectDeviceId, new VVClient.Callback<SurfaceViewRenderer[]>() {
//            @Override
//            public void onSuccess(SurfaceViewRenderer[] data) {
//                Log.e(TAG, "onSuccess: ????????????");
//
//                if (basePopupView != null) {
//                    basePopupView.dismiss();
//                }
//                if (data == null) {
//
//                } else {
//                    if (data.length < 2) return;
//                    Intent intent = new Intent(getActivity(), VideoActivity.class);
//                    EventBus.getDefault().postSticky(new SurfaceViewBus(data, new CovertBean.ItemsBean(), 6, null, tempSelectDeviceId));
//                    startActivity(intent);
//
//                    devicePosition = 0;
//                }
//            }
//
//            @Override
//            public void onFailed(int errCode, String errMsg) {
//
//                Log.e(TAG, "onFailed: " + errMsg);
//                devicePosition++;
//                if (devicePosition < meetingRoomList.size()) {
//                    CallShilianwang();
//                } else {
//
//                    if (basePopupView != null) {
//                        basePopupView.dismiss();
//                    }
//                    ToastUtils.showLong(errMsg);
//                    devicePosition = 0;
//                }
//            }
//        });
//
//    }

    /**
     * ???????????????????????????0?????????num??????????????????
     */
    private String autoGenericCode(String code, int num) {
        try {
            String result = "";
            // ??????num?????????
            // 0 ??????????????????0
            // num ???????????????4
            // d ????????????????????????
            result = String.format("%0" + num + "d", Integer.parseInt(code));
            return result;
        } catch (Exception e) {
            Logger.e("???????????????????????????,????????????00000");
            return "00000";
        }
    }

    public void endCall() {
        VVClient.getInstance().cancelVideoCall(VVClient.CALL_MODE.CALL_SLW, tempSelectDeviceId, "", tempSelectDeviceId, "",
                new VVClient.Callback<String>() {
                    @Override
                    public void onSuccess(String data) {

                        if (basePopupView != null) {
                            basePopupView.dismiss();
                        }
                    }

                    @Override
                    public void onFailed(int errCode, String errMsg) {

                        if (basePopupView != null) {
                            basePopupView.dismiss();
                        }
                        ToastUtils.showLong("????????????");
                    }
                });
    }


    private void initShilianwangSDKMethod() {

        initShilianwangServer();

        initShilianwangKeySecret();
    }

    //Banner ????????????
    class ImageLoader implements XPopupImageLoader {
        @Override
        public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
            //????????????Target.SIZE_ORIGINAL??????????????????????????????????????????????????????????????????
            Glide.with(imageView).load(url).apply(new RequestOptions().placeholder(R.mipmap.logo).override(Target.SIZE_ORIGINAL)).into(imageView);
        }

        @Override
        public File getImageFile(@NonNull Context context, @NonNull Object uri) {
            try {
                return Glide.with(context).downloadOnly().load(uri).submit().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * ??????????????????sdk
     */

    private void initShilianwangServer() {

        //?????????????????????????????????url???????????????
        VVClient.getInstance().setServer(Constant.ShilianwangSDK.serverUrl);
    }

    private void initShilianwangKeySecret() {
        VVClient.getInstance().fetchStreamServerList(Constant.ShilianwangSDK.app_key,
                Constant.ShilianwangSDK.app_secret,
                new VVClient.Callback<List<ListItemBean>>() {
                    @Override
                    public void onSuccess(final List<ListItemBean> listItemBeans) {

//                        Toast.makeText(getActivity(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < listItemBeans.size(); i++) {
                            Log.e(TAG, "onSuccess: " + new Gson().toJson(listItemBeans.get(i)));
                        }
                        if (listItemBeans.size() > 0) {
                            initRequestStreamServer(listItemBeans.get(0));
                        }

                    }

                    @Override
                    public void onFailed(int i, String s) {
//                        Toast.makeText(getActivity(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailed: " + s);
                    }
                });
    }

    /**
     * ???????????????????????????
     */
    private void initRequestStreamServer(ListItemBean listItemBean) {
        Log.e(TAG, "initRequestStreamServer: " + listItemBean.id + ":" + accountService.getGetAccountInfo().userId + ":" + accountService.getGetAccountInfo().userName + ":" + accountService.getGetAccountInfo().phoneNumber);
        if (listItemBean != null) {
            VVClient.getInstance().initRequest(listItemBean.id,
                    accountService.getGetAccountInfo().phoneNumber,
                    accountService.getGetAccountInfo().userName,
                    "",
                    "1",
                    new VVClient.Callback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            shilianwangSDKinit = true;
                            checkPermissions();
//                            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onSuccess: " + s);
                        }

                        @Override
                        public void onFailed(int i, String s) {
                            shilianwangSDKinit = false;
//
//                            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onFailed===========: " + s);
                        }
                    });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onLoginEventBus(LoginEventBus busBean) {
        //???????????????????????????
        if (busBean != null) {
            if (busBean.getAccountBean() != null) {
                viewBinding.fragmentHomeSmartrefresh.autoRefresh();
            }
        }
    }

    public interface setChecked {
        void onSetChecked(int position);
    }

}
