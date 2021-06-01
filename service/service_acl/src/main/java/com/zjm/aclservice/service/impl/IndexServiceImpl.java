package com.zjm.aclservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zjm.aclservice.entity.Role;
import com.zjm.aclservice.entity.User;
import com.zjm.aclservice.service.IndexService;
import com.zjm.aclservice.service.PermissionService;
import com.zjm.aclservice.service.RoleService;
import com.zjm.aclservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据用户名获取用户登录信息
     *
     * @param username
     * @return
     */
    @Override
    public Map<String, Object> getUserInfo(String username) {
            Object redisData;
            Map<String, Object> result = new HashMap<>();
            User user = userService.selectByUsername(username);
            if (null == user) {
                //throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            List<String> roleNameList = new ArrayList<>();
            redisData = redisTemplate.opsForValue().get(username + "_role");
            if(redisData != null){
                roleNameList = (List<String>) redisData;
                System.out.println("===================通过redis获取用户角色信息==================");
                System.out.println(roleNameList);
            }else {
                //根据用户id获取角色
                List<Role> roleList = roleService.selectRoleByUserId(user.getId());
                roleNameList = roleList.stream().map(item -> item.getRoleName()).collect(Collectors.toList());
                if(roleNameList.size() == 0) {
                    //前端框架必须返回一个角色，否则报错，如果没有角色，返回一个空角色
                    roleNameList.add("");
                    //roleNameList.add("未分配角色");
                }else {
                    redisTemplate.opsForValue().set(username + "_role",roleNameList,30L, TimeUnit.MINUTES);
                }
            }

            //根据用户id获取操作权限值
            List<String> permissionValueList = new ArrayList<>();
            redisData = redisTemplate.opsForValue().get(username + "_permission");
            if(redisData != null){
                permissionValueList = (List<String>) redisData;
                System.out.println("================通过redis获取权限信息===================");
                System.out.println(permissionValueList);
            }else {
                permissionValueList = permissionService.selectPermissionValueByUserId(user.getId());
                redisTemplate.opsForValue().set(username + "_permission", permissionValueList,30L,TimeUnit.MINUTES);
            }

            result.put("name", user.getUsername());
            result.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            result.put("roles", roleNameList);
            result.put("permissionValueList", permissionValueList);

            return result;
        }

        /**
         * 根据用户名获取动态菜单
         * @param username
         * @return
         */
        @Override
        public List<JSONObject> getMenu(String username) {
            Object o = redisTemplate.opsForValue().get(username + "_menu");
            if(o != null){
                System.out.println("================通过redis获取菜单====================");
                System.out.println((List<JSONObject>)o);
                return (List<JSONObject>) o;
            }


            User user = userService.selectByUsername(username);
            //根据用户id获取用户菜单权限
            System.out.println("================用户id获取用户菜单权限====================");
            List<JSONObject> permissionList = permissionService.selectPermissionByUserId(user.getId());
            redisTemplate.opsForValue().set(username + "_menu",permissionList,30L, TimeUnit.MINUTES);
            return permissionList;
        }


}
