package com.ystech.weixin.core.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyStore;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ssl.SSLContexts;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ystech.core.util.LogUtil;
import com.ystech.core.util.PathUtil;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;

import net.sf.json.JSONObject;


/**
 * 公众平台通用接口工具类
* 
 * @author liuyq
 * @date 2013-08-09
 */
public class WeixinUtil {
	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	// 菜单创建（POST） 限100（次/天）
    public static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    //菜单删除（get)
    public static String menu_delete_url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
    //创建个性化菜单
    public static String menu_addconditional = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=ACCESS_TOKEN";
    //删除个性化菜单
    public static String menu_deleteconditional = "https://api.weixin.qq.com/cgi-bin/menu/delconditional?access_token=ACCESS_TOKEN";
    //测试个性化菜单
    public static String menu_tryconditional = "https://api.weixin.qq.com/cgi-bin/menu/trymatch?access_token=ACCESS_TOKEN";
    
    //模板消息  行业设置
    public static String api_set_industry = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=ACCESS_TOKEN";
    //模板消息 获取设置的行业信息
    public static String api_get_industry = "https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token=ACCESS_TOKEN";
    //模板消息 获得模板ID
    public static String api_add_template = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=ACCESS_TOKEN";
    //模板消息 获取模板列表
    public static String template_mesg_get_all_private = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=ACCESS_TOKEN";
    //模板消息 删除模板列表
    public static String template_mesg_del_private = "https://api.weixin.qq.com/cgi-bin/template/del_private_template?access_token=ACCESS_TOKEN";
    //模板消息发送
    public static String template_mesg_send = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
    
    
    //客服接口地址
    public static String ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    //获取用户信息接口
    public static String userinfo_url ="https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE&agentid=AGENTID";
    //获取用户列表 接口 access_token（next_openid）
    public static String usergets ="https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
    
    //通过授权获取用户信息
    public static String API_USER_INFO="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    
    //在关注者与公众号产生消息交互后
    public static String userMsgInfo_url="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    //关注者用户权限验证
    public static String 	oauth2="https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECTURI&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
    public static String 	oauth2Access="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    
    //新增永久性图文 接口 返回数据说明：{"media_id":MEDIA_ID}
    public static String ADDNEWS="https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=ACCESS_TOKEN";
    //修改永久图文素材 接口 返回数据说明：{"media_id":MEDIA_ID}
    public static String NEWSUPDATENEWS="https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=ACCESS_TOKEN";
    //新增永久性图文(在图文消息的具体内容中) 上传图文消息内的图片获取URL
    public static String UPLOADINGIMG="https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
    //新增永久性多媒体数据上传接口
    public static String ADDMATERIAL="http://api.weixin.qq.com/cgi-bin/material/add_material?access_token=%1s&type=%2s";
	//上传文章中图文
	public static String UPLOADING="https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
	//W删除永久素材(接口）
	public static String DELMATERIAL="https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=ACCESS_TOKEN";
	
	
	///////////////////客服管理接口////////////////
	//客服管理 添加客服账号接口
	public static String KFACCOUNTADD="https://api.weixin.qq.com/customservice/kfaccount/add?access_token=ACCESS_TOKEN";
	//客服管理 更新客服账号接口
	public static String KFACCOUNTUPDATE="https://api.weixin.qq.com/customservice/kfaccount/update?access_token=ACCESS_TOKEN";
	//客服管理 删除客服账号接口
	public static String KFACCOUNTDELETE="https://api.weixin.qq.com/customservice/kfaccount/del?access_token=ACCESS_TOKEN&kf_account=KFACCOUNT";
	//客服管理 上传客服头像
	public static String KFACCOUNTUPLOADINGIMAGE="http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=ACCESS_TOKEN&kf_account=KFACCOUNT";
	
	//////////////////////////////////////群发接口//////////////////////////////////////////////////////
	//根据分组进行群发(http请求方式: POST)
	public static String MESSAGESENDALL="https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN";
	//根据OpenID列表群发(http请求方式: POST
	public static String MESSAGESEND="https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=ACCESS_TOKEN";
	//删除发送信息Id
	public static String MESSAGEDELETE="https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=ACCESS_TOKEN";
	//预览接口【订阅号与服务号认证后均可用】
	public static String MESSAGEPREVIEW="https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=ACCESS_TOKEN";
	
	
	// 获取用户增减数据（getusersummary）; 最大时间跨度: 7
	public static String GETUSERSUMMARY="https://api.weixin.qq.com/datacube/getusersummary?access_token=ACCESS_TOKEN";
	public static String GETUSERCUMULATE="https://api.weixin.qq.com/datacube/getusercumulate?access_token=ACCESS_TOKEN";
	
