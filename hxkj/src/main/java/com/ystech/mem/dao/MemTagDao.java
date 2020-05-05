package com.ystech.mem.dao;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.mem.model.MemTag;

@Component("memTagManageImpl")
public class MemTagDao extends HibernateEntityDao<MemTag>{

}
