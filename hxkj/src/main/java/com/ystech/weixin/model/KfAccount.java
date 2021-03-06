package com.ystech.weixin.model;

// Generated 2015-9-18 17:58:15 by Hibernate Tools 4.0.0

/**
 * WeixinKfaccount generated by hbm2java
 */
public class KfAccount implements java.io.Serializable {

	private Integer dbid;
	private String headImg;
	private String kfAccount;
	private String nickname;
	private String password;
	private Integer accountid;

	public KfAccount() {
	}

	public KfAccount(String kfAccount, String nickname, String password) {
		this.kfAccount = kfAccount;
		this.nickname = nickname;
		this.password = password;
	}

	public Integer getDbid() {
		return this.dbid;
	}

	public void setDbid(Integer dbid) {
		this.dbid = dbid;
	}

	public String getKfAccount() {
		return this.kfAccount;
	}

	public void setKfAccount(String kfAccount) {
		this.kfAccount = kfAccount;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAccountid() {
		return accountid;
	}

	public void setAccountid(Integer accountid) {
		this.accountid = accountid;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	
}
