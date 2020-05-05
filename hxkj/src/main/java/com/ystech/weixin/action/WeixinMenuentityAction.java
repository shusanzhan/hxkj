package com.ystech.weixin.action;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.core.common.Button;
import com.ystech.weixin.core.common.CommonButton;
import com.ystech.weixin.core.common.ComplexButton;
import com.ystech.weixin.core.common.MatchRule;
import com.ystech.weixin.core.common.Menu;
import com.ystech.weixin.core.common.MenuConditional;
import com.ystech.weixin.core.common.ViewButton;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinMenuentity;
import com.ystech.weixin.model.WeixinMenuentityGroup;
import com.ystech.weixin.model.WeixinMenuentityGroupMatchRule;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinMenuentityGroupManageImpl;
import com.ystech.weixin.service.WeixinMenuentityManageImpl;

import net.sf.json.JSONObject;
@Component("weixinMenuentityAction")
@Scope("prototype")
public class WeixinMenuentityAction extends BaseController{
	private String message;
	private WeixinMenuentityManageImpl weixinMenuentityManageImpl;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	private WeixinMenuentity weixinMenuentity;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinMenuentityGroupManageImpl weixinMenuentityGroupManageImpl;

	public WeixinMenuentity getWeixinMenuentity() {
		return weixinMenuentity;
	}
	public void setWeixinMenuentity(WeixinMenuentity weixinMenuentity) {
		this.weixinMenuentity = weixinMenuentity;
	}
	@Resource
	public void setWeixinMenuentityManageImpl(
			WeixinMenuentityManageImpl weixinMenuentityManageImpl) {
		this.weixinMenuentityManageImpl = weixinMenuentityManageImpl;
	}
	
