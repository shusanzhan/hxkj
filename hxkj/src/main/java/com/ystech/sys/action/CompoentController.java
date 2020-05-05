package com.ystech.sys.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.DateUtil;
import com.ystech.core.util.PathUtil;
import com.ystech.core.util.ZipUtils;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.model.User;
import com.ystech.sys.service.DepartmentManageImpl;
import com.ystech.sys.service.UserManageImpl;



@Component("compoentController")
@Scope("prototype")
public class CompoentController extends BaseController{
	private File upload;
	private String uploadFileName;
	private UserManageImpl userManageImpl;
	private DepartmentManageImpl departmentManageImpl;
	
	@Resource
	public void setUserManageImpl(UserManageImpl userManageImpl) {
		this.userManageImpl = userManageImpl;
	}
	@Resource
	public void setDepartmentManageImpl(DepartmentManageImpl departmentManageImpl) {
		this.departmentManageImpl = departmentManageImpl;
	}

	public String fileSave() throws Exception {
		return "fileSave";
	}
	public String upload() throws Exception {
		System.out.println("==================");
		return "upload";
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
	
	public void saveFile() throws Exception {
		HttpServletRequest request = this.getRequest();
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String string = (String) parameterNames.nextElement();
			System.out.println("========"+string);
			
		}
		File dataFile = null;
		if (null!=upload&&!upload.getName().trim().equals("")) {// getName()返回文件名称，如果是空字符串，说明没有选择文件。
			if (!new File(PathUtil.getWebRootPath() + System.getProperty("file.separator") + "archives"
					+ System.getProperty("file.separator") + DateUtil.format(new Date())).exists()) {
				FileUtils.forceMkdir(new File(PathUtil.getWebRootPath() + System.getProperty("file.separator")
						+ "archives" + System.getProperty("file.separator") + DateUtil.format(new Date())));
			}
			
			dataFile = new File(PathUtil.getWebRootPath() + System.getProperty("file.separator") + "archives"
					+ System.getProperty("file.separator") + DateUtil.format(new Date())
					+ System.getProperty("file.separator") + uploadFileName);
			FileUtils.copyFile(upload, dataFile);  
		}
		if (dataFile == null && !dataFile.exists()) {
			//renderText(response, "failed|上传失败");
			renderText("failed|上传失败");
			return;
		}
		renderText("success|"+ dataFile.getAbsolutePath().replaceAll("\\\\", "/").replace(PathUtil.getWebRootPath(), ""));
	}
	public void uploadImages() throws Exception {
		File dataFile = null;
		String CKEditorFuncNum = this.getRequest().getParameter("CKEditorFuncNum");  
		if (null!=upload&&!upload.getName().trim().equals("")) {// getName()返回文件名称，如果是空字符串，说明没有选择文件。
			if (!new File(PathUtil.getWebRootPath() + System.getProperty("file.separator") + "archives"
					+ System.getProperty("file.separator") + DateUtil.format(new Date())).exists()) {
				FileUtils.forceMkdir(new File(PathUtil.getWebRootPath() + System.getProperty("file.separator")
						+ "archives" + System.getProperty("file.separator") + DateUtil.format(new Date())));
			}
			
			dataFile = new File(PathUtil.getWebRootPath() + System.getProperty("file.separator") + "archives"
					+ System.getProperty("file.separator") + DateUtil.format(new Date())
					+ System.getProperty("file.separator") + uploadFileName);
			FileUtils.copyFile(upload, dataFile);  
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
                + dataFile.getAbsolutePath().replaceAll("\\\\", "/").replace(PathUtil.getWebRootPath(),"")  
                + "' , '"  
                + ""  
                + "');</script>");
		//renderText("success|"+ dataFile.getAbsolutePath().replaceAll("\\\\", "/").replace(PathUtil.getWebRootPath(), ""));
	}
		
	
	public void downLoad() throws Exception {
		HttpServletRequest request = this.getRequest();
		HttpServletResponse response = this.getResponse();
		String path = URLDecoder.decode(request.getParameter("path"), "utf-8");
		File file = new File(PathUtil.getWebRootPath() + path);
		byte[] b = new byte[100];
		int len = 0;
		System.out.println("=================" + request.getCharacterEncoding());
		System.out.println("hello++++++" + path);
		System.out.println("hello ++++fileName" + file.getName());
		InputStream is = new FileInputStream(file);

		// 防止IE缓存
		response.setHeader("pragma", "no-cache");
		response.setHeader("cache-control", "no-cache");
		response.setDateHeader("Expires", 0);

		// 设置编码
		request.setCharacterEncoding("UTF-8");
		// 设置输出的格式
		response.reset();

		response.setContentType("application/octet-stream;charset=UTF-8");// 解决在弹出文件下载框不能打开文件的问题
		// 解决在弹出文件下载框不能打开文件的问题
		response.addHeader("Content-Disposition", "attachment;filename=" + ZipUtils.toUtf8String(file.getName()));// );


		ServletOutputStream outputStream = response.getOutputStream();
		// 循环取出流中的数据
		while ((len = is.read(b)) > 0) {
			outputStream.write(b, 0, len);
		}
		is.close();
		response.flushBuffer();
		outputStream.flush();
		outputStream.close();
		directlyOutput(request);
		return;
	}
	public void deleteFile() throws Exception{
		HttpServletRequest request = this.getRequest();
		HttpServletResponse response = this.getResponse();
		String path = URLDecoder.decode(request.getParameter("path"), "utf-8");
		File file = new File(PathUtil.getWebRootPath() + path);
		boolean delete = file.delete();
		if (delete==true) {
			
		}else{
			
		}
	}
	
	public String uploadConpent() {
		try{
			String path=PathUtil.getWebRootPath();
			path=path+"/archives/coupon/";
			File file=new File(path);
			File[] listFiles = file.listFiles();
			HttpServletRequest request = this.getRequest();
			request.setAttribute("listFiles", listFiles);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "uploadConpent";
	}
	public String departmentSelect() throws Exception {

		return "departmentSelect";
	}
	public String positionSelect() throws Exception {
		
		return "positionSelect";
	}
	/**
	 * 用户选择器加载页面
	 * @return
	 * @throws Exception
	 */
	public String userSelect() throws Exception {
		return "userSelect";
	}
	//所有人员信息<select>
	public void getUser() {
		String allPerson = userManageImpl.getAllPerson();
		renderText(allPerson);
	}
	//部门信息	
	public void getDepartment() {
		Enterprise enterprise = SecurityUserHolder.getEnterprise();
		/*Department parent=enterprise.getDbid();
		String departmentSelect = departmentManageImpl.getDepartmentComSelect(null,parent);
		renderText(departmentSelect);*/
	}
	public static void main(String[] args) {
		String abc="1pu";
		System.out.println("==========="+abc.replace("pu", ""));
		
	}
	//返回解析人员解析数据
	public void analyticSelectedData(){
		String resultIds = this.getRequest().getParameter("resultIds");
		String resultName = this.getRequest().getParameter("resultNames");
		String analyticSelected = analyticSelected(resultIds,resultName);
		System.out.println("================"+analyticSelected);
		analyticSelected=analyticSelected.replace("[", "").replace("]", "").replace(" ", "");
		System.out.println("============"+analyticSelected);
		renderText(analyticSelected);
	}
	
	//解析前台人员选择数据
	private String analyticSelected(String resultIds,String resultNames){
		if(null!=resultIds&&resultIds.length()>0){
			String[] ids = resultIds.split(",");
			String[] resultNameses=resultNames.split(",");
			Set<String> setIds=new HashSet<String>();
			Set<String> setNames=new HashSet<String>();
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				String name=resultNameses[i];
				if(id.lastIndexOf("us")>0){
					setIds.add(id.replace("us", ""));
					setNames.add(name);
				}
				if(id.lastIndexOf("du")>0){
					setIds.add(id.replace("du",""));
					setNames.add(name);
				}
				if(id.lastIndexOf("pu")>0){
					setIds.add(id.replace("pu",""));
					setNames.add(name+",");
				}
				if(id.lastIndexOf("zu")>0){
					setIds.add(id.replace("zu",""));
					setNames.add(name);
				}
				if(id.lastIndexOf("dp")>0){
					Integer departmentId=Integer.parseInt(id.replace("dp", ""));
					List<User> users = userManageImpl.find("from User where userState=? and departmentid=?", new Object[] {1,departmentId });
					if (null!=users&&users.size()>0) {
						for (User user : users) {
							setIds.add(user.getDbid()+"");
							setNames.add(user.getRealName());
						}
					}
				}
			}
			return setIds.toString()+"|"+setNames.toString();
		}
		return null;
	}
}
