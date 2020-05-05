package com.ystech.pllm.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.mem.model.DateNum;
import com.ystech.pllm.model.RecShareRecord;

@Component("recShareRecordManageImpl")
public class RecShareRecordManageImpl extends HibernateEntityDao<RecShareRecord>{
	/**
	 * 批量生成优惠吗
	 * @param coupon
	 */
	public List<DateNum> queryShareCount7(){
		String sql="SELECT "
				+ "DATE_FORMAT(shareTime,'%Y-%m-%d') AS createTime,"
				+ "COUNT(*) AS shareNum "
				+ "FROM  "
				+ "pllm_rec_sharerecord "
				+ "WHERE  "
				+ "TO_DAYS(NOW()) - TO_DAYS(shareTime)<7 and type=2 "
				+ "GROUP BY "
				+ " DATE_FORMAT(shareTime,'%y-%m-%d')";
		List<DateNum> dateNums=new ArrayList<DateNum>();
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			Statement createStatement = jdbcConnection.createStatement();
			ResultSet resultSet = createStatement.executeQuery(sql);
			while (resultSet.next()) {
				DateNum dateNum=new DateNum();
				String date = resultSet.getString("createTime");
				int shareNum = resultSet.getInt("shareNum");
				dateNum.setDate(date);
				dateNum.setShareNum(shareNum);
				dateNums.add(dateNum);
			}
			createStatement.close();
			jdbcConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateNums;
	}
}