	//创建二维码ticket
	public static String CREATEQRCODE="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
	public static String SHOWQRCODE="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
	//客服接口-发消息
	public static String SEND="https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
    /**
     * 发起https请求并获取结果
     * 
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
                // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                TrustManager[] tm = { new MyX509TrustManager() };
                SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
                sslContext.init(null, tm, new java.security.SecureRandom());
                // 从上述SSLContext对象中得到SSLSocketFactory对象
                SSLSocketFactory ssf = sslContext.getSocketFactory();

                URL url = new URL(requestUrl);
                HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
                httpUrlConn.setSSLSocketFactory(ssf);

                httpUrlConn.setDoOutput(true);
                httpUrlConn.setDoInput(true);
                httpUrlConn.setUseCaches(false);
                // 设置请求方式（GET/POST）
                httpUrlConn.setRequestMethod(requestMethod);

                if ("GET".equalsIgnoreCase(requestMethod))
                        httpUrlConn.connect();

                // 当有数据需要提交时
                if (null != outputStr) {
                        OutputStream outputStream = httpUrlConn.getOutputStream();
                        // 注意编码格式，防止中文乱码
                        outputStream.write(outputStr.getBytes("UTF-8"));
                        outputStream.close();
                }

                // 将返回的输入流转换成字符串
                InputStream inputStream = httpUrlConn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                        buffer.append(str);
                }
                bufferedReader.close();
                inputStreamReader.close();
                // 释放资源
                inputStream.close();
                inputStream = null;
                httpUrlConn.disconnect();
                jsonObject = JSONObject.fromObject(buffer.toString());
                //jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {
        	LogUtil.info("Weixin server connection timed out.");
        } catch (Exception e) {
        	LogUtil.info("https request error:{}"+e.getMessage());
        }
        return jsonObject;
    }
    public static JSONObject httpRequestNOSll(String requestUrl, String requestMethod, String outputStr) {
    	JSONObject jsonObject = null;
    	StringBuffer buffer = new StringBuffer();
    	try {
    		// 创建SSLContext对象，并使用我们指定的信任管理器初始化
    		TrustManager[] tm = { new MyX509TrustManager() };
    		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
    		sslContext.init(null, tm, new java.security.SecureRandom());
    		// 从上述SSLContext对象中得到SSLSocketFactory对象
    		SSLSocketFactory ssf = sslContext.getSocketFactory();
    		
    		URL url = new URL(requestUrl);
    		//HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
    		sun.net.www.protocol.http.HttpURLConnection httpUrlConn = (sun.net.www.protocol.http.HttpURLConnection)url.openConnection();
    		//httpUrlConn.setSSLSocketFactory(ssf);
    		
    		httpUrlConn.setDoOutput(true);
    		httpUrlConn.setDoInput(true);
    		httpUrlConn.setUseCaches(false);
    		// 设置请求方式（GET/POST）
    		httpUrlConn.setRequestMethod(requestMethod);
    		
    		if ("GET".equalsIgnoreCase(requestMethod))
    			httpUrlConn.connect();
    		
    		// 当有数据需要提交时
    		if (null != outputStr) {
    			OutputStream outputStream = httpUrlConn.getOutputStream();
    			// 注意编码格式，防止中文乱码
    			outputStream.write(outputStr.getBytes("UTF-8"));
    			outputStream.close();
    		}
    		
    		// 将返回的输入流转换成字符串
    		InputStream inputStream = httpUrlConn.getInputStream();
    		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gb2312");
    		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    		
    		String str = null;
    		while ((str = bufferedReader.readLine()) != null) {
    			buffer.append(str);
    		}
    		bufferedReader.close();
    		inputStreamReader.close();
    		// 释放资源
    		inputStream.close();
    		inputStream = null;
    		httpUrlConn.disconnect();
    		System.out.println("======"+buffer.toString());
    		jsonObject = JSONObject.fromObject(buffer.toString());
    		//jsonObject = JSONObject.fromObject(buffer.toString());
    	} catch (ConnectException ce) {
    		LogUtil.info("Weixin server connection timed out.");
    	} catch (Exception e) {
    		LogUtil.info("https request error:{}"+e.getMessage());
    	}
    	return jsonObject;
    }
    /**
     * 发起https请求并获取结果，返回结果为map集合
     * 
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static Map<String, String>  httpRequestMap(String requestUrl, String requestMethod, String outputStr) {
    	 Map<String, String> map = new HashMap<String, String>();
    	try {
    		// 创建SSLContext对象，并使用我们指定的信任管理器初始化
    		TrustManager[] tm = { new MyX509TrustManager() };
    		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
    		sslContext.init(null, tm, new java.security.SecureRandom());
    		// 从上述SSLContext对象中得到SSLSocketFactory对象
    		SSLSocketFactory ssf = sslContext.getSocketFactory();
    		
    		URL url = new URL(requestUrl);
    		HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
    		httpUrlConn.setSSLSocketFactory(ssf);
    		
    		httpUrlConn.setDoOutput(true);
    		httpUrlConn.setDoInput(true);
    		httpUrlConn.setUseCaches(false);
    		// 设置请求方式（GET/POST）
    		httpUrlConn.setRequestMethod(requestMethod);
    		
    		if ("GET".equalsIgnoreCase(requestMethod))
    			httpUrlConn.connect();
    		
    		// 当有数据需要提交时
    		if (null != outputStr) {
    			OutputStream outputStream = httpUrlConn.getOutputStream();
    			// 注意编码格式，防止中文乱码
    			outputStream.write(outputStr.getBytes("UTF-8"));
    			outputStream.close();
    		}
    		
    		// 将返回的输入流转换成字符串
    		InputStream inputStream = httpUrlConn.getInputStream();
    		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
    		 // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStreamReader);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();

            // 遍历所有子节点
            for (Element e : elementList)
                    map.put(e.getName(), e.getText());

            // 释放资源
    		inputStreamReader.close();
    		// 释放资源
    		inputStream.close();
    		inputStream = null;
    		httpUrlConn.disconnect();
            return map;
    	} catch (ConnectException ce) {
    		LogUtil.info("Weixin server connection timed out.");
    	} catch (Exception e) {
    		LogUtil.info("https request error:{}"+e.getMessage());
    	}
    	return map;
    }
    /**
     * 发起https请求并获取结果为String，结果为xml
     * 
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static String  httpRequestString(String requestUrl, String requestMethod, String outputStr) {
    	 StringBuffer buffer = new StringBuffer();
    	try {
    		// 创建SSLContext对象，并使用我们指定的信任管理器初始化
    		TrustManager[] tm = { new MyX509TrustManager() };
    		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
    		sslContext.init(null, tm, new java.security.SecureRandom());
    		// 从上述SSLContext对象中得到SSLSocketFactory对象
    		SSLSocketFactory ssf = sslContext.getSocketFactory();
    		
    		URL url = new URL(requestUrl);
    		HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
    		httpUrlConn.setSSLSocketFactory(ssf);
    		
    		httpUrlConn.setDoOutput(true);
    		httpUrlConn.setDoInput(true);
    		httpUrlConn.setUseCaches(false);
    		// 设置请求方式（GET/POST）
    		httpUrlConn.setRequestMethod(requestMethod);
    		
    		if ("GET".equalsIgnoreCase(requestMethod))
    			httpUrlConn.connect();
    		
    		// 当有数据需要提交时
    		if (null != outputStr) {
    			OutputStream outputStream = httpUrlConn.getOutputStream();
    			// 注意编码格式，防止中文乱码
    			outputStream.write(outputStr.getBytes("UTF-8"));
    			outputStream.close();
    		}
    		
    		  // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
    		
    	} catch (ConnectException ce) {
    		LogUtil.info("Weixin server connection timed out.");
    	} catch (Exception e) {
    		LogUtil.info("https request error:{}"+e.getMessage());
    	}
    	return buffer.toString();
    }
    /**
     * 发起https请求并获取结果为String，结果为xml
     * 
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static String  httpRequestKPCSString(String requestUrl, String requestMethod, String outputStr,String shopNo) {
    	StringBuffer buffer = new StringBuffer();
    	try {
    		 	KeyStore keyStore  = KeyStore.getInstance("PKCS12");
    		 	String keyStorePath=PathUtil.getWebRootPath()+ System.getProperty("file.separator")+"WEB-INF"+System.getProperty("file.separator")+"cert"+System.getProperty("file.separator")+"apiclient_cert.p12";
    	        FileInputStream instream = new FileInputStream(new File(keyStorePath));
    	        try {
    	            keyStore.load(instream, shopNo.toCharArray());
    	        } finally {
    	            instream.close();
    	        }
    	        // Trust own CA and all self-signed certs
    	        SSLContext sslcontext = SSLContexts.custom()
    	                .loadKeyMaterial(keyStore, shopNo.toCharArray())
    	                .build();
    		URL url = new URL(requestUrl);
    		HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
    		httpUrlConn.setSSLSocketFactory(sslcontext.getSocketFactory());
    		
    		httpUrlConn.setDoOutput(true);
    		httpUrlConn.setDoInput(true);
    		httpUrlConn.setUseCaches(false);
    		// 设置请求方式（GET/POST）
    		httpUrlConn.setRequestMethod(requestMethod);
    		
    		if ("GET".equalsIgnoreCase(requestMethod))
    			httpUrlConn.connect();
    		
    		// 当有数据需要提交时
    		if (null != outputStr) {
    			OutputStream outputStream = httpUrlConn.getOutputStream();
    			// 注意编码格式，防止中文乱码
    			outputStream.write(outputStr.getBytes("UTF-8"));
    			outputStream.close();
    		}
    		
    		// 将返回的输入流转换成字符串
    		InputStream inputStream = httpUrlConn.getInputStream();
    		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
    		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    		
    		String str = null;
    		while ((str = bufferedReader.readLine()) != null) {
    			buffer.append(str);
    		}
    		bufferedReader.close();
    		inputStreamReader.close();
    		// 释放资源
    		inputStream.close();
    		inputStream = null;
    		httpUrlConn.disconnect();
    		
    	} catch (ConnectException ce) {
    		LogUtil.info("Weixin server connection timed out.");
    	} catch (Exception e) {
    		LogUtil.info("https request error:{}"+e.getMessage());
    	}
    	return buffer.toString();
    }
    
    /**
     * 获取access_token
     * @param appid 凭证
    * @param appsecret 密钥
    * @return
     */
    public static WeixinAccesstoken getAccessToken(WeixinAccesstokenManageImpl weixinAccesstokenManageImpl,WeixinAccount weixinAccount) {
    	// 第三方用户唯一凭证
//        String appid = bundle.getString("appId");
//        // 第三方用户唯一凭证密钥
//        String appsecret = bundle.getString("appSecret");
        
    	WeixinAccesstoken accessTocken = getRealAccessToken(weixinAccesstokenManageImpl,weixinAccount);
    	try{
        	if(null!=accessTocken){
        		java.util.Date end = new java.util.Date();
        		System.out.println("timeLong:"+(end.getTime()-accessTocken.getAddtime().getTime()));
        		System.out.println("timeLong:"+(accessTocken.getExpiresIb()*1000));
            	if(end.getTime()-accessTocken.getAddtime().getTime()>=(accessTocken.getExpiresIb()*250)){
                     String requestUrl = access_token_url.replace("APPID", weixinAccount.getAccountappid()).replace("APPSECRET", weixinAccount.getAccountappsecret());
                     JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
                     // 如果请求成功
                     if (null != jsonObject) {
                         try {
                             //凭证过期更新凭证
                        	 accessTocken.setDbid(accessTocken.getDbid());
                        	 accessTocken.setExpiresIb(jsonObject.getInt("expires_in"));
                        	 accessTocken.setAccessToken(jsonObject.getString("access_token"));
                        	 accessTocken.setAddtime(new Date());
                        	 accessTocken.setAccountId(weixinAccount.getDbid());
                        	 //更新并添加 ticket 
                             WeixinAccesstoken ticketAccessToken = getTicketAccessToken(accessTocken);
                           ///accessToken.set
                            updateAccessToken(ticketAccessToken,weixinAccesstokenManageImpl);
                             return accessTocken;
                         } catch (Exception e) {	
                             // 获取token失败
                             String wrongMessage = "获取token失败 errcode:{} errmsg:{}"+jsonObject.getInt("errcode")+jsonObject.getString("errmsg");
                             LogUtil.info(wrongMessage);
                         }
                     }
            	}
        	}else{
        		 WeixinAccesstoken accessToken = null;
                 String requestUrl = access_token_url.replace("APPID", weixinAccount.getAccountappid()).replace("APPSECRET", weixinAccount.getAccountappsecret());
                 JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
                 // 如果请求成功
                 if (null != jsonObject) {
                     try {
                         accessToken = new WeixinAccesstoken();
                         accessToken.setExpiresIb(jsonObject.getInt("expires_in"));
                         accessToken.setAccessToken(jsonObject.getString("access_token"));
                         accessToken.setAddtime(new Date());
                         accessToken.setAccountId(weixinAccount.getDbid());
                         //更新并添加 ticket 
                         WeixinAccesstoken ticketAccessToken = getTicketAccessToken(accessToken);
                         saveAccessToken(ticketAccessToken,weixinAccesstokenManageImpl);
                     } catch (Exception e) {
                         accessToken = null;
                         // 获取token失败
                         String wrongMessage = "获取token失败 errcode:{} errmsg:{}"+jsonObject.getInt("errcode")+jsonObject.getString("errmsg");
                         LogUtil.info(wrongMessage);
                     }
                 }
                 return accessToken;
        	}
    	}catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    	return accessTocken;
    }
    
    
    /**
     * 获取TICKEt
     * @return
     */
    public static String getTicket(WeixinAccesstokenManageImpl accessTokenYwManageImpl,WeixinAccount weixinAccount){
    	WeixinAccesstoken accessToken = getAccessToken(accessTokenYwManageImpl,weixinAccount);
    	String requestUrl = ticket_url.replace("ACCESS_TOKEN", accessToken.getAccessToken());
    	JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
    	String ticket = jsonObject.getString("ticket");
    	return ticket;
    }
    /**
     *功能描述：获取带有TICKEt的accessTooken对象
     * @return
     */
    public static WeixinAccesstoken getTicketAccessToken(WeixinAccesstoken accessToken){
    	String requestUrl = ticket_url.replace("ACCESS_TOKEN", accessToken.getAccessToken());
    	JSONObject jsonObject = httpRequest(requestUrl, "GET", null);
    	if(null!=jsonObject){
    		accessToken.setJsapiTicket(jsonObject.getString("ticket"));
    		accessToken.setAddtime(new Timestamp(System.currentTimeMillis()));
    	}
    	return accessToken;
    }
    
    /**
     * 从数据库中读取凭证
     * @return
     */
    public static WeixinAccesstoken getRealAccessToken(WeixinAccesstokenManageImpl accessTokenYwManageImpl,WeixinAccount weixinAccount){
        List<WeixinAccesstoken> accessTockenList = accessTokenYwManageImpl.executeSql("select * from Weixin_AccessToken where accountId="+weixinAccount.getDbid(),null);
        if(null!=accessTockenList&&accessTockenList.size()>0){
        	return accessTockenList.get(0);
        }
        return null;
    }
    
    /**
     * 保存凭证
     * @return
     */
    public static void saveAccessToken(WeixinAccesstoken accessTocken,WeixinAccesstokenManageImpl accessTokenYwManageImpl){
    	accessTokenYwManageImpl.save(accessTocken);
    }
    
    /**
     * 更新凭证
     * @return
     */
    public static void updateAccessToken( WeixinAccesstoken accessTocken,WeixinAccesstokenManageImpl accessTokenYwManageImpl){
    	accessTokenYwManageImpl.updateSql(accessTocken);
    }
    
    /** 
     * 编码 
     * @param bstr 
     * @return String 
     */  
    public static String encode(byte[] bstr){  
    	return new sun.misc.BASE64Encoder().encode(bstr);  
    }  
  
    /** 
     * 解码 
     * @param str 
     * @return string 
     */  
    public static byte[] decode(String str){ 
    	
	    byte[] bt = null;  
	    try {  
	        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();  
	        bt = decoder.decodeBuffer( str );  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
        return bt;  
        
    }  
    /** 
     * 获得KeyStore. 
     * @param keyStorePath 
     *            密钥库路径 
     * @param password 
     *            密码 
     * @return 密钥库 
     * @throws Exception 
     */  
    public static KeyStore getKeyStore(String password, String keyStorePath)  
            throws Exception {  
    	 KeyStore keyStore  = KeyStore.getInstance("PKCS12");
         FileInputStream instream = new FileInputStream(new File(keyStorePath));
         try {
             keyStore.load(instream, password.toCharArray());
         } finally {
             instream.close();
         }
        return keyStore;  
    }  
    /** 
     * 获得SSLSocketFactory. 
     * @param password 
     *            密码 
     * @param keyStorePath 
     *            密钥库路径 
     * @param trustStorePath 
     *            信任库路径 
     * @return SSLSocketFactory 
     * @throws Exception 
     */  
    public static SSLContext getSSLContext(String password,  
            String keyStorePath, String trustStorePath) throws Exception {  
        // 获得信任库  
        KeyStore keyStore = getKeyStore(password, trustStorePath);  
     // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, password.toCharArray())
                .build();
      
        // 获得SSLSocketFactory  
        return sslcontext;  
    }  	
   
   
}