package com.zjm.aclservice.mapper;

import com.zjm.aclservice.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 角色权限 Mapper 接口
 * @author ZengJinming
 * @since 2020-04-16
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    List<String> selectUsernameByRoleId(String role);
}
