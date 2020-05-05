package com.ystech.sys.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.sys.model.SystemInfo;

@Component("systemInfoMangeImpl")
public class SystemInfoMangeImpl extends HibernateEntityDao<SystemInfo>{
	public boolean isSynQywx(){
		List<SystemInfo> systemInfos = getAll();
		if(null==systemInfos||systemInfos.isEmpty()){
			return false;
		}
		SystemInfo systemInfo = systemInfos.get(0);
		if(null==systemInfo){
			return false;
		}
		if(systemInfo.getWxUserStatus()==2){
			return true;
		}
		return false;
	}
	
}
