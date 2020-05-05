package com.ystech.core.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ystech.core.util.Md5;
import com.ystech.sys.model.User;


public class LoginFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// 获得在下面代码中要用的request,response,session对象
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		HttpServletResponse servletResponse = (HttpServletResponse) response;
		HttpSession session = servletRequest.getSession();
		User user = (User) session.getAttribute("user");
		// 获得用户请求的URI
		String path = servletRequest.getRequestURI();
		if(path.indexOf("/css/")>-1||path.indexOf("/img/")>-1||path.indexOf("/images/")>-1||path.indexOf(".js")>-1||
				path.indexOf("/widgets/")>-1||path.indexOf("/main/login")>-1||path.indexOf("/MP_verify_dGFQsO46hzGaKcLe.txt")>-1||path.indexOf("/main/modifyPassword")>-1||path.indexOf("/main/saveModifyPassword")>-1||
				path.indexOf("/pages/login/login.jsp")>-1||path.indexOf("/j_spring_security_check")>-1||path.indexOf("enterpriseAuth")>-1
				||path.indexOf("archives")>-1||path.indexOf("swfUpload")>-1||path.indexOf("enterpriseAssistant")>-1||path.indexOf("qywx")>-1
				||path.indexOf("Wechat")>-1||path.indexOf("wechat")>-1){
			chain.doFilter(servletRequest, servletResponse);
			 return ;
		}else{
			if(user!=null&&user.getDbid()>0){
				String calcMD5 = Md5.calcMD5("123456{"+user.getUserId()+"}"); 
				if(!user.getPassword().equals(calcMD5)){
					chain.doFilter(servletRequest, servletResponse);
					return ;
				}else{
					servletResponse.sendRedirect("/main/modifyPassword");
					return ;
				}
			}else{
				servletResponse.sendRedirect("/main/login");
			}
		}
		 return ;
	}

	public void destroy() {

	}

}