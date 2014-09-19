package com.youge.report.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.youge.report.exception.DbException;
/**
 * 用于数据库操作
 * 简易版本
 * @author hanxj
 *
 */
public class JdbcUtil{
	private static  Logger logger = LogManager.getLogger(JdbcUtil.class);
	private String dataSourceId;
	private static Map<String,DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();
	/**
	 * 批量更新数据量,每50000条更新一次
	 */
	private static ThreadLocal<Integer> batchCount = new ThreadLocal<Integer>(){
		public Integer initialValue(){
			return 50000;
		}
	};
	public JdbcUtil(String dataSourceId){
		this.dataSourceId = dataSourceId;
	}
	public  Connection getConnection() throws SQLException {
		DataSource basicDataSource = dataSourceMap.get(getDataSourceId());
		if(basicDataSource==null){
			throw new SQLException("get dataSource error,dataSourceId="+getDataSourceId());
		}
		Connection conn = basicDataSource.getConnection();
		conn.setAutoCommit(false);
		return conn;
	}
	public static void close(Statement stmt,ResultSet rs) {
		close(null, stmt, rs);
	}
	public static void close(Connection conn, Statement stmt) {
		close(conn, stmt, null);
	}
	public static void close(Connection conn) {
		close(conn, null, null);
	}
	public static void close(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn(e.getMessage());
		}
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage());
		}
		try {
			if ((conn != null) && (!conn.isClosed()))
				conn.close();
		} catch (SQLException e) {
			logger.warn(e.getMessage());
		}
	}

	/**
	 * 将结果集中数据封装入对象
	 * @param rs
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private <T> T fillObjectField(ResultSet rs,int columCount,T t) throws  Exception{
		Class<?> cla = t.getClass();
		for (int i = 1; i <= columCount; i++) {
			String columnName = rs.getMetaData().getColumnName(i);
			if("ROWNUM1".equals(columnName.toUpperCase())){
				continue;
			}
			Field f = null;
			try{
				f = cla.getDeclaredField(columnName);
			}catch(NoSuchFieldException e){
				String s1 = columnNameToObjName(columnName);
				try{
					f = cla.getDeclaredField(s1);
				}catch(NoSuchFieldException e1){
					continue;
				}
			}
			f.setAccessible(true);
			Object o = rs.getObject(i);
			if(o==null){
				continue;
			}
			if(f.getType().equals(Long.class)){
				if(o.getClass().equals(BigDecimal.class)){
					BigDecimal bd = (BigDecimal)o;
					f.set(t, bd.longValue());
				}else{
					f.set(t, rs.getObject(i));
				}
			}else if(f.getType().equals(String.class)){
				f.set(t, formatString(o));
			}else if(f.getType().getName().equals("int")){
				if(o.getClass().equals(Boolean.class)){
					boolean b = (boolean)o;
					if(b){
						f.set(t, 1);
					}else{
						f.set(t, 0);
					}
				}else if(o.getClass().equals(BigDecimal.class)){
					BigDecimal bd = (BigDecimal)o;
					f.set(t, bd.intValue());
				}else{
					f.set(t, rs.getObject(i));
				}
			}else{
				f.set(t, rs.getObject(i));
			}
		}
		return t;
	}
	/**
	 * 将对象转为字符串
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public String formatString(Object o) throws Exception{
		if(o == null){
			return null;
		}
		if(o.getClass().equals(String.class)){
			return (String)o;
		}else if(o.getClass().equals(BigDecimal.class)){
			BigDecimal bd = (BigDecimal)o;
			return bd.toString();
		}else if(o instanceof java.util.Date){
			return DateUtil.format((java.util.Date)o);
		}else{
			return o.toString();
		}
	}
	
	public static int getBatchCount() {
		return batchCount.get();
	}

	public static void setBatchCount(int batchCount) {
		JdbcUtil.batchCount.set(batchCount);
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public static Map<String, DataSource> getDataSourceMap() {
		return dataSourceMap;
	}

	public static void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
		JdbcUtil.dataSourceMap = dataSourceMap;
	}
	
	public static void addDataSource(String dataSourceId,DataSource basicDataSource) throws Exception{
		if(JdbcUtil.dataSourceMap.containsKey(dataSourceId)){
			throw new Exception("dataSourceId = '"+dataSourceId+"' 已存在");
		}
		JdbcUtil.dataSourceMap.put(dataSourceId, basicDataSource);
	}
	
	/**
	 * 将 SET_CENTER_NAME 转成 setCenterName SET_DATE 转成 setDate DRIVER_NO转成
	 * driverNo
	 * 
	 * @param name
	 * @return
	 */
	public static String columnNameToObjName(String name) {
		if (name.indexOf("_") == -1) {
			return name.toLowerCase();
		}
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '_') {
				list.add(i);
			}
		}
		int j = 0;
		StringBuilder sb = new StringBuilder();
		for (int i : list) {
			String s1 = name.substring(j, i);
			if (j == 0) {
				sb.append(s1.toLowerCase());
			} else {
				if (s1.length() > 0) {
					String s2 = s1.substring(0, 1);
					String s3 = s1.substring(1);
					sb.append(s2.toUpperCase() + s3.toLowerCase());
				}
			}
			j = j + s1.length() + 1;
		}
		String s4 = name.substring(j);
		if (s4.length() > 0) {
			String s5 = s4.substring(0, 1);
			String s6 = s4.substring(1);
			sb.append(s5.toUpperCase() + s6.toLowerCase());
		}
		return sb.toString();
	}
	
	@SuppressWarnings({ "unchecked", "resource" })
	public  <T> List<T> getListp(String sql, Class<T> cla,Object... params)
			throws DbException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> list = new ArrayList<T>();
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			if(params!=null && params.length>0){
				for(int i=0;i<params.length;i++){
					Object obj = params[i];
//					System.out.println("=="+obj.getClass().getName());
					if(obj==null){
						ps.setNull(i+1, Types.VARCHAR);
					}else{
						if(obj.getClass().getName().equals("java.lang.String")){
							ps.setString(i+1,(String)params[i]);
						}else if(obj.getClass().getName().equals("java.lang.Integer")){
							ps.setInt(i+1, (int)params[i]);
						}else{
							logger.error("error,new param type,"+obj.getClass().getName());
							throw new DbException();
						}
					}
				}
			}
			rs = ps.executeQuery();
			int columCount = rs.getMetaData().getColumnCount();
			boolean strFlag = cla.getName().equals("java.lang.String");
			boolean intFlag = cla.getName().equals("java.lang.Integer");
			boolean mapFlag = cla.getName().equals("java.util.Map");
			boolean arrFlag = cla.getName().equals("[Ljava.lang.String;");
			while (rs.next()) {
				if(mapFlag){
					Map<String,Object> map = new HashMap<String,Object>();
					for (int i = 1; i <= columCount; i++) {
						String columnName = rs.getMetaData().getColumnName(i);
						map.put(columnName, rs.getObject(i));
					}
					list.add((T)map);
				}else if(arrFlag){
					String[] arr = new String[columCount];
					for (int i = 1; i <= columCount; i++) {
						arr[i-1] = formatString(rs.getObject(i));
					}
					list.add((T)arr);
				}else{
					Object t = cla.newInstance();
					if(strFlag){
						t = (T)rs.getString(1);
					}else if(intFlag){
						t = rs.getInt(1);
					}else{
						t = fillObjectField(rs,columCount,t);
					}
					list.add((T)t);
				}
			}
		} catch (Exception e) {
			logger.error(sql);
			logger.error(e.getMessage(),e);
			throw new DbException();
		} finally {
			close(conn, ps, rs);
		}
		return list;
	}
	
	@SuppressWarnings("resource")
	public int getIntp(String sql,Object... params)
			throws DbException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int temp = 0;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			if(params!=null && params.length>0){
				for(int i=0;i<params.length;i++){
					Object obj = params[i];
					if(obj==null){
						ps.setNull(i+1, Types.VARCHAR);
					}else{
						if(obj.getClass().getName().equals("java.lang.String")){
							ps.setString(i+1,(String)params[i]);
						}else if(obj.getClass().getName().equals("java.lang.Integer")){
							ps.setInt(i+1, (int)params[i]);
						}else{
							logger.error("error,new param type,"+obj.getClass().getName());
							throw new DbException();
						}
					}
				}
			}
			rs = ps.executeQuery();
			if(rs.next()){
				temp = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error(sql);
			logger.error(e.getMessage(),e);
			throw new DbException();
		} finally {
			close(conn, ps, rs);
		}
		return temp;
	}
	
	public static void main(String[] args){
		
	}
}