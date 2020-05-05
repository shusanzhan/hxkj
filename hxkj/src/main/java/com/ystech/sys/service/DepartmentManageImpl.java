/**
 * 
 */
package com.ystech.sys.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.sys.model.Department;
import com.ystech.sys.model.Enterprise;

/**
 * @author shusanzhan
 * @date 2013-5-23
 */
@Component("departmentManageImpl")
public class DepartmentManageImpl extends HibernateEntityDao<Department>{
	private EnterpriseManageImpl enterpriseManageImpl;
	@Resource
	public void setEnterpriseManageImpl(EnterpriseManageImpl enterpriseManageImpl) {
		this.enterpriseManageImpl = enterpriseManageImpl;
	}
	/**
	 * 功能描述：
	 * @param enterprise
	 */
	public void createDepartmentByEnterprise(Enterprise enterprise){
		Department department=new Department();
		department.setBussiType(Department.BUSSITYPEADMIN);
		department.setDiscription("");
		department.setEnterprise(enterprise);
		department.setName(enterprise.getName());
		department.setParentId(Department.ROOT);
		department.setPhone(department.getFax());
		department.setType(Department.TYPEBRANCH);
		save(department);
	}
	/**
	 * @return
	 */
	public String getDepartmentSelect(Department depart,Integer enterpriseId) {
		List<Department> departments = find("from Department where enterpriseId=? order by suqNo",new Object[]{enterpriseId});
		List<Department> departmentsTrees = buidTree(departments,Department.ROOT);
		String listDep = getListDep(departmentsTrees,"|-",depart);
		return listDep;
	}
	public List<Department> buidTree(List<Department> departments,int parentId){
		List<Department> tempDeparment=new ArrayList<Department>();
		for (Department department : departments) {
			if(department.getParentId()==parentId){
				department.setChildren(buidTree(departments,department.getDbid()));
				tempDeparment.add(department);
			}else{
				continue;
			}
		}
		return tempDeparment;
	}
	public String getListDep(List<Department> departmentsTrees,String indent,Department choceDep){
		try{
			StringBuilder sb = new StringBuilder();
			for (Department department : departmentsTrees) {
				if(null!=choceDep){
					if((int)choceDep.getDbid()==(int)department.getDbid()){
						sb.append("<option value='"+department.getDbid()+"' selected=\"selected\">"+indent+department.getName()+"</option>");
					}else{
						sb.append("<option value='"+department.getDbid()+"'>"+indent+department.getName()+"</option>");
					}
				}else{
					sb.append("<option value='"+department.getDbid()+"'>"+indent+department.getName()+"</option>");
				}
				List<Department> children = department.getChildren();
				if(!children.isEmpty()){
					String listDep = getListDep(children, indent+"-", choceDep);
					sb.append(listDep);
				}
			}
			return sb.toString();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public String getDepartmentIds(Department department) {
		StringBuilder dbids = new StringBuilder("");
		if(null!=department){
			dbids=dbids.append(department.getDbid()+",");
			List<Department> children =findBy("parentId",department.getDbid());
			if(null!=children&&children.size()>0){
				for (Department department2 : children) {
					dbids=dbids.append(department2.getDbid()+",");
					List<Department> findBy = findBy("parentId",department2.getDbid());
					if (null!=findBy&&findBy.size()>0) {
						dbids.append(getDepartmentIds(department2)+",");
					}
				}
			}else{
				
			}
			String string = dbids.toString();
			string=string.substring(0, string.length()-1);
			return string;
		}else{
			return null;
		}
	}
	/**
	 * 通过
	 * @param dbid
	 * @return
	 */
	public String getDepartmentIdsByDbid(Integer dbid) {
		if(dbid>0){
			Department department = get(dbid);
			String departmentIds = getDepartmentIds(department);
			return departmentIds;
		}else{
			return null;
		}
	}
	/**
	 *功能描述： 将传入的对象转化为JSON数据格式
	 * 部门树：根节点，具有子结构节点，子节点
	 * @throws JSONException
	 */
	public JSONObject makeJSONSuperObject(Enterprise enterprise){
		JSONObject jObject = new JSONObject();
		jObject.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/1_open.png");// 根节点
		jObject.put("root", "root");
		jObject.put("id", 0);
		jObject.put("name","根节点");
		jObject.put("open", true);
		List<Department> departments = queryByEnterpriseId(enterprise);
		jObject.put("children",makeJSONObject(departments, 0));
		return jObject;
	}
	/**
	 *功能描述： 将传入的对象转化为JSON数据格式
	 * 部门树：根节点，具有子结构节点，子节点
	 * @throws JSONException
	 */
	private JSONArray makeJSONObject(List<Department> departments,int rootId){
		JSONArray array=new JSONArray();
		for (Department department : departments) {
			if(department.getParentId()==rootId){
				JSONObject jObject = new JSONObject();
				jObject.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/2.png");// 菜单阶段
				jObject.put("id", department.getDbid());
				jObject.put("name", department.getName());
				jObject.put("open", true);
				jObject.put("children", makeJSONObject(departments, department.getDbid()));
				array.put(jObject);
			}else{
				continue;
			}
		}
		return array;
	}
	private List<Department> queryByEnterpriseId(Enterprise enterprise){
		String sql="select * from  sys_Department where 1=1 ";
		if(enterprise!=null&&enterprise.getDbid()>0){
			sql=sql+"  AND enterpriseId="+enterprise.getDbid();
		}
		sql=sql+" order by suqNo ";
		List<Department> departments = executeSql(sql, null);
		return departments;
	}
	
}
