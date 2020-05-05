package com.ystech.weixin.action;

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
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WechatNewsItem;
import com.ystech.weixin.model.WechatNewsTemplate;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.service.WechatNewsItemManageImpl;
import com.ystech.weixin.service.WechatNewsTemplateManageImpl;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component("wechatNewsItemAction")
@Scope("prototype")
public class WechatNewsItemAction extends BaseController{
	private WechatNewsItem wechatNewsItem;
	private WechatNewsItemManageImpl wechatNewsItemManageImpl;
	private WechatNewsTemplateManageImpl wechatNewsTemplateManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	@Resource
	public void setWechatNewsItemManageImpl(
			WechatNewsItemManageImpl wechatNewsItemManageImpl) {
		this.wechatNewsItemManageImpl = wechatNewsItemManageImpl;
	}
	@Resource
	public void setWechatNewsTemplateManageImpl(
			WechatNewsTemplateManageImpl wechatNewsTemplateManageImpl) {
		this.wechatNewsTemplateManageImpl = wechatNewsTemplateManageImpl;
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
	public WechatNewsItem getWechatNewsItem() {
		return wechatNewsItem;
	}
	public void setWechatNewsItem(WechatNewsItem wechatNewsItem) {
		this.wechatNewsItem = wechatNewsItem;
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
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				List<WechatNewsTemplate> wechatNewsTemplates = wechatNewsTemplateManageImpl.find("from WechatNewsTemplate where accountId="+weixinAccount.getDbid(), new Object[]{});
				request.setAttribute("wechatNewsTemplates", wechatNewsTemplates);
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
			WechatNewsTemplate wechatNewsTemplate = wechatNewsTemplateManageImpl.get(dbid);
			request.setAttribute("wechatNewsTemplate", wechatNewsTemplate);
			Set<WechatNewsItem> wechatNewsItems = wechatNewsTemplate.getWechatNewsitems();
			if(null!=wechatNewsItems&&wechatNewsItems.size()>0){
				int i=0;
				for (WechatNewsItem wechatNewsItem : wechatNewsItems) {
					if(i==0){
						request.setAttribute("wechatNewsItem", wechatNewsItem);
					}
				}
			}
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
			WechatNewsTemplate wechatNewsTemplate = wechatNewsTemplateManageImpl.get(dbid);
			request.setAttribute("wechatNewsTemplate", wechatNewsTemplate);
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
		Integer previewStatus = ParamUtil.getIntParam(request, "previewStatus", -1);
		Integer wechatTemplateDbid = ParamUtil.getIntParam(request, "wechatTemplateDbid", -1);
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				String contentArea = request.getParameter("contentArea");
				WechatNewsTemplate wechatNewsTemplate=null;
				if(wechatTemplateDbid>0){
					wechatNewsTemplate=wechatNewsTemplateManageImpl.get(wechatTemplateDbid);
				}else{
					wechatNewsTemplate=new WechatNewsTemplate();
					String addtime = DateUtil.format2(new Date());
					wechatNewsTemplate.setAddtime(addtime);
					wechatNewsTemplate.setType(type);
				}
				wechatNewsTemplate.setAccountId(weixinAccount.getDbid());
				wechatNewsTemplate.setTitle(wechatNewsItem.getTitle());
				wechatNewsTemplateManageImpl.save(wechatNewsTemplate);	
				
				wechatNewsItem.setContent(contentArea);
				wechatNewsItem.setWechatNewsTemplate(wechatNewsTemplate);
				wechatNewsItemManageImpl.save(wechatNewsItem);
				
				boolean addNews=false;
				//更新图文消息
				if(null!=wechatNewsTemplate.getMediaId()&&wechatNewsTemplate.getMediaId().trim().length()>0){
					addNews = updateNews(wechatNewsTemplate);
				}else{
					//同步多图文至微信后台
					wechatNewsItem.setWechatNewsTemplate(null);
					JSONObject jsonObject=JSONObject.fromObject(wechatNewsItem);
					String json="["+jsonObject.toString()+"]";
					//新增图文消息
					addNews = addNews(json, wechatNewsTemplate);
					wechatNewsItem.setWechatNewsTemplate(wechatNewsTemplate);
					wechatNewsItemManageImpl.save(wechatNewsItem);
				}
				if(addNews==true){
					if(previewStatus==1){
						renderMsg(wechatNewsTemplate.getDbid()+"", "保存数据成功！");
					}
					if(previewStatus==2){
						renderMsg("/wechatSendMessage/add?wechatNewsTemplateId="+wechatNewsTemplate.getDbid(), "保存数据成功,页面跳转到信息群发页面！");
						return ;
					}
					else{
						renderMsg("/wechatNewsItem/queryList", "保存数据成功！");
					}
					return ;
				}else{
					renderErrorMsg(new Throwable("同步图文至微信发生错误"), "");
					return ;
				}
			}else{
				renderErrorMsg(new Throwable("保存数据失败，系统无公众号信息"), "");
				return ;
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
	}
	/**
	 * 功能描述：保存多图文
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveMore() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer type = ParamUtil.getIntParam(request, "type", 2);
		Integer wechatTemplateDbid = ParamUtil.getIntParam(request, "wechatTemplateDbid", -1);
		Integer previewStatus = ParamUtil.getIntParam(request, "previewStatus", -1);
		String jsonData = request.getParameter("jsonData");
		try{
			JSONArray fromObject = JSONArray.fromObject(jsonData);
			List<WechatNewsItem> wechatNewsitems = JSONArray.toList(fromObject,WechatNewsItem.class);
			if(null==wechatNewsitems||wechatNewsitems.size()<=0){
				renderErrorMsg(new Throwable("未添加内容信息，请确定！"),"");
				return ;
			}
			WechatNewsTemplate wechatNewsTemplate=null;
			if(wechatTemplateDbid>0){
				wechatNewsTemplate=wechatNewsTemplateManageImpl.get(wechatTemplateDbid);
			}else{
				wechatNewsTemplate=new WechatNewsTemplate();
				String addtime = DateUtil.format2(new Date());
				wechatNewsTemplate.setAddtime(addtime);
				wechatNewsTemplate.setType(type);
			}
			wechatNewsTemplate.setTitle(wechatNewsitems.get(0).getTitle());
			wechatNewsTemplateManageImpl.save(wechatNewsTemplate);	
			
			//新增图文数据
			if(wechatTemplateDbid<0){
				for (WechatNewsItem wechatNewsItem : wechatNewsitems) {
					wechatNewsItem.setWechatNewsTemplate(wechatNewsTemplate);
					wechatNewsItemManageImpl.save(wechatNewsItem);
				}
				boolean addNews = addNews(fromObject.toString(), wechatNewsTemplate);
				if(addNews==true){
					if(previewStatus==1){
						renderMsg(wechatNewsTemplate.getDbid()+"", "保存数据成功！");
					}
					if(previewStatus==2){
						renderMsg("/wechatSendMessage/add?wechatNewsTemplateId="+wechatNewsTemplate.getDbid(), "保存数据成功,页面跳转到信息群发页面！");
						return ;
					}
					else{
						renderMsg("/wechatNewsItem/queryList", "保存数据成功！");
					}
					return ;
				}else{
					renderErrorMsg(new Throwable("同步图文至微信发生错误"), "");
					return ;
				}
			}else{
				//更新数据
				for (WechatNewsItem wechatNewsItem : wechatNewsitems) {
					if(null!=wechatNewsItem.getDbid()&&wechatNewsItem.getDbid()>0){
						WechatNewsItem wechatNewsItem2 = wechatNewsItemManageImpl.get(wechatNewsItem.getDbid());
						wechatNewsItem2.setAuthor(wechatNewsItem.getAuthor());
						wechatNewsItem2.setContent(wechatNewsItem.getContent());
						wechatNewsItem2.setTitle(wechatNewsItem.getTitle());
						wechatNewsItem2.setContent_source_url(wechatNewsItem.getContent_source_url());
						wechatNewsItem2.setDigest(wechatNewsItem.getDigest());
						wechatNewsItem2.setShow_cover_pic(wechatNewsItem.getShow_cover_pic());
						wechatNewsItem2.setWechatNewsTemplate(wechatNewsTemplate);
						wechatNewsItemManageImpl.save(wechatNewsItem2);
					}else{
						wechatNewsItem.setWechatNewsTemplate(wechatNewsTemplate);
						wechatNewsItemManageImpl.save(wechatNewsItem);
					}
				}
				//更新数据
				boolean updateNews = updateNews(wechatNewsTemplate);
				if(updateNews==true){
					if(previewStatus==1){
						renderMsg(wechatNewsTemplate.getDbid()+"", "保存数据成功！");
					}
					if(previewStatus==2){
						renderMsg("/wechatSendMessage/add?wechatNewsTemplateId="+wechatNewsTemplate.getDbid(), "保存数据成功,页面跳转到信息群发页面！");
						return ;
					}
					else{
						renderMsg("/wechatNewsItem/queryList", "保存数据成功！");
					}
					return ;
				}else{
					renderErrorMsg(new Throwable("同步图文至微信发生错误"), "");
					return ;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
		}
		renderMsg("/wechatNewsItem/queryList", "保存数据成功！");
		return ;
	}
	/**
	 * 功能描述：删除多图文中单图文
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void deleteNewsItem() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid",-1);
		try {
			wechatNewsItemManageImpl.deleteById(dbid);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return;
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
		String query = ParamUtil.getQueryUrl(request);
		try {
			if(null!=dbids&&dbids.length>0){
			for (Integer dbid : dbids) {
					WechatNewsTemplate wechatNewsTemplate = wechatNewsTemplateManageImpl.get(dbid);
					String mediaId = wechatNewsTemplate.getMediaId();
					wechatNewsTemplateManageImpl.deleteById(dbid);
					boolean deleteNews = deleteNews(mediaId);
					if(deleteNews==true){
						renderMsg("/wechatNewsItem/queryList"+query, "删除数据成功！");
						return ;
					}else{
						renderMsg("/wechatNewsItem/queryList"+query, "服务器数据成功,微信端数据删除失败！");
						return;
					}
				}
			}
			else{
				renderErrorMsg(new Throwable("未选中数据！"), "");
				return ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		
		renderMsg("/wechatNewsItem/queryList"+query, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：同步多图文至微信素材，并记录下多图文微信端素材的media_id
	 * 参数描述：
	 * 逻辑描述：json格式为[{},{}]
	 * @return
	 * @throws Exception
	 */
	public boolean addNews(String json,WechatNewsTemplate wechatNewsTemplate) throws Exception {
		try {
			String targetJson="{\"articles\": "+json+"}";
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			if(null!=weixinAccount){
				WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
				String addNews = WeixinUtil.ADDNEWS.replace("ACCESS_TOKEN", accessToken.getAccessToken());
				JSONObject jsonObject = WeixinUtil.httpRequest(addNews, "POST", targetJson);
				if(null!=jsonObject){
					System.err.println(jsonObject.toString());
					String media = jsonObject.toString();
					if(media.contains("errcode")){
						return false;
					}
					if(media.contains("media_id")){
						String media_id = jsonObject.getString("media_id");
						wechatNewsTemplate.setMediaId(media_id);
						wechatNewsTemplateManageImpl.save(wechatNewsTemplate);
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
	/**
	 * 功能描述：删除远程微信 公众平台图片
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public boolean deleteNews(String mediaId) throws Exception {
		String targetJson="{\"media_id\":"+mediaId+"}";
		try {
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			if(null!=weixinAccount){
				WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
				String addNews = WeixinUtil.DELMATERIAL.replace("ACCESS_TOKEN", accessToken.getAccessToken());
				JSONObject jsonObject = WeixinUtil.httpRequest(addNews, "POST", targetJson);
				if(null!=jsonObject){
					String errcode = jsonObject.getString("errcode");
					if(errcode.contains("0")){
						return true;
					}else{
						System.err.println("erroCode:"+jsonObject.getString("errcode")+"  errmsg:"+jsonObject.getString("errmsg"));
						return false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
	/**
	 * 功能描述：更新图文消息
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public boolean updateNews(WechatNewsTemplate wechatNewsTemplate) throws Exception {
		try {
			Set<WechatNewsItem> wechatNewsitems = wechatNewsTemplate.getWechatNewsitems();
			if(null==wechatNewsitems||wechatNewsitems.size()<=0){
				return false;
			}
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			if(null!=weixinAccount){
				WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
				String updateNews = WeixinUtil.NEWSUPDATENEWS.replace("ACCESS_TOKEN", accessToken.getAccessToken());
				int i=0;
				boolean status=true;
				for (WechatNewsItem wechatNewsItem : wechatNewsitems) {
					wechatNewsItem.setWechatNewsTemplate(null);
					JSONObject jsonObject=JSONObject.fromObject(wechatNewsItem);
					String targetJson="{\"media_id\":\""+wechatNewsTemplate.getMediaId()+"\"," +
							"\"index\":"+i+","+
							"\"articles\": "+jsonObject.toString() +
							"}";
					JSONObject resutObject = WeixinUtil.httpRequest(updateNews, "POST", targetJson);
					String errcode = resutObject.getString("errcode");
					if(null!=errcode){
						if(!errcode.equals("0")){
							status=false;
						}
					}else{
						status=false;
					}
					i++;
				}
				return status;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
}
