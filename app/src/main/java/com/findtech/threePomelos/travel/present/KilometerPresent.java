package com.findtech.threePomelos.travel.present;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.travel.bean.KilometerBean;
import com.findtech.threePomelos.travel.fragment.KilometerFragment;
import com.findtech.threePomelos.travel.model.KilometerModelImpl;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author :   Alex
 * @version :   V 1.0.9
 * @e_mail :   18238818283@sina.cn
 * @time :   2018/03/20
 * @desc :
 */

public class KilometerPresent extends BasePresenterMvp<KilometerFragment, KilometerModelImpl> {
    private Context context;

    public KilometerPresent(Context context) {
        this.context = context;
    }

    @Override
    public KilometerModelImpl createModel() {
        return new KilometerModelImpl(context);
    }

    public void getDataList() {

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getKiloMeterList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayMap<String, Float>>() {
                    @Override
                    public void accept(ArrayMap<String, Float> kilometerData) throws Exception {
                        mView.loadSuccess(kilometerData);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadFailed(throwable.toString());
                    }
                }));
    }

    /**
     * 获取当天的出行信息
     *
     * @param date 请求的日期
     */
    public void getInfoWithDate(Date date) {

        mRxManager.register(model.getTravelBeans(date)
                .compose(RxHelper.<ArrayList<KilometerBean>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<KilometerBean>>() {
                    @Override
                    public void accept(ArrayList<KilometerBean> travelBeans) throws Exception {
                        mView.getDaySuccess(travelBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadFailed(throwable.toString());

                    }
                }));

    }

    /**
     * 获取当天的出行分析信息
     *
     * @param date 请求的日期
     */
    public void getAnalysis(Date date) {

        mRxManager.register(model.getTravelAnalysis(date)
                .compose(RxHelper.<String>rxSchedulerHelper())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String string) throws Exception {
                        mView.getDayAnalysis(string);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.getDayAnalysisFailed(throwable.toString());

                    }
                }));

    }
}
