package com.ystech.weixin.action;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.util.LogUtil;
import com.ystech.core.web.BaseController;
import com.ystech.weixin.core.util.CookieUtile;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinGzuserinfo;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinGzuserinfoManageImpl;

import net.sf.json.JSONObject;

@Component("wechatAuthBaseAction")
@Scope("prototype")
public class WechatAuthBaseAction extends BaseController{
	private static String AUTH_URL="https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECTURI&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
	private static String ACCESS_URL="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	private static String AUTH="/wechatAuthBase/authRemote";
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl;
	private MemberGzUserUtil memberGzUserUtil;
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
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
	@Resource
	public void setBussiCardManageImpl(BussiCardManageImpl bussiCardManageImpl) {
		this.bussiCardManageImpl = bussiCardManageImpl;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String oAuth() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {
			String weixinUrl = oAuth2Url();
			request.setAttribute("weixinUrl", weixinUrl);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "oAuth";
	}
	/**
	 * 功能描述：远程获取微信粉丝信息
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String authRemote(){
		HttpServletRequest request = this.getRequest();
		HttpServletResponse response = getResponse();
		HttpSession session = request.getSession();
		String code = request.getParameter("code");
		try {	
			String codeBase = (String) request.getSession().getAttribute("codeBase");
			log.error("weixinCode:"+codeBase);
			if(codeBase==null){
				request.setAttribute("error","未获取到微信公众号CODE");
				return "error";
			}
			WeixinAccount weixinAccount = weixinAccountManageImpl.findUniqueBy("code", codeBase);
			if(weixinAccount==null){
				request.setAttribute("error","未获取到微信公众号基础信息");
				return "error";
			}	
			if(code==null){
				request.setAttribute("error","未获取到接口/oauth2/authorize的CODE码");
				return "error";
			}
			//获取用户权限 返回openId
			String	codeUrl=ACCESS_URL.replace("APPID",weixinAccount.getAccountappid()).replace("SECRET", weixinAccount.getAccountappsecret()).replace("CODE", code);
			JSONObject jsonObject = WeixinUtil.httpRequest(codeUrl,"GET", null);
			String resultString = jsonObject.toString();
			if(resultString.contains("invalid")){
				request.setAttribute("error","s/oauth2/access_token接口获取数据错误！"+resultString);
				return "error";
			}
			String access_token = jsonObject.getString("access_token");
			Integer expires_in = jsonObject.getInt("expires_in");
			String refresh_token = jsonObject.getString("refresh_token");
			String openid = jsonObject.getString("openid");
			String scope = jsonObject.getString("scope");
			LogUtil.info("access_token:"+access_token+"  expires_in:"+expires_in+"   refresh_token:"+refresh_token+"  openid:"+openid+"   scope:"+scope);
			//先查询数据库中是否已经存在该粉丝
			List<WeixinGzuserinfo> weixinGzuserinfos = weixinGzuserinfoManageImpl.findBy("openid", openid);
			if(null!=weixinGzuserinfos&&weixinGzuserinfos.size()>0){
				WeixinGzuserinfo weixinGzuserinfo = weixinGzuserinfos.get(0);
				//设置粉丝信息到session
				session.setAttribute("weixinGzuserinfo", weixinGzuserinfo);
				//设置粉丝信息到cookie中
				Cookie addCookie =CookieUtile.addCookie(weixinGzuserinfo);
				response.addCookie(addCookie);
				return "authRemote";
			}else{
				String resultBaseUrl = (String) request.getSession().getAttribute("resultBaseUrl");
				WeixinGzuserinfo parent=null;
				if(null!=resultBaseUrl&&resultBaseUrl.contains("cardCode=")){
					int indexOf = resultBaseUrl.indexOf("cardCode=");
					String baseCode = resultBaseUrl.substring(indexOf, resultBaseUrl.length());
					if(null!=baseCode){
						baseCode=baseCode.substring(baseCode.indexOf("=")+1,baseCode.length());
						List<BussiCard> bussiCards = bussiCardManageImpl.findBy("code", baseCode);
						if(null!=bussiCards&&!bussiCards.isEmpty()){
							BussiCard bussiCard = bussiCards.get(0);
							Integer weixinGzuserinfoId = bussiCard.getWeixinGzuserinfoId();
							if(null!=weixinGzuserinfoId){
								parent = weixinGzuserinfoManageImpl.get(weixinGzuserinfoId);
							}
						}
					}
				}
				//String accessToken = weixinAccountManageImpl.getAccessToken(weixinAccount.getWeixinAccountid());
				//WeixinGzuserinfo weixinGzuserinfo = weixinGzuserinfoManageImpl.saveApiUserInfoOther(openid, accessToken, weixinAccount,parent);
				WeixinGzuserinfo weixinGzuserinfo = weixinGzuserinfoManageImpl.saveApiUserInfo(access_token,openid, weixinAccount,parent);
				memberGzUserUtil.saveMember(weixinGzuserinfo, 3);
				//置粉丝信息到session
				session.setAttribute("weixinGzuserinfo", weixinGzuserinfo);
				//设置粉丝信息到cookie中
				Cookie addCookie =CookieUtile.addCookie(weixinGzuserinfo);
				response.addCookie(addCookie);
				return "authRemote";
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			request.setAttribute("error","获取权限发生未知错误");
		}
		return "error";
	}
	/** 
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
			String code = (String) request.getSession().getAttribute("codeBase");
			if(null!=code){
				WeixinAccount weixinAccount = weixinAccountManageImpl.findUniqueBy("code", code);
				String url="";
				if(serverPort==80){
					url = request.getScheme()+"://"+request.getServerName()+path+AUTH;
				}else{
					 url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+AUTH;
				}
				System.out.println(url);
				url = java.net.URLEncoder.encode(url, "utf-8");  
	            String oauth2Url = AUTH_URL.replace("APPID", weixinAccount.getAccountappid()).replace("REDIRECTURI", url);
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
    /** 
     * 将emoji表情替换成* 
     * @param source 
     * @return 过滤后的字符串 
     */  
    public static String filterEmoji(String source) {  
        if(StringUtils.isNotBlank(source)){  
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");  
        }else{  
            return source;  
        }  
    }  
}
