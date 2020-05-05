package com.ystech.weixin.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.SystemInfo;
import com.ystech.sys.service.SystemInfoMangeImpl;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WeixinAccount;

import net.sf.json.JSONObject;

@Component("weixinAccountManageImpl")
public class WeixinAccountManageImpl extends HibernateEntityDao<WeixinAccount>{
	private SystemInfoMangeImpl systemInfoMangeImpl;
	@Resource
	public void setSystemInfoMangeImpl(SystemInfoMangeImpl systemInfoMangeImpl) {
		this.systemInfoMangeImpl = systemInfoMangeImpl;
	}
	/**
	 * 功能描述：获取微信账号
	 * @return
	 */
	public WeixinAccount findByWeixinAccount(){
		List<SystemInfo> systemInfos = systemInfoMangeImpl.getAll();
		if(null==systemInfos||systemInfos.isEmpty()){
			return null;
		}
		SystemInfo systemInfo = systemInfos.get(0);
		Integer wechatType = systemInfo.getWechatType();
		WeixinAccount weixinAccount=null;
		if(wechatType==SystemInfo.WECHATTYPE_MODEL_ONCE){
			weixinAccount=get(SystemInfo.ROOT);
		}
		if(wechatType==SystemInfo.WECHATTYPE_MODEL_MORE){
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			weixinAccount= findUniqueBy("enterpriseId", enterprise.getDbid());
		}
		return weixinAccount;
	}
	public String getAccessToken(String accountId) {
		WeixinAccount weixinAccountEntity = findUniqueBy("weixinAccountid", accountId);
		String token = weixinAccountEntity.getAccountaccesstoken();
		if (token != null && !"".equals(token)) {
			// 判断有效时间 是否超过2小时
			java.util.Date end = new java.util.Date();
			java.util.Date start = new java.util.Date(weixinAccountEntity.getAddtoekntime()
					.getTime());
			if ((end.getTime() - start.getTime()) / 1000 / 3600 >= 2) {
				// 失效 重新获取
				String requestUrl = WeixinUtil.access_token_url.replace(
						"APPID", weixinAccountEntity.getAccountappid()).replace(
						"APPSECRET", weixinAccountEntity.getAccountappsecret());
				JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl,
						"GET", null);
				if (null != jsonObject) {
					try {
						token = jsonObject.getString("access_token");
						// 重置token
						weixinAccountEntity.setAccountaccesstoken(token);
						// 重置事件
						weixinAccountEntity.setAddtoekntime(new Date());
						save(weixinAccountEntity);
					} catch (Exception e) {
						token = null;
						// 获取token失败
						String wrongMessage = "获取token失败 errcode:{} errmsg:{}"
								+ jsonObject.getInt("errcode")
								+ jsonObject.getString("errmsg");
						System.out.println(""+wrongMessage);
					}
				}
			} else {
				return weixinAccountEntity.getAccountaccesstoken();
			}
		} else {
			String requestUrl = WeixinUtil.access_token_url.replace("APPID",
					weixinAccountEntity.getAccountappid()).replace("APPSECRET",
							weixinAccountEntity.getAccountappsecret());
			JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "GET",
					null);
			if (null != jsonObject) {
				try {
					token = jsonObject.getString("access_token");
					// 重置token
					weixinAccountEntity.setAccountaccesstoken(token);
					// 重置事件
					weixinAccountEntity.setAddtoekntime(new Date());
					save(weixinAccountEntity);
				} catch (Exception e) {
					token = null;
					// 获取token失败
					String wrongMessage = "获取token失败 errcode:{} errmsg:{}"
							+ jsonObject.getInt("errcode")
							+ jsonObject.getString("errmsg");
					System.out.println(""+wrongMessage);
				}
			}
		}
		return token;
	}
}
