package com.ystech.mem.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.mem.model.UseCarArea;
import com.ystech.mem.service.UseCarAreaManageImpl;

@Component("useCarAreaAction")
@Scope("prototype")
public class UseCarAreaAction extends BaseController{
	private UseCarAreaManageImpl useCarAreaManageImpl;
	private UseCarArea useCarArea;
	
	public UseCarArea getUseCarArea() {
		return useCarArea;
	}
	public void setUseCarArea(UseCarArea useCarArea) {
		this.useCarArea = useCarArea;
	}
	@Resource
	public void setUseCarAreaManageImpl(UseCarAreaManageImpl useCarAreaManageImpl) {
		this.useCarAreaManageImpl = useCarAreaManageImpl;
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
		try{
			List<UseCarArea> useCarAreas = useCarAreaManageImpl.executeSql("select * from mem_UseCarArea where parentId IS NULL order by num", new Object[]{});
			request.setAttribute("useCarAreas", useCarAreas);
		}catch (Exception e) {
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
	public String add() throws Exception {
		HttpServletRequest request = this.getRequest();
		try{
			String useCarAreaStr = useCarAreaManageImpl.getUseCarArea(null);
			request.setAttribute("useCarAreaSelect", useCarAreaStr);
		}catch (Exception e) {
			log.error(e);
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
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try{
			if(dbid>0){
				UseCarArea useCarArea = useCarAreaManageImpl.get(dbid);
				String cusString = useCarAreaManageImpl.getUseCarArea(useCarArea);
				request.setAttribute("useCarAreaSelect", cusString);
				request.setAttribute("useCarArea", useCarArea);
			}else{
				String cusString = useCarAreaManageImpl.getUseCarArea(null);
				request.setAttribute("useCarAreaSelect", cusString);
			}
			
		}catch (Exception e) {
			log.error(e);
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
		HttpServletRequest request = this.getRequest();
		Integer parendId = ParamUtil.getIntParam(request, "parentId", -1);
		if(parendId>-1){
			try{
				UseCarArea parent=null;
				if(parendId>0){
					parent = useCarAreaManageImpl.get(parendId);
					useCarArea.setParent(parent);
					useCarArea.setLevelNum(2);
				}else{
					useCarArea.setLevelNum(1);
				}
				//第一添加数据 保存
				if(null==useCarArea.getDbid()||useCarArea.getDbid()<=0)
				{
					useCarArea.setModifyDate(new Date());
					useCarArea.setCreateDate(new Date());
					useCarAreaManageImpl.save(useCarArea);
				}else{
					//修改时保存数据
					UseCarArea useCarArea2 = useCarAreaManageImpl.get(useCarArea.getDbid());
					useCarArea2.setModifyDate(new Date());
					useCarArea2.setName(useCarArea.getName());
					useCarArea2.setNum(useCarArea.getNum());
					useCarArea2.setLevelNum(useCarArea.getLevelNum());
					useCarArea2.setParent(useCarArea.getParent());
					useCarArea2.setNote(useCarArea.getNote());
					useCarAreaManageImpl.save(useCarArea2);
				}
			}catch (Exception e) {
				log.error(e);
				e.printStackTrace();
				renderErrorMsg(e, "");
				return ;
			}
		}else{
			renderErrorMsg(new Throwable("系统异常！"), "");
			return;
		}
		renderMsg("/useCarArea/queryList", "保存数据成功！");
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
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(null!=dbid&&dbid>0){
			try {
					List<UseCarArea> childs = useCarAreaManageImpl.findBy("parent.dbid", dbid);
					if(!childs.isEmpty()){
						renderErrorMsg(new Throwable("该数据有子级分类，请先删除子级分类在删除数据！"), "");
						return ;
					}else{
						useCarAreaManageImpl.deleteById(dbid);
					}
			} catch (Exception e) {
				log.error(e);
				renderErrorMsg(e, "");
				return ;
			}
		}else{
			renderErrorMsg(new Throwable("未选中数据！"), "");
			return ;
		}
		String query = ParamUtil.getQueryUrl(request);
		renderMsg("/useCarArea/queryList"+query, "删除数据成功！");
		return;
	}
}
