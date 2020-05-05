package com.ystech.core.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.util.TokenHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.ystech.core.util.ZipUtils;
import com.ystech.sys.model.User;
import com.ystech.weixin.core.util.WeixinCommon;
import com.ystech.weixin.core.util.WeixinSignUtil;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinGzuserinfo;

public class BaseController extends ActionSupport implements SessionAware{
	protected Logger log = Logger.getLogger(BaseController.class);
	private static final long serialVersionUID = 1L;
	private static final String DIRECTLY_OUTPUT_ATTR_NAME = "directlyOutput";
	private String mess;
	
	public String getMess() {
		return mess;
	}

	public void setMess(String mess) {
		this.mess = mess;
	}

	public void setSession(Map<String, Object> sessionMap) {		
	}	
	
	protected Map<String,Object> getSession(){
		return  ActionContext.getContext().getSession();
	}
	
	protected HttpServletRequest getRequest(){
		return ServletActionContext.getRequest (); 
	}
	protected HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}
	protected static HttpServletResponse getRes() {
		return ServletActionContext.getResponse();
	}
	protected static HttpServletRequest getReq() {
		return ServletActionContext.getRequest (); 
	}
	
	protected void render(String text,String contentType){
		HttpServletResponse response = this.getResponse();
		try {
			response.setContentType(contentType);
			response.getWriter().write(text);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	protected void rendOk() {
		setMess("ok");
	}
	
	protected void rendError() {
		setMess("error");
	}
	
	//输出JSON文件
	protected void renderJson(String text) {
		render(text, "text/x-json;charset=UTF-8");
	}
	//输出JSON文件
	protected void renderJsonP(String text) {
		render(text, "text/javascript;charset=UTF-8");
	}
	/*输出xml*/
	protected void renderHtml(String text) {
		render(text, "text/html;charset=UTF-8");
	}
	/*输出xml*/
	protected void renderXML(String text) {
		render(text, "text/xml;charset=UTF-8");
	}
	/**
	 * 直接输出纯字符串.
	 */
	protected void renderText(String text) {
		render(text, "text/plain;charset=UTF-8");
	}

	/*保存数据成功处理机制 */
	protected void renderMsg(String url,String message){
		JSONArray jsonArray=new JSONArray();
		JSONObject jsonObject=new JSONObject();
		try {
			jsonObject.put("mark", "0");
			jsonObject.put("url",  getRequest().getContextPath()+url);
			jsonObject.put("message", message);
		} catch (JSONException e) {
			e.printStackTrace();
			log.error("保存数据成功！返回提示信息转换成JSON数据是发生错误！");
		}
		jsonArray.put(jsonObject);
		renderJson(jsonArray.toString());
	}
	
	/*保存数据成功处理机制 */
	protected void renderMsg(String url,String message,String totalCount,String totalPrice){
		JSONArray jsonArray=new JSONArray();
		JSONObject jsonObject=new JSONObject();
		try {
			jsonObject.put("mark", "0");
			jsonObject.put("url",  getRequest().getContextPath()+url);
			jsonObject.put("message", message);
			jsonObject.put("totalCount", totalCount);
			jsonObject.put("totalPrice", totalPrice);
		} catch (JSONException e) {
			e.printStackTrace();
			log.error("保存数据成功！返回提示信息转换成JSON数据是发生错误！");
		}
		jsonArray.put(jsonObject);
		renderJson(jsonArray.toString());
	}
	
	/**
	 * 当存在引用关系，删除出错时当用户点击确认后，才跳转到前一个操作页面
	 * @param t
	 * @param url
	 */
	protected void renderLsitByOk(String url,String message){
		JSONArray jsonArray=new JSONArray();
		JSONObject jsonObject=new JSONObject();
		try {
			jsonObject.put("mark", "2");
			jsonObject.put("url",  getRequest().getContextPath()+url);
			jsonObject.put("message", message);
		} catch (JSONException e) {
			e.printStackTrace();
			log.error("返回提示信息转换成JSON数据是发生错误！");
		}
		jsonArray.put(jsonObject);
		renderJson(jsonArray.toString());
	}
	
	
	
	//异常处理机制
	protected void renderErrorMsg(Throwable t,String url) {
		try {
			this.getResponse().setLocale(java.util.Locale.CHINESE);
			String msg = t.getMessage();
			//转换为unicode码，解决中文乱码问题
			/*if (msg != null) {
				StringBuffer unicode = new StringBuffer();
				for (int i = 0; i < msg.length(); i++) {
					char c = msg.charAt(i);
					if (c >= 0x0391 && c <= 0xFFE5) {//判断是中文就转unicode
						unicode.append("\\u" + Integer.toHexString(c));
					} else if ("'".equals(c+"") || "\"".equals(c+"")) {
						unicode.append(" ");
					} 
					else unicode.append(c);
				}
				msg = unicode.toString();
			}*/
			/*this.getResponse().addHeader("Error-Json", "{code:2001,msg:'"+msg+"',script:''}");
			this.getResponse().sendError(300);*/
			JSONArray jsonArray=new JSONArray();
			JSONObject jsonObject=new JSONObject();
			try {
				jsonObject.put("mark", "1");
				jsonObject.put("url", getRequest().getContextPath()+url);
				jsonObject.put("message",msg);
			} catch (JSONException e) {
				e.printStackTrace();
				log.error("保存数据失败！返回提示信息转换成JSON数据是发生错误！");
			}
			jsonArray.put(jsonObject);
			renderJson(jsonArray.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*保存数据成功处理机制 */
	protected void renderMsgParams(String url,String message,String params,String params2){
		JSONArray jsonArray=new JSONArray();
		JSONObject jsonObject=new JSONObject();
		try {
			jsonObject.put("mark", "0");
			jsonObject.put("url",  getRequest().getContextPath()+url);
			jsonObject.put("message", message);
			jsonObject.put("params", params);
			jsonObject.put("params2", params2);
		} catch (JSONException e) {
			e.printStackTrace();
			log.error("保存数据成功！返回提示信息转换成JSON数据是发生错误！");
		}
		jsonArray.put(jsonObject);
		renderJson(jsonArray.toString());
	}
	/**
	 * 功能描述：下载文件
	 * @param request
	 * @param response
	 * @param path
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public void downFile(HttpServletRequest request,
			HttpServletResponse response, String path) {
		try {
			//文件下载模块
			File file = new File(path);
			byte[] b = new byte[10000];
			int len = 0;
			InputStream is = new FileInputStream(file);
			// 防止IE缓存
			response.setHeader("pragma", "no-cache");
			response.setHeader("cache-control", "no-cache");
			response.setDateHeader("Expires", 0);

			// 设置编码
			request.setCharacterEncoding("UTF-8");
			// 设置输出的格式
			response.reset();

			response.setContentType("application/octet-stream;charset=UTF-8");// 解决在弹出文件下载框不能打开文件的问题
			// 解决在弹出文件下载框不能打开文件的问题
			response.addHeader("Content-Disposition", "attachment;filename=" + ZipUtils.toUtf8String(file.getName()));// );


			ServletOutputStream outputStream = response.getOutputStream();
			// 循环取出流中的数据
			while ((len = is.read(b)) > 0) {
				outputStream.write(b, 0, len);
			}
			is.close();
			response.flushBuffer();
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	protected void directlyOutput(HttpServletRequest request) {
		request.setAttribute(DIRECTLY_OUTPUT_ATTR_NAME, Boolean.TRUE);
	}
	
	/**
	 * 获取关注微信基础信息
	 * @return
	 */
	public WeixinGzuserinfo getWeixinGzuserinfo(){
		HttpServletRequest request = this.getRequest();
		HttpSession session = request.getSession();
		WeixinGzuserinfo weixinGzuserinfo = (WeixinGzuserinfo)session.getAttribute("weixinGzuserinfo");
		return weixinGzuserinfo;
	}
	
	/**
	 * 获取用户信息 企业微信端
	 * @return
	 */
	public User getSessionUser(){
		HttpServletRequest request = getRequest();
		User user = (User) request.getSession().getAttribute("user");
		return user;
	}
	/**
	 * 获取微信ID
	 * @return
	 */
	public String getWechatIdBy(){
		HttpServletRequest request = getRequest();
		HttpSession session = request.getSession();
		WeixinGzuserinfo weixinGzuserinfo = (WeixinGzuserinfo) session.getAttribute("weixinGzuserinfo");
		if(null!=weixinGzuserinfo){
			System.out.println("====================weixinGzuserinfo"+weixinGzuserinfo.getOpenid());
			String wechatId = weixinGzuserinfo.getOpenid();
			return wechatId;
		}else{
			System.out.println("====================error");
			
			return null;
		}
	}
	/**
	 * 获取微信ID
	 * @return
	 */
	public String getWechatId(){
		HttpServletRequest request = getRequest();
		HttpSession session = request.getSession();
		WeixinGzuserinfo weixinGzuserinfo = (WeixinGzuserinfo) session.getAttribute("weixinGzuserinfo");
		if(null!=weixinGzuserinfo){
			System.out.println("====================weixinGzuserinfo"+weixinGzuserinfo.getOpenid());
			String wechatId = weixinGzuserinfo.getOpenid();
			return wechatId;
		}else{
			System.out.println("====================error");
			return null;
		}
	}
	/**
	 * 功能描述：获取分享前台配置参数
	 * @param url
	 * @param weixinAccount
	 */
	public WeixinCommon getWeixinCommon(String url,WeixinAccesstoken accessToken,WeixinAccount weixinAccount) {
		HttpServletRequest request = getRequest();
		String path = request.getContextPath();
		int serverPort = request.getServerPort();
		String queryString = request.getQueryString();
		String tartgetUrl="";
		if(serverPort==80){
			if(null!=queryString){
				tartgetUrl = request.getScheme()+"://"+request.getServerName()+path+""+url+"?"+queryString.split("#")[0];
			}else{
				tartgetUrl = request.getScheme()+"://"+request.getServerName()+path+""+url;
			}
		}else{
			if(null!=queryString){
				tartgetUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+""+url+"?"+request.getQueryString().split("#")[0];
			}else{
				tartgetUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+""+url;
			}
		}
		System.out.println("=======================tartgetUrl:"+tartgetUrl);
		if(null!=weixinAccount){
			WeixinCommon weixinCommon = WeixinSignUtil.getWeixin(accessToken.getJsapiTicket(), tartgetUrl,weixinAccount.getAccountappid());
			return weixinCommon;
		}
		return null;
	}
	/**
	 * 功能描述：表单重复提交
	 * @return
	 */
	public static boolean validToken() {
        String token = getReq().getParameter("token");
        if (token == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("no token found for token name  -> Invalid token ");
            }
            return false;
        }
        Map session = ActionContext.getContext().getSession();
        String sessionToken = (String) session.get("struts.tokens.token");
        if (!token.equals(sessionToken)) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(LocalizedTextUtil.findText(TokenHelper.class, "struts.internal.invalid.token", ActionContext.getContext().getLocale(), "Form token {0} does not match the session token {1}.", new Object[]{
                        token, sessionToken
                }));
            }
            return false;
        }
        // remove the token so it won't be used again
        session.remove("token");
        return true;
    }
}
