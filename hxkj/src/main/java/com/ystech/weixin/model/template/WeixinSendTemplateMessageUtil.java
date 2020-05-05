package com.ystech.weixin.model.template;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.slf4j.LoggerFactory;

import com.ystech.core.util.DateUtil;
import com.ystech.core.util.LogUtil;
import com.ystech.mem.model.Member;
import com.ystech.mem.model.OnlineBooking;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinGzuserinfo;

import net.sf.json.JSONObject;

public class WeixinSendTemplateMessageUtil {

	/**
	 * 功能描述：预约服务成功通知
	 * @param accesstoken
	 * @param orders
	 * @param bussi
	 * @param weixinGzuserinfo
	 */
    public static void send_template_onlineBooking(WeixinAccesstoken accesstoken,OnlineBooking onlineBooking,WeixinGzuserinfo weixinGzuserinfo,Enterprise enterprise) {
    	String access_token = accesstoken.getAccessToken();
    	String url= "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+access_token;
    	DataDelevierMemberOnlineTemplate temp = new DataDelevierMemberOnlineTemplate();
    	DataMemberOnline data = new DataMemberOnline();
    	KeyWord first=new KeyWord();
    	KeyWord productType=new KeyWord();
    	KeyWord name=new KeyWord();
    	KeyWord time=new KeyWord();
    	KeyWord result=new KeyWord();
    	KeyWord remark=new KeyWord();
    	String nameValue="";
    	if(onlineBooking.getBookingType().equals("1")){
    		nameValue="试乘试驾";
    	}
    	if(onlineBooking.getBookingType().equals("2")){
    		nameValue="保养维修";
    	}
    	if(onlineBooking.getBookingType().equals("3")){
    		nameValue="续保年审";
    	}
    	if(onlineBooking.getBookingType().equals("4")){
    		nameValue="旧车置换";
    	}
    	
    	first.setValue("您好，您已预约成功。");
    	first.setColor("#173177");
    	productType.setValue("服务");
    	productType.setColor("#173177");
    	name.setValue(nameValue);
    	name.setColor("#173177");
    	time.setValue(DateUtil.format(onlineBooking.getBookingDate()));
    	time.setColor("#173177");
     	result.setValue("已预约");
    	result.setColor("#173177");
    	remark.setValue("如有疑问，请咨询"+enterprise.getPhone());
    	remark.setColor("#173177");
    	
    	data.setFirst(first);
    	data.setProductType(productType);
    	data.setTime(time);
    	data.setResult(result);
    	data.setName(name);
    	data.setRemark(remark);
    	temp.setTouser(weixinGzuserinfo.getOpenid());
    	temp.setTemplate_id("dzmSns_QOdTEIHa-LVyLJhyVEq_CxsSiOb5ySeYLHqA");
    	temp.setUrl("http://www.cdryqc.com/memberWechat/memberCenter?code=83a3e919bcb0b83fc264d78ed92bb521&state=no");
    	temp.setTopcolor("#173177");
    	temp.setData(data);
    	
    	String jsonString = JSONObject.fromObject(temp).toString().replace("day", "Day");
    	JSONObject jsonObject = WeixinUtil.httpRequest(url, "POST", jsonString);
    	LogUtil.error(jsonString);
    	System.out.println(jsonObject);
    	int result2 = 0;
    	if (null != jsonObject) {  
    		if (0 != jsonObject.getInt("errcode")) {  
    			result2 = jsonObject.getInt("errcode");  
    			//("错误 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));  
    		}  
    	}
    }
    /**
     * 功能描述：会员认证通知
     * @param accesstoken
     * @param orders
     * @param bussi
     * @param weixinGzuserinfo
     */
    public static void send_template_memberAtuh(WeixinAccesstoken accesstoken,Member me,Enterprise enterprise) {
    	String access_token = accesstoken.getAccessToken();
    	String url= "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+access_token;
    	DataDelevierMemberTemplate temp = new DataDelevierMemberTemplate();
    	DataMemberCar data = new DataMemberCar();
    	KeyWord first=new KeyWord();
    	KeyWord type=new KeyWord();
    	KeyWord cardNumber=new KeyWord();
    	KeyWord address=new KeyWord();
    	KeyWord VIPName=new KeyWord();
    	KeyWord VIPPhone=new KeyWord();
    	KeyWord expDate=new KeyWord();
    	KeyWord remark=new KeyWord();
    	
    	first.setValue("您好，您在"+enterprise.getName()+"会员认证成功");
    	first.setColor("#173177");
    	type.setValue("商户");
    	type.setColor("#173177");
    	cardNumber.setValue(""+me.getNo());
    	cardNumber.setColor("#173177");
    	address.setValue(""+enterprise.getAddress());
    	address.setColor("#173177");
    	VIPName.setValue(me.getName());
    	VIPName.setColor("#173177");
    	VIPPhone.setValue(me.getMobilePhone());
    	VIPPhone.setColor("#173177");
    	Date addYear = DateUtil.addYear(new Date(), 10);
    	expDate.setValue(DateUtil.format(addYear));
    	expDate.setColor("#173177");
    	remark.setValue("如有疑问，请咨询"+enterprise.getPhone());
    	remark.setColor("#173177");
    	
    	data.setFirst(first);
    	data.setType(type);;
    	data.setCardNumber(cardNumber);
    	data.setAddress(address);
    	data.setVIPName(VIPName);
    	data.setVIPPhone(VIPPhone);
    	data.setExpDate(expDate);
    	data.setRemark(remark);
    	WeixinGzuserinfo weixinGzuserinfo = me.getWeixinGzuserinfo();
    	temp.setTouser(weixinGzuserinfo.getOpenid());
    	temp.setTemplate_id("dxgaGRwHZmgl3WZLF6Ie4FIXWhu88QisE-uI6Hu8GEE");
    	temp.setUrl("http://www.cdryqc.com/agentWechat/jionIn?code=83a3e919bcb0b83fc264d78ed92bb521&state=no");
    	temp.setTopcolor("#173177");
    	temp.setData(data);
    	
    	String jsonString = JSONObject.fromObject(temp).toString();
    	jsonString=jsonString.replace("day", "Day");
    	JSONObject jsonObject = WeixinUtil.httpRequest(url, "POST", jsonString);
    	LogUtil.error(jsonString);
    	int result = 0;
    	if (null != jsonObject) {  
    		if (0 != jsonObject.getInt("errcode")) {  
    			result = jsonObject.getInt("errcode");  
    			//("错误 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));  
    		}  
    	}
    	//log.info("模板消息发送结果："+result);
    }
    
}
