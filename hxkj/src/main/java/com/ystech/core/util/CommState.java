package com.ystech.core.util;

public class CommState {
	//正常状态
	public static final int STATE_NORMAL=1;
	//停用状态
	public static final int STATE_STOP=2;
	//经销商退网状态
	public static final int ENTER_STATE_BACKNET=10;
	
	public static final int BUSS_TYPE_AUTOCAR=1;//小车业务
	public static final int ENT_TYPE_TRUCK=2;//大车业务
	public static final int ENT_TYPE_COMPBRAND=3;//综合品牌业务
	
	public static final int ROLE_SYS=1;//角色：为系统角色（不可编辑停用操作）
	
	public static final int ROLE_USER=2;//角色：用户角色
	
	public static String getStateName(int key){
		switch (key) {
		case STATE_NORMAL:
			return "启用";
		case STATE_STOP:
			return "停用";
		case ENTER_STATE_BACKNET:
			return "退网";
		default:
			return  "启用";
		}
	}
	//经销商业务类型
	public static String getBussTypeName(int key){
		switch (key) {
		case BUSS_TYPE_AUTOCAR:
			return "小车";
		case ENT_TYPE_TRUCK:
			return "卡车";
		case ENT_TYPE_COMPBRAND:
			return "多品牌";
		default:
			return  "小车";
		}
	}
}
