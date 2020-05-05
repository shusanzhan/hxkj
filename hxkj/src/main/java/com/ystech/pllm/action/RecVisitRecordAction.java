package com.ystech.pllm.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.util.DateUtil;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.pllm.model.RecVisitRecord;
import com.ystech.pllm.service.RecVisitRecordManageImpl;

@Component("recVisitRecordAction")
@Scope("prototype")
public class RecVisitRecordAction extends BaseController{
	private RecVisitRecordManageImpl recVisitRecordManageImpl;
	@Resource
	public void setRecVisitRecordManageImpl(RecVisitRecordManageImpl recVisitRecordManageImpl) {
		this.recVisitRecordManageImpl = recVisitRecordManageImpl;
	} 
	/**
	 * 功能描述：
	 * 参数描述： 
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		try {
			
			String sql="select * from pllm_rec_visitrecord where 1=1 ";
			List params=new ArrayList();
			if(null!=startTime&&startTime.trim().length()>0){
				sql=sql+" and startTime>= ? ";
				params.add(DateUtil.string2Date(startTime));
			}
			if(null!=endTime&&endTime.trim().length()>0){
				sql=sql+" and startTime< ? ";
				params.add(DateUtil.nextDay(endTime));
			}
			sql=sql+" order by startTime DESC";
			Page<RecVisitRecord> page=recVisitRecordManageImpl.pagedQuerySql(pageNo, pageSize, RecVisitRecord.class, sql, params.toArray());
			request.setAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "list";
	}
	
}
