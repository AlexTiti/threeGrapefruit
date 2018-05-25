package com.findtech.threePomelos.travel.iterator;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/31
 */

public interface Iterator<T> {
    /**
     * 判断是否有下一个
     *
     * @return
     */
    boolean hasNext();

    /**
     * 获取数据并移动下一个
     *
     * @return T
     */
    T next();

    /**
     * 获取数量
     *
     * @return int
     */
    int length();

    /**
     * 根据下标获取数据
     *
     * @param index 下标
     * @return T
     */
    T getIndex(int index);

    /**
     * 获取最后数据
     * @return
     */
    T getLastData();


}
