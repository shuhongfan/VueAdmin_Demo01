package com.shf.vueadminspringboot.mapper;

import com.shf.vueadminspringboot.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shuhongfan
 * @since 2022-08-17
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 获取菜单操作编码
     *
     * @param userId
     * @return
     */
    List<Long> getNavMenuIds(@Param("userId") Long userId);

    List<SysUser> listByMenuId(@Param("menuId") Long menuId);
}
