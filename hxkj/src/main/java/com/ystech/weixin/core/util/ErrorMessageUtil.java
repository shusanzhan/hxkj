package com.ystech.weixin.core.util;

import net.sf.json.JSONObject;

public class ErrorMessageUtil {
	public static ErrorMessage paraseErrorMessage(JSONObject jsonObject){
		try {
			if(null!=jsonObject){
				ErrorMessage errorMessage =new ErrorMessage();
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
