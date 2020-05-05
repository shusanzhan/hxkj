package com.ystech.weixin.action;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.model.WechatMedia;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinKeyAutoresponse;
import com.ystech.weixin.model.WeixinKeyWord;
import com.ystech.weixin.model.WeixinKeyWordRole;
import com.ystech.weixin.model.WeixinNewstemplate;
import com.ystech.weixin.model.WeixinTexttemplate;
import com.ystech.weixin.service.WechatMediaManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinKeyAutoresponseManageImpl;
import com.ystech.weixin.service.WeixinKeyWordManageImpl;
import com.ystech.weixin.service.WeixinKeyWordRoleManageImpl;
import com.ystech.weixin.service.WeixinNewstemplateManageImpl;
import com.ystech.weixin.service.WeixinTexttemplateManageImpl;

import net.sf.json.JSONObject;

@Component("weixinKeyWordRoleAction")
@Scope("prototype")
public class WeixinKeyWordRoleAction extends BaseController{
	private WeixinKeyWordRole weixinKeyWordRole;
	private WeixinKeyWord weixinKeyWord;
	private WeixinKeyWordManageImpl weixinKeyWordManageImpl;
	private WeixinKeyWordRoleManageImpl weixinKeyWordRoleManageImpl;
	private WeixinKeyAutoresponseManageImpl weixinKeyAutoresponseManageImpl;
	private WeixinTexttemplateManageImpl weixinTexttemplateManageImpl;
	private WeixinNewstemplateManageImpl weixinNewstemplateManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WechatMediaManageImpl wechatMediaManageImpl;
	public void setWeixinKeyWordRole(WeixinKeyWordRole weixinKeyWordRole) {
		this.weixinKeyWordRole = weixinKeyWordRole;
	}
	@Resource
	public void setWechatMediaManageImpl(WechatMediaManageImpl wechatMediaManageImpl) {
		this.wechatMediaManageImpl = wechatMediaManageImpl;
	}
	@Resource
	public void setWeixinKeyWordManageImpl(WeixinKeyWordManageImpl weixinKeyWordManageImpl) {
		this.weixinKeyWordManageImpl = weixinKeyWordManageImpl;
	}
	@Resource
	public void setWeixinKeyWordRoleManageImpl(WeixinKeyWordRoleManageImpl weixinKeyWordRoleManageImpl) {
		this.weixinKeyWordRoleManageImpl = weixinKeyWordRoleManageImpl;
	}
	@Resource
	public void setWeixinKeyAutoresponseManageImpl(WeixinKeyAutoresponseManageImpl weixinKeyAutoresponseManageImpl) {
		this.weixinKeyAutoresponseManageImpl = weixinKeyAutoresponseManageImpl;
	}
	
