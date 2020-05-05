package com.ystech.weixin.core.util;

import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;

import net.sf.json.JSONObject;

public class SendMessageUtil {
	public static void sendMessage(WeixinAccesstokenManageImpl weixinAccesstokenManageImpl,WeixinAccount weixinAccount,String openId,String message){
		String jsonObj="{" +
				"\"touser\":\""+openId+"\"," +
				"\"msgtype\":\"text\"," +
				"\"text\":{" +
					"\"content\":\""+message+"\"" +
					"}" +
				"}";
		WeixinAccesstoken accessToken = com.ystech.weixin.core.util.WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
		String send = WeixinUtil.SEND.replace("ACCESS_TOKEN", accessToken.getAccessToken());
		JSONObject httpRequest = com.ystech.weixin.core.util.WeixinUtil.httpRequest(send, "POST", jsonObj);
		System.out.println(httpRequest.toString());
	}
}
