package com.findtech.threePomelos.travel.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/30
 */

public class TravelBean implements Parcelable {
    
    private String todayDistance;
    private String weekFrequency;
    private String travelDate;
    private String textTravelNotice;



    public TravelBean(String todayDistance, String weekFrequency, String travelDate, String textTravelNotice) {
        this.todayDistance = todayDistance;
        this.weekFrequency = weekFrequency;
        this.travelDate = travelDate;
        this.textTravelNotice = textTravelNotice;
    }


    protected TravelBean(Parcel in) {
        todayDistance = in.readString();
        weekFrequency = in.readString();
        travelDate = in.readString();
        textTravelNotice = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(todayDistance);
        dest.writeString(weekFrequency);
        dest.writeString(travelDate);
        dest.writeString(textTravelNotice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TravelBean> CREATOR = new Creator<TravelBean>() {
        @Override
        public TravelBean createFromParcel(Parcel in) {
            return new TravelBean(in);
        }

        @Override
        public TravelBean[] newArray(int size) {
            return new TravelBean[size];
        }
    };

    public String getTodayDistance() {
        return todayDistance;
    }

    public String getWeekFrequency() {
        return weekFrequency;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public String getTextTravelNotice() {
        return textTravelNotice;
    }


}
