package com.shf.vueadminspringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shf.vueadminspringboot.entity.SysMenu;
import com.shf.vueadminspringboot.entity.SysRole;
import com.shf.vueadminspringboot.entity.SysUser;
import com.shf.vueadminspringboot.mapper.SysUserMapper;
import com.shf.vueadminspringboot.service.SysMenuService;
import com.shf.vueadminspringboot.service.SysRoleService;
import com.shf.vueadminspringboot.service.SysUserRoleService;
import com.shf.vueadminspringboot.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.vueadminspringboot.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shuhongfan
 * @since 2022-08-17
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return getOne(wrapper);
    }

    /**
     * 获取角色和权限信息
     * @param userId
     * @return
     */
    @Override
    public String getUserAuthorityInfo(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);

        String authority = "";

        if (redisUtil.hasKey("GrantedAuthority:" + sysUser.getUsername())) {
            authority = (String) redisUtil.get("GrantedAuthority:" + sysUser.getUsername());
            return authority;
        }

//        获取角色编码
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.inSql("id", "select role_id from sys_user_role where user_id=" + userId);
        List<SysRole> roles = sysRoleService.list(wrapper);

        if (roles.size() > 0) {
            String roleCodes = roles.stream().map(r -> "ROLE_"+r.getCode()).collect(Collectors.joining(","));
            authority = roleCodes.concat(",");
        }

//        获取菜单操作编码
        List<Long> menuIds = sysUserMapper.getNavMenuIds(userId);
        if (menuIds.size() > 0) {
            List<SysMenu> menus = sysMenuService.listByIds(menuIds);
            String menuParms = menus.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));

            authority = authority.concat(menuParms);
        }

        redisUtil.set("GrantedAuthority:" + sysUser.getUsername(), authority, 60 * 60); // redis缓存

        return authority;
    }

    @Override
    public void clearUserAuthorityInfo(String username) {
        redisUtil.del("GrantedAuthority:" + username);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<SysUser>()
                .inSql("id", "select user_id from sys_user_role where role_id=" + roleId);
        List<SysUser> sysUsers = list(wrapper);
        sysUsers.forEach(u->{
            clearUserAuthorityInfo(u.getUsername());
        });
    }

    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        List<SysUser> sysUserList = sysUserMapper.listByMenuId(menuId);

        sysUserList.forEach(u->{
            clearUserAuthorityInfo(u.getUsername());
        });
    }

}
