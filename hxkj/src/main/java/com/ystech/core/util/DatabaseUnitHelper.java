package com.ystech.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

/**
 * 仅暴露Dbunit对Excel的支持，是因为几种数据提供方案中，Excel编辑数据最容易，在实践中被使用的最广泛。 支持每次Setup是否清空数据的选择
 *
 * @author Anders.Lin
 */
public class DatabaseUnitHelper {

	public static final String DEFAULT_CONFIG_FILE = "db.properties";

	public static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";

	public static final String DEFAULT_PASS = "123456";

	public static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/yczd?useUnicode=true&characterEncoding=UTF-8";

	public static final String DEFAULT_USER = "root";

	public static final String DRIVER = "connection.driver_class";

	public static final String PASS = "connection.password";

	public static final String URL = "connection.url";

	public static final String USER = "connection.username";



	private static PropertiesConfiguration config = new PropertiesConfiguration();
 
	private static final Log log = LogFactory.getLog(DatabaseUnitHelper.class);


	private String connectionPassword;

	private String connectionUrl;

	private String connectionUser;

	private String driverClazz;


	public DatabaseUnitHelper() {
		doInit(DEFAULT_CONFIG_FILE);
	}
	
	public void doInit(String path) {
		try {
			config.load(path);
		} catch (ConfigurationException e) {
			log.error("could not load the properties file : " + path, e);
		}
		Properties properties = new Properties();
		properties.setProperty(DRIVER, config.getString(DRIVER, DEFAULT_DRIVER));
		properties.setProperty(USER, config.getString(USER, DEFAULT_USER));
		properties.setProperty(PASS, config.getString(PASS, DEFAULT_PASS));
		properties.setProperty(URL, config.getString(URL, DEFAULT_URL));
		doInit(properties);
	}
	
	public void doInit(Properties properties) {
		if (properties == null || properties.isEmpty()) {
			return;
		}
		this.driverClazz = properties.getProperty(DRIVER);
		this.connectionUrl = properties.getProperty(URL);
		this.connectionUser = properties.getProperty(USER);
		this.connectionPassword = properties.getProperty(PASS);
	}
	/**
	 * Close the specified connection. Ovverride this method of you want to keep your connection alive between tests.
	 */
	protected void closeConnection(IDatabaseConnection connection) throws Exception {
		connection.close();
	}


	public Connection getJdbcConnection() throws Exception {
		return this.getConnection().getConnection();
	}
	
	/**
	 * Returns the test database connection.
	 */
	public IDatabaseConnection getConnection() throws Exception {
		Class.forName(this.driverClazz);
		Connection jdbcConnection = DriverManager.getConnection(this.connectionUrl, this.connectionUser,
				this.connectionPassword);
		return new DatabaseConnection(jdbcConnection);
	}
	
	public String getDriver() {
		return this.driverClazz;
	}
	
	public String getConnectionUrl() {
		return this.connectionUrl;
	}
	public String getPassword() {
		return this.connectionPassword;
	}
	public String getUser() {
		return this.connectionUser;
	}

 public static  void main(String[] args){
	 DatabaseUnitHelper databaseUnitHelper=new DatabaseUnitHelper();
	 try {
	//	System.out.println(databaseUnitHelper.getJdbcConnection().createStatement().executeQuery(sql));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
 }
}
