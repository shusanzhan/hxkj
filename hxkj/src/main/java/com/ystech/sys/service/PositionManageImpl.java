package com.ystech.sys.service;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.sys.model.Position;

@Component("positionManageImpl")
public class PositionManageImpl extends HibernateEntityDao<Position>{

}
