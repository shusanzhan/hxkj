package com.ystech.sys.model;

import java.util.List;


public class TagRoleUser {
	public Integer tagid;
	public String[] userlist;
	public Integer[] partylist;
	public TagRoleUser(Role role,List<User> users){
		this.tagid=role.getDbid();
		userlist=new String[users.size()];
		int i=0;
		for (User user : users) {
			userlist[i]="R"+user.getUserId();
			i++;
		}
	}
	public Integer getTagid() {
		return tagid;
	}
	public void setTagid(Integer tagid) {
		this.tagid = tagid;
	}
	public Integer[] getPartylist() {
		return partylist;
	}
	public void setPartylist(Integer[] partylist) {
		this.partylist = partylist;
	}
	public String[] getUserlist() {
		return userlist;
	}
	public void setUserlist(String[] userlist) {
		this.userlist = userlist;
	}
	
}
