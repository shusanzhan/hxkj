package com.ystech.mem.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.util.DateUtil;
import com.ystech.mem.model.UseCarArea;

@Component("useCarAreaManageImpl")
public class UseCarAreaManageImpl extends HibernateEntityDao<UseCarArea>{
	public String getUseCarArea(UseCarArea curent) {
		List<UseCarArea> useCarAreas=find("from UseCarArea where parent.dbid IS NULL order by num", new Object[]{});
		String select="";
		if (null!=useCarAreas&&useCarAreas.size()>0) {
			for (UseCarArea useCarArea : useCarAreas) {
				String lest = getListSelect(useCarArea, "&nbsp;&nbsp;&nbsp;&nbsp;",curent);
				select=select+lest;
			}
		}
		return select;
	}
	/**
	 * 功能描述：生成下来选择数据分类，以树状展示
	 * @param productCategory
	 * @param indent
	 * @return
	 */
	public String getListSelect(UseCarArea useCarArea,String indent,UseCarArea curent){
		StringBuilder sb = new StringBuilder();
		if (null!=useCarArea) {
			sb.append("<option value='"+useCarArea.getDbid()+"'");
			if((null!=curent&&curent.getDbid()>0)&&useCarArea.getDbid()==curent.getDbid()){
				sb.append("selected='selected'>"+useCarArea.getName()+"</option>");
			}else{
				sb.append(">"+useCarArea.getName()+"</option>");
			}
			List<UseCarArea> children = find("from UseCarArea where  parent.dbid=? order by num",useCarArea.getDbid());
			if (null!=children&&children.size()>0) {
				for (UseCarArea customerIn : children) {
					sb.append("<option value='"+customerIn.getDbid()+"'");
					if((null!=curent&&curent.getDbid()>0)&&customerIn.getDbid()==curent.getDbid()){
						sb.append("selected='selected'>"+indent+""+customerIn.getName()+"</option>");
					}else{
						sb.append(">"+indent+""+customerIn.getName()+"</option>");
					}
					List<UseCarArea> findBy = find("from UseCarArea where  parent.dbid=? order by num",customerIn.getDbid());
					if (null!=findBy&&findBy.size()>0.) {
						sb.append(getListSelect(customerIn, indent+"&nbsp;&nbsp;&nbsp;&nbsp;",curent));
					}
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 功能描述：生成下来选择数据分类，以树状展示
	 * @param productCategory
	 * @param indent
	 * @return
	 */
	public String getTableTr(UseCarArea useCarArea,String indent){
		StringBuilder sb = new StringBuilder();
		if (null!=useCarArea) {
			List<UseCarArea> children = find("from UseCarArea where  parent.dbid=? order by num",useCarArea.getDbid());
			if (null!=children&&children.size()>0) {
				for (UseCarArea useCar : children) {
					sb.append("<tr>");
					sb.append("<td style=\"text-align: left;\">"+indent+""+useCar.getName() +"</td>");
					sb.append("<td style=\"text-align: left;\">"+useCar.getNote()+"</td>");
					sb.append("<td>"+DateUtil.format(useCar.getCreateDate())+"</td>");
					sb.append("<td>"+useCar.getNum()+"</td>");
					sb.append("<td style=\"text-align: center;\">");
					sb.append("<a href='javascript:void(-1)'  class='aedit' onclick=\"window.location.href='${ctx}/useCarArea/edit?dbid="+useCar.getDbid()+"&parentMenu=1'\">编辑</a> |"); 
					sb.append("<a href='javascript:void(-1)'  class='aedit' onclick=\"$.utile.deleteById('${ctx}/useCarArea/delete?dbid="+useCar.getDbid()+"','searchPageForm')\" title='删除'>删除</a></td>");
					sb.append("</tr>");
					List<UseCarArea> findBy = find("from CustomerInfrom where  parent.dbid=? order by num",useCar.getDbid());
					if (null!=findBy&&findBy.size()>0.) {
						sb.append(getTableTr(useCar, indent+"&nbsp;&nbsp;&nbsp;&nbsp;"));
					}
				}
			}
		}
		return sb.toString();
	}
	public List<UseCarArea> queryUseCarAreas() {
		try {
			String sql="select * from mem_usecararea where 1=1  order by num  ";
			List<UseCarArea> lists = executeSql(sql, null);
			return lists;
		} catch (Exception e) {
		}
		return null;
	}
}
