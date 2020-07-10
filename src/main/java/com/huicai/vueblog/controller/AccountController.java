package com.huicai.vueblog.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.huicai.vueblog.common.dto.LoginDto;
import com.huicai.vueblog.common.lang.Result;
import com.huicai.vueblog.entity.User;
import com.huicai.vueblog.service.UserService;
import com.huicai.vueblog.util.JwtUtils;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {
    @Autowired
    UserService userService;
    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response){
       User user= userService.getOne(new QueryWrapper<User>().eq("username",loginDto.getUsername()));
       //如果用户名为空，则提示用户不存在：抛出IllegalAccessException异常
        Assert.notNull(user,"用户不存在");
        //判断用户和密码是否正确
        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
            return Result.fail("密码不正确");
        }
            //密码正确，则生成jwt
            String jwt = jwtUtils.generateToken(user.getId());
            //将jwt返回到header里面
            response.setHeader("Authorizatin",jwt);
            response.setHeader("Access-control-Expose-Headers","Authorizatin");

       return Result.succ(MapUtil.builder()
               .put("id",user.getId())
               .put("username",user.getUsername())
               .put("avatar",user.getAvatar())
               .put("email",user.getEmail())
               .map()
       );
    }
    //权限
    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout(){
        SecurityUtils.getSubject().logout();

        return Result.succ(null);
    }
}
