package com.findtech.threePomelos.music.view;

import com.findtech.threePomelos.music.info.MusicInfo;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   author   :   Administrator
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/15
 *   desc     :
 *   version  :   V 1.0.5
 * @author Administrator
 */
public interface MusicViewIn<T> {

    /**
     *获取数据成功
     * @param musicInfos
     */
    void  successful(ArrayList<T> musicInfos);

    /**
     *获取数据失败
     */
    void  onError();


}
