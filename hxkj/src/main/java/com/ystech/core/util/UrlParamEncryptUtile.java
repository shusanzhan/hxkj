package com.ystech.core.util;


public class UrlParamEncryptUtile {
	private static final String KEY = "modourl";
	
	//加密
	public static String encrypt(String value) {
		String decrypt="";
		try {
			decrypt = SecurityHelper.encrypt(KEY, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypt;
	}
	//解密
	public static String decrypt(String value){
		String decrypt="";
		try {
			decrypt = SecurityHelper.decrypt(KEY, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypt;
	}
	public static void main(String[] args) {
		String encrypt = encrypt("12000");
		System.out.println(encrypt);
		String decrypt = decrypt(encrypt);
		System.out.println(decrypt);
	}
}
