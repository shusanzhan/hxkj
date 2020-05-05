package com.ystech.weixin.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinTexttemplate;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinTexttemplateManageImpl;

import net.sf.json.JSONObject;

@Component("weixinTexttemplateAction")
@Scope("prototype")
public class WeixinTexttemplateAction extends BaseController{
	private WeixinTexttemplate weixinTexttemplate;
	private WeixinTexttemplateManageImpl weixinTexttemplateManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	public WeixinTexttemplate getWeixinTexttemplate() {
		return weixinTexttemplate;
	}
	public void setWeixinTexttemplate(WeixinTexttemplate weixinTexttemplate) {
		this.weixinTexttemplate = weixinTexttemplate;
	}
	@Resource
	public void setWeixinTexttemplateManageImpl(
			WeixinTexttemplateManageImpl weixinTexttemplateManageImpl) {
		this.weixinTexttemplateManageImpl = weixinTexttemplateManageImpl;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
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
		String name = request.getParameter("name");
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				String sql="select * from Weixin_Texttemplate where accountid="+weixinAccount.getDbid();
				Page<WeixinTexttemplate> page=null;
				if(null!=name&&name.trim().length()>0){
					sql=sql+" and templatename like ?";
					page = weixinTexttemplateManageImpl.pagedQuerySql(pageNo, pageSize, WeixinTexttemplate.class, sql, new Object[]{"%"+name+"%"});
				}else{
					page = weixinTexttemplateManageImpl.pagedQuerySql(pageNo, pageSize, WeixinTexttemplate.class, sql, new Object[]{});
				}
			 request.setAttribute("page", page);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "list";
	}
	/**
	* 功能描述：
	* 参数描述：
	* 逻辑描述：
	* @return
	* @throws Exception
	*/
	public String selectText() throws Exception {
		HttpServletRequest request = this.getRequest();
		String title = request.getParameter("title");
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				 String sql="select * from weixin_texttemplate where accountid="+weixinAccount.getDbid();
				 List params=new ArrayList();
				 if(null!=title){
					 sql=sql+" and content like ? or templatename like ? ";
					 params.add("%"+title+"%");
					 params.add("%"+title+"%");
				 }
				 List<WeixinTexttemplate> weixinTexttemplates = weixinTexttemplateManageImpl.executeSql(sql, params.toArray());
				 request.setAttribute("weixinTexttemplates", weixinTexttemplates);
			}
		}catch (Exception e) {
		}
		return "selectText";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxTempt() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer weixinTexttemplateId = ParamUtil.getIntParam(request, "weixinTexttemplateId", -1);
		try {
			JSONObject object=new JSONObject();
			WeixinTexttemplate weixinTexttemplate2 = weixinTexttemplateManageImpl.get(weixinTexttemplateId);
			StringBuffer buffer=new  StringBuffer();
			buffer.append("<div class=\"ng ng-single\">");
			buffer.append("<a href=\"javascript:;\" class=\"close--circle js-delete-complex\" onclick=\"removeSelectText(this)\">×</a>");
				buffer.append("<div class=\"ng-item\">");
					buffer.append("<span class=\"label label-success\">文文</span>");
						buffer.append(weixinTexttemplate2.getContent());
				buffer.append("</div>");	
			/*	buffer.append("<div class=\"ng-item view-more\">");	
				buffer.append("<p>"+weixinTexttemplate2.getContent()+"</p>");	
				buffer.append("</div>");	*/
			buffer.append("</div>");
			object.put("value", buffer.toString());
			object.put("dbid",weixinTexttemplate2.getDbid());
			renderJson(object.toString());
		} catch (Exception e) {
		}
		return;
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
			WeixinTexttemplate weixinTexttemplate2 = weixinTexttemplateManageImpl.get(dbid);
			request.setAttribute("weixinTexttemplate", weixinTexttemplate2);
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
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				Integer dbid = weixinTexttemplate.getDbid();
				if(dbid==null||dbid<=0){
					weixinTexttemplate.setAccountid(weixinAccount.getDbid()+"");
					weixinTexttemplate.setAddtime(new Date());
					weixinTexttemplateManageImpl.save(weixinTexttemplate);
				}else{
					weixinTexttemplate.setAccountid(weixinAccount.getDbid()+"");
					weixinTexttemplateManageImpl.save(weixinTexttemplate);
				}
			}else{
				renderErrorMsg(new Throwable("添加失败，无公众号信息"), "");
				return ;
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/weixinTexttemplate/queryList", "保存数据成功！");
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
					weixinTexttemplateManageImpl.deleteById(dbid);
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
		renderMsg("/weixinTexttemplate/queryList"+query, "删除数据成功！");
		return;
	}

	
}
