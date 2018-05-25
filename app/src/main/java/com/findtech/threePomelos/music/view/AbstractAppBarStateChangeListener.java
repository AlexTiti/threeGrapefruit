package com.findtech.threePomelos.music.view;

import android.support.design.widget.AppBarLayout;
import android.util.Log;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/13
 */

public abstract class AbstractAppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener{
    public enum State {
        /**
         * 展开
         */
        EXPANDED,
        /**
         * 关闭
         */
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        float f = Math.abs(i)/(appBarLayout.getTotalScrollRange()*1.0f);
        onScale(f);
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);

            }
            mCurrentState = State.IDLE;
        }
    }

    /**
     * 状态改变
     * @param appBarLayout
     * @param state
     */
    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);

    /**
     * 缩放
     * @param f
     */
    public abstract void onScale(float f);

}
