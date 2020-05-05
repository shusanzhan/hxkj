package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WeixinAccesstoken;

@Component("weixinAccesstokenManageImpl")
public class WeixinAccesstokenManageImpl extends HibernateEntityDao<WeixinAccesstoken>{

	public void updateSql(WeixinAccesstoken accessTocken) {
		String sql = "update weixin_accesstoken set access_token='"+accessTocken.getAccessToken()+"',expires_ib="+accessTocken.getExpiresIb()+",jsapiTicket='"+accessTocken.getJsapiTicket()+"',addtime=now() where dbid="+accessTocken.getDbid();
    	executeSql(sql);
	}

}
