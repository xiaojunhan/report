package com.youge.report;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.druid.pool.DruidDataSource;
import com.youge.report.util.JdbcUtil;

public class SystemInit implements ServletContextListener{
	protected static final  Logger logger = LogManager.getLogger(SystemInit.class);
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		initdb();
	}
	/**
	 * 初始化数据库
	 */
	private void initdb(){
		logger.error("initdb start");
		try{
			//使用容器的连接池
//			 Context ic = new InitialContext();
//			 DataSource source = (DataSource)ic.lookup("java:comp/env/jdbc/smartbox");
			
			 //可以初始化多个池
			 DruidDataSource dataSource = new DruidDataSource();
			 dataSource.setUrl("jdbc:mysql://192.168.1.10:3306/smartbox");
			 dataSource.setUsername("smartbox");
			 dataSource.setPassword("smartbox");
			 dataSource.setMaxActive(20);
			 dataSource.setInitialSize(1);
			 JdbcUtil.addDataSource(Constants.DATA_SOURCE_ID, dataSource);
			 logger.error("initdb end");
		}catch(Exception e){
			logger.error("initdb error",e);
		}
	}
	

}
