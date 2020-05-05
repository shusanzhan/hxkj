package com.ystech.mem.action;

import java.util.ArrayList;
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
import com.ystech.mem.model.MemTag;
import com.ystech.mem.model.Member;
import com.ystech.mem.service.MemTagManageImpl;
import com.ystech.mem.service.MemberManageImpl;
import com.ystech.pllm.model.SpreadDetail;
import com.ystech.pllm.service.SpreadDetailManageImpl;
import com.ystech.sys.model.Enterprise;

@Component("memTagAction")
@Scope("prototype")
public class MemTagAction extends BaseController{
	private MemTag memTag;
	private MemTagManageImpl memTagManageImpl;
	private MemberManageImpl memberManageImpl;
	private SpreadDetailManageImpl spreadDetailManageImpl;
	public MemTag getMemTag() {
		return memTag;
	}

	public void setMemTag(MemTag memTag) {
		this.memTag = memTag;
	}
	@Resource
	public void setMemTagManageImpl(MemTagManageImpl memTagManageImpl) {
		this.memTagManageImpl = memTagManageImpl;
	}
	@Resource
	public void setMemberManageImpl(MemberManageImpl memberManageImpl) {
		this.memberManageImpl = memberManageImpl;
	}
	@Resource
	public void setSpreadDetailManageImpl(SpreadDetailManageImpl spreadDetailManageImpl) {
		this.spreadDetailManageImpl = spreadDetailManageImpl;
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
		String name = request.getParameter("name");
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			String sql="select * from mem_memTag where 1=1 and enterpriseId="+enterprise.getDbid();
			List params=new ArrayList();
			if(null!=name&&name.trim().length()>0){
				sql=sql+" and name like ? ";
				params.add("%"+name+"%");
			}
			Page<MemTag> page = memTagManageImpl.pagedQuerySql(pageNo, pageSize,MemTag.class,sql,params.toArray());
			request.setAttribute("page", page);
		} catch (Exception e) {
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
				MemTag memTag2 = memTagManageImpl.get(dbid);
				request.setAttribute("memTag", memTag2);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			Integer dbid = memTag.getDbid();
			if(dbid==null||dbid<=0){
				memTag.setCreateDate(new Date());
				memTag.setEnterpriseId(enterprise.getDbid());
				memTag.setModifyDate(new Date());
				memTag.setStatus(MemTag.COMM);
				memTag.setNum(0);
				memTagManageImpl.save(memTag);
			}else{
				MemTag memTag2 = memTagManageImpl.get(dbid);
				memTag2.setName(memTag.getName());
				memTag2.setNote(memTag.getNote());
				memTagManageImpl.save(memTag2);
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/memTag/queryList", "保存数据成功！");
		return ;
	}
	/**
	* 功能描述：
	* 参数描述：
	* 逻辑描述：
	* @return
	* @throws Exception
	*/
	public String selectTag() throws Exception {
		HttpServletRequest request = getRequest();
		Integer memberId = ParamUtil.getIntParam(request, "memberId", -1);
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			if(memberId>0){
				Member member = memberManageImpl.get(memberId);
				request.setAttribute("member", member);
				
				String memTagIds = member.getMemTagIds();
				String[] tagIds = memTagIds.split(",");
				request.setAttribute("tagIds", tagIds);
				
			}
			
			List<MemTag> memTags = memTagManageImpl.findBy("enterpriseId", enterprise.getDbid());
			request.setAttribute("memTags", memTags);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "selectTag";
	}
	
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveSelectTag() throws Exception {
		HttpServletRequest request = getRequest();
		String memberIds = request.getParameter("memberIds");
		String memTagIds = request.getParameter("memTagIds");
		String tagNames = request.getParameter("tagNames");
		try {
			if(null==memberIds||memberIds.trim().length()<=0){
				renderErrorMsg(new Throwable("设置失败，请选择会员"), "");
				return ;
			}
			String[] memIds = memberIds.split(",");
			if(memIds.length<=0){
				renderErrorMsg(new Throwable("设置失败，请选择会员"), "");
				return ;
			}
			if(null==memTagIds||memTagIds.trim().length()<=0){
				renderErrorMsg(new Throwable("设置失败，请选择会员标签"), "");
				return ;
			}
			memTagIds=memTagIds.substring(0, memTagIds.trim().length()-1);
			tagNames=tagNames.substring(0, tagNames.trim().length()-1);
			for (String memberIdStr : memIds) {
				if(null!=memberIdStr&&memberIdStr.trim().length()>0){
					int memberId = Integer.parseInt(memberIdStr);
					Member member = memberManageImpl.get(memberId);
					member.setMemTagIds(memTagIds);
					member.setMemTagNames(tagNames);
					memberManageImpl.save(member);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderMsg("", "保存数据成功");
		return;
	}
	//////////////////////////////////参数二维码////////////////////////////////
	/**
	* 功能描述：
	* 参数描述：
	* 逻辑描述：
	* @return
	* @throws Exception
	*/
	public String spreadDetailTag() throws Exception {
		HttpServletRequest request = getRequest();
		Integer spreadDetailId = ParamUtil.getIntParam(request, "spreadDetailId", -1);
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			if(spreadDetailId>0){
				SpreadDetail spreadDetail = spreadDetailManageImpl.get(spreadDetailId);
				request.setAttribute("spreadDetail", spreadDetail);
				
				String memTagIds = spreadDetail.getMemTagIds();
				if(null!=memTagIds){
					String[] tagIds = memTagIds.split(",");
					request.setAttribute("tagIds", tagIds);
				}
			}
			
			List<MemTag> memTags = memTagManageImpl.findBy("enterpriseId", enterprise.getDbid());
			request.setAttribute("memTags", memTags);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "spreadDetailTag";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveSpreadTags() throws Exception {
		HttpServletRequest request = getRequest();
		String memTagIds = request.getParameter("memTagIds");
		String tagNames = request.getParameter("tagNames");
		Integer spreadDetailId = ParamUtil.getIntParam(request, "spreadDetailId", -1);
		try {
			if(spreadDetailId<=0){
				renderErrorMsg(new Throwable("设置失败，请选参数二维码"), "");
				return ;
			}
			if(null==memTagIds||memTagIds.trim().length()<=0){
				renderErrorMsg(new Throwable("设置失败，请选择会员标签"), "");
				return ;
			}
			memTagIds=memTagIds.substring(0, memTagIds.trim().length()-1);
			tagNames=tagNames.substring(0, tagNames.trim().length()-1);
			SpreadDetail spreadDetail = spreadDetailManageImpl.get(spreadDetailId);
			spreadDetail.setMemTagIds(memTagIds);
			spreadDetail.setMemTagNames(tagNames);
			spreadDetailManageImpl.save(spreadDetail);
			String tagHtml = tagHtml(spreadDetail);
			renderMsg(tagHtml, "保存数据成功");
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
		}
		return;
	}
	/**
	 * 生成前台页面数据
	 * @return
	 */
	private String tagHtml(SpreadDetail spreadDetail){
		StringBuffer buffer=new StringBuffer();
		String memTagNames = spreadDetail.getMemTagNames();
		if(null!=memTagNames&&memTagNames.trim().length()>0){
			String[] split = memTagNames.split(",");
			for (String tag : split) {
				buffer.append("<div class=\"tag\">"+tag+"</div>");
			}
		}
		return buffer.toString();
	}
	//////////////////////////////////参数二结束////////////////////////////////
	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		Integer parentMenu = ParamUtil.getIntParam(request, "parentMenu", 5);
		try {
			if(null==dbids||dbids.length<=0){
				renderErrorMsg(new Throwable("未选中数据！"), "");
				return ;
			}
			boolean status=false;
			StringBuffer buffer=new StringBuffer();
			for (Integer dbid : dbids) {
				MemTag memTag2 = memTagManageImpl.get(dbid);
				if(memTag2.getStatus()==(int)MemTag.YEAS){
					status=true;
					buffer.append("【"+memTag2.getName()+"】有会员存在引用！");
				}
			}
			if(status==true){
				renderErrorMsg(new Throwable(buffer.toString()), "");
				return ;
			}
			for (Integer dbid : dbids) {
				memTagManageImpl.deleteById(dbid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		String query = ParamUtil.getQueryUrl(request);
		renderMsg("/memTag/queryList"+query, "删除数据成功！");
		return;
	}
}
