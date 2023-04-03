package com.catmai.common.wechat.model;

/**
 * @描述: 微信水印
 * @作者: CatMai
 * @创建时间： 2021-06-20 17:50
 **/
public class WeixinWaterMark {

    private Long timestamp;
    private String appid;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
}
