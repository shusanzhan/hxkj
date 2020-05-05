/**
 * 
 */
package com.ystech.sys.action;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Department;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.SystemInfo;
import com.ystech.sys.model.User;
import com.ystech.sys.service.DepartmentManageImpl;
import com.ystech.sys.service.EnterpriseManageImpl;
import com.ystech.sys.service.SystemInfoMangeImpl;
import com.ystech.sys.service.UserManageImpl;

/**
 * @author shusanzhan
 * @date 2013-5-23
 */
@Component("departmentAction")
@Scope("prototype")
public class DepartmentAction extends BaseController{
	private Department department;
	private DepartmentManageImpl departmentManageImpl;
	private EnterpriseManageImpl enterpriseManageImpl;
	private UserManageImpl userManageImpl;
	private SystemInfoMangeImpl systemInfoMangeImpl;
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	@Resource
	public void setDepartmentManageImpl(DepartmentManageImpl departmentManageImpl) {
		this.departmentManageImpl = departmentManageImpl;
	}
	@Resource
	public void setEnterpriseManageImpl(EnterpriseManageImpl enterpriseManageImpl) {
		this.enterpriseManageImpl = enterpriseManageImpl;
	}
	@Resource
	public void setUserManageImpl(UserManageImpl userManageImpl) {
		this.userManageImpl = userManageImpl;
	}
	@Resource
	public void setSystemInfoMangeImpl(SystemInfoMangeImpl systemInfoMangeImpl) {
		this.systemInfoMangeImpl = systemInfoMangeImpl;
	}
	/**
	 * 功能描述：部门树页面
	 * @return
	 * @throws Exception
	 */
	public String list() throws Exception {
		return "list";
	}
	
	/**
	 * 功能描述：添加部门信息
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if (dbid > 0) {
			Department department = departmentManageImpl.get(dbid);
			request.setAttribute("department", department);
			
			Enterprise enterprise = department.getEnterprise();
			request.setAttribute("enterprise", enterprise);
		}
		return "edit";
	}
	/**
	 * 功能描述：部门信息保存
	 * @throws Exception
	 */
	public void save() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		Integer manager = ParamUtil.getIntParam(request,"managerId", -1);
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			if(parentId<0){
				renderErrorMsg(new Throwable("请选择上级部门"), "");
				return ;
			}
			if(manager>0){
				User user = userManageImpl.get(manager);
				department.setManager(user);
			}
			//用户配置信息
			SystemInfo systemInfo=null;
			List<SystemInfo> systemInfos = systemInfoMangeImpl.getAll();
			if(null!=systemInfos){
				systemInfo=systemInfos.get(0);
			}
			if (null!=department.getDbid()&&department.getDbid()>0) {
				//编辑资料
				Department department2 = departmentManageImpl.get(department.getDbid());
				department2.setDiscription(department.getDiscription());
				department2.setFax(department.getFax());
				department2.setManager(department.getManager());
				department2.setName(department.getName());
				department2.setParentId(parentId);
				department2.setPhone(department.getPhone());
				department2.setSuqNo(department.getSuqNo());
				department2.setType(department.getType());
				department2.setBussiType(department.getBussiType());
				departmentManageImpl.save(department2);
				
			}
			else{
				//第一次创建部门
				department.setParentId(parentId);
				 //如果父节点不为空，并且父节点不为根节点
				department.setEnterprise(enterprise);
				departmentManageImpl.save(department);
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		
		renderMsg("/department/list?parentId="+parentId, "保存数据成功！",department.getDbid());
		return ;
	}
	/*保存数据成功处理机制 */
	public void renderMsg(String url,String message,Integer depId){
		JSONArray jsonArray=new JSONArray();
		JSONObject jsonObject=new JSONObject();
		try {
			jsonObject.put("mark", "0");
			jsonObject.put("url",  getRequest().getContextPath()+url);
			jsonObject.put("message", message);
			jsonObject.put("depId", depId);
		} catch (JSONException e) {
			e.printStackTrace();
			log.error("保存数据成功！返回提示信息转换成JSON数据是发生错误！");
		}
		jsonArray.put(jsonObject);
		renderJson(jsonArray.toString());
	}
	/**
	 * 功能描述：删除部门信息
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			Department department2 = departmentManageImpl.get(dbid);
			departmentManageImpl.deleteById(dbid);
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("", "删除数据成功！");
		return;
	}
	public void getDepartmentByDbid() throws Exception{
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			JSONObject object=new JSONObject();
			Department department2 = departmentManageImpl.get(dbid);
			if(null!=department2){
				object.put("dbid", department2.getDbid());
				object.put("name", department2.getName());
				object.put("phone", department2.getPhone());
				object.put("fax", department2.getFax());
				if(null!=department2.getManager())
					object.put("manager", department2.getManager().getRealName());
				object.put("suqNo", department2.getSuqNo());
				object.put("discription", department2.getDiscription());
				renderJson(object.toString());
			}else{
				renderText("error");
				return ;
			}
		}
		else{
			renderText("error");
			return ;
		}
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String orderNum() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		try {
			List<Department> departments=null;
			if(parentId<=0){
				departments = departmentManageImpl.find("from Department where parent.dbid is NUll order by suqNo", null);
			}else{
				departments = departmentManageImpl.find("from Department where parent.dbid=? order by suqNo", parentId);
			}
			request.setAttribute("departments", departments);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "orderNum";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveOrderNum() throws Exception {
		HttpServletRequest request = this.getRequest();
		String[] dbids = request.getParameterValues("dbid");
		String[] orderNos = request.getParameterValues("orderNo");
		int parentId=0;
		try {
			int i=0;
			for (String dbid : dbids) {
				Integer d = Integer.parseInt(dbid);
				Department department = departmentManageImpl.get(d);
				if(null!=orderNos[i]&&orderNos[i].trim().length()>0){
					department.setSuqNo(Integer.parseInt(orderNos[i]));
				}
				departmentManageImpl.save(department);
				i++;
				parentId=department.getParentId();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
		}
		renderMsg("/department/list?parentId="+parentId, "保存数据成功！");
		return;
	}
	/**
	 * 功能描述：前台拖拽更新资源
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void updateParent() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		try {
			if(dbid>0){
				Department department2 = departmentManageImpl.get(dbid);
				department2.setParentId(parentId);
				departmentManageImpl.save(department2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("", "更新数据成功！");
		return ;
	}
	/**
	 * 功能描述：部门树生成JSON串
	 * 逻辑描述：默认绑定一颗根节点，更节点的父节点为0
	 */
	public void editResourceJson() {
		try {
			Enterprise company = SecurityUserHolder.getEnterprise();
			JSONObject jsonObject = departmentManageImpl.makeJSONSuperObject(company);
			renderJson(jsonObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
}
