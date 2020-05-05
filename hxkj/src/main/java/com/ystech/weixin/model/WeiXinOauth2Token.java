package com.ystech.weixin.model;

public class WeiXinOauth2Token {
	private Integer dbid;
	private String accessToken;//接口调用凭证基础支持的 access_token 不同
	private int expiresIn;//access_token 接口调用凭证超时时间，单位（秒）
	private String refeshToken;//用户刷新 access_token 
	private String openId;//用户唯一标识
	private String scope;//用户授权的作用域
	public Integer getDbid() {
		return dbid;
	}
	public void setDbid(Integer dbid) {
		this.dbid = dbid;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public int getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
	public String getRefeshToken() {
		return refeshToken;
	}
	public void setRefeshToken(String refeshToken) {
		this.refeshToken = refeshToken;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
}
