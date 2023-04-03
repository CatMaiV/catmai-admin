package com.catmai.common.wechat;

import com.alibaba.fastjson2.JSON;
import com.catmai.common.utils.http.HttpUtils;
import com.catmai.common.utils.sign.Base64;
import com.catmai.common.wechat.model.WechatResult;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

/**
 * @描述: 微信工具类
 * @作者: chenzhiqiang
 * @创建时间： 2020-12-12 10:56
 **/
public class WechatUtils {

    private static final Logger log = LoggerFactory.getLogger(WechatUtils.class);

    private static final String AuthCode2Session = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 通过ID和密钥，免登授权码获得微信用户信息
     * @param appId
     * @param appSecret
     * @param code
     * @return
     */
    public static WechatResult getWechatOpenId(String appId, String appSecret, String code){
        log.info("开始获得微信用户信息---------");
        log.debug("免登授权码：" + code);
        String jsonResult = HttpUtils.sendGet(AuthCode2Session,"appid=" + appId + "&secret=" + appSecret + "&js_code=" + code + "&grant_type=authorization_code");
        log.debug(jsonResult);
        return JSON.parseObject(jsonResult,WechatResult.class);
    }

    /**
     * 解密用户信息
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    public static String getUserInfo(String encryptedData, String sessionKey, String iv) {
        byte[] dataByte = Base64.decode(encryptedData);
        byte[] keyByte = Base64.decode(sessionKey);
        byte[] ivByte = Base64.decode(iv);
        try {
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return result;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }


}
