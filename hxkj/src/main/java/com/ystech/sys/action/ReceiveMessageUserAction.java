/**
 * 
 */
package com.ystech.sys.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.ReceiveMessageUser;
import com.ystech.sys.model.User;
import com.ystech.sys.service.ReceiveMessageUserManageImpl;
import com.ystech.sys.service.UserManageImpl;

/**
 * @author shusanzhan
 * @date 2014-2-27
 */
@Component("receiveMessageUserAction")
@Scope("prototype")
public class ReceiveMessageUserAction extends BaseController{
	private UserManageImpl userManageImpl;
	private ReceiveMessageUserManageImpl receiveMessageUserManageImpl;
	@Resource
	public void setUserManageImpl(UserManageImpl userManageImpl) {
		this.userManageImpl = userManageImpl;
	}
	@Resource
	public void setReceiveMessageUserManageImpl(
			ReceiveMessageUserManageImpl receiveMessageUserManageImpl) {
		this.receiveMessageUserManageImpl = receiveMessageUserManageImpl;
	}

	/**
	 * 功能描述：
	 * 参数描述： 
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		try{
			Page<ReceiveMessageUser> page= receiveMessageUserManageImpl.pagedQuerySql(pageNo, pageSize,ReceiveMessageUser.class, "select * from sys_ReceiveMessageUser", new Object[]{});
			request.setAttribute("page", page);
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "list";
	}

	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String add() throws Exception {
		return "edit";
	}

	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			ReceiveMessageUser receiveMessageUser = receiveMessageUserManageImpl.get(dbid);
			request.setAttribute("receiveMessageUser", receiveMessageUser);
		}
		return "edit";
	}

	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void save() throws Exception {
		HttpServletRequest request = getRequest();
		try{
			User currentUser = SecurityUserHolder.getCurrentUser();
			Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
			if(null!=dbids&&dbids.length>0){
				for (Integer dbid : dbids) {
					User user = userManageImpl.get(dbid);
					ReceiveMessageUser receiveMessageUser=new ReceiveMessageUser();
					receiveMessageUser.setUser(user);
					receiveMessageUserManageImpl.save(receiveMessageUser);
				}
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/receiveMessageUser/queryList", "保存数据成功！");
		return ;
	}

	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		if(null!=dbids&&dbids.length>0){
			try {
				for (Integer dbid : dbids) {
					receiveMessageUserManageImpl.deleteById(dbid);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
				renderErrorMsg(e, "");
				return ;
			}
		}else{
			renderErrorMsg(new Throwable("未选中数据！"), "");
			return ;
		}
		String query = ParamUtil.getQueryUrl(request);
		renderMsg("/receiveMessageUser/queryList"+query, "删除数据成功！");
		return;
	}

}
