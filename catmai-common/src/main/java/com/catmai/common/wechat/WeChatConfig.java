package com.catmai.common.wechat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @描述: 支付配置
 * @作者: chenzhiqiang
 * @创建时间： 2022-06-01 17:20
 **/
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatConfig {

    //微信支付类型
    //NATIVE--原生支付
    //JSAPI--公众号支付-小程序支付
    //MWEB--H5支付
    //APP -- app支付
    public static final String TRADE_TYPE_NATIVE = "NATIVE";
    public static final String TRADE_TYPE_JSAPI = "JSAPI";
    public static final String TRADE_TYPE_MWEB = "MWEB";
    public static final String TRADE_TYPE_APP = "APP";

    //微信支付API
    public static final String WX_PAY_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //微信退款接口
    public static final String WX_REFOUND_ORDER_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    private static String appId;

    private static String appSecret;

    private static String mchId;

    private static String key;

    /**
     * 退款商家私钥
     */
    private static String caKey;

    /**
     * 回调地址
     */
    private static String notifyUrl;

    public static String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        WeChatConfig.appId = appId;
    }

    public static String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        WeChatConfig.mchId = mchId;
    }

    public static String getAppSecret() {
        return appSecret;
    }

    public void setAppSercet(String appSecret) {
        WeChatConfig.appSecret = appSecret;
    }

    public static String getKey() {
        return key;
    }

    public void setKey(String key) {
        WeChatConfig.key = key;
    }

    public void setAppSecret(String appSecret) {
        WeChatConfig.appSecret = appSecret;
    }

    public static String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        WeChatConfig.notifyUrl = notifyUrl;
    }

    public static String getCaKey() {
        return caKey;
    }

    public void setCaKey(String caKey) {
        WeChatConfig.caKey = caKey;
    }
}
