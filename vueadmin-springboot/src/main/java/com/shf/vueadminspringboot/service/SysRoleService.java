package com.shf.vueadminspringboot.service;

import com.shf.vueadminspringboot.entity.SysRole;
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
public interface SysRoleService extends IService<SysRole> {

    List<SysRole> listRolesByUserId(Long id);
}
