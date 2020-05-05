package com.ystech.weixin.core.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.ystech.sys.model.User;
import com.ystech.weixin.model.WeixinGzuserinfo;

public class CookieUtile {
	public static final String USER_COOKIE = "user";  
	public static final String GZUI_COOKIE = "openid";  
	public static final String USER_SESSION = "user";  
	 // 添加一个cookie  
    public static Cookie addCookie(User user) { 
        Cookie cookie = new Cookie(USER_COOKIE, user.getDbid()+"");
        //设置cookie作用域
        cookie.setDomain("cdryqc.com");
        //设置cookie有效路径
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);// cookie保存两周  
        return cookie;  
    }
    // 添加一个cookie  
    public static Cookie addCookie(WeixinGzuserinfo weixinGzuserinfo) { 
    	Cookie cookie = new Cookie(GZUI_COOKIE, weixinGzuserinfo.getOpenid()+"");
    	//设置cookie作用域
    	cookie.setDomain("cdryqc.com");
    	//设置cookie有效路径
    	cookie.setPath("/");
    	cookie.setMaxAge(60 * 60 * 24 * 14);// cookie保存两周  
    	return cookie;  
    }
    
 // 删除cookie  
    public Cookie delCookie(HttpServletRequest request) {  
        Cookie[] cookies = request.getCookies();  
        if (cookies != null) {  
            for (Cookie cookie : cookies) {  
                if (USER_COOKIE.equals(cookie.getName())) {  
                    cookie.setValue("");  
                    cookie.setMaxAge(0);  
                    return cookie;  
                }  
            }  
        }  
        return null;  
    }  
}
