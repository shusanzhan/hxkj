/**
 * 
 */
package com.ystech.mem.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.dao.Page;
import com.ystech.mem.model.OnlineBooking;

/**
 * @author shusanzhan
 * @date 2014-2-25
 */
@Component("onlineBookingManageImpl")
public class OnlineBookingManageImpl extends HibernateEntityDao<OnlineBooking>{

	/**
	 * @param pageNo
	 * @param pageSize
	 * @param bookingType
	 * @param status
	 * @return
	 */
	public Page<OnlineBooking> pageQueryByStateAndType(Integer pageNo,
			Integer pageSize, String bookingType, Integer status) {
		String hql="from OnlineBooking where 1=1 ";
		List param=new ArrayList();
		if(null!=bookingType&&bookingType.trim().length()>0){
			hql=hql+" and bookingType=?";
			param.add(bookingType);
		}
		if(null!=status&&status>0){
			hql=hql+" and status=?";
			param.add(status);
		}
		hql=hql+"order by status,bookingDate DESC";
		Page<OnlineBooking> page = pageQuery(pageNo, pageSize, hql, param.toArray());
		
		return page;
	}
	public List<OnlineBooking> getOnlineBookings(String sql){
		List<OnlineBooking> list = getSession().createSQLQuery(sql).addEntity(OnlineBooking.class).list();
		return list;
	}
}
