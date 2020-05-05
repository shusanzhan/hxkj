package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WeixinKeyAutoresponse;

@Component("weixinKeyAutoresponseManageImpl")
public class WeixinKeyAutoresponseManageImpl extends HibernateEntityDao<WeixinKeyAutoresponse>{

	public void deleteByRoleId(Integer keywordroleId) {
		// TODO Auto-generated method stub
		String sql="delete  from weixin_keyautoresponse where keywordroleId="+keywordroleId;
		executeSql(sql);
		
	}

}
