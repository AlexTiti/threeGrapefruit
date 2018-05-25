package com.findtech.threePomelos.travel.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author :   Alex
 * @version :   V 1.0.9
 * @e_mail :   18238818283@sina.cn
 * @time :   2018/03/19
 * @desc :
 */

public class KilometerData implements Parcelable {

   private String data;
   private Float kilometer;

    public KilometerData(String data, float kilometer) {
        this.data = data;
        this.kilometer = kilometer;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.data);
        dest.writeValue(this.kilometer);
    }

    protected KilometerData(Parcel in) {
        this.data = in.readString();
        this.kilometer = (Float) in.readValue(Float.class.getClassLoader());
    }

    public static final Creator<KilometerData> CREATOR = new Creator<KilometerData>() {
        @Override
        public KilometerData createFromParcel(Parcel source) {
            return new KilometerData(source);
        }

        @Override
        public KilometerData[] newArray(int size) {
            return new KilometerData[size];
        }
    };

    public String getData() {
        return data;
    }

    public Float getKilometer() {
        return kilometer;
    }
}
