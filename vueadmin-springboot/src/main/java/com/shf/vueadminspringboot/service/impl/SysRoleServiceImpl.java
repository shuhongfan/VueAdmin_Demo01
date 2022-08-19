package com.shf.vueadminspringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shf.vueadminspringboot.entity.SysRole;
import com.shf.vueadminspringboot.mapper.SysRoleMapper;
import com.shf.vueadminspringboot.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shuhongfan
 * @since 2022-08-17
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public List<SysRole> listRolesByUserId(Long id) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<SysRole>()
                .inSql("id", "select role_id from sys_user_role where user_id=" + id);

        return list(wrapper);
    }
}
