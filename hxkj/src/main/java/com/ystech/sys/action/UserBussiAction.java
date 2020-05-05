package com.ystech.sys.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.security.service.UserDetailsManageImpl;
import com.ystech.core.util.Md5;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Area;
import com.ystech.sys.model.Department;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.Role;
import com.ystech.sys.model.SystemInfo;
import com.ystech.sys.model.User;
import com.ystech.sys.service.AreaManageImpl;
import com.ystech.sys.service.DepartmentManageImpl;
import com.ystech.sys.service.EnterpriseManageImpl;
import com.ystech.sys.service.RoleManageImpl;
import com.ystech.sys.service.SystemInfoMangeImpl;
import com.ystech.sys.service.UserManageImpl;

@Component("userBussiAction")
@Scope("prototype")
public class UserBussiAction extends BaseController{
	private Enterprise enterprise;
	private User user;
	private UserManageImpl userManageImpl;
	private RoleManageImpl roleManageImpl;
	private UserDetailsManageImpl userDetailsManageImpl;
	private DepartmentManageImpl departmentManageImpl;
	private AreaManageImpl areaManageImpl;
	private EnterpriseManageImpl enterpriseManageImpl;
	private SystemInfoMangeImpl systemInfoMangeImpl;
	@Resource
	public void setUserManageImpl(UserManageImpl userManageImpl) {
		this.userManageImpl = userManageImpl;
	}
	@Resource
	public void setRoleManageImpl(RoleManageImpl roleManageImpl) {
		this.roleManageImpl = roleManageImpl;
	}
	@Resource
	public void setAreaManageImpl(AreaManageImpl areaManageImpl) {
		this.areaManageImpl = areaManageImpl;
	}
	@Resource
	public void setEnterpriseManageImpl(EnterpriseManageImpl enterpriseManageImpl) {
		this.enterpriseManageImpl = enterpriseManageImpl;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Resource
	public void setUserDetailsManageImpl(UserDetailsManageImpl userDetailsManageImpl) {
		this.userDetailsManageImpl = userDetailsManageImpl;
	}
	@Resource
	public void setDepartmentManageImpl(DepartmentManageImpl departmentManageImpl) {
		this.departmentManageImpl = departmentManageImpl;
	}
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	@Resource
	public void setSystemInfoMangeImpl(SystemInfoMangeImpl systemInfoMangeImpl) {
		this.systemInfoMangeImpl = systemInfoMangeImpl;
	}
	/**
	 * 功能描述：商家用户管理列表
	 * @return
	 * @throws Exception
	 */
	public String queryBussiList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		String userId = request.getParameter("userId");
		String userName = request.getParameter("userName");
		Integer departmentId = ParamUtil.getIntParam(request, "departmentId", -1);
		Integer adminType = ParamUtil.getIntParam(request, "adminType", -1);
		Integer userState = ParamUtil.getIntParam(request, "userState", 1);
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			String departmentIds=null;
			Page<User> page=null;
			String sql="select * from sys_user where 1=1 ";
			List params=new ArrayList();
			if(null!=enterprise&&enterprise.getDbid()>0){
				sql=sql+" AND enterpriseId="+enterprise.getDbid();
			}
			if(null!=userId&&userId.trim().length()>0){
				sql=sql+" and userId like ? ";
				params.add("%"+userId+"%");
			}
			if(null!=userName&&userName.trim().length()>0){
				sql=sql+" and realName like ?";
				params.add("%"+userName+"%");
			}
			if(adminType>0){
				sql=sql+" and adminType=? ";
				params.add(adminType);
			}
			if(userState>0){
				sql=sql+" and userState=? ";
				params.add(userState);
			}
			if(departmentId>0){
				departmentIds = departmentManageImpl.getDepartmentIdsByDbid(departmentId);
				sql=sql+" and departmentId in("+departmentIds+")";
				request.setAttribute("departmentId", departmentId);
			}
			page = userManageImpl.pagedQuerySql(pageNo, pageSize,User.class,sql,params.toArray());
			List<User> result = page.getResult();
			request.setAttribute("page", page);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "bussiList";
	}
	/**
	 * 功能描述：添加商家用户
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String edit(){
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			if(dbid>0){
				User user = userManageImpl.get(dbid);
				request.setAttribute("userAdmin", user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "系统发生未知错误!");
			return "error";
		}
		return "edit";
	}
	/**
	 * 功能描述：保存普通用户信息
	 * @throws Exception
	 */
	public void save(){
		HttpServletRequest request = this.getRequest();
		String[] departmentIds = request.getParameterValues("departmentIds");
		String[] positIds = request.getParameterValues("positionIds");
		String[] posiNames = request.getParameterValues("positionNames");
		Integer enterpriseId = ParamUtil.getIntParam(request, "enterpriseId", -1);
	    Integer type = ParamUtil.getIntParam(request, "type", -1);
		String message="保存数据成功！";
		try{
			if(null!=departmentIds&&departmentIds.length>0){
				if(departmentIds.length>1){
					renderErrorMsg(new Throwable("只能选择一个部门，请选择部门"), "");
					return ;
				}
			}else{
				renderErrorMsg(new Throwable("请选择部门信息"), "");
				return ;
			}
			if(null==positIds||positIds.length<=0){
				renderErrorMsg(new Throwable("请选岗位信息"), "");
				return ;
			}
			String positionIds="";
			String positionNames="";
			int i=0;
			for (String positId : positIds) {
				positionIds=positionIds+positId+",";
				positionNames=positionNames+posiNames[i]+",";
				i++;
			}
			//用户配置信息
			SystemInfo systemInfo=null;
			List<SystemInfo> systemInfos = systemInfoMangeImpl.getAll();
			if(null!=systemInfos){
				systemInfo=systemInfos.get(0);
			}
			user.setPositionIds(positionIds);
			user.setPositionNames(positionNames);
			Enterprise enterprise = enterpriseManageImpl.get(enterpriseId);
			if(null==enterprise){
				enterprise=SecurityUserHolder.getEnterprise();
			}
			if(null==enterprise||enterprise.getDbid()<0){
				renderErrorMsg(new Throwable("创建用户失败，无经销商信息！"), "");
				return ;
			}
			//保存用户信息
			String calcMD5 = Md5.calcMD5("123456{"+user.getUserId()+"}");
			Integer dbid = user.getDbid();
			String depIs = departmentIds[0];
			if(null!=depIs){
				Department department = departmentManageImpl.get(Integer.parseInt(depIs));
				user.setDepartment(department);
			}
			if(null==dbid||dbid<=0){
				user.setPassword(calcMD5);
				user.setEnterprise(enterprise);
				user.setUserState(1);
				user.setMobilePhone(user.getUserId());
				user.setApprovalCpidStatus(User.APPROVALCOMM);
				user.setAdminType(User.ADMINTYPEADCOMM);
				user.setQueryOtherDataStatus(User.QUERYCOMM);
				user.setSysWeixinStatus(User.SYNCOMM);
				user.setAttentionStatus(User.ATTATIONSTATUSDEFUAL);
				userManageImpl.save(user);
			}else{
				User user2 = userManageImpl.get(dbid);
				user2.setEmail(user.getEmail());
				user2.setMobilePhone(user.getUserId());
				user2.setPhone(user.getPhone());
				user2.setWechatId(user.getWechatId());
				user2.setRealName(user.getRealName());
				user2.setUserId(user.getUserId());
				user2.setDepartment(user.getDepartment());
				user2.setQq(user.getQq());
				user2.setPositionIds(user.getPositionIds());
				user2.setEnterprise(enterprise);
				user2.setPositionNames(user.getPositionNames());
				user2.setBussiType(user.getBussiType());
				userManageImpl.save(user2);
			}
		}catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return; 
		}
		if(type==1){
			renderMsg("/userBussi/queryBussiList", message);
		}
		if(type==2){
			renderMsg("/userBussi/userRoleAdd?dbid="+user.getDbid(), message);
		}
		return ;
	}
	/**系统配置角色**/
	public String userRole() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {
			Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
			if(dbid>0){
				User user2=userManageImpl.get(dbid);
				request.setAttribute("user2", user2);
				String compnayIds = user2.getCompnayIds();
				if(null!=compnayIds&&compnayIds.trim().length()>0){
					String[] compnayArrarys = compnayIds.split(",");
					request.setAttribute("compnayArrarys", compnayArrarys);
				}
			}
			List<Enterprise> enterprises = enterpriseManageImpl.getAll();
			request.setAttribute("enterprises", enterprises);
			List<SystemInfo> systemInfos = systemInfoMangeImpl.getAll();
			if(null!=systemInfos&&systemInfos.size()>0){
				SystemInfo systemInfo = systemInfos.get(0);
				request.setAttribute("systemInfo", systemInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return "userRole";
	}
	/**系统配置角色**/
	public String userRoleAdd() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {
			Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
			if(dbid>0){
				User user2=userManageImpl.get(dbid);
				request.setAttribute("user2", user2);
				String compnayIds = user2.getCompnayIds();
				if(null!=compnayIds&&compnayIds.trim().length()>0){
					String[] compnayArrarys = compnayIds.split(",");
					request.setAttribute("compnayArrarys", compnayArrarys);
				}
			}
			List<Enterprise> enterprises = enterpriseManageImpl.getAll();
			request.setAttribute("enterprises", enterprises);
			List<SystemInfo> systemInfos = systemInfoMangeImpl.getAll();
			if(null!=systemInfos&&systemInfos.size()>0){
				SystemInfo systemInfo = systemInfos.get(0);
				request.setAttribute("systemInfo", systemInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return "userRoleAdd";
	}
	/**
	 * 功能描述：保存用户权限
	 * 参数描述：
	 * 逻辑描述：
	 * @param user
	 */
	public void saveUserRole() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		Integer type = ParamUtil.getIntParam(request, "type", 1);
		//合同审批权限
		Integer approvalCpidStatus = ParamUtil.getIntParam(request, "approvalCpidStatus", -1);
		//查询其他公司数据权限
		Integer queryOtherDataStatus = ParamUtil.getIntParam(request, "queryOtherDataStatus", -1);
		//利润审批权限金额
		Integer approvalMoney = ParamUtil.getIntParam(request, "approvalMoney", 0);
		//领导自己审批自己权限
		Integer selfApproval = ParamUtil.getIntParam(request, "selfApproval", -1);
		Integer[] roleIds = ParamUtil.getIntArray(request, "id");
		Integer[] companyIds = ParamUtil.getIntArray(request, "companyId");
		Set<Role> roles=new HashSet<Role>();
		try{
			if(dbid>0){
				User user2 = userManageImpl.saveUserRole(dbid, roleIds, companyIds);
				if(type==1){
					renderMsg("/userBussi/queryBussiList", dbid+"权限分配成功！");
				}
				if(type==2){
					renderMsg("/userBussi/wechatRole?userId="+dbid, user2.getUserId()+"权限分配成功！");
				}
			}else{
				renderErrorMsg(new Throwable("请选择分配权限用户"), "");
				return ;
			}
		}catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		return ;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String wechatRole() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer userId = ParamUtil.getIntParam(request, "userId", -1);
		try {
			User user2 = userManageImpl.get(userId);
			request.setAttribute("user", user2);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "wechatRole";
	}
	/**
	 * 功能描述：删除用户
	 * 参数描述：
	 * 逻辑描述：
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		if(null!=dbids&&dbids.length>0){
			try {
				//用户配置信息
				SystemInfo systemInfo=null;
				List<SystemInfo> systemInfos = systemInfoMangeImpl.getAll();
				if(null!=systemInfos){
					systemInfo=systemInfos.get(0);
				}
				for (Integer dbid : dbids) {
					User user = userManageImpl.get(dbid);
					if(user.getUserId().equals("super")){
						renderErrorMsg(new Throwable("超级管理员不能删除！"), "");
						return ;
					}
					userManageImpl.deleteById(dbid);
				}
			} catch (Exception e) {
				e.printStackTrace();
				renderErrorMsg(e, "");
				return ;
			}
		}
		String query = ParamUtil.getQueryUrl(request);
		renderMsg("/userBussi/queryBussiList"+query, "删除数据成功！");
		return;
	}
	public void userRoleJson() throws JSONException {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			user=userManageImpl.get(dbid);
			Set<Role> roles = user.getRoles();
			JSONArray makeJson = makeJson(roles,user);
			renderJson(makeJson.toString());
			return ;
		}
		renderJson("1");
		return ;
	}
	private JSONArray makeJson(Set<Role> roles,User user) throws JSONException{
		User currentUser = SecurityUserHolder.getCurrentUser();
		List<Role> roList=new ArrayList<Role>(); 
		if(currentUser.getUserId().equals("super")){
			roList = roleManageImpl.find("from Role where state=? ", new Object[]{1});
		}else{
			roList = roleManageImpl.find("from Role where state=? AND dbid>=10 ", new Object[]{1});
		}
		JSONArray jsonArray=null;
		if(null!=roList&&roList.size()>0){
			jsonArray=new JSONArray();
			for (Role role : roList) {
				JSONObject object=new JSONObject();
				object.put("dbid", role.getDbid());
				object.put("name", role.getName());
				if(null!=roles&&roles.size()>0){
					for (Role role2 : roles) {
						if(role.getDbid()==role2.getDbid()){
							object.put("checked", true);
							break;
						}
					}
				}
				jsonArray.put(object);
			}
		}
		return jsonArray;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void resetPassword() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try{
			if(dbid>0){
				User user2 = userManageImpl.get(dbid);
				String password = Md5.calcMD5("123456{"+user2.getUserId()+"}");
				user2.setPassword(password);
				userManageImpl.save(user2);
			}else{
				renderErrorMsg(new Throwable("请选择操作数据"), "");
				return ;
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		String query = ParamUtil.getQueryUrl(request);
		renderMsg("/userBussi/queryBussiList"+query, "设置成功！");
	}
	/**
	 * @param memberInfo2
	 */
	private String areaSelect(Area area) {
		StringBuffer buffer=new StringBuffer();
		if(null!=area){
			String treePath = area.getTreePath();
			String[] split = treePath.split(",");
			if(split.length>1){
				//父节点
				List<Area> areas = areaManageImpl.find("from Area where area.dbid IS NULL", new Object[]{});
				if(null!=areas&&areas.size()>0){
					buffer.append("<select id='ar' name='ar' class='midea text' onchange='ajaxArea(this)'>");
					buffer.append("<option>请选择...</option>");
					for (Area ar : areas) {
						if(ar.getDbid()==Integer.parseInt(split[1])){
							buffer.append("<option selected='selected' value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
						}else{
							buffer.append("<option value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
						}
					}
					buffer.append("</select> ");
				}
				for (int i=2; i<split.length ; i++) {
					List<Area> areas2 = areaManageImpl.findBy("area.dbid",Integer.parseInt(split[i-1]));
					if(null!=areas2&&areas2.size()>0){
						buffer.append("<select id='ar' name='ar' class='midea text' onchange='ajaxArea(this)'>");
						buffer.append("<option>请选择...</option>");
						for (Area ar : areas2) {
							if(ar.getDbid()==Integer.parseInt(split[i])){
								buffer.append("<option selected='selected' value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
							}else{
								buffer.append("<option value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
							}
						}
						buffer.append("</select> ");
					}
				}
				//最后一个下拉框
				List<Area> areas2 = areaManageImpl.findBy("area.dbid",Integer.parseInt(split[split.length-1]));
				if(null!=areas2&&areas2.size()>0){
					buffer.append("<select id='ar' name='ar' class='midea text' onchange='ajaxArea(this)'>");
					buffer.append("<option>请选择...</option>");
					for (Area ar : areas2) {
						if(ar.getDbid()==area.getDbid()){
							buffer.append("<option selected='selected' value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
						}else{
							buffer.append("<option value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
						}
					}
					buffer.append("</select> ");
				}
			}
		}else{
			return null;
		}
		return buffer.toString();
	}
	/**
	 * 功能描述：启用或者停用用户
	 */
	public void stopOrStartUser() {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		String query = ParamUtil.getQueryUrl(request);
		try{
			if(dbid>0){
				User user2 = userManageImpl.get(dbid);
				Integer userState = user2.getUserState();
				if(null!=userState){
					if(userState==1){
						user2.setUserState(2);
					}else if(userState==2){
						user2.setUserState(1);
					}
				}
				userManageImpl.save(user2);
				boolean synQywx = systemInfoMangeImpl.isSynQywx();
				
			}else{
				renderErrorMsg(new Throwable("请选择操作数据"), "");
				return ;
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
		}
		
		renderMsg("/userBussi/queryBussiList"+query, "设置成功！");
	}
	/**
	 * 功能描述：修改密码
	 * @return
	 * @throws Exception
	 */
	public String modifyPassword() throws Exception {
		User user = SecurityUserHolder.getCurrentUser();
		HttpServletRequest request = this.getRequest();
		if(null!=user&&user.getDbid()>0){
			User user2 = userManageImpl.get(user.getDbid());
			request.setAttribute("user", user2);
		}
		return "modifyPassword";
	}
	/**
	 * 功能描述：修改密码
	 * @return
	 * @throws Exception
	 */
	public void updateModifyPassword() throws Exception {
		HttpServletRequest request = getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		String oldPassword = request.getParameter("oldPassword");
		String password = request.getParameter("password");
		if(null==oldPassword||oldPassword.trim().length()<=0){
			renderErrorMsg(new Throwable("输入旧密码错误！"), "");
			return ;
		}
		if(null==password||password.trim().length()<=0){
			renderErrorMsg(new Throwable("密码输入错误！"), "");
			return ;
		}
		try{
			if (dbid>0) {
				User user2 = userManageImpl.get(dbid);
				String password2 = user2.getPassword();
				String calcMD5 = Md5.calcMD5(oldPassword+"{"+user2.getUserId()+"}");
				if(password2.equals(calcMD5)){
					user2.setPassword(Md5.calcMD5(password+"{"+user2.getUserId()+"}"));
					userManageImpl.save(user2);
				}else{
					renderErrorMsg(new Throwable("旧密码输入错误！"), "");
					return ;	
				}
			} else {
				renderErrorMsg(new Throwable("操作数据错误！"), "");
				return ;
			}	
		}catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(new Throwable("操作数据错误！"), "");
			return ;
		}
		renderMsg("/userBussi/modifyPassword", "修改密码成功！");
		return ;
	}
	/**
	 * 功能描述：个人设置-设置用户信息
	 * @return
	 * @throws Exception
	 */
	public String editSelf() throws Exception {
		User user = SecurityUserHolder.getCurrentUser();
		HttpServletRequest request = this.getRequest();
		if(null!=user&&user.getDbid()>0){
			User user2 = userManageImpl.get(user.getDbid());
			request.setAttribute("user", user2);
		}
		return "editSelf";
	}
	/**
	 * 功能描述：保存用户信息
	 * @throws Exception
	 */
	public void saveEditSelf() throws Exception {
		try{
			//保存用户信息
			Integer dbid = user.getDbid();
			User user2 = userManageImpl.get(dbid);
			user2.setEmail(user.getEmail());
			user2.setMobilePhone(user.getMobilePhone());
			user2.setPhone(user.getPhone());
			user2.setRealName(user.getRealName());
			user2.setQq(user.getQq());
			user2.setWechatId(user.getWechatId());
			user2.setUserId(user.getUserId());
			userManageImpl.save(user2);
			this.getRequest().getSession().setAttribute("user", user2);
		}catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/userBussi/editSelf", "保存数据成功！");
		return ;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxUser(){
		HttpServletRequest request = this.getRequest();
		try{
			String pingyin = request.getParameter("q");
			Integer limit = ParamUtil.getIntParam(request, "limit", 10);
			if(null==pingyin||pingyin.trim().length()<0){
				pingyin="";
			}else{
			}
			List<User> users=new ArrayList<User>();
			User currentUser = SecurityUserHolder.getCurrentUser();
			if(null==currentUser){
				currentUser = getSessionUser();
			}
			if(null==currentUser){
				return ;
			}
			String sql="select * from sys_user where 1=1 ";
			Enterprise enterprise2 = currentUser.getEnterprise();
			if(null!=enterprise2){
				sql=sql+" and enterpriseId="+enterprise2.getDbid();
			}
			sql=sql+" and (userId like ?  or realName like ? ) limit "+limit;
			users= userManageImpl.executeSql(sql, new Object[]{"%"+pingyin+"%","%"+pingyin+"%"});
			if(null==users||users.size()<=0){
				users = userManageImpl.getAll();
			}
			JSONArray  array=new JSONArray();
			if(null!=users&&users.size()>0){
				for (User user : users) {
					JSONObject object=new JSONObject();
					object.put("dbid", user.getDbid());
					object.put("name", user.getRealName());
					object.put("userId", user.getUserId());
					object.put("mobilePhone", user.getMobilePhone());
					array.put(object);
				}
				renderJson(array.toString());
			}else{
				renderJson("1");
			}
			
			return ;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return;
	}
}
