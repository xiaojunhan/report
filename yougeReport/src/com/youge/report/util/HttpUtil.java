package com.youge.report.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
/**
 * 简易版HTTP访问
 * @author hanxj
 *
 */
public class HttpUtil {
	protected static final  Logger logger = LogManager.getLogger(HttpUtil.class);
	public static boolean checkReport(String token,String branchId){
		String url = PropertiesUtil.get("WEB_SERVER");
		url = url + "/manage1/checkReport.json?token="+token+"&branchId="+branchId;
		try {
			String str = getContent(url,"UTF-8");
//			System.out.println("str="+str);
			if(str!=null && str.indexOf("000000") > -1){
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	public static String getContent(String url,String code) throws Exception {
		HttpURLConnection connect = null;
		try {
			URL myurl = new URL(url);
			connect = (HttpURLConnection) myurl.openConnection();
			connect.setConnectTimeout(30000);//30秒
			connect.setReadTimeout(30000);
			return convertStreamToString(connect.getInputStream(),
					code);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw e;
		} finally {
			if (connect != null) {
				connect.disconnect();
			}
		}
		//return null;
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println(getContent("http://www.uuuge.com/system/flush.json?name=name&key=124324&t=89", "UTF-8"));
	}
	
	/**
	 * 将输入流转化成字符串
	 * 
	 * @param is
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String convertStreamToString(InputStream is, String code) {
		if (isEmpty(code)) {
			code = "gbk";
		}
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(is, code));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				is.close();
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}
		}
		return sb.toString();
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
}
