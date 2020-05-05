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
import com.ystech.mem.model.Member;
import com.ystech.mem.model.PointRecord;
import com.ystech.mem.service.MemberManageImpl;
import com.ystech.mem.service.PointRecordManageImpl;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.User;

/**
 * @author shusanzhan
 * @date 2014-2-26
 */
@Component("pointRecordAction")
@Scope("prototype")
public class PointRecordAction extends BaseController{
	private PointRecord pointRecord;
	private PointRecordManageImpl pointRecordManageImpl;
	private MemberManageImpl memberManageImpl;
	public void setPointRecord(PointRecord pointRecord) {
		this.pointRecord = pointRecord;
	}
	
	public PointRecord getPointRecord() {
		return pointRecord;
	}

	@Resource
	public void setPointRecordManageImpl(PointRecordManageImpl pointRecordManageImpl) {
		this.pointRecordManageImpl = pointRecordManageImpl;
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
		Integer memberId = ParamUtil.getIntParam(request,"memberId", -1);
		try{
			String sql="select * from mem_PointRecord  where 1=1 ";
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			User currentUser = SecurityUserHolder.getCurrentUser();
			if(currentUser.getQueryOtherDataStatus()==(int)User.QUERYYES){
				sql=sql+" and enterpriseId in("+currentUser.getCompnayIds()+")";
			}else{
				sql=sql+" and enterpriseId="+enterprise.getDbid();
			}
			Page<PointRecord> page= pointRecordManageImpl.pagedQuerySql(pageNo, pageSize, PointRecord.class, sql, null);
			request.setAttribute("page", page);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
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
		HttpServletRequest request = getRequest();
		try{
			Integer memberId = ParamUtil.getIntParam(request, "memberId", -1);
			Member member = memberManageImpl.get(memberId);
			request.setAttribute("member", member);
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
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
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			PointRecord pointRecord2 = pointRecordManageImpl.get(pointRecord);
			request.setAttribute("pointRecord", pointRecord2);
			request.setAttribute("member", pointRecord2.getMember());
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
	public void save() throws Exception {
		HttpServletRequest request = getRequest();
		Integer dbid=pointRecord.getDbid();
		try{
			Integer memberId = ParamUtil.getIntParam(request, "memberId", -1);
			User currentUser = SecurityUserHolder.getCurrentUser();
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			if(memberId>0){
				Member member = memberManageImpl.get(memberId);
				Integer totalPoint=0;
				if(null!=member.getTotalPoint()){
					totalPoint=member.getTotalPoint()+pointRecord.getNum();
				}else{
					totalPoint=pointRecord.getNum();
				}
				member.setTotalPoint(totalPoint);
				Integer overagePiont=0;
				if(null!=member.getOveragePiont()){
					overagePiont=member.getOveragePiont()+pointRecord.getNum();
				}else{
					overagePiont=pointRecord.getNum();
				}
				member.setOveragePiont(overagePiont);
				memberManageImpl.save(member);
				
				pointRecord.setMember(member);
				pointRecord.setEnterpriseId(enterprise.getDbid());
				pointRecord.setCreateTime(new Date());
				pointRecord.setCreator(currentUser.getRealName());
				pointRecordManageImpl.save(pointRecord);
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		if(null!=dbid&&dbid>0){
			renderMsg("/pointRecord/queryList", "保存数据成功！");
		}else{
			renderMsg("/member/queryList", "保存数据成功！");
		}
		return ;
	}

	/**
	 * 功能描述： 参数描述： 逻辑描述：
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
					pointRecordManageImpl.deleteById(dbid);
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
		renderMsg("/pointRecord/queryList"+query, "删除数据成功！");
		return;
	}
	
	/**
	 * 功能描述：积分报表
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String report() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		String creator = request.getParameter("creator");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		try{
			List param=new ArrayList();
			String sql="select * from mem_PointRecord  where 1=1 ";
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			User currentUser = SecurityUserHolder.getCurrentUser();
			if(currentUser.getQueryOtherDataStatus()==(int)User.QUERYYES){
				sql=sql+" and enterpriseId in("+currentUser.getCompnayIds()+")";
			}else{
				sql=sql+" and enterpriseId="+enterprise.getDbid();
			}
			if(null!=creator&&creator.trim().length()>0){
				sql=sql+" and creator like  ? ";
				param.add("%"+creator+"%");
			}
			if(null!=startTime&&startTime.trim().length()>0){
				sql=sql+" and createTime >= ? ";
				param.add(DateUtil.string2Date(startTime));
			}
			if(null!=endTime&&endTime.trim().length()>0){
				sql=sql+" and createTime < ? ";
				param.add(DateUtil.nextDay(endTime));
				
			}
			sql=sql+" order by createTime DESC";
			Page<PointRecord> page= pointRecordManageImpl.pagedQuerySql(pageNo, pageSize, PointRecord.class, sql, param.toArray());
			request.setAttribute("page", page);
			
			Object countZ = pointRecordManageImpl.count(creator, startTime, endTime, true,enterprise.getDbid());
			request.setAttribute("countZ", countZ);
			
			Object countF = pointRecordManageImpl.count(creator, startTime, endTime, false,enterprise.getDbid());
			request.setAttribute("countF", countF);
			
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			log.error(e);
		}
		return "report";
	}
	/**
	 * 功能描述：导出积分记录
	 * @throws Exception
	 */
	public void exportExcel() throws Exception {
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		String creator = request.getParameter("creator");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String fileName="";
		try {
			if(null!=startTime&&startTime.trim().length()>0){
				fileName=fileName+""+startTime;
			}
			
			if(null!=endTime&&endTime.trim().length()>0){
				fileName=fileName+"至"+endTime;
			}else{
				fileName=fileName+"至"+DateUtil.format(new Date());
			}
			List param=new ArrayList();
			String sql="select * from mem_PointRecord where 1=1 ";
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			User currentUser = SecurityUserHolder.getCurrentUser();
			if(currentUser.getQueryOtherDataStatus()==(int)User.QUERYYES){
				sql=sql+" and enterpriseId in("+currentUser.getCompnayIds()+")";
			}else{
				sql=sql+" and enterpriseId="+enterprise.getDbid();
			}
			if(null!=creator&&creator.trim().length()>0){
				sql=sql+" and creator like  ? ";
				param.add("%"+creator+"%");
			}
			if(null!=startTime&&startTime.trim().length()>0){
				sql=sql+" and createTime >= ? ";
				param.add(DateUtil.string2Date(startTime));
			}
			if(null!=endTime&&endTime.trim().length()>0){
				sql=sql+" and createTime < ? ";
				param.add(DateUtil.nextDay(endTime));
			}
			sql=sql+" order by createTime DESC";
			List<PointRecord> pointRecords = pointRecordManageImpl.executeSql(sql, param.toArray());
			//String filePath = PointRecordToExcel.writeExcel(fileName, pointRecords);
			//downFile(request, response, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
