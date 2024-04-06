package com.zhao.auth.service;

import com.zhao.auth.github.Oauth2Properties;
import com.zhao.auth.ldap.LdapPerson;
import com.zhao.auth.ldap.PersonAttributesMapper;
import com.zhao.common.core.exception.base.BaseException;
import com.zhao.system.api.RemoteDeptService;
import com.zhao.system.api.domain.SysDept;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Component;
import com.zhao.common.core.constant.CacheConstants;
import com.zhao.common.core.constant.Constants;
import com.zhao.common.core.constant.SecurityConstants;
import com.zhao.common.core.constant.UserConstants;
import com.zhao.common.core.domain.R;
import com.zhao.common.core.enums.UserStatus;
import com.zhao.common.core.exception.ServiceException;
import com.zhao.common.core.text.Convert;
import com.zhao.common.core.utils.StringUtils;
import com.zhao.common.core.utils.ip.IpUtils;
import com.zhao.common.redis.service.RedisService;
import com.zhao.common.security.utils.SecurityUtils;
import com.zhao.system.api.RemoteUserService;
import com.zhao.system.api.domain.SysUser;
import com.zhao.system.api.model.LoginUser;
import sun.misc.MessageUtils;

import java.util.List;

/**
 * 登录校验方法
 * 
 * @author zhao
 */
@Component
public class SysLoginService
{
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private RemoteDeptService remoteDeptService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysRecordLogService recordLogService;

    @Autowired
    private RedisService redisService;

    private final Oauth2Properties oauth2Properties;
    private final LdapTemplate ldapTemplate;

    public SysLoginService(Oauth2Properties oauth2Properties, LdapTemplate ldapTemplate) {
        this.oauth2Properties = oauth2Properties;
        this.ldapTemplate = ldapTemplate;
    }

    /**
     * 登录
     */
    public LoginUser login(String username, String password)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password))
        {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户/密码必须填写");
            throw new ServiceException("用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户密码不在指定范围");
            throw new ServiceException("用户密码不在指定范围");
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户名不在指定范围");
            throw new ServiceException("用户名不在指定范围");
        }
        // IP黑名单校验
        String blackStr = Convert.toStr(redisService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKIPLIST));
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "很遗憾，访问IP已被列入系统黑名单");
            throw new ServiceException("很遗憾，访问IP已被列入系统黑名单");
        }
        // 查询用户信息
        R<LoginUser> userResult = remoteUserService.getUserInfo(username, SecurityConstants.INNER);

        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData()))
        {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "登录用户不存在");
            throw new ServiceException("登录用户：" + username + " 不存在");
        }

        if (R.FAIL == userResult.getCode())
        {
            throw new ServiceException(userResult.getMsg());
        }
        
        LoginUser userInfo = userResult.getData();
        SysUser user = userResult.getData().getSysUser();
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "对不起，您的账号已被删除");
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            recordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户已停用，请联系管理员");
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        passwordService.validate(user, password);
        recordLogService.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
        return userInfo;
    }

    public void logout(String loginName)
    {
        recordLogService.recordLogininfor(loginName, Constants.LOGOUT, "退出成功");
    }

    /**
     * 注册
     */
    public void register(String username, String password)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password))
        {
            throw new ServiceException("用户/密码必须填写");
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }

        // 注册用户信息
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        R<?> registerResult = remoteUserService.registerUserInfo(sysUser, SecurityConstants.INNER);

        if (R.FAIL == registerResult.getCode())
        {
            throw new ServiceException(registerResult.getMsg());
        }
        recordLogService.recordLogininfor(username, Constants.REGISTER, "注册成功");
    }

    /**
     * github登录
     */
    public void githubLogin(String username)
    {
      recordLogService.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
    }

    /**
     * github登录
     */
    public LoginUser loginByOtherSource(String code,String source, String uuid) {
        //先到数据库查询这个人曾经有没有登录过，没有就注册
        // 创建授权request
        AuthRequest authRequest = new AuthGithubRequest(AuthConfig.builder()
                .clientId(oauth2Properties.getClientId())
                .clientSecret(oauth2Properties.getClientSecret())
                .redirectUri(oauth2Properties.getRedirectUrl())
                .build());
        AuthResponse<AuthUser> login = authRequest.login(AuthCallback.builder().state(uuid).code(code).build());
        //先查询数据库有没有该用户
        AuthUser authUser = login.getData();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(authUser.getUsername());
        List<SysUser> sysUsers = remoteUserService.selectUserListNoDataScope(sysUser,SecurityConstants.INNER);
            if (sysUsers.size() > 1) {
                throw new ServiceException("第三方登录异常，账号重叠");
            } else if (sysUsers.size() == 0) {
                //相当于注册
                sysUser.setGithubId(authUser.getUuid());
                sysUser.setUserName(authUser.getUsername());
                sysUser.setNickName(authUser.getUsername());
                sysUser.setPassword(SecurityUtils.encryptPassword(authUser.getUsername()));
                //默认角色EDITOR
                sysUser.setRoleIds(new Long[]{3L});
                //默认研发部
                sysUser.setDeptId(new Long(103L));
                sysUser.setRemark(source);
                remoteUserService.githubRegister(sysUser,SecurityConstants.INNER);
            }else {
                sysUser = sysUsers.get(0);
            }
        recordLogService.recordLogininfor(sysUser.getUserName(), Constants.LOGIN_SUCCESS, "登录成功");
        // 查询用户信息
        R<LoginUser> userResult = remoteUserService.getUserInfo(sysUser.getUserName(), SecurityConstants.INNER);
        LoginUser userInfo = userResult.getData();
        return userInfo;
    }

    /**
     * Ldap登录
     */
    public LoginUser loginByLdap(String username, String password) {
        try
        {
            if (!authenticate(username,password)){
                throw new  BaseException("注册失败请联系管理人员");
            }
            //先查询数据库有没有该用户
            SysUser sysUser = new SysUser();
            sysUser.setUserName(username);
            List<SysUser> sysUsers = remoteUserService.selectUserListNoDataScope(sysUser,SecurityConstants.INNER);
            if (sysUsers.size() > 1) {
                throw new ServiceException("第三方登录异常，账号重叠");
            } else if (sysUsers.size() == 0) {
                // 注册
                sysUser.setUserName(username);
                sysUser.setNickName(username);
                sysUser.setPassword(SecurityUtils.encryptPassword(password));
                sysUser.setCreateBy("ldap");
                sysUser.setRemark("ldap");
                // 普通岗位
                sysUser.setPostIds(new Long[]{4L});
                // 默认普通角色
                sysUser.setRoleIds(new Long[]{3L});
                //默认研发部
                sysUser.setDeptId(new Long(103L));
                remoteUserService.githubRegister(sysUser, SecurityConstants.INNER);
            }else {
                sysUser = sysUsers.get(0);
            }
            recordLogService.recordLogininfor(sysUser.getUserName(), Constants.LOGIN_SUCCESS, "登录成功");
            // 查询用户信息
            R<LoginUser> userResult = remoteUserService.getUserInfo(sysUser.getUserName(), SecurityConstants.INNER);
            LoginUser userInfo = userResult.getData();
            return userInfo;
        }catch (Exception e){
            throw new  BaseException("注册失败请联系管理人员");
        }
    }


    public Boolean authenticate(String uid,String password){
        LdapQuery query = LdapQueryBuilder.query().where("uid").is(uid);
        ldapTemplate.authenticate(query, password);
        List<LdapPerson> search = ldapTemplate.search(query, new PersonAttributesMapper());
        if (search.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

}