	public WeixinAccountManageImpl getWeixinAccountManageImpl() {
		return weixinAccountManageImpl;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	public WeixinMenuentityManageImpl getWeixinMenuentityManageImpl() {
		return weixinMenuentityManageImpl;
	}
	@Resource
	public void setWeixinAccesstokenManageImpl(
			WeixinAccesstokenManageImpl weixinAccesstokenManageImpl) {
		this.weixinAccesstokenManageImpl = weixinAccesstokenManageImpl;
	}
	@Resource
	public void setWeixinMenuentityGroupManageImpl(WeixinMenuentityGroupManageImpl weixinMenuentityGroupManageImpl) {
		this.weixinMenuentityGroupManageImpl = weixinMenuentityGroupManageImpl;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer groupId = ParamUtil.getIntParam(request, "groupId", -1);
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				WeixinMenuentityGroup weixinMenuentityGroup = weixinMenuentityGroupManageImpl.get(groupId);
				request.setAttribute("weixinMenuentityGroup", weixinMenuentityGroup);
				List<WeixinMenuentity> weixinMenuentities = weixinMenuentityManageImpl.find("from WeixinMenuentity where accountid=? AND weixinMenuentityGroupId=? and weixinMenuentity.dbid IS NULL order by orders", new Object[]{weixinAccount.getDbid()+"",groupId});
				request.setAttribute("weixinMenuentities", weixinMenuentities);
			}
		}catch (Exception e) {
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
	public String add() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer groupId = ParamUtil.getIntParam(request, "groupId", -1);
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				request.setAttribute("weixinAccount", weixinAccount);
				WeixinMenuentityGroup weixinMenuentityGroup = weixinMenuentityGroupManageImpl.get(groupId);
				request.setAttribute("weixinMenuentityGroup", weixinMenuentityGroup);
				String productCateGorySelect = weixinMenuentityManageImpl.getProductCateGorySelect(weixinAccount.getDbid(),null,groupId);
				request.setAttribute("productCateGorySelect", productCateGorySelect);
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return "edit";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		Integer groupId = ParamUtil.getIntParam(request, "groupId", -1);
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				if(dbid>0){
					WeixinMenuentity weixinMenuentity2 = weixinMenuentityManageImpl.get(dbid);
					String productCateGorySelect = weixinMenuentityManageImpl.getProductCateGorySelect(weixinAccount.getDbid(),weixinMenuentity2,groupId);
					request.setAttribute("productCateGorySelect", productCateGorySelect);
					request.setAttribute("weixinMenuentity", weixinMenuentity2);
				}else{
					String productCateGorySelect = weixinMenuentityManageImpl.getProductCateGorySelect(weixinAccount.getDbid(),null,groupId);
					request.setAttribute("productCateGorySelect", productCateGorySelect);
				}
				request.setAttribute("weixinAccount", weixinAccount);
				
				WeixinMenuentityGroup weixinMenuentityGroup = weixinMenuentityGroupManageImpl.get(groupId);
				request.setAttribute("weixinMenuentityGroup", weixinMenuentityGroup);
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return "edit";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void save() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parendId = ParamUtil.getIntParam(request, "parentId", -1);
		Integer weixinMenuentityGroupId = ParamUtil.getIntParam(request, "weixinMenuentityGroupId", -1);
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null==weixinAccounts||weixinAccounts.size()<=0){
				renderErrorMsg(new Throwable("同步菜单失败,无公众号信息"), "");
				return ;
			}
			WeixinMenuentityGroup weixinMenuentityGroup = weixinMenuentityGroupManageImpl.get(weixinMenuentityGroupId);
			WeixinAccount weixinAccount = weixinAccounts.get(0);
			WeixinMenuentity parent=null;
			if(parendId>0){
				parent = weixinMenuentityManageImpl.get(parendId);
				weixinMenuentity.setWeixinMenuentity(parent);
			}
			if(weixinMenuentity.getDbid()==null){
				weixinMenuentity.setWeixinMenuentityGroup(weixinMenuentityGroup);
				weixinMenuentity.setMenuType(weixinMenuentityGroup.getType());
				weixinMenuentity.setAccountid(weixinAccount.getDbid()+"");
				weixinMenuentity.setEnterpriseId(enterprise.getDbid());
				//第一添加数据 保存
				weixinMenuentityManageImpl.save(weixinMenuentity);
			}else{
				WeixinMenuentity weixinMenuentity2 = weixinMenuentityManageImpl.get(weixinMenuentity.getDbid());
				weixinMenuentity2.setMenukey(weixinMenuentity.getMenukey());
				weixinMenuentity2.setMenuType(weixinMenuentity.getMenuType());
				weixinMenuentity2.setMsgtype(weixinMenuentity.getMsgtype());
				weixinMenuentity2.setName(weixinMenuentity.getName());
				weixinMenuentity2.setOrders(weixinMenuentity.getOrders());
				weixinMenuentity2.setTemplateid(weixinMenuentity.getTemplateid());
				weixinMenuentity2.setType(weixinMenuentity.getType());
				weixinMenuentity2.setUrl(weixinMenuentity.getUrl());
				weixinMenuentity2.setWeixinMenuentityGroup(weixinMenuentityGroup);
				weixinMenuentityManageImpl.save(weixinMenuentity2);
			}
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/weixinMenuentity/queryList?groupId="+weixinMenuentityGroupId, "保存数据成功！");
		return ;
	}
	
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		Integer groupId = ParamUtil.getIntParam(request, "groupId", -1);
		if(null!=dbid&&dbid>0){
			try {
					List<WeixinMenuentity> childs = weixinMenuentityManageImpl.findBy("weixinMenuentity.dbid", dbid);
					if(null!=childs&&childs.size()>0){
						renderErrorMsg(new Throwable("该数据有子级分类，请先删除子级分类在删除数据！"), "");
						return ;
					}else{
						weixinMenuentityManageImpl.deleteById(dbid);
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
		renderMsg("/weixinMenuentity/queryList"+query+"&groupId="+groupId, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：同步微信菜单
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void sameMenu()  {
		HttpServletRequest request = this.getRequest();
		Integer groupId = ParamUtil.getIntParam(request, "groupId", -1);
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null==weixinAccounts||weixinAccounts.size()<=0){
				renderErrorMsg(new Throwable("同步菜单失败,无公众号信息"), "");
				return ;
			}
			WeixinAccount weixinAccount = weixinAccounts.get(0);
			WeixinAccesstoken weixinAccesstoken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			String sql = "select * from weixin_menuentity where fatherid is null and accountId ='"+ weixinAccount.getDbid() + "' and weixinMenuentityGroupId="+groupId+"  order by  orders asc";
			List<WeixinMenuentity> menuList = weixinMenuentityManageImpl.executeSql(sql, null);
			Menu menu = new Menu();
			Button firstArr[] = new Button[menuList.size()];
			for (int a = 0; a < menuList.size(); a++) {
				WeixinMenuentity entity = menuList.get(a);
				String hqls = "select * from weixin_menuentity where fatherid =" + entity.getDbid()
						+ " and accountId = '" + weixinAccount.getDbid() + "' and weixinMenuentityGroupId="+groupId
						+ "  order by  orders asc";
				List<WeixinMenuentity> childList = weixinMenuentityManageImpl.executeSql(hqls, null);
				if (childList.size() == 0) {
					if("view".equals(entity.getType())){
						ViewButton viewButton = new ViewButton();
						viewButton.setName(entity.getName());
						viewButton.setType(entity.getType());
						viewButton.setUrl(entity.getUrl());
						firstArr[a] = viewButton;
					}else if("click".equals(entity.getType())){
						CommonButton cb = new CommonButton();
						cb.setKey(entity.getDbid()+"P");
						cb.setName(entity.getName());
						cb.setType(entity.getType());
						firstArr[a] = cb;
					}
				} else {
					ComplexButton complexButton = new ComplexButton();
					complexButton.setName(entity.getName());
					Button[] secondARR = new Button[childList.size()];
					for (int i = 0; i < childList.size(); i++) {
						WeixinMenuentity children = childList.get(i);
						String type = children.getType();
						if ("view".equals(type)) {
							ViewButton viewButton = new ViewButton();
							viewButton.setName(children.getName());
							viewButton.setType(children.getType());
							viewButton.setUrl(children.getUrl());
							secondARR[i] = viewButton;

						} else if ("click".equals(type)) {
							CommonButton cb1 = new CommonButton();
							cb1.setName(children.getName());
							cb1.setType(children.getType());
							cb1.setKey(children.getDbid()+"P");
							secondARR[i] = cb1;
						}

					}
					complexButton.setSub_button(secondARR);
					firstArr[a] = complexButton;
				}
			}
			menu.setButton(firstArr);
			JSONObject jsonMenu = JSONObject.fromObject(menu);
			System.out.println("=========="+jsonMenu.toString());
			String accessToken = weixinAccesstoken.getAccessToken();
			if(null==accessToken){
				renderErrorMsg(new Throwable("同步菜单失败！"), "");
				return ;
			}
			String url = WeixinUtil.menu_create_url.replace("ACCESS_TOKEN",accessToken);
			JSONObject jsonObject= new JSONObject();
			try {
				jsonObject = WeixinUtil.httpRequest(url, "POST", jsonMenu.toString());
				System.out.println("==========="+jsonObject);
				if(jsonObject!=null){
					if (0 == jsonObject.getInt("errcode")) {
						message = "同步菜单信息数据成功！";
						renderMsg("",message);
						return ;
					}
					else {
						message = "同步菜单信息数据失败！错误码为："+jsonObject.getInt("errcode")+"错误信息为："+jsonObject.getString("errmsg");
						renderMsg("",message);
						return ;
					}
				}else{
					message = "同步菜单信息数据失败！同步自定义菜单URL地址不正确。";
					renderMsg("",message);
					return ;
				}
			} catch (Exception e) {
				message = "同步菜单信息数据失败！";
				e.printStackTrace();
			}finally{
				System.out.println(""+message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		renderErrorMsg(new Throwable("同步菜单失败！"), "");
		return ;
	}
	/**
	 * 功能描述：删除微信菜单
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void deleteWechatMenu() throws Exception {
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null==weixinAccounts||weixinAccounts.size()<=0){
				renderErrorMsg(new Throwable("同步菜单失败,无公众号信息"), "");
				return ;
			}
			WeixinAccount weixinAccount = weixinAccounts.get(0);
			WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			if(null==accessToken){
				renderErrorMsg(new Throwable("同步菜单失败！"), "");
				return ;
			}
			String url = WeixinUtil.menu_delete_url.replace("ACCESS_TOKEN",accessToken.getAccessToken());
			JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
			System.out.println("==========="+jsonObject.toString());
			if(null!=jsonObject){
				if (0 == jsonObject.getInt("errcode")) {
					message = "同步菜单信息数据成功！";
					renderMsg("",message);
					return ;
				}else{
					message = "同步菜单信息数据失败！错误码为："+jsonObject.getInt("errcode")+"错误信息为："+jsonObject.getString("errmsg");
					renderMsg("",message);
					return ;
				}
			}else{
				message = "同步菜单信息数据失败！同步自定义菜单URL地址不正确。";
				renderMsg("",message);
				return ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
	}
	
	/**
	 * 功能描述：创建个性化菜单
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void addconditional() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer groupId = ParamUtil.getIntParam(request, "groupId", -1);
		try {
			WeixinMenuentityGroup weixinMenuentityGroup = weixinMenuentityGroupManageImpl.get(groupId);
			if(null==weixinMenuentityGroup){
				renderErrorMsg(new Throwable("同步失败，无个性化菜单信息"), "");
				return ;
			}
			if(null==weixinMenuentityGroup.getWeixinMenuentityGroupMatchRule()){
				renderErrorMsg(new Throwable("同步失败，个性化菜单匹配条件为空"), "");
				return ;
			}
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null==weixinAccounts||weixinAccounts.size()<=0){
				renderErrorMsg(new Throwable("同步菜单失败,无公众号信息"), "");
				return ;
			}
			WeixinMenuentityGroupMatchRule weixinMenuentityGroupMatchRule = weixinMenuentityGroup.getWeixinMenuentityGroupMatchRule();
			WeixinAccount weixinAccount = weixinAccounts.get(0);
			WeixinAccesstoken weixinAccesstoken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			String sql = "select * from weixin_menuentity where fatherid is null and accountId ='"+ weixinAccount.getDbid() + "' and weixinMenuentityGroupId="+groupId+"  order by  orders asc";
			List<WeixinMenuentity> menuList = weixinMenuentityManageImpl.executeSql(sql, null);
			MenuConditional menu = new MenuConditional();
			Button firstArr[] = new Button[menuList.size()];
			for (int a = 0; a < menuList.size(); a++) {
				WeixinMenuentity entity = menuList.get(a);
				String hqls = "select * from weixin_menuentity where fatherid=" + entity.getDbid()
						+ " and accountId ='" + weixinAccount.getDbid() + "' and weixinMenuentityGroupId="+groupId
						+ "  order by  orders asc";
				List<WeixinMenuentity> childList = weixinMenuentityManageImpl.executeSql(hqls, null);
				if (childList.size() == 0) {
					if("view".equals(entity.getType())){
						ViewButton viewButton = new ViewButton();
						viewButton.setName(entity.getName());
						viewButton.setType(entity.getType());
						viewButton.setUrl(entity.getUrl());
						firstArr[a] = viewButton;
					}else if("click".equals(entity.getType())){
						CommonButton cb = new CommonButton();
						cb.setKey(entity.getDbid()+"P");
						cb.setName(entity.getName());
						cb.setType(entity.getType());
						firstArr[a] = cb;
					}
				} else {
					ComplexButton complexButton = new ComplexButton();
					complexButton.setName(entity.getName());
					Button[] secondARR = new Button[childList.size()];
					for (int i = 0; i < childList.size(); i++) {
						WeixinMenuentity children = childList.get(i);
						String type = children.getType();
						if ("view".equals(type)) {
							ViewButton viewButton = new ViewButton();
							viewButton.setName(children.getName());
							viewButton.setType(children.getType());
							viewButton.setUrl(children.getUrl());
							secondARR[i] = viewButton;

						} else if ("click".equals(type)) {
							CommonButton cb1 = new CommonButton();
							cb1.setName(children.getName());
							cb1.setType(children.getType());
							cb1.setKey(children.getDbid()+"P");
							secondARR[i] = cb1;
						}

					}
					complexButton.setSub_button(secondARR);
					firstArr[a] = complexButton;
				}
			}
			menu.setButton(firstArr);
			MatchRule matchRule=new MatchRule();
			matchRule.setCity(weixinMenuentityGroupMatchRule.getCity());
			matchRule.setClient_platform_type(weixinMenuentityGroupMatchRule.getClient_platform_type());
			matchRule.setCountry(weixinMenuentityGroupMatchRule.getCountry());
			matchRule.setTag_id(weixinMenuentityGroupMatchRule.getGroup_id());
			matchRule.setLanguage(weixinMenuentityGroupMatchRule.getLanguage());
			matchRule.setProvince(weixinMenuentityGroupMatchRule.getProvince());
			matchRule.setSex(weixinMenuentityGroupMatchRule.getSex());
			menu.setMatchrule(matchRule);	
			JSONObject jsonMenu = JSONObject.fromObject(menu);
			System.out.println(jsonMenu);
			String accessToken = weixinAccesstoken.getAccessToken();
			if(null==accessToken){
				renderErrorMsg(new Throwable("同步菜单失败,Token为空"), "");
				return ;
			}
			String url = WeixinUtil.menu_addconditional.replace("ACCESS_TOKEN",accessToken);
			JSONObject jsonObject= new JSONObject();
			try {
				jsonObject = WeixinUtil.httpRequest(url, "POST", jsonMenu.toString());
				System.out.println("==========="+jsonObject);
				if(jsonObject!=null){
					if(jsonObject.containsKey("menuid")){
						String menuid=jsonObject.getString("menuid");
						if (null!= menuid&&menuid.trim().length()>0) {
							weixinMenuentityGroup.setMenuid(menuid);
							weixinMenuentityGroupManageImpl.save(weixinMenuentityGroup);
							message = "同步菜单信息数据成功！";
							renderMsg("",message);
							return ;
						}
					}
					else {
						message = "同步菜单信息数据失败！错误码为："+jsonObject.getInt("errcode")+"错误信息为："+jsonObject.getString("errmsg");
						renderMsg("",message);
						return ;
					}
				}else{
					message = "同步菜单信息数据失败！同步自定义菜单URL地址不正确。";
					renderMsg("",message);
					return ;
				}
			}catch (Exception e) {
					e.printStackTrace();
					log.error(e);
					renderErrorMsg(e, "");
					return ;
				}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
	}
	/**
	 * 功能描述：删除个性化菜单
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void deleteconditional() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer groupId = ParamUtil.getIntParam(request, "groupId", -1);
		try {
			WeixinMenuentityGroup weixinMenuentityGroup = weixinMenuentityGroupManageImpl.get(groupId);
			if(null==weixinMenuentityGroup){
				renderErrorMsg(new Throwable("删除失败，无个性化菜单信息"), "");
				return ;
			}
			String menuid = weixinMenuentityGroup.getMenuid();
			if(null==menuid||menuid.trim().length()<=0){
				renderErrorMsg(new Throwable("删除失败，无个性化菜单信息"), "");
				return ;
			}
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null==weixinAccounts||weixinAccounts.size()<=0){
				renderErrorMsg(new Throwable("删除失败,无公众号信息"), "");
				return ;
			}
			WeixinAccount weixinAccount = weixinAccounts.get(0);
			WeixinAccesstoken weixinAccesstoken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			JSONObject jsonObject= new JSONObject();
			jsonObject.put("menuid", menuid);
			String url = WeixinUtil.menu_deleteconditional.replace("ACCESS_TOKEN",weixinAccesstoken.getAccessToken());
			JSONObject httpRequest = WeixinUtil.httpRequest(url, "POST", jsonObject.toString());
			if(httpRequest.containsKey("errcode")){
				String errcode = httpRequest.getString("errcode");
				if(errcode.equals("0")){
					renderMsg("","删除个性化菜单成功！");
					return ;
				}else{
					String errmsg = httpRequest.getString("errmsg");
					renderErrorMsg(new Throwable("删除失败,"+errmsg), "");
					return ;
				}
			}else{
				renderErrorMsg(new Throwable("删除个性化菜单失败"),"");
				return ;
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
	}
	/**
	 * 功能描述：测试个性化菜单
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void tryconditional() throws Exception {
		HttpServletRequest request = this.getRequest();
		String user_id = request.getParameter("user_id");
		try {
			if(null==user_id||user_id.trim().length()<=0){
				renderErrorMsg(new Throwable("发送失败，测试微信号为空"), "");
				return ;
			}
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null==weixinAccounts||weixinAccounts.size()<=0){
				renderErrorMsg(new Throwable("发送失败,无公众号信息"), "");
				return ;
			}
			WeixinAccount weixinAccount = weixinAccounts.get(0);
			WeixinAccesstoken weixinAccesstoken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			JSONObject jsonObject= new JSONObject();
			jsonObject.put("user_id", user_id);
			String url = WeixinUtil.menu_tryconditional.replace("ACCESS_TOKEN",weixinAccesstoken.getAccessToken());
			JSONObject httpRequest = WeixinUtil.httpRequest(url, "POST", jsonObject.toString());
			if(httpRequest.containsKey("menu")){
				renderMsg("","测试发送成功！");
				return ;
			}else{
				renderErrorMsg(new Throwable("测试个性化菜单失败"),"");
				return ;
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
	}
}
