package com.ystech.sys.model;

// Generated 2014-1-17 10:12:36 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Area generated by hbm2java
 */
public class Area implements java.io.Serializable {

	private Integer dbid;
	private Area area;
	private Date createDate;
	private Date modifyDate;
	private Integer orders;
	private String fullName;
	private String name;
	private String treePath;
	private Set areas = new HashSet(0);

	public Area() {
	}

	public Area(Date createDate, Date modifyDate, String fullName, String name,
			String treePath) {
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.fullName = fullName;
		this.name = name;
		this.treePath = treePath;
	}

	public Area(Area area, Date createDate, Date modifyDate, Integer orders,
			String fullName, String name, String treePath, Set areas) {
		this.area = area;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.orders = orders;
		this.fullName = fullName;
		this.name = name;
		this.treePath = treePath;
		this.areas = areas;
	}


	public Integer getDbid() {
		return dbid;
	}

	public void setDbid(Integer dbid) {
		this.dbid = dbid;
	}

	public Area getArea() {
		return this.area;
	}

	public void setArea(Area area) {
		this.area = area;
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

	public Integer getOrders() {
		return this.orders;
	}

	public void setOrders(Integer orders) {
		this.orders = orders;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTreePath() {
		return this.treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	public Set getAreas() {
		return this.areas;
	}

	public void setAreas(Set areas) {
		this.areas = areas;
	}

}
