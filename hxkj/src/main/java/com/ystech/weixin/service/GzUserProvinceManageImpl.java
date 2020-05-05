package com.ystech.weixin.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.weixin.model.GzUserProvince;

@Component("gzUserProvinceManageImpl")
public class GzUserProvinceManageImpl {
	/**
	 * @param date
	 * @return
	 */
	public List<GzUserProvince> queryGzUserProvince() {
		String sql="SELECT province ,COUNT(*) as countNum FROM weixin_gzuserinfo WHERE province!='' GROUP BY province ORDER BY COUNT(*) DESC LIMIT 10";
		List<GzUserProvince> gzUserProvinces=new ArrayList<GzUserProvince>();
		try {
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			Statement createStatement = jdbcConnection.createStatement();
			ResultSet resultSet = createStatement.executeQuery(sql);
			resultSet.last();
			resultSet.beforeFirst();
			while (resultSet.next()) {
				String province = resultSet.getString("province");
				Integer countNum = resultSet.getInt("countNum");
				GzUserProvince statisticalDesk = new GzUserProvince();
				statisticalDesk.setCount(countNum);
				statisticalDesk.setProvince(province);
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
