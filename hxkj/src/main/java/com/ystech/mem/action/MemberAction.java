/**
 * 
 */
package com.ystech.mem.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.DateUtil;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.mem.model.MemPointrecordSet;
import com.ystech.mem.model.MemTag;
import com.ystech.mem.model.Member;
import com.ystech.mem.model.MemberInfo;
import com.ystech.mem.model.MemberShipLevel;
import com.ystech.mem.model.OnlineBooking;
import com.ystech.mem.model.PointRecord;
import com.ystech.mem.service.MemPointrecordSetManageImpl;
import com.ystech.mem.service.MemTagManageImpl;
import com.ystech.mem.service.MemberManageImpl;
import com.ystech.mem.service.MemberShipLevelManagImpl;
import com.ystech.mem.service.OnlineBookingManageImpl;
import com.ystech.mem.service.PointRecordManageImpl;
import com.ystech.mem.service.UseCarAreaManageImpl;
import com.ystech.sys.model.Area;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.User;
import com.ystech.sys.service.AreaManageImpl;
import com.ystech.sys.service.UserManageImpl;

/**
 * @author shusanzhan
 * @date 2014-1-17
 */
@Component("memberAction")
@Scope("prototype")
public class MemberAction extends BaseController{
	private Member member;
	private MemberInfo memberInfo;
	private MemberManageImpl memberManageImpl;
	private MemberShipLevelManagImpl memberShipLevelManagImpl;
	private AreaManageImpl areaManageImpl;
	private PointRecordManageImpl pointRecordManageImpl;
	private MemTagManageImpl memTagManageImpl;
	private MemPointrecordSetManageImpl memPointrecordSetManageImpl;
	private UserManageImpl userManageImpl;
	private UseCarAreaManageImpl useCarAreaManageImpl;
	private OnlineBookingManageImpl onlineBookingManageImpl;
	
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public MemberInfo getMemberInfo() {
		return memberInfo;
	}
	public void setMemberInfo(MemberInfo memberInfo) {
		this.memberInfo = memberInfo;
	}
	
