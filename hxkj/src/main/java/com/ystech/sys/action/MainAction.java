/**
 * 
 */
package com.ystech.sys.action;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.Md5;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Role;
import com.ystech.sys.model.SystemInfo;
import com.ystech.sys.model.User;
import com.ystech.sys.service.DepartmentManageImpl;
import com.ystech.sys.service.ResourceManageImpl;
import com.ystech.sys.service.SystemInfoMangeImpl;
import com.ystech.sys.service.UserManageImpl;

/**
 * @author shusanzhan
 * @date 2014-2-11
 */
@Component("mainAction")
@Scope("prototype")
public class MainAction extends BaseController{
	private String[] datas={"新到车辆","预警一级","预警二级","自有一级","自有二级","自有三级","自有重点"};
	private String[] iocns={"fa-tachometer","fa-desktop","fa-list","fa-pencil-square-o","fa-list-alt","fa-calendar","fa-picture-o"," fa-tag","fa-file-o"};
	private ResourceManageImpl resourceManageImpl;
	private HttpServletRequest request=getRequest();
	private DepartmentManageImpl departmentManageImpl;
	private SystemInfoMangeImpl systemInfoMangeImpl;
	private UserManageImpl userManageImpl;
	@Resource
	public void setResourceManageImpl(ResourceManageImpl resourceManageImpl) {
		this.resourceManageImpl = resourceManageImpl;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	@Resource
	public void setDepartmentManageImpl(DepartmentManageImpl departmentManageImpl) {
		this.departmentManageImpl = departmentManageImpl;
	}
	@Resource
	public void setSystemInfoMangeImpl(SystemInfoMangeImpl systemInfoMangeImpl) {
		this.systemInfoMangeImpl = systemInfoMangeImpl;
	}
	@Resource
	public void setUserManageImpl(UserManageImpl userManageImpl) {
		this.userManageImpl = userManageImpl;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String login() throws Exception {
		try {
			List<SystemInfo> systemInfos = systemInfoMangeImpl.getAll();
			if(null!=systemInfos&&systemInfos.size()>0){
				request.setAttribute("systemInfo", systemInfos.get(0));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "login";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String modifyPassword() throws Exception {
		HttpServletRequest request2 = getRequest();
		try {
			HttpSession session = request2.getSession();
			User user = (User) session.getAttribute("user");
			User user2 = userManageImpl.get(user.getDbid());
			request2.setAttribute("user2", user2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modifyPassword";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveModifyPassword() {
		HttpServletRequest request = getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		String password = request.getParameter("password");
		if(null==password||password.trim().length()<=0){
			renderErrorMsg(new Throwable("密码输入错误！"), "");
			return ;
		}
		try{
			if (dbid>0) {
				User user2 = userManageImpl.get(dbid);
				user2.setPassword(Md5.calcMD5(password+"{"+user2.getUserId()+"}"));
				userManageImpl.save(user2);
				HttpSession session = request.getSession();
				session.setAttribute("user", user2);
			} else {
				renderErrorMsg(new Throwable("操作数据错误！"), "");
				return ;
			}	
		}catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(new Throwable("操作数据错误！"), "");
			return ;
		}
		renderMsg("/main/index", "修改密码成功！");
		return ;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String index() throws Exception {
		HttpServletRequest request = this.getRequest();
		try{
			User user = SecurityUserHolder.getCurrentUser();
			List<com.ystech.sys.model.Resource> resources = resourceManageImpl.queryResourceByUserId(user.getDbid());
			if(null!=resources&&resources.size()>0){
				com.ystech.sys.model.Resource resource = resources.get(0);
				String menu = menu(resource.getDbid());
				request.setAttribute("menu", menu);
			}
			request.setAttribute("resources", resources);
			List<SystemInfo> systemInfos = systemInfoMangeImpl.getAll();
			if(null!=systemInfos&&systemInfos.size()>0){
				request.setAttribute("systemInfo", systemInfos.get(0));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "index";
	}
	/**
	 * 功能描述：销售顾问访问主页面
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String salerContent() throws Exception {
		User currentUser = SecurityUserHolder.getCurrentUser();
		try{
			Integer userId = currentUser.getDbid();
			Integer enterpriseId = currentUser.getEnterprise().getDbid();
			Set<Role> roles = currentUser.getRoles();
			for (Role role : roles) {
				if(role.getName().equals("经销商管理员")){
					userId=-1;
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "salerContent";
	}
	/**
	 * 功能描述：系统管理员
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String adminContent() throws Exception {
		try{
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return "adminContent";
	}
	/**
	 * 功能描述：销售副总统主页
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String mangerContent() throws Exception {
		User user = SecurityUserHolder.getCurrentUser();
		try{
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "mangerContent";
	}
	/**
	 * 功能描述：总经理系统主页
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String generalMangerContent() throws Exception {
		try{
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "generalMangerContent";
	}
	/**
	 * 功能描述：物流部主页
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String wlbContent() throws Exception {
		try{
			
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		return "wlbContent";
	}
	/**
	 * 功能描述：客户部主页面
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String serviceContent() throws Exception {
		return "serviceContent";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void submenu2() throws Exception {
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try{
			String menu = menu(dbid);
			renderText(menu);
		}catch (Exception e) {
			e.printStackTrace();
			renderText("error");
			return ;
		}
	}
	private String menu(Integer parnentId){
		StringBuffer buffer=new StringBuffer();
		User user = SecurityUserHolder.getCurrentUser();
		
		List<com.ystech.sys.model.Resource> resources = resourceManageImpl.queryResourceByUserId(user.getDbid(),parnentId,1);
		if(null!=resources){
			int i=0;
			for (com.ystech.sys.model.Resource parent : resources) {
				List<com.ystech.sys.model.Resource> childResources = resourceManageImpl.queryResourceByUserId(user.getDbid(),parent.getDbid(),2);
				if(null!=childResources&&childResources.size()>0){
					//如果还有子集菜单
					if(i==0){
						buffer.append("<li class=\"active open hsub\">");
					}else{
						buffer.append("<li class=\"hsub\">");
					}
					buffer.append("<a href='#' class=\"dropdown-toggle\">");
					if(i<=iocns.length){
						buffer.append("<i class='menu-icon fa "+iocns[i]+"'></i>");
					}else{
						buffer.append("<i class='menu-icon fa "+iocns[i-iocns.length]+"'></i>");
					}
					buffer.append("<span class=\"menu-text\">"+parent.getTitle()+"</span>");
					buffer.append("<b class=\"arrow fa fa-angle-down\"></b>");
					buffer.append("</a>");
					buffer.append("<b class=\"arrow\"></b>");
					if(i==0){
						buffer.append("<ul class=\"submenu nav-show\" style=\"display: block;\">");
					}
					else{
						buffer.append("<ul class=\"submenu\">");
					}
					int j=0;
					for (com.ystech.sys.model.Resource  child : childResources) {
						if(i==0&&j==0){
							buffer.append("<li class=\"active\">");
						}else{
							buffer.append("<li class=\"\">");
						}
						buffer.append("<a href='"+child.getContent()+"' target='contentUrl'>");
						buffer.append("<i class=\"menu-icon fa fa-caret-right\"></i>");
						buffer.append(child.getTitle());
						buffer.append("</a>");
						buffer.append("<b class=\"arrow\"></b>");
						buffer.append("</li>");
						j++;
					}
					buffer.append("</ul>");
					buffer.append("</li>");
				}else{
					//如果是独立的链接
					if(i==0){
						buffer.append("<li class=\"active open\">");
					}else{
						buffer.append("<li class=\"\">");
					}
					if(null!=parent.getContent()&&parent.getContent().trim().length()>0){
						buffer.append("<a href='"+parent.getContent()+"' target='contentUrl'>");
					}
					if(i<=iocns.length){
						buffer.append("<i class='menu-icon fa "+iocns[i]+"'></i>");
					}else{
						buffer.append("<i class='menu-icon fa "+iocns[i-iocns.length]+"'></i>");
					}
					buffer.append("<span class=\"menu-text\">"+parent.getTitle()+"</span>");
					buffer.append("</a>");
					buffer.append("<b class=\"arrow\"></b>");
					buffer.append("</li>");
				}
				i++;
			}
		}
		return buffer.toString();
	}
}
