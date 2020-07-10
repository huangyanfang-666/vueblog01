package com.huicai.vueblog.common.exception;

import com.huicai.vueblog.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //返回状态码，提供给前端
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e){
        log.error("运行时异常------",e);
        return Result.fail((e.getMessage()));
    }
    //shiro的异常
    //返回状态码，提供给前端
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = ShiroException.class)
    public Result handler(ShiroException e){
        log.error("shiro异常------",e);
        return Result.fail(401,e.getMessage(),null);
    }
    //捕获实体校验异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e){
        log.error("实体校验异常------",e);
        //将异常信息进行处理，值拿出必要的信息进行展示
        BindingResult bindingResult=e.getBindingResult();
        //如果有多个校验异常，只抛出第一个
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();

        return Result.fail(objectError.getDefaultMessage());
    }

    //补货异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalAccessException.class)
    public Result handler(IllegalAccessException e){
        log.error("没有访问权限：------",e);
        return Result.fail(e.getMessage());
    }
}
