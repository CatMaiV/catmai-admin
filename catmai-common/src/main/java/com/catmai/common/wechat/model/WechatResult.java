package com.catmai.common.wechat.model;


/**
 * @描述: 微信接口返回对象
 * @作者: CatMai
 * @创建时间： 2020-12-12 10:59
 **/
public class WechatResult {

    //用户唯一标识
    private String openid;
    //会话ID
    private String session_key;
    //用户在开放平台的唯一标识
    private String unionid;
    //错误码
    private String errcode;
    //错误信息
    private String errmsg;

    private String uuid;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
