package com.zjm.aclservice.service;

import com.zjm.aclservice.entity.RolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色权限 服务类
 * @author ZengJinming
 * @since 2020-04-16
 */
public interface RolePermissionService extends IService<RolePermission> {
    List<String> selectUsernameByRoleId(String role);
}
