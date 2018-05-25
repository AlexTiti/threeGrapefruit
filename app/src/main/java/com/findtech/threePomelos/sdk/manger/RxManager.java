package com.findtech.threePomelos.sdk.manger;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 *
 * @author Alex
 * @date 2017/9/12
 * <p>
 * 用于管理Rxjava 注册订阅和取消订阅
 */

public class RxManager {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public void register(Disposable d) {
        mCompositeDisposable.add(d);
    }

    public void unSubscribe() {
        mCompositeDisposable.dispose();// 取消订阅
    }
}