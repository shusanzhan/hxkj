package com.ystech.weixin.action;

import java.io.File;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.util.DateUtil;
import com.ystech.core.util.PathUtil;
import com.ystech.core.web.BaseController;
import com.ystech.weixin.core.util.WeixinUploadMidea;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WechatMedia;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.service.WechatMediaManageImpl;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;

import net.sf.json.JSONObject;



@Component("wechatMediaImageAction")
@Scope("prototype")
public class WechatMediaImageAction extends BaseController{
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	private WechatMediaManageImpl wechatMediaManageImpl;
	private File upload;
	private String uploadFileName;
	private String fileContentType;

	public String getFileContentType() {
		return fileContentType;
	}
	
	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
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
	/**
	 * 功能描述：上传微信图文 具体文件内容中图文素材
	 * @throws Exception
	 */
	public void uploadImages() throws Exception {
		File dataFile = null;
		String CKEditorFuncNum = this.getRequest().getParameter("CKEditorFuncNum");  
		String filePath=null;
		WechatMedia wechatMedia=new WechatMedia();
		try{
			if (null!=upload&&!upload.getName().trim().equals("")) {// getName()返回文件名称，如果是空字符串，说明没有选择文件。
				if (!new File(PathUtil.getWebRootPath() + System.getProperty("file.separator") + "archives"
						+ System.getProperty("file.separator") + DateUtil.format(new Date())).exists()) {
					FileUtils.forceMkdir(new File(PathUtil.getWebRootPath() + System.getProperty("file.separator")
							+ "archives" + System.getProperty("file.separator") + DateUtil.format(new Date())));
				}
				
				dataFile = new File(PathUtil.getWebRootPath() + System.getProperty("file.separator") + "archives"
						+ System.getProperty("file.separator") + DateUtil.format(new Date())
						+ System.getProperty("file.separator") + uploadFileName);
				upload.renameTo(dataFile);
				
				filePath=dataFile.getAbsolutePath();
				//上传到微信服务器
				List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
				WeixinAccount weixinAccount=null;
				if(null!=weixinAccounts){
					weixinAccount = weixinAccounts.get(0);
				}
				WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
				JSONObject jsonObject = WeixinUploadMidea.uploadImage(accessToken.getAccessToken(), dataFile);
				 // 解析返回结果
		        if (jsonObject.get("errcode") != null) {
		            wechatMedia.setErrcode(jsonObject.get("errcode").toString());
		            wechatMedia.setErrmsg(jsonObject.get("errmsg").toString());
		        }
		        wechatMedia.setType(WeixinUploadMidea.IMAGE);
		        wechatMedia.setThumbWechatUrl(jsonObject.get("url").toString());
		        // type等于thumb时的返回结果和其它类型不一样
		        if ("thumb".equals(WeixinUploadMidea.IMAGE)) {
		        	if(jsonObject.toString().contains("thumb_media_id")){
		        		wechatMedia.setMediaId(jsonObject.get("thumb_media_id").toString());
		        	}
		        } else {
		        	if(jsonObject.toString().contains("media_id")){
		        		wechatMedia.setMediaId(jsonObject.get("media_id").toString());
		        	}
		        }
		        wechatMedia.setThumbUrl(filePath.replaceAll("\\\\", "/").replace(PathUtil.getWebRootPath(), ""));
		        wechatMedia.setUploadDate(new Date());
		        wechatMedia.setMediaType(WechatMedia.MEDIACONTENT);
		        wechatMedia.setName(upload.getName());
		        wechatMediaManageImpl.save(wechatMedia);
			}
			if (dataFile == null && !dataFile.exists()) {
				//renderText(response, "failed|上传失败");
				renderText("failed|上传失败");
				return;
			}
			HttpServletResponse response = this.getResponse();
			response.getWriter().write("<script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction("  
	                + CKEditorFuncNum  
	                + ", '"  
	                //+ dataFile.getAbsolutePath().replaceAll("\\\\", "/").replace(PathUtil.getWebRootPath(),"")  
	                + wechatMedia.getThumbWechatUrl()
	                + "' , '"  
	                + ""  
	                + "');</script>");
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
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
	
	public String departmentSelect() throws Exception {

		return "departmentSelect";
	}
	
	
	public static void main(String[] args) {
		String abc="1pu";
		System.out.println("==========="+abc.replace("pu", ""));
		
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
}
