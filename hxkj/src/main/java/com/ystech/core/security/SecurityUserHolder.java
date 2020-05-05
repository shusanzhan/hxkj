/**
 * 
 */
package com.ystech.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.User;



/**
 * @author shusanzhan
 * @date 2012-11-23
 */
public class SecurityUserHolder {
	public static User getCurrentUser(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null==authentication){
			return null;
		}
		if(authentication.getPrincipal() instanceof User){
			return (User) authentication.getPrincipal();
		}
		return null;
	}
	public static Enterprise getEnterprise(){
		User currentUser = SecurityUserHolder.getCurrentUser();
		if(null==currentUser){
			Enterprise company = new Enterprise();
			// 返回个临时对象，防止空指针
			company.setDbid(-1);
			return company;
		}
		Enterprise company = currentUser.getEnterprise();
		if (company != null) {
			return company;
		} else {
			company = new Enterprise();
			// 返回个临时对象，防止空指针
			company.setDbid(-1);
			return company;
		}
	}
}
