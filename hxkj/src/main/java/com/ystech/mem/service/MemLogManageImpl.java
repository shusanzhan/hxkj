package com.ystech.mem.service;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.mem.model.MemLog;
import com.ystech.sys.model.User;

@Component("memLogManageImpl")
public class MemLogManageImpl extends HibernateEntityDao<MemLog>{
	public void saveMemberOperatorLog(Integer memberId,String type,String note){
		User currentUser = SecurityUserHolder.getCurrentUser();
		MemLog memLog=new MemLog();
		memLog.setMemberId(memberId);
		memLog.setOperateDate(new Date());
		memLog.setOperator(currentUser.getRealName());
		memLog.setType(type);
		memLog.setNote(note);
		save(memLog);
	}
	public void saveMemberOperatorLog(Integer memberId,String type,String note,User user){
		MemLog memLog=new MemLog();
		memLog.setMemberId(memberId);
		memLog.setOperateDate(new Date());
		memLog.setOperator(user.getRealName());
		memLog.setType(type);
		memLog.setNote(note);
		save(memLog);
	}
}
