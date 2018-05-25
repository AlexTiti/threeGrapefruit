package com.findtech.threePomelos.music.view;

import com.findtech.threePomelos.sdk.base.mvp.Contract;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/08
 * 音乐界面回调接口
 */

public interface RecyclerMusicView<T> extends Contract.ViewMvp<T> {

    /**
     * 加载更多成功回调
     * @param t 数据类型
     */
   void loadMoreSuccess(T t);

    /**
     * 加载更多失败回调
     * @param message
     */
   void loadMoreFailed(String message);

    /**
     * 空数据
     */
   void emptyDate();

    /**
     * 没有更多数据
     */
   void showNoMoreData();


}
