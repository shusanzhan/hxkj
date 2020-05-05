package com.ystech.pllm.model;
// Generated 2016-11-18 11:04:34 by Hibernate Tools 4.0.0.Final

import java.util.Date;

/**
 * PllmSSpreaddetailrecord generated by hbm2java
 */
public class SpreadDetailRecord implements java.io.Serializable {
	public static Integer NEW=1;
	public static Integer OLD=2;
	private Integer dbid;
	//二维码渠道
	private Integer spreadId;
	//分组Id
	private Integer spreadGroupId;
	//二维码明细
	private Integer spreadDetailId;
	//扫码时间
	private Date scDate;
	//烧苗类型1、新粉丝用户；2、老粉丝用户
	private Integer type;
	//扫码微信ID
	private Integer gzusrId;

	public SpreadDetailRecord() {
	}

	public SpreadDetailRecord(Integer spreadId, Integer spreadGroupId, Integer spreadDetailId, Date scDate,
			Integer type, Integer gzusrId) {
		this.spreadId = spreadId;
		this.spreadGroupId = spreadGroupId;
		this.spreadDetailId = spreadDetailId;
		this.scDate = scDate;
		this.type = type;
		this.gzusrId = gzusrId;
	}

	public Integer getDbid() {
		return this.dbid;
	}

	public void setDbid(Integer dbid) {
		this.dbid = dbid;
	}

	public Integer getSpreadId() {
		return this.spreadId;
	}

	public void setSpreadId(Integer spreadId) {
		this.spreadId = spreadId;
	}

	public Integer getSpreadGroupId() {
		return this.spreadGroupId;
	}

	public void setSpreadGroupId(Integer spreadGroupId) {
		this.spreadGroupId = spreadGroupId;
	}

	public Integer getSpreadDetailId() {
		return this.spreadDetailId;
	}

	public void setSpreadDetailId(Integer spreadDetailId) {
		this.spreadDetailId = spreadDetailId;
	}

	public Date getScDate() {
		return this.scDate;
	}

	public void setScDate(Date scDate) {
		this.scDate = scDate;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getGzusrId() {
		return this.gzusrId;
	}

	public void setGzusrId(Integer gzusrId) {
		this.gzusrId = gzusrId;
	}

}
