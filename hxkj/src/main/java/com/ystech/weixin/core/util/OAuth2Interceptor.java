package com.ystech.weixin.core.util;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.ystech.weixin.model.WeixinGzuserinfo;
import com.ystech.weixin.service.WeixinGzuserinfoManageImpl;

public class OAuth2Interceptor extends AbstractInterceptor {
	private WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl;
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		String resultUrl = request.getRequestURI();
		//通过判断连接参数中是否包含state=no参数 确定是否拦截连接地址获取关注用户信息
		String queryString = request.getQueryString();
		if(null!=queryString){
			if(queryString.contains("state=no")){
				return invocation.invoke();
			}
		}
		
		WeixinGzuserinfo weixinGzuserinfo = weixinGzuserinfoManageImpl.findUniqueBy("openid", "oVrJCuOcJcSUtqFKabDqwzZIVUWc");
		//WeixinGzuserinfo weixinGzuserinfo = (WeixinGzuserinfo)session.getAttribute("weixinGzuserinfo");
		//第一步：先判断session中是否包含user，包含user直接跳转到目标页面
		if(null!=weixinGzuserinfo){
			session.setAttribute("weixinGzuserinfo", weixinGzuserinfo);
			session.setAttribute("member", weixinGzuserinfo.getMember());
			return invocation.invoke();
		}else{
			//第二步：判断session中不含user，通过cookie获取user，如果cookie中不包含user，在向微信端发送请求
			WeixinGzuserinfo weixinGzuserinfo2 = getCookie(request, weixinGzuserinfoManageImpl);
			if(null!=weixinGzuserinfo2){
				System.out.println("===============:Cookie"+weixinGzuserinfo2.getOpenid());
				session.setAttribute("weixinGzuserinfo",weixinGzuserinfo2);
				return invocation.invoke();  
			}else{
				if(null!=resultUrl){
					resultUrl=resultUrl.replace("!", "/").replace(".action", "");
				}
	            String param=request.getQueryString();  
	            if(param!=null){  
	                resultUrl+= "?" + param;  
	            }  
	            
	            //解析参数code并设置到session里面
	            if(null!=queryString){
	            	Map<String, String[]> values = new HashMap<String, String[]>();  
	            	RequestUtil.parseParameters(values, queryString, "UTF-8");
	            	String[] code = values.get("code");
	            	String codeValue="";
	            	if(code.length>0){
	            		for (String string : code) {
	            			codeValue=codeValue+string;
						}
	            	}
	            	System.out.println("code:"+codeValue);
	            	session.setAttribute("code", codeValue);
	            }
	            
	            //设置目标跳转连接，后续通过微信端获取到user后在进行页面跳转
	            session.setAttribute("resultUrl", resultUrl);
	            //请求的路径  
	            String contextPath=request.getContextPath();  
	            response.sendRedirect(contextPath + "/wechatAuth/oAuth2");
			}
		}
		
		return null;
	}

	// 得到cookie  
    public static WeixinGzuserinfo getCookie(HttpServletRequest request, WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl) {  
        Cookie[] cookies = request.getCookies();  
        if (cookies != null) {  
            for (Cookie cookie : cookies) {  
                if (CookieUtile.GZUI_COOKIE.equals(cookie.getName())) {  
                    String value = cookie.getValue();  
                    if (StringUtils.isNotBlank(value)) {  
                        if(null!=value&&value.trim().length()>0){
                        	WeixinGzuserinfo weixinGzuserinfo = weixinGzuserinfoManageImpl.findUniqueBy("openid", value);
                        	if (weixinGzuserinfo != null) {  
                        		return weixinGzuserinfo;
                        	}  
                        }
                    }  
                }  
            }  
        }  
        return null;  
    }
    @Resource
	public void setWeixinGzuserinfoManageImpl(
			WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl) {
		this.weixinGzuserinfoManageImpl = weixinGzuserinfoManageImpl;
	}
}
