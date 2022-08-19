package com.shf.vueadminspringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shf.vueadminspringboot.common.dto.SysMenuDto;
import com.shf.vueadminspringboot.entity.SysMenu;
import com.shf.vueadminspringboot.entity.SysUser;
import com.shf.vueadminspringboot.mapper.SysMenuMapper;
import com.shf.vueadminspringboot.mapper.SysUserMapper;
import com.shf.vueadminspringboot.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shf.vueadminspringboot.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author shuhongfan
 * @since 2022-08-17
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取导航栏信息
     *
     * @return
     */
    @Override
    public List<SysMenuDto> getCurrentUserNav() {
//        获取用户名
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SysUser sysUser = sysUserService.getByUsername(username);

        List<Long> menuIds = sysUserMapper.getNavMenuIds(sysUser.getId());
        List<SysMenu> menus = listByIds(menuIds);

//        转树状结构
        List<SysMenu> menuTree = buildTreeMenu(menus);

//        实体转换DTO

        return convert(menuTree);
    }

    /**
     * 获取所有菜单信息
     * @return
     */
    @Override
    public List<SysMenu> tree() {
        List<SysMenu> sysMenus = list(new QueryWrapper<SysMenu>().orderByAsc("orderNum"));
        return buildTreeMenu(sysMenus);
    }


    /**
     * 实体转换DTO
     *
     * @param menuTree
     * @return
     */
    private List<SysMenuDto> convert(List<SysMenu> menuTree) {
        ArrayList<SysMenuDto> menuDtos = new ArrayList<>();

        menuTree.forEach(m->{
            SysMenuDto dto = new SysMenuDto();

            dto.setId(m.getId());
            dto.setName(m.getPerms());
            dto.setTitle(m.getName());
            dto.setComponent(m.getComponent());
            dto.setPath(m.getPath());

            if (m.getChildren().size() > 0) {
                dto.setChildren(convert(m.getChildren()));
            }
            menuDtos.add(dto);
        });

        return menuDtos;
    }


    /**
     * 转树状结构
     *
     * @param menus
     * @return
     */
    private List<SysMenu> buildTreeMenu(List<SysMenu> menus) {
        ArrayList<SysMenu> finalMenus = new ArrayList<>();

        for (SysMenu menu : menus) {
            for (SysMenu e : menus) {
                if (menu.getId() == e.getParentId()) {
                    menu.getChildren().add(e);
                }
            }

            if (menu.getParentId() == 0L) {
                finalMenus.add(menu);
            }
        }
        System.out.println(finalMenus);


        return finalMenus;
    }
}
