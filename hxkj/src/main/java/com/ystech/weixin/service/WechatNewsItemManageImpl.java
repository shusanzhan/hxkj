package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WechatNewsItem;
@Component("wechatNewsItemManageImpl")
public class WechatNewsItemManageImpl extends HibernateEntityDao<WechatNewsItem>{

}
