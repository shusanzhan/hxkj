package com.ystech.core.util;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/** 
 * @author 作者  舒三战
 * @version 创建时间：2013-1-5 上午9:52:52 
 * 类说明 
 **/
public class ImageProperties {
	public static final String DEFAULT_CONFIG_FILE = "customerImage.properties";
	
	public static final String DEFAULT_PAHTR = "/usr/local/scrm/data";
	
	private final static String PAHT = "path";
	
	public String path;
	
	private static final Log log = LogFactory.getLog(ImageProperties.class);
	private static PropertiesConfiguration config = new PropertiesConfiguration();
	/*public ImageProperties() {
		doInit(DEFAULT_CONFIG_FILE);
	}*/
	/*public void doInit(String path) {
		try {
			config.load(path);
		} catch (ConfigurationException e) {
			log.error("could not load the properties file : " + path, e);
		}
		Properties properties = new Properties();
		properties.setProperty(PAHT, config.getString(PAHT, DEFAULT_PAHTR));
		doInit(properties);
	}
	
	public void doInit(Properties properties) {
		if (properties == null || properties.isEmpty()) {
			return;
		}
		this.path = properties.getProperty(PAHT);
	}
	public String getPath() {
		return this.path;
	}*/
}
