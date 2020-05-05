/**
 * 
 */
package com.ystech.sys.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Area;
import com.ystech.sys.service.AreaManageImpl;

/**
 * @author shusanzhan
 * @date 2014-1-17
 */
@Component("areaAction")
@Scope("prototype")
public class AreaAction extends BaseController{
	private Area area;
	private AreaManageImpl areaManageImpl;
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	@Resource
	public void setAreaManageImpl(AreaManageImpl areaManageImpl) {
		this.areaManageImpl = areaManageImpl;
	}
	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		if(parentId>0){
			List<Area> areas = areaManageImpl.findBy("area.dbid",parentId);
			request.setAttribute("areas", areas);
			request.setAttribute("parent", areaManageImpl.get(parentId));
		}else{
			List<Area> areas = areaManageImpl.find("from Area where area.dbid IS NULL", new Object[]{});
			request.setAttribute("areas", areas);
			request.setAttribute("parent", null);
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
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		if(parentId>0){
			Area area2 = areaManageImpl.get(parentId);
			request.setAttribute("parent", area2);
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
		if(dbid>0){
			Area area2 = areaManageImpl.get(dbid);
			request.setAttribute("area", area2);
			if(null!=area2.getArea()&&area2.getDbid()>0){
				request.setAttribute("parent", area2.getArea());
			}
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
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		try{
			if(parentId>-1){
				Area area2 = areaManageImpl.get(parentId);
				area.setArea(area2);
				area.setFullName(area2.getFullName()+area.getName());
				area.setTreePath(area2.getTreePath()+area2.getDbid()+",");
			}else{
				area.setFullName(area.getName());
				area.setTreePath(",");
				area.setArea(null);
			}
			
			Integer dbid = area.getDbid();
			if(dbid==null||dbid<=0){
				area.setCreateDate(new Date());
				area.setModifyDate(new Date());
				areaManageImpl.save(area);
			}else{
				area.setModifyDate(new Date());
			}
			areaManageImpl.save(area);
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/area/queryList", "保存数据成功！");
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
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		Integer parentId=ParamUtil.getIntParam(request, "parentId", -1);
		if(null!=dbid&&dbid>0){
			try {
				areaManageImpl.deleteById(dbid);
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
		renderMsg("/area/queryList"+query, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxArea() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		try{
			if(parentId>0){
				List<Area> areas = areaManageImpl.findBy("area.dbid",parentId);
				StringBuffer buffer=new StringBuffer();
				if(null!=areas&&areas.size()>0){
					buffer.append("<select id='ar' name='ar' class='midea text' onchange='ajaxArea(this)'>");
					buffer.append("<option>请选择...</option>");
					for (Area area : areas) {
						buffer.append("<option value='"+area.getDbid()+"'>"+area.getName()+"</option>");
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
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxAreaCustomerFile() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		try{
			if(parentId>0){
				List<Area> areas = areaManageImpl.findBy("area.dbid",parentId);
				StringBuffer buffer=new StringBuffer();
				if(null!=areas&&areas.size()>0){
					buffer.append("<select id='ar' name='ar' class='midea text' onchange='ajaxArea(this)' checkType='integer,1' tip='请选择'>");
					buffer.append("<option>请选择...</option>");
					for (Area area : areas) {
						buffer.append("<option value='"+area.getDbid()+"'>"+area.getName()+"</option>");
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
	/**
	 * 功能描述：经销商
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxAreaDistributor() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		String areaLabel = request.getParameter("areaLabel");
		String companyAreaId = request.getParameter("companyAreaId");
		String areaId = request.getParameter("areaId");
		try{
			if(parentId>0){
				List<Area> areas = areaManageImpl.findBy("area.dbid",parentId);
				StringBuffer buffer=new StringBuffer();
				if(null!=areas&&areas.size()>0){
					buffer.append("<select id='ar' name='ar' class='small text' checkType='integer,1' tip='请选择' onchange=\"ajaxArea('"+areaLabel+"',this,'"+areaId+"')\">");
					buffer.append("<option>请选择...</option>");
					for (Area area : areas) {
						buffer.append("<option value='"+area.getDbid()+"'>"+area.getName()+"</option>");
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
