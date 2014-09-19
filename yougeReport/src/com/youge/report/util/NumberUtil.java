package com.youge.report.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:hanxiaojun85@gmail.com">hanxiaojun</a>
 * 
 * @version 1.0
 * 
 * @since 2010-6-25
 */
public class NumberUtil {
	/**
	 * 
	 * @param start
	 * @param end
	 * @return >= start && <=end 的随机数 end需大于start 否则返回-1
	 */
	public static int getRandom(int start, int end) {
		if (start > end || start < 0 || end < 0) {
			return -1;
		}
		return (int) (Math.random() * (end - start + 1)) + start;
	}
	/**
	 * 返回制定长度的随机数
	 * @param len
	 * @return
	 */
	public static long getRandom(int len) {
		if(len<1){
			return -1;
		}
		double start = Math.pow(10,len-1);
		double end = Math.pow(10,len)-1;
		return  (long)((Math.random() * (end - start + 1)) + start);
	}
	/**
	 * 判断数字在数组中是否存在
	 * 
	 * @param array
	 * @param num
	 * @return 存在返回true 不存在返回false
	 */
	public static boolean isNumExsit(int[] array, int num) {
		if (array == null || array.length < 1) {
			return false;
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i] == num) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// List<Integer> list = new ArrayList<Integer>();
		// for (int i = 0; i < 7; i++) {
		// list.add(i);
		// // System.out.println(getRandom(6,40000));
		// }
		// //System.out.println(list);
		// for (int i = 0; i < 100; i++) {
		// //System.out.println(randomList(list, 6));
		// }
		//System.out.println(getPage(13, 6));
		System.out.println(getMax(1,2,3,4,0,6,7676,7,13, 6));
	}

	/**
	 * 从列表中随机取
	 * 
	 * @param <T>
	 * @param list
	 * @param count
	 * @return
	 */
	public static <T> List<T> randomList(List<T> list, int count) {
		if (list == null || list.size() <= count) {
			return list;
		}
		int size = list.size();
		List<T> newList = new ArrayList<T>();
		int[] randoms = new int[size];
		while (newList.size() < count) {
			int random = getRandom(0, size - 1);
			if (randoms[random] != 1) {
				randoms[random] = 1;
				newList.add(list.get(random));
			}
		}
		return newList;
	}

	/**
	 * 根据总数及每页数得到总页数
	 * 
	 * @param count
	 *            总数
	 * @param step
	 *            每页数
	 * @return
	 */
	public static int getPage(int count, int step) {
		return count % step == 0 ? count / step : count / step + 1;
	}
	
	public static int getMax(int... arr){
		int temp = 0;
		for(int i=0;i<arr.length;i++){
			if(arr[i]>temp){
				temp = arr[i];
			}
		}
		return temp;
	}
}
