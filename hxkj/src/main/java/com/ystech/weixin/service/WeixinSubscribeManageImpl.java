package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WeixinSubscribe;

@Component("weixinSubscribeManageImpl")
public class WeixinSubscribeManageImpl extends HibernateEntityDao<WeixinSubscribe>{

}
