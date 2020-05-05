package com.ystech.pllm.action;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.pllm.model.RecPageUrl;
import com.ystech.pllm.service.RecPageUrlManageImpl;

@Component("recPageUrlAction")
@Scope("prototype")
public class RecPageUrlAction extends BaseController{
	private RecPageUrl recPageUrl;
	private RecPageUrlManageImpl recPageUrlManageImpl;
	public RecPageUrl getRecPageUrl() {
		return recPageUrl;
	}
	public void setRecPageUrl(RecPageUrl recPageUrl) {
		this.recPageUrl = recPageUrl;
	}
	@Resource
	public void setRecPageUrlManageImpl(RecPageUrlManageImpl recPageUrlManageImpl) {
		this.recPageUrlManageImpl = recPageUrlManageImpl;
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
		try {
			String username = request.getParameter("title");
			Page<RecPageUrl> page=null;
			if(null!=username&&username.trim().length()>0){
				page = recPageUrlManageImpl.pagedQuerySql(pageNo, pageSize,RecPageUrl.class,"select * from pllm_rec_pageurl where  title like '%"+username+"%'", new Object[]{});
			}else{
				page = recPageUrlManageImpl.pagedQuerySql(pageNo, pageSize,RecPageUrl.class, "select *  from pllm_rec_pageurl   ", new Object[]{});
			}
			request.setAttribute("page", page);
		} catch (Exception e) {
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
		HttpServletRequest request = this.getRequest();
		String url="http://"+request.getRemoteHost()+"/";
		request.setAttribute("url", url);
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
			RecPageUrl recPageUrl2 = recPageUrlManageImpl.get(dbid);
			request.setAttribute("recPageUrl", recPageUrl2);
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
			Integer dbid = recPageUrl.getDbid();
			if(dbid==null||dbid<=0){
				recPageUrl.setCreateDate(new Date());
				recPageUrl.setClickNum(0);
				recPageUrl.setNum(0);
				recPageUrl.setShareNum(0);
				recPageUrl.setReadNum(0);
				recPageUrl.setModifyDate(new Date());
				recPageUrlManageImpl.save(recPageUrl);
			}else{
				RecPageUrl recPageUrl2 = recPageUrlManageImpl.get(dbid);
				recPageUrl2.setModifyDate(new Date());
				recPageUrl2.setName(recPageUrl.getName());
				recPageUrl2.setNote(recPageUrl.getNote());
				recPageUrl2.setUrl(recPageUrl.getUrl());
				recPageUrlManageImpl.save(recPageUrl2);
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/recPageUrl/queryList?", "保存数据成功！");
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
		Integer parentMenu = ParamUtil.getIntParam(request, "parentMenu", 5);
		if(null!=dbids&&dbids.length>0){
			try {
				for (Integer dbid : dbids) {
					recPageUrlManageImpl.deleteById(dbid);
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
		renderMsg("/recPageUrl/queryList"+query+"&parentMenu="+parentMenu, "删除数据成功！");
		return;
	}
	
	
}
