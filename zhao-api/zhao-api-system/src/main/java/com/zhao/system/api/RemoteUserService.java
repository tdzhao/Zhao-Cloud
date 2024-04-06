package com.zhao.system.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.zhao.common.core.constant.SecurityConstants;
import com.zhao.common.core.constant.ServiceNameConstants;
import com.zhao.common.core.domain.R;
import com.zhao.system.api.domain.SysUser;
import com.zhao.system.api.factory.RemoteUserFallbackFactory;
import com.zhao.system.api.model.LoginUser;

import java.util.List;

/**
 * 用户服务
 * 
 * @author zhao
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @PostMapping("/user/githubRegister")
    public R<Boolean> githubRegister(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/user/getGithubInfo/{githubId}")
    public R<Boolean> getGithubInfo(@PathVariable("githubId") String githubId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @PostMapping("/user/selectUserListNoDataScope")
    public List<SysUser> selectUserListNoDataScope(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
