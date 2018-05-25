package com.findtech.threePomelos.travel.model;

import com.findtech.threePomelos.sdk.base.fragment.BaseCompatFragment;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.travel.fragment.FrequencyFragment;
import com.findtech.threePomelos.travel.fragment.KilometerFragment;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/23
 */

public class TravelDetailModel implements Contract.ModeMvp{

    public Observable<ArrayList<BaseCompatFragment>> getFragments(){

        return Observable.create(new ObservableOnSubscribe<ArrayList<BaseCompatFragment>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<BaseCompatFragment>> e) throws Exception {
                ArrayList<BaseCompatFragment> fragmentArrayList = new ArrayList<>();
                fragmentArrayList.add(KilometerFragment.newInstance());
                fragmentArrayList.add(FrequencyFragment.newInstance());
                e.onNext(fragmentArrayList);
            }
        });
    }
}
