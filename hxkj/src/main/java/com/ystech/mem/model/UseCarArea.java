package com.ystech.mem.model;

// Generated 2017-10-23 11:32:29 by Hibernate Tools 4.0.0

import java.util.Date;

/**
 * MemUsecararea generated by hbm2java
 */
public class UseCarArea implements java.io.Serializable {

	private Integer dbid;
	private String name;
	private String note;
	private Integer num;
	private UseCarArea parent;
	private Integer levelNum;
	private Date createDate;
	private Date modifyDate;

	public UseCarArea() {
	}

	public UseCarArea(String name, String note, Integer num,
			Integer parentId, Integer levelNum, Date createDate, Date modifyDate) {
		this.name = name;
		this.note = note;
		this.num = num;
		this.levelNum = levelNum;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
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

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getNum() {
		return this.num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}


	public UseCarArea getParent() {
		return parent;
	}

	public void setParent(UseCarArea parent) {
		this.parent = parent;
	}

	public Integer getLevelNum() {
		return this.levelNum;
	}

	public void setLevelNum(Integer levelNum) {
		this.levelNum = levelNum;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return this.modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

}
