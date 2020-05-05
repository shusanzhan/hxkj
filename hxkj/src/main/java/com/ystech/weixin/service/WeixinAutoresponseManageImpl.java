package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WeixinAutoresponse;

@Component("weixinAutoresponseManageImpl")
public class WeixinAutoresponseManageImpl extends HibernateEntityDao<WeixinAutoresponse>{

}
