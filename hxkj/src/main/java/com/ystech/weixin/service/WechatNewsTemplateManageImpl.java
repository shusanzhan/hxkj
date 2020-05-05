package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WechatNewsTemplate;

@Component("wechatNewsTemplateManageImpl")
public class WechatNewsTemplateManageImpl extends HibernateEntityDao<WechatNewsTemplate>{

}
