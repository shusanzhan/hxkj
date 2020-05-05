package com.ystech.core.util;

import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 仅暴露Dbunit对Excel的支持，是因为几种数据提供方案中，Excel编辑数据最容易，在实践中被使用的最广泛。 支持每次Setup是否清空数据的选择
 *
 * @author 
 */
public class CwProperties {

	public static final String DEFAULT_CONFIG_FILE = "cw.properties";

	public static final String DEFAULT_DATE = "2018-08-01";

	public static final String STARTDATE = "startDate";
	
	private String startDate;

	private static PropertiesConfiguration config = new PropertiesConfiguration();
 
	private static final Log log = LogFactory.getLog(CwProperties.class);

	public CwProperties() {
		doInit(DEFAULT_CONFIG_FILE);
	}
	
	public void doInit(String path) {
		try {
			config.load(path);
		} catch (ConfigurationException e) {
			log.error("could not load the properties file : " + path, e);
		}
		Properties properties = new Properties();
		properties.setProperty(STARTDATE, config.getString(STARTDATE, DEFAULT_DATE));
		doInit(properties);
	}
	
	public void doInit(Properties properties) {
		if (properties == null || properties.isEmpty()) {
			return;
		}
		this.startDate = properties.getProperty(STARTDATE);
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	

}
