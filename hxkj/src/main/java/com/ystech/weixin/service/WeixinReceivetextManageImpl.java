package com.ystech.weixin.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.weixin.model.DateNum;
import com.ystech.weixin.model.WeixinReceivetext;

@Component("weixinReceivetextManageImpl")
public class WeixinReceivetextManageImpl extends HibernateEntityDao<WeixinReceivetext>{
	/**
	 * 获取消接受消息数量
	 * @param coupon
	 */
	public List<DateNum> queryReciveMessage(){
		String sql="SELECT "
				+ "DATE_FORMAT(createTime,'%Y-%m-%d') AS createTime,"
				+ "COUNT(*) AS shareNum "
				+ "FROM  "
				+ "weixin_receivetext "
				+ "WHERE  "
				+ "TO_DAYS(NOW()) - TO_DAYS(createTime)<7 "
				+ "GROUP BY "
				+ " DATE_FORMAT(createTime,'%y-%m-%d')";
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
	/**
	 * 获取点击菜单数量
	 * @param coupon
	 */
	public List<DateNum> queryReciveClickMessage(){
		String sql="SELECT "
				+ "DATE_FORMAT(createTime,'%Y-%m-%d') AS createTime,"
				+ "COUNT(*) AS shareNum "
				+ "FROM  "
				+ "weixin_receivetext "
				+ "WHERE  "
				+ "TO_DAYS(NOW()) - TO_DAYS(createTime)<7 and msgtype='event' "
				+ "GROUP BY "
				+ " DATE_FORMAT(createTime,'%y-%m-%d') ";
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
	/**
	 * 互动人数
	 * @param coupon
	 */
	public List<DateNum> queryReciveGzuser(){
		String sql="SELECT "
				+ "B.createTime,COUNT(*) AS shareNum "
				+ "FROM "
				+ "(SELECT "
				+ "DATE_FORMAT(createTime,'%Y-%m-%d') AS createTime,"
				+ "COUNT(fromusername) AS shareNum "
				+ "FROM  "
				+ "weixin_receivetext "
				+ "WHERE  "
				+ "TO_DAYS(NOW()) - TO_DAYS(createTime)<7  "
				+ "GROUP BY "
				+ " fromusername)B "
				+ " GROUP BY b.createTime";
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
