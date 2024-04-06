package com.zhao.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.nacos.api.common.Constants;
import com.zhao.auth.form.LoginByGithub;
import com.zhao.auth.github.Oauth2Properties;
import com.zhao.common.core.utils.uuid.IdUtils;
import com.zhao.common.core.web.domain.AjaxResult;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zhao.auth.form.LoginBody;
import com.zhao.auth.form.RegisterBody;
import com.zhao.auth.service.SysLoginService;
import com.zhao.common.core.domain.R;
import com.zhao.common.core.utils.JwtUtils;
import com.zhao.common.core.utils.StringUtils;
import com.zhao.common.security.auth.AuthUtil;
import com.zhao.common.security.service.TokenService;
import com.zhao.common.security.utils.SecurityUtils;
import com.zhao.system.api.model.LoginUser;

import java.util.Map;

/**
 * token 控制
 * 
 * @author zhao
 */
@RestController
public class TokenController
{
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    private final Oauth2Properties oauth2Properties;

    public TokenController(Oauth2Properties oauth2Properties) {
        this.oauth2Properties = oauth2Properties;
    }

    @PostMapping("login")
    public R<?> login(@RequestBody LoginBody form)
    {
        // 用户登录
        LoginUser userInfo = sysLoginService.login(form.getUsername(), form.getPassword());
        // 获取登录token
        return R.ok(tokenService.createToken(userInfo));
    }

    /**
     * 生成github url
     * @return
     */
    @GetMapping("/preLoginByGithub")
    public AjaxResult PreLoginByGithub() {
        AjaxResult ajax = AjaxResult.success();
        AuthRequest authRequest = new AuthGithubRequest(AuthConfig.builder()
                .clientId(oauth2Properties.getClientId())
                .clientSecret(oauth2Properties.getClientSecret())
                .redirectUri(oauth2Properties.getRedirectUrl())
                .build());
        String uuid = IdUtils.fastUUID();
        String authorizeUrl = authRequest.authorize(uuid);
        //存储
        ajax.put("authorizeUrl", authorizeUrl);
        ajax.put("uuid", uuid);
        return ajax;
    }

    /**
     * github回调登录
     * @param loginByGithub
     * @return
     */
    @PostMapping("/loginByGithub")
    public R<?> loginByGithub(@RequestBody LoginByGithub loginByGithub) {
        LoginUser loginUser = sysLoginService.loginByOtherSource(loginByGithub.getCode(), loginByGithub.getSource(), loginByGithub.getUuid());
        // 获取登录token
        return R.ok(tokenService.createToken(loginUser));

    }

    /**
     * LDAP登录
     * @param form
     * @return
     */
    @PostMapping("/loginByLdap")
    public R<?> loginByLdap(@RequestBody LoginBody form)
    {
        // 用户登录
        LoginUser userInfo = sysLoginService.loginByLdap(form.getUsername(), form.getPassword());
        // 获取登录token
        return R.ok(tokenService.createToken(userInfo));
    }

    @DeleteMapping("logout")
    public R<?> logout(HttpServletRequest request)
    {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return R.ok();
    }

    @PostMapping("refresh")
    public R<?> refresh(HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return R.ok();
        }
        return R.ok();
    }

    @PostMapping("register")
    public R<?> register(@RequestBody RegisterBody registerBody)
    {
        // 用户注册
        sysLoginService.register(registerBody.getUsername(), registerBody.getPassword());
        return R.ok();
    }
}
