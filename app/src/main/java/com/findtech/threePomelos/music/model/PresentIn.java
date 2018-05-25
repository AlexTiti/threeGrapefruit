package com.findtech.threePomelos.music.model;

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
public interface PresentIn<T> {

    /**
     * 设置数据
     * @param arrayList
     */
    void setData(ArrayList<T> arrayList);

    /**
     * 异常回调
     */
    void onError();

}
