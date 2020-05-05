package com.ystech.sys.action;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.Resource;
import com.ystech.sys.service.ResourceManageImpl;

@Component("resourceAction")
@Scope("prototype")
public class ResourceAction extends BaseController {
	private Resource resource;
	private ResourceManageImpl resourceManageImpl;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@javax.annotation.Resource
	public void setResourceManageImpl(ResourceManageImpl resourceManageImpl) {
		this.resourceManageImpl = resourceManageImpl;
	}

	public String queryList() throws Exception {
		return "list";
	}

	public void save() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		Resource parent=null;
		try {
			if(parentId>0){
				parent= resourceManageImpl.get(parentId);
			}
			if(null!=resource.getDbid()&&resource.getDbid()>0){
				Resource resource2 = resourceManageImpl.get(resource.getDbid());
				resource2.setContent(resource.getContent());
				resource2.setMenu(resource.getMenu());
				resource2.setOrderNo(resource.getOrderNo());
				resource2.setParent(parent);
				resource2.setTitle(resource.getTitle());
				resource2.setType(resource.getType());
				resourceManageImpl.save(resource2);
			}else{
				resource.setParent(parent);
				resourceManageImpl.save(resource);
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
		}
		renderMsg("/resource/queryList", "保存数据成功！");
		return;
	}

	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if (dbid > 0) {
			Resource resource = resourceManageImpl.get(dbid);
			request.setAttribute("resource", resource);
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
	public String orderNum() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer parentId = ParamUtil.getIntParam(request, "parentId", -1);
		try {
			List<Resource> resources = resourceManageImpl.find("from Resource where parent.dbid=? order by orderNo", parentId);
			request.setAttribute("resources", resources);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "orderNum";
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void saveOrderNum() throws Exception {
		HttpServletRequest request = this.getRequest();
		String[] dbids = request.getParameterValues("dbid");
		String[] orderNos = request.getParameterValues("orderNo");
		try {
			int i=0;
			for (String dbid : dbids) {
				Integer d = Integer.parseInt(dbid);
				Resource resource2 = resourceManageImpl.get(d);
				if(null!=orderNos[i]&&orderNos[i].trim().length()>0){
					resource2.setOrderNo(Integer.parseInt(orderNos[i]));
				}
				resourceManageImpl.save(resource2);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
		}
		renderMsg("/resource/queryList", "保存数据成功！");
		return;
	}
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		try {
			
			resourceManageImpl.deleteById(dbid);
		} catch (Exception e) {
			e.printStackTrace();
			renderErrorMsg(e, "");
		}
		renderMsg("/resource/queryList", "删除数据成功！");
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
				Resource resource2 = resourceManageImpl.get(dbid);
				Resource parent = resourceManageImpl.get(parentId);
				resource2.setMenu(parent.getMenu()+1);
				resource2.setParent(parent);
				resourceManageImpl.save(resource2);
				Set<Resource> children = resource2.getChildren();
				if (children.size()>0) {
					for (Resource chi : children) {
						chi.setMenu(resource2.getMenu()+1);
						resourceManageImpl.save(chi);
					}
				}
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
	public void editResourceJson() {
		try {
			List<Resource> resources = resourceManageImpl.executeSql(
					"select * from sys_resource where parentId=?",
					new Object[] { 0 });
			if (null != resources && resources.size() > 0) {
				JSONObject jsonObject = makeJSONObject(resources.get(0));
				renderJson(jsonObject.toString());
			} else {
				renderJson("1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	public void roleResourceJson(){
		try {
			JSONArray makeJson = makeJson();
			if(null!=makeJson){
				renderJson(makeJson.toString());
			}else{
				renderJson("1");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 将传入的对象转化为JSON数据格式
	 * 
	 * @throws JSONException
	 */
	private JSONObject makeJSONObject(Resource resource) throws JSONException {
		JSONObject jObject = new JSONObject();
		List<Resource> children = resourceManageImpl.find(
				"from Resource where parent.dbid=? order by orderNo",
				new Object[] { resource.getDbid() });
		if (null != children && children.size() >= 0) {// 如果resource不为空
			resourceToJsonObject(resource, jObject);
			jObject.put("children", makeJSONChildren(children));
			return jObject;
		} else {
			resourceToJsonObject(resource, jObject);
			return jObject;
		}
	}

	private void resourceToJsonObject(Resource resource, JSONObject jObject)
			throws JSONException {
		jObject.put("open", false);
		if (resource.getMenu() == 0) {
			if (resource.getParent() != null&&resource.getParent().getDbid()>0) {
				jObject.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/2.png");// 菜单阶段
			} else {
				jObject.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/1_open.png");// 根节点
				jObject.put("open", true);
				jObject.put("root", "root");
			}
		}
		if (resource.getMenu() == 1) {
			jObject.put("icon",
					"/widgets/ztree/css/zTreeStyle/img/diy/3.png");// 菜单阶段
			jObject.put("root", "root");
		}
		jObject.put("id", resource.getDbid());
		jObject.put("name", resource.getTitle());
		jObject.put("menu", resource.getMenu());
	}

	/**
	 * 将部门数据生成可以编辑的JSON格式
	 * **/
	private JSONArray makeJSONChildren(List<Resource> children)
			throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (Resource resource : children) {
			JSONObject subJSONjObject = makeJSONObject(resource);
			jsonArray.put(subJSONjObject);
		}
		return jsonArray;
	}
	//通过平面构造资源树
	private JSONArray makeJson() throws JSONException{
		JSONArray array=null;
		List<Resource> resources = resourceManageImpl.getAll();
		if(null!=resources&&resources.size()>0){
			array=new JSONArray();
			for (Resource resource : resources) {
				JSONObject object=new JSONObject();
				object.put("id", resource.getDbid());
				object.put("name", resource.getTitle());
				if (resource.getMenu() == 0) {
					if (resource.getParent() != null&&resource.getParent().getDbid()>0) {
						object.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/2.png");// 菜单阶段
						object.put("pId", resource.getParent().getDbid());
						object.put("open", true);
					} else {
						object.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/1_open.png");// 根节点
						object.put("root", "root");
						object.put("pId", 0);
						object.put("open", true);
					}
				}else if(resource.getMenu()==1){
					object.put("icon","/widgets/ztree/css/zTreeStyle/img/diy/3.png");// 菜单阶段
					object.put("pId", resource.getParent().getDbid());
					object.put("open", true);
				}else{
					object.put("pId", resource.getParent().getDbid());
				}
				
				array.put(object);
			}
		}
		return array;
	}
}