	public WeixinKeyWordRole getWeixinKeyWordRole() {
		return weixinKeyWordRole;
	}
	@Resource
	public void setWeixinTexttemplateManageImpl(WeixinTexttemplateManageImpl weixinTexttemplateManageImpl) {
		this.weixinTexttemplateManageImpl = weixinTexttemplateManageImpl;
	}
	public WeixinKeyWord getWeixinKeyWord() {
		return weixinKeyWord;
	}
	public void setWeixinKeyWord(WeixinKeyWord weixinKeyWord) {
		this.weixinKeyWord = weixinKeyWord;
	}
	@Resource
	public void setWeixinNewstemplateManageImpl(WeixinNewstemplateManageImpl weixinNewstemplateManageImpl) {
		this.weixinNewstemplateManageImpl = weixinNewstemplateManageImpl;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	/**
	* 功能描述：
	* 参数描述：
	* 逻辑描述：
	* @return
	* @throws Exception
	*/
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 50);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				String sql="select * from weixin_keywordrole where type=1 and accountid="+weixinAccount.getDbid()+" order by createDate DESC";
				Page<WeixinKeyWordRole> page=weixinKeyWordRoleManageImpl.pagedQuerySql(pageNo, pageSize,WeixinKeyWordRole.class,sql, new Object[]{});
				request.setAttribute("page", page);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "list";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String querySubScriptList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 50);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				String sql="select * from weixin_keywordrole where type=3  and accountid="+weixinAccount.getDbid()+" order by createDate DESC";
				Page<WeixinKeyWordRole> page=weixinKeyWordRoleManageImpl.pagedQuerySql(pageNo, pageSize,WeixinKeyWordRole.class,sql, new Object[]{});
				request.setAttribute("page", page);
			}
		} catch (Exception e) {
			
		}
		return "subScriptList";
	}
	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String add() throws Exception {
		return "edit";
	}

	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			if(dbid>0){
				WeixinKeyWordRole weixinKeyWordRole2 = weixinKeyWordRoleManageImpl.get(dbid);
				request.setAttribute("weixinKeyWordRole", weixinKeyWordRole2);
			}
		} catch (Exception e) {
		}
		return "edit";
	}

	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void save() throws Exception {
		try{
			Integer dbid = weixinKeyWordRole.getDbid();
			if(dbid==null||dbid<=0){
				List<WeixinKeyWord> weixinKeyWords = weixinKeyWordManageImpl.findBy("keyword", weixinKeyWordRole.getName());
				if(null!=weixinKeyWords&&weixinKeyWords.size()>0){
					renderErrorMsg(new Throwable("添加失败，关键词重复"), "");
					return ;
				}
				Enterprise enterprise = SecurityUserHolder.getEnterprise();
				List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
				if(null==weixinAccounts||weixinAccounts.size()<=0){
					renderErrorMsg(new Throwable("保存失败,无公众号信息"), "");
					return ;
				}
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				weixinKeyWordRole.setAccountid(weixinAccount.getDbid());
				weixinKeyWordRole.setCreateDate(new Date());
				weixinKeyWordRole.setModifyDate(new Date());
				weixinKeyWordRole.setType(1);
				weixinKeyWordRoleManageImpl.save(weixinKeyWordRole);
				
				//创建默认关键词回复
				WeixinKeyWord weixinKeyWord=new WeixinKeyWord();
				weixinKeyWord.setCreateDate(new Date());
				weixinKeyWord.setModifyDate(new Date());
				weixinKeyWord.setKeyword(weixinKeyWordRole.getName());
				weixinKeyWord.setWeixinKeyWordRole(weixinKeyWordRole);
				//全匹配
				weixinKeyWord.setMatchingType(1);
				weixinKeyWordManageImpl.save(weixinKeyWord);
				
				String html=createHtml(weixinKeyWordRole);
				renderMsg(html, "保存数据成功！");
			}else{
				WeixinKeyWordRole weixinKeyWordRole2 = weixinKeyWordRoleManageImpl.get(dbid);
				weixinKeyWordRole2.setName(weixinKeyWordRole.getName());
				weixinKeyWordRoleManageImpl.save(weixinKeyWordRole2);
				JSONObject text=new JSONObject();
				text.put("name", weixinKeyWordRole2.getName());
				renderMsg(text.toString(), "更新数据成功");
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		return ;
	}

	/**
	 * 功能描述：创建前台二维码操作html
	 * @param spreadDetail
	 * @return
	 */
	private String createHtml(WeixinKeyWordRole weixinKeyWordRole){
		long num = weixinKeyWordRoleManageImpl.countSqlResult("select count(*) from weixin_keywordrole",null);
		StringBuffer buffer=new StringBuffer();
		buffer.append("<div class=\"rule-group\" id=\"rule-group"+weixinKeyWordRole.getDbid()+"\">");
			buffer.append("<div class=\"rule-meta\">");
				buffer.append(" <h3 style=\" width: 460px;\">");
				buffer.append(" <em class=\"rule-id\">"+num+"）</em>");
				buffer.append(" <span class=\"rule-name\" id=\"rule-name"+weixinKeyWordRole.getDbid()+"\">"+weixinKeyWordRole.getName()+"</span>");
				buffer.append(" <div class=\"rule-opts\">");
					buffer.append(" <a href=\"javascript:;\" class=\"js-edit-rule\" onclick=\"editKeyWordRole("+weixinKeyWordRole.getDbid()+")\">编辑</a>");
					buffer.append("  <span>-</span>");
					buffer.append("<a href=\"javascript:;\" class=\"js-disable-rule\" onclick=\"deleteWeixinKeyWordRole("+weixinKeyWordRole.getDbid()+")\">删除</a>");
				buffer.append(" </div>");
				buffer.append(" </h3>");
				buffer.append(" </div>");
			buffer.append("<div class=\"rule-body\">");
				buffer.append("<div class=\"long-dashed\"></div>");
					buffer.append("<div class=\"rule-keywords\">");
						buffer.append(" <div class=\"rule-inner\">");
							buffer.append(" <h4  class=\"dashed\">关键词：</h4>");
							buffer.append(" <div class=\"keyword-container\">");
								buffer.append(" <div class=\"keyword-list\" id=\"keyword-list"+weixinKeyWordRole.getDbid()+"\">");
								Set<WeixinKeyWord> weixinKeyWords = weixinKeyWordRole.getWeixinKeyWords();
								if(null!=weixinKeyWords&&weixinKeyWords.size()>0){
									for (WeixinKeyWord weixinKeyWord : weixinKeyWords) {
										buffer.append("<div class=\"keyword input-append\">");
										buffer.append("<a href=\"javascript:;\" class=\"close--circle\">×</a>");
										buffer.append("<span class=\"value\">"+weixinKeyWord.getKeyword()+"</span>");
										buffer.append("<span class=\"add-on\">");
										if(weixinKeyWord.getMatchingType()==1){
											buffer.append("全匹配");
										}
										if(weixinKeyWord.getMatchingType()==1){
											buffer.append("模糊匹配");
										}
										buffer.append("</span>");
										buffer.append("</div>");
									}
								}
								buffer.append("</div>");
								buffer.append(" <hr class=\"dashed\">");
								buffer.append(" <div class=\"opt\">");
									buffer.append("  <a href=\"javascript:;\" class=\"js-add-keyword\" onclick=\"editKey('',"+weixinKeyWordRole.getDbid()+")\" id=\"editKey"+weixinKeyWordRole.getDbid()+"\">+ 添加关键词</a>");
								buffer.append(" </div>");
							buffer.append(" </div>");
						buffer.append(" </div>");
						buffer.append(" </div>");
						//自动回复
						buffer.append("<div class=\"rule-replies\">");
							buffer.append("<div class=\"rule-inner\">");
								buffer.append("<h4>自动回复：");
									buffer.append("<span class=\"send-method\">随机发送 </span>");
								buffer.append(" </h4>");
								buffer.append(" <div class=\"reply-container\" id=\"reply-container"+weixinKeyWordRole.getDbid()+"\">");
									buffer.append(" <div class=\"info\">还没有任何回复！</div>");
									buffer.append(" <ol class=\"reply-list\" id='reply-list"+weixinKeyWordRole.getDbid()+"'></ol>");
								buffer.append(" </div>");
								buffer.append(" <hr class=\"dashed\">");
								buffer.append("  <div class=\"opt\">");
									buffer.append("   <a class=\"js-add-reply add-reply-menu\" href=\"javascript:;\" id=\"js-add-reply"+weixinKeyWordRole.getDbid()+"\" onclick=\"message("+weixinKeyWordRole.getDbid()+")\">+ 添加一条回复</a>");
									buffer.append("    <span class=\"disable-opt hide\">最多三条回复</span>");
									buffer.append(" </div>");
							buffer.append(" </div>");
						buffer.append(" </div>");
					buffer.append(" </div>");
			buffer.append(" </div>");
		return buffer.toString();
	}
	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		try {
			for (Integer dbid : dbids) {
				weixinKeyAutoresponseManageImpl.deleteByRoleId(dbid);
				weixinKeyWordManageImpl.deleteByRoleId(dbid);
				weixinKeyWordRoleManageImpl.deleteById(dbid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("", "删除成功");
		return;
	}
	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void deleteKeyWord() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		try {
			for (Integer dbid : dbids) {
				weixinKeyWordManageImpl.deleteById(dbid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("", "删除成功");
		return;
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String editSpread() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try{
			WeixinKeyWord weixinKeyWord = weixinKeyWordManageImpl.get(dbid);
			request.setAttribute("weixinKeyWord", weixinKeyWord);
		}catch (Exception e) {
		}
		return  "editSpread";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveEditSpread() throws Exception {
		HttpServletRequest request = getRequest();
		Integer spreadDetailId = ParamUtil.getIntParam(request, "spreadDetailId", -1);
		try {
			Integer dbid = weixinKeyWord.getDbid();
			String keyword = weixinKeyWord.getKeyword();
			if(null==dbid||dbid<=0){
				List<WeixinKeyWord> weixinAutoresponses = weixinKeyWordManageImpl.findBy("keyword", keyword);
				if(null!=weixinAutoresponses&&weixinAutoresponses.size()>0){
					renderErrorMsg(new Throwable("添加失败，关键词重复"), "");
					return ;
				}
				List<WeixinKeyWordRole> weixinKeyWordRoles = weixinKeyWordRoleManageImpl.findBy("spreadDetail.dbid", spreadDetailId);
				WeixinKeyWordRole weixinKeyWordRole=null;
				if(null!=weixinKeyWordRoles&&weixinKeyWordRoles.size()>0){
					weixinKeyWordRole= weixinKeyWordRoles.get(0);
					weixinKeyWordRole.setModifyDate(new Date());
					weixinKeyWordRoleManageImpl.save(weixinKeyWordRole);
				}else{
					weixinKeyWordRole=new WeixinKeyWordRole();
					weixinKeyWordRole.setCreateDate(new Date());
					weixinKeyWordRole.setModifyDate(new Date());
					weixinKeyWordRole.setType(2);
					weixinKeyWordRoleManageImpl.save(weixinKeyWordRole);
				}
				weixinKeyWord.setCreateDate(new Date());
				weixinKeyWord.setModifyDate(new Date());
				weixinKeyWord.setWeixinKeyWordRole(weixinKeyWordRole);
				weixinKeyWordManageImpl.save(weixinKeyWord);
				
				/*String keyHtml=createKeyWord(weixinKeyWord);
				renderMsg(keyHtml, "创建成功");*/
				
			}else{
				List<WeixinKeyWord> weixinAutoresponses = weixinKeyWordManageImpl.executeSql("select * from Weixin_KeyWord where keyword=? and dbid!=?", new Object[]{keyword,dbid});
				if(null!=weixinAutoresponses&&weixinAutoresponses.size()>0){
					renderErrorMsg(new Throwable("关键词重复"), "");
					return ;
				}
				WeixinKeyWord weixinKeyWord2 = weixinKeyWordManageImpl.get(dbid);
				weixinKeyWord2.setKeyword(weixinKeyWord.getKeyword());
				weixinKeyWord2.setMatchingType(weixinKeyWord.getMatchingType());
				weixinKeyWordManageImpl.save(weixinKeyWord2);
				renderMsg("", "编辑成功");
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		return;
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public String editKey() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try{
			WeixinKeyWord weixinKeyWord = weixinKeyWordManageImpl.get(dbid);
			request.setAttribute("weixinKeyWord", weixinKeyWord);
		}catch (Exception e) {
		}
		return  "editKey";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveEditKey() throws Exception {
		HttpServletRequest request = getRequest();
		Integer weixinKeyWordRoleId = ParamUtil.getIntParam(request, "weixinKeyWordRoleId", -1);
		try {
			Integer dbid = weixinKeyWord.getDbid();
			String keyword = weixinKeyWord.getKeyword();
			if(null==dbid||dbid<=0){
				List<WeixinKeyWord> weixinAutoresponses = weixinKeyWordManageImpl.findBy("keyword", keyword);
				if(null!=weixinAutoresponses&&weixinAutoresponses.size()>0){
					renderErrorMsg(new Throwable("添加失败，关键词重复"), "");
					return ;
				}
				WeixinKeyWordRole weixinKeyWordRole = weixinKeyWordRoleManageImpl.get(weixinKeyWordRoleId);
				weixinKeyWord.setCreateDate(new Date());
				weixinKeyWord.setModifyDate(new Date());
				weixinKeyWord.setWeixinKeyWordRole(weixinKeyWordRole);
				weixinKeyWordManageImpl.save(weixinKeyWord);
				
				String keyHtml=createKeyWordSelf(weixinKeyWord);
				renderMsg(keyHtml, "创建成功");
				
			}else{
				List<WeixinKeyWord> weixinAutoresponses = weixinKeyWordManageImpl.executeSql("select * from Weixin_KeyWord where keyword=? and dbid!=?", new Object[]{keyword,dbid});
				if(null!=weixinAutoresponses&&weixinAutoresponses.size()>0){
					renderErrorMsg(new Throwable("关键词重复"), "");
					return ;
				}
				WeixinKeyWord weixinKeyWord2 = weixinKeyWordManageImpl.get(dbid);
				weixinKeyWord2.setKeyword(weixinKeyWord.getKeyword());
				weixinKeyWord2.setMatchingType(weixinKeyWord.getMatchingType());
				weixinKeyWordManageImpl.save(weixinKeyWord2);
				renderMsg("", "编辑成功");
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		return;
	}
	/**
	 * 功能描述：删除回复内容
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void deleteAutoResponse() throws Exception {
		HttpServletRequest request = getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		try {
			for (Integer dbid : dbids) {
				weixinKeyAutoresponseManageImpl.deleteById(dbid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("", "删除成功");
		return;
	}
	/**
	 * 创建关键词html
	 * @param weixinAutoresponse2
	 * @return
	 */
	private String createKeyWordSelf(WeixinKeyWord weixinKeyWord) {
		StringBuffer buffer=new StringBuffer();
		WeixinKeyWordRole weixinKeyWordRole2 = weixinKeyWord.getWeixinKeyWordRole();
		buffer.append("<div class=\"keyword input-append\" id='keyword"+weixinKeyWordRole2.getDbid()+""+weixinKeyWord.getDbid()+"'>");
		buffer.append("<a href=\"javascript:;\" class=\"close--circle\" onclick=\"deleteKey("+weixinKeyWord.getDbid()+","+weixinKeyWordRole2.getDbid()+")\">×</a>");
		buffer.append("<span class=\"value\" onclick=\"editKey("+weixinKeyWord.getDbid()+","+weixinKeyWordRole2.getDbid()+")\">"+weixinKeyWord.getKeyword()+"</span>");
		buffer.append("<span class=\"add-on\">");
		if(weixinKeyWord.getMatchingType()==1){
			buffer.append("全匹配");
		}
		if(weixinKeyWord.getMatchingType()==2){
			buffer.append("	模糊匹配");
		}
		buffer.append("</span>");
		buffer.append("</div>")	;
		return buffer.toString();
	}
	/**
		 * 功能描述：
		 * 参数描述：
		 * 逻辑描述：
		 * @return
		 * @throws Exception
		 */
	public void saveAjaxResult() throws Exception {
		HttpServletRequest request = getRequest();
		Integer msgdbid = ParamUtil.getIntParam(request, "msgdbid", -1);
		Integer weixinKeyAutoresponseDbid = ParamUtil.getIntParam(request, "weixinKeyAutoresponseDbid", -1);
		Integer msgtype = ParamUtil.getIntParam(request, "msgtype", -1);
		Integer weixinKeyWordRoleId = ParamUtil.getIntParam(request, "weixinKeyWordRoleId", -1);
		String msgtext = request.getParameter("msgtext");
		StringBuffer buffer=new  StringBuffer();
		try {
			List<WeixinKeyAutoresponse> mores = weixinKeyAutoresponseManageImpl.find("from WeixinKeyAutoresponse where weixinKeyWordRole.dbid=? ", new Object[]{weixinKeyWordRoleId});
			if(null!=mores&&mores.size()>=3){
				renderErrorMsg(new Throwable("添加失败，最多添加3条回复"), "");
				return ;
			}
			WeixinKeyWordRole weixinKeyWordRole2 = weixinKeyWordRoleManageImpl.get(weixinKeyWordRoleId);
			if(null!=weixinKeyAutoresponseDbid&&weixinKeyAutoresponseDbid>0){
				WeixinKeyAutoresponse weixinKeyAutoresponse = weixinKeyAutoresponseManageImpl.get(weixinKeyAutoresponseDbid);
				if(msgtype==1){
					weixinKeyAutoresponse.setModifyDate(new Date());
					weixinKeyAutoresponse.setTemplateId(null);
					weixinKeyAutoresponse.setTemplatename(null);
					weixinKeyAutoresponse.setMsgtype("text");
					weixinKeyAutoresponse.setContent(msgtext);
				}
				if(msgtype==2){
					List<WeixinKeyAutoresponse> weixinKeyAutoresponses = weixinKeyAutoresponseManageImpl.find("from WeixinKeyAutoresponse where templateId=? and weixinKeyWordRole.dbid=?  and msgtype='text' and dbid!=?", new Object[]{msgdbid,weixinKeyWordRoleId,weixinKeyAutoresponseDbid});
					if(null!=weixinKeyAutoresponses&&weixinKeyAutoresponses.size()>0){
						renderErrorMsg(new Throwable("设置失败，已经添加该文本"), "");
						return ;
					}
					WeixinTexttemplate weixinTexttemplate = weixinTexttemplateManageImpl.get(msgdbid);
					weixinKeyAutoresponse.setModifyDate(new Date());
					weixinKeyAutoresponse.setMsgtype("text");
					weixinKeyAutoresponse.setTemplateId(weixinTexttemplate.getDbid());
					weixinKeyAutoresponse.setTemplatename(weixinTexttemplate.getTemplatename());
					weixinKeyAutoresponse.setContent(weixinTexttemplate.getContent());
				}
				if(msgtype==3){
					List<WeixinKeyAutoresponse> weixinKeyAutoresponses = weixinKeyAutoresponseManageImpl.find("from WeixinKeyAutoresponse where templateId=? and weixinKeyWordRole.dbid=?  and msgtype='text' ", new Object[]{msgdbid,weixinKeyWordRoleId});
					if(null!=weixinKeyAutoresponses&&weixinKeyAutoresponses.size()>0){
						renderErrorMsg(new Throwable("设置失败，已经添加该图文"), "");
						return ;
					}
					WeixinNewstemplate weixinNewstemplate = weixinNewstemplateManageImpl.get(msgdbid);
					weixinKeyAutoresponse.setModifyDate(new Date());
					weixinKeyAutoresponse.setMsgtype("news");
					weixinKeyAutoresponse.setTemplateId(weixinNewstemplate.getDbid());
					weixinKeyAutoresponse.setTemplatename(weixinNewstemplate.getTemplatename());
				}
				if(msgtype==4){
					List<WeixinKeyAutoresponse> weixinKeyAutoresponses = weixinKeyAutoresponseManageImpl.find("from WeixinKeyAutoresponse where templateId=? and weixinKeyWordRole.dbid=?  and msgtype='image' ", new Object[]{msgdbid,weixinKeyWordRoleId});
					if(null!=weixinKeyAutoresponses&&weixinKeyAutoresponses.size()>0){
						renderErrorMsg(new Throwable("设置失败，已经添加该图片"), "");
						return ;
					}
					WechatMedia wechatMedia = wechatMediaManageImpl.get(msgdbid);
					weixinKeyAutoresponse.setModifyDate(new Date());
					weixinKeyAutoresponse.setMsgtype("image");
					weixinKeyAutoresponse.setTemplateId(wechatMedia.getDbid());
					weixinKeyAutoresponse.setTemplatename(wechatMedia.getName());
					weixinKeyAutoresponse.setContent(wechatMedia.getThumbUrl());
					weixinKeyAutoresponseManageImpl.save(weixinKeyAutoresponse);
					
				}
				weixinKeyAutoresponseManageImpl.save(weixinKeyAutoresponse);
				createAutoResponseMessageHtml(buffer, weixinKeyAutoresponse,2);
			}else{
				if(msgtype==1){
					WeixinKeyAutoresponse weixinKeyAutoresponse=new WeixinKeyAutoresponse();
					weixinKeyAutoresponse.setCreateDate(new Date());
					weixinKeyAutoresponse.setModifyDate(new Date());
					weixinKeyAutoresponse.setMsgtype("text");
					weixinKeyAutoresponse.setContent(msgtext);
					weixinKeyAutoresponse.setWeixinKeyWordRole(weixinKeyWordRole2);
					weixinKeyAutoresponseManageImpl.save(weixinKeyAutoresponse);
					createAutoResponseMessageHtml(buffer, weixinKeyAutoresponse,1);
				}
				if(msgtype==2){
					List<WeixinKeyAutoresponse> weixinKeyAutoresponses = weixinKeyAutoresponseManageImpl.find("from WeixinKeyAutoresponse where templateId=? and weixinKeyWordRole.dbid=?  and msgtype='text' ", new Object[]{msgdbid,weixinKeyWordRoleId});
					if(null!=weixinKeyAutoresponses&&weixinKeyAutoresponses.size()>0){
						renderErrorMsg(new Throwable("设置失败，已经添加该文本"), "");
						return ;
					}
					WeixinTexttemplate weixinTexttemplate = weixinTexttemplateManageImpl.get(msgdbid);
					WeixinKeyAutoresponse weixinKeyAutoresponse=new WeixinKeyAutoresponse();
					weixinKeyAutoresponse.setCreateDate(new Date());
					weixinKeyAutoresponse.setModifyDate(new Date());
					weixinKeyAutoresponse.setMsgtype("text");
					weixinKeyAutoresponse.setTemplateId(weixinTexttemplate.getDbid());
					weixinKeyAutoresponse.setTemplatename(weixinTexttemplate.getTemplatename());
					weixinKeyAutoresponse.setWeixinKeyWordRole(weixinKeyWordRole2);
					weixinKeyAutoresponse.setContent(weixinTexttemplate.getContent());
					weixinKeyAutoresponseManageImpl.save(weixinKeyAutoresponse);
					createAutoResponseMessageHtml(buffer, weixinKeyAutoresponse,1);
				}
				if(msgtype==3){
					List<WeixinKeyAutoresponse> weixinKeyAutoresponses = weixinKeyAutoresponseManageImpl.find("from WeixinKeyAutoresponse where templateId=? and weixinKeyWordRole.dbid=?  and msgtype='news' ", new Object[]{msgdbid,weixinKeyWordRoleId});
					if(null!=weixinKeyAutoresponses&&weixinKeyAutoresponses.size()>0){
						renderErrorMsg(new Throwable("设置失败，已经添加该图文"), "");
						return ;
					}
					WeixinNewstemplate weixinNewstemplate = weixinNewstemplateManageImpl.get(msgdbid);
					WeixinKeyAutoresponse weixinKeyAutoresponse=new WeixinKeyAutoresponse();
					weixinKeyAutoresponse.setCreateDate(new Date());
					weixinKeyAutoresponse.setModifyDate(new Date());
					weixinKeyAutoresponse.setMsgtype("news");
					weixinKeyAutoresponse.setTemplateId(weixinNewstemplate.getDbid());
					weixinKeyAutoresponse.setTemplatename(weixinNewstemplate.getTemplatename());
					weixinKeyAutoresponse.setWeixinKeyWordRole(weixinKeyWordRole2);
					weixinKeyAutoresponseManageImpl.save(weixinKeyAutoresponse);
					createAutoResponseMessageHtml(buffer, weixinKeyAutoresponse,1);
				}
				if(msgtype==4){
					List<WeixinKeyAutoresponse> weixinKeyAutoresponses = weixinKeyAutoresponseManageImpl.find("from WeixinKeyAutoresponse where templateId=? and weixinKeyWordRole.dbid=?  and msgtype='image' ", new Object[]{msgdbid,weixinKeyWordRoleId});
					if(null!=weixinKeyAutoresponses&&weixinKeyAutoresponses.size()>0){
						renderErrorMsg(new Throwable("设置失败，已经添加该图片"), "");
						return ;
					}
					WechatMedia wechatMedia = wechatMediaManageImpl.get(msgdbid);
					WeixinKeyAutoresponse weixinKeyAutoresponse=new WeixinKeyAutoresponse();
					weixinKeyAutoresponse.setCreateDate(new Date());
					weixinKeyAutoresponse.setModifyDate(new Date());
					weixinKeyAutoresponse.setMsgtype("image");
					weixinKeyAutoresponse.setTemplateId(wechatMedia.getDbid());
					weixinKeyAutoresponse.setTemplatename(wechatMedia.getName());
					weixinKeyAutoresponse.setContent(wechatMedia.getThumbUrl());
					weixinKeyAutoresponse.setWeixinKeyWordRole(weixinKeyWordRole2);
					weixinKeyAutoresponseManageImpl.save(weixinKeyAutoresponse);
					createAutoResponseMessageHtml(buffer, weixinKeyAutoresponse,1);
				}
			}
		} catch (Exception e) {
			renderErrorMsg(e, "");;
			return ;
		}
		renderMsg(buffer.toString(),"设置成功");
		return;
	}
	/**
	 * 功能描述：创建自动回复html
	 * @param buffer
	 * @param weixinKeyAutoresponse
	 */
	private void createAutoResponseMessageHtml(StringBuffer buffer, WeixinKeyAutoresponse weixinKeyAutoresponse,int type) {
		WeixinKeyWordRole weixinKeyWordRole2 = weixinKeyAutoresponse.getWeixinKeyWordRole();
		if(type==1){
			buffer.append("<li id=\"reply-list"+weixinKeyWordRole2.getDbid()+""+weixinKeyAutoresponse.getDbid()+"\">");
		}
		if(weixinKeyAutoresponse.getMsgtype().equals("image")){
			buffer.append("<div class=\"reply-cont\">");
				buffer.append(" <div class=\"picture-wrapper\">");
					buffer.append("<img src=\""+weixinKeyAutoresponse.getContent()+"\"  class=\"js-img-preview\">");
				buffer.append("</div>");	
			buffer.append("</div>");
		}else{
			buffer.append("<div class=\"reply-cont\">");
			buffer.append(" <div class=\"reply-summary\">");
			if(weixinKeyAutoresponse.getMsgtype().equals("news")){
				buffer.append("<span class=\"label label-success\">图文</span>");
				buffer.append("<a href='javascript:void(-1)' target='' class=\"new-window\">"+weixinKeyAutoresponse.getTemplatename()+"</a>");
			}
			if(weixinKeyAutoresponse.getMsgtype().equals("text")){
				buffer.append("<span class=\"label label-success\">文本</span>");
				buffer.append(weixinKeyAutoresponse.getContent());
			}
			buffer.append("</div>");	
		}
		buffer.append("<div class=\"reply-opts\">");
		buffer.append("<a class=\"js-edit-it\" href=\"javascript:;\" id=\"js-add-reply"+weixinKeyWordRole2.getDbid()+""+weixinKeyAutoresponse.getDbid()+"\" onclick=\"editMessage("+weixinKeyWordRole2.getDbid()+","+weixinKeyAutoresponse.getDbid()+")\">编辑</a> - ");
		buffer.append("<a class=\"js-delete-it\" href=\"javascript:;\" onclick=\"deleteAutoResponse("+weixinKeyWordRole2.getDbid()+","+weixinKeyAutoresponse.getDbid()+")\">删除</a>");
		buffer.append("</div>");	
		buffer.append("</div>");
		if(type==1){
			buffer.append("</li>");
		}
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxEditweixinKeyAutoresponse() throws Exception {
		HttpServletRequest request = getRequest();
		Integer weixinKeyAutoresponseDbid = ParamUtil.getIntParam(request, "weixinKeyAutoresponseDbid", -1);
		StringBuffer buffer=new  StringBuffer();
		JSONObject object=new JSONObject();
		try {
			WeixinKeyAutoresponse weixinKeyAutoresponse = weixinKeyAutoresponseManageImpl.get(weixinKeyAutoresponseDbid);
			//图文
			if(weixinKeyAutoresponse.getMsgtype().equals("news")){
				WeixinNewstemplate weixinNewstemplate = weixinNewstemplateManageImpl.get(weixinKeyAutoresponse.getTemplateId());
				buffer.append("<div class=\"ng ng-single\">");
				buffer.append("<a href=\"javascript:;\" class=\"close--circle js-delete-complex\" onclick=\"removeSelectText(this)\">×</a>");
					buffer.append("<div class=\"ng-item\">");
						buffer.append("<span class=\"label label-success\">图文</span>");
						buffer.append("<div class=\"ng-title\">");
							buffer.append("<a href=''  class=\"new-window\" title=\""+weixinNewstemplate.getTemplatename()+"\">"+weixinNewstemplate.getTemplatename()+"</a>");
						buffer.append("</div>");
					buffer.append("</div>");	
				/*	buffer.append("<div class=\"ng-item view-more\">");	
					buffer.append("<p>"+weixinTexttemplate2.getContent()+"</p>");	
					buffer.append("</div>");	*/
				buffer.append("</div>");
				object.put("value", buffer.toString());
				object.put("type", "3");
				
				object.put("dbid",weixinNewstemplate.getDbid());
			}
			if(weixinKeyAutoresponse.getMsgtype().equals("text")){
				if(null!=weixinKeyAutoresponse.getTemplateId()&&weixinKeyAutoresponse.getTemplateId()>0){
					//选择文本
					WeixinTexttemplate weixinTexttemplate = weixinTexttemplateManageImpl.get(weixinKeyAutoresponse.getTemplateId());
					buffer.append("<div class=\"ng ng-single\">");
					buffer.append("<a href=\"javascript:;\" class=\"close--circle js-delete-complex\" onclick=\"removeSelectText(this)\">×</a>");
						buffer.append("<div class=\"ng-item\">");
							buffer.append("<span class=\"label label-success\">文文</span>");
								buffer.append(weixinTexttemplate.getContent());
						buffer.append("</div>");	
					/*	buffer.append("<div class=\"ng-item view-more\">");	
						buffer.append("<p>"+weixinTexttemplate2.getContent()+"</p>");	
						buffer.append("</div>");	*/
					buffer.append("</div>");
					object.put("value", buffer.toString());
					object.put("type", "2");
					object.put("dbid",weixinTexttemplate.getDbid());
				}else{
					//自定义文本
					object.put("value", weixinKeyAutoresponse.getContent());
					object.put("type", "1");
				}
			}
		} catch (Exception e) {
		}
		renderJson(object.toString());
		return;
	}
}
