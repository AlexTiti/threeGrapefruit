package com.findtech.threePomelos.travel.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/29
 */

public class KilometerBean implements Parcelable {
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 出行距离
     */
    private int distance;
    /**
     * 卡路里
     */
    private int calorie;

    public KilometerBean(String startTime, String endTime, int distance, int calorie) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.calorie = calorie;
    }

    protected KilometerBean(Parcel in) {
        startTime = in.readString();
        endTime = in.readString();
        distance = in.readInt();
        calorie = in.readInt();
    }

    public static final Creator<KilometerBean> CREATOR = new Creator<KilometerBean>() {
        @Override
        public KilometerBean createFromParcel(Parcel in) {
            return new KilometerBean(in);
        }

        @Override
        public KilometerBean[] newArray(int size) {
            return new KilometerBean[size];
        }
    };

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public double getDistance() {
        return distance;
    }

    public double getCalorie() {
        return calorie;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeInt(distance);
        dest.writeInt(calorie);
    }
}
