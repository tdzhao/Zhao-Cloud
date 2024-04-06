package com.zhao.system.api.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.zhao.common.core.domain.R;
import com.zhao.system.api.RemoteUserService;
import com.zhao.system.api.domain.SysUser;
import com.zhao.system.api.model.LoginUser;

import java.util.List;

/**
 * 用户服务降级处理
 * 
 * @author zhao
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable throwable)
    {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService()
        {
            @Override
            public R<LoginUser> getUserInfo(String username, String source)
            {
                return R.fail("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> registerUserInfo(SysUser sysUser, String source)
            {
                return R.fail("注册用户失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> githubRegister(SysUser sysUser, String source) {
                return R.fail("github登录失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> getGithubInfo(String githubId, String source) {
                return R.fail("github查询失败失败:" + throwable.getMessage());
            }

            @Override
            public List<SysUser> selectUserListNoDataScope(SysUser sysUser, String source) {
                return null;
            }
        };
    }
}
