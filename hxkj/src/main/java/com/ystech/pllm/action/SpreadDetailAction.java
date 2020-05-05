package com.ystech.pllm.action;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.DateUtil;
import com.ystech.core.util.FileNameUtil;
import com.ystech.core.util.Md5;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.util.PathUtil;
import com.ystech.core.web.BaseController;
import com.ystech.mem.model.MemberShipLevel;
import com.ystech.mem.service.MemberShipLevelManagImpl;
import com.ystech.pllm.model.Spread;
import com.ystech.pllm.model.SpreadDetail;
import com.ystech.pllm.model.SpreadGroup;
import com.ystech.pllm.service.SpreadDetailManageImpl;
import com.ystech.pllm.service.SpreadGroupManageImpl;
import com.ystech.pllm.service.SpreadManageImpl;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinKeyWordRole;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinKeyWordRoleManageImpl;

import net.sf.json.JSONObject;

@Component("spreadDetailAction")
@Scope("prototype")
public class SpreadDetailAction extends BaseController{
	private SpreadDetailManageImpl spreadDetailManageImpl;
	private SpreadManageImpl spreadManageImpl;
	private SpreadGroupManageImpl spreadGroupManageImpl;
	private SpreadDetail spreadDetail;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	private MemberShipLevelManagImpl memberShipLevelManagImpl;
	private WeixinKeyWordRoleManageImpl weixinKeyWordRoleManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	public SpreadDetail getSpreadDetail() {
		return spreadDetail;
	}
	public void setSpreadDetail(SpreadDetail spreadDetail) {
		this.spreadDetail = spreadDetail;
	}
	@Resource
	public void setSpreadDetailManageImpl(SpreadDetailManageImpl spreadDetailManageImpl) {
		this.spreadDetailManageImpl = spreadDetailManageImpl;
	}
	@Resource
	public void setMemberShipLevelManagImpl(MemberShipLevelManagImpl memberShipLevelManagImpl) {
		this.memberShipLevelManagImpl = memberShipLevelManagImpl;
	}
	@Resource
	public void setSpreadManageImpl(SpreadManageImpl spreadManageImpl) {
		this.spreadManageImpl = spreadManageImpl;
	}
	@Resource
	public void setSpreadGroupManageImpl(SpreadGroupManageImpl spreadGroupManageImpl) {
		this.spreadGroupManageImpl = spreadGroupManageImpl;
	}
	@Resource
	public void setWeixinAccesstokenManageImpl(WeixinAccesstokenManageImpl weixinAccesstokenManageImpl) {
		this.weixinAccesstokenManageImpl = weixinAccesstokenManageImpl;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	@Resource
	public void setWeixinKeyWordRoleManageImpl(WeixinKeyWordRoleManageImpl weixinKeyWordRoleManageImpl) {
		this.weixinKeyWordRoleManageImpl = weixinKeyWordRoleManageImpl;
	}
	/**
	 * 功能描述：
	 * 参数描述： 
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String queryList() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		Integer spreadId = ParamUtil.getIntParam(request, "spreadId", 1);
		try {
			Spread spread = spreadManageImpl.get(spreadId);
			request.setAttribute("spread", spread);
			
			String sql="select * from pllm_s_SpreadDetail where spreadId=?";
			List params=new ArrayList();
			params.add(spreadId);
			Page<SpreadDetail> page=spreadDetailManageImpl.pagedQuerySql(pageNo, pageSize,SpreadDetail.class, sql, params.toArray());
			request.setAttribute("page", page);
		} catch (Exception e) {
		}
		return "list";
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
			List<Spread> spreads = spreadManageImpl.getAll();
			request.setAttribute("spreads", spreads);
			if(dbid>0){
				SpreadDetail spreadDetail2 = spreadDetailManageImpl.get(dbid);
				request.setAttribute("spreadDetail", spreadDetail2);
				
				request.setAttribute("spread", spreadDetail2.getSpread());
				
				List<SpreadGroup> spreadGroups = spreadGroupManageImpl.findBy("spread.dbid", spreadDetail2.getSpread().getDbid());
				request.setAttribute("spreadGroups", spreadGroups);
				
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
		HttpServletRequest request = getRequest();
		Integer spreadId = ParamUtil.getIntParam(request, "spreadId", 1);
		Integer type = ParamUtil.getIntParam(request, "type", 1);
		Integer spreadGroupId = ParamUtil.getIntParam(request, "spreadGroupId", 1);
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				Spread spread = spreadManageImpl.get(spreadId);
				spreadDetail.setSpread(spread);
				SpreadGroup spreadGroup = spreadGroupManageImpl.get(spreadGroupId);
				spreadDetail.setSpreadGroup(spreadGroup);
				Integer dbid = spreadDetail.getDbid();
				if(dbid==null||dbid<=0){
					String formatFile = DateUtil.formatFile(new Date());
					String calcMD5 = Md5.calcMD5(formatFile);
					spreadDetail.setCreateDate(new Date());
					spreadDetail.setModifyDate(new Date());
					spreadDetail.setSpreadNum(0);
					spreadDetail.setScanNum(0);
					spreadDetail.setStatus(1);
					spreadDetail.setSceneStr(calcMD5);
					spreadDetailManageImpl.save(spreadDetail);
					
					//设置回复规则
					WeixinKeyWordRole weixinKeyWordRole=new WeixinKeyWordRole();
					weixinKeyWordRole.setCreateDate(new Date());
					weixinKeyWordRole.setModifyDate(new Date());
					//参数二维码
					weixinKeyWordRole.setType(2);
					weixinKeyWordRole.setSpreadDetail(spreadDetail);
					weixinKeyWordRole.setName(spreadDetail.getName());
					weixinKeyWordRole.setAccountid(weixinAccount.getDbid());
					weixinKeyWordRoleManageImpl.save(weixinKeyWordRole);
					
					WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
					String createQrCodeUrl = WeixinUtil.CREATEQRCODE.replace("ACCESS_TOKEN", accessToken.getAccessToken());
					String json="{\"action_name\":\"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \""+calcMD5+"\"}}}";
					JSONObject jsonObject = WeixinUtil.httpRequest(createQrCodeUrl, "POST", json);
					if(null!=jsonObject){
						String ticket = jsonObject.getString("ticket");
						String ticketUrl = URLEncoder.encode(ticket, "UTF-8");
						String showCodeUrl = WeixinUtil.SHOWQRCODE.replace("TICKET", ticketUrl);
						File resourceFile = FileNameUtil.getResourceFile(spreadDetail.getSceneStr()+".jpg");
						saveUrlFile(showCodeUrl,resourceFile);
						String filePath = resourceFile.getAbsolutePath();
						String replace = filePath.replaceAll("\\\\", "/").replace(PathUtil.getWebRootPath(), "");
						spreadDetail.setTicket(ticket);
						spreadDetail.setQrcode(replace);
						spreadDetailManageImpl.save(spreadDetail);
					}
					//生成前台html
					String createQrCode = createQrCode(spreadDetail,weixinKeyWordRole);
					renderMsg(createQrCode, " 二维码创建成功");
				}else{
					SpreadDetail spreadDetail2 = spreadDetailManageImpl.get(dbid);
					if(null==spreadDetail2.getSceneStr()||spreadDetail2.getSceneStr().trim().length()<=0){
						String formatFile = DateUtil.formatFile(new Date());
						String calcMD5 = Md5.calcMD5(formatFile);
						spreadDetail2.setSceneStr(calcMD5);
					}
					spreadDetail2.setName(spreadDetail.getName());
					spreadDetail2.setSpread(spreadDetail.getSpread());
					spreadDetail2.setSpreadGroup(spreadDetail.getSpreadGroup());
					spreadDetail2.setOrderNum(spreadDetail.getOrderNum());
					spreadDetail2.setNote(spreadDetail.getNote());
					spreadDetailManageImpl.save(spreadDetail2);
					
					JSONObject text=new JSONObject();
					text.put("name", spreadDetail2.getName());
					text.put("groupName", spreadDetail2.getSpreadGroup().getName());
					text.put("spreadName", spreadDetail2.getSpread().getName());
					renderMsg(text.toString(), "更新数据成功");
				}
			}else{
				renderErrorMsg(new Throwable("无公众号信息"), "");
				return ;
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
	private String createQrCode(SpreadDetail spreadDetail,WeixinKeyWordRole weixinKeyWordRole){
		long num = spreadDetailManageImpl.countSqlResult("select count(*) from pllm_s_spreaddetail",null);
		StringBuffer buffer=new StringBuffer();
		buffer.append("<div class=\"rule-group\" id=\"rule-group"+spreadDetail.getDbid()+"\">");
			buffer.append("<div class=\"rule-meta\">");
				buffer.append(" <h3>");
				buffer.append(" <em class=\"rule-id\">"+num+"）</em>");
				buffer.append(" <span class=\"rule-name\" id=\"rule-name"+spreadDetail.getDbid()+"\">"+spreadDetail.getName()+"</span>");
				buffer.append(" <div class=\"rule-opts\">");
					buffer.append(" <a href=\"javascript:;\" class=\"js-edit-rule\" onclick=\"editSpreadDetail("+spreadDetail.getDbid()+")\">编辑</a>");
					buffer.append("  <span>-</span>");
					buffer.append("<a href=\"javascript:;\" class=\"js-disable-rule\">失效</a>");
				buffer.append(" </div>");
				buffer.append(" </h3>");
				buffer.append(" </div>");
			buffer.append("<div class=\"rule-body\">");
				buffer.append("<div class=\"long-dashed\"></div>");
					buffer.append("<div class=\"rule-keywords\">");
						buffer.append(" <div class=\"rule-inner\">");
							buffer.append("  <a href=\"javascript:;\" class=\"pull-right opt js-download-qrcode\" onclick=\"window.location.href='${ctx}/spread/downloand?dbid="+spreadDetail.getDbid()+"'\">下载二维码</a>");
							buffer.append(" <h4>扫带参数二维码：</h4>");
							buffer.append(" <h5 class=\"ta-c\" id=\"spreadDetailSpread"+spreadDetail.getDbid()+"\">渠道："+spreadDetail.getSpread().getName()+"</h5>");
							buffer.append(" <h5 class=\"ta-c\" id=\"spreadDetailspreadGroup"+spreadDetail.getDbid()+"\">分组："+spreadDetail.getSpreadGroup().getName()+"</h5>");
							buffer.append(" <div class=\"qrcode-container loading\">");
								buffer.append(" <img src=\""+spreadDetail.getQrcode()+"\">");
							buffer.append(" </div>");
							buffer.append(" <h4  class=\"dashed\">或发送关键词：</h4>");
							buffer.append(" <div class=\"keyword-container\">");
							buffer.append(" <div class=\"keyword-list\" id=\"keyword-list"+spreadDetail.getDbid()+"\">");
							buffer.append("</div>");
							buffer.append(" <hr class=\"dashed\">");
							buffer.append(" <div class=\"opt\">");
								buffer.append("  <a href=\"javascript:;\" class=\"js-add-keyword\" onclick=\"editKey('',"+spreadDetail.getDbid()+")\" id=\"editKey"+spreadDetail.getDbid()+"\">+ 添加关键词</a>");
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
						//粉丝标签
						buffer.append(" <div class=\"rule-tags\">");
							buffer.append("<div class=\"rule-inner\">");
								buffer.append(" <h4>给粉丝打标签：</h4>");
								buffer.append("<div class=\"tag-container\">");
									buffer.append("<div class=\"info\">还没有任何标签！</div>");
									buffer.append("<div class=\"tag-list\" ></div>");
								buffer.append("</div>");
								buffer.append("<hr class=\"dashed\">");
								buffer.append("<div class=\"opt\">");
									buffer.append("<a data-index=\"8\" class=\"js-edit-tag\" >+ 添加标签组</a>");
								buffer.append("</div>");
							buffer.append("</div>");
						buffer.append(" </div>");
						buffer.append("<div class=\"rule-level\">");
							buffer.append("<h4>成为指定等级会员：</h4>");
							buffer.append("<hr class=\"dashed\">");
								buffer.append(" <div class=\"rule-inner\">");
									buffer.append(" <div class=\"level-container\">");
										buffer.append(" <div class=\"info\">扫码不调整会员等级</div>");
										buffer.append(" <div class=\"level-list\" id=\"level-list"+spreadDetail.getDbid()+"\"></div>");
									buffer.append(" </div>");
									buffer.append(" <hr class=\"dashed\">");
									buffer.append(" <div class=\"opt\">");
										buffer.append(" <a class=\"js-edit-level\" href=\"javascript:;\" href=\"javascript:;\" id=\"js-edit-level"+spreadDetail.getDbid()+"\" onclick=\"editMemberShipLevel("+spreadDetail.getDbid()+")\">+ 设置等级</a>");
									buffer.append(" </div>");
								buffer.append(" </div>");
						buffer.append(" </div>");
					buffer.append(" </div>");
			buffer.append(" </div>");
		return buffer.toString();
	}
	/**
	 * 功能描述： 参数描述： 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		if(null!=dbids&&dbids.length>0){
			try {
				for (Integer dbid : dbids) {
					spreadDetailManageImpl.deleteById(dbid);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
				renderErrorMsg(e, "");
				return ;
			}
		}else{
			renderErrorMsg(new Throwable("未选中数据！"), "");
			return ;
		}
		String query = ParamUtil.getQueryUrl(request);
		renderMsg("/spreadDetail/queryList"+query, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：启用或停用参数二维码 
	 * 参数描述： 
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void available() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		try {
			for (Integer dbid : dbids) {
				SpreadDetail spreadDetail2 = spreadDetailManageImpl.get(dbid);
				Integer status = spreadDetail2.getStatus();
				if(status==1){
					spreadDetail2.setStatus(2);
				}
				if(status==2){
					spreadDetail2.setStatus(1);
				}
				spreadDetailManageImpl.save(spreadDetail2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("","设置成功");
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxMemberShipLevel() throws Exception {
		HttpServletRequest request = getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		StringBuffer buffer=new StringBuffer();
		try {
			SpreadDetail spreadDetail2 = spreadDetailManageImpl.get(dbid);
			MemberShipLevel memberShipLevel = spreadDetail2.getMemberShipLevel();
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.findBy("enterpriseId", enterprise.getDbid());
			buffer.append("<select  name='memberShipLevelId' id='memberShipLevelId' class=\"media text\" 	checkType=\"integer,1\" tip=\"请选择会员级别\">");
			for (MemberShipLevel memberShipLevel2 : memberShipLevels) {
				if(null!=memberShipLevel){
					if(memberShipLevel.getDbid()==(int)memberShipLevel2.getDbid()){
						buffer.append("<option value='"+memberShipLevel2.getDbid()+"' selected=\"selected\">"+memberShipLevel2.getName()+"</option>");
					}else{
						buffer.append("<option value='"+memberShipLevel2.getDbid()+"'>"+memberShipLevel2.getName()+"</option>");
					}
				}else{
					buffer.append("<option value='"+memberShipLevel2.getDbid()+"'>"+memberShipLevel2.getName()+"</option>");
				}
			}
			buffer.append("</select>");
		} catch (Exception e) {
		}
		renderText(buffer.toString());
		return;
	}
	/**
		 * 功能描述：
		 * 参数描述：s
		 * 逻辑描述：
		 * @return
		 * @throws Exception
		 */
	public void saveMemberShipLevel() throws Exception {
		HttpServletRequest request = getRequest();
		Integer memberShipLevelId = ParamUtil.getIntParam(request, "memberShipLevelId", -1);
		Integer dbid = ParamUtil.getIntParam(request, "spreadDetail.dbid", -1);
		StringBuffer buffer=new StringBuffer();
		try {
			SpreadDetail spreadDetail2 = spreadDetailManageImpl.get(dbid);
			if(memberShipLevelId>0){
				MemberShipLevel memberShipLevel = memberShipLevelManagImpl.get(memberShipLevelId);
				spreadDetail2.setMemberShipLevel(memberShipLevel);
				spreadDetailManageImpl.save(spreadDetail2);
				buffer.append("<div class=\"level\">"+memberShipLevel.getName());
					buffer.append("<a class=\"close-modal small hide js-delete-level\" data-index=\"0\" onclick=\"deleteMemberShipLevel("+spreadDetail2.getDbid()+")\">×</a>");
				buffer.append("</div>");
			}else{
				renderErrorMsg(new Throwable("设置失败"), "");
			}
		} catch (Exception e) {
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg(buffer.toString(), "设置成功");
		return;
	}
	/**
		 * 功能描述：
		 * 参数描述：
		 * 逻辑描述：
		 * @return
		 * @throws Exception
		 */
	public void deleteMemberShipLevel() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			SpreadDetail spreadDetail2 = spreadDetailManageImpl.get(dbid);
			spreadDetail2.setMemberShipLevel(null);
			spreadDetailManageImpl.save(spreadDetail2);
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("", "删除数据成功");
		return;
	}
	//获取网络文件，转存到fileDes中，fileDes需要带文件后缀名    
    public static void saveUrlFile(String fileUrl,File toFile) throws Exception    
    {    
        if (toFile.exists())    
        {    
//          throw new Exception("file exist");    
            return;    
        }    
        toFile.createNewFile();    
        FileOutputStream outImgStream = new FileOutputStream(toFile);    
        outImgStream.write(getUrlFileData(fileUrl));    
        outImgStream.close();    
    }    
        
    //获取链接地址文件的byte数据    
    public static byte[] getUrlFileData(String fileUrl) throws Exception    
    {    
        URL url = new URL(fileUrl);    
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();    
        httpConn.connect();    
        InputStream cin = httpConn.getInputStream();    
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        while ((len = cin.read(buffer)) != -1) {    
            outStream.write(buffer, 0, len);    
        }    
        cin.close();    
        byte[] fileData = outStream.toByteArray();    
        outStream.close();    
        return fileData;    
    }    
        
    //获取链接地址的字符数据，wichSep是否换行标记    
    public static String getUrlDetail(String urlStr,boolean withSep) throws Exception    
    {    
        URL url = new URL(urlStr);    
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();    
        httpConn.connect();    
        InputStream cin = httpConn.getInputStream();    
        BufferedReader reader = new BufferedReader(new InputStreamReader(cin,"UTF-8"));    
        StringBuffer sb = new StringBuffer();    
        String rl = null;    
        while((rl = reader.readLine()) != null)    
        {    
            if (withSep)    
            {    
                sb.append(rl).append(System.getProperty("line.separator"));    
            }    
            else    
            {    
                sb.append(rl);    
            }    
        }    
        return sb.toString();    
    }  
}
