package com.findtech.threePomelos.travel.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/24
 */

public class FrequencyData implements Parcelable {

    private String data;
    private Float frequency;

    @Override
    public int describeContents() {
        return 0;
    }

    public String getData() {
        return data;
    }

    public Float getFrequency() {
        return frequency;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.data);
        dest.writeValue(this.frequency);
    }

    public FrequencyData(String data, float frequency) {
        this.data = data;
        this.frequency = frequency;
    }

    protected FrequencyData(Parcel in) {
        this.data = in.readString();
        this.frequency = (Float) in.readValue(Float.class.getClassLoader());
    }

    public static final Parcelable.Creator<FrequencyData> CREATOR = new Parcelable.Creator<FrequencyData>() {
        @Override
        public FrequencyData createFromParcel(Parcel source) {
            return new FrequencyData(source);
        }

        @Override
        public FrequencyData[] newArray(int size) {
            return new FrequencyData[size];
        }
    };
}
