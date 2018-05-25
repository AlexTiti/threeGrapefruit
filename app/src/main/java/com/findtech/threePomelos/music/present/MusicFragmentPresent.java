package com.findtech.threePomelos.music.present;

import com.findtech.threePomelos.music.fragment.MusicNewFragment;
import com.findtech.threePomelos.music.info.MusicBean;
import com.findtech.threePomelos.music.model.MusicFragmentModelImpl;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/09
 */

public class MusicFragmentPresent extends BasePresenterMvp<MusicNewFragment,MusicFragmentModelImpl> {
    @Override
    public MusicFragmentModelImpl createModel() {
        return new MusicFragmentModelImpl();
    }

    public void getRecyclerData(){
        if (model == null && mView == null){
            return;
        }
        mRxManager.register(model.getRecyclerViewData()
        .compose(RxHelper.<ArrayList<MusicBean>>rxSchedulerHelper())
        .subscribe(new Consumer<ArrayList<MusicBean>>() {
            @Override
            public void accept(ArrayList<MusicBean> musicBeans) throws Exception {
                mView.loadSuccess(musicBeans);

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.loadFailed(throwable.toString());
            }
        }));
    }
}
