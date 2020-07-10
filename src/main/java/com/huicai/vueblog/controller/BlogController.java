package com.huicai.vueblog.controller;


import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huicai.vueblog.common.lang.Result;
import com.huicai.vueblog.entity.Blog;
import com.huicai.vueblog.service.BlogService;
import com.huicai.vueblog.util.ShiroUtil;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2020-07-09
 */
@RestController
public class BlogController {
    @Autowired
    BlogService blogService;

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage){
        Page page=new Page(currentPage,5);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("created"));
        return Result.succ(pageData);
    }

    @GetMapping("/blog/{id}")
    public Result detail(@PathVariable(name="id")  Long id){
        Blog blog=blogService.getById(id);
        //参数值是否不为空值，如果为肯呢个就抛出IllegalArgumentException,不合法的参数异常
        Assert.notNull(blog,"该博客已被删除");
        return Result.succ(null);
    }
    //需要认证之后才能访问
    @RequiresAuthentication
    //修改和添加是同一体的
    @PostMapping("/blog/edit")
    public Result edit(@Validated @RequestBody Blog blog){
        //根据是否传入id，判断是修改还是添加
        Blog temp=null;
        if(blog.getId()!=null){
            //修改
          temp=blogService.getById(blog.getId());
          //只能编写自己的文章,设置权限，如果获取的用户id（m_blog表中的user_id）与程序登录时的用户id(m_user表中的id)一致，才可以进行修改博客的内容
          //不相等的时候抛出IllegalArgumentException异常
            Assert.isTrue(temp.getUserId().longValue()== ShiroUtil.getProfile().getId().longValue(),"你没有权限修改");


        }else{
            //删除
            temp=new Blog();
            //获取user_id---->用户登录的id
            temp.setUserId(ShiroUtil.getProfile().getId());
            //博客编写的时间，获取当时的系统时间
            temp.setCreated(LocalDateTime.now());
            //设置状态
            temp.setStatus(0);
        }
        BeanUtils.copyProperties(blog,temp,"id","userId","created","status");
        blogService.saveOrUpdate(temp);
        return Result.succ(null);
    }
}
