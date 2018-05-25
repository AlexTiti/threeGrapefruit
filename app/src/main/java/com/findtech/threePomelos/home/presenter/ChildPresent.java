package com.findtech.threePomelos.home.presenter;


import com.findtech.threePomelos.home.fragment.ChildFragment;
import com.findtech.threePomelos.home.model.ChildMode;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   @author   :   Alex
 *   @e_mail   :   18238818283@sina.cn
 *   @time     :   2018/03/14
 *   @desc     :
 *   @version  :   V 1.0.9
 */

public class ChildPresent extends BasePresenterMvp<ChildFragment,ChildMode> {

    @Override
    public ChildMode createModel() {
        return new ChildMode();
    }

    public void getTabs(){
        if (model== null || mView == null){
            return;
        }
        mView.loadSuccess(model.getTabArray());
    }

}
