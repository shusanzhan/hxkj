package com.ystech.weixin.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WeixinGroup;

@Component("weixinGroupManageImpl")
public class WeixinGroupManageImpl extends HibernateEntityDao<WeixinGroup>{
	public List<WeixinGroup> getTotalWeixinGroups(){
		String sql="SELECT " +
				"wgroup.dbid,wgroup.accountId,wgroup.dis,wgroup.isCommon,wgroup.`name`,wgroup.wechatGroupId,COUNT(wgroup.dbid) AS totalNum " +
				"FROM " +
				"weixin_group wgroup,weixin_gzuserinfo gzuser WHERE wgroup.wechatGroupId=gzuser.groupId " +
				"GROUP BY wgroup.dbid";
		List<WeixinGroup> weixinGroups= createQuerySql(sql, WeixinGroup.class, null);
		return weixinGroups;
	}
}
