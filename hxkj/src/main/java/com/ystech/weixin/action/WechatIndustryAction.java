package com.ystech.weixin.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.weixin.model.WechatIndustry;
import com.ystech.weixin.service.WechatIndustryManageImpl;

@Component("wechatIndustryAction")
@Scope("prototype")
public class WechatIndustryAction extends BaseController{
	private WechatIndustryManageImpl industryManageImpl;
	private WechatIndustry industry;
	
	public WechatIndustry getIndustry() {
		return industry;
	}
	public void setIndustry(WechatIndustry industry) {
		this.industry = industry;
	}
	@Resource
	public void setIndustryManageImpl(WechatIndustryManageImpl industryManageImpl) {
		this.industryManageImpl = industryManageImpl;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		try{
			List<WechatIndustry> industries = industryManageImpl.executeSql("select * from weixin_Industry where parentId IS NULL order by num", new Object[]{});
			request.setAttribute("industries", industries);
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
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
	public String add() throws Exception {
		HttpServletRequest request = this.getRequest();
		try{
			String cusString = industryManageImpl.getIndustry(null);
			request.setAttribute("cusString", cusString);
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return "edit";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try{
			if(dbid>0){
				WechatIndustry industry2 = industryManageImpl.get(dbid);
				WechatIndustry parent = industry2.getParent();
				String cusString = industryManageImpl.getIndustry(parent);
				request.setAttribute("cusString", cusString);
				request.setAttribute("industry", industry2);
			}else{
				String cusString = industryManageImpl.getIndustry(null);
				request.setAttribute("cusString", cusString);
			}
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return "edit";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void save() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parendId = ParamUtil.getIntParam(request, "parentId", -1);
		if(parendId>-1){
			try{
				WechatIndustry parent=null;
				if(parendId>0){
					parent = industryManageImpl.get(parendId);
					industry.setParent(parent);
				}
				//第一添加数据 保存
				if(null==industry.getDbid()||industry.getDbid()<=0)
				{
					industry.setModifyDate(new Date());
					industry.setCreateDate(new Date());
					industryManageImpl.save(industry);
				}else{
					//修改时保存数据
					WechatIndustry industry2 = industryManageImpl.get(industry.getDbid());
					industry2.setModifyDate(new Date());
					industry2.setName(industry.getName());
					industry2.setNum(industry.getNum());
					industry2.setParent(industry.getParent());
					industry2.setNote(industry.getNote());
					industry2.setCode(industry.getCode());
					industryManageImpl.save(industry2);
				}
			}catch (Exception e) {
				log.error(e);
				e.printStackTrace();
				renderErrorMsg(e, "");
				return ;
			}
		}else{
			renderErrorMsg(new Throwable("系统异常！"), "");
			return;
		}
		renderMsg("/industry/queryList", "保存数据成功！");
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
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(null!=dbid&&dbid>0){
			try {
					List<WechatIndustry> childs = industryManageImpl.findBy("parent.dbid", dbid);
					if(null!=childs&&childs.size()>0){
						renderErrorMsg(new Throwable("该数据有子级分类，请先删除子级分类在删除数据！"), "");
						return ;
					}else{
						industryManageImpl.deleteById(dbid);
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
		renderMsg("/industry/queryList"+query, "删除数据成功！");
		return;
	}
	/**
		 * 功能描述：
		 * 参数描述：
		 * 逻辑描述：
		 * @return
		 * @throws Exception
		 */
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxIndustry() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		try{
			if(parentId>0){
				List<WechatIndustry> industrys = industryManageImpl.findBy("parent.dbid",parentId);
				StringBuffer buffer=new StringBuffer();
				if(null!=industrys&&industrys.size()>0){
					buffer.append("<select id='ar' name='ar' class='small text' onchange='ajaxIndustry(this)' checkType='integer,1' tip='请选择'>");
					buffer.append("<option>请选择...</option>");
					for (WechatIndustry industry : industrys) {
						buffer.append("<option value='"+industry.getDbid()+"'>"+industry.getName()+"</option>");
					}
					buffer.append("</select> ");
					renderText(buffer.toString());
				}else{
					renderText("error");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error(e);
			renderText("error");
			return ;
		}
		return ;
	}
}
