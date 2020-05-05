package com.ystech.sys.model;

// Generated 2015-10-3 16:53:04 by Hibernate Tools 4.0.0

/**
 * SysSysteminfo generated by hbm2java
 */
public class SystemInfo implements java.io.Serializable {
	public static Integer ROOT=1;
	public static int WECHATTYPE_MODEL_ONCE=1;
	public static int WECHATTYPE_MODEL_MORE=2;
	private Integer dbid;
	private String name;
	private String nameImage;
	private String loginLogo;
	private String infoLogo;
	//创建用户时是否同步用户信息至微信企业号1、默认不同步；2、同步
	private Integer wxUserStatus;
	//系统是否开启利润审批权限，1、默认（不开启）；2、开启
	private Integer grofitAprovalStatus;
	//1、销售顾问验证；2、经销商验证；3、系统验证
	private Integer custValidateStatus;
	//展厅流量统计状态是否开启：1、开启（有前台接待或值班人员）；2、不开启
	private Integer showRoomRecordStatus;
	//dcc网销邀约和谈判组是否分开开启状态：1、为不开启（默认）；2、开启
	private Integer dccInvationAndRecpStatus;
	//微信公众号模式：1、系统唯一模式；2、自助设置模式
	private Integer wechatType;
	public SystemInfo() {
	}

	public SystemInfo(String name, String nameImage, String ss,
			String infoLogo) {
		this.name = name;
		this.nameImage = nameImage;
		this.infoLogo = infoLogo;
	}

	public Integer getDbid() {
		return this.dbid;
	}

	public void setDbid(Integer dbid) {
		this.dbid = dbid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameImage() {
		return this.nameImage;
	}

	public void setNameImage(String nameImage) {
		this.nameImage = nameImage;
	}


	public String getLoginLogo() {
		return loginLogo;
	}

	public void setLoginLogo(String loginLogo) {
		this.loginLogo = loginLogo;
	}

	public String getInfoLogo() {
		return this.infoLogo;
	}

	public void setInfoLogo(String infoLogo) {
		this.infoLogo = infoLogo;
	}

	public Integer getWxUserStatus() {
		return wxUserStatus;
	}

	public void setWxUserStatus(Integer wxUserStatus) {
		this.wxUserStatus = wxUserStatus;
	}

	public Integer getGrofitAprovalStatus() {
		return grofitAprovalStatus;
	}

	public void setGrofitAprovalStatus(Integer grofitAprovalStatus) {
		this.grofitAprovalStatus = grofitAprovalStatus;
	}

	public Integer getCustValidateStatus() {
		return custValidateStatus;
	}

	public void setCustValidateStatus(Integer custValidateStatus) {
		this.custValidateStatus = custValidateStatus;
	}

	public Integer getShowRoomRecordStatus() {
		return showRoomRecordStatus;
	}

	public void setShowRoomRecordStatus(Integer showRoomRecordStatus) {
		this.showRoomRecordStatus = showRoomRecordStatus;
	}

	/**
	 * @return the dccInvationAndRecpStatus
	 */
	public Integer getDccInvationAndRecpStatus() {
		return dccInvationAndRecpStatus;
	}

	/**
	 * @param dccInvationAndRecpStatus the dccInvationAndRecpStatus to set
	 */
	public void setDccInvationAndRecpStatus(Integer dccInvationAndRecpStatus) {
		this.dccInvationAndRecpStatus = dccInvationAndRecpStatus;
	}

	public Integer getWechatType() {
		return wechatType;
	}

	public void setWechatType(Integer wechatType) {
		this.wechatType = wechatType;
	}
	
}
