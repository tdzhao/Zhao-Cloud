package com.zhao.system.api;

import com.zhao.common.core.constant.SecurityConstants;
import com.zhao.common.core.constant.ServiceNameConstants;
import com.zhao.common.core.domain.R;
import com.zhao.common.core.web.domain.AjaxResult;
import com.zhao.system.api.domain.SysDept;
import com.zhao.system.api.domain.SysUser;
import com.zhao.system.api.factory.RemoteDeptFallbackFactory;
import com.zhao.system.api.factory.RemoteUserFallbackFactory;
import com.zhao.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务
 * 
 * @author zhao
 */
@FeignClient(contextId = "remoteDeptService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDeptFallbackFactory.class)
public interface RemoteDeptService
{
    /**
     * 查询部门列表
     * @return 结果
     */
    @PostMapping("/dept/deptList")
    public List<SysDept> deptList(@RequestBody SysDept dept,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
