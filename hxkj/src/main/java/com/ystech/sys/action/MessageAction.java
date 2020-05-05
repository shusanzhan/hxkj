/**
 * 
 */
package com.ystech.sys.action;

import java.util.List;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Message;
import com.ystech.sys.model.ReceiveMessageUser;
import com.ystech.sys.model.User;
import com.ystech.sys.service.MessageManageImpl;
import com.ystech.sys.service.ReceiveMessageUserManageImpl;

/**
 * @author shusanzhan
 * @date 2014-3-19
 */
@Component("messageAction")
@Scope("prototype")
public class MessageAction extends BaseController{
	private MessageManageImpl messageManageImpl;
	private ReceiveMessageUserManageImpl receiveMessageUserManageImpl;
	public MessageManageImpl getMessageManageImpl() {
		return messageManageImpl;
	}
	@Resource
	public void setMessageManageImpl(MessageManageImpl messageManageImpl) {
		this.messageManageImpl = messageManageImpl;
	}
	
	public ReceiveMessageUserManageImpl getReceiveMessageUserManageImpl() {
		return receiveMessageUserManageImpl;
	}
	@Resource
	public void setReceiveMessageUserManageImpl(
			ReceiveMessageUserManageImpl receiveMessageUserManageImpl) {
		this.receiveMessageUserManageImpl = receiveMessageUserManageImpl;
	}
	/**
	 * 功能描述：在线预约提示方法，每次只查询一条记录
	 * 参数描述：无参数
	 * 逻辑描述：根据微信端用户提交信息，查询实时更新的预约
	 * @return
	 * @throws Exception
	 */
	public void noticeMessage() throws Exception {
		JSONObject jsonObject=new JSONObject();  
		try{
			User currentUser = SecurityUserHolder.getCurrentUser();
			if(null!=currentUser){
				List<Message> messages = messageManageImpl.find("from Message where isNotice=? and userId=? ", new Object[]{false,currentUser.getDbid()});
				if(null!=messages&&messages.size()>0){
					Message message = messages.get(0);
					jsonObject.put("dbid", message.getDbid());
					jsonObject.put("title", message.getTitle());
					jsonObject.put("url", message.getUrl());
					jsonObject.put("content", message.getContent());
					jsonObject.put("status", true);
					//修改提示信息状态
					message.setIsNotice(true);
					messageManageImpl.save(message);
					renderJson(jsonObject.toString());
				}
				else{
					jsonObject.put("status", false);
					renderJson(jsonObject.toString());
				}
			}else{
				jsonObject.put("status", false);
				renderJson(jsonObject.toString());
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			jsonObject.put("status", false);
			renderJson(jsonObject.toString());
			return ;
		}
		return ;
	}

	public void sendMessage(String title,String content,String url){
		try{
			Integer[] receiveMessageUserIds = receiveMessageUserIds();
		if(null!=receiveMessageUserIds&&receiveMessageUserIds.length>0){
			for (Integer userId : receiveMessageUserIds) {
				Message message=new Message();
				message.setTitle(title);
				message.setContent(content);
				message.setUrl(url);
				message.setIsNotice(false);
				message.setIsRead(false);
				message.setUserId(userId);
				messageManageImpl.save(message);
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Integer[] receiveMessageUserIds(){
		List<ReceiveMessageUser> receiveMessageUsers = receiveMessageUserManageImpl.getAll();
		Integer[] integers=new Integer[]{};
		if(null!=receiveMessageUsers&&receiveMessageUsers.size()>0){
			for (int i=0;i<receiveMessageUsers.size() ;i++) {
				ReceiveMessageUser receiveMessageUser = receiveMessageUsers.get(i+1);
				integers[i]=receiveMessageUser.getUser().getDbid();
			}
		}
		return integers;
	}
}
