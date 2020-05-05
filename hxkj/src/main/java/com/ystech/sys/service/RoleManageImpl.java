package com.ystech.sys.service;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Role;
@Component("roleManageImpl")
public class RoleManageImpl extends HibernateEntityDao<Role>{
	protected Logger log = Logger.getLogger(BaseController.class);
	
	private SystemInfoMangeImpl  systemInfoMangeImpl;
	
	@Resource
	public void setSystemInfoMangeImpl(SystemInfoMangeImpl systemInfoMangeImpl) {
		this.systemInfoMangeImpl = systemInfoMangeImpl;
	}
	/**
	 * 保存角色
	 */
	public void saveRole(Role role){
		Integer dbid = role.getDbid();
		if(dbid==null){
			role.setCreateTime(new Date());
			role.setModifyTime(new Date());
			save(role);
		}else{
			Role role2 = get(dbid);
			role2.setModifyTime(new Date());
			role2.setName(role.getName());
			role2.setRoleType(role.getRoleType());
			role2.setState(role.getState());
			save(role2);
		}
	}
	/**
	 * 功能描述：删除标签
	 * @param roleId
	 */
	public void deleteById(int roleId){
		Role role = get(roleId);
		delete(role);
	}
	
}
