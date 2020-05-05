package com.ystech.weixin.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.mem.model.Member;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.model.WeixinNewsitem;

@Component("weixinNewsitemManageImpl")
public class WeixinNewsitemManageImpl extends HibernateEntityDao<WeixinNewsitem>{
	public WeixinNewsitem getWeixinNewsitem(Member member){
		String sql="select wni.* "
				+ "from "
				+ "	weixin_newsitem wni,weixin_newstemplate wnt "
				+ "where wni.templateid=wnt.dbid AND  wnt.tempType=2 ";
		List<WeixinNewsitem> weixinNewsitems=null;
		if(null!=member){
			Integer enterpriseId = member.getEnterpriseId();
			if(null!=enterpriseId){
				String sql2=sql+" AND wnt.enterpriseId="+enterpriseId;
				weixinNewsitems = executeSql(sql2, null);
			}
		}
		if(null==weixinNewsitems||weixinNewsitems.isEmpty()){
			sql=sql+" AND wnt.enterpriseId=0";
			weixinNewsitems = executeSql(sql, null);
		}
		if(null==weixinNewsitems||weixinNewsitems.isEmpty()){
			return null;
		}
		return weixinNewsitems.get(0);
	}
}
