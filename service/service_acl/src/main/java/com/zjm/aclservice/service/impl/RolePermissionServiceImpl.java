package com.zjm.aclservice.service.impl;

import com.zjm.aclservice.entity.RolePermission;
import com.zjm.aclservice.mapper.RolePermissionMapper;
import com.zjm.aclservice.service.RolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-01-12
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

    @Override
    public List<String> selectUsernameByRoleId(String role) {
        List<String> usernameList = baseMapper.selectUsernameByRoleId(role);
        return usernameList;
    }
}
