package com.catmai.web.controller.system;

import java.util.List;
import java.util.Set;

import com.alibaba.fastjson2.JSONObject;
import com.catmai.common.annotation.Anonymous;
import com.catmai.common.annotation.Log;
import com.catmai.common.wechat.WeChatConfig;
import com.catmai.common.wechat.WechatUtils;
import com.catmai.common.wechat.model.WechatAppLoginDto;
import com.catmai.common.wechat.model.WechatResult;
import com.catmai.common.wechat.model.WechatUserInfo;
import com.catmai.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.catmai.common.constant.Constants;
import com.catmai.common.core.domain.AjaxResult;
import com.catmai.common.core.domain.entity.SysMenu;
import com.catmai.common.core.domain.entity.SysUser;
import com.catmai.common.core.domain.model.LoginBody;
import com.catmai.common.utils.SecurityUtils;
import com.catmai.framework.web.service.SysLoginService;
import com.catmai.framework.web.service.SysPermissionService;
import com.catmai.system.service.ISysMenuService;

import javax.validation.Valid;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
public class SysLoginController {

    private static final Logger log = LoggerFactory.getLogger(SysLoginController.class);

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }

    @Anonymous
    @PostMapping("/wechat/auth")
    public AjaxResult wechatLogin(@Valid @RequestBody WechatAppLoginDto appLoginDto){
        AjaxResult ajax = AjaxResult.success();
        WechatResult wechatResult = WechatUtils.getWechatOpenId(WeChatConfig.getAppId(),WeChatConfig.getAppSecret(),appLoginDto.getCode());
        if (StringUtils.isNotEmpty(wechatResult.getOpenid())){
            //获取微信用户信息
            if (StringUtils.isNotEmpty(wechatResult.getSession_key())){
                log.info("拿到session_key了");
                String info = WechatUtils.getUserInfo(appLoginDto.getEncryptedData(),wechatResult.getSession_key(),appLoginDto.getIv());
                WechatUserInfo wechatUserInfo = JSONObject.parseObject(info,WechatUserInfo.class);
                if (null == wechatUserInfo || !WeChatConfig.getAppId().equals(wechatUserInfo.getWatermark().getAppid())){
                    return AjaxResult.error("获取用户信息失败");
                }
                wechatUserInfo.setOpenId(wechatResult.getOpenid());
                wechatUserInfo.setUnionId(wechatResult.getUnionid());
                String token = loginService.wechatLogin(wechatUserInfo);
                ajax.put(Constants.TOKEN, token);
                return ajax;
            }else {
                log.error("session_key获取失败");
                return AjaxResult.error("sessionKey获取失败");
            }
        }else {
            log.error(wechatResult.getErrmsg());
            return AjaxResult.error(wechatResult.getErrmsg());
        }
    }

    @Log(title = "获取手机号")
    @GetMapping("/wechat/getPhoneNumber")
    public AjaxResult getUserPhone(String code){
        //先获取access_token
        String accessToken = WechatUtils.getAccessToken(WeChatConfig.getAppId(),WeChatConfig.getAppSecret());
        if (StringUtils.isEmpty(accessToken)){
            return AjaxResult.error("accessToken获取失败");
        }
        String phone = WechatUtils.getUserPhone(accessToken,code);
        if (StringUtils.isNotEmpty(phone)){
            SysUser loginUser = SecurityUtils.getLoginUser().getUser();
            loginUser.setPhonenumber(phone);
            sysUserService.updateUserProfile(loginUser);
            return AjaxResult.success();
        }else {
            return AjaxResult.error("手机号获取失败");
        }
    }
}
