package com.findtech.threePomelos.travel.iterator;

import java.util.ArrayList;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/31
 */

public class FrequencyStore<T> implements Store {

    private ArrayList<T> frequencyData;

    public FrequencyStore() {
        frequencyData = new ArrayList<>();
    }

    protected T getFrequencyData(int index) {
        return frequencyData.get(index);
    }

    public void addFrequencyData(T data) {
        frequencyData.add(data);
    }

    protected int getDataLength(){
        return frequencyData == null ? 0 : frequencyData.size();
    }

    protected T getLastData(){
        return frequencyData == null ? null : frequencyData.get(getDataLength() - 1);
    }

    @Override
    public Iterator<T> iterator() {
        return new FrequencyIterator<T>(this);
    }
}
