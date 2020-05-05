/**
 * 
 */
package com.ystech.mem.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.mem.model.Member;
import com.ystech.mem.model.MemberShipLevel;
import com.ystech.mem.model.OnlineBooking;
import com.ystech.mem.model.OnlineBookingExamined;
import com.ystech.mem.model.OnlineBookingOldCarChanage;
import com.ystech.mem.model.OnlineBookingSafetyMaintenance;
import com.ystech.mem.service.MemberManageImpl;
import com.ystech.mem.service.MemberShipLevelManagImpl;
import com.ystech.mem.service.OnlineBookingManageImpl;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.User;

/**
 * @author shusanzhan
 * @date 2014-2-25
 */
@Component("onlineBookingAction")
@Scope("prototype")
public class OnlineBookingAction extends BaseController{
	private OnlineBooking onlineBooking;
	private OnlineBookingManageImpl onlineBookingManageImpl;
	private MemberShipLevelManagImpl memberShipLevelManagImpl;
	private MemberManageImpl memberManageImpl;
	public OnlineBooking getOnlineBooking() {
		return onlineBooking;
	}
	public void setOnlineBooking(OnlineBooking onlineBooking) {
		this.onlineBooking = onlineBooking;
	}
	public OnlineBookingManageImpl getOnlineBookingManageImpl() {
		return onlineBookingManageImpl;
	}
	@Resource
	public void setOnlineBookingManageImpl(
			OnlineBookingManageImpl onlineBookingManageImpl) {
		this.onlineBookingManageImpl = onlineBookingManageImpl;
	}
	@Resource
	public void setMemberShipLevelManagImpl(
			MemberShipLevelManagImpl memberShipLevelManagImpl) {
		this.memberShipLevelManagImpl = memberShipLevelManagImpl;
	}
	@Resource
	public void setMemberManageImpl(MemberManageImpl memberManageImpl) {
		this.memberManageImpl = memberManageImpl;
	}
	/**
	 * 功能描述：
	 * 参数描述： 
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		String bookingType = request.getParameter("bookingType");
		Integer status = ParamUtil.getIntParam(request, "status", -1);
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			User currentUser = SecurityUserHolder.getCurrentUser();
			String hql="From OnlineBooking where 1=1 ";
			String countSql="select count(*) From mem_OnlineBooking where 1=1 ";
			if(currentUser.getQueryOtherDataStatus()==(int)User.QUERYYES){
				hql=hql+" and enterpriseId in("+currentUser.getCompnayIds()+")";
				countSql=countSql+" and enterpriseId in("+currentUser.getCompnayIds()+")";
			}else{
				hql=hql+" and enterpriseId="+enterprise.getDbid();
				countSql=countSql+" and enterpriseId="+enterprise.getDbid();
			}
			List param=new ArrayList();
			if(null!=bookingType&&bookingType.trim().length()>0){
				hql=hql+" and bookingType=?";
				countSql=countSql+" and bookingType=?";
				param.add(bookingType);
			}
			if(null!=status&&status>0){
				hql=hql+" and status=?";
				countSql=countSql+" and status=?";
				param.add(status);
			}
			hql=hql+" order by status,bookingDate DESC";
			Page<OnlineBooking> page = onlineBookingManageImpl.pagedQueryHqlSql(pageNo, pageSize,countSql,hql, param.toArray());
			request.setAttribute("page", page);
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "list";
	}

	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String add() throws Exception {
		return "edit";
	}

	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			if(dbid>0){
				OnlineBooking onlineBooking2 = onlineBookingManageImpl.get(dbid);
				request.setAttribute("onlineBooking", onlineBooking2);
				List<Member> members = memberManageImpl.findBy("microId", onlineBooking2.getMicroId());
				if(null!=members&&members.size()>0){
					request.setAttribute("member", members.get(0));
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "edit";
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String view() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			if(dbid>0){
				OnlineBooking onlineBooking2 = onlineBookingManageImpl.get(dbid);
				request.setAttribute("onlineBooking", onlineBooking2);
				List<Member> members = memberManageImpl.findBy("microId", onlineBooking2.getMicroId());
				if(null!=members&&members.size()>0){
					request.setAttribute("member", members.get(0));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "view";
	}

	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void save() throws Exception {
		HttpServletRequest request = getRequest();
		try{
			if(null!=onlineBooking.getDbid()&&onlineBooking.getDbid()>0){
				OnlineBooking onlineBooking2 = onlineBookingManageImpl.get(onlineBooking.getDbid());
				User currentUser = SecurityUserHolder.getCurrentUser();
				onlineBooking2.setCarModel(onlineBooking.getCarModel());
				onlineBooking2.setBookingDate(onlineBooking.getBookingDate());
				onlineBooking2.setCreateTime(onlineBooking.getCreateTime());
				onlineBooking2.setMicroId(onlineBooking.getMicroId());
				onlineBooking2.setMobilePhone(onlineBooking.getMobilePhone());
				onlineBooking2.setModifyTime(new Date());
				onlineBooking2.setName(onlineBooking.getName());
				onlineBooking2.setNote(onlineBooking.getNote());
				onlineBooking2.setOperator(currentUser.getUsername());
				onlineBooking2.setPhone(onlineBooking.getPhone());
				onlineBookingManageImpl.save(onlineBooking2);
			}else{
				onlineBooking.setCreateTime(new Date());
				onlineBooking.setStatus(OnlineBooking.NORMALL);
				onlineBooking.setIsCustomer(false);
				onlineBooking.setIsMember(false);
				onlineBookingManageImpl.save(onlineBooking);
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/onlineBooking/queryList", "保存数据成功！");
		return ;
	}
	/**
	 * 功能描述：处理预约信息 ,页面跳转
	 * 参数描述：预约信息ID
	 * @return
	 * @throws Exception
	 */
	public String dealWithOnlineBooking() throws Exception {
		HttpServletRequest request = this.getRequest();
		try{
			Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
			if(dbid>0){
				OnlineBooking onlineBooking2 = onlineBookingManageImpl.get(dbid);
				if(onlineBooking2.getBookingType().equals(OnlineBooking.ONLINEBOOKINGSAFETYMAINT)){
					request.setAttribute("onlineBooking", (OnlineBookingSafetyMaintenance)onlineBooking2);
				}
				if(onlineBooking2.getBookingType().equals(OnlineBooking.ONLINEBOOKINGEXAMINED)){
					request.setAttribute("onlineBooking", (OnlineBookingExamined)onlineBooking2);
				}
				if(onlineBooking2.getBookingType().equals(OnlineBooking.ONLINEBOOKIINGOLDCARCHAGE)){
					request.setAttribute("onlineBooking", (OnlineBookingOldCarChanage)onlineBooking2);
				}
				else{
					request.setAttribute("onlineBooking", onlineBooking2);
				}
				List<Member> members = memberManageImpl.findBy("microId", onlineBooking2.getMicroId());
				if(null!=members&&members.size()>0){
					request.setAttribute("member", members.get(0));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "dealWithOnlineBooking";
	}
	/**
	 * 功能描述：保存预约处理信息
	 * 参数描述：  
	 * 逻辑描述：根据信息Id，获取onlineBooking 信息，
	 * 根系onlineBooking状态、当前用户、当前时间、处理基本信息系
	 * @return
	 * @throws Exception
	 */
	public void saveDealWithOnlineBooking() throws Exception {
		HttpServletRequest request = this.getRequest();
		try{
			Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
			String dealRecord = request.getParameter("dealRecord");
			if(dbid>0){
				User currentUser = SecurityUserHolder.getCurrentUser();
				OnlineBooking onlineBooking2 = onlineBookingManageImpl.get(dbid);
				onlineBooking2.setDealRecord(dealRecord);
				onlineBooking2.setOperator(currentUser.getRealName());
				onlineBooking2.setModifyTime(new Date());
				onlineBooking2.setStatus(OnlineBooking.CHECKED);
				onlineBookingManageImpl.save(onlineBooking2);
			}
		}catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/onlineBooking/queryList", "保存数据成功！");
		return ;
	}
	/**
	 * 功能描述：把在线申请预约人员添加成会员
	 * 参数描述：预约人员Id
	 * 逻辑描述：通过预约人员Id，获取预约人信息
	 * @return
	 * @throws Exception
	 */
	public String addOnlineBookingToMember() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {
			Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
			OnlineBooking onlineBooking2 = onlineBookingManageImpl.get(dbid);
			request.setAttribute("onlineBooking", onlineBooking2);
			
			List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.find("from MemberShipLevel  ", new Object[]{});
			request.setAttribute("memberShipLevels", memberShipLevels);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "addOnlineBookingToMember";
	}
	
	/**
	 * 功能描述：把在线申请预约人员添加成会员
	 * 参数描述：预约人员Id
	 * 逻辑描述：通过预约人员Id，获取预约人信息
	 * @return
	 * @throws Exception
	 */
	public String addOnlineBookingToCustomer() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {
			Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
			OnlineBooking onlineBooking2 = onlineBookingManageImpl.get(dbid);
			request.setAttribute("onlineBooking", onlineBooking2);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "addOnlineBookingToCustomer";
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		if(null!=dbids&&dbids.length>0){
			try {
				for (Integer dbid : dbids) {
					onlineBookingManageImpl.deleteById(dbid);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
				renderErrorMsg(e, "");
				return ;
			}
		}else{
			renderErrorMsg(new Throwable("未选中数据！"), "");
			return ;
		}
		String query = ParamUtil.getQueryUrl(request);
		renderMsg("/onlineBooking/queryList"+query, "删除数据成功！");
		return;
	}
	
	/**
	 * 功能描述：在线预约提示方法，每次只查询一条记录
	 * 参数描述：无参数
	 * 逻辑描述：根据微信端用户提交信息，查询实时更新的预约
	 * @return
	 * @throws Exception
	 */
	public void onlineBokingMessage() throws Exception {
		JSONObject jsonObject=new JSONObject();  
		try{
			List<OnlineBooking> onlineBookings = onlineBookingManageImpl.find("from OnlineBooking where status=? and isMessage=? order by status,bookingDate DESC", new Object[]{OnlineBooking.NORMALL,false});
			if(null!=onlineBookings&&onlineBookings.size()>0){
				OnlineBooking booking = onlineBookings.get(0);
				String type="";
				if(booking.getBookingType().equals("1")){
					type="试乘试驾";
				}
				if(booking.getBookingType().equals("2")){
					type="保养维修";
				}
				if(booking.getBookingType().equals("3")){
					type="续保年审";
				}
				if(booking.getBookingType().equals("4")){
					type="旧车置换";
				}
				jsonObject.put("dbid", booking.getDbid());
				jsonObject.put("title", booking.getName()+type);
				jsonObject.put("url", "/onlineBooking/dealWithOnlineBooking?dbid="+booking.getDbid());
				
				String content="";
				content=booking.getName()+"预约"+booking.getBookingDate()+"["+type+"]，预约车型";
				if(null!=booking.getCarSeriy()&&booking.getCarSeriy().trim().length()>0){
					content=content+booking.getCarSeriy();
				}
				if(null!=booking.getCarModel()&&booking.getCarModel().trim().length()>0){
					content=content+booking.getCarModel();
				}
				jsonObject.put("content", content);
				jsonObject.put("status", true);
				
				//修改提示信息状态
				
				booking.setIsMessage(true);
				onlineBookingManageImpl.save(booking);
				
				renderJson(jsonObject.toString());
			}
			else{
				jsonObject.put("status", false);
				renderJson(jsonObject.toString());
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			jsonObject.put("status", false);
			renderJson(jsonObject.toString());
			return ;
		}
		return ;
	}
}
