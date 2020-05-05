package com.ystech.weixin.action;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.model.WeixinMenuentityGroup;
import com.ystech.weixin.model.WeixinMenuentityGroupMatchRule;
import com.ystech.weixin.service.WeixinMenuentityGroupManageImpl;
import com.ystech.weixin.service.WeixinMenuentityGroupMatchRuleManageImpl;

@Component("weixinMenuentityGroupAction")
@Scope("prototype")
public class WeixinMenuentityGroupAction extends BaseController{
	private WeixinMenuentityGroupManageImpl weixinMenuentityGroupManageImpl;
	private WeixinMenuentityGroup weixinMenuentityGroup;
	private WeixinMenuentityGroupMatchRuleManageImpl weixinMenuentityGroupMatchRuleManageImpl;
	private WeixinMenuentityGroupMatchRule weixinMenuentityGroupMatchRule;
	public WeixinMenuentityGroup getWeixinMenuentityGroup() {
		return weixinMenuentityGroup;
	}
	public void setWeixinMenuentityGroup(WeixinMenuentityGroup weixinMenuentityGroup) {
		this.weixinMenuentityGroup = weixinMenuentityGroup;
	}
	@Resource
	public void setWeixinMenuentityGroupManageImpl(WeixinMenuentityGroupManageImpl weixinMenuentityGroupManageImpl) {
		this.weixinMenuentityGroupManageImpl = weixinMenuentityGroupManageImpl;
	}
	
	public WeixinMenuentityGroupMatchRuleManageImpl getWeixinMenuentityGroupMatchRuleManageImpl() {
		return weixinMenuentityGroupMatchRuleManageImpl;
	}
	@Resource
	public void setWeixinMenuentityGroupMatchRuleManageImpl(
			WeixinMenuentityGroupMatchRuleManageImpl weixinMenuentityGroupMatchRuleManageImpl) {
		this.weixinMenuentityGroupMatchRuleManageImpl = weixinMenuentityGroupMatchRuleManageImpl;
	}
	public void setWeixinMenuentityGroupMatchRule(WeixinMenuentityGroupMatchRule weixinMenuentityGroupMatchRule) {
		this.weixinMenuentityGroupMatchRule = weixinMenuentityGroupMatchRule;
	}
	
	public WeixinMenuentityGroupMatchRule getWeixinMenuentityGroupMatchRule() {
		return weixinMenuentityGroupMatchRule;
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
		String username = request.getParameter("title");
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			String sql="select * from weixin_menuentitygroup where 1=1 and enterpriseId="+enterprise.getDbid();
			Page<WeixinMenuentityGroup> page=weixinMenuentityGroupManageImpl.pagedQuerySql(pageNo, pageSize,WeixinMenuentityGroup.class,sql, new Object[]{});
			request.setAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
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
		try {
			if(dbid>0){
				WeixinMenuentityGroup weixinMenuentityGroup2 = weixinMenuentityGroupManageImpl.get(dbid);
				request.setAttribute("weixinMenuentityGroup", weixinMenuentityGroup2);
				WeixinMenuentityGroupMatchRule weixinMenuentityGroupMatchRule = weixinMenuentityGroup2.getWeixinMenuentityGroupMatchRule();
				request.setAttribute("weixinMenuentityGroupMatchRule", weixinMenuentityGroupMatchRule);
			}
		} catch (Exception e) {
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
	public void save() throws Exception {
		HttpServletRequest request = getRequest();
		Integer parentMenu = ParamUtil.getIntParam(request, "parentMenu", 5);
		try{
			Integer dbid = weixinMenuentityGroup.getDbid();
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			if(dbid==null||dbid<=0){
				weixinMenuentityGroup.setEnterpriseId(enterprise.getDbid());
				weixinMenuentityGroup.setCreateDate(new Date());
				weixinMenuentityGroup.setModifyDate(new Date());
				weixinMenuentityGroupManageImpl.save(weixinMenuentityGroup);
				
				weixinMenuentityGroupMatchRule.setWeixinMenuentityGroup(weixinMenuentityGroup);
				weixinMenuentityGroupMatchRuleManageImpl.save(weixinMenuentityGroupMatchRule);
			}else{
				weixinMenuentityGroup.setEnterpriseId(enterprise.getDbid());
				weixinMenuentityGroupManageImpl.save(weixinMenuentityGroup);
				weixinMenuentityGroupMatchRule.setWeixinMenuentityGroup(weixinMenuentityGroup);
				weixinMenuentityGroupMatchRuleManageImpl.save(weixinMenuentityGroupMatchRule);
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/weixinMenuentityGroup/queryList?parentMenu="+parentMenu, "保存数据成功！");
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
					weixinMenuentityGroupManageImpl.deleteById(dbid);
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
		renderMsg("/weixinMenuentityGroup/queryList"+query, "删除数据成功！");
		return;
	}
}
