package com.visionvera.psychologist.c.module.counselling.contract;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.visionvera.library.base.bean.BaseResponseBean2;
import com.visionvera.library.base.mvp.view.IBaseRetrofitView;
import com.visionvera.library.base.mvp.view.IBaseView;
import com.visionvera.psychologist.c.R;
import com.visionvera.psychologist.c.module.counselling.bean.ConfirmOrderDoneRequestBean;
import com.visionvera.psychologist.c.module.counselling.bean.ConsulantListResponseBean;
import com.visionvera.psychologist.c.module.counselling.bean.CounselorDetailBean;
import com.visionvera.psychologist.c.module.counselling.bean.ExpertiesResponseBean;
import com.visionvera.psychologist.c.module.counselling.bean.NewOrderConsultRequest;
import com.visionvera.psychologist.c.module.counselling.bean.NewOrderconsultBean;
import com.visionvera.psychologist.c.module.counselling.bean.OrderCancelRequest;
import com.visionvera.psychologist.c.module.counselling.bean.OrderConsultCancelRequest;
import com.visionvera.psychologist.c.module.counselling.bean.OrderConsultDetailBean;
import com.visionvera.psychologist.c.module.counselling.bean.OrderConsultDetailRequest;
import com.visionvera.psychologist.c.module.counselling.bean.OrderConsultListBean;
import com.visionvera.psychologist.c.module.counselling.bean.OrderConsultListRequest;
import com.visionvera.psychologist.c.module.counselling.bean.QueryPaysStatusRequestBean;
import com.visionvera.psychologist.c.module.counselling.bean.QueryPaysStatusResponseBean;
import com.visionvera.psychologist.c.module.counselling.bean.ReportInformationBeanRequest;
import com.visionvera.psychologist.c.module.counselling.bean.SaveMeetingRecordRequest;
import com.visionvera.psychologist.c.module.counselling.bean.SuggestListBean;
import com.visionvera.psychologist.c.module.counselling.bean.TimeCalendarBean;
import com.visionvera.psychologist.c.module.counselling.bean.TimeCalendarRequest;
import com.visionvera.psychologist.c.module.counselling.bean.UserUpdateAgainConsultInfoRequest;
import com.visionvera.psychologist.c.module.ordertreatment.bean.AddEvaluateRequest;
import com.visionvera.psychologist.c.module.ordertreatment.bean.DoctorDetailBean;
import com.visionvera.psychologist.c.module.ordertreatment.bean.DoctorDetailRequest;
import com.visionvera.psychologist.c.module.ordertreatment.bean.EvaluateDetailBean;
import com.visionvera.psychologist.c.module.ordertreatment.bean.EvaluateListBean;
import com.visionvera.psychologist.c.module.ordertreatment.bean.EvaluateListRequest;
import com.visionvera.psychologist.c.module.usercenter.bean.InforMationBean;
import com.visionvera.psychologist.c.utils.cos.SavePathRequestBean;
import com.visionvera.psychologist.c.utils.cos.SavePathResponseBean;

import okhttp3.RequestBody;

/**
 * author:lilongfeng
 * date:2019/12/25
 * ??????:????????????????????????contract
 */

public interface OrderConsultContract {

    /**
     * ????????????????????????
     */
    interface TimeCalendar {
        interface TimeCalendarView extends IBaseRetrofitView {
            void onTimeCalendar(TimeCalendarBean bean, ResultType resultType, String errorMsg);
        }

        interface TimeCalendarBasePersenter {
            void getTimeCalendar(TimeCalendarRequest request, LifecycleProvider lifecycleProvider);
        }
    }


    /**
     * ???????????? ????????????-????????????????????????
     */
    interface NewOrderConsult {
        interface NewOrderConsultView extends IBaseRetrofitView {

        }

        interface NewOrderConsultBasePresenter {

        }
    }

    /**
     * ???????????????????????????
     */
    interface OrderPayActivity {
        interface NewOrderConsultView extends IBaseRetrofitView {
            void onNewOrderConsult(NewOrderconsultBean response, ResultType resultType, String errorMsg);

            void onGetInforMation(InforMationBean inforMationBean, ResultType resultType, String errorMsg);

            void onSavePath(SavePathResponseBean responseBean, ResultType resultType, String errorMsg, int position);

        }

        interface NewOrderConsultBasePresenter {
            void getNewOrderConsult(NewOrderConsultRequest request, LifecycleProvider lifecycleProvider);

            void getInforMation(LifecycleProvider lifecycleProvider);

            void savePath(SavePathRequestBean requestBean, LifecycleProvider lifecycleProvider, int position);

        }
    }


    /**
     * ?????? ????????????-???????????????????????????
     */
    interface OrderConsultList {
        interface OrderConsultListView extends IBaseRetrofitView {
            void onOrderConsultList(boolean isRefresh, OrderConsultListBean response, ResultType resultType, String errorMsg);
        }

        interface OrderConsultListBasePresenter {
            void getOrderConsultList(boolean isRefresh, OrderConsultListRequest request, LifecycleProvider lifecycleProvider);
        }
    }

