package com.youge.report.util;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.MemcachedClientCallable;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MemCacheUtil {
	private static final  Logger logger = LogManager.getLogger(MemCacheUtil.class);
	private static MemcachedClientBuilder builder=null;
	private static MemcachedClient memcachedClient = null;
	
	static{
		String servers = PropertiesUtil.get("MEMCACHE_SERVERS");
		builder = new XMemcachedClientBuilder(AddrUtil.getAddressMap(servers));
		builder.setFailureMode(true);
        builder.setCommandFactory(new BinaryCommandFactory());
        builder.setSessionLocator(new KetamaMemcachedSessionLocator());
        builder.setTranscoder(new SerializingTranscoder());
		try {
			memcachedClient = builder.build();
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
	/**
	 * 
	 * @param key
	 * @param exp 单位为秒
	 * @param value
	 * @throws Exception
	 */
	public static void set(String key,int exp,Object value) throws Exception{
		memcachedClient.set(key, exp, value);
	}
	
	public static <T> T get(String key) throws Exception{
		return memcachedClient.get(key);
	}
	
	public static void nsset(String namespace,final String key,final int exp,final Object value) throws Exception{
		memcachedClient.withNamespace(namespace,  new MemcachedClientCallable<Object>(){
			@Override
			public Void call(MemcachedClient client)
					throws MemcachedException, InterruptedException,
					TimeoutException {
				client.set(key, exp, value);
				return null;
			}
    	});
	}
	
	public static <T> T nsget(String namespace,final String key) throws Exception{
		T t = memcachedClient.withNamespace(namespace,  new MemcachedClientCallable<T>(){
			@Override
			public T call(MemcachedClient client)
					throws MemcachedException, InterruptedException,
					TimeoutException {
				return client.get(key);
			}
    	});
		return t;
	}
	/**
	 * 使命名空间失效
	 * @param ns
	 * @throws Exception
	 */
	public static void invalidateNamespace(String ns) throws Exception{
		memcachedClient.invalidateNamespace(ns);
	}
	
	public static void shutdown(){
		try {
			memcachedClient.shutdown();
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		set("hello1", 0, "Hello,xiaoli");
        String value = get("hello1");
        System.out.println("hello=" + value);
        shutdown();
        //不仅如此，xmemcached还支持主辅模式，你可以设置一个memcached的节点的备份节点，当主节点down掉的情况下，
        //会将本来应该发往主节点的请求转发给standby备份节点。使用备份节点的前提是启用failure模式。备份节点设置如下：
        //MemcachedClient builder=new XmemcachedClientBuilder(AddrUtil.getAddressMap("localhost:11211,localhost:11212 host2:11211,host2:11212"));
        //上面的例子，将localhost:11211的备份节点设置为localhost:11212,而将host2:11211的备份节点设置为host2:11212
        //形如“host:port,host:port"的字符串也可以使用在spring配置中，完全兼容1.3之前的格式。
	}

//	public static final int NO_PARAM_KEY = 0;
//	public static final int NULL_PARAM_KEY = 53;
//	
//	/**
//	 * 此处有坑 不要什么参数都往里传
//	 * 不适合 list map等集合类//一定要用的话 自行修改
//	 * @param params
//	 * @return
//	 */
//	public static int getKey(Object... params){
//		if (params.length == 1) {
//			if(params[0] instanceof Object[]){
//				return getKey((Object[])params[0]);
//			}else{
//				return (params[0] == null ? NULL_PARAM_KEY : params[0].hashCode());
//			}
//		}
//		if (params.length == 0) {
//			return NO_PARAM_KEY;
//		}
//		int hashCode = 17;
//		for (Object object : params) {
//			int tempCode = 0;
//			if(object instanceof Object[]){
//				tempCode = getKey((Object[])object);
//			}else{
//				tempCode = (object == null ? NULL_PARAM_KEY : object.hashCode());
//			}
//			hashCode = 31 * hashCode + tempCode;
//		}
//		return Integer.valueOf(hashCode);
//	}
}
