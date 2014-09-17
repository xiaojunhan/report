package com.youge.report.test;

public class ArrayClassNameTest {

	public static void main(String[] args) {
		String[] arr = {};
		System.out.println(arr.getClass().getName());
		System.out.println("[Ljava.lang.String;".equals(arr.getClass().getName()));
	}

}
