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
import com.ystech.pllm.model.RecPageRecord;
import com.ystech.pllm.service.RecPageRecordManageImpl;

@Component("recPageRecordAction")
@Scope("prototype")
public class RecPageRecordAction extends BaseController{
	private RecPageRecordManageImpl recPageRecordManageImpl;
	@Resource
	public void setRecPageRecordManageImpl(RecPageRecordManageImpl recPageRecordManageImpl) {
		this.recPageRecordManageImpl = recPageRecordManageImpl;
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
			
			String sql="select * from pllm_rec_pagerecord where 1=1 ";
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
			Page<RecPageRecord> page=recPageRecordManageImpl.pagedQuerySql(pageNo, pageSize, RecPageRecord.class, sql, params.toArray());
			request.setAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "list";
	}
	
}
