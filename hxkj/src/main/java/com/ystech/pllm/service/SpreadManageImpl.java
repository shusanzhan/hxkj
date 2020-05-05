package com.ystech.pllm.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.pllm.model.Spread;

@Component("spreadManageImpl")
public class SpreadManageImpl extends HibernateEntityDao<Spread>{
	
	public List<Spread> queryCount(String dateSql){
		String sql="SELECT "
				+ "sre.`name`,IFNULL(A.countNum,0) AS countNum "
				+ "FROM  "
				+ "pllm_s_spread sre "
				+ "LEFT JOIN "
				+ "( "
				+ "SELECT spread.dbid,COUNT(spread.dbid) AS countNum FROM "
				+ "pllm_s_spreaddetailrecord sr,pllm_s_spread spread "
				+ "WHERE "
				+ "sr.spreadId=spread.dbid "+dateSql
				+ "GROUP BY "
				+ "spread.dbid)A "
				+ "ON A.dbid=sre.dbid";
		List<Spread> spreads=new ArrayList<Spread>();
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			Statement createStatement = jdbcConnection.createStatement();
			ResultSet resultSet = createStatement.executeQuery(sql);
			resultSet.last();
			resultSet.beforeFirst();
			while (resultSet.next()) {
				String province = resultSet.getString("name");
				Integer countNum = resultSet.getInt("countNum");
				Spread spread = new Spread();
				spread.setCountNum(countNum);
				spread.setName(province);
				spreads.add(spread);
			}
			createStatement.close();
			jdbcConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return spreads;
	}
}
