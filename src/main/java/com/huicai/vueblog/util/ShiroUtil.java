package com.huicai.vueblog.util;

import com.huicai.vueblog.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;

public class ShiroUtil {
    public static AccountProfile getProfile(){
        return (AccountProfile)SecurityUtils.getSubject().getPrincipal();
    }
}
