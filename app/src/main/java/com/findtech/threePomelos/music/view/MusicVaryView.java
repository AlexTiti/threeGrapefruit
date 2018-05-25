package com.findtech.threePomelos.music.view;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/11
 */

public interface MusicVaryView<T> {
    /**
     * 获取标签失败
     * @param s
     */
    void loadTabFailed(String s);

    /**
     * 获取标签成功
     * @param t
     */
    void loadTabSuccess(T t);
}
