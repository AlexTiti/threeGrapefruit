package com.findtech.threePomelos.mydevices.bean;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/06/22
 *   desc     :
 *   version  :   V 1.0.5
 *
 * @author Administrator
 */
public class DeviceCarBean  {

    private String deviceName;
    private String deviceAddress;
    private String deviceType;
    private String functionType;
    private String company;

    public String getCompany() {
        return company;
    }

    public String getFunctionType() {
        return functionType;
    }

    public DeviceCarBean(String deviceName, String deviceAddress, String deviceType , String functionType ,String company) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.deviceType = deviceType;
        this.functionType = functionType;
        this.company = company;

    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceaAddress() {
        return deviceAddress;
    }

    public void setDeviceaAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }


}
