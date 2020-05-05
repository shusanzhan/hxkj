/**
 * 
 */
package com.ystech.sys.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.OperateLog;
import com.ystech.sys.service.OperateLogManageImpl;

/**
 * @author shusanzhan
 * @date 2013-7-7
 */
@Component("operateLogAction")
@Scope("prototype")
public class OperateLogAction extends BaseController{
	private OperateLogManageImpl operateLogManageImpl;
	@Resource
	public void setOperateLogManageImpl(OperateLogManageImpl operateLogManageImpl) {
		this.operateLogManageImpl = operateLogManageImpl;
	}
	
	/**
	 * 功能描述：列表查询
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		String name = request.getParameter("operator");
		Page<OperateLog> page=null;
		if(null!=name&&name.trim().length()>0){
			 page= operateLogManageImpl.pagedQuerySql(pageNo, pageSize,OperateLog.class,"select * from sys_OperateLog where operator like '%"+name+"%' order by operatedate DESC", new Object[]{});
		}else{
			page= operateLogManageImpl.pagedQuerySql(pageNo, pageSize, OperateLog.class,"select * from sys_OperateLog  order by operatedate DESC", new Object[]{});
		}
		request.setAttribute("page", page);
		return "list";
	}
	/**
	 * 功能描述：删除操作日志信息
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		String dbids = request.getParameter("dbids");
		int contNum=0;
		if(null!=dbids&&dbids.trim().length()>0){
			try {
				contNum = operateLogManageImpl.deleteByIds(dbids);
			} catch (Exception e) {
				e.printStackTrace();
				renderErrorMsg(e, "");
				return ;
			}
		}else{
			renderErrorMsg(new Throwable("未选择操作数据！"), "");
			return ;
		}
		String query = ParamUtil.getQueryUrl(request);
		renderMsg("/operateLog/queryList"+query, "成功删除数据【"+contNum+"】！");
		return;
	}
}
