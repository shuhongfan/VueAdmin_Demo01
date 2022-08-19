package com.shf.vueadminspringboot.service;

import com.shf.vueadminspringboot.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shuhongfan
 * @since 2022-08-17
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 查询用户信息
     * @param username
     * @return
     */
    SysUser getByUsername(String username);

    /**
     * 获取角色和权限信息
     * @param userId
     * @return
     */
    String getUserAuthorityInfo(Long userId);

    void clearUserAuthorityInfo(String username);
    void clearUserAuthorityInfoByRoleId(Long roleId);
    void clearUserAuthorityInfoByMenuId(Long menuId);
}
