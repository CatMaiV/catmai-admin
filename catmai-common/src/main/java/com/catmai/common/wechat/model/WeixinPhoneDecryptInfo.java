package com.catmai.common.wechat.model;

import com.alibaba.fastjson2.JSON;

/**
 * @描述: 用户手机号解迷返回类
 * @作者: CatMai
 * @创建时间： 2021-06-20 17:49
 **/
public class WeixinPhoneDecryptInfo {

    private String phoneNumber;

    private String purePhoneNumber;

    private int countryCode;

    private String watermark;

    private WeixinWaterMark weixinWaterMark;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPurePhoneNumber() {
        return purePhoneNumber;
    }

    public void setPurePhoneNumber(String purePhoneNumber) {
        this.purePhoneNumber = purePhoneNumber;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
        this.weixinWaterMark = JSON.toJavaObject(JSON.parseObject(this.watermark),WeixinWaterMark.class);
    }

    public WeixinWaterMark getWeixinWaterMark(){
        return weixinWaterMark;
    }

    @Override
    public String toString() {
        return "WeixinPhoneDecryptInfo{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", purePhoneNumber='" + purePhoneNumber + '\'' +
                ", countryCode=" + countryCode +
                ", appid=" + weixinWaterMark.getAppid() +
                ", timestamp=" + weixinWaterMark.getTimestamp() +
                '}';
    }
}
