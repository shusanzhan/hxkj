package com.ystech.weixin.action;

import java.io.File;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.FileNameUtil;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.util.PathUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.User;
import com.ystech.sys.service.UserManageImpl;
import com.ystech.weixin.core.util.WeixinUploadMidea;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WechatMedia;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.service.WechatMediaManageImpl;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;

import net.sf.json.JSONObject;



@Component("wechatMediaAction")
@Scope("prototype")
public class WechatMediaAction extends BaseController{
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	private WechatMediaManageImpl wechatMediaManageImpl;
	private UserManageImpl userManageImpl;
	private File file;
	private String fileFileName;
	private String fileContentType;
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}
	
	@Resource
	public void setUserManageImpl(UserManageImpl userManageImpl) {
		this.userManageImpl = userManageImpl;
	}

	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	@Resource
	public void setWeixinAccesstokenManageImpl(
			WeixinAccesstokenManageImpl weixinAccesstokenManageImpl) {
		this.weixinAccesstokenManageImpl = weixinAccesstokenManageImpl;
	}
	@Resource
	public void setWechatMediaManageImpl(WechatMediaManageImpl wechatMediaManageImpl) {
		this.wechatMediaManageImpl = wechatMediaManageImpl;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}
	//文件上传！出现异常文能解决
	/**
	 * 功能描述：上传微信永久素材，并保存数据
	 * @throws Exception
	 */
	public void uploadFileMidia() {
		HttpServletRequest request = this.getRequest();
		File dataFile = null;
		String filePath=null;
		JSONObject fromObject=new JSONObject();
		try{
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				if (null!=file&&!file.getName().trim().equals("")) {// getName()返回文件名称，如果是空字符串，说明没有选择文件。
					dataFile = FileNameUtil.getResourceFile(fileFileName);
					FileUtils.copyFile( file, dataFile);  
					filePath=dataFile.getAbsolutePath();
					
					if(null==weixinAccount){
						Integer userId = ParamUtil.getIntParam(request, "userId", -1);
						User user = userManageImpl.get(userId);
						weixinAccount = weixinAccountManageImpl.findUniqueBy("username", user.getUserId());
						if (weixinAccount != null) {
						} else {
							weixinAccount = new WeixinAccount();
							// 返回个临时对象，防止空指针
							weixinAccount.setWeixinAccountid("-1");
							weixinAccount.setDbid(-1);
						}
					}
					WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
					if(null==accessToken){
						fromObject.put("result", "failed");
						return ;
					}
					JSONObject jsonObject = WeixinUploadMidea.mediaUploadGraphic(accessToken.getAccessToken(), WeixinUploadMidea.IMAGE, dataFile);
					WechatMedia wechatMedia=new WechatMedia();
					 // 解析返回结果
			        if (jsonObject.get("errcode") != null) {
			            wechatMedia.setErrcode(jsonObject.get("errcode").toString());
			            wechatMedia.setErrmsg(jsonObject.get("errmsg").toString());
			        }
			        wechatMedia.setType(WeixinUploadMidea.IMAGE);
			        wechatMedia.setThumbWechatUrl(jsonObject.get("url").toString());
			        // type等于thumb时的返回结果和其它类型不一样
			        if ("thumb".equals(WeixinUploadMidea.IMAGE)) {
			            wechatMedia.setMediaId(jsonObject.get("thumb_media_id").toString());
			        } else {
			            wechatMedia.setMediaId(jsonObject.get("media_id").toString());
			        }
			        wechatMedia.setThumbUrl(filePath.replaceAll("\\\\", "/").replace(PathUtil.getWebRootPath(), ""));
			        wechatMedia.setUploadDate(new Date());
			        wechatMedia.setMediaType(WechatMedia.MEDIAIMAGE);
			        wechatMedia.setName(dataFile.getName());
			        wechatMedia.setWeixinAccountId(weixinAccount.getDbid());
			        wechatMediaManageImpl.save(wechatMedia);
			        
					fromObject = JSONObject.fromObject(wechatMedia);
					fromObject.put("result", "success");
					renderJson(fromObject.toString());
					return ;
				}
			}else{
				fromObject.put("result", "failed");
				return ;
			}
		}catch (Exception e) {
			e.printStackTrace();
			fromObject.put("result", "failed");
			renderJson(fromObject.toString());
			return ;
		}
		if (dataFile == null && !dataFile.exists()) {
			fromObject.put("result", "failed");
			renderJson(fromObject.toString());
			return;
		}
		return ;
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
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 10);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				Page<WechatMedia> page = wechatMediaManageImpl.pagedQuerySql(pageNo, pageSize, WechatMedia.class, "select * from weixin_WechatMedia where mediaType=? and weixinAccountId=? ",new Object[]{WechatMedia.MEDIAIMAGE,weixinAccount.getDbid()});
				request.setAttribute("page", page);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
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
	public String selectWechatMedia() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				List<WechatMedia> wechatMedias = wechatMediaManageImpl.executeSql("select * from weixin_WechatMedia where mediaType=? and weixinAccountId=? ",new Object[]{WechatMedia.MEDIAIMAGE,weixinAccount.getDbid()});
				request.setAttribute("wechatMedias", wechatMedias);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "selectWechatMedia";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxTempt() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer wechatMediaId = ParamUtil.getIntParam(request, "wechatMediaId", -1);
		try {
			JSONObject object=new JSONObject();
			WechatMedia wechatMedia = wechatMediaManageImpl.get(wechatMediaId);
			StringBuffer buffer=new  StringBuffer();
			buffer.append("<div class=\"ng ng-single  ng-image\">");
			buffer.append("<a href=\"javascript:;\" class=\"close--circle js-delete-complex\" onclick=\"removeSelectText(this)\">×</a>");
				buffer.append("<a title=\""+wechatMedia.getName()+"\" class='picture' target=\"_blank\" >");
				buffer.append("<img src=\""+wechatMedia.getThumbUrl()+"\" alt=''>");
				buffer.append("</a>");	
			buffer.append("</div>");
			object.put("value", buffer.toString());
			object.put("dbid",wechatMedia.getDbid());
			renderJson(object.toString());
		} catch (Exception e) {
		}
		return;
	}
	/**
	 * 功能描述：更新数据
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveEdit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer wechatMediaId = ParamUtil.getIntParam(request, "wechatMediaId", -1);
		String name = request.getParameter("name");
		try {
			if (wechatMediaId>0) {
				WechatMedia wechatMedia = wechatMediaManageImpl.get(wechatMediaId);
				wechatMedia.setName(name);
				wechatMediaManageImpl.save(wechatMedia);
				renderMsg("/wechatMedia/queryList", "保存数据成功！");
				return ;
			}
			renderErrorMsg(new Throwable("保存数据失败"), "");
			return ;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
	}
	/**
	 * 功能描述：微信多媒体素材选择器
	 * @return
	 */
	public String uploadConpentWechat() {
		HttpServletRequest request = this.getRequest();
		try{
			List<WechatMedia> wechatMedias = wechatMediaManageImpl.getAll();
			request.setAttribute("wechatMedias", wechatMedias);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "uploadConpentWechat";
	}
	/**
	 * 功能描述：微信多媒体上传本地图片
	 * @return
	 */
	public String uploadLocalImage() {
		HttpServletRequest request = this.getRequest();
		try{
			List<WechatMedia> wechatMedias = wechatMediaManageImpl.getAll();
			request.setAttribute("wechatMedias", wechatMedias);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "uploadLocalImage";
	}
	
	//删除文件
	public void deleteFile() throws Exception{
		HttpServletRequest request = this.getRequest();
		String fileUrl = request.getParameter("fileUrl");
		String path = URLDecoder.decode(request.getParameter("fileUrl"), "utf-8");
		try{
			File file = new File(PathUtil.getWebRootPath() + path);
			boolean delete = file.delete();
			if (delete==true) {
				renderText("success");
				return ;
			}else{
				renderText("failed");
				return ;
			}
		}catch (Exception e) {
			e.printStackTrace();
			renderText("failed");
		}
		return ;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		String query = ParamUtil.getQueryUrl(request);
		try {
			if(null!=dbids&&dbids.length>0){
				for (Integer dbid : dbids) {
					WechatMedia wechatMedia = wechatMediaManageImpl.get(dbid);
					String mediaId = wechatMedia.getMediaId();
					wechatMediaManageImpl.deleteById(dbid);
					boolean deleteNews = deleteNews(mediaId);
					if(deleteNews==true){
						renderMsg("/wechatMedia/queryList"+query, "删除数据成功！");
						return ;
					}else{
						renderMsg("/wechatMedia/queryList"+query, "服务器数据成功,微信端数据删除失败！");
						return;
					}
				}
			}
			else{
				renderErrorMsg(new Throwable("未选中数据！"), "");
				return ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		return;
	}
	/**
	 * 功能描述：多图文选择图片
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void ajaxJsonByDbid() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		JSONObject fromObject=new JSONObject();
		try {
			WechatMedia wechatMedia = wechatMediaManageImpl.get(dbid);
			fromObject = JSONObject.fromObject(wechatMedia);
			fromObject.put("result", "success");
			renderJson(fromObject.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			fromObject.put("result", "failed");
			renderJson(fromObject.toString());
			return;
		}
		return;
	}
	/** 
	* 替换文件上传中出现的错误信息 
	* */  
	@Override  
	public void addActionError(String anErrorMessage) {  
	           //这里要先判断一下，是我们要替换的错误，才处理  
	    if (anErrorMessage.startsWith("the request was rejected because its size")) {  
	                  //这些只是将原信息中的文件大小提取出来。  
	        Matcher m = Pattern.compile("\\d+").matcher(anErrorMessage);  
	        String s1 = "";  
	        if (m.find())   s1 = m.group();  
	        String s2 = "";  
	        if (m.find())   s2 = m.group();  
	                   //偷梁换柱，将信息替换掉  
	        super.addActionError("你上传的文件（" + s1 + "）超过允许的大小（" + s2 + "）");  
	    } else {//不是则不管它  
	        super.addActionError(anErrorMessage);  
	    }  
	}  
	public String save() throws Exception {
		HttpServletRequest request = this.getRequest();
		String userName = request.getParameter("userName");
		return "selfUpload";
	}
	/**
	 * 功能描述：删除远程微信 公众平台图片
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public boolean deleteNews(String mediaId) throws Exception {
		String targetJson="{\"media_id\":"+mediaId+"}";
		try {
			Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				if(null!=weixinAccount){
					WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
					String addNews = WeixinUtil.DELMATERIAL.replace("ACCESS_TOKEN", accessToken.getAccessToken());
					JSONObject jsonObject = WeixinUtil.httpRequest(addNews, "POST", targetJson);
					if(null!=jsonObject){
						String errcode = jsonObject.getString("errcode");
						if(errcode.contains("0")){
							return true;
						}else{
							System.err.println("erroCode:"+jsonObject.getString("errcode")+"  errmsg:"+jsonObject.getString("errmsg"));
							return false;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
}
