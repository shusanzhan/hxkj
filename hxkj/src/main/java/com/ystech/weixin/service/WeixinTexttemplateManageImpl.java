package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WeixinTexttemplate;

@Component("weixinTexttemplateManageImpl")
public class WeixinTexttemplateManageImpl extends HibernateEntityDao<WeixinTexttemplate> {

}
