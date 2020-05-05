package com.ystech.weixin.core.util;

import net.sf.json.JSONObject;

public class WechatMessageUtil {
	public static WechatMessage paraseErrorMessage(JSONObject jsonObject){
		try {
			if(null!=jsonObject){
				WechatMessage errorMessage =new WechatMessage();
				Integer errcode=(Integer)jsonObject.get("errcode");
				String errmsg=(String)jsonObject.get("errmsg");
				errorMessage.setErrcode(errcode+"");
				errorMessage.setErrmsg(errmsg);
				return errorMessage;
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			return null;
		}
	}
}
