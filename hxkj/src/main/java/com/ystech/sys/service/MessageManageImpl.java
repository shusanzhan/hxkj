/**
 * 
 */
package com.ystech.sys.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.sys.model.Message;

/**
 * @author shusanzhan
 * @date 2014-3-19
 */
@Component("messageManageImpl")
public class MessageManageImpl extends HibernateEntityDao<Message>{
	
}
