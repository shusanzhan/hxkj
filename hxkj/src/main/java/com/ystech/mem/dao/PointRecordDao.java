/**
 * 
 */
package com.ystech.mem.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.dao.Page;
import com.ystech.core.util.DateUtil;
import com.ystech.mem.model.PointRecord;

/**
 * @author shusanzhan
 * @date 2014-2-26
 */
@Component("pointRecordManageImpl")
public class PointRecordDao extends HibernateEntityDao<PointRecord>{
	public Page<PointRecord> pageQuery(Integer pageNo,Integer pageSize,String creator,String startTime,String endTime,Integer  bussinesId){
		List param=new ArrayList();
		String sql="select * from mem_PointRecord where 1=1 and businessId=? ";
		param.add(bussinesId);
		if(null!=creator&&creator.trim().length()>0){
			sql=sql+" and creator like  ? ";
			param.add("%"+creator+"%");
		}
		if(null!=startTime&&startTime.trim().length()>0){
			sql=sql+" and createTime >= ? ";
			param.add(DateUtil.string2Date(startTime));
		}
		if(null!=endTime&&endTime.trim().length()>0){
			sql=sql+" and createTime < ? ";
			param.add(DateUtil.nextDay(endTime));
			
		}
		sql=sql+" order by createTime DESC";
		Page<PointRecord> page= pagedQuerySql(pageNo, pageSize, PointRecord.class, sql, param.toArray());
		return page;
	}
	
	public Object count(String creator,String startTime,String endTime,Boolean status,Integer  bussinesId){
		String countSql="select sum(num) from mem_PointRecord where 1=1  and enterpriseId=? ";
		List param=new ArrayList();
		param.add(bussinesId);
		if(null!=creator&&creator.trim().length()>0){
			countSql=countSql+" and creator like  ? ";
			param.add("%"+creator+"%");
		}
		if(null!=startTime&&startTime.trim().length()>0){
			countSql=countSql+" and createTime >= ? ";
			param.add(DateUtil.string2Date(startTime));
		}
		if(null!=endTime&&endTime.trim().length()>0){
			countSql=countSql+" and createTime < ? ";
			param.add(DateUtil.nextDay(endTime));
		}
		if(status){
			countSql=countSql+" and num>0 ";
		}else{
			countSql=countSql+" and num<=0 ";
		}
		Object count = count(countSql, param.toArray());
		return count;
	}

	/**
	 * @param creator
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PointRecord> queryToExcel(String creator, String startTime,
			String endTime,Integer  bussinesId) {
		@SuppressWarnings("rawtypes")
		List param=new ArrayList();
		String sql="select * from mem_PointRecord where 1=1 and businessId=? ";
		param.add(bussinesId);
		if(null!=creator&&creator.trim().length()>0){
			sql=sql+" and creator like  ? ";
			param.add("%"+creator+"%");
		}
		if(null!=startTime&&startTime.trim().length()>0){
			sql=sql+" and createTime >= ? ";
			param.add(DateUtil.string2Date(startTime));
		}
		if(null!=endTime&&endTime.trim().length()>0){
			sql=sql+" and createTime < ? ";
			param.add(DateUtil.nextDay(endTime));
		}
		sql=sql+" order by createTime DESC";
		List<PointRecord> pointRecords = executeSql(sql, param.toArray());
		return pointRecords;
	}

	/**
	 * @param string
	 */
	public void deleteByMemberId(String sql) {
		executeSql(sql);
	}

	public void deleteBySql(int recommendCustomerId) {
		String deleteSql="delete from mem_PointRecord where recommendCustomerId="+recommendCustomerId;
		executeSql(deleteSql);
	}
}
