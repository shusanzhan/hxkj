package com.ystech.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @author 作者 李林杰
 * @version 创建时间：2013-4-23 类说明 短信接口
 **/

public class PhoneMessage {

	private static final String username = "kyjjr";// 用户名
	private static final String pwd = "kyjjr2016";// 密码

	/**
	 * 发送短信给一个用户
	 * 
	 * @param phoneNum
	 * @param content
	 * 0 代表发送失败，1代表发送成功
	 */
	public int sendMessageByPhone(String phoneNum, String content) {

		URL url = null;
		HttpURLConnection httpurlconnection = null;
		try {
			url = new URL("http://service1.hslx.org:8080/service.asmx/SendSMS");
			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setRequestMethod("POST");
			String param = "LoginName=" + username + "&pwd=" + pwd
					+ "&phoneNum=" + phoneNum + "&Msg=" + content + "&otime=";

			httpurlconnection.getOutputStream().write(param.getBytes("utf-8"));
			httpurlconnection.getOutputStream().flush();
			httpurlconnection.getOutputStream().close();
			int code = httpurlconnection.getResponseCode();
			System.out.println("code   " + code);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					httpurlconnection.getInputStream()));

			httpurlconnection.connect();

			String line = br.readLine();

			while (line != null) {

				System.out.println(line);
				if (line.indexOf("</string>") != -1) {

					// 发送成功
					if ("000".indexOf(line) != -1) {
						return 1;
					}

				}
				line = br.readLine();

			}

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (httpurlconnection != null)
				httpurlconnection.disconnect();
		}
        return 0;
	}

	/**
	 * 发送短信给多个用户
	 * 
	 * @param list
	 * @param content
	 * @return 0代表未发送成功 1代表发送成功
	 */
	public int sendMessageByListPhone(List list, String content) {

		String temp = "";
		for (int i = 0; i < list.size(); i++) {
			temp = temp + "," + list.get(i);
		}
		temp = temp.substring(1, temp.length());

		URL url = null;
		HttpURLConnection httpurlconnection = null;
		try {
			url = new URL("http://service1.hslx.org:8080/service.asmx/SendSMS");
			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setRequestMethod("POST");
			String param = "LoginName=" + username + "&pwd=" + pwd
					+ "&phoneNum=" + temp + "&Msg=" + content + "&otime=";

			httpurlconnection.getOutputStream().write(param.getBytes("utf-8"));
			httpurlconnection.getOutputStream().flush();
			httpurlconnection.getOutputStream().close();
			int code = httpurlconnection.getResponseCode();
			System.out.println("code   " + code);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					httpurlconnection.getInputStream()));

			httpurlconnection.connect();

			String line = br.readLine();

			while (line != null) {

				System.out.println(line);

				if (line.indexOf("</string>") != -1) {

					// 发送成功
					if ("000".indexOf(line) != -1) {
						return 1;
					}

				}
				line = br.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (httpurlconnection != null)
				httpurlconnection.disconnect();
		}

		return 0;

	}

	public static void main(String[] args) throws IOException {
		PhoneMessage phoneMessage = new PhoneMessage();
		phoneMessage.sendMessageByPhone("18708150883", "ddd！");

		/*
		 * List list = new ArrayList(); list.add("18200575592");
		 * phoneMessage.sendMessageByListPhone(list, "cc");
		 */
	}

}
