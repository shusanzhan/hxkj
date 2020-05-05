package com.ystech.sys.action;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.web.BaseController;
import com.ystech.sys.model.SystemInfo;
import com.ystech.sys.service.SystemInfoMangeImpl;

@Component("systemInfoAction")
@Scope("prototype")
public class SystemInfoAction extends BaseController{
	private SystemInfo systemInfo;
	private SystemInfoMangeImpl systemInfoMangeImpl;
	
	public SystemInfo getSystemInfo() {
		return systemInfo;
	}
	public void setSystemInfo(SystemInfo systemInfo) {
		this.systemInfo = systemInfo;
	}
	@Resource
	public void setSystemInfoMangeImpl(SystemInfoMangeImpl systemInfoMangeImpl) {
		this.systemInfoMangeImpl = systemInfoMangeImpl;
	}
	public String systemInfo() throws Exception {
		HttpServletRequest request = getRequest();
		List<SystemInfo> systemInfos = systemInfoMangeImpl.getAll();
		if(null!=systemInfos&&systemInfos.size()>0){
			request.setAttribute("systemInfo", systemInfos.get(0));
		}
		return "systemInfo";
	}
	public void save() {
		try {
			systemInfoMangeImpl.save(systemInfo);
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/systemInfo/systemInfo", "保存数据成功！");
	}
}
