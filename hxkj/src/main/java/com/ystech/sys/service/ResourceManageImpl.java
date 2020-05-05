package com.ystech.sys.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.sys.model.Resource;
@Component("resourceManageImpl")
public class ResourceManageImpl extends HibernateEntityDao<Resource>{
	@SuppressWarnings("unchecked")
	public List<Resource> queryResourceByUserId(Integer userId) {
		String sql="SELECT *,count(res.dbid) FROM sys_resource res,"
				+ "("
				+ "	SELECT "
				+ "	resourceId "
				+ "	from "
				+ "	sys_roleresource ror,"
				+ "	("
				+ "		SELECT "
				+ "		roleId "
				+ "		FROM "
				+ "		sys_userroles "
				+ "		where "
				+ "		sys_userroles.userId="+userId
				+ "	)B "
				+ "	where "
				+ "	ror.roleId=B.roleId"
				+ ")C "
				+ "WHERE "
				+ "res.dbid=C.resourceId and menu=0 and parentId!=0 group by res.dbid  ORDER BY orderNo ";
		List<Resource> resources = executeSql(sql, new Object[]{});
		return resources;
	}
	public List<Resource> queryResourceByUserId(Integer userId,Integer parentId,Integer menu) {
		String sql="SELECT *,count(re.dbid) FROM sys_resource re,"
				+ "( "
					+ "SELECT "
					+ "resourceId "
					+ "from "
					+ "sys_roleresource ros,"
					+ " ("
						+ "SELECT roleId FROM sys_userroles where sys_userroles.userId="+userId+""
					+ ")B"
				+ " WHERE  "
				+ "ros.roleId=B.roleId"
				+ ")C "
				+ "WHERE re.dbid=C.resourceId AND re.parentId="+parentId+" and re.menu="+menu+" group by re.dbid  ORDER BY orderNo;";
		List<Resource> resources = executeSql(sql, new Object[]{});
		return resources;
	}
}
