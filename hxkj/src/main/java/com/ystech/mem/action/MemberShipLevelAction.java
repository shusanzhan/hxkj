/**
 * 
 */
package com.ystech.mem.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.mem.model.Member;
import com.ystech.mem.model.MemberShipLevel;
import com.ystech.mem.service.MemberManageImpl;
import com.ystech.mem.service.MemberShipLevelManagImpl;
import com.ystech.sys.model.Enterprise;

/**
 * @author shusanzhan
 * @date 2014-1-16
 */
@Component("memberShipLevelAction")
@Scope("prototype")
public class MemberShipLevelAction extends BaseController{
	private MemberShipLevel memberShipLevel;
	private MemberShipLevelManagImpl memberShipLevelManagImpl;
	private MemberManageImpl memberManageImpl;
	
	public MemberShipLevel getMemberShipLevel() {
		return memberShipLevel;
	}

	public void setMemberShipLevel(MemberShipLevel memberShipLevel) {
		this.memberShipLevel = memberShipLevel;
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
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		String name = request.getParameter("name");
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			Page<MemberShipLevel> page=memberShipLevelManagImpl.pagedQuerySql(pageNo, pageSize,MemberShipLevel.class, "select * from mem_MemberShipLevel where enterpriseId=?  ", new Object[]{enterprise.getDbid()});
			request.setAttribute("page", page);
		}catch (Exception e) {
			e.printStackTrace();
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
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			MemberShipLevel memberShipLevel = memberShipLevelManagImpl.get(dbid);
			request.setAttribute("memberShipLevel", memberShipLevel);
		}
		return "edit";
	}

	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void save() throws Exception {
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			memberShipLevel.setEnterpriseId(enterprise.getDbid());
			Integer dbid = memberShipLevel.getDbid();
			if(dbid==null||dbid<=0){
				memberShipLevel.setCreateTime(new Date());
				memberShipLevel.setModifyTime(new Date());
				
			}else{
				memberShipLevel.setModifyTime(new Date());
			}
			memberShipLevelManagImpl.save(memberShipLevel);
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/memberShipLevel/queryList", "保存数据成功！");
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
					//删除数据时判断是否有会员在使用
					memberShipLevelManagImpl.deleteById(dbid);
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
		renderMsg("/memberShipLevel/queryList"+query, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxMemberShipLevel() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer memberId = ParamUtil.getIntParam(request, "memberId", -1);
		StringBuffer ap=new StringBuffer();
		try {
			Member member = memberManageImpl.get(memberId);
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.findBy("enterpriseId", enterprise.getDbid());
			ap.append("<select class=\"text midea\" id=\"memberShipLevelId\" name=\"memberShipLevelId\">");
			for (MemberShipLevel memberShipLevel : memberShipLevels) {
				if(memberId>0){
					if(null!=member&&null!=member.getMemberShipLevel()){
						if(member.getMemberShipLevel().getDbid()==(int)memberShipLevel.getDbid()){
							ap.append("<option value='"+memberShipLevel.getDbid()+"' selected='selected'>"+memberShipLevel.getName()+"</option>");
						}else{
							ap.append("<option value='"+memberShipLevel.getDbid()+"'>"+memberShipLevel.getName()+"</option>");
						}
					}else{
						ap.append("<option value='"+memberShipLevel.getDbid()+"'>"+memberShipLevel.getName()+"</option>");
					}
				}else{
					ap.append("<option value='"+memberShipLevel.getDbid()+"'>"+memberShipLevel.getName()+"</option>");
				}
			}
			ap.append("</select>");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		renderText(ap.toString());
		return;
	}
}
