package com.visionvera.psychologist.c.module.ordertreatment.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lxj.xpopup.XPopup;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.visionvera.library.base.BaseMVPLoadActivity;
import com.visionvera.library.base.Constant;
import com.visionvera.library.util.OneClickUtils;
import com.visionvera.psychologist.c.R;
import com.visionvera.psychologist.c.R2;
import com.visionvera.psychologist.c.module.counselling.bean.ExpertiesResponseBean;
import com.visionvera.psychologist.c.module.counselling.view.EvaluationTypePopup;
import com.visionvera.psychologist.c.module.ordertreatment.adapter.RecommendConsultantListAdapter;
import com.visionvera.psychologist.c.module.ordertreatment.bean.DoctorListRequestBean;
import com.visionvera.psychologist.c.module.ordertreatment.bean.DoctorListResponseBean;
import com.visionvera.psychologist.c.module.ordertreatment.bean.TagListRequestBean;
import com.visionvera.psychologist.c.module.ordertreatment.bean.TagListResponseBean;
import com.visionvera.psychologist.c.module.ordertreatment.contract.IContract;
import com.visionvera.psychologist.c.module.ordertreatment.presenter.TypeRecommendActivityPresenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author: ?????????
 * @date: 2020-01-02 16:14
 * QQ:1052374416
 * ??????:????????????-?????????????????????????????????
 * ????????????:
 */

public class TypeRecommendActivity extends BaseMVPLoadActivity<IContract.TypeRecommendActivity.View, TypeRecommendActivityPresenter> {

    @BindView(R2.id.tv_search_match)
    TextView tv_search_match;
    @BindView(R2.id.rc_consultant)
    RecyclerView consultantRecyclerView;



    ImageView ivArrow;
    RelativeLayout rlTitle;
    TextView tvTitle;
    SmartRefreshLayout smartRefreshLayout;

