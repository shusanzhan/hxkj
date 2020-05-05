package com.ystech.weixin.action;

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
import com.ystech.weixin.core.util.Configure;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinGroup;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinGroupManageImpl;

import net.sf.json.JSONObject;

@Component("weixinGroupAction")
@Scope("prototype")
public class WeixinGroupAction extends BaseController{
	private WeixinGroupManageImpl weixinGroupManageImpl;
	private WeixinGroup weixinGroup;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	public WeixinGroup getWeixinGroup() {
		return weixinGroup;
	}
	public void setWeixinGroup(WeixinGroup weixinGroup) {
		this.weixinGroup = weixinGroup;
	}
	@Resource
	public void setWeixinGroupManageImpl(WeixinGroupManageImpl weixinGroupManageImpl) {
		this.weixinGroupManageImpl = weixinGroupManageImpl;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	@Resource
	public void setWeixinAccesstokenManageImpl(
			WeixinAccesstokenManageImpl weixinAccesstokenManageImpl) {
		this.weixinAccesstokenManageImpl = weixinAccesstokenManageImpl;
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
				Page<WeixinGroup> page=weixinGroupManageImpl.pagedQuerySql(pageNo, pageSize,WeixinGroup.class, "select * from Weixin_Group   where accountId=? ", new Object[]{weixinAccount.getDbid()});
				request.setAttribute("page", page);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
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
		if(dbid>0){
			WeixinGroup weixinGroup2 = weixinGroupManageImpl.get(dbid);
			request.setAttribute("weixinGroup", weixinGroup2);
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
			Integer dbid = weixinGroup.getDbid();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null==weixinAccounts||weixinAccounts.size()<=0){
				renderErrorMsg(new Throwable("保存错误，无公众号信息"), "");
				return ;
			}
			WeixinAccount weixinAccount = weixinAccounts.get(0);
			 if(null==weixinGroup.getIsCommon()||weixinGroup.getIsCommon()<=0){
				 weixinGroup.setIsCommon(2);
			 }
			 weixinGroup.setAccountId(weixinAccount.getDbid());
			 weixinGroupManageImpl.save(weixinGroup);
			 WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			 if(null!=accessToken){
				 if(null==dbid){
					 String createApi = Configure.GROUPS_CREATE_API.replace("ACCESS_TOKEN", accessToken.getAccessToken());
					 String outputStr="{\"group\":{\"name\":\""+weixinGroup.getName()+"\"}}";
					 JSONObject jsonObject = WeixinUtil.httpRequest(createApi, "POST", outputStr);
					 if(null!=jsonObject){
						 if(jsonObject.containsKey("errmsg")){
							 renderErrorMsg(new Throwable("同步组错误:"+jsonObject.getString("errmsg")), "");
						 }
						 else{
							  String group= jsonObject.getString("group");
							  JSONObject fromObject = JSONObject.fromObject(group);
							  Object object = fromObject.get("id");
							  weixinGroup.setWechatGroupId(object.toString());
							  weixinGroupManageImpl.save(weixinGroup);
						 }
					 }else{
						 renderErrorMsg(new Throwable("同步组错误，远程微信服务端返回结果为空！"), "");
					 } 
				 }
				 else if(dbid>0){
					 String createApi = Configure.GROUPS_UPDATE_API.replace("ACCESS_TOKEN", accessToken.getAccessToken());
					 String outputStr="{\"group\":{\"id\":"+weixinGroup.getWechatGroupId()+",\"name\":\""+weixinGroup.getName()+"\"}}";
					 JSONObject jsonObject = WeixinUtil.httpRequest(createApi, "POST", outputStr);
					 if(null!=jsonObject){
						 String result = jsonObject.toString();
						 if(result.contains("ok")){
							 renderMsg("/weixinGroup/queryList", "保存数据成功！");
							 return ;
						 }else{
							 renderErrorMsg(new Throwable("同步组错误，远程微信服务端返回结果为空！"), "");
						 }
					 }else{
						 renderErrorMsg(new Throwable("同步组错误，远程微信服务端返回结果为空！"), "");
					 } 
				 }
				 
			 }
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/weixinGroup/queryList", "保存数据成功！");
		return ;
	}

	/**
	 * 功能描述： 移动微信粉丝到对应用户组下
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		String query = ParamUtil.getQueryUrl(request);
		if(null!=dbids&&dbids.length>0){
			try {
				Enterprise enterprise = SecurityUserHolder.getEnterprise();
				List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
				if(null==weixinAccounts||weixinAccounts.size()<=0){
					renderErrorMsg(new Throwable("删除错误，无公众号信息"), "");
					return ;
				}
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
				for (Integer dbid : dbids) {
					WeixinGroup weixinGroup2 = weixinGroupManageImpl.get(dbid);
					weixinGroupManageImpl.deleteById(dbid);
					 String createApi = Configure.GROUPS_DELETE_API.replace("ACCESS_TOKEN", accessToken.getAccessToken());
					 String outputStr="{\"group\":{\"id\":"+weixinGroup2.getWechatGroupId()+"}}";
					 JSONObject jsonObject = WeixinUtil.httpRequest(createApi, "POST", outputStr);
					 System.out.println("======"+jsonObject.toString());
					 if(null!=jsonObject){
						 String result = jsonObject.toString();
						 if(result.contains("ok")){
							 renderMsg("/weixinGroup/queryList"+query, "删除数据成功！");
							 return ;
						 }else{
							 renderErrorMsg(new Throwable("同步组错误，远程微信服务端返回结果为空！"), "");
							 return ;
						 }
					 }else{
						 renderErrorMsg(new Throwable("同步组错误，远程微信服务端返回结果为空！"), "");
						 return;
					 } 
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
		renderMsg("/weixinGroup/queryList"+query, "删除数据成功！");
		return;
	}
	
}
