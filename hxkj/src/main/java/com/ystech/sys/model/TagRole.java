package com.ystech.sys.model;

public class TagRole {
	private String tagname;
	private Integer tagid;

	public TagRole(Role role) {
		this.tagid = role.getDbid();
		this.tagname = role.getName();
	}

	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	public Integer getTagid() {
		return tagid;
	}

	public void setTagid(Integer tagid) {
		this.tagid = tagid;
	}

}
