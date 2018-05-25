package com.findtech.threePomelos.sdk.base.mvp;


import com.findtech.threePomelos.sdk.manger.RxManager;

/**
 * <pre>
 *
 *   @author   :   Alex
 *   @e_mail   :   18238818283@sina.cn
 *   @time     :   2017/12/12
 *   @desc     :
 *   @version  :   V 1.0.9
 */

public abstract class BasePresenterMvp< V extends Contract.ViewMvp, M extends Contract.ModeMvp>  {

    public V mView;
    public M model;
    protected RxManager mRxManager = new RxManager();

    public void onAttach(V mView) {
        this.mView = mView;
        this.model = createModel();
    }

    public void disAttach() {
        mRxManager.unSubscribe();
        if (mView != null) {
            mView = null;
        }
        if (model != null){
            model = null;
        }
    }

    /**
     * 创建Model
     * @return 返回Model
     */
    public abstract M createModel();

}
