package com.ystech.weixin.action;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.util.LogUtil;
import com.ystech.core.web.BaseController;
import com.ystech.weixin.core.util.CookieUtile;
import com.ystech.weixin.core.util.SignUtil;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinGzuserinfo;
import com.ystech.weixin.service.WechatService;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinGzuserinfoManageImpl;

import net.sf.json.JSONObject;

@Component("wechatAuthAction")
@Scope("prototype")
public class WechatAuthAction extends BaseController{
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WechatService wechatService;
	private WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl;
	private MemberGzUserUtil memberGzUserUtil;
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	@Resource
	public void setWechatService(WechatService wechatService) {
		this.wechatService = wechatService;
	}
	@Resource
	public void setWeixinGzuserinfoManageImpl(
			WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl) {
		this.weixinGzuserinfoManageImpl = weixinGzuserinfoManageImpl;
	}
	@Resource
	public void setMemberGzUserUtil(MemberGzUserUtil memberGzUserUtil) {
		this.memberGzUserUtil = memberGzUserUtil;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void auth() throws Exception {
		HttpServletRequest request = this.getRequest();
		HttpServletResponse response = this.getResponse();
		try {
			// 微信加密签名
			String signature = request.getParameter("signature");
			// 时间戳
			String timestamp = request.getParameter("timestamp");
			// 随机数
			String nonce = request.getParameter("nonce");
			// 随机字符串
			String echostr = request.getParameter("echostr");
			String code = request.getParameter("code");
			System.out.println("signature:"+signature+"timestamp:"+timestamp+"nonce:"+nonce+"code:"+code);
			if(null!=echostr&& echostr.length()>0){
				if(null!=code&&code.trim().length()>0){
					PrintWriter out = response.getWriter();
					WeixinAccount weixinAccount = weixinAccountManageImpl.findUniqueBy("code",code);
					// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
					if (SignUtil.checkSignature(weixinAccount.getAccounttoken(), signature,
							timestamp, nonce)) {
						out.print(echostr);
					}
					out.close();
					out = null;
				}
			}else{
				// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				// 调用核心业务类接收消息、处理消息
				String respMessage = wechatService.coreService(request);
				// 响应消息
				PrintWriter out = response.getWriter();
				out.print(respMessage);
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return ;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String oAuth2() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {
			String weixinUrl = oAuth2Url();
			System.out.println("================weixinUrl:"+weixinUrl);
			request.setAttribute("weixinUrl", weixinUrl);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "oAuth2";
	}
	/**
	 * 功能描述：连接获取用户权限
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String outh2Remote() throws Exception {
		HttpServletRequest request = this.getRequest();
		HttpSession session = request.getSession();
		HttpServletResponse response = this.getResponse();
		try {
			String weixinCode = (String) request.getSession().getAttribute("code");
			WeixinAccount weixinAccount=null;
			if(null!=weixinCode){
				weixinAccount = weixinAccountManageImpl.findUniqueBy("code", weixinCode);
			}
			else{
				request.setAttribute("error","连接地址未配置code参数，请确认！");
				return "error";
			}
			//微信端返回连接CODE参数
			String code = request.getParameter("code");
			if(null==code){
				request.setAttribute("error","微信端返回CODE参数错误！");
				return "error";
			}
			if(null==weixinAccount){
				request.setAttribute("error","连接地址配置code参数错误，系统不存在改code参数，请确认！！");
				return "error";
			}
			String weixinUrl = WeixinUtil.oauth2Access.replace("APPID", weixinAccount.getAccountappid()).replace("SECRET", weixinAccount.getAccountappsecret()).replace("CODE", code);
			System.out.println("===oauth2Access:"+weixinUrl);
			JSONObject jsonObject = WeixinUtil.httpRequest(weixinUrl, "GET", null);
			System.err.println("httpRequest:"+jsonObject.toString());
			try{
				if(null!=jsonObject){
					String resultString = jsonObject.toString();
					if(resultString.contains("errcode")){
						request.setAttribute("error","错误码："+resultString);
						return "error";
					}else{
						System.err.println("httpRequest:"+jsonObject.toString());
						String openId = jsonObject.getString("openid");
						WeixinGzuserinfo weixinGzuserinfo = weixinGzuserinfoManageImpl.findUniqueBy("openid", openId);
						//关注时间
						if(null!=weixinGzuserinfo){
							session.setAttribute("weixinGzuserinfo",weixinGzuserinfo);
							Cookie addCookie =CookieUtile.addCookie(weixinGzuserinfo);
							response.addCookie(addCookie);
						}else{
							//查询不到关注用户时
							String accessToken = weixinAccountManageImpl.getAccessToken(weixinAccount.getWeixinAccountid());
							WeixinGzuserinfo weixinGzuserinfo2 = weixinGzuserinfoManageImpl.saveWeixinGzuserinfo(openId, accessToken,weixinAccount);
							session.setAttribute("weixinGzuserinfo", weixinGzuserinfo2);
							//对关注微信用户创建会员信息
							memberGzUserUtil.saveMember(weixinGzuserinfo,0);
							
							Cookie addCookie =CookieUtile.addCookie(weixinGzuserinfo2);
							response.addCookie(addCookie);
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				// 获取token失败
				String wrongMessage = "获取token失败 errcode:{} errmsg:{}"
						+ jsonObject.getString("errcode")
						+ jsonObject.getString("errmsg");
				jsonObject = null;
				System.out.println(""+wrongMessage);
			}
			
			String resultUrl =(String) request.getSession().getAttribute("resultUrl");
			request.setAttribute("resultUrl", resultUrl);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "outh2Remote";
	}
	/** 
     * 构造带员工身份信息的URL 
     * @param corpid 企业id 
     * @param redirectUri 
     *  授权后重定向的回调链接地址，请使用urlencode对链接进行处理 
     * @param state 
     *   重定向后会带上state参数，企业可以填写a-zA-Z0-9的参数值 
     * @return 
     */  
    private String oAuth2Url() {
    	HttpServletRequest request = this.getRequest();
        try {  
			String path = request.getContextPath();
			int serverPort = request.getServerPort();
			String code = (String) request.getSession().getAttribute("code");
			if(null!=code){
				WeixinAccount weixinAccount = weixinAccountManageImpl.findUniqueBy("code", code);
				String url="";
				if(serverPort==80){
					url = request.getScheme()+"://"+request.getServerName()+path+"/wechatAuth/outh2Remote";
				}else{
					 url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/wechatAuth/outh2Remote";
				}
				System.out.println(url);
				url = java.net.URLEncoder.encode(url, "utf-8");  
	            String oauth2Url = WeixinUtil.oauth2.replace("APPID", weixinAccount.getAccountappid()).replace("REDIRECTURI", url);
	            return oauth2Url; 
			}
			else{
				LogUtil.error("oAuth2Url:连接跳转未设置code参数，请确认！");
				return null;
			}
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        return null;
    }
}
