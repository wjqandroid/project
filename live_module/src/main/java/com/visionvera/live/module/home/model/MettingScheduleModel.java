package com.visionvera.live.module.home.model;import com.trello.rxlifecycle2.LifecycleProvider;import com.visionvera.live.base.bean.ResBean;import com.visionvera.live.module.home.bean.MettingScheduleBean;import com.visionvera.live.module.home.contract.Contract;import com.visionvera.live.network.HttpHelper;import java.util.List;import java.util.Map;import io.reactivex.Observer;/** * @Desc 日程安排的model实现类 * * @Author yemh * @Date 2019/4/15 17:42 */public class MettingScheduleModel implements Contract.IMettingSchedule.IModel {    @Override    public void getMettingScheduleResult(Map<String, String> params, LifecycleProvider provider, Observer<ResBean<List<MettingScheduleBean>>> observer) {        HttpHelper.getInstance().getMettingSchedule(params, provider, observer);    }}