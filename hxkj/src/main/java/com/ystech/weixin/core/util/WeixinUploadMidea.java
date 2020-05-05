package com.ystech.weixin.core.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;

import com.ystech.core.util.LogUtil;

import net.sf.json.JSONObject;

public class WeixinUploadMidea {
	public static String VIDEO="video";
	public static String IMAGE="image";
	public static String VOICE="voice";
	public static String THUMB="thumb";
	/**
	 * 功能描述：上传图片信息
	 * @param access_token
	 * @param mediaType
	 * @param fileMedia
	 * @return
	 */
	public static JSONObject mediaUploadGraphic(String access_token, String mediaType, File fileMedia) {
	    String result = null;
	    try {
	        // 拼装请求地址
	        String uploadMediaUrl = String.format(WeixinUtil.ADDMATERIAL,access_token,mediaType);
	        URL url = new URL(uploadMediaUrl);
	        if (!fileMedia.exists() || !fileMedia.isFile()) {
	            System.out.println("上传的文件不存在");
	        }
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
	        con.setDoInput(true);
	        con.setDoOutput(true);
	        con.setUseCaches(false); // post方式不能使用缓存
	        // 设置请求头信息
	        con.setRequestProperty("Connection", "Keep-Alive");
	        con.setRequestProperty("Charset", "UTF-8");
	        // 设置边界
	        String BOUNDARY = "----------" + System.currentTimeMillis();
	        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
	         
	        // 请求正文信息
	        // 第一部分：
	        StringBuilder sb = new StringBuilder();
	        sb.append("--"); // 必须多两道线
	        sb.append(BOUNDARY);
	        sb.append("\r\n");
	        String filename = fileMedia.getName();
	        sb.append("Content-Disposition: form-data;name=\"media\";filename=\"" + filename + "\" \r\n");
	        sb.append("Content-Type:application/octet-stream\r\n\r\n");
	        byte[] head = sb.toString().getBytes("utf-8");
	 
	        // 获得输出流
	        OutputStream out = new DataOutputStream(con.getOutputStream());
	        // 输出表头
	        out.write(head);
	        // 文件正文部分
	        // 把文件已流文件的方式 推入到url中
	        DataInputStream in = new DataInputStream(new FileInputStream(fileMedia));
	        int bytes = 0;
	        byte[] bufferOut = new byte[1024];
	        while ((bytes = in.read(bufferOut)) != -1) {
	            out.write(bufferOut, 0, bytes);
	        }
	        in.close();
	 
	        // 结尾部分
	        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
	        out.write(foot);
	        out.flush();
	        out.close();
	         
	        StringBuffer buffer = new StringBuffer();
	        BufferedReader reader = null;
	        // 定义BufferedReader输入流来读取URL的响应
	        reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            buffer.append(line);
	        }
	        if (result == null) {
	            result = buffer.toString();
	        }
	         
	        // 解析返回结果
	        JSONObject jsonObject = JSONObject.fromObject(buffer.toString());
	        return jsonObject;
	    } catch (IOException e) {
	        System.out.println("发送POST请求出现异常！" + e);
	        e.printStackTrace();
	    } finally {
	    	
	    }
		return null;
	}
	/**
	 * 功能描述：上传图片信息
	 * @param access_token
	 * @param mediaType
	 * @param fileMedia
	 * @return
	 */
	public static JSONObject uploadImage(String access_token,File fileMedia) {
		String result = null;
		try {
			// 拼装请求地址
			String uploadingImgUrl = WeixinUtil.UPLOADING.replace("ACCESS_TOKEN", access_token);
			URL url = new URL(uploadingImgUrl);
			if (!fileMedia.exists() || !fileMedia.isFile()) {
				System.out.println("上传的文件不存在");
			}
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); // post方式不能使用缓存
			// 设置请求头信息
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			// 设置边界
			String BOUNDARY = "----------" + System.currentTimeMillis();
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			
			// 请求正文信息
			// 第一部分：
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			String filename = fileMedia.getName();
			sb.append("Content-Disposition: form-data;name=\"media\";filename=\"" + filename + "\" \r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			byte[] head = sb.toString().getBytes("utf-8");
			
			// 获得输出流
			OutputStream out = new DataOutputStream(con.getOutputStream());
			// 输出表头
			out.write(head);
			// 文件正文部分
			// 把文件已流文件的方式 推入到url中
			DataInputStream in = new DataInputStream(new FileInputStream(fileMedia));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();
			
			// 结尾部分
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
			out.write(foot);
			out.flush();
			out.close();
			
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = null;
			// 定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
			
			// 解析返回结果
			JSONObject jsonObject = JSONObject.fromObject(buffer.toString());
			return jsonObject;
		} catch (IOException e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			
		}
		return null;
	}
	/**
	 * 功能描述：上传图片信息
	 * @param access_token
	 * @param mediaType
	 * @param fileMedia
	 * @return
	 */
	public static JSONObject kfAccountUploadImage(String access_token,File fileMedia,String kf_account) {
		String result = null;
		try {
			// 拼装请求地址
			String uploadingImgUrl = WeixinUtil.KFACCOUNTUPLOADINGIMAGE.replace("ACCESS_TOKEN", access_token).replace("KFACCOUNT", kf_account);
			URL url = new URL(uploadingImgUrl);
			if (!fileMedia.exists() || !fileMedia.isFile()) {
				System.out.println("上传的文件不存在");
			}
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); // post方式不能使用缓存
			// 设置请求头信息
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			// 设置边界
			String BOUNDARY = "----------" + System.currentTimeMillis();
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			
			// 请求正文信息
			// 第一部分：
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			String filename = fileMedia.getName();
			sb.append("Content-Disposition: form-data;name=\"media\";filename=\"" + filename + "\" \r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			byte[] head = sb.toString().getBytes("utf-8");
			
			// 获得输出流
			OutputStream out = new DataOutputStream(con.getOutputStream());
			// 输出表头
			out.write(head);
			// 文件正文部分
			// 把文件已流文件的方式 推入到url中
			DataInputStream in = new DataInputStream(new FileInputStream(fileMedia));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();
			
			// 结尾部分
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
			out.write(foot);
			out.flush();
			out.close();
			
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = null;
			// 定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
			
			// 解析返回结果
			JSONObject jsonObject = JSONObject.fromObject(buffer.toString());
			return jsonObject;
		} catch (IOException e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			
		}
		return null;
	}
	/**
	 * 功能说明：上传视频
	 * @param access_token
	 * @param mediaType
	 * @param media
	 * @return
	 */
	 public static String postVidiFile(String access_token,File media) {
 		if(!media.exists())
 			return null;
 		String result = null;
 		 String title=media.getName();
		 String intro=media.getName();
 		try {
 			 	String url = String.format(WeixinUtil.ADDMATERIAL,access_token,VIDEO);
	    		URL url1 = new URL(url); 
	    		HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
	    		conn.setConnectTimeout(5000);
	    		conn.setReadTimeout(30000);  
	            conn.setDoOutput(true);  
	            conn.setDoInput(true);  
	            conn.setUseCaches(false);  // post方式不能使用缓存
	            conn.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
	            // 设置请求头信息
	            conn.setRequestProperty("Connection", "Keep-Alive");
	            conn.setRequestProperty("Cache-Control", "no-cache");
	            // 设置边界
	            String boundary = "-----------------------------"+System.currentTimeMillis();
	            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
	    		       
	            
	    		OutputStream output = conn.getOutputStream();
    		    // 请求正文信息
    		    // 第一部分：
	    		output.write(("--" + boundary + "\r\n").getBytes());
	    		output.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n", media.getName()).getBytes());  
	    		output.write("Content-Type: video/mp4 \r\n\r\n".getBytes());
	    		// 文件正文部分
		        // 把文件已流文件的方式 推入到url中
		        byte[] data = new byte[1024];
		        int len =0;
		        FileInputStream input = new FileInputStream(media);
	    		while((len=input.read(data))>-1){
	    			output.write(data, 0, len);
	    		}
	    		 // 结尾部分
	    		output.write(("--" + boundary + "\r\n").getBytes());
	    		output.write("Content-Disposition: form-data; name=\"description\";\r\n\r\n".getBytes());
	    		output.write(String.format("{\"title\":\"%s\", \"introduction\":\"%s\"}",title,intro).getBytes());
	    		output.write(("\r\n--" + boundary + "--\r\n\r\n").getBytes());
	    		output.flush();
	    		output.close();
	    		input.close();
	    		// 定义BufferedReader输入流来读取URL的响应
	    		InputStream resp = conn.getInputStream();
	    		StringBuffer sb = new StringBuffer();
	    		while((len= resp.read(data))>-1){
		    		sb.append(new String(data,0,len,"utf-8"));
	    		}
	    		resp.close();
	    		result = sb.toString();
 		} catch (ClientProtocolException e) {
 			LogUtil.error("postFile，不支持http协议",e);
 		} catch (IOException e) {
 			LogUtil.error("postFile数据传输失败",e);
 		}
 		return result;
 	}
}
