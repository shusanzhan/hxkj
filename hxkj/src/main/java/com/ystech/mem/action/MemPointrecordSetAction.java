package com.ystech.mem.action;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.mem.model.MemPointrecordSet;
import com.ystech.mem.service.MemPointrecordSetManageImpl;
import com.ystech.sys.model.Enterprise;

@Component("memPointrecordSetAction")
@Scope("prototype")
public class MemPointrecordSetAction extends BaseController{
	private MemPointrecordSet memPointrecordSet;
	
	private MemPointrecordSetManageImpl memPointrecordSetManageImpl;
	
	public MemPointrecordSet getMemPointrecordSet() {
		return memPointrecordSet;
	}
	public void setMemPointrecordSet(MemPointrecordSet memPointrecordSet) {
		this.memPointrecordSet = memPointrecordSet;
	}
	@Resource
	public void setMemPointrecordSetManageImpl(
			MemPointrecordSetManageImpl memPointrecordSetManageImpl) {
		this.memPointrecordSetManageImpl = memPointrecordSetManageImpl;
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
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			MemPointrecordSet memPointrecordSet = memPointrecordSetManageImpl.findUniqueBy("enterpriseId", enterprise.getDbid());
			request.setAttribute("memPointrecordSet", memPointrecordSet);
			request.setAttribute("enterprise", enterprise);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
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
		Integer rigPointStatus = ParamUtil.getIntParam(request, "rigPointStatus", 2);
		Integer rigParentStatus = ParamUtil.getIntParam(request, "rigParentStatus", 2);
		Integer agentSuccessStatus = ParamUtil.getIntParam(request, "agentSuccessStatus", 2);
		Integer agentParentSuccessStatus = ParamUtil.getIntParam(request, "agentParentSuccessStatus", 2);
		Integer customerSuccessStatus = ParamUtil.getIntParam(request, "customerSuccessStatus", 2);
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			Integer dbid = memPointrecordSet.getDbid();
			memPointrecordSet.setRigPointStatus(rigPointStatus);
			memPointrecordSet.setRigParentStatus(rigParentStatus);
			memPointrecordSet.setAgentParentSuccessStatus(agentParentSuccessStatus);
			memPointrecordSet.setAgentSuccessStatus(agentSuccessStatus);
			memPointrecordSet.setCustomerSuccessStatus(customerSuccessStatus);
			if(null==dbid){
				memPointrecordSet.setCreateTime(new Date());
				memPointrecordSet.setModifyTime(new Date());
				memPointrecordSet.setEnterpriseId(enterprise.getDbid());
			}else{
				memPointrecordSet.setModifyTime(new Date());
				memPointrecordSet.setEnterpriseId(enterprise.getDbid());
			}
			memPointrecordSetManageImpl.save(memPointrecordSet);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/memPointrecordSet/edit", "保存数据成功");
		return;
	}
}
