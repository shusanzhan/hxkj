package com.ystech.weixin.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.util.DateUtil;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.weixin.model.WeixinNewstemplate;
import com.ystech.weixin.model.WeixinSubscribe;
import com.ystech.weixin.model.WeixinTexttemplate;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinNewstemplateManageImpl;
import com.ystech.weixin.service.WeixinSubscribeManageImpl;
import com.ystech.weixin.service.WeixinTexttemplateManageImpl;

@Component("weixinSubscribeAction")
@Scope("prototype")
public class WeixinSubscribeAction extends BaseController {
	private WeixinSubscribe weixinSubscribe;
	private WeixinSubscribeManageImpl weixinSubscribeManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinTexttemplateManageImpl weixinTexttemplateManageImpl;
	private WeixinNewstemplateManageImpl weixinNewstemplateManageImpl;
	public WeixinSubscribe getWeixinSubscribe() {
		return weixinSubscribe;
	}
	public void setWeixinSubscribe(WeixinSubscribe weixinSubscribe) {
		this.weixinSubscribe = weixinSubscribe;
	}
	@Resource
	public void setWeixinSubscribeManageImpl(
			WeixinSubscribeManageImpl weixinSubscribeManageImpl) {
		this.weixinSubscribeManageImpl = weixinSubscribeManageImpl;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	@Resource
	public void setWeixinTexttemplateManageImpl(
			WeixinTexttemplateManageImpl weixinTexttemplateManageImpl) {
		this.weixinTexttemplateManageImpl = weixinTexttemplateManageImpl;
	}
	@Resource
	public void setWeixinNewstemplateManageImpl(
			WeixinNewstemplateManageImpl weixinNewstemplateManageImpl) {
		this.weixinNewstemplateManageImpl = weixinNewstemplateManageImpl;
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
			 Page<WeixinSubscribe> page= weixinSubscribeManageImpl.pagedQuerySql(pageNo, pageSize, WeixinSubscribe.class, "select * from Weixin_Subscribe ", new Object[]{});
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
		HttpServletRequest request = this.getRequest();
		try{
			List<WeixinSubscribe> weixinSubscribes = weixinSubscribeManageImpl.getAll();
			if(null!=weixinSubscribes&&weixinSubscribes.size()>0){
				request.setAttribute("message", "只能填写一条关注时回复记录！");
			}else{
				List<WeixinTexttemplate> weixinTexttemplates = weixinTexttemplateManageImpl.getAll();
				request.setAttribute("weixinTexttemplates", weixinTexttemplates);
			}
		}catch (Exception e) {
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
		try {
			Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
			if(dbid>0){
				WeixinSubscribe weixinSubscribe2 = weixinSubscribeManageImpl.get(dbid);
				request.setAttribute("weixinSubscribe", weixinSubscribe2);
				if(weixinSubscribe2.getMsgType().equals("text")){
					List<WeixinTexttemplate> weixinTexttemplates = weixinTexttemplateManageImpl.getAll();
					request.setAttribute("weixinTexttemplates", weixinTexttemplates);
				}
				if(weixinSubscribe2.getMsgType().equals("news")){
					 List<WeixinNewstemplate> weixinTexttemplates = weixinNewstemplateManageImpl.getAll();
					request.setAttribute("weixinTexttemplates", weixinTexttemplates);
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
	public void save() throws Exception {
		HttpServletRequest request = getRequest();
		String msgType = request.getParameter("weixinSubscribe.msgType");
		Integer templateId = ParamUtil.getIntParam(request, "templateId", 1);
		try{
			Integer dbid = weixinSubscribe.getDbid();
			 weixinSubscribe.setMsgType(msgType+"");
			 if(msgType.equals("text")){
				  WeixinTexttemplate weixinTexttemplate = weixinTexttemplateManageImpl.get(templateId);
				  if(null!=weixinTexttemplate){
					  weixinSubscribe.setTemplateId(weixinTexttemplate.getDbid());
					  weixinSubscribe.setTemplateName(weixinTexttemplate.getTemplatename());
				  }
			 }
			 if(msgType.equals("news")){
				 WeixinNewstemplate weixinNewstemplate = weixinNewstemplateManageImpl.get(templateId);
				  if(null!=weixinNewstemplate){
					  weixinSubscribe.setTemplateId(weixinNewstemplate.getDbid());
					  weixinSubscribe.setTemplateName(weixinNewstemplate.getTemplatename());
				  }
			 }
			if(dbid==null||dbid<=0){
				weixinSubscribe.setAddTime(DateUtil.format2(new Date()));
				weixinSubscribeManageImpl.save(weixinSubscribe);
			}else{
				weixinSubscribeManageImpl.save(weixinSubscribe);
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/weixinSubscribe/queryList", "保存数据成功！");
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
					weixinSubscribeManageImpl.deleteById(dbid);
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
		renderMsg("/weixinSubscribe/queryList"+query, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxTemplate() throws Exception {
		HttpServletRequest request = this.getRequest();
		String msgType = request.getParameter("msgType");
		try{
			if(msgType.equals("text")){
				List<WeixinTexttemplate> weixinTexttemplates = weixinTexttemplateManageImpl.getAll();
				StringBuffer buffer=new StringBuffer();
				if(null!=weixinTexttemplates&&weixinTexttemplates.size()>0){
					for (WeixinTexttemplate weixinTexttemplate : weixinTexttemplates) {
						buffer.append("<option value='"+weixinTexttemplate.getDbid()+"'>"+weixinTexttemplate.getTemplatename()+"</option>");
					}
					renderText(buffer.toString());
				}else{
					buffer.append("<option value=''>无数据</option>");
					renderText(buffer.toString());
				}
			}
			if(msgType.equals("news")){
				List<WeixinNewstemplate> weixinTexttemplates = weixinNewstemplateManageImpl.getAll();
				StringBuffer buffer=new StringBuffer();
				if(null!=weixinTexttemplates&&weixinTexttemplates.size()>0){
					for (WeixinNewstemplate weixinTexttemplate : weixinTexttemplates) {
						buffer.append("<option value='"+weixinTexttemplate.getDbid()+"'>"+weixinTexttemplate.getTemplatename()+"</option>");
					}
					renderText(buffer.toString());
				}else{
					buffer.append("<option value=''>无数据</option>");
					renderText(buffer.toString());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderText("error");
			return ;
		}
		return ;
	}
}
