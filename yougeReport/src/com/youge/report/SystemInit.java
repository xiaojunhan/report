package com.youge.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.youge.report.util.JdbcUtil;
import com.youge.report.util.PropertiesUtil;

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
			 String url = PropertiesUtil.get("jdbc_url");
			 String userName = PropertiesUtil.get("jdbc_user");
			 String password = PropertiesUtil.get("jdbc_password");
			 DruidDataSource dataSource = new DruidDataSource();
			 dataSource.setUrl(url);
			 dataSource.setUsername(userName);
			 dataSource.setPassword(password);
			 dataSource.setMaxActive(20);
			 dataSource.setInitialSize(1);
			 List<Filter> list = new ArrayList<Filter>();
			 Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
			 slf4jLogFilter.setConnectionLogEnabled(false);
			 slf4jLogFilter.setStatementCreateAfterLogEnabled(false);
			 slf4jLogFilter.setStatementPrepareAfterLogEnabled(false);
			 slf4jLogFilter.setStatementCloseAfterLogEnabled(false);
			 slf4jLogFilter.setStatementExecutableSqlLogEnable(true);//完整sql
			 slf4jLogFilter.setStatementParameterSetLogEnabled(false);
			 slf4jLogFilter.setResultSetLogEnabled(false);
			 list.add(slf4jLogFilter);
			 dataSource.setProxyFilters(list);
			 JdbcUtil.addDataSource(Constants.DATA_SOURCE_ID, dataSource);
			 logger.error("initdb end");
		}catch(Exception e){
			logger.error("initdb error",e);
		}
	}
	

}