    /**
     * ?????? ?????????????????????-???????????????
     */
    interface OrderConsultSuccessActivity {
        interface View extends IBaseRetrofitView {
            void onQueryPayStatus(QueryPaysStatusResponseBean response, ResultType resultType, String errorMsg);
        }

        interface Presenter {
            void queryPayStatus(QueryPaysStatusRequestBean request, LifecycleProvider lifecycleProvider);
        }
    }


    //???????????????????????????
    //OrderConsultDetailActivity
    interface OrderConsultDetail {

        interface OrderConsultDetailView extends IBaseRetrofitView {


            //?????????
            void onOrderDoctorDetail(CounselorDetailBean bean, ResultType resultType, String errorMsg);

            //??????
            void onDoctorDetail(DoctorDetailBean detailBean, ResultType resultType, String errorMsg);


            //?????????
            //???????????????????????????????????????????????????
            void onOrderConsultDetail(OrderConsultDetailBean bean, ResultType resultType, String errorMsg);

            //?????????
            //??????/????????????????????????
            void onReportInformation(BaseResponseBean2 response, ResultType resultType, String errorMsg,String state);

            //?????????
            //????????????/????????????
            void onOrderCancel(BaseResponseBean2 response, ResultType resultType, String errorMsg);

            //?????????
            //????????????/????????????
            void onOrderReject(BaseResponseBean2 response, ResultType resultType, String errorMsg);

            //?????????
            //????????????/????????????
            void onOrderAccept(BaseResponseBean2 response, ResultType resultType, String errorMsg);
        }

        interface OrderConsultDetailBasePresenter {
            //?????????
            void onOrderDoctorDetail(int id, LifecycleProvider lifecycleProvider);

            //??????
            void onDoctorDetailByUserId(DoctorDetailRequest request, LifecycleProvider lifecycleProvider);

            //?????????
            //???????????????????????????????????????????????????
            void getOrderConsultDetail(OrderConsultDetailRequest request, LifecycleProvider lifecycleProvider);

            //?????????
            //??????/?????? ??????????????????
            void reportInformation(ReportInformationBeanRequest request, LifecycleProvider lifecycleProvider,String state);

            //?????????
            //????????????/????????????
            void getOrderCancel(OrderCancelRequest request, LifecycleProvider lifecycleProvider);


            //?????????
            //????????????/????????????
            void getOrderReject(OrderCancelRequest request, LifecycleProvider lifecycleProvider);

            //?????????
            //????????????/????????????
            void getOrderAccept(OrderCancelRequest request, LifecycleProvider lifecycleProvider);

        }
    }

    /**
     * ????????????(?????????)
     */
    interface FindConsulantancy {
        interface View extends IBaseRetrofitView {
            void onGetExpertiesList(ExpertiesResponseBean responseBean, ResultType resultType, String errorMsg);

            void onGetConsulantList(ConsulantListResponseBean responseBean, ResultType resultType, String errorMsg);

            void onSearchConsulantList(ConsulantListResponseBean responseBean, ResultType resultType, String errorMsg);

        }

        interface Presenter {
            void getExpertiesList(LifecycleProvider lifecycleProvider);

            void getConsulantList(RequestBody requestBean, LifecycleProvider lifecycleProvider);

            void getSearchConsulantList(RequestBody requestBean, LifecycleProvider lifecycleProvider);
        }
    }

    /**
     * ???????????????
     */
    interface Suggest {
        interface View extends IBaseRetrofitView {
            void onGetSuggestList(SuggestListBean responseBean, ResultType resultType, String errorMsg);
        }

        interface Presenter {
            void getSuggestList(LifecycleProvider lifecycleProvider);
        }
    }

    /**
     * ??????????????????????????????
     */
    interface CounselorDetail {
        interface CounselorDetailView extends IBaseRetrofitView {
            void onCounselorDetailByPsyInfoId(CounselorDetailBean bean, ResultType resultType, String errorMsg);

            void onCounselorDetailByUserId(CounselorDetailBean bean, ResultType resultType, String errorMsg);

            //??????List
            void onEvaluateList(EvaluateListBean bean, ResultType resultType, String errorMsg);

            //add??????
            void onAddEvaluate(BaseResponseBean2 bean, ResultType resultType, String errorMsg);

            //????????????
            void onEvaluateDetail (EvaluateDetailBean bean, ResultType resultType, String errorMsg);


        }

        interface CounselorDetailBasePresenter {
            void getCounselorDetailByPsyInfoId(int id, LifecycleProvider lifecycleProvider);

            void getCounselorDetailByUserId(int id, LifecycleProvider lifecycleProvider);

            void getUserEvaluateList(EvaluateListRequest request, LifecycleProvider lifecycleProvider);

            void getAddEvaluate(AddEvaluateRequest request, LifecycleProvider lifecycleProvider);

            void getEvaluateDetail(EvaluateListRequest request, LifecycleProvider lifecycleProvider);
        }
    }


}
