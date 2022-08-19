package com.shf.vueadminspringboot.controller;


import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.shf.vueadminspringboot.common.lang.Const;
import com.shf.vueadminspringboot.common.lang.Result;
import com.shf.vueadminspringboot.entity.SysUser;
import sun.misc.BASE64Encoder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.UUID;

@RestController
public class AuthController extends BaseController{

    @Autowired
    private Producer producer;

    @GetMapping("/captcha")
    @SneakyThrows
    public Result captcha() {
        String key = UUID.randomUUID().toString();
        String code = producer.createText();

//        生成图片
        BufferedImage image = producer.createImage(code);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        BASE64Encoder encoder = new BASE64Encoder();
        String str = "data:image/jpeg;base64,";

        String base64Img = str + encoder.encode(outputStream.toByteArray());

        redisUtil.hset(Const.CAPTCHA_KEY, key, code, 120);

        return Result.succ(MapUtil.builder()
                .put("token", key)
                .put("captchaImg", base64Img)
                .build());
    }

    /**
     * 获取用户信息接口
     * @param principal
     * @return
     */
    @GetMapping("/sys/userInfo")
    public Result userInfo(Principal principal) {
        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        return Result.succ(MapUtil.builder()
                .put("id", sysUser.getId())
                .put("username", sysUser.getUsername())
                .put("avatar", sysUser.getAvatar())
                .put("created", sysUser.getCreated())
                .map());
    }
}
