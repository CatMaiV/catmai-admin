package com.catmai.common.wechat.model;

import javax.validation.constraints.NotBlank;

/**
 * @描述: 微信小程序登录数据模型
 * @作者: chenzhiqiang
 * @创建时间： 2022-06-05 14:04
 **/
public class WechatAppLoginDto {

    @NotBlank(message = "加密信息不能为空")
    private String encryptedData;
    @NotBlank(message = "iv不能为空")
    private String iv;
    @NotBlank(message = "授权码不能为空")
    private String code;

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
