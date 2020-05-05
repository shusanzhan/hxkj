package com.ystech.weixin.service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.weixin.model.WeixinTexttemplate;
@Component("weixinTextTemplateAction")
@Scope("prototype")
public class WeixinTextTemplateAction extends BaseController{
	private WeixinTexttemplate weixinTexttemplate;
	private WeixinTexttemplateManageImpl weixinTexttemplateManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	public WeixinTexttemplate getWeixinTexttemplate() {
		return weixinTexttemplate;
	}
	public void setWeixinTexttemplate(WeixinTexttemplate weixinTexttemplate) {
		this.weixinTexttemplate = weixinTexttemplate;
	}
	@Resource
	public void setWeixinTexttemplateManageImpl(
			WeixinTexttemplateManageImpl weixinTexttemplateManageImpl) {
		this.weixinTexttemplateManageImpl = weixinTexttemplateManageImpl;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
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
		String username = request.getParameter("title");
		try{
			 Page<WeixinTexttemplate> page= weixinTexttemplateManageImpl.pagedQuerySql(pageNo, pageSize, WeixinTexttemplate.class, "select * from weixin_texttemplate", new Object[]{});
			 request.setAttribute("page", page);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "list";
	}
	
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
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
			WeixinTexttemplate weixinTexttemplate2 = weixinTexttemplateManageImpl.get(dbid);
			request.setAttribute("weixinTexttemplate", weixinTexttemplate2);
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
		//slide.setBussiId(currentBussi);
		try{
			weixinTexttemplateManageImpl.save(weixinTexttemplate);
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/weixinTexttemplate/queryList", "保存数据成功！");
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
					//slideManageImpl.deleteById(dbid);
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
		renderMsg("/weixinTexttemplate/queryList"+query, "删除数据成功！");
		return;
	}
}
