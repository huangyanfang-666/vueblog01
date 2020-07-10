package com.huicai.vueblog.controller;


import com.baomidou.mybatisplus.extension.api.R;
import com.huicai.vueblog.common.lang.Result;
import com.huicai.vueblog.entity.User;
import com.huicai.vueblog.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2020-07-09
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @RequiresAuthentication
    @GetMapping("/index")
    public Object index(){
        User user=userService.getById(1);
        return Result.succ(user);
    }

    @PostMapping("/save")
    public Result save(@RequestBody User user){
        return Result.succ(user);
    }
}
