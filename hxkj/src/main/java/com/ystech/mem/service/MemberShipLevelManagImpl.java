/**
 * 
 */
package com.ystech.mem.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.mem.model.MemberShipLevel;

/**
 * @author shusanzhan
 * @date 2014-1-16
 */
@Component("memberShipLevelManagImpl")
public class MemberShipLevelManagImpl extends HibernateEntityDao<MemberShipLevel>{
	public List<MemberShipLevel> queryCount(){
		String memberShipSql="SELECT ms.`name`,IFNULL(A.count,0) as countNum "
				+ " FROM mem_membershiplevel ms "
				+ "LEFT JOIN "
				+ "("
				+ "SELECT"
				+ " ms.dbid,ms.`name`,COUNT(ms.dbid) count "
				+ "FROM mem_member mem,mem_membershiplevel ms "
				+ "WHERE "
				+ "mem.memberShipLevelId=ms.dbid GROUP BY ms.dbid "
				+ ")A"
				+ " ON ms.dbid=A.dbid";
		List<MemberShipLevel> gzUserProvinces=new ArrayList<MemberShipLevel>();
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			Statement createStatement = jdbcConnection.createStatement();
			ResultSet resultSet = createStatement.executeQuery(memberShipSql);
			resultSet.last();
			resultSet.beforeFirst();
			while (resultSet.next()) {
				String province = resultSet.getString("name");
				Integer countNum = resultSet.getInt("countNum");
				MemberShipLevel statisticalDesk = new MemberShipLevel();
				statisticalDesk.setCount(countNum);
				statisticalDesk.setName(province);
				gzUserProvinces.add(statisticalDesk);
			}
			createStatement.close();
			jdbcConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return gzUserProvinces;
	}
}
