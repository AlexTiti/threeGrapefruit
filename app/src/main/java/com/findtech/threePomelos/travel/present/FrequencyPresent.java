package com.findtech.threePomelos.travel.present;

import android.content.Context;

import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.travel.bean.FrequencyData;
import com.findtech.threePomelos.travel.fragment.FrequencyFragment;
import com.findtech.threePomelos.travel.iterator.FrequencyStore;
import com.findtech.threePomelos.travel.model.FrequencyModelImpl;

import java.util.ArrayList;
import java.util.HashMap;

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

public class FrequencyPresent extends BasePresenterMvp<FrequencyFragment, FrequencyModelImpl> {
    private Context context;

    public FrequencyPresent(Context context) {
        this.context = context;
    }

    @Override
    public FrequencyModelImpl createModel() {
        return new FrequencyModelImpl(context);
    }

    public void getDataList() {

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getFrequencyWeek().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HashMap<String,Integer>>() {
                    @Override
                    public void accept(HashMap<String,Integer> frequencyStore) throws Exception {
                        mView.loadSuccess(frequencyStore);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadFailed(throwable.toString());
                    }
                }));
    }

    public void getDataListMonth() {

        if (model == null || mView == null) {
            return;
        }
        mRxManager.register(model.getFrequencyMonth().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HashMap<String,Integer>>() {
                    @Override
                    public void accept(HashMap<String,Integer> frequencyStore) throws Exception {
                        mView.getFrequencyMonth(frequencyStore);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.getFrequencyMonthFaild(throwable.toString());
                    }
                }));
    }
}
