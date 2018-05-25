package com.findtech.threePomelos.travel.present;

import com.findtech.threePomelos.sdk.base.fragment.BaseCompatFragment;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.travel.activity.TravelDetailActivity;
import com.findtech.threePomelos.travel.model.TravelDetailModel;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/23
 */

public class TravelDetailPresent extends BasePresenterMvp<TravelDetailActivity,TravelDetailModel> {

    @Override
    public TravelDetailModel createModel() {
        return new TravelDetailModel();
    }

    public void getFragment(){
        if (model == null && mView == null){
            return;
        }

        model.getFragments()
                .compose(RxHelper.<ArrayList<BaseCompatFragment>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<BaseCompatFragment>>() {
                    @Override
                    public void accept(ArrayList<BaseCompatFragment> baseCompatFragments) throws Exception {
                        mView.loadSuccess(baseCompatFragments);
                    }
                });
    }
}
