package com.ystech.weixin.model.template;

public class DataMemberCar {
	private KeyWord first;
	private KeyWord type;
	private KeyWord cardNumber;
	private KeyWord address;
	private KeyWord VIPName;
	private KeyWord VIPPhone;
	private KeyWord expDate;
	private KeyWord remark;
	public KeyWord getFirst() {
		return first;
	}
	public void setFirst(KeyWord first) {
		this.first = first;
	}
	public KeyWord getRemark() {
		return remark;
	}
	public void setRemark(KeyWord remark) {
		this.remark = remark;
	}
	public KeyWord getType() {
		return type;
	}
	public void setType(KeyWord type) {
		this.type = type;
	}
	
	public KeyWord getVIPName() {
		return VIPName;
	}
	public void setVIPName(KeyWord vIPName) {
		VIPName = vIPName;
	}
	public KeyWord getVIPPhone() {
		return VIPPhone;
	}
	public void setVIPPhone(KeyWord vIPPhone) {
		VIPPhone = vIPPhone;
	}
	public KeyWord getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(KeyWord cardNumber) {
		this.cardNumber = cardNumber;
	}
	public KeyWord getAddress() {
		return address;
	}
	public void setAddress(KeyWord address) {
		this.address = address;
	}
	public KeyWord getExpDate() {
		return expDate;
	}
	public void setExpDate(KeyWord expDate) {
		this.expDate = expDate;
	}
	
}