	@Resource
	public void setMemberManageImpl(MemberManageImpl memberManageImpl) {
		this.memberManageImpl = memberManageImpl;
	}
	@Resource
	public void setMemTagManageImpl(MemTagManageImpl memTagManageImpl) {
		this.memTagManageImpl = memTagManageImpl;
	}
	@Resource
	public void setMemberShipLevelManagImpl(
			MemberShipLevelManagImpl memberShipLevelManagImpl) {
		this.memberShipLevelManagImpl = memberShipLevelManagImpl;
	}
	@Resource
	public void setAreaManageImpl(AreaManageImpl areaManageImpl) {
		this.areaManageImpl = areaManageImpl;
	}
	@Resource
	public void setPointRecordManageImpl(PointRecordManageImpl pointRecordManageImpl) {
		this.pointRecordManageImpl = pointRecordManageImpl;
	}
	@Resource
	public void setUserManageImpl(UserManageImpl userManageImpl) {
		this.userManageImpl = userManageImpl;
	}
	@Resource
	public void setMemPointrecordSetManageImpl(
			MemPointrecordSetManageImpl memPointrecordSetManageImpl) {
		this.memPointrecordSetManageImpl = memPointrecordSetManageImpl;
	}
	/**
	 * 功能描述： 会员综合管理
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Enterprise enterprise = SecurityUserHolder.getEnterprise();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.findBy("enterpriseId", enterprise.getDbid());
		request.setAttribute("memberShipLevels", memberShipLevels);
		Integer memberShipLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
		Integer memAuthStatus = ParamUtil.getIntParam(request, "memAuthStatus", -1);
		Integer carMasterStatus = ParamUtil.getIntParam(request, "carMasterStatus", -1);
		Integer agentStatus = ParamUtil.getIntParam(request, "agentStatus", -1);
		String mobilePhone = request.getParameter("mobilePhone");
		String name = request.getParameter("name");
		String nickName = request.getParameter("nickName");
		String carNo = request.getParameter("carNo");
		String car = request.getParameter("car");
		String vinNo = request.getParameter("vinNo");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		Integer hasCar = ParamUtil.getIntParam(request, "hasCar", -1);
		try{
			String sql="select * from mem_Member where 1=1 ";
			User currentUser = SecurityUserHolder.getCurrentUser();
			if(currentUser.getQueryOtherDataStatus()==(int)User.QUERYYES){
				sql=sql+" and enterpriseId in("+currentUser.getCompnayIds()+")";
			}else{
				sql=sql+" and enterpriseId="+enterprise.getDbid();
			}
			List param= new ArrayList();
			sql=sql+" AND `name` IS NOT NULL ";
			if(memberShipLevelId>0){
				sql=sql+" and memberShipLevelId=? ";
				param.add(memberShipLevelId);
			}
			if(agentStatus>0){
				sql=sql+" and agentStatus=? ";
				param.add(agentStatus);
			}
			if(memAuthStatus>0){
				sql=sql+" and memAuthStatus=? ";
				param.add(memAuthStatus);
			}
			if(carMasterStatus>0){
				sql=sql+" and carMasterStatus=? ";
				param.add(carMasterStatus);
			}
			if(null!=name&&name.trim().length()>0){
				sql=sql+" and name like ? ";
				param.add("%"+name +"%");
			}
			if(null!=carNo&&carNo.trim().length()>0){
				sql=sql+" and carNo like ? ";
				param.add("%"+carNo +"%");
			}
			if(null!=car&&car.trim().length()>0){
				sql=sql+" and car like ? ";
				param.add("%"+car +"%");
			}
			if(null!=vinNo&&vinNo.trim().length()>0){
				sql=sql+" and vinNo like ? ";
				param.add("%"+vinNo +"%");
			}
			if(null!=nickName&&nickName.trim().length()>0){
				sql=sql+" and nickName like ? ";
				param.add("%"+nickName +"%");
			}
			if(null!=mobilePhone&&mobilePhone.trim().length()>0){
				sql=sql+" and mobilePhone like ? ";
				param.add("%"+mobilePhone+"%");
			}
			if(null!=hasCar&&hasCar>0){
				sql=sql+" and hasCar = ? ";
				param.add(hasCar);
			}
			if(null!=startTime&&startTime.trim().length()>0){
				sql=sql+" and createTime >= ? ";
				param.add(DateUtil.string2Date(startTime));
			}
			if(null!=endTime&&endTime.trim().length()>0){
				sql=sql+" AND createTime < ? ";
				param.add(DateUtil.nextDay(endTime));
			}
			sql=sql+" order by createTime DESC";
			Page<Member> page = memberManageImpl.pagedQuerySql(pageNo, pageSize, Member.class, sql, param.toArray());
			request.setAttribute("page", page);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error(e);
		}
		return "list";
	}
	/**
	 * 功能描述： 会员储值、消费管理
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String queryStoreList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.find("from MemberShipLevel  ", new Object[]{});
		request.setAttribute("memberShipLevels", memberShipLevels);
		Integer memberShipLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
		String mobilePhone = request.getParameter("mobilePhone");
		String name = request.getParameter("name");
		Integer hasCar = ParamUtil.getIntParam(request, "hasCar", -1);
		try{
			String sql="select * from mem_Member where status=? ";
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			User currentUser = SecurityUserHolder.getCurrentUser();
			if(currentUser.getQueryOtherDataStatus()==(int)User.QUERYYES){
				sql=sql+" and enterpriseId in("+currentUser.getCompnayIds()+")";
			}else{
				sql=sql+" and enterpriseId="+enterprise.getDbid();
			}
			List param= new ArrayList();
			//param.add(userId);
			param.add(Member.ENABLE);
			if(memberShipLevelId>0){
				sql=sql+" and memberShipLevelId=? ";
				param.add(memberShipLevelId);
			}
			if(null!=name&&name.trim().length()>0){
				sql=sql+" and name like ? ";
				param.add("%"+name +"%");
			}
			if(null!=mobilePhone&&mobilePhone.trim().length()>0){
				sql=sql+" and mobilePhone like ? ";
				param.add("%"+mobilePhone+"%");
			}
			if(null!=hasCar&&hasCar>0){
				sql=sql+" and hasCar = ? ";
				param.add(hasCar);
			}
			sql=sql+" order by createTime DESC";
			Page<Member> page= memberManageImpl.pagedQuerySql(pageNo, pageSize, Member.class, sql, param.toArray());
			request.setAttribute("page", page);
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "storeList";
	}
	/**
	 * 功能描述： 会员综合管理---重复数据
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String duplicateData() throws Exception {
		HttpServletRequest request = this.getRequest();
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			User currentUser = SecurityUserHolder.getCurrentUser();
			String sql="SELECT * from mem_Member where weixinGzuserinfoId IN( SELECT weixinGzuserinfoId FROM member GROUP BY microId HAVING COUNT(weixinGzuserinfoId) >1 ";
			if(currentUser.getQueryOtherDataStatus()==(int)User.QUERYYES){
				sql=sql+" and enterpriseId in("+currentUser.getCompnayIds()+")";
			}else{
				sql=sql+" and enterpriseId="+enterprise.getDbid();
			}
			sql=sql+ ")";
			List<Member> members = memberManageImpl.executeSql(sql, null);
			request.setAttribute("members", members);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error(e);
		}
		return "duplicateData";
	}
	/**
	 * 功能描述： 会员综合管理
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String queryMemberBirthday() throws Exception {
		HttpServletRequest request = this.getRequest();
		User currentUser = SecurityUserHolder.getCurrentUser();
		try{
			List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.find("from MemberShipLevel  ", new Object[]{});
			request.setAttribute("memberShipLevels", memberShipLevels);
			Integer memberShipLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
			String mobilePhone = request.getParameter("mobilePhone");
			
			
			List<Member> queryBirthdayMember = memberManageImpl.queryBirthdayMember(currentUser.getDbid(),memberShipLevelId,mobilePhone);
			request.setAttribute("members", queryBirthdayMember);
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "birthday";
	}
	
	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String add() throws Exception {
		HttpServletRequest request = getRequest();
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.findBy("enterpriseId", enterprise.getDbid());
			request.setAttribute("memberShipLevels", memberShipLevels);
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "edit";
	}

	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		Enterprise enterprise = SecurityUserHolder.getEnterprise();
		if(dbid>0){
			Member member2 = memberManageImpl.get(dbid);
			request.setAttribute("member", member2);
			List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.findBy("enterpriseId", enterprise.getDbid());
			request.setAttribute("memberShipLevels", memberShipLevels);
		}
		return "edit";
	}
	/**
	 * @param memberInfo2
	 */
	private String areaSelect(Area area) {
		StringBuffer buffer=new StringBuffer();
		if(null!=area){
			String treePath = area.getTreePath();
			String[] split = treePath.split(",");
			if(split.length>1){
				//父节点
				List<Area> areas = areaManageImpl.find("from Area where area.dbid IS NULL", new Object[]{});
				if(null!=areas&&areas.size()>0){
					buffer.append("<select id='ar' name='ar' style='width: 120px;' onchange='ajaxArea(this)'>");
					buffer.append("<option>请选择...</option>");
					for (Area ar : areas) {
						if(ar.getDbid()==Integer.parseInt(split[1])){
							buffer.append("<option selected='selected' value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
						}else{
							buffer.append("<option value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
						}
					}
					buffer.append("</select> ");
				}
				for (int i=2; i<split.length ; i++) {
					List<Area> areas2 = areaManageImpl.findBy("area.dbid",Integer.parseInt(split[i-1]));
					if(null!=areas2&&areas2.size()>0){
						buffer.append("<select id='ar' name='ar' style='width: 120px;' onchange='ajaxArea(this)'>");
						buffer.append("<option>请选择...</option>");
						for (Area ar : areas2) {
							if(ar.getDbid()==Integer.parseInt(split[i])){
								buffer.append("<option selected='selected' value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
							}else{
								buffer.append("<option value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
							}
						}
						buffer.append("</select> ");
					}
				}
				//最后一个下拉框
				List<Area> areas2 = areaManageImpl.findBy("area.dbid",Integer.parseInt(split[split.length-1]));
				if(null!=areas2&&areas2.size()>0){
					buffer.append("<select id='ar' name='ar' style='width: 120px;' onchange='ajaxArea(this)'>");
					buffer.append("<option>请选择...</option>");
					for (Area ar : areas2) {
						if(ar.getDbid()==area.getDbid()){
							buffer.append("<option selected='selected' value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
						}else{
							buffer.append("<option value='"+ar.getDbid()+"'>"+ar.getName()+"</option>");
						}
					}
					buffer.append("</select> ");
				}
			}
		}else{
			return null;
		}
		return buffer.toString();
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String information() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try{
			Member member2 = memberManageImpl.get(dbid);
			request.setAttribute("member", member2);
			
			
			//会员积分记录
			List<PointRecord> pointRecords= pointRecordManageImpl.findBy("member.dbid", dbid);
			request.setAttribute("pointRecords", pointRecords);
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "information";
	}
	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void save() throws Exception {
		HttpServletRequest request = getRequest();
		try{
			Integer dbid = member.getDbid();
			Integer memberShipLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
			/**保持customer 信息**/
			String no = member.getNo();
			if(null==no||no.trim().length()<=0){
				no=DateUtil.generatedName(new Date());
				member.setNo(no);
			}
			if(memberShipLevelId>0){
				MemberShipLevel memberShipLevel = memberShipLevelManagImpl.get(memberShipLevelId);
				member.setMemberShipLevel(memberShipLevel);
			}
			if(dbid==null||dbid<=0){
				member.setCreateTime(new Date());
				member.setModifyTime(new Date());
				//会员状态
				if(null==member.getTotalPoint()||member.getTotalPoint()<=0){
					member.setTotalPoint(0);
				}
				member.setOveragePiont(0);
				member.setConsumpiontPoint(0);
				//是否同步到微信平台
				memberManageImpl.save(member);
				
				//保存积分记录
				if(null!=member.getTotalPoint()&&member.getTotalPoint()>0){
					PointRecord pointRecord=new PointRecord();
					pointRecord.setCreateTime(new Date());
					pointRecord.setMember(member);
					pointRecord.setEnterpriseId(member.getEnterpriseId());
					pointRecord.setNote("第一次创建会员赠送积分");
					pointRecord.setPointFrom("开通会员赠送积分");
					pointRecord.setType(PointRecord.TYPECOMMON);
					pointRecord.setNum(member.getTotalPoint());
					pointRecordManageImpl.save(pointRecord);
				}
			}else{
				Member member2 = memberManageImpl.get(member.getDbid());
				member2.setNo(member.getNo());
				member2.setName(member.getName());
				member2.setMobilePhone(member.getMobilePhone());
				member2.setPhone(member.getPhone());
				member2.setMemberShipLevel(member.getMemberShipLevel());
				member2.setCarNo(member.getCarNo());
				member2.setVinNo(member.getVinNo());
				member2.setNote(member.getNote());
				member2.setModifyTime(new Date());
				member2.setMobilePhone(member.getMobilePhone());
				member2.setMemberShipLevel(member.getMemberShipLevel());
				member2.setBirthday(member.getBirthday());
				member2.setBirthdayType(member.getBirthdayType());
				member2.setSex(member.getSex());
				memberManageImpl.save(member2);
				
			}
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/member/queryList?parentMenu=3", "保存数据成功！");
		return ;
	}
	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void saveOnlineBookingMember() throws Exception {
		HttpServletRequest request = getRequest();
		
		try{
			Integer dbid = member.getDbid();
			User currentUser = SecurityUserHolder.getCurrentUser();
			Integer memberShipLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
			Integer carSeriyId = ParamUtil.getIntParam(request, "carSeriyId", -1);
			Integer carModelId = ParamUtil.getIntParam(request, "carModelId", -1);
			/**保持customer 信息**/
			String no = member.getNo();
			if(null==no||no.trim().length()<=0){
				no=DateUtil.generatedName(new Date());
				member.setNo(no);
			}
			if(memberShipLevelId>0){
				MemberShipLevel memberShipLevel = memberShipLevelManagImpl.get(memberShipLevelId);
				member.setMemberShipLevel(memberShipLevel);
			}
			if(dbid==null||dbid<=0){
				member.setCreateTime(new Date());
				member.setModifyTime(new Date());
				if(null==member.getTotalPoint()||member.getTotalPoint()<=0){
					member.setTotalPoint(0);
				}
				member.setOveragePiont(0);
				member.setConsumpiontPoint(0);
				//是否同步到微信平台
				memberManageImpl.save(member);
				
				//保存积分记录
				if(null!=member.getTotalPoint()&&member.getTotalPoint()>0){
					PointRecord pointRecord=new PointRecord();
					pointRecord.setCreateTime(new Date());
					pointRecord.setMember(member);
					pointRecord.setEnterpriseId(member.getEnterpriseId());
					pointRecord.setNote("第一次创建会员赠送积分");
					pointRecord.setPointFrom("开通会员赠送积分");
					pointRecord.setType(PointRecord.TYPECOMMON);
					pointRecord.setNum(member.getTotalPoint());
					pointRecordManageImpl.save(pointRecord);
				}
			}else{
				Member member2 = memberManageImpl.get(member.getDbid());
				member2.setNo(member.getNo());
				member2.setName(member.getName());
				member2.setMobilePhone(member.getMobilePhone());
				member2.setPhone(member.getPhone());
				member2.setMemberShipLevel(member.getMemberShipLevel());
				member2.setCarNo(member.getCarNo());
				member2.setVinNo(member.getVinNo());
				member2.setNote(member.getNote());
				member2.setModifyTime(new Date());
				member2.setMobilePhone(member.getMobilePhone());
				member2.setMemberShipLevel(member.getMemberShipLevel());
				member2.setBirthday(member.getBirthday());
				member2.setBirthdayType(member.getBirthdayType());
				member2.setSex(member.getSex());
				memberManageImpl.save(member2);
			}
			
			
			//更新online booking 状态 
			Integer onlineBookingDbid = ParamUtil.getIntParam(request, "onlineBookingDbid", -1);
			OnlineBooking onlineBooking = onlineBookingManageImpl.get(onlineBookingDbid);
			Integer status = onlineBooking.getStatus();
			if(status==OnlineBooking.NORMALL){
				//如果在线预约没有被处理过，那么更新onlineBooking数据
				onlineBooking.setStatus(OnlineBooking.CHECKED);
				onlineBooking.setDealRecord("预约用户于"+ DateUtil.format(new Date())+"创建了会员！");
				onlineBooking.setOperator(currentUser.getRealName());
				onlineBooking.setModifyTime(new Date());
			}
			onlineBooking.setIsMember(true);
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/onlineBooking/queryList", "创建会员成功！");
		return ;
	}

	/**
	 * 功能描述： 删除会员
	 * 参数描述： 
	 * 逻辑描述：
	 * 1、删除会员的积分信息
	 * 2、删除会员的优惠券
	 * 3、删除会员
	 * 
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer type = ParamUtil.getIntParam(request, "type", -1);
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		if(null!=dbids&&dbids.length>0){
			try {
				for (Integer dbid : dbids) {
					pointRecordManageImpl.deleteByMemberId("delete from mem_pointRecord where memberId="+dbid);
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
		if(type==1){
			renderMsg("/member/queryList"+query+"&parentMenu=3", "删除数据成功！");
			return ;
		}
		if(type==2){
			renderMsg("/member/duplicateData", "删除数据成功！");
			return ;
		}
		renderMsg("/member/queryList"+query+"&parentMenu=3", "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String pointRecordDetail() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer memberId = ParamUtil.getIntParam(request, "memberId", -1);
		Integer selectType = ParamUtil.getIntParam(request, "selectType", -1);
		List<PointRecord> pointRecords=null;
		try{
			if(memberId>0){
				Member member2 = memberManageImpl.get(memberId);
				request.setAttribute("member", member2);
				if(selectType==0){
					pointRecords= pointRecordManageImpl.findBy("member.dbid", memberId);
					request.setAttribute("pointRecords", pointRecords);
				}else if(selectType==1){
					pointRecords = pointRecordManageImpl.executeSql("select *  from mem_PointRecord where memberId=? and num>?", new Object[]{member2.getDbid(),0});
					request.setAttribute("pointRecords", pointRecords);
				}
				else if(selectType==2){
					pointRecords = pointRecordManageImpl.executeSql("select *  from PointRecord where memberId=? and num<=?", new Object[]{member2.getDbid(),0});
					request.setAttribute("pointRecords", pointRecords);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "pointRecordDetail";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String memberSelect() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer memberLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
		String phone = request.getParameter("mobilePhone");
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.findBy("enterpriseId", enterprise.getDbid());
			request.setAttribute("memberShipLevels", memberShipLevels);
			if(memberLevelId<0&&null==phone){
				return "memberSelect";
			}
			String sql="select * from mem_Member where 1=1 ";
			if(memberLevelId>0){
				sql=sql+" and memberShipLevelId="+memberLevelId;
			}
			if(null!=phone&&phone.trim().length()>0){
				sql=sql+ " and mobilePhone like  '%"+phone+"%'";
			}
		    List<Member> members=memberManageImpl.executeSql(sql, null);
		    request.setAttribute("members", members);
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "memberSelect";
	}
	/**
	 * 功能描述：开启或听推荐权限
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void stopOrStartAgent() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer memberId = ParamUtil.getIntParam(request, "memberId", -1);
		try {
			Member member2 = memberManageImpl.get(memberId);
			Integer agentStatus = member2.getAgentStatus();
			if(agentStatus==null){
				member2.setAgentStatus(Member.AGENTAUTHED);
				member2.setAgentDate(new Date());
			}else{
				if(agentStatus==(int)Member.AGENTAUTHCOMM){
					member2.setAgentStatus(Member.AGENTAUTHED);
					member2.setAgentDate(new Date());
				}
				if(agentStatus==(int)Member.AGENTAUTHED){
					member2.setAgentStatus(Member.AGENTAUTHCOMM);
					member2.setAgentDate(new Date());
				}
			}
			memberManageImpl.save(member2);
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(new Throwable("权限设置错误，请重试"),"");
			return ;
		}
		String queryUrl = ParamUtil.getQueryUrl(request);
		renderMsg("/member/queryList"+queryUrl, "设置权限成功");
		return;
	}
	/**
	 * 功能描述：会员认证
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String authMember() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer memberId = ParamUtil.getIntParam(request, "memberId", -1);
		try {
			Member member2 = memberManageImpl.get(memberId);
			request.setAttribute("member", member2);
			
			String useCarArea = useCarAreaManageImpl.getUseCarArea(null);
			request.setAttribute("useCarArea", useCarArea);
			
		} catch (Exception e) {
			log.error(e);
		}
		return "authMember";
	}
	
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveMemberShipLeval() throws Exception {
		HttpServletRequest request = getRequest();
		Integer memberId = ParamUtil.getIntParam(request, "memberId", -1);
		Integer memberShipLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
		try {
			Member member2 = memberManageImpl.get(memberId);
			String sourceName="";
			MemberShipLevel memberShipLevel2 = member2.getMemberShipLevel();
			if(null!=memberShipLevel2){
				sourceName=memberShipLevel2.getName();
			}
			MemberShipLevel memberShipLevel = memberShipLevelManagImpl.get(memberShipLevelId);
			if(null!=memberShipLevel){
				member2.setMemberShipLevel(memberShipLevel);
				memberManageImpl.save(member2);
				//memLogManageImpl.saveMemberOperatorLog(memberId, "设置会员等级","原等级【"+sourceName+ "】会更改为【"+memberShipLevel.getName()+"】");
				renderMsg(memberShipLevel.getName(), "修改成功");
			}else{
				renderErrorMsg(new Throwable("请选择会员等级"), "修改失败");
			}
		} catch (Exception e) {
			renderErrorMsg(e, "");
		}
		return;
	}
	/**
	 * 功能描述：批量保存
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveMoreMemberShipLeval() throws Exception {
		HttpServletRequest request = getRequest();
		String memberIds = request.getParameter("memberIds");
		Integer memberShipLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
		try {
			MemberShipLevel memberShipLevel = memberShipLevelManagImpl.get(memberShipLevelId);
			if(null!=memberShipLevel){
				memberManageImpl.updateMember(memberIds, memberShipLevelId);
				renderMsg(memberShipLevel.getName(), "修改成功");
			}else{
				renderErrorMsg(new Throwable("请选择会员等级"), "修改失败");
			}
		} catch (Exception e) {
			renderErrorMsg(e, "");
		}
		return;
	}
	/**
	* 功能描述：
	* 参数描述：
	* 逻辑描述：
	* @return
	* @throws Exception
	*/
	public String  addTag() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			Member member2 = memberManageImpl.get(dbid);
			request.setAttribute("member",member2);
			List<MemTag> memTags = memTagManageImpl.getAll();
			request.setAttribute("memTags", memTags);
			String memTagIds = member2.getMemTagIds();
			if(null!=memTagIds&&memTagIds.length()>0){
				String[] areaIdArray = memTagIds.split(",");
				String memTagNames = member2.getMemTagNames();
				String[] areaNameArray = memTagNames.split(",");
				request.setAttribute("areaNameArray", areaNameArray);
				request.setAttribute("areaIdArray", areaIdArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "addTag";
	}
	/**
	 * 功能描述：设置会员是否为员工
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void updateMemberType() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer memberId = ParamUtil.getIntParam(request, "memberId", -1);
		String query = ParamUtil.getQueryUrl(request);
		String mess="";
		try {
			Member member2 = memberManageImpl.get(memberId);
			String mobilePhone = member2.getMobilePhone();
			User user=null;
			if(null!=mobilePhone){
				List<User> users = userManageImpl.findBy("mobilePhone", mobilePhone);
				if(null!=users&&users.size()==1){
					user = users.get(0);
					member2.setUser(user);
				}
			}
			if(null!=member2){
				Integer memType = member2.getMemType();
				if(memType==1){
					member2.setMemType(0);
				}else{
					if(null!=user){
						mess=" 绑定系统用户成功！";
					}
					member2.setMemType(1);
				}
				memberManageImpl.save(member2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(new Throwable("设置失败"),"");
			return ;
		}
		renderMsg("/member/queryList"+query, "设置成功  "+mess);
		return;
	}
	
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String queryAgentTotalList() throws Exception {
		HttpServletRequest request = getRequest();
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		Date start=null;
		Date end=null;
		try {
			if(null!=startTime&&startTime.trim().length()>0){
				start = DateUtil.string2Date(startTime);
			}else{
				start=DateUtil.string2Date(DateUtil.getNowMonthFirstDay()) ;
			}
			if(null!=endTime&&endTime.trim().length()>0){
				end=DateUtil.string2Date(endTime);
			}else{
				end=new Date();
			}
			String beginDate=DateUtil.format(start);
			String endDate=DateUtil.format(end);
			
			request.setAttribute("beginDate", beginDate);
			request.setAttribute("endDate", endDate);
			
			List<Member> members = memberManageImpl.queryAgentTotal(beginDate, endDate);
			request.setAttribute("members", members);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "agentTotalList";
	}
	
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveAddTag() throws Exception {
		HttpServletRequest request = getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		Integer type = ParamUtil.getIntParam(request, "type", 1);
		String memTagIds = request.getParameter("resultIds");
		String memTagNames = request.getParameter("resultNames");
		try {
			Member member=memberManageImpl.get(dbid);
			member.setMemTagIds(memTagIds);
			member.setMemTagNames(memTagNames);
			memberManageImpl.save(member);
			
			//memLogManageImpl.saveMemberOperatorLog(dbid, "贴标签", "会员添加/编辑标签");
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		if(type==1){
			renderMsg("/member/queryList", "设置标签成功！");
		}
		if(type==2){
			renderMsg("/member/information?dbid="+dbid+"&type=1", "设置标签成功！");
		}
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveClearPoint() throws Exception {
		HttpServletRequest request = getRequest();
		String memberIds=request.getParameter("memberIds");
		try {
			memberManageImpl.updateMemberPoint(memberIds);
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
		}
		renderMsg("", "清空成功");
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void validateMember() throws Exception {
		HttpServletRequest request = this.getRequest();
		String userId =null;
		userId=request.getParameter("member.userId");
		List<Member> members=null;
		if(null!=userId&&userId.trim().length()>0){
			members = memberManageImpl.findBy("userId", userId);
		}else{
			renderText("系统已经存在该用户了!请换一个用户ID!");//输入的账户类型不匹配！
			return ;
		}
		
		if (null!=members&&members.size()>0) {
			renderText("系统已经存在该用户了!请换一个用户ID!");//已经注册，请直接登录！
		}else{
			renderText("success");//
		}
	}
	/**
	 * 功能描述：通过IP地址获取客户端的地域地址
	 * @param request
	 * @return
	 */
	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getRemoteAddr();
		}
		return ip;
	}
	/**
	 * 功能描述：操作认证赠送积分
	 * @param member
	 */
	private void registerSendPoint(Member member){
		if(null==member){
			return ;
		}
		Integer enterpriseId = member.getEnterpriseId();
		if(null==enterpriseId||enterpriseId<=0){
			return ;
		}
		List<MemPointrecordSet> memPointrecordSets = memPointrecordSetManageImpl.findBy("enterpriseId",enterpriseId);
		if(null==memPointrecordSets||memPointrecordSets.size()<=0){
			return ;
		}
		MemPointrecordSet memPointrecordSet = memPointrecordSets.get(0);
		Integer customerSuccessStatus = memPointrecordSet.getCustomerSuccessStatus();
		//注册送积分
		if(null!=customerSuccessStatus&&customerSuccessStatus>0&&customerSuccessStatus==(int)MemPointrecordSet.START){
			Integer customerSuccessNum = memPointrecordSet.getCustomerSuccessNum();
			if(null!=customerSuccessNum&&customerSuccessNum>0){
				StringBuffer buffer=new StringBuffer();
				PointRecord pointRecord=new PointRecord();
				pointRecord.setCreateTime(new Date());
				pointRecord.setCreator("系统管理员");
				pointRecord.setEnterpriseId(enterpriseId);
				pointRecord.setMember(member);
				buffer.append("车主认证成功赠送积分，赠送："+customerSuccessNum+" 积分。");
				pointRecord.setNote(buffer.toString());
				pointRecord.setNum(memPointrecordSet.getRigPointNum());
				pointRecord.setPointFrom("车主认证成功赠送积分");
				pointRecord.setType(1);
				pointRecordManageImpl.save(pointRecord);
				
				//更新会员积分记录
				memberManageImpl.updateMemberPoint(member, pointRecord);
				
			}
		}
	}
	
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	/**
	 * 功能描述：会员导出
	 * @throws Exception
	 */
	public void exportExcel() throws Exception {
		HttpServletRequest request = this.getRequest();
		HttpServletResponse response = this.getResponse();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		Integer memberShipLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
		Integer memAuthStatus = ParamUtil.getIntParam(request, "memAuthStatus", -1);
		Integer carMasterStatus = ParamUtil.getIntParam(request, "carMasterStatus", -1);
		Integer agentStatus = ParamUtil.getIntParam(request, "agentStatus", -1);
		String mobilePhone = request.getParameter("mobilePhone");
		String name = request.getParameter("name");
		String nickName = request.getParameter("nickName");
		String carNo = request.getParameter("carNo");
		String car = request.getParameter("car");
		String vinNo = request.getParameter("vinNo");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		Integer hasCar = ParamUtil.getIntParam(request, "hasCar", -1);
		try{
			String sql="select * from mem_Member where 1=1 ";
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			User currentUser = SecurityUserHolder.getCurrentUser();
			if(currentUser.getQueryOtherDataStatus()==(int)User.QUERYYES){
				sql=sql+" and enterpriseId in("+currentUser.getCompnayIds()+")";
			}else{
				sql=sql+" and enterpriseId="+enterprise.getDbid();
			}
			List param= new ArrayList();
			if(memberShipLevelId>0){
				sql=sql+" and memberShipLevelId=? ";
				param.add(memberShipLevelId);
			}
			if(agentStatus>0){
				sql=sql+" and agentStatus=? ";
				param.add(agentStatus);
			}
			if(memAuthStatus>0){
				sql=sql+" and memAuthStatus=? ";
				param.add(memAuthStatus);
			}
			if(carMasterStatus>0){
				sql=sql+" and carMasterStatus=? ";
				param.add(carMasterStatus);
			}
			if(null!=name&&name.trim().length()>0){
				sql=sql+" and name like ? ";
				param.add("%"+name +"%");
			}
			if(null!=carNo&&carNo.trim().length()>0){
				sql=sql+" and carNo like ? ";
				param.add("%"+carNo +"%");
			}
			if(null!=car&&car.trim().length()>0){
				sql=sql+" and car like ? ";
				param.add("%"+car +"%");
			}
			if(null!=vinNo&&vinNo.trim().length()>0){
				sql=sql+" and vinNo like ? ";
				param.add("%"+vinNo +"%");
			}
			if(null!=nickName&&nickName.trim().length()>0){
				sql=sql+" and nickName like ? ";
				param.add("%"+nickName +"%");
			}
			if(null!=mobilePhone&&mobilePhone.trim().length()>0){
				sql=sql+" and mobilePhone like ? ";
				param.add("%"+mobilePhone+"%");
			}
			if(null!=hasCar&&hasCar>0){
				sql=sql+" and hasCar = ? ";
				param.add(hasCar);
			}
			if(null!=startTime&&startTime.trim().length()>0){
				sql=sql+" and createTime >= ? ";
				param.add(DateUtil.string2Date(startTime));
			}
			if(null!=endTime&&endTime.trim().length()>0){
				sql=sql+" AND createTime < ? ";
				param.add(DateUtil.nextDay(endTime));
			}
			sql=sql+" order by createTime DESC";
			List<Member> members = memberManageImpl.executeSql(sql, param.toArray());
			String fileName="经纪人"+DateUtil.format(new Date())+"日导出数据";
			//String filePath = memberToExcel.writeExcel(fileName, members);
			//downFile(request, response, filePath);
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
}
