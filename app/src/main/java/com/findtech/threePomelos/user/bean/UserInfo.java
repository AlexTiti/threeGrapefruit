package com.findtech.threePomelos.user.bean;

import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/14
 */

public class UserInfo implements Serializable{

    private static UserInfo userInfo;

    private ArrayList<DeviceCarBean> carBeanArrayList ;
    private ArrayList<BabyInfoEntity> babyInfoEntities;
    private String nickName;

    public ArrayList<BabyInfoEntity> getBabyInfoEntities() {
        return babyInfoEntities;
    }

    public void setBabyInfoEntities(ArrayList<BabyInfoEntity> babyInfoEntities) {
        this.babyInfoEntities = babyInfoEntities;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private UserInfo() {
    }

    public static UserInfo getInstance() {

        if (userInfo == null){
            synchronized (UserInfo.class){
                if (userInfo == null){
                    userInfo = new UserInfo();
                }
            }
        }
        return userInfo;
    }

    public ArrayList<DeviceCarBean> getCarBeanArrayList() {
        return carBeanArrayList;
    }

    public void setCarBeanArrayList(ArrayList<DeviceCarBean> carBeanArrayList) {
        this.carBeanArrayList = carBeanArrayList;
    }


}
