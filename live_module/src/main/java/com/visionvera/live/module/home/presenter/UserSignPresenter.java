package com.visionvera.live.module.home.presenter;import android.content.Context;import com.trello.rxlifecycle2.LifecycleProvider;import com.visionvera.live.base.bean.ResBean;import com.visionvera.live.module.home.contract.Contract;import com.visionvera.live.module.home.model.UserSignModel;import com.visionvera.live.network.helper.ApiException;import com.visionvera.live.network.helper.HttpRxObserver;import java.util.Map;/** * @Desc IM的presenter实现类 * * @Author yemh * @Date 2019/4/15 17:35 */public class UserSignPresenter implements Contract.IUserSign.IPresenter {    Contract.IUserSign.IModel mModel;    Contract.IUserSign.IView mView;    public UserSignPresenter(Contract.IUserSign.IView mView) {        this.mView = mView;        mModel = new UserSignModel();    }    @Override    public void getUserSign(Context context, Map<String, Integer> params, LifecycleProvider provider) {        mModel.getUserSignResult(params, provider, new HttpRxObserver<ResBean<String>>(context, true) {            @Override            protected void onError(ApiException e) {                mView.showError(e.getMessage());            }            @Override            protected void onSuccess(ResBean<String> data) {                mView.showUserSignResult(data);            }        });    }}