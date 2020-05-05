package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WechatMedia;

@Component("wehcatMediaManageImpl")
public class WechatMediaManageImpl extends HibernateEntityDao<WechatMedia>{

}
