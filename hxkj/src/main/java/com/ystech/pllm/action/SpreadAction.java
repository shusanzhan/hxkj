package com.ystech.pllm.action;

import java.math.BigInteger;
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
import com.ystech.core.util.PathUtil;
import com.ystech.core.web.BaseController;
import com.ystech.pllm.model.Spread;
import com.ystech.pllm.model.SpreadDetail;
import com.ystech.pllm.model.SpreadGroup;
import com.ystech.pllm.service.SpreadDetailManageImpl;
import com.ystech.pllm.service.SpreadDetailRecordManageImpl;
import com.ystech.pllm.service.SpreadGroupManageImpl;
import com.ystech.pllm.service.SpreadManageImpl;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinGzuserinfoManageImpl;

import net.sf.json.JSONObject;

@Component("spreadAction")
@Scope("prototype")
public class SpreadAction extends BaseController{
	private Spread spread;
	private SpreadManageImpl spreadManageImpl;
	private SpreadDetailManageImpl spreadDetailManageImpl;
	private SpreadDetail spreadDetail;
	private SpreadGroup spreadGroup;
	private SpreadGroupManageImpl spreadGroupManageImpl;
	private SpreadDetailRecordManageImpl spreadDetailRecordManageImpl;
	private WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	public Spread getSpread() {
		return spread;
	}
	public void setSpread(Spread spread) {
		this.spread = spread;
	}
	public SpreadDetail getSpreadDetail() {
		return spreadDetail;
	}
	public void setSpreadDetail(SpreadDetail spreadDetail) {
		this.spreadDetail = spreadDetail;
	}
	public SpreadGroup getSpreadGroup() {
		return spreadGroup;
	}
	public void setSpreadGroup(SpreadGroup spreadGroup) {
		this.spreadGroup = spreadGroup;
	}
	@Resource
	public void setSpreadManageImpl(SpreadManageImpl spreadManageImpl) {
		this.spreadManageImpl = spreadManageImpl;
	}
	@Resource
	public void setSpreadDetailManageImpl(SpreadDetailManageImpl spreadDetailManageImpl) {
		this.spreadDetailManageImpl = spreadDetailManageImpl;
	}
	@Resource
	public void setSpreadGroupManageImpl(SpreadGroupManageImpl spreadGroupManageImpl) {
		this.spreadGroupManageImpl = spreadGroupManageImpl;
	}
	@Resource
	public void setSpreadDetailRecordManageImpl(SpreadDetailRecordManageImpl spreadDetailRecordManageImpl) {
		this.spreadDetailRecordManageImpl = spreadDetailRecordManageImpl;
	}
	@Resource
	public void setWeixinGzuserinfoManageImpl(WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl) {
		this.weixinGzuserinfoManageImpl = weixinGzuserinfoManageImpl;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	/**
	 * 功能描述：查询
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String queryMyList() throws Exception {
		HttpServletRequest request = getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		String name = request.getParameter("name");
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			String sql="select * from pllm_S_Spread where 1=1 AND enterpriseId=? ";
			List params=new ArrayList();
			params.add(enterprise.getDbid());
			if(null!=name&&name.trim().length()>0){
				sql=sql+" and name like ? ";
				params.add("%"+name+"%");
			}
			Page<Spread> page= spreadManageImpl.pagedQuerySql(pageNo, pageSize,Spread.class, sql, params.toArray());
			request.setAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "myList";
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
		Integer spreadId = ParamUtil.getIntParam(request, "spreadId", -1);
		Integer spreadGroupId = ParamUtil.getIntParam(request, "spreadGroupId", -1);
		String name = request.getParameter("name");
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				String sql="select * from pllm_S_Spread where weixinAccountId="+weixinAccount.getDbid();
				List<Spread> spreads=spreadManageImpl.executeSql(sql,null);
				request.setAttribute("spreads", spreads);
				if(spreadId>0){
					List<SpreadGroup> spreadGroups = spreadGroupManageImpl.findBy("spread.dbid", spreadId);
					request.setAttribute("spreadGroups", spreadGroups);
				}else{
					List<SpreadGroup> spreadGroups = spreadGroupManageImpl.getAll();
					request.setAttribute("spreadGroups", spreadGroups);
				}
				String sql2="select * from pllm_s_spreaddetail where 1=1 ";
				List params=new ArrayList();
				if(spreadId>0){
					sql2=sql2+" and spreadId= ? ";
					params.add(spreadId);
				}
				if(spreadGroupId>0){
					sql2=sql2+" and groupId= ? ";
					params.add(spreadGroupId);
				}
				if(null!=name&&name.trim().length()>0){
					sql2=sql2+" and name like ? ";
					params.add("%"+name+"%");
				}
				sql2=sql2+" order by createDate DESC";
				Page<SpreadDetail> page = spreadDetailManageImpl.pagedQuerySql(pageNo, pageSize,SpreadDetail.class, sql2, params.toArray());
				request.setAttribute("page", page);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "list";
	}
	/**
	 * 功能描述：
	 * 参数描述： 
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String spreadGroupList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer spreadId = ParamUtil.getIntParam(request, "spreadId", -1);
		try {
			List<SpreadGroup> spreadGroups = spreadGroupManageImpl.findBy("spread.dbid", spreadId);
			request.setAttribute("spreadGroups", spreadGroups);
			
			Spread spread2 = spreadManageImpl.get(spreadId);
			request.setAttribute("spread", spread2);
		} catch (Exception e) {
		}
		return "spreadGroupList";
	}
	/**
	 * 功能描述：编辑添加分组
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String editSpreadGroup() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer spreadId = ParamUtil.getIntParam(request, "spreadId", -1);
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			Spread spread2 = spreadManageImpl.get(spreadId);
			request.setAttribute("spread", spread2);
			
			SpreadGroup spreadGroup2 = spreadGroupManageImpl.get(dbid);
			request.setAttribute("spreadGroup", spreadGroup2);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "editSpreadGroup";
	}
	/**
	 * 功能描述：保存分组
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveSpreadGroup() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer spreadId = ParamUtil.getIntParam(request, "spreadId", -1);
		try {
			Spread spread2 = spreadManageImpl.get(spreadId);
			spreadGroup.setSpread(spread2);;
			spreadGroupManageImpl.save(spreadGroup);
		} catch (Exception e) {
			// TODO: handle exception
			renderErrorMsg(e, "");
			return ;
		}
		renderMsgParams("/spread/spreadGroupList?spreadId="+spreadId, "保存数据成功！",spreadId+""+spreadGroup.getDbid(),spreadGroup.getDbid()+"");
		return;
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
		if(dbid>0){
			Spread spread2 = spreadManageImpl.get(dbid);
			request.setAttribute("spread", spread2);
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
	public void ajaxEdit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		JSONObject object=new JSONObject();
		if(dbid>0){
			Spread spread2 = spreadManageImpl.get(dbid);
			request.setAttribute("spread", spread2);
			object.put("dbid", spread2.getDbid());
			object.put("name", spread2.getName());
			object.put("policyStateMentUrl", spread2.getPolicyStateMentUrl());
			object.put("note", spread2.getNote());
		}
		renderJson(object.toString());
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
		String cString="";
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				Integer dbid = spread.getDbid();
				if(dbid==null||dbid<=0){
					spread.setWeixinAccountId(weixinAccount.getDbid());
					spread.setScanCodeNum(0);
					spread.setSpreadNum(0);
					spread.setStatus(2);
					spread.setCreateDate(new Date());
					spread.setModifyDate(new Date());
					spreadManageImpl.save(spread);
					
					
					List<Spread> all = spreadManageImpl.getAll();
					cString=createSperad(spread, all.size());
				}else{
					Spread spread2 = spreadManageImpl.get(spread.getDbid());
					spread2.setName(spread.getName());
					spread2.setNote(spread.getNote());
					spread2.setModifyDate(new Date());
					spread2.setPolicyStateMentUrl(spread.getPolicyStateMentUrl());
					spreadManageImpl.save(spread2);
					cString=spread2.getName();
				}
			}
			else{
				renderErrorMsg(new Throwable("无公众号信息"), "");
				return ;
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/spread/queryList",cString);
		return ;
	}

	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		if(null!=dbids&&dbids.length>0){
			try {
				for (Integer dbid : dbids) {
					List<SpreadGroup> spreadGroups = spreadGroupManageImpl.findBy("spread.dbid", dbid);
					if(null!=spreadGroups&&spreadGroups.size()>0){
						renderErrorMsg(new Throwable("删除渠道失败，该渠道下包含分组信息，请先删除分组！"), "");
						return ;
					}
					spreadManageImpl.deleteById(dbid);
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
		renderMsg("/spread/queryList"+query, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void deleteSpreadGroup() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		if(null!=dbids&&dbids.length>0){
			try {
				for (Integer dbid : dbids) {
					spreadGroupManageImpl.deleteById(dbid);
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
		renderMsg("/spread/spreadGroupList?spreadId=", "删除数据成功！");
		return;
	}
	/**
	* 功能描述：二维码统计
	* 参数描述：
	* 逻辑描述：
	* @return
	* @throws Exception
	*/
	public String staticQrCode() throws Exception {
		HttpServletRequest request = this.getRequest();
		String begin = request.getParameter("begin");
		String end = request.getParameter("end");
		try {
			String dateSql="";
			if(null!=begin&&begin.trim().length()>0){
				dateSql=dateSql+" and scDate>='"+begin+"' ";
			}
			if(null!=end&&end.trim().length()>0){
				dateSql=dateSql+" and scDate>='"+DateUtil.nextDay(end)+"' ";
			}
			String dateGzSql="";
			if(null!=begin&&begin.trim().length()>0){
				dateGzSql=dateGzSql+" and addtime>='"+begin+"' ";
			}
			if(null!=end&&end.trim().length()>0){
				dateGzSql=dateGzSql+" and addtime>='"+DateUtil.nextDay(end)+"' ";
			}
			String paySuccessDateGzSql="";
			if(null!=begin&&begin.trim().length()>0){
				paySuccessDateGzSql=paySuccessDateGzSql+" and paySuccessDate>='"+begin+"' ";
			}
			if(null!=end&&end.trim().length()>0){
				paySuccessDateGzSql=paySuccessDateGzSql+" and paySuccessDate>='"+DateUtil.nextDay(end)+"' ";
			}
			
			//扫描次数
			String scanCountSql="SELECT COUNT(*) FROM pllm_s_spreaddetailrecord WHERE 1=1 "+dateSql;
			Object scanCount = spreadManageImpl.count(scanCountSql, null);
			request.setAttribute("scanCount", scanCount);
			//扫描人数
			String scanGzUserCountSql="SELECT COUNT(*) FROM pllm_s_spreaddetailrecord WHERE 1=1 "+dateSql;
			Object scanGzUserCount = spreadManageImpl.count(scanGzUserCountSql, null);
			request.setAttribute("scanGzUserCount", scanGzUserCount);
			//新增人数
			String scanNewGzUserCountSql="SELECT COUNT(*) FROM pllm_s_spreaddetailrecord WHERE 1=1 and type=1 "+dateSql;
			Object scanNewGzUserCount = spreadManageImpl.count(scanNewGzUserCountSql, null);
			request.setAttribute("scanNewGzUserCount", scanNewGzUserCount);
			
			//新增人数（总的）
			String gzUserEventedSql="SELECT COUNT(*) FROM weixin_gzuserinfo WHERE eventStatus=1 "+dateGzSql;
			Object gzUserEventedCount = weixinGzuserinfoManageImpl.count(gzUserEventedSql,null);
			request.setAttribute("gzUserEventedCount", gzUserEventedCount);
			
			
			//二维码渠道统计 图像
			List<Spread> spreads = spreadManageImpl.queryCount(dateSql);
			request.setAttribute("spreads", spreads);
			StringBuffer buf=new StringBuffer();
			int i=0;
			for (Spread spread : spreads) {
				if((i+1)==spreads.size()){
					buf.append("['"+spread.getName()+"',"+spread.getSpreadNum()+"]");
				}else{
					buf.append("['"+spread.getName()+"',"+spread.getSpreadNum()+"],");
				}
				i++;
			}
			request.setAttribute("data", buf.toString());
			String spreadDetailStatic = createQrcodeTable(spreads);
			request.setAttribute("spreadDetailStatic", spreadDetailStatic);
			
			
			//扫描记录
			/*String scanSql="SELECT * FROM pllm_s_spreaddetailrecord WHERE 1=1 "+dateSql;
			List<SpreadDetailRecord> spreadDetailRecords = spreadDetailRecordManageImpl.executeSql(scanSql, null);
			request.setAttribute("spreadDetailRecords", spreadDetailRecords);*/
			
			List<Spread> spreadNuls =spreadManageImpl.getAll();
			List<Spread> spreads2 = createSpreadTable(spreadNuls, dateSql);
			request.setAttribute("spreads2", spreads2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "staticQrCode";
	}
	/**
	 * 功能描述：带参数二维码数据
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public List<Spread> createSpreadTable(List<Spread> spreads,String dateSql) throws Exception {
		for (Spread spread : spreads) {
			String countSql="SELECT COUNT(*) FROM pllm_s_spreaddetailrecord WHERE 1=1 and spreadId="+spread.getDbid()+"  "+dateSql;
			String newSql="SELECT COUNT(*) FROM pllm_s_spreaddetailrecord WHERE 1=1 and spreadId="+spread.getDbid()+" and type=1 "+dateSql;
			BigInteger countNum = spreadManageImpl.count(countSql,null);
			BigInteger countNewNum = spreadManageImpl.count(newSql,null);
			if(null!=countNum){
				spread.setCountNum(countNum.intValue());
			}else{
				spread.setCountNum(0);
			}
			if(null!=countNewNum){
				spread.setSpreadNum(countNewNum.intValue());
			}else{
				spread.setSpreadNum(0);
			}
		}
		return spreads;
	}
	/**
	 * 功能描述：各渠道二维码扫描明细
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String createQrcodeTable(List<Spread> spreads) throws Exception {
		StringBuffer buffer=new StringBuffer();
		for (Spread spread : spreads) {
			List<SpreadDetail> spreadDetails = spreadDetailManageImpl.findBy("spread.dbid", spread.getDbid());
			buffer.append("<table class=\"ui-table ui-table-thead-weight\">");
			buffer.append("<thead>");
			buffer.append("<tr>");
			buffer.append("<td align=\"center\" colspan=\"6\" style=\"text-align: left;font-size:16px;\">"+spread.getName()+"</td>");
			buffer.append("</tr>");
			buffer.append("<tr>");
			buffer.append("<th align=\"center\" width=\"40\">序号</th>");
			buffer.append("<th align=\"center\" width=\"80\">二维码名称</th>");
			buffer.append("<th align=\"center\" width=\"60\">扫码次数</th>");
			buffer.append("<th align=\"center\" width=\"60\">引流人数</th>");
			buffer.append("<th align=\"center\" width=\"60\">新粉丝</th>");
			buffer.append("<th align=\"center\" width=\"60\">老粉丝</th>");
			buffer.append("</tr>");
			buffer.append("</thead>");
			buffer.append("<tbody class=\"js-list-tbody\">");
			if(null!=spreadDetails&&spreadDetails.size()>0){
				int i=1;
				if(null!=spreadDetails&&spreadDetails.size()>0){
					for (SpreadDetail spreadDetail : spreadDetails) {
						buffer.append("<tr>");
						buffer.append("<td align=\"center\" width=\"40\">"+i+"</td>");
						buffer.append("<td align=\"center\" width=\"80\">"+spreadDetail.getName()+"</td>");
						buffer.append("<td align=\"center\" width=\"60\">"+spreadDetail.getScanNum()+"</td>");
						buffer.append("<td align=\"center\" width=\"60\">"+spreadDetail.getSpreadNum()+"</td>");
						buffer.append("<td align=\"center\" width=\"60\">"+spreadDetail.getSpreadNum()+"</td>");
						buffer.append("<td align=\"center\" width=\"60\">"+(spreadDetail.getScanNum()-spreadDetail.getSpreadNum())+"</td>");
						buffer.append("</tr>");
						i++;
					}
					buffer.append("<tr>");
					buffer.append("<td align=\"center\" colspan=\"2\" style=\"text-align: right;\">合计：</td>");
					buffer.append("<td align=\"center\" width=\"60\">"+spread.getScanCodeNum()+"</td>");
					buffer.append("<td align=\"center\" width=\"60\">"+spread.getSpreadNum()+"</td>");
					buffer.append("<td align=\"center\" width=\"60\">"+spread.getSpreadNum()+"</td>");
					buffer.append("<td align=\"center\" width=\"60\">"+(spread.getScanCodeNum()-spread.getSpreadNum())+"</td>");
					buffer.append("</tr>");
				}
			}else{
				buffer.append("<tr>");
				buffer.append("<td align=\"center\" width=\"40\" colspan=\"6\">无数据</td>");
				buffer.append("</tr>");
			}
			buffer.append("</tbody>");
			buffer.append("</table>");
			buffer.append("<br>");
		}
		return buffer.toString();
	}
	/**
	 * 创建分组
	 * @param spread
	 * @param size
	 * @return
	 */
	private String createSperad(Spread spread,int size){
		String text=" <div id='spreadDiv"+spread.getDbid()+"'> "
				+ "<div class=\"help-body-title\">"
					+ "<div style=\"float: left;\">"
						+ size+"、<span id='spread"+spread.getDbid()+"'>"+spread.getName()+"</span>"
					+ "</div>"
					+ " <div class=\"rule-opts\" style=\"float: right;padding-right: 12px;\">"
					+ "  <a href=\"javascript:;\" class=\"js-edit-rule\" onclick=\"editSpread("+spread.getDbid()+")\">编辑</a> "
					+ " 	<span>-</span>"
					+ "		 <a href=\"javascript:;\" class=\"js-edit-rule\" onclick=\"window.location.href='${ctx}/agentSet/edit?spreadId="+spread.getDbid()+"'\">设置奖励</a>"
				;
		if(spread.getStatus()==2){
			text=text+ " 	<span>-</span>"
					+ "		<a href=\"javascript:;\" class=\"js-disable-rule\" onclick=\"deleteSpread('${ctx}/spread/delete?dbids="+spread.getDbid()+"','"+spread.getDbid()+"')\">删除</a>";
		}
			
		text=text+ "</div>"
					+ "<div style='clear: both;'></div>"
					+ " <div class='help-body-content' id='spreadDiv'>"
					+ "<p>"
					+ "		<span style=\"color: #999999;\">引流："+spread.getSpreadNum()+"</span>&nbsp;&nbsp;"
					+ "     <span style=\"color: #999999;\">扫码："+spread.getScanCodeNum()+"</span>"
					+ "     <span style=\"color: #999999;\">分组数：0</span>"
					+"</p>"
					+ "<p><a href=\"javascript:void(-1)\" onclick=\"addSpreadGroup("+spread.getDbid()+")\" >创建分组</a></p>"
							+ "<div id=\"spreadDetail"+spread.getDbid()+"\" class=\"keyword-list\">"
							+ "</div>"
					+ "</div>"
		+ "</div>"
		+ "<div class=\"help-body-split-line\"></div>"
		+ "</div>";
		return text;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxSpreadGroup() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer spreadId = ParamUtil.getIntParam(request, "spreadId", -1);
		StringBuffer buffer=new StringBuffer();
		try {
			List<SpreadGroup> spreadGroups = spreadGroupManageImpl.findBy("spread.dbid", spreadId);
			buffer.append("<option value=\"0\">请选择...</option>");
			for (SpreadGroup spreadGroup : spreadGroups) {
				buffer.append("<option value='"+spreadGroup.getDbid()+"'>"+spreadGroup.getName()+"</option>");
			}
		} catch (Exception e) {
			buffer.append("<option value=\"0\">请选择...</option>");
			renderText(buffer.toString());
			return;
		}
		renderText(buffer.toString());
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void downloand() throws Exception {
		HttpServletRequest request = getRequest();
		HttpServletResponse response = getResponse();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			SpreadDetail spreadDetail2 = spreadDetailManageImpl.get(dbid);
			String qrcode = spreadDetail2.getQrcode();
			String filePath=PathUtil.getWebRootPath()+qrcode;
			downFile(request, response, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
}
