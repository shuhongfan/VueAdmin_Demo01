package com.shf.vueadminspringboot.service;

import com.shf.vueadminspringboot.common.dto.SysMenuDto;
import com.shf.vueadminspringboot.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shuhongfan
 * @since 2022-08-17
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取导航栏信息
     * @return
     */
    List<SysMenuDto> getCurrentUserNav();

    /**
     * 获取所有菜单信息
     * @return
     */
    List<SysMenu> tree();
}
