package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WeixinExpandconfig;

@Component("weixinExpandconfigManageImpl")
public class WeixinExpandconfigManageImpl extends HibernateEntityDao<WeixinExpandconfig> {

}
