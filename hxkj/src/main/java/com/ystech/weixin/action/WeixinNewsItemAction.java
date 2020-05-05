package com.ystech.weixin.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.DateUtil;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinNewsitem;
import com.ystech.weixin.model.WeixinNewstemplate;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinNewsitemManageImpl;
import com.ystech.weixin.service.WeixinNewstemplateManageImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component("weixinNewsItemAction")
@Scope("prototype")
public class WeixinNewsItemAction extends BaseController{
	private WeixinNewstemplateManageImpl weixinNewstemplateManageImpl;
	private WeixinNewsitem weixinNewsitem;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinNewsitemManageImpl weixinNewsitemManageImpl;
	
	public WeixinNewsitem getWeixinNewsitem() {
		return weixinNewsitem;
	}

	public void setWeixinNewsitem(WeixinNewsitem weixinNewsitem) {
		this.weixinNewsitem = weixinNewsitem;
	}
	@Resource
	public void setWeixinNewstemplateManageImpl(
			WeixinNewstemplateManageImpl weixinNewstemplateManageImpl) {
		this.weixinNewstemplateManageImpl = weixinNewstemplateManageImpl;
	}
	@Resource
	public void setWeixinNewsitemManageImpl(
			WeixinNewsitemManageImpl weixinNewsitemManageImpl) {
		this.weixinNewsitemManageImpl = weixinNewsitemManageImpl;
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
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				List<WeixinNewstemplate> weixinNewstemplates = weixinNewstemplateManageImpl.find("from WeixinNewstemplate where accountid="+weixinAccount.getDbid(), new Object[]{});
				request.setAttribute("weixinNewstemplates", weixinNewstemplates);
			}
		}catch (Exception e) {
			// TODO: handle exception
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
	public String addMore() throws Exception {
		return "addMore";
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
			WeixinNewstemplate weixinNewstemplate = weixinNewstemplateManageImpl.get(dbid);
			request.setAttribute("weixinNewstemplate", weixinNewstemplate);
			Set<WeixinNewsitem> weixinNewsitems = weixinNewstemplate.getWeixinNewsitems();
			if(null!=weixinNewsitems&&weixinNewsitems.size()>0){
				int i=0;
				for (WeixinNewsitem weixinNewsitem : weixinNewsitems) {
					if(i==0){
						request.setAttribute("weixinNewsitem", weixinNewsitem);
					}
				}
			}
			//slideManageImpl.get(dbid);
			//request.setAttribute("slide", slide2);
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
	public String editMore() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			WeixinNewstemplate weixinNewstemplate = weixinNewstemplateManageImpl.get(dbid);
			request.setAttribute("weixinNewstemplate", weixinNewstemplate);
		}
		return "editMore";
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
		Integer type = ParamUtil.getIntParam(request, "type", 1);
		Integer weixinNewstemplateDbid = ParamUtil.getIntParam(request, "weixinNewstemplateDbid", -1);
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				String contentArea = request.getParameter("contentArea");
				WeixinNewstemplate weixinNewstemplate=null;
				if(weixinNewstemplateDbid>0){
					weixinNewstemplate=weixinNewstemplateManageImpl.get(weixinNewstemplateDbid);
				}else{
					weixinNewstemplate=new WeixinNewstemplate();
					String addtime = DateUtil.format2(new Date());
					weixinNewstemplate.setAddtime(addtime);
					weixinNewstemplate.setType(type+"");
				}
				weixinNewstemplate.setAccountid(weixinAccount.getDbid()+"");
				weixinNewstemplate.setTemplatename(weixinNewsitem.getTitle());
				weixinNewstemplateManageImpl.save(weixinNewstemplate);	
				
				Integer dbid = weixinNewsitem.getDbid();
				if(dbid==null||dbid<=0){
					weixinNewsitem.setContent(contentArea);
					weixinNewsitem.setReadNum(0);
					weixinNewsitem.setWeixinNewstemplate(weixinNewstemplate);
					weixinNewsitem.setNewType("news");
					weixinNewsitem.setCreateDate(new Date());
					weixinNewsitemManageImpl.save(weixinNewsitem);
				}else{
					WeixinNewsitem weixinNewsitem2 = weixinNewsitemManageImpl.get(dbid);
					weixinNewsitem2.setAuthor(weixinNewsitem.getAuthor());
					weixinNewsitem2.setContent(contentArea);
					weixinNewsitem2.setCoverShow(weixinNewsitem.getCoverShow());
					weixinNewsitem2.setDescription(weixinNewsitem.getDescription());
					weixinNewsitem2.setImagepath(weixinNewsitem.getImagepath());
					weixinNewsitem2.setOrders(weixinNewsitem.getOrders());
					weixinNewsitem2.setTitle(weixinNewsitem.getTitle());
					weixinNewsitem2.setUrl(weixinNewsitem.getUrl());
					weixinNewsitem2.setWeixinNewstemplate(weixinNewstemplate);
					weixinNewsitemManageImpl.save(weixinNewsitem2);
				}
			}else{
				renderErrorMsg(new Throwable("保存错误，系统无公众号信息"), "");
				return ;
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/weixinNewsItem/queryList", "保存数据成功！");
		return ;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveMore() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer type = ParamUtil.getIntParam(request, "type", 2);
		Integer weixinNewstemplateDbid = ParamUtil.getIntParam(request, "weixinNewstemplateDbid", -1);
		String jsonData = request.getParameter("jsonData");
		try{
			JSONArray fromObject = JSONArray.fromObject(jsonData);
			List<WeixinNewsitem> weixinNewsitems = JSONArray.toList(fromObject,WeixinNewsitem.class);
			if(null==weixinNewsitems||weixinNewsitems.size()<=0){
				renderErrorMsg(new Throwable("未添加内容信息，请确定！"),"");
				return ;
			}
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				WeixinNewstemplate weixinNewstemplate=null;
				if(weixinNewstemplateDbid>0){
					weixinNewstemplate=weixinNewstemplateManageImpl.get(weixinNewstemplateDbid);
				}else{
					weixinNewstemplate=new WeixinNewstemplate();
					String addtime = DateUtil.format2(new Date());
					weixinNewstemplate.setAddtime(addtime);
					weixinNewstemplate.setType(type+"");
				}
				weixinNewstemplate.setAccountid(weixinAccount.getDbid()+"");
				weixinNewstemplate.setTemplatename(weixinNewsitems.get(0).getTitle());
				weixinNewstemplateManageImpl.save(weixinNewstemplate);	
				if(weixinNewstemplateDbid<0){
					for (WeixinNewsitem weixinNewsitem : weixinNewsitems) {
						weixinNewsitem.setCreateDate(new Date());
						weixinNewsitem.setNewType("news");
						weixinNewsitem.setReadNum(0);
						weixinNewsitem.setWeixinNewstemplate(weixinNewstemplate);
						weixinNewsitemManageImpl.save(weixinNewsitem);
					}
				}else{
					for (WeixinNewsitem weixinNewsitem : weixinNewsitems) {
						if(null!=weixinNewsitem.getDbid()&&weixinNewsitem.getDbid()>0){
							WeixinNewsitem weixinNewsitem2 = weixinNewsitemManageImpl.get(weixinNewsitem.getDbid());
							weixinNewsitem2.setAuthor(weixinNewsitem.getAuthor());
							weixinNewsitem2.setContent(weixinNewsitem.getContent());
							weixinNewsitem2.setCoverShow(weixinNewsitem.getCoverShow());
							weixinNewsitem2.setImagepath(weixinNewsitem.getImagepath());
							weixinNewsitem2.setTitle(weixinNewsitem.getTitle());
							weixinNewsitem2.setUrl(weixinNewsitem.getUrl());
							weixinNewsitemManageImpl.save(weixinNewsitem2);
						}else{
							weixinNewsitem.setCreateDate(new Date());
							weixinNewsitem.setNewType("news");
							weixinNewsitem.setReadNum(0);
							weixinNewsitem.setWeixinNewstemplate(weixinNewstemplate);
							weixinNewsitemManageImpl.save(weixinNewsitem);
						}
					}
				}
			}else{
				renderErrorMsg(new Throwable("保存错误，系统无公众号信息"), "");
				return ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
		}
		renderMsg("/weixinNewsItem/queryList", "保存数据成功！");
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
					weixinNewstemplateManageImpl.deleteById(dbid);
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
		renderMsg("/weixinNewsItem/queryList"+query, "删除数据成功！");
		return;
	}
	
	/**
	* 功能描述：
	* 参数描述：
	* 逻辑描述：
	* @return
	* @throws Exception
	*/
	public String selectNewsItem() throws Exception {
		HttpServletRequest request = this.getRequest();
		String title = request.getParameter("title");
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				 String sql="select * from weixin_newstemplate where 1=1 and accountid="+weixinAccount.getDbid();
				 List params=new ArrayList();
				 if(null!=title){
					 sql=sql+" and templatename like ?  ";
					 params.add("%"+title+"%");
				 }
				 List<WeixinNewstemplate> weixinNewstemplates = weixinNewstemplateManageImpl.executeSql(sql, params.toArray());
				 request.setAttribute("weixinNewstemplates", weixinNewstemplates);
			}
		}catch (Exception e) {
		}
		return "selectNewsItem";
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
			WeixinNewstemplate weixinNewstemplate = weixinNewstemplateManageImpl.get(weixinTexttemplateId);
			StringBuffer buffer=new  StringBuffer();
			buffer.append("<div class=\"ng ng-single\">");
			buffer.append("<a href=\"javascript:;\" class=\"close--circle js-delete-complex\" onclick=\"removeSelectText(this)\">×</a>");
				buffer.append("<div class=\"ng-item\">");
					buffer.append("<span class=\"label label-success\">图文</span>");
					buffer.append("<div class=\"ng-title\">");
						buffer.append("<a href=''  class=\"new-window\" title=\""+weixinNewstemplate.getTemplatename()+"\">"+weixinNewstemplate.getTemplatename()+"</a>");
					buffer.append("</div>");
				buffer.append("</div>");	
			/*	buffer.append("<div class=\"ng-item view-more\">");	
				buffer.append("<p>"+weixinTexttemplate2.getContent()+"</p>");	
				buffer.append("</div>");	*/
			buffer.append("</div>");
			object.put("value", buffer.toString());
			object.put("dbid",weixinNewstemplate.getDbid());
			renderJson(object.toString());
		} catch (Exception e) {
		}
		return;
	}
}
