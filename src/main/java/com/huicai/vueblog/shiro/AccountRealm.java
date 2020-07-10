package com.huicai.vueblog.shiro;

import com.huicai.vueblog.entity.User;
import com.huicai.vueblog.service.UserService;
import com.huicai.vueblog.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserService userService;
    @Override
    public boolean supports(AuthenticationToken token){
        return token instanceof JwtToken;
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
       //获取到jwt
        JwtToken jwtToken=(JwtToken)token;
        //解析jwt
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        User user = userService.getById(Long.valueOf(userId));
        if(user==null){
            throw new UnknownAccountException("账户不存在");
        }
        if (user.getStatus()==-1){
            throw new LockedAccountException("账户被锁定");
        }
        //将可返回的用户信息进行封装
        AccountProfile profile=new AccountProfile();
        BeanUtils.copyProperties(user,profile);
        //jwtToken.getCredentials()秘钥
        return new SimpleAuthenticationInfo(profile,jwtToken.getCredentials(),getName());
    }
}
