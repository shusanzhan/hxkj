package com.ystech.mem.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.mem.model.Reward;

@Component("rewardManageImpl")
public class RewardDao extends HibernateEntityDao<Reward>{
	/**
	 * 功能描述：查询订单
	 * @param memberIds
	 * @return
	 */
	public List<Reward> queryFanli(String memberIds,Integer parentId){
		if(null!=memberIds&&memberIds.trim().length()>0){
			String sql="SELECT * FROM mem_reward WHERE  memberid IN("+memberIds+") and parentId="+parentId;
			List<Reward> fanlis = executeSql(sql, null);
			return fanlis;
		}
		return null;
	}
	public Long countNum(String memberIds,Integer parentId){
		if(null!=memberIds&&memberIds.trim().length()>0){
			String sql="SELECT count(*) FROM  mem_reward WHERE  memberid IN("+memberIds+") and parentId="+parentId;
			BigInteger countNum  = count(sql, null);
			return countNum.longValue();
		}
		return Long.valueOf(0);
	}
	public Integer getCurrentMonth(Integer agentId){
		String sql="SELECT SUM(rewardMoney) AS totalMoney FROM mem_reward WHERE DATE_FORMAT(createTime,'%Y-%m')=DATE_FORMAT(CURDATE(),'%Y-%m') and memberId="+agentId;
		Long totalMoney=Long.valueOf(0);
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			PreparedStatement createStatement = jdbcConnection.prepareStatement(sql);
			ResultSet resultSet = createStatement.executeQuery();
			while (resultSet.next()) {  
				totalMoney = resultSet.getLong("totalMoney");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return totalMoney.intValue();
	}
		/**
	 * 功能描述：删除推荐客户返利
	 * @param recommendCustomerId
	 */
	public void deleteBySql(Integer recommendCustomerId){
		String deleteSql="delete from mem_reward where recommendCustomerId="+recommendCustomerId;
		executeSql(deleteSql);
	}
}
