package com.ystech.weixin.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.web.BaseController;
import com.ystech.weixin.model.DateNum;
import com.ystech.weixin.service.WeixinGzuserinfoManageImpl;
import com.ystech.weixin.service.WeixinReceivetextManageImpl;

@Component("weixinAction")
@Scope("prototype")
public class WeixinAction extends BaseController{
	private WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl;
	private WeixinReceivetextManageImpl weixinReceivetextManageImpl;
	@Resource
	public void setWeixinGzuserinfoManageImpl(WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl) {
		this.weixinGzuserinfoManageImpl = weixinGzuserinfoManageImpl;
	}
	@Resource
	public void setWeixinReceivetextManageImpl(WeixinReceivetextManageImpl weixinReceivetextManageImpl) {
		this.weixinReceivetextManageImpl = weixinReceivetextManageImpl;
	}

	/**
	* 功能描述：
	* 参数描述：
	* 逻辑描述：
	* @return
	* @throws Exception
	*/
	public String index() throws Exception {
		HttpServletRequest request = getRequest();
		try {
			String gzUserEventedSql="SELECT COUNT(*) FROM weixin_gzuserinfo WHERE eventStatus=1 AND (TO_DAYS( NOW( ) ) - TO_DAYS(addtime) <= 1)";
			Object gzUserEventedCount = weixinGzuserinfoManageImpl.count(gzUserEventedSql,null);
			request.setAttribute("gzUserEventedCount", gzUserEventedCount);
			//跑路粉丝
			String gzUserEventCancelSql="SELECT COUNT(*) FROM weixin_gzuserinfo WHERE eventStatus=2 AND (TO_DAYS( NOW( ) ) - TO_DAYS(cancelDate) <= 1)";
			Object gzUserEventCancelCount = weixinGzuserinfoManageImpl.count(gzUserEventCancelSql,null);
			request.setAttribute("gzUserEventCancelCount", gzUserEventCancelCount);
			//总粉丝
			String gzUserCountSql="SELECT COUNT(*) FROM weixin_gzuserinfo ";
			Object gzUserCount = weixinGzuserinfoManageImpl.count(gzUserCountSql,null);
			request.setAttribute("gzUserCount", gzUserCount);
			
			//线性图标标签
			String linexAxis = getLinexAxis();
			request.setAttribute("linexAxis", linexAxis);
			
			//跑路粉丝
			List<DateNum> queryCanncel = weixinGzuserinfoManageImpl.queryCanncel();
			List<DateNum> canncels = zeroLineByEnterprise(queryCanncel);
			//新增粉丝
			List<DateNum> queryNews = weixinGzuserinfoManageImpl.queryNews();
			List<DateNum> news = zeroLineByEnterprise(queryNews);
			
			//跑路粉丝
			List<DateNum> jfs=new ArrayList<DateNum>();
			int i=0;
			for (DateNum dateNum : news) {
				DateNum js=new DateNum();
				js.setDate(dateNum.getDate());
				DateNum dateNum2 = canncels.get(i);
				js.setShareNum(dateNum.getShareNum()-dateNum2.getShareNum());
				jfs.add(js);
				i++;
			}
			
			
			StringBuffer cancelBuffer=new StringBuffer();
			cancelBuffer.append("{  name: '跑路粉丝', data:[");
			i=0;
			for (DateNum dateNum : queryCanncel) {
				if(i==(queryCanncel.size()-1)){
					cancelBuffer.append(dateNum.getShareNum());
				}else{
					cancelBuffer.append(dateNum.getShareNum()+",");
				}
				i++;
			}
			cancelBuffer.append("]}");
			
			StringBuffer newsBuffer=new StringBuffer();
			newsBuffer.append("{  name: '新增粉丝', data:[");
			i=0;
			for (DateNum dateNum : news) {
				if(i==(queryCanncel.size()-1)){
					newsBuffer.append(dateNum.getShareNum());
				}else{
					newsBuffer.append(dateNum.getShareNum()+",");
				}
				i++;
			}
			newsBuffer.append("]}");
			
			StringBuffer jzfsBuffer=new StringBuffer();
			jzfsBuffer.append("{  name: '净增粉丝', data:[");
			i=0;
			for (DateNum dateNum : jfs) {
				if(i==(queryCanncel.size()-1)){
					jzfsBuffer.append(dateNum.getShareNum());
				}else{
					jzfsBuffer.append(dateNum.getShareNum()+",");
				}
				i++;
			}
			jzfsBuffer.append("]}");
			request.setAttribute("line", newsBuffer.toString()+","+jzfsBuffer.toString()+","+cancelBuffer.toString());
			
			String exchange = exchange();
			request.setAttribute("exchange", exchange);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "index";
	}
	/**
	 * 功能描述：填充解析日期格式（主要解决统计出数据为0的日期无数据）
	 * @param dateNums
	 * @return
	 */
	private static List<DateNum> zeroLineByEnterprise(List<DateNum> dateNums){
		List<DateNum> reDateNums=new ArrayList<DateNum>();
		String[] weekDay = getWeekDay();
		for (String day : weekDay) {
			if(null==dateNums||dateNums.size()<=0){
				DateNum dateNum=new DateNum();
				dateNum.setDate(day);
				dateNum.setShareNum(0);
				reDateNums.add(dateNum);
			}else{
				boolean status=false;
				for (DateNum dateNum : dateNums) {
					if(dateNum.getDate().equals(day)){
						status=true;
						reDateNums.add(dateNum);
						break;
					}
				}
				if(status==false){
					DateNum dateNum=new DateNum();
					dateNum.setDate(day);
					dateNum.setShareNum(0);
					reDateNums.add(dateNum);
				}
			}
		}
		return reDateNums;
	}
	/**
	 * 功能描述：获取线性图标的标题栏目
	 * @param type
	 * @return
	 */
	private static String getLinexAxis(){
		StringBuffer buffer=new StringBuffer();
		//日
		String[] latelySevenDay = getWeekDay();
		buffer.append("[");
		int i=0;
		for (String string : latelySevenDay) {
			if(i!=(latelySevenDay.length-1)){
				buffer.append("'"+string.replace("-", "月")+"',");
			}else{
				buffer.append("'"+string.replace("-", "月")+"'");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
	/**
	 * 功能描述：获取周报数据
	 * @return
	 */
	private static String[] getWeekDay(){
		String[] dates=new String[7];
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式  
	    int j=0;
	    for (int i=6;i>=0;i--) {
	    	 Calendar cal2 = Calendar.getInstance();
	    	 cal2.add(Calendar.DATE, -i);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值   
	    	 String imptimeEnd = sdf.format(cal2.getTime());  
	    	 dates[j]=imptimeEnd;  
	    	 j++;
		}
		return dates;
	}
	/**
	 * 互动数据列表
	 * @return
	 */
	private String exchange(){
		
		//接受消息数量
		List<DateNum> queryReciveMessage = weixinReceivetextManageImpl.queryReciveMessage();
		List<DateNum> reciveMessages = zeroLineByEnterprise(queryReciveMessage);
		//点击菜单数量
		List<DateNum> queryReciveClickMessages = weixinReceivetextManageImpl.queryReciveClickMessage();
		List<DateNum> reciveClickMessagess = zeroLineByEnterprise(queryReciveClickMessages);
		//互动人数
		List<DateNum> queryReciveGzuser = weixinReceivetextManageImpl.queryReciveGzuser();
		List<DateNum> reciveGzusers = zeroLineByEnterprise(queryReciveGzuser);
		
		
		StringBuffer reciveMessageBuffer=new StringBuffer();
		reciveMessageBuffer.append("{  name: '接受消息数', data:[");
		int i=0;
		for (DateNum dateNum : reciveMessages) {
			if(i==(reciveMessages.size()-1)){
				reciveMessageBuffer.append(dateNum.getShareNum());
			}else{
				reciveMessageBuffer.append(dateNum.getShareNum()+",");
			}
			i++;
		}
		reciveMessageBuffer.append("]}");
		
		StringBuffer reciveClikeBuffer=new StringBuffer();
		reciveClikeBuffer.append("{  name: '点击菜单数', data:[");
		i=0;
		for (DateNum dateNum : reciveClickMessagess) {
			if(i==(reciveClickMessagess.size()-1)){
				reciveClikeBuffer.append(dateNum.getShareNum());
			}else{
				reciveClikeBuffer.append(dateNum.getShareNum()+",");
			}
			i++;
		}
		reciveClikeBuffer.append("]}");
		
		StringBuffer reciveGzusersBuffer=new StringBuffer();
		reciveGzusersBuffer.append("{  name: '互动人数', data:[");
		i=0;
		for (DateNum dateNum : reciveGzusers) {
			if(i==(reciveGzusers.size()-1)){
				reciveGzusersBuffer.append(dateNum.getShareNum());
			}else{
				reciveGzusersBuffer.append(dateNum.getShareNum()+",");
			}
			i++;
		}
		reciveGzusersBuffer.append("]}");
		
		return reciveMessageBuffer.toString()+","+reciveClikeBuffer.toString()+","+reciveGzusersBuffer.toString();
	}
}
