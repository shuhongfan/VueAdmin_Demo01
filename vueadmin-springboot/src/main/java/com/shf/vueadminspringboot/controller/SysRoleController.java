package com.shf.vueadminspringboot.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shf.vueadminspringboot.common.lang.Const;
import com.shf.vueadminspringboot.common.lang.Result;
import com.shf.vueadminspringboot.entity.SysRole;
import com.shf.vueadminspringboot.entity.SysRoleMenu;
import com.shf.vueadminspringboot.entity.SysUserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author shuhongfan
 * @since 2022-08-17
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    @GetMapping("info/{id}")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result info(@PathVariable("id") Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", sysRole.getId()));

        List<Long> menuIds = roleMenus.stream().map(p -> p.getMenuId()).collect(Collectors.toList());
        sysRole.setMenuIds(menuIds);

        return Result.succ(sysRole);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result list(String name) {
        Page page = sysRoleService.page(
                getPage(),
                new QueryWrapper<SysRole>()
                        .like(StrUtil.isNotBlank(name), "name", name));
        return Result.succ(page);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result save(@Validated @RequestBody SysRole sysRole) {
        sysRole.setCreated(LocalDateTime.now());
        sysRoleService.save(sysRole);
        return Result.succ(sysRole);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result update(@Validated @RequestBody SysRole sysRole) {
        sysRole.setUpdated(LocalDateTime.now());
        sysRoleService.updateById(sysRole);

//        缓存
        sysUserService.clearUserAuthorityInfoByRoleId(sysRole.getId());
        return Result.succ(sysRole);
    }

    @PostMapping("/delete")
    @Transactional
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result delete(@RequestBody Long[] roleIds) {
        sysRoleService.removeByIds(Arrays.asList(roleIds));

//        删除中间表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id", roleIds));
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id", roleIds));
//        缓存
        Arrays.stream(roleIds).forEach(id->{
            sysUserService.clearUserAuthorityInfoByRoleId(id);
        });
        return Result.succ(roleIds);
    }

    @PostMapping("/perm/{roleId}")
    @Transactional
    @PreAuthorize("hasAuthority('sys:role:perm')")
    public Result perm(@PathVariable("roleId") Long roleId, @RequestBody Long[] menuIds) {
        ArrayList<SysRoleMenu> sysRoleMenus = new ArrayList<>();

        Arrays.stream(menuIds).forEach(menuId->{
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);

            sysRoleMenus.add(roleMenu);
        });

//        先删除原来的记录，再保存新的
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id", roleId));
        sysRoleMenuService.saveBatch(sysRoleMenus);

//        删除缓存
        sysUserService.clearUserAuthorityInfoByRoleId(roleId);

        return Result.succ(menuIds);
    }
}
