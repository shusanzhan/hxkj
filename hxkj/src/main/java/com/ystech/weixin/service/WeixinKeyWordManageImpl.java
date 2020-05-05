package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WeixinKeyWord;

@Component("weixinKeyWordManageImpl")
public class WeixinKeyWordManageImpl extends HibernateEntityDao<WeixinKeyWord>{

	public void deleteByRoleId(Integer keywordroleId) {
		String sql="delete  from weixin_keyword where keywordroleId="+keywordroleId;
		executeSql(sql);
	}

}
