package com.findtech.threePomelos.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.findtech.threePomelos.utils.RequestUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author zhi.zhang
 * @date 3/10/16
 */
public class BabyInfoEntity implements Parcelable {

    private String babyName;
    private String babySex;
    private String birthday;
    private String babyNative;
    private String image;
    private boolean isBind;
    private String bluetoothDeviceId;
    private String babyInfoObjectId;
    private String babyTotalDay;

    public BabyInfoEntity() {
    }

    protected BabyInfoEntity(Parcel in) {
        babyName = in.readString();
        babySex = in.readString();
        birthday = in.readString();
        babyNative = in.readString();
        image = in.readString();
        isBind = in.readByte() != 0;
        bluetoothDeviceId = in.readString();
        babyInfoObjectId = in.readString();
        babyTotalDay = in.readString();
    }

    public static final Creator<BabyInfoEntity> CREATOR = new Creator<BabyInfoEntity>() {
        @Override
        public BabyInfoEntity createFromParcel(Parcel in) {
            return new BabyInfoEntity(in);
        }

        @Override
        public BabyInfoEntity[] newArray(int size) {
            return new BabyInfoEntity[size];
        }
    };

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName, String def) {
        if (TextUtils.isEmpty(babyName)) {
            this.babyName = def;
        } else {
            this.babyName = babyName;
        }
    }

    public String getBabySex() {
        return babySex;
    }

    public void setBabySex(String babySex, String def) {
        if (TextUtils.isEmpty(babySex)) {
            this.babySex = def;
        } else {
            this.babySex = babySex;
        }
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday, String def) {
        if (TextUtils.isEmpty(birthday)) {
            this.birthday = def;
        } else {
            this.birthday = birthday;
        }
    }

    public String getBabyNative() {
        return babyNative;
    }

    public void setBabyNative(String babyNative, String def) {
        if (TextUtils.isEmpty(babyNative)) {
            this.babyNative = def;
        } else {
            this.babyNative = babyNative;
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image, String def) {
        if (TextUtils.isEmpty(image)) {
            this.image = def;
        } else {
            this.image = image;
        }
    }

    public boolean getIsBind() {
        return isBind;
    }

    public void setIsBind(int isBind) {
        if (isBind == 1) {
            this.isBind = true;
        } else {
            this.isBind = false;
        }
    }

    public String getBluetoothDeviceId() {
        return bluetoothDeviceId;
    }

    public void setBluetoothDeviceId(String bluetoothDeviceId) {
        this.bluetoothDeviceId = bluetoothDeviceId;
    }

    public String getBabyInfoObjectId() {
        return babyInfoObjectId;
    }

    public void setBabyInfoObjectId(String babyInfoObjectId) {
        this.babyInfoObjectId = babyInfoObjectId;
    }

    public String getBabyTotalDay() {
        return babyTotalDay;
    }

    public void setBabyTotalDay(Context context, String babyBirthday, String def) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        if (TextUtils.isEmpty(babyBirthday)) {
            this.babyTotalDay = def;
        } else {
            Calendar fromCalendar = null;
            Calendar toCalendar = null;
            try {
                Date date = sdf.parse(babyBirthday);
                fromCalendar = Calendar.getInstance();
                fromCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                final String currentDate = sdf.format(new java.util.Date());
                Date date = sdf.parse(currentDate);
                toCalendar = Calendar.getInstance();
                toCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (fromCalendar != null && toCalendar != null) {
                this.babyTotalDay = String.valueOf((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
            }
        }
        RequestUtils.getSharepreferenceEditor(context).putString(RequestUtils.TOTALE_DAY, babyTotalDay).commit();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(babyName);
        dest.writeString(babySex);
        dest.writeString(birthday);
        dest.writeString(babyNative);
        dest.writeString(image);
        dest.writeByte((byte) (isBind ? 1 : 0));
        dest.writeString(bluetoothDeviceId);
        dest.writeString(babyInfoObjectId);
        dest.writeString(babyTotalDay);
    }
}
