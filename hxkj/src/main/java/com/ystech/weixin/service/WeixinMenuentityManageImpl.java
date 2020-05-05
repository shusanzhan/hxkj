package com.ystech.weixin.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ystech.core.dao.HibernateEntityDao;
import com.ystech.weixin.model.WeixinMenuentity;
import com.ystech.weixin.model.WeixinMenuentityGroup;

@Component("weixinMenuentityManageImpl")
public class WeixinMenuentityManageImpl extends HibernateEntityDao<WeixinMenuentity>{
	/**
	 * 功能描述：生成下来选择数据分类，以树状展示
	 * @param weixinMenuentity
	 * @param indent
	 * @return
	 */
	public String getListSelect(WeixinMenuentity weixinMenuentity,String indent,WeixinMenuentity curent){
		StringBuilder sb = new StringBuilder();
		if (null!=weixinMenuentity) {
			sb.append("<option value='"+weixinMenuentity.getDbid()+"'");
			if((null!=curent&&curent.getDbid()>0)&&weixinMenuentity.getDbid()==curent.getDbid()){
				sb.append("selected='selected'>"+weixinMenuentity.getName()+"</option>");
			}else{
				sb.append(">"+weixinMenuentity.getName()+"</option>");
			}
			List<WeixinMenuentity> children = find("from WeixinMenuentity where  weixinMenuentity.dbid=?  order by orders",new Object[]{weixinMenuentity.getDbid()});
			if (null!=children&&children.size()>0) {
				for (WeixinMenuentity productCateGorup2 : children) {
					sb.append("<option value='"+productCateGorup2.getDbid()+"'");
					if((null!=curent&&curent.getDbid()>0)&&productCateGorup2.getDbid()==curent.getDbid()){
						sb.append("selected='selected'>"+indent+""+productCateGorup2.getName()+"</option>");
					}else{
						sb.append(">"+indent+""+productCateGorup2.getName()+"</option>");
					}
					List<WeixinMenuentity> findBy = find("from WeixinMenuentity where  weixinMenuentity.dbid=?  order by orders",new Object[]{productCateGorup2.getDbid()});
					if (null!=findBy&&findBy.size()>0.) {
						sb.append(getListSelect(productCateGorup2, indent+"&nbsp;&nbsp;&nbsp;&nbsp;",curent));
					}
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 功能描述：生成下来选择数据分类，以树状展示
	 * @param weixinMenuentity
	 * @param indent
	 * @return
	 */
	public String getTableTr(WeixinMenuentity weixinMenuentity,String indent){
		StringBuilder sb = new StringBuilder();
		if (null!=weixinMenuentity) {
			List<WeixinMenuentity> children = find("from WeixinMenuentity where  weixinMenuentity.dbid=? order by orders",weixinMenuentity.getDbid());
			if (null!=children&&children.size()>0) {
				for (WeixinMenuentity productCateGorup2 : children) {
					sb.append("<tr>");
					sb.append("<td  style=\"text-align: left;\">"+indent+""+productCateGorup2.getName() +"</td>");
					if(null!=productCateGorup2.getType()){
						if(productCateGorup2.getType().equals("click")){
							sb.append("<td>消息类触发</td>");
						}
						if(productCateGorup2.getType().equals("view")){
							sb.append("<td>网页链接类</td>");
						}
					}else{
						sb.append("<td>无</td>");
					}
					
					if(null!=productCateGorup2.getUrl()&&productCateGorup2.getUrl().length()>50){
						sb.append("<td>"+productCateGorup2.getUrl().substring(0,50)+"...</td>");
					}else{
						sb.append("<td>"+productCateGorup2.getUrl()+"</td>");
					}
					WeixinMenuentityGroup weixinMenuentityGroup = productCateGorup2.getWeixinMenuentityGroup();
					sb.append("<td>"+productCateGorup2.getOrders()+"</td>");
					sb.append("<td style=\"text-align: center;\">");
					sb.append("<a href='javascript:void(-1)' onclick=\"$.utile.openDialog('${ctx}/weixinMenuentity/edit?dbid="+productCateGorup2.getDbid()+"&parentMenu=1&groupId="+weixinMenuentityGroup.getDbid()+"','编辑菜单',1024,560)\">编辑</a> |"); 
					sb.append("<a href='javascript:void(-1)' onclick=\"$.utile.deleteById('/weixinMenuentity/delete?dbid="+productCateGorup2.getDbid()+"&groupId="+weixinMenuentityGroup.getDbid()+"','searchPageForm')\" title='删除'>删除</a></td>");
					sb.append("</tr>");
					List<WeixinMenuentity> findBy = find("from WeixinMenuentity where  weixinMenuentity.dbid=? order by orders",productCateGorup2.getDbid());
					if (null!=findBy&&findBy.size()>0) {
						sb.append(getTableTr(productCateGorup2, indent+"&nbsp;&nbsp;&nbsp;&nbsp;"));
					}
				}
			}
		}
		return sb.toString();
	}
	public String getProductCateGorySelect(Integer businessId,WeixinMenuentity curent,Integer groupId) {
		List<WeixinMenuentity> productCategories=find("from WeixinMenuentity where accountid=? and weixinMenuentityGroupId=? and weixinMenuentity.dbid IS NULL ", new Object[]{businessId+"",groupId});
		String select="";
		if (null!=productCategories&&productCategories.size()>0) {
			for (WeixinMenuentity productCategory : productCategories) {
				String lest = getListSelect(productCategory, "&nbsp;&nbsp;&nbsp;&nbsp;",curent);
				select=select+lest;
			}
		}
		return select;
	}
	
	
	public String getTable(WeixinMenuentity productCategory){
		int trLength=0;
		StringBuilder sb = new StringBuilder();
		if(null!=productCategory){
			List<WeixinMenuentity> children = executeSql("select * from WeixinMenuentity where  fatherId=? order by orders limit 6",new Object[]{ productCategory.getDbid()});
			if (null!=children&&children.size()>0) {
				sb.append("<table width='100%' cellpadding=\"0\" cellspacing=\"0\">");
				int size = children.size();
				
				trLength=size/3;
				if(size%3>0){
					trLength=trLength+1;
				}
				int j=0;
				for(int i=0;i<trLength;i++){
					sb.append("<tr>");
					for(j=i*3;j<i*3+3;j++){
						if(j<size){
							WeixinMenuentity productCategory2= children.get(j);
							sb.append("<td onclick=\"window.location.href='/weixinIndex/productList?productCategoryId="+productCategory2.getDbid()+"&menu=1'\" title='"+productCategory2.getName()+"'>"+productCategory2.getName());
							sb.append("</td>");
						}else{
							break;
						}
					}
					sb.append("</tr>");
				}
				sb.append("</table>");
			}
		}
		
		return sb.toString();
	}
	
	
	public String getTableToMBOne(WeixinMenuentity productCategory){
		int trLength=0;
		StringBuilder sb = new StringBuilder();
		if(null!=productCategory){
			List<WeixinMenuentity> children = executeSql("select * from ProductCategory where  fatherId=? order by orders limit 6",new Object[]{ productCategory.getDbid()});
			if (null!=children&&children.size()>0) {
				sb.append("<table width='100%' cellpadding=\"0\" cellspacing=\"0\">");
				int size = children.size();
				
				trLength=size/3;
				if(size%3>0){
					trLength=trLength+1;
				}
				int j=0;
				for(int i=0;i<trLength;i++){
					sb.append("<tr>");
					for(j=i*3;j<i*3+3;j++){
						if(j<size){
							WeixinMenuentity productCategory2= children.get(j);
							sb.append("<td onclick=\"window.location.href='/mBOneIndex/productList?productCategoryId="+productCategory2.getDbid()+"&menu=1'\" title='"+productCategory2.getName()+"'>"+productCategory2.getName());
							sb.append("</td>");
						}else{
							break;
						}
					}
					sb.append("</tr>");
				}
				sb.append("</table>");
			}
		}
		
		return sb.toString();
	}
}
