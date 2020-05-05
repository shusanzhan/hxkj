package com.ystech.weixin.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.User;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WechatNewsTemplate;
import com.ystech.weixin.model.WechatSendMessage;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinGroup;
import com.ystech.weixin.service.WechatNewsTemplateManageImpl;
import com.ystech.weixin.service.WechatSendMessageManageImpl;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinGroupManageImpl;
import com.ystech.weixin.service.WeixinGzuserinfoManageImpl;

import net.sf.json.JSONObject;

@Component("wechatSendMessageAction")
@Scope("prototype")
public class WechatSendMessageAction extends BaseController{
	private int openLenght=10000;
	private WechatSendMessageManageImpl wechatSendMessageManageImpl;
	private WeixinGroupManageImpl weixinGroupManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WechatNewsTemplateManageImpl wechatNewsTemplateManageImpl;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	private WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl;
	@Resource
	public void setWechatSendMessageManageImpl(
			WechatSendMessageManageImpl wechatSendMessageManageImpl) {
		this.wechatSendMessageManageImpl = wechatSendMessageManageImpl;
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
	public void setWechatNewsTemplateManageImpl(
			WechatNewsTemplateManageImpl wechatNewsTemplateManageImpl) {
		this.wechatNewsTemplateManageImpl = wechatNewsTemplateManageImpl;
	}
	@Resource
	public void setWeixinAccesstokenManageImpl(
			WeixinAccesstokenManageImpl weixinAccesstokenManageImpl) {
		this.weixinAccesstokenManageImpl = weixinAccesstokenManageImpl;
	}
	@Resource
	public void setWeixinGzuserinfoManageImpl(
			WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl) {
		this.weixinGzuserinfoManageImpl = weixinGzuserinfoManageImpl;
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
		Page<WechatSendMessage> page=null;
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				if(null!=username&&username.trim().length()>0){
					page = wechatSendMessageManageImpl.pagedQuerySql(pageNo, pageSize,WechatSendMessage.class, "select * from weixin_WechatSendMessage where accountId=? and title like '%"+username+"%'", new Object[]{weixinAccount.getDbid()});
				}else{
					page = wechatSendMessageManageImpl.pagedQuerySql(pageNo, pageSize,WechatSendMessage.class, "select * from weixin_WechatSendMessage where accountId=?  ", new Object[]{weixinAccount.getDbid()});
				}
				request.setAttribute("page", page);
			}
		}catch (Exception e) {
			// TODO: handle exception
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
		HttpServletRequest request = this.getRequest();
		try{
			 List<WeixinGroup> weixinGroups = weixinGroupManageImpl.getAll();
			 request.setAttribute("weixinGroups", weixinGroups);
			 Integer wechatNewsTemplateId = ParamUtil.getIntParam(request, "wechatNewsTemplateId", -1);
			 if(wechatNewsTemplateId>0){
				 WechatNewsTemplate wechatNewsTemplate = wechatNewsTemplateManageImpl.get(wechatNewsTemplateId);
				 request.setAttribute("wechatNewsTemplate", wechatNewsTemplate);
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
		try {
			if(dbid>0){
				WechatSendMessage wechatSendMessage = wechatSendMessageManageImpl.get(dbid);
				request.setAttribute("wechatSendMessage", wechatSendMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		Integer newsItemTemplateId = ParamUtil.getIntParam(request, "newsItemTemplateId", -1);
		Integer groupId = ParamUtil.getIntParam(request, "groupId", 0);
		//发送内容类型：图文、文本
		String msgType = request.getParameter("msgType");
		//接受对象类型：1、群发；2、分组；3、openIds
		Integer sendType = ParamUtil.getIntParam(request, "sendType", -1);
		String content = request.getParameter("content");
		//接受openIds
		String openIds = request.getParameter("openIds");
		try{
			WechatSendMessage wechatSendMessage=new WechatSendMessage();
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				wechatSendMessage.setMsgtype(msgType);
				//图文
				if(msgType.equals("mpnews")){
					if(newsItemTemplateId<0){
						renderErrorMsg(new Throwable("请选择素材！"), "");
						return ;
					}
					WechatNewsTemplate wechatNewsTemplate = wechatNewsTemplateManageImpl.get(newsItemTemplateId);
					wechatSendMessage.setDescription(wechatNewsTemplate.getTitle());
					wechatSendMessage.setMediaId(wechatNewsTemplate.getMediaId());
					wechatSendMessage.setWechatNewsTemplate(wechatNewsTemplate);
					wechatSendMessage.setMpnews(null);
				}
				//文本
				if(msgType.equals("text")){
					wechatSendMessage.setMpnews(content);
					wechatSendMessage.setDescription(content);
				}
				
		
				//设置发送类型
				wechatSendMessage.setSendType(sendType);
				wechatSendMessage.setStatus("0");
				//粉丝数
				wechatSendMessage.setTotalCount("0");
				//过滤
				wechatSendMessage.setFilterCount("0");
				//发送成功的粉丝数
				wechatSendMessage.setSentCount("0");
				//发送失败的粉丝数
				wechatSendMessage.setErrorCount("0");
				
				//发送次数
				wechatSendMessage.setSendNum(0);
				//等待发送
				wechatSendMessage.setSendStatus(1);
				
				User currentUser = SecurityUserHolder.getCurrentUser();
				wechatSendMessage.setCreateDate(new Date());
				wechatSendMessage.setCreator(currentUser.getRealName());
				wechatSendMessage.setAccountId(weixinAccount.getDbid());
				if(sendType==1){
					wechatSendMessageManageImpl.save(wechatSendMessage);
				}
				if(sendType==2){
					//当选择用户组室
					if(groupId>0){
						WeixinGroup weixinGroup = weixinGroupManageImpl.get(groupId);
						wechatSendMessage.setGroupId(weixinGroup.getDbid());
						wechatSendMessage.setWeixinGroup(weixinGroup);
						wechatSendMessageManageImpl.save(wechatSendMessage);
					}
				}
				if(sendType==3){
					//选择openIDs
					saveOpenIds(openIds, wechatSendMessage,weixinAccount);
				}
				
				renderMsg("/wechatSendMessage/queryList", "发送成功！");
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
	}
	/**
	 * 保存openIds发送消息
	 * @param openIds
	 * @param wechatSendMessage
	 */
	private void saveOpenIds(String openIds, WechatSendMessage wechatSendMessage,WeixinAccount weixinAccount) {
		//openIds 保存
		if(null!=openIds&&openIds.trim().length()>0){
			String[] split = openIds.split(",");
			if(split.length>openLenght){
				int length = split.length;
				int openIdarrLenght=length/openLenght;
				for (int i=0;i<openIdarrLenght ;i++) {
					System.err.println("bengin:"+i*openLenght+"end:"+(i*openLenght+openLenght) );
					String[] openIdSubS = Arrays.copyOfRange(split, i*openLenght, i*openLenght+openLenght);
					WechatSendMessage wechatSendMessage2=new WechatSendMessage();
					wechatSendMessage2.setAccountId(wechatSendMessage.getAccountId());
					wechatSendMessage2.setCreateDate(wechatSendMessage.getCreateDate());
					wechatSendMessage2.setCreator(wechatSendMessage.getCreator());
					wechatSendMessage2.setDescription(wechatSendMessage.getDescription());
					wechatSendMessage2.setErrorCount(wechatSendMessage.getErrorCount());
					wechatSendMessage2.setFilterCount(wechatSendMessage.getFilterCount());
					wechatSendMessage2.setGroupId(wechatSendMessage.getGroupId());
					wechatSendMessage2.setMediaId(wechatSendMessage.getMediaId());
					wechatSendMessage2.setMpnews(wechatSendMessage.getMpnews());
					wechatSendMessage2.setMsgDataId(wechatSendMessage.getMsgDataId());
					wechatSendMessage2.setMsgStatus(wechatSendMessage.getMsgStatus());
					wechatSendMessage2.setMsgtype(wechatSendMessage.getMsgtype());
					wechatSendMessage2.setSendNum(wechatSendMessage.getSendNum());
					wechatSendMessage2.setStatus(wechatSendMessage.getStatus());
					wechatSendMessage2.setSendStatus(wechatSendMessage.getSendStatus());
					wechatSendMessage2.setSendType(wechatSendMessage.getSendType());
					wechatSendMessage2.setSentCount(wechatSendMessage.getSentCount());
					wechatSendMessage2.setThumbMediaId(wechatSendMessage.getThumbMediaId());
					wechatSendMessage2.setTitle(wechatSendMessage.getTitle());
					wechatSendMessage2.setTotalCount(wechatSendMessage.getTotalCount());
					wechatSendMessage2.setAccountId(weixinAccount.getDbid());
					String string = Arrays.toString(openIdSubS);
					wechatSendMessage2.setTouser(string.replace("[", "").replace("]",""));
					wechatSendMessage2.setWechatNewsTemplate(wechatSendMessage.getWechatNewsTemplate());
					wechatSendMessageManageImpl.save(wechatSendMessage2);
				}
				if(length%openLenght>0){
					System.err.println("bengin:"+openIdarrLenght*openLenght+"end:"+split.length);
					String[] openIdSubS = Arrays.copyOfRange(split, openIdarrLenght*openLenght, length);
					WechatSendMessage wechatSendMessage2=new WechatSendMessage();
					wechatSendMessage2.setAccountId(wechatSendMessage.getAccountId());
					wechatSendMessage2.setCreateDate(wechatSendMessage.getCreateDate());
					wechatSendMessage2.setCreator(wechatSendMessage.getCreator());
					wechatSendMessage2.setDescription(wechatSendMessage.getDescription());
					wechatSendMessage2.setErrorCount(wechatSendMessage.getErrorCount());
					wechatSendMessage2.setFilterCount(wechatSendMessage.getFilterCount());
					wechatSendMessage2.setGroupId(wechatSendMessage.getGroupId());
					wechatSendMessage2.setMediaId(wechatSendMessage.getMediaId());
					wechatSendMessage2.setMpnews(wechatSendMessage.getMpnews());
					wechatSendMessage2.setMsgDataId(wechatSendMessage.getMsgDataId());
					wechatSendMessage2.setMsgStatus(wechatSendMessage.getMsgStatus());
					wechatSendMessage2.setMsgtype(wechatSendMessage.getMsgtype());
					wechatSendMessage2.setSendNum(wechatSendMessage.getSendNum());
					wechatSendMessage2.setStatus(wechatSendMessage.getStatus());
					wechatSendMessage2.setSendStatus(wechatSendMessage.getSendStatus());
					wechatSendMessage2.setSendType(wechatSendMessage.getSendType());
					wechatSendMessage2.setSentCount(wechatSendMessage.getSentCount());
					wechatSendMessage2.setThumbMediaId(wechatSendMessage.getThumbMediaId());
					wechatSendMessage2.setTitle(wechatSendMessage.getTitle());
					wechatSendMessage2.setTotalCount(wechatSendMessage.getTotalCount());
					wechatSendMessage2.setAccountId(weixinAccount.getDbid());
					String string = Arrays.toString(openIdSubS);
					wechatSendMessage2.setTouser(string.replace("[", "").replace("]",""));
					wechatSendMessage2.setWechatNewsTemplate(wechatSendMessage.getWechatNewsTemplate());
					wechatSendMessageManageImpl.save(wechatSendMessage2);
				}
			}else{
				wechatSendMessage.setTouser(openIds);
				wechatSendMessageManageImpl.save(wechatSendMessage);
			}
		}
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void saveSelectGzUser() throws Exception {
		HttpServletRequest request = getRequest();
		Integer newsItemTemplateId = ParamUtil.getIntParam(request, "newsItemTemplateId", -1);
		String msgType = request.getParameter("msgType");
		String content = request.getParameter("content");
		String toUser = request.getParameter("toUser");
		try{
			WechatSendMessage wechatSendMessage=new WechatSendMessage();
			if(msgType.equals("mpnews")){
				if(newsItemTemplateId<0){
					renderErrorMsg(new Throwable("请选择素材！"), "");
					return ;
				}
				WechatNewsTemplate wechatNewsTemplate = wechatNewsTemplateManageImpl.get(newsItemTemplateId);
				wechatSendMessage.setDescription(wechatNewsTemplate.getTitle());
				wechatSendMessage.setMediaId(wechatNewsTemplate.getMediaId());
				wechatSendMessage.setWechatNewsTemplate(wechatNewsTemplate);
				wechatSendMessage.setMpnews(null);
			}
			if(msgType.equals("text")){
				wechatSendMessage.setMpnews(content);
				wechatSendMessage.setDescription(content);
			}
			
			User currentUser = SecurityUserHolder.getCurrentUser();
			wechatSendMessage.setCreateDate(new Date());
			wechatSendMessage.setCreator(currentUser.getRealName());
			
			wechatSendMessage.setStatus("0");
			//粉丝数
			wechatSendMessage.setTotalCount("0");
			//过滤
			wechatSendMessage.setFilterCount("0");
			//发送成功的粉丝数
			wechatSendMessage.setSentCount("0");
			//发送失败的粉丝数
			wechatSendMessage.setErrorCount("0");
			
			wechatSendMessage.setGroupId(null);
			wechatSendMessage.setWeixinGroup(null);
			//渠道为null
			wechatSendMessage.setMsgtype(msgType);
			wechatSendMessage.setTouser(toUser);
			wechatSendMessageManageImpl.save(wechatSendMessage);
			
			boolean send =send(wechatSendMessage);
			if(send==true){
				renderMsg("/wechatSendMessage/queryList", "发送成功！");
				return ;
			}else{
				wechatSendMessageManageImpl.delete(wechatSendMessage);
				renderErrorMsg(new Throwable("发送失败"), "");
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
	 * 功能描述： 参数描述： 逻辑描述：
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
				for (Integer dbid : dbids) {
					WechatSendMessage wechatSendMessage = wechatSendMessageManageImpl.get(dbid);
					wechatSendMessageManageImpl.delete(wechatSendMessage);
					boolean deleteMessage = deleteMessage(wechatSendMessage.getMsgId());
					if(deleteMessage==true){
						renderMsg("/wechatSendMessage/queryList"+query, "删除数据成功！");
						return ;
					}else{
						renderMsg("/wechatSendMessage/queryList"+query, "删除数据成功,微信公众平台同步失败！");
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
		renderMsg("/wechatSendMessage/queryList"+query, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：群发接口（通过groupId）
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public boolean sendAll() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
	/**
	 * 功能描述：群发接口（通过openId）
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public boolean send(WechatSendMessage wechatSendMessage) throws Exception {
		try {
			WeixinAccesstoken weixinAccesstoken = getWeixinAccesstoken();
			String postJson = getRespGroupId(wechatSendMessage);   
			WeixinGroup weixinGroup = wechatSendMessage.getWeixinGroup();
			String sendMessageurl="";
			if(null==weixinGroup){
				//通过openId发送
				sendMessageurl = WeixinUtil.MESSAGESEND.replace("ACCESS_TOKEN", weixinAccesstoken.getAccessToken());
			}else{
				//根据分组Id发送
				sendMessageurl = WeixinUtil.MESSAGESENDALL.replace("ACCESS_TOKEN", weixinAccesstoken.getAccessToken());
			}
			JSONObject httpRequest = WeixinUtil.httpRequest(sendMessageurl, "POST", postJson);
			System.out.println(httpRequest.toString());
			if(null!=httpRequest){
				if(httpRequest.toString().contains("success")){
					String errmsg = httpRequest.getString("errmsg");
					String msg_id = httpRequest.getString("msg_id");
					wechatSendMessage.setMsgId(msg_id);
					if(wechatSendMessage.getMsgtype().equals("mpnews")){
						String msg_data_id = httpRequest.getString("msg_data_id");
						wechatSendMessage.setMsgDataId(msg_data_id);
					}
					wechatSendMessageManageImpl.save(wechatSendMessage);
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
	/**
	 * 功能描述：根据发送（wechatSendMessage）组装分组发送内容
	 * @param wechatSendMessage
	 * @return
	 */
	private String getRespGroupId(WechatSendMessage wechatSendMessage) {
		String postJson="{";
		if(null==wechatSendMessage){
			return null;
		}
		WeixinGroup weixinGroup = wechatSendMessage.getWeixinGroup();
		//群发给所有人员（受限一个月4条记录)
		if(null==weixinGroup){
		}else{
			//群发给指定用户组
			postJson=postJson+"\"filter\":{"+  
					"\"is_to_all\":false,"+ 
					"\"group_id\":\""+wechatSendMessage.getWeixinGroup().getWechatGroupId()+"\""+  
					"},";
		}
		String msgtype = wechatSendMessage.getMsgtype();
		//发送内容为（图文） 
		if(msgtype.equals("mpnews")){
			postJson=postJson+"\"mpnews\":{"+              
									"\"media_id\":\""+wechatSendMessage.getMediaId()+"\""+               
									"},"+
								"\"msgtype\":\"mpnews\""+ 
							"}";
		}
		//发送内容为（文本）
		if(msgtype.equals("text")){
			postJson=postJson+"\"text\":{"+              
					"\"content\":\""+wechatSendMessage.getMpnews()+"\""+               
					"},"+
				"\"msgtype\":\"text\""+ 
			"}";
		}
		return postJson;
	}
	/**
	 * 功能描述：根据发送（wechatSendMessage）组装openid发送内容
	 * @param wechatSendMessage
	 * @return
	 */
	private String[] getRespOPenId(WechatSendMessage wechatSendMessage) {
		List<String> message=new ArrayList<String>();
		if(null==wechatSendMessage){
			return null;
		}
		String touser = wechatSendMessage.getTouser();
		//发送给所有用户
		if(null==touser||touser.trim().length()<=0){
			return null;
		}
		String[] split = touser.split(",");
		int total = split.length;
		if(total<=1000){
			String postJson="{";
			postJson=postJson+"\"touser\":["+touser+"],";
			String msgtype = wechatSendMessage.getMsgtype();
			//发送内容为（图文） 
			if(msgtype.equals("mpnews")){
				postJson=postJson+"\"mpnews\":{"+              
						"\"media_id\":\""+wechatSendMessage.getMediaId()+"\""+               
						"},"+
						"\"msgtype\":\"mpnews\""+ 
						"}";
			}
			//发送内容为（文本）
			if(msgtype.equals("text")){
				postJson=postJson+"\"text\":{"+              
						"\"content\":\""+wechatSendMessage.getMpnews()+"\""+               
						"},"+
						"\"msgtype\":\"text\""+ 
						"}";
			}
			message.add(postJson);
		}else{
			
		}
		return split;
	}
	
	/**
	 * 功能描述：删除群发消息 微信端
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public boolean deleteMessage(String msg_id) throws Exception {
		try {
			if(null==msg_id||msg_id.trim().length()<=0){
				return false;
			}
			String postJson="{"+
					   "\"msg_id\":\""+msg_id+"\""+ 
					"}";
			WeixinAccesstoken weixinAccesstoken = getWeixinAccesstoken();
			String prviewUrl = WeixinUtil.MESSAGEDELETE.replace("ACCESS_TOKEN", weixinAccesstoken.getAccessToken());
			JSONObject httpRequest = WeixinUtil.httpRequest(prviewUrl, "POST", postJson);
			System.out.println("============"+httpRequest.toString());
			if(null!=httpRequest){
				if(httpRequest.toString().contains("ok")){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
	/**
	 * 功能描述：发送数据前预览数据
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void preview() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer newsItemTemplateId = ParamUtil.getIntParam(request, "newsItemTemplateId", -1);
		String wechatId = request.getParameter("wechatId");
		String msgType = request.getParameter("msgType");
		String content = request.getParameter("content");
		try {
			String media_id="";
			WechatNewsTemplate wechatNewsTemplate = wechatNewsTemplateManageImpl.get(newsItemTemplateId);
			if(null!=wechatNewsTemplate){
				media_id=wechatNewsTemplate.getMediaId();
			}
			String postJson="{"+
				   "\"towxname\":\""+wechatId+"\",";
			if(msgType.equals("mpnews")){
				if(null==wechatNewsTemplate){
					renderErrorMsg(new Throwable("返回错误，请选择模板"),"");
					return;
				}
				postJson=postJson+ "\"mpnews\":{"+              
				            "\"media_id\":\""+media_id+"\""+               
				             "},"+
				   "\"msgtype\":\"mpnews\""+ 
				"}";
			}
			if(msgType.equals("text")){
				postJson=postJson+ "\"text\":{"+              
			            "\"content\":\""+content+"\""+               
			             "},"+
			   "\"msgtype\":\"text\""+ 
			"}";
			}
				  
			WeixinAccesstoken weixinAccesstoken = getWeixinAccesstoken();
			String prviewUrl = WeixinUtil.MESSAGEPREVIEW.replace("ACCESS_TOKEN", weixinAccesstoken.getAccessToken());
			JSONObject httpRequest = WeixinUtil.httpRequest(prviewUrl, "POST", postJson);
			System.out.println("============"+httpRequest.toString());
			if(null!=httpRequest){
				if(httpRequest.toString().contains("preview success")){
					renderMsg("", "预览信息成功!");
				}else{
					renderErrorMsg(new Throwable("返回错误："+httpRequest.toString()),"");
					return;
				}
			}else{
				renderErrorMsg(new Throwable("返回结果错误"),"");
				return ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return ;
	}
	/**
	 * 功能描述：选择微信图文素材
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String selectNewsItem() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				List<WechatNewsTemplate> weixinNewstemplates = wechatNewsTemplateManageImpl.find("from WechatNewsTemplate where accountId=? ", new Object[]{weixinAccount.getDbid()});
				request.setAttribute("weixinNewstemplates", weixinNewstemplates);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "selectNewsItem";
	}
	/**
	 * 获取微信accessToken
	 * @return
	 */
	public WeixinAccesstoken getWeixinAccesstoken(){
		Enterprise enterprise = SecurityUserHolder.getEnterprise();
		List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
		if(null!=weixinAccounts&&weixinAccounts.size()>0){
			WeixinAccount weixinAccount = weixinAccounts.get(0);
			WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			return accessToken;
		}else{
			return null;
		}
	}
	/**
	 * 功能描述：群发给所用人员时，获取所有人员的OPenId
	 * @return
	 */
	public String getOpenIds(){
		StringBuffer buffer=new StringBuffer();
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				String sql="select openId from weixin_gzuserinfo where eventStatus=1 and accountid="+weixinAccount.getDbid();
				DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
				Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
				PreparedStatement createStatement = jdbcConnection.prepareStatement(sql);
				ResultSet resultSet = createStatement.executeQuery();
				if(null==resultSet){
					return null;
				}
				while (resultSet.next()) {
					String openId=resultSet.getString("openid");
					buffer.append(",").append("\""+openId+"\"");
				}
				resultSet.close();
				createStatement.close();
				jdbcConnection.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		String resultString = buffer.toString();
		if(null!=resultString&&resultString.trim().length()>0){
			resultString = resultString.replaceFirst(",", "");
		}
		return resultString;
	}
	/**
	 * 
	 * @param spreadDetailId
	 * @return
	 */
	private String getOpenIdsBySpreadDetailIds(Integer spreadDetailId) {
		StringBuffer buffer=new StringBuffer();
		try {
			String sql="select openId from weixin_gzuserinfo where eventStatus=1 and spreadDetailId="+spreadDetailId;
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			PreparedStatement createStatement = jdbcConnection.prepareStatement(sql);
			ResultSet resultSet = createStatement.executeQuery();
			if(null==resultSet){
				return null;
			}
			while (resultSet.next()) {
				String openId=resultSet.getString("openid");
				buffer.append(",").append("\""+openId+"\"");
			}
			resultSet.close();
			createStatement.close();
			jdbcConnection.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		String resultString = buffer.toString();
		if(null!=resultString&&resultString.trim().length()>0){
			resultString = resultString.replaceFirst(",", "");
		}
		return resultString;
	}
	
}
