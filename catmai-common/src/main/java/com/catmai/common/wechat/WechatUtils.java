package com.catmai.common.wechat;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.catmai.common.constant.Constants;
import com.catmai.common.core.redis.RedisCache;
import com.catmai.common.exception.ServiceException;
import com.catmai.common.utils.StringUtils;
import com.catmai.common.utils.http.HttpUtils;
import com.catmai.common.utils.sign.Base64;
import com.catmai.common.utils.spring.SpringUtils;
import com.catmai.common.wechat.model.WechatResult;
import okhttp3.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @描述: 微信工具类
 * @作者: chenzhiqiang
 * @创建时间： 2020-12-12 10:56
 **/
public class WechatUtils {

    private static final Logger log = LoggerFactory.getLogger(WechatUtils.class);

    private static final String AuthCode2Session = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 获取accessToken
     */
    private static final String AccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 获取用户手机号接口
     */
    private static final String UserPhoneUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";


    public static final String ACCESS_TOKEN = "access_token";

    public static final String ACCESS_TOKEN_CACHE = "WECHAT:ACCESS_TOKEN";

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
     * 通过ID和密钥，免登授权码获得微信用户信息
     * @param appId
     * @param appSecret
     * @return
     */
    public static String getAccessToken(String appId, String appSecret){
        log.info("开始获得accessToken---------");
        String result = SpringUtils.getBean(RedisCache.class).getCacheObject(ACCESS_TOKEN_CACHE);
        if (StringUtils.isNotEmpty(result)){
            log.info("accessToken缓存命中...返回");
            return result;
        }
        String jsonResult = HttpUtils.sendGet(AccessTokenUrl,"appid=" + appId + "&secret=" + appSecret + "&grant_type=client_credential");
        log.debug(jsonResult);
        if (StringUtils.isNotEmpty(jsonResult)){
            JSONObject resultObject = JSON.parseObject(jsonResult);
            if (StringUtils.isNotEmpty(resultObject.getString(ACCESS_TOKEN))){
                SpringUtils.getBean(RedisCache.class).setCacheObject(ACCESS_TOKEN_CACHE,resultObject.getString(ACCESS_TOKEN),7200, TimeUnit.SECONDS);
                result = resultObject.getString(ACCESS_TOKEN);
            }else {
                throw new ServiceException(resultObject.getString("errmsg"));
            }
        }
        return result;
    }

    /**
     * 获取用户手机号
     * @return
     */
    public static String getUserPhone(String accessToken,String code) {
        log.info("开始获取手机号---------");
        Map<String,String> dataParam = new HashMap<>();
        dataParam.put("code",code);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSONObject.toJSONString(dataParam), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(UserPhoneUrl + accessToken)
                .post(body)
                .build();
        String jsonResult = null;
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                jsonResult = response.body().string();
            }
        } catch (IOException e) {
            log.error("手机号获取失败",e);
            throw new ServiceException("手机号获取失败");
        }
        log.debug(jsonResult);
        if (StringUtils.isNotEmpty(jsonResult)){
            JSONObject resultObject = JSONObject.parseObject(jsonResult);
            if (StringUtils.isNotEmpty(resultObject.getString("errcode")) && resultObject.getString("errcode").equals(Constants.SUCCESS)){
                return resultObject.getJSONObject("phone_info").getString("purePhoneNumber");
            }
        }
        return "";
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
