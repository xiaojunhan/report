package com.youge.report.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class PropertiesUtil{
	private static Resource resource = new ClassPathResource("config.properties");
	private static Properties props = null;
	private static String LOCAL_SERVER = null;
	static {
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String get(String key){
		return props.getProperty(key);
	}
	/**
	 * 
	 * @return 192.168.1.20:9191
	 */
	public static String getLocalServer(){
		if(LOCAL_SERVER==null){
			String localServer = get("LOCAL_SERVER");
			LOCAL_SERVER = localServer;
		}
		return LOCAL_SERVER;
	}
	
	public static void setLocalServer(String localServer){
		LOCAL_SERVER = localServer;
	}
}