    private RecommendConsultantListAdapter consultantListAdapter;
    private EvaluationTypePopup evaluationTypePopup;
    private List<ExpertiesResponseBean.ResultBean> tagList = new ArrayList<>();
    int selectPosition = 0;
    List<DoctorListResponseBean.ResultBean> dataList = new ArrayList<>();
    private int page = 0;//?????????.  ?????????1?????????.??????????????????????????????0,???????????????????????????
    int pageSize = 20;
    private RelativeLayout rl_titleAndArrow;
    private ImageView iv_back;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TypeRecommendActivity.class));
    }

    //????????????????????????????????????null????????????????????????????????????
    public IntentBean intentBean = new IntentBean();

    /**
     * ???????????????????????????
     *
     * @param context
     * @param intentBean
     */
    public static void startActivity(Context context, IntentBean intentBean) {
        Intent intent = new Intent(context, TypeRecommendActivity.class);
        intent.putExtra(Constant.IntentKey.IntentBean, intentBean);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.evaluation_module_activity_order_treatment_type_recommend;
    }

    @Override
    protected void doYourself() {
        ivArrow = (ImageView) findViewById(R.id.iv_arrow);
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        smartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.normal_view);
        rl_titleAndArrow = (RelativeLayout) findViewById(R.id.rl_titleAndArrow);
        rl_titleAndArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypePopup();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        parseIntent();
        //????????????taglist
        ExpertiesResponseBean.ResultBean popBean = new ExpertiesResponseBean.ResultBean();
        popBean.setLableName(intentBean.subjectName);
        popBean.setId(Integer.parseInt(intentBean.subjectId));
        popBean.setSelect(true);
        tagList.add(popBean);
        selectPosition = 0;
        tvTitle.setText(intentBean.subjectName);
        initAdapter();
        showLoading();
        http_getTagList();
        http_getList(true);

    }

    private void parseIntent() {
        //????????????
        IntentBean bean = (IntentBean) getIntent().getSerializableExtra(Constant.IntentKey.IntentBean);
        if (bean != null) {
            intentBean = bean;
        }
        Logger.i(intentBean.toString());
    }

    private void initAdapter() {
        smartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        smartRefreshLayout.setEnableOverScrollDrag(true);//?????????????????????????????????????????????1.0.4
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setEnableRefresh(true);
        consultantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        consultantListAdapter = new RecommendConsultantListAdapter(this, dataList);
        consultantRecyclerView.setAdapter(consultantListAdapter);
        consultantListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                NewOrderTreatmentDetailActivity.IntentBean intentBean
                        = new NewOrderTreatmentDetailActivity.IntentBean(dataList.get(position).getId(), dataList.get(position).getUserId());

                NewOrderTreatmentDetailActivity.startActivity(activity, intentBean);
            }
        });
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                http_getList(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                http_getList(true);
            }
        });
    }


    private void showTypePopup() {
        if (evaluationTypePopup != null && evaluationTypePopup.isShow()) {
            return;
        }
        evaluationTypePopup = (EvaluationTypePopup) new XPopup.Builder(this)
                .atView(rlTitle)
                .asCustom(new EvaluationTypePopup(this, tagList, new EvaluationTypePopup.OnListPopupClick() {
                    @Override
                    public void onPopupClick(String id, String name) {
                        tvTitle.setText(name);
                        for (int i = 0; i < tagList.size(); i++) {
                            if ((tagList.get(i).getId() + "").equals(id)) {
                                selectPosition = i;
                                for (int i1 = 0; i1 < tagList.size(); i1++) {
                                    ExpertiesResponseBean.ResultBean ivo = tagList.get(i1);
                                    if (selectPosition == i1) {
                                        ivo.setSelect(true);
                                    } else {
                                        ivo.setSelect(false);
                                    }
                                }
                                break;
                            }
                        }
                        http_getList(true);
                    }
                }));
        evaluationTypePopup.show();
    }

    public void http_getList(boolean isRefresh) {

        int requestPage = page;
        if (isRefresh) {
            //????????????
            requestPage = 1;
        } else {
            //??????????????????
            requestPage = page + 1;
        }

        DoctorListRequestBean requestBean = new DoctorListRequestBean();
        requestBean.lablesId = tagList.get(selectPosition).getId();
        requestBean.pageSize = pageSize;
        requestBean.pageIndex = requestPage;
        mPresenter.getDoctorListFromHospital(isRefresh, requestBean, this);
    }

    public void http_getTagList() {
        TagListRequestBean tagListRequestBean = new TagListRequestBean();
        mPresenter.getTagList(tagListRequestBean, this);
    }


    @Override
    protected void initMVP() {
        mView = new IContract.TypeRecommendActivity.View() {
            @Override
            public void onGetDoctorListFromHospital(boolean isRefresh, DoctorListResponseBean responseBean, ResultType resultType, String errorMsg) {
                if (smartRefreshLayout != null) {
                    smartRefreshLayout.finishRefresh();
                    smartRefreshLayout.finishLoadMore();
                }
                switch (resultType) {
                    case NET_ERROR:
                        //??????????????????????????????????????????????????????????????????
                        if (page == 0 && dataList.size() == 0) {
                            showError(errorMsg);
                        }
                        ToastUtils.showLong(getString(R.string.base_module_net_error));
                    case SERVER_ERROR:
                        //?????????????????????????????????code?????????

                        if (isRefresh) {
                            dataList.clear();
                            page = 0;
                            showEmpty();
                        } else {
                            if (page == 0 && dataList.size() == 0) {
                                showEmpty();
                            } else {
                                showNormal();
                            }
                        }
                        ToastUtils.showShort(errorMsg);
                        break;
                    case SERVER_NORMAL_DATANO:
                        //?????????result???null
                        if (isRefresh) {
                            dataList.clear();
                            page = 0;
                        }
                        consultantListAdapter.notifyDataSetChanged();
                        if (page == 0 && dataList.size() == 0) {
                            showEmpty();
                        } else {
                            showNormal();
                        }
                        break;
                    case SERVER_NORMAL_DATAYES:
                        //??????????????????????????????code?????????result??????null?????????result???????????????????????????????????????

                        if (isRefresh) {
                            dataList.clear();
                            page = 0;
                        }
                        if (responseBean.getResult().size() != 0) {
                            page++;
                            dataList.addAll(responseBean.getResult());
                        }
                        consultantListAdapter.notifyDataSetChanged();
                        if (page == 0 && dataList.size() == 0) {
                            showEmpty();
                        } else {
                            showNormal();
                        }

                        SpannableStringBuilder spannableString = getSpannableString("??????????????????" + tagList.get(selectPosition).getLableName() + "??????????????????" + dataList.size() + "???"
                                , tagList.get(selectPosition).getLableName());

                        tv_search_match.setText(spannableString);
                        break;
                }
            }

            @Override
            public void onGetTagList(TagListResponseBean responseBean, ResultType resultType, String errorMsg) {
                switch (resultType) {
                    case NET_ERROR:
                        //??????????????????????????????????????????????????????????????????
                        ToastUtils.showLong(getString(R.string.base_module_net_error));
                        break;
                    case SERVER_ERROR:
                        //?????????????????????????????????code?????????
                    case SERVER_NORMAL_DATANO:
                        //?????????result???null
                        ToastUtils.showShort(errorMsg);
                        break;
                    case SERVER_NORMAL_DATAYES:
                        //??????????????????????????????code?????????result??????null?????????result???????????????????????????????????????
                        tagList.clear();

                        for (TagListResponseBean.ResultBean resultBean : responseBean.getResult()) {
                            ExpertiesResponseBean.ResultBean popBean = new ExpertiesResponseBean.ResultBean();
                            popBean.setLableName(resultBean.getLableName());
                            popBean.setId(resultBean.getId());
                            tagList.add(popBean);
                        }
                        for (int i = 0; i < tagList.size(); i++) {
                            if ((tagList.get(i).getId() + "").equals(intentBean.subjectId)) {
                                selectPosition = i;
                                tagList.get(i).setSelect(true);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onComplete() {

            }
        };
        mPresenter = new TypeRecommendActivityPresenter(this, mView);
    }

    @Override
    protected void onReload() {
        showLoading();
        http_getList(true);
    }

    private SpannableStringBuilder getSpannableString(String wholeStr, String matchStr) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(wholeStr);
        //????????????
        Pattern p = Pattern.compile(matchStr);
        //????????????
        Matcher m = p.matcher(spannableString);
        while (m.find()) {
            //???????????????
            int start = m.start();
            int end = m.end();
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#333333"));
            spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(ConvertUtils.sp2px(15));
            spannableString.setSpan(absoluteSizeSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return spannableString;
    }

    public static class IntentBean implements Serializable {
        /**
         * tag id
         */
        private String subjectId = "";
        /**
         * tag??????
         */
        private String subjectName = "";
        /**
         * ????????????
         */
        private int selectPosition;


        public IntentBean() {
        }

        public IntentBean(String subjectId, String subjectName, int selectPosition) {
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.selectPosition = selectPosition;
        }
    }

}
