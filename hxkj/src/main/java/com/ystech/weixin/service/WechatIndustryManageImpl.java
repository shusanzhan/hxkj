package com.ystech.weixin.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.core.util.DateUtil;
import com.ystech.weixin.model.WechatIndustry;

@Component("wechatIndustryManageImpl")
public class WechatIndustryManageImpl extends HibernateEntityDao<WechatIndustry>{
	public String getIndustry(WechatIndustry curent) {
		List<WechatIndustry> industrys=find("from WechatIndustry where parent.dbid IS NULL order by num", new Object[]{});
		String select="";
		if (null!=industrys&&industrys.size()>0) {
			for (WechatIndustry industry : industrys) {
				String lest = getListSelect(industry, "&nbsp;&nbsp;&nbsp;&nbsp;",curent);
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
	public String getListSelect(WechatIndustry industry,String indent,WechatIndustry curent){
		StringBuilder sb = new StringBuilder();
		if (null!=industry) {
			sb.append("<option value='"+industry.getDbid()+"'");
			if((null!=curent&&curent.getDbid()>0)&&industry.getDbid()==curent.getDbid()){
				sb.append("selected='selected'>"+industry.getName()+"</option>");
			}else{
				sb.append(">"+industry.getName()+"</option>");
			}
			List<WechatIndustry> children = find("from WechatIndustry where  parent.dbid=? order by num",industry.getDbid());
			if (null!=children&&children.size()>0) {
				for (WechatIndustry customerIn : children) {
					sb.append("<option value='"+customerIn.getDbid()+"'");
					if((null!=curent&&curent.getDbid()>0)&&customerIn.getDbid()==curent.getDbid()){
						sb.append("selected='selected'>"+indent+""+customerIn.getName()+"</option>");
					}else{
						sb.append(">"+indent+""+customerIn.getName()+"</option>");
					}
					List<WechatIndustry> findBy = find("from WechatIndustry where  parent.dbid=? order by num",customerIn.getDbid());
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
	public String getTableTr(WechatIndustry industry,String indent){
		StringBuilder sb = new StringBuilder();
		if (null!=industry) {
			List<WechatIndustry> children = find("from WechatIndustry where  parent.dbid=? order by num",industry.getDbid());
			if (null!=children&&children.size()>0) {
				for (WechatIndustry industry2 : children) {
					sb.append("<tr>");
					sb.append("<td style=\"text-align: left;\">"+indent+""+industry2.getName() +"</td>");
					sb.append("<td style=\"text-align: center;\">"+industry2.getCode()+"</td>");
					sb.append("<td style=\"text-align: left;\">"+industry2.getNote()+"</td>");
					sb.append("<td>"+DateUtil.format(industry2.getCreateDate())+"</td>");
					sb.append("<td>"+industry2.getNum()+"</td>");
					sb.append("<td style=\"text-align: center;\">");
					sb.append("<a href='javascript:void(-1)'  class='aedit' onclick=\"window.location.href='${ctx}/industry/edit?dbid="+industry2.getDbid()+"&parentMenu=1'\">编辑</a> |"); 
					sb.append("<a href='javascript:void(-1)'  class='aedit' onclick=\"$.utile.deleteById('${ctx}/industry/delete?dbid="+industry2.getDbid()+"','searchPageForm')\" title='删除'>删除</a></td>");
					sb.append("</tr>");
					List<WechatIndustry> findBy = find("from WechatIndustry where  parent.dbid=? order by num",industry2.getDbid());
					if (null!=findBy&&findBy.size()>0.) {
						sb.append(getTableTr(industry2, indent+"&nbsp;&nbsp;&nbsp;&nbsp;"));
					}
				}
			}
		}
		return sb.toString();
	}
}
