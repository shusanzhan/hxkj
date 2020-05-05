package com.ystech.mem.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.mem.model.MemTag;

@Component("memTagManageImpl")
public class MemTagManageImpl extends HibernateEntityDao<MemTag>{

}
