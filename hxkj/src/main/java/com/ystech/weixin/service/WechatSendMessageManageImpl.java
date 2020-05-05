package com.ystech.weixin.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WechatSendMessage;

@Component("wechatSendMessageManageImpl")
public class WechatSendMessageManageImpl extends HibernateEntityDao<WechatSendMessage>{

}
