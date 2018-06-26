package com.findtech.threePomelos.mydevices.bean;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/11
 */

public class DeviceStateBean {

    private static DeviceStateBean bean;

    private int brakeModel = 2;

    private int brakeNoticeModel = 0;
    private int otherNoticeModel = 1;
    private int index = 0 ;

    private boolean isConnected;
    private String codeSystem;

    public static DeviceStateBean getInstance() {
        if (bean == null) {
            synchronized (DeviceStateBean.class) {
                if (bean == null) {
                    bean = new DeviceStateBean();
                }
            }
        }
        return bean;
    }

    private int brakeFast = 0;

    public int getBrakeFast() {
        return brakeFast;
    }

    public void setBrakeFast(int brakeFast) {
        this.brakeFast = brakeFast;
    }


    public int getBrakeNoticeModel() {
        return brakeNoticeModel;
    }

    public void setBrakeNoticeModel(int brakeNoticeModel) {
        this.brakeNoticeModel = brakeNoticeModel;
    }

    public int getOtherNoticeModel() {
        return otherNoticeModel;
    }

    public void setOtherNoticeModel(int otherNoticeModel) {
        this.otherNoticeModel = otherNoticeModel;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }

    public int getBrakeModel() {
        return brakeModel;
    }

    public void setBrakeModel(int brakeModel) {
        this.brakeModel = brakeModel;
    }
}
