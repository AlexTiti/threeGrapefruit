package com.findtech.threePomelos.travel.present;

import android.content.Context;

import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.weather.WeatherBean;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.travel.bean.TravelBean;
import com.findtech.threePomelos.travel.fragment.TravelFragment;
import com.findtech.threePomelos.travel.model.TravelFragmentModel;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/30
 */

public class TravelFragmentPresent extends BasePresenterMvp<TravelFragment, TravelFragmentModel> {

    @Override
    public TravelFragmentModel createModel() {
        return new TravelFragmentModel();
    }

    public void getTravelInfo() {
        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getTravelInfo()
                .compose(RxHelper.<TravelBean>rxSchedulerHelper())
                .subscribe(new Consumer<TravelBean>() {
                    @Override
                    public void accept(TravelBean travelBean) throws Exception {
                        mView.loadSuccess(travelBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadFailed(throwable.toString());
                    }
                }));
    }

    public void getWeather(Context context, String location) {
        if (model == null || mView == null) {
            return;
        }

        if (!NetUtils.isConnectInternet(context)) {
            return;
        }

        mRxManager.register(model.getWeather(context, location)
                .compose(RxHelper.<WeatherBean>rxSchedulerHelper()).subscribe(
                        new Consumer<WeatherBean>() {
                            @Override
                            public void accept(WeatherBean weatherBean) throws Exception {
                                mView.getWeatherSuccess(weatherBean);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        }
                ));


    }

    public void sendWeather(String weather, String air) {
        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.sendWeather(weather, air)
                .compose(RxHelper.rxSchedulerHelper())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.sendWeatherSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.sendWeatherFailed();
                    }
                }));
    }
}
