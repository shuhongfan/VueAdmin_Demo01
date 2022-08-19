package com.shf.vueadminspringboot.security;

import cn.hutool.core.util.StrUtil;
import com.shf.vueadminspringboot.entity.SysUser;
import com.shf.vueadminspringboot.service.SysUserService;
import com.shf.vueadminspringboot.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private SysUserService sysUserService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = request.getHeader(jwtUtils.getHeader());
        if (StrUtil.isBlankOrUndefined(jwt)) { // 没有jwt
            chain.doFilter(request,response);
            return;
        }

        Claims claim = jwtUtils.getClaimByToken(jwt);
        if (claim == null) {
            throw new JwtException("token 异常");
        }
        if (jwtUtils.isTokenExpired(claim)) {
            throw new JwtException("token已过期");
        }

        String username = claim.getSubject();
        SysUser sysUser = sysUserService.getByUsername(username);
//        获取用户权限等信息
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        userDetailService.getUserAuthority(sysUser.getId())); // 权限

//        自动登录
        SecurityContextHolder.getContext().setAuthentication(token);
//        放行
        chain.doFilter(request,response);
    }
}
