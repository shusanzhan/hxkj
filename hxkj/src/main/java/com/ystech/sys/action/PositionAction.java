package com.ystech.sys.action;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Position;
import com.ystech.sys.service.EnterpriseManageImpl;
import com.ystech.sys.service.PositionManageImpl;

@Component("positionAction")
@Scope("prototype")
public class PositionAction extends BaseController{
	private Position position;
	private PositionManageImpl positionManageImpl;
	private EnterpriseManageImpl enterpriseManageImpl;
	@Resource
	public void setEnterpriseManageImpl(EnterpriseManageImpl enterpriseManageImpl) {
		this.enterpriseManageImpl = enterpriseManageImpl;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	@Resource
	public void setPositionManageImpl(PositionManageImpl positionManageImpl) {
		this.positionManageImpl = positionManageImpl;
	}

	/**
	 * 功能描述：部门树页面
	 * @return
	 * @throws Exception
	 */
	public String list() throws Exception {
		System.out.println("===========11");
		return "list";
	}
	
	/**
	 * 功能描述：添加部门信息
	 * @return
	 * @throws Exception
	 */
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if (dbid > 0) {
			Position position2 = positionManageImpl.get(dbid);
			request.setAttribute("position", position2);
		}
		return "edit";
	}
	/**
	 * 功能描述：部门信息保存
	 * @throws Exception
	 */
	public void save() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		try {
			if (null!=position.getDbid()&&position.getDbid()>0) {
				Position parent=null;
				if(parentId>0){
					 parent = positionManageImpl.get(parentId);
				}
				Position position2 = positionManageImpl.get(position.getDbid());
				position2.setDiscription(position.getDiscription());
				position2.setName(position.getName());
				position2.setParent(parent);
				position2.setSuqNo(position.getSuqNo());
				positionManageImpl.save(position2);
			}
			else{
				if(parentId>0){
					Position parent = positionManageImpl.get(parentId);
					position.setParent(parent);
					positionManageImpl.save(position);
				}else{
					position.setParent(null);
					positionManageImpl.save(position);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
		}
		renderMsg("/position/list", "保存数据成功！");
		return ;
	}
	/**
	 * 功能描述：删除部门信息
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			positionManageImpl.deleteById(dbid);
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("", "删除数据成功！");
		return;
	}
	public void getPositionByDbid() throws Exception{
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			JSONObject object=new JSONObject();
			Position position2 = positionManageImpl.get(dbid);
			if(null!=position2){
				object.put("dbid", position2.getDbid());
				object.put("name", position2.getName());
				object.put("suqNo", position2.getSuqNo());
				object.put("discription", position2.getDiscription());
				renderJson(object.toString());
			}else{
				renderText("error");
				return ;
			}
		}
		else{
			renderText("error");
			return ;
		}
	}
	/**
	 * 功能描述：部门树生成JSON串
	 * 逻辑描述：默认绑定一颗根节点，更节点的父节点为0
	 */
	public void editResourceJson() {
		try {
			JSONObject jsonObject=null;
			JSONArray array=new JSONArray();
			 List<Position> positions = positionManageImpl.executeSql("select * from sys_Position where  ISNULL(parentId) order by suqNo",new Object[] {});
			if (null != positions && positions.size() > 0) {
				for (Position position : positions) {
					jsonObject=new JSONObject();
					jsonObject = makeJSONObject(position);
					if(jsonObject!=null){
						array.put(jsonObject);
					}
				}
			} 
			renderJson(jsonObject.toString());
			
			/*JSONObject jsonObject2 =new  JSONObject();
			List<Enterprise> enterprises = enterpriseManageImpl.getAll();
			if(null!=enterprises&&enterprises.size()==1){
				Enterprise enterprise = enterprises.get(0);
				jsonObject2.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/1_open.png");// 根节点
				jsonObject2.put("root", "root");
				jsonObject2.put("id", -1);
				jsonObject2.put("name", enterprise.getName());
				jsonObject2.put("open", true);
				jsonObject2.put("children",array);
				renderJson(jsonObject2.toString());
				//System.err.println(jsonObject2.toString());
			}else{
				renderJson("1");
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	/**
	 * 功能描述：前台拖拽更新资源
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void updateParent() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		try {
			if(dbid>0){
				Position position2 = positionManageImpl.get(dbid);
				Position parent = positionManageImpl.get(parentId);
				position2.setParent(parent);
				positionManageImpl.save(position2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("", "更新数据成功！");
		return ;
	}
	/**
	 *功能描述： 将传入的对象转化为JSON数据格式
	 * 部门树：根节点，具有子结构节点，子节点
	 * @throws JSONException
	 */
	private JSONObject makeJSONObject(Position position) throws JSONException {
		JSONObject jObject = new JSONObject();
		List<Position> children = positionManageImpl.find("from Position where parent.dbid=? order by suqNo",	new Object[] { position.getDbid() });
		if (null != children && children.size() > 0) {// 如果子部门不空
			if (position.getParent() != null&&position.getParent().getDbid()>0) {
				jObject.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/2.png");// 菜单阶段
			} else{
				jObject.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/2.png");// 菜单阶段
			}
			
			jObject.put("id", position.getDbid());
			jObject.put("name", position.getName());
			jObject.put("open", true);
			jObject.put("children", makeJSONChildren(children));
			return jObject;
		} else {
			if (position.getParent() != null&&position.getParent().getDbid()>0) {
				jObject.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/2.png");// 菜单阶段
			}
			jObject.put("id", position.getDbid());
			jObject.put("name", position.getName());
			jObject.put("children", "");
			return jObject;
		}
	}
	/**
	 * 将部门数据生成可以编辑的JSON格式
	 * **/
	private JSONArray makeJSONChildren(List<Position> children)throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (Position position : children) {
			JSONObject subJSONjObject = makeJSONObject(position);
			jsonArray.put(subJSONjObject);
		}
		return jsonArray;
	}
}
