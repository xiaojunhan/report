package com.youge.report.test;

public class MemcacheKeyTest {

	public static void main(String[] args) {
		String s1 = "1";
		String s2 = "2";
		Object[] objArr = {"33",8};
		
		int key1 = getKey(s1,objArr,s2,objArr);
		
		
		Object[] objArr1 = {"33",8};
		
		int key2 = getKey(s1,objArr1,s2,objArr1);
		
		System.out.println(key1+"===="+key2);
		
		getKey1(objArr1);//==2
		
		Object[] objArr2 = {"33"};
		int key3 = getKey(objArr2);//==2
		
		System.out.println("===="+key3);
		
		 getKey1(objArr2);
	}

	
	public static final int NO_PARAM_KEY = 0;
	public static final int NULL_PARAM_KEY = 53;
	public static int getKey(Object... params){
		System.out.println(params.length);
		if (params.length == 1) {
			if(params[0] instanceof Object[]){
				return getKey((Object[])params[0]);
			}else{
				return (params[0] == null ? NULL_PARAM_KEY : params[0].hashCode());
			}
		}
		if (params.length == 0) {
			return NO_PARAM_KEY;
		}
		int hashCode = 17;
		for (Object object : params) {
			int tempCode = 0;
			if(object instanceof Object[]){
				tempCode = getKey((Object[])object);
			}else{
				tempCode = (object == null ? NULL_PARAM_KEY : object.hashCode());
			}
			hashCode = 31 * hashCode + tempCode;
		}
		return Integer.valueOf(hashCode);
	}
	public static void getKey1(Object... params){
		System.out.println(params.getClass().getName());
		System.out.println(params.length);
		if(params.length==1){
			System.out.println(params[0].getClass().getName());
		}
	}
}
