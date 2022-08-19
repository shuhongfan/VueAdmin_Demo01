package com.shf.vueadminspringboot.security;

import com.shf.vueadminspringboot.common.exception.CaptchaException;
import com.shf.vueadminspringboot.common.lang.Const;
import com.shf.vueadminspringboot.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if ("/login".equals(uri) && request.getMethod().equalsIgnoreCase("POST")) {
//            校验验证码
            try {
                validate(request);
            } catch (CaptchaException e) {
                loginFailureHandler.onAuthenticationFailure(request, response, e);
            }

        }

        filterChain.doFilter(request, response);
    }

    /**
     * 校验验证码
     *
     * @param request
     */
    private void validate(HttpServletRequest request) {
        String code = request.getParameter("code");
        String token = request.getParameter("token");

        if (StringUtils.isBlank(code) || StringUtils.isBlank(token)) {
            throw new CaptchaException("验证码错误");
        }

        if (!code.equals(redisUtil.hget(Const.CAPTCHA_KEY, token))) {
            throw new CaptchaException("验证码错误");
        }

//        一次性使用
        redisUtil.hdel(Const.CAPTCHA_KEY, token);
    }
}
