/**
 * 
 */
package com.ystech.sys.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.sys.model.OperateLog;

/**
 * @author shusanzhan
 * @date 2013-6-22
 */
@Component("operateLogManageImpl")
public class OperateLogManageImpl extends HibernateEntityDao<OperateLog>{

}
