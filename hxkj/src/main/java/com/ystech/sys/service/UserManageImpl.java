package com.ystech.sys.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.security.service.UserDetailsManageImpl;
import com.ystech.core.util.CommState;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.Role;
import com.ystech.sys.model.User;
@Component("userManageImpl")
public class UserManageImpl extends HibernateEntityDao<User>{
	private RoleManageImpl roleManageImpl;
	private SystemInfoMangeImpl systemInfoMangeImpl;
	private UserDetailsManageImpl userDetailsManageImpl;
	@Resource
	public void setRoleManageImpl(RoleManageImpl roleManageImpl) {
		this.roleManageImpl = roleManageImpl;
	}
	@Resource
	public void setSystemInfoMangeImpl(SystemInfoMangeImpl systemInfoMangeImpl) {
		this.systemInfoMangeImpl = systemInfoMangeImpl;
	}
	@Resource
	public void setUserDetailsManageImpl(UserDetailsManageImpl userDetailsManageImpl) {
		this.userDetailsManageImpl = userDetailsManageImpl;
	}
	public User saveUserRole(Integer userId,Integer[] roleIds,Integer[] companyIds){
		Set<Role> roles=new HashSet<Role>();
		User user2 = get(userId);
		Set<Role> resRoles = user2.getRoles();
		
		roles.clear();
		if(null!=roleIds&&roleIds.length>0){
			for (Integer roId : roleIds) {
				Role role = roleManageImpl.get(roId);
				roles.add(role);
			}
		}
		user2.setRoles(roles);
		//设置查询其他公司数据权限
		if(null!=companyIds&&companyIds.length>0){
			String compIds=new String();
			for (Integer companyId : companyIds) {
				compIds=compIds+companyId+",";
			}
			int lastIndexOf = compIds.lastIndexOf(",");
			String substring = compIds.substring(0, lastIndexOf);
			user2.setCompnayIds(substring);
		}
		save(user2);
		userDetailsManageImpl.loadUserByUsername(user2.getUserId());
		return user2;
	}
	/**
	 * @param userName
	 * @param pa
	 */
	public boolean checkUser(String userName, String pa) {
		return true;
	}
	/**
	 * 功能描述：获取人员选择器，所有人员的下来框
	 * @return
	 */
	public String getAllPerson(){
		Enterprise enterprise = SecurityUserHolder.getEnterprise();
		List<User> users = find("from User where enterprise.dbid=?", new Object[]{enterprise.getDbid()});
		String userSelect="";
		if (null!=users&&users.size()>0) {
			for (User user : users) {
				userSelect+="<option value='"+user.getDbid()+"us'>"+user.getRealName()+"</option>";
			}
		}
		return userSelect;
	}
	
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public List<User> queryUsersReadNew(String userIds) throws Exception {
		String sql="SELECT * FROM user WHERE dbid in ("+userIds+")";
		List<User> list = getSession().createSQLQuery(sql).addEntity(User.class).list();
		return list;
	}
	/**
	 * @param realName
	 * @param departmentId
	 * @return
	 */
	public Page<User> queryContact(Integer pageNo,Integer pageSize,String realName, Integer departmentId) {
		String sql="select * from user where 1=1 ";
		List param=new ArrayList();
		if(null!=realName&&realName.trim().length()>0){
			sql=sql+" and realName like ?";
			param.add("%"+realName+"%");
		}
		if(departmentId>1){
			sql=sql+" and  FIND_IN_SET(?,departmentIds)";
			param.add(departmentId);
		}
		Page pagedQuerySql = pagedQuerySql(pageNo, pageSize, User.class, sql, param.toArray());
		return pagedQuerySql;
	}
	/**
	 * 功能描述：查询导出数据
	 * @param realName 查询人姓名
	 * @param departmentId 查询部门名称
	 * @return
	 */
	public List<User> queryContactExcel(String realName, Integer departmentId) {
		String sql="select * from user where 1=1 ";
		List param=new ArrayList();
		if(null!=realName&&realName.trim().length()>0){
			sql=sql+" and realName like ?";
			param.add("%"+realName+"%");
		}
		if(departmentId>1){
			sql=sql+" and  FIND_IN_SET(?,departmentIds)";
			param.add(departmentId);
		}
		List<User> user = executeSql(sql, param.toArray());
		return user;
	}
	public String getUserIds(Integer dbid) {
		StringBuffer buff=new StringBuffer();
		List<User> users = findBy("parent.dbid", dbid);
		if(null!=users&&users.size()>0){
			for (User userSub : users) {
				buff.append(userSub.getDbid()+",");
			}
			buff = buff.append(dbid);
		}else{
			buff.append(dbid);
		}
		return buff.toString();
	}
	/**
	 * 停用经销商账号
	 * @param enterprise
	 */
	public void updateStopUser(Enterprise enterprise){
		String uselSql="UPDATE sys_user SET userState="+CommState.STATE_STOP+" WHERE enterpriseId="+enterprise.getDbid();
		executeSql(uselSql);
	}
	/**
	 * 功能描述：
	 * @param enterprise
	 */
	public void updateStartUser(Enterprise enterprise){
		String uselSql="UPDATE sys_user SET userState="+CommState.STATE_NORMAL+" WHERE enterpriseId="+enterprise.getDbid();
		executeSql(uselSql);
	}
	public List<User> findBySendWechatMessageUser(Enterprise enterprise){
		String sql="select * from sys_user where 1=1 AND  positionNames like  '%销售经理%' AND enterpriseId="+enterprise.getDbid();
		List<User> users = executeSql(sql, null);
		return users;
	}
}
