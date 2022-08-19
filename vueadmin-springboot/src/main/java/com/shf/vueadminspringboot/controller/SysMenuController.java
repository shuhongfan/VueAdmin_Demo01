package com.shf.vueadminspringboot.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shf.vueadminspringboot.common.dto.SysMenuDto;
import com.shf.vueadminspringboot.common.lang.Result;
import com.shf.vueadminspringboot.entity.SysMenu;
import com.shf.vueadminspringboot.entity.SysRoleMenu;
import com.shf.vueadminspringboot.entity.SysUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author shuhongfan
 * @since 2022-08-17
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {

    @GetMapping("nav")
    public Result nav(Principal principal) {
        SysUser sysUser = sysUserService.getByUsername(principal.getName());

//        获取权限信息
        String userAuthorityInfo = sysUserService.getUserAuthorityInfo(sysUser.getId());
        String[] userAuthorityArray = StringUtils.tokenizeToStringArray(userAuthorityInfo, ",");

//        获取导航栏信息
        List<SysMenuDto> nav = sysMenuService.getCurrentUserNav();


        return Result.succ(
                MapUtil.builder()
                        .put("authoritys", userAuthorityArray)
                        .put("nav", nav)
                        .map());
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable(name = "id") Long id) {
        return Result.succ(sysMenuService.getById(id));
    }

    /**
     * 获取所有菜单信息
     *
     * @return
     */
    @GetMapping("list")
    @PreAuthorize("hasAnyAuthority('sys:menu:list')")
    public Result list() {
        List<SysMenu> menus = sysMenuService.tree();
        return Result.succ(menus);
    }


    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('sys:menu:save')")
    public Result save(@Validated @RequestBody SysMenu sysMenu) {
        sysMenu.setCreated(LocalDateTime.now());
        sysMenuService.save(sysMenu);
        return Result.succ(sysMenu);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('sys:menu:update')")
    public Result update(@Validated @RequestBody SysMenu sysMenu) {
        sysMenu.setCreated(LocalDateTime.now());
        sysMenuService.updateById(sysMenu);

//        清除缓存
        sysUserService.clearUserAuthorityInfoByMenuId(sysMenu.getId());
        return Result.succ(sysMenu);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('sys:menu:delete')")
    public Result delete(@PathVariable("id") Long id) {
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if (count > 0) {
            return Result.fail("请先删除子菜单");
        }

//        清除缓存
        sysUserService.clearUserAuthorityInfoByMenuId(id);

        sysMenuService.removeById(id);
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id", id));
        return Result.succ(id);
    }
}
