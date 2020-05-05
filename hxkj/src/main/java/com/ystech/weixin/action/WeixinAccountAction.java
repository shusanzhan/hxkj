package com.ystech.weixin.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.DateUtil;
import com.ystech.core.util.Md5;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.User;
import com.ystech.sys.service.UserManageImpl;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.service.WeixinAccountManageImpl;
@Component("weixinAccountAction")
@Scope("prototype")
public class WeixinAccountAction extends BaseController{
	private WeixinAccount weixinAccount;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private UserManageImpl userManageImpl;
	public WeixinAccount getWeixinAccount() {
		return weixinAccount;
	}

	public void setWeixinAccount(WeixinAccount weixinAccount) {
		this.weixinAccount = weixinAccount;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
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
	@SuppressWarnings("unchecked")
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		try{
			 Page<WeixinAccount> page= weixinAccountManageImpl.pagedQuerySql(pageNo, pageSize, WeixinAccount.class, "select * from weixin_account", new Object[]{});
			 request.setAttribute("page", page);
		}catch (Exception e) {
			e.printStackTrace();
			//TODO: handle exception
		}
		return "list";
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String editSelf() throws Exception {
		HttpServletRequest request = this.getRequest();
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> findBy = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=findBy&&findBy.size()>0){
				WeixinAccount weixinAccount = findBy.get(0);
				if(null!=weixinAccount){
					request.setAttribute("weixinAccount", weixinAccount);
					if(null==weixinAccount.getCode()||weixinAccount.getCode().trim().length()<=0){
						String formatFile = DateUtil.formatFile(new Date());
						String calcMD5 = Md5.calcMD5(formatFile);
						request.setAttribute("code", calcMD5);
					}
				}else{
					String formatFile = DateUtil.formatFile(new Date());
					String calcMD5 = Md5.calcMD5(formatFile);
					request.setAttribute("code", calcMD5);
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "editSelf";
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String add() throws Exception {
		HttpServletRequest request = this.getRequest();
		try{
			User currentUser = SecurityUserHolder.getCurrentUser();
			WeixinAccount weixinAccount = weixinAccountManageImpl.findUniqueBy("username", currentUser.getUserId());
			if(null!=weixinAccount){
				request.setAttribute("message", "您已经添加公众账号，请勿重复添加！");
			}
			
		}catch (Exception e) {
			// TODO: handle exception
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
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			WeixinAccount weixinAccount2 = weixinAccountManageImpl.get(dbid);
			request.setAttribute("weixinAccount", weixinAccount2);
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
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			weixinAccount.setEnterpriseId(enterprise.getDbid());
			weixinAccountManageImpl.save(weixinAccount);
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/weixinAccount/queryList", "保存数据成功！");
		return ;
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void saveEdit() throws Exception {
		HttpServletRequest request = getRequest();
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			weixinAccount.setEnterpriseId(enterprise.getDbid());
			weixinAccountManageImpl.save(weixinAccount);
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/weixinAccount/editSelf", "保存数据成功！");
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
					weixinAccountManageImpl.deleteById(dbid);
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
		renderMsg("/weixinAccount/queryList"+query, "删除数据成功！");
		return;
	}
}
