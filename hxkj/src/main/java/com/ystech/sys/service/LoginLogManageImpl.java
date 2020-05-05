/**
 * 
 */
package com.ystech.sys.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.sys.model.LoginLog;

/**
 * @author shusanzhan
 * @date 2013-6-22
 */
@Component("loginLogManageImpl")
public class LoginLogManageImpl extends HibernateEntityDao<LoginLog>{
	@SuppressWarnings("unchecked")
	public List<LoginLog> queryByIndex(Integer userId) {
		String sql="SELECT * FROM loginlog where userId=?  ORDER BY loginDate DESC LIMIT 6";
		List<LoginLog> list = executeSqlQuery(LoginLog.class, sql, new Object[]{userId}).list();
		return list;
	}
}
