package com.ystech.mem.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.web.BaseController;
import com.ystech.mem.model.FanliSet;
import com.ystech.mem.service.FanliSetManageImpl;

@Component("fanliSetAction")
@Scope("prototype")
public class FanliSetAction extends BaseController{
	private FanliSetManageImpl fanliSetManageImpl;
	private FanliSet fanliSet;
	
	public FanliSet getFanliSet() {
		return fanliSet;
	}
	public void setFanliSet(FanliSet fanliSet) {
		this.fanliSet = fanliSet;
	}
	@Resource
	public void setFanliSetManageImpl(FanliSetManageImpl fanliSetManageImpl) {
		this.fanliSetManageImpl = fanliSetManageImpl;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		try {
			List<FanliSet> fanliSets = fanliSetManageImpl.getAll();
			if(null!=fanliSets&&fanliSets.size()>0){
				request.setAttribute("fanliSet", fanliSets.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "edit";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void save() throws Exception {
		try {
			Integer dbid = fanliSet.getDbid();
			if(null==dbid||dbid<=0){
				fanliSet.setCreateDate(new Date());
				fanliSet.setModifyDate(new Date());
			}else{
				fanliSet.setModifyDate(new Date());
			}
			fanliSetManageImpl.save(fanliSet);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/fanliSet/edit", "设置成功！");
		return;
	}
}
