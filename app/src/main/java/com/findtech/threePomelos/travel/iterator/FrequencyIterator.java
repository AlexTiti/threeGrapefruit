package com.findtech.threePomelos.travel.iterator;

import com.findtech.threePomelos.travel.bean.FrequencyData;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/31
 */

public class FrequencyIterator<T> implements Iterator<T> {

    private FrequencyStore<T> frequencyStore;
    private int index = 0;

    public FrequencyIterator(FrequencyStore frequencyStore) {
        this.frequencyStore = frequencyStore;
    }

    @Override
    public boolean hasNext() {
        return index < frequencyStore.getDataLength();
    }

    @Override
    public T next() {
        T frequencyData = frequencyStore.getFrequencyData(index);
        index++;
        return frequencyData;
    }

    @Override
    public int length() {
        return frequencyStore.getDataLength();
    }

    @Override
    public T getIndex(int index) {
        return frequencyStore.getFrequencyData(index);
    }

    @Override
    public T getLastData() {
        return frequencyStore.getLastData();
    }
}
