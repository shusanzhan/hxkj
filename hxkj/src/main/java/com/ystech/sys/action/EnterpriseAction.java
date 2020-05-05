/**
 * 
 */
package com.ystech.sys.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.CommState;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.User;
import com.ystech.sys.service.DepartmentManageImpl;
import com.ystech.sys.service.EnterpriseManageImpl;
import com.ystech.sys.service.UserManageImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author shusanzhan
 * @date 2013-6-2
 */
@Component("enterpriseAction")
@Scope("prototype")
public class EnterpriseAction extends BaseController{
	private DepartmentManageImpl departmentManageImpl;
	private Enterprise enterprise;
	private UserManageImpl userManageImpl;
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	private EnterpriseManageImpl enterpriseManageImpl;

	@Resource
	public void setEnterpriseManageImpl(EnterpriseManageImpl enterpriseManageImpl) {
		this.enterpriseManageImpl = enterpriseManageImpl;
	}
	@Resource
	public void setDepartmentManageImpl(DepartmentManageImpl departmentManageImpl) {
		this.departmentManageImpl = departmentManageImpl;
	}
	@Resource
	public void setUserManageImpl(UserManageImpl userManageImpl) {
		this.userManageImpl = userManageImpl;
	}
	/**
	 * 功能描述：查询分公司列表
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String queryList() {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		Integer bussiType = ParamUtil.getIntParam(request, "bussiType", -1);
		String name = request.getParameter("name");
		try {
			String sql="select * from sys_Enterprise where 1=1 ";
			List params=new ArrayList();
			if(bussiType>0){
				sql=sql+" AND bussiType=? ";
				params.add(bussiType);
			}
			if(name!=null&&name.trim().length()>0){
				sql=sql+" AND name like ? ";
				params.add("%"+name+"%");
			}
			sql=sql+" order by createTime DESC ";
			Page<Enterprise> page= enterpriseManageImpl.pagedQuerySql(pageNo, pageSize,Enterprise.class,sql,params.toArray());
			request.setAttribute("page", page);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "list";
	}
	/**
	 * 功能描述：编辑区域和经销商信息
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String edit() {
		HttpServletRequest request = getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try{
			
			Enterprise enterprise2 = enterpriseManageImpl.get(dbid);
			request.setAttribute("enterprise", enterprise2);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "edit";
	}
	/**
	 * 功能描述：分店自己管理自己的基础信息
	 * @return
	 * @throws Exception
	 */
	public String enterprise() {
		HttpServletRequest request = getRequest();
		User currentUser = SecurityUserHolder.getCurrentUser();
		if(null!=currentUser){
			List<Enterprise> companies = enterpriseManageImpl.findBy("userId", currentUser.getDbid());
			if(null!=companies&&companies.size()>0){
				Enterprise enterprise2 = companies.get(0);
				request.setAttribute("enterprise", enterprise2);
			}
		}
		return "enterprise";
	}
	/**
	 * 功能描述：管理员管理分店基础信息
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void save(){
		HttpServletRequest request = this.getRequest();
		try {
			if(enterprise.getDbid()==null){
				enterprise.setCreateTime(new Date());
				enterprise.setModifyTime(new Date());
				enterprise.setBeginDate(new Date());
				enterprise.setBackNetStatus(CommState.STATE_NORMAL);
				enterpriseManageImpl.save(enterprise);
				departmentManageImpl.createDepartmentByEnterprise(enterprise);
			}else{
				//更新基础分店基础数据
				Enterprise enterprise2 = enterpriseManageImpl.get(enterprise.getDbid());
				enterprise2.setAccount(enterprise.getAccount());
				enterprise2.setAddress(enterprise.getAddress());
				enterprise2.setBank(enterprise.getBank());
				enterprise2.setContent(enterprise.getContent());
				enterprise2.setEmail(enterprise.getEmail());
				enterprise2.setFax(enterprise.getFax());
				enterprise2.setName(enterprise.getName());
				enterprise2.setPhone(enterprise.getPhone());
				enterprise2.setWebAddress(enterprise.getWebAddress());
				enterprise2.setZipCode(enterprise.getZipCode());
				enterprise2.setAllName(enterprise.getAllName());
				enterprise2.setOldCarPhone(enterprise.getOldCarPhone());
				enterprise2.setMaintCarPhone(enterprise.getMaintCarPhone());
				enterprise2.setSalerPhone(enterprise.getSalerPhone());
				enterprise2.setEmergencyPhone(enterprise.getEmergencyPhone());
				enterprise2.setEaxminPhone(enterprise.getEaxminPhone());
				enterprise2.setTryCarPhone(enterprise.getTryCarPhone());
				enterprise2.setBussiType(enterprise.getBussiType());
				enterprise2.setPoint(enterprise.getPoint());
				enterprise2.setModifyTime(new Date());
				enterprise2.setEndDate(enterprise.getEndDate());
				enterpriseManageImpl.save(enterprise2);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/enterprise/queryList", "保存数据成功！");
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String backNetCompany() {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			Enterprise enterprise2 = enterpriseManageImpl.get(dbid);
			request.setAttribute("enterprise", enterprise2);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "backNetCompany";
	}
	/**
	 * 功能描述：保存退网数据
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveBackNetCompany() {
		HttpServletRequest request = this.getRequest();
		Integer enterpriseId = ParamUtil.getIntParam(request, "enterpriseId", -1);
		Integer nextEnterpriseId = ParamUtil.getIntParam(request, "nextEnterpriseId", -1);
		String content = request.getParameter("content");
		try {
			Enterprise enterprise2 = enterpriseManageImpl.get(enterpriseId);
			enterprise2.setBackNetDate(new Date());
			enterprise2.setBackNetStatus(CommState.ENTER_STATE_BACKNET);
			enterprise2.setContent(content);
			//转单客户
			Enterprise nextCompany = enterpriseManageImpl.get(nextEnterpriseId);
			if(nextCompany==null){
				renderErrorMsg(new Throwable("保存失败，接受退网经销商不存在请确认"), "");
				return ;
			}
			
			//停用账号
			userManageImpl.updateStopUser(enterprise2);
			
			//enterpriseManageImpl.updateTurnCustomerToCompnay(enterprise2,nextCompany);
			
			enterpriseManageImpl.save(enterprise2);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/enterprise/queryList", "退网成功");
		return;
	}
	/**
	 * 功能描述：启用或停用经销商
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void stopOrStart(){
		HttpServletRequest request = getRequest();
		Integer companyId = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			Enterprise enterprise2 = enterpriseManageImpl.get(companyId);
			Integer backNetStatus = enterprise2.getBackNetStatus();
			//启用经销商
			if(backNetStatus==CommState.STATE_STOP){
				enterprise2.setBackNetStatus(CommState.STATE_NORMAL);
				enterprise2.setBackNetDate(new Date());
				//启用账号
				userManageImpl.updateStartUser(enterprise2);
				enterpriseManageImpl.save(enterprise2);
				renderMsg("/enterprise/queryList", "启用经销商成功");
			}
			//停用经销商
			if(backNetStatus==CommState.STATE_NORMAL){
				enterprise2.setBackNetStatus(CommState.STATE_STOP);
				enterprise2.setBackNetDate(new Date());
				//停用账号
				userManageImpl.updateStopUser(enterprise2);
				enterpriseManageImpl.save(enterprise2);
				renderMsg("/enterprise/queryList", "停用经销商成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		return;
	}
	/**
	 * 功能描述：分店维基础信息保存
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveEnterprise() {
		try {
			User currentUser = SecurityUserHolder.getCurrentUser();
			//更新基础分店基础数据
			Enterprise enterprise2 = enterpriseManageImpl.get(enterprise.getDbid());
			enterprise2.setAccount(enterprise.getAccount());
			enterprise2.setAddress(enterprise.getAddress());
			enterprise2.setBank(enterprise.getBank());
			enterprise2.setContent(enterprise.getContent());
			enterprise2.setEmail(enterprise.getEmail());
			enterprise2.setFax(enterprise.getFax());
			enterprise2.setName(enterprise.getName());
			enterprise2.setPhone(enterprise.getPhone());
			enterprise2.setWebAddress(enterprise.getWebAddress());
			enterprise2.setZipCode(enterprise.getZipCode());
			enterprise2.setOldCarPhone(enterprise.getOldCarPhone());
			enterprise2.setMaintCarPhone(enterprise.getMaintCarPhone());
			enterprise2.setSalerPhone(enterprise.getSalerPhone());
			enterprise2.setEmergencyPhone(enterprise.getEmergencyPhone());
			enterprise2.setEaxminPhone(enterprise.getEaxminPhone());
			enterprise2.setTryCarPhone(enterprise.getTryCarPhone());
			enterprise.setUserId(currentUser.getDbid());
			enterpriseManageImpl.save(enterprise2);
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/enterprise/enterprise", "保存数据成功！");
	}
	/**
	 * 功能描述：删除分店新
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void delete() {
		HttpServletRequest request = this.getRequest();
		String dbids = request.getParameter("dbids");
		int contNum=0;
		try {
			if(null!=dbids&&dbids.trim().length()>0){
				try {
					contNum = enterpriseManageImpl.deleteByIds(dbids);
				} catch (Exception e) {
					e.printStackTrace();
					renderErrorMsg(e, "");
					return ;
				}
			}else{
				renderErrorMsg(new Throwable("未选择操作数据！"), "");
				return ;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		String query = ParamUtil.getQueryUrl(request);
		renderMsg("/enterprise/queryList"+query, "成功删除数据【"+contNum+"】！");

		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void autoCompany(){
		HttpServletRequest request = this.getRequest();
		String pingyin = request.getParameter("q");
		try {
			String sql="select * from sys_enterprise where name like '%"+pingyin+"%'  ";
			List<Enterprise> companies = enterpriseManageImpl.executeSql(sql,null);
			JSONArray  array=new JSONArray();
			if(null!=companies){
				for (Enterprise company : companies) {
					JSONObject object=new JSONObject();
					object.put("name", company.getName());
					object.put("companyId", company.getDbid());
					object.put("dbid", company.getDbid());
					array.add(object);
				}
			}
			renderJson(array.toString());
			return ;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			return ;
		}
	}
}
