package com.ystech.weixin.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.DatabaseUnitHelper;
import com.ystech.core.util.LogUtil;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.web.BaseController;
import com.ystech.mem.model.MemberShipLevel;
import com.ystech.mem.service.MemberShipLevelManagImpl;
import com.ystech.sys.model.Enterprise;
import com.ystech.weixin.core.util.Configure;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinGroup;
import com.ystech.weixin.model.WeixinGzuserinfo;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinGroupManageImpl;
import com.ystech.weixin.service.WeixinGzuserinfoManageImpl;

import net.sf.json.JSONObject;

@Component("weixinGzUserInfoAction")
@Scope("prototype")
public class WeixinGzUserInfoAction extends BaseController{
	private WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinGzuserinfo weixinGzuserinfo;
	private WeixinGroupManageImpl weixinGroupManageImpl;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	private MemberShipLevelManagImpl memberShipLevelManagImpl;
	
	private boolean status=true;
	public WeixinGzuserinfo getWeixinGzuserinfo() {
		return weixinGzuserinfo;
	}
	public void setWeixinGzuserinfo(WeixinGzuserinfo weixinGzuserinfo) {
		this.weixinGzuserinfo = weixinGzuserinfo;
	}
	@Resource
	public void setWeixinGzuserinfoManageImpl(
			WeixinGzuserinfoManageImpl weixinGzuserinfoManageImpl) {
		this.weixinGzuserinfoManageImpl = weixinGzuserinfoManageImpl;
	}
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	@Resource
	public void setMemberShipLevelManagImpl(MemberShipLevelManagImpl memberShipLevelManagImpl) {
		this.memberShipLevelManagImpl = memberShipLevelManagImpl;
	}
	@Resource
	public void setWeixinGroupManageImpl(WeixinGroupManageImpl weixinGroupManageImpl) {
		this.weixinGroupManageImpl = weixinGroupManageImpl;
	}
	@Resource
	public void setWeixinAccesstokenManageImpl(
			WeixinAccesstokenManageImpl weixinAccesstokenManageImpl) {
		this.weixinAccesstokenManageImpl = weixinAccesstokenManageImpl;
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
		Integer pageSize = ParamUtil.getIntParam(request, "pageSize", 50);
		Integer pageNo = ParamUtil.getIntParam(request, "currentPage", 1);
		//关注状态
		String eventStatus = request.getParameter("eventStatus");
		//男女
		String gender = request.getParameter("gender");
		//购买次数
		String totalBuy = request.getParameter("totalBuy");
		//昵称
		String key = request.getParameter("key");
		//积分区间
		String points = request.getParameter("points");
		//会员等级Ids
		String level = request.getParameter("level");
		//标签IDs
		String tag = request.getParameter("tag");
		//关注时间
		String gzTime = request.getParameter("gzTime");
		//最近消费时间
		String latestConsume = request.getParameter("latestConsume");
		//商品均价区间
		String average = request.getParameter("average");
		//区域
		String loc = request.getParameter("loc");
		
		try{
			 String selectParam=""; 
			// 会员等级
			 List<MemberShipLevel> memberShipLevels = memberShipLevelManagImpl.getAll();
			 request.setAttribute("memberShipLevels", memberShipLevels);
			 
			 List params=new ArrayList();
			 Enterprise enterprise = SecurityUserHolder.getEnterprise();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null!=weixinAccounts&&weixinAccounts.size()>0){
				WeixinAccount weixinAccount = weixinAccounts.get(0);
				 String sql="select * from Weixin_Gzuserinfo gzu,mem_member memb where gzu.dbid=memb.weixinGzuserinfoId and gzu.accountid="+weixinAccount.getDbid();
				 String sql2="select memb.dbid as dbid from Weixin_Gzuserinfo gzu,mem_member memb where gzu.dbid=memb.weixinGzuserinfoId and gzu.accountid="+weixinAccount.getDbid();
				 if(null!=key&&key.trim().length()>0){
					 sql=sql+" and gzu.nickname like ? ";
					 sql2=sql2+" and gzu.nickname like '%"+key+"%' ";
					 params.add("%"+key+"%");
					 selectParam=selectParam+"关键词（"+key+"）,";
				 }
				if(null!=eventStatus&&!eventStatus.equals("-1m")&&eventStatus.trim().length()>0){
					sql=sql+" and gzu.eventStatus=?";
					sql2=sql2+" and gzu.eventStatus="+eventStatus;
				    params.add(eventStatus);
				    if(eventStatus.equals("2")){
				    	selectParam=selectParam+"账户（已跑路）,";
				    }
				    if(eventStatus.equals("1")){
				    	selectParam=selectParam+"账户（已关注）,";
				    }
				}
				if(null!=gender&&!gender.equals("-1m")&&gender.trim().length()>0){
					sql=sql+" and gzu.sex=?";
					sql2=sql2+" and gzu.sex="+gender;
					params.add(gender);
					if(eventStatus.equals("1")){
				    	selectParam=selectParam+"性别（男）,";
				    }
				    if(eventStatus.equals("2")){
				    	selectParam=selectParam+"性别（女）,";
				    }
				}
				if(null!=points&&!points.equals("-1m")&&points.trim().length()>0){
					sql=sql+getPointSql(points);
					sql2=sql2+getPointSql(points);
				    selectParam=selectParam+"本店积分（"+points+"）,";
				}
				if(null!=level&&level.trim().length()>0){
					if(!level.equals("ml")){
						level=level.substring(0, level.length()-1);
						sql=sql+" and memb.memberShipLevelId in("+level+")";
						sql2=sql2+" and memb.memberShipLevelId in("+level+")";
						//构造发送参数
						List<MemberShipLevel> memberShipLevels2 = memberShipLevelManagImpl.executeSql("select * from mem_membershiplevel where dbid in("+level+")", null);
						for (MemberShipLevel memberShipLevel : memberShipLevels2) {
							selectParam=selectParam+"会员卡等级（"+memberShipLevel.getName()+"）,";
						}
					}
				}
				if(null!=tag&&tag.trim().length()>0){
					if(!tag.equals("tm")){
						sql=sql+" and memb.memTagIds like ? ";
						sql2=sql2+" and memb.memTagIds like '%"+tag+",%' ";
						params.add("%"+tag+",%");
						selectParam=selectParam+"会员标签（"+tag+"）,";
					}
				}
				//购买次数
				if(null!=totalBuy&&totalBuy.trim().length()>0){
					if(!totalBuy.equals("gm")){
						String toString=getTotalBuySql(totalBuy);
						sql=sql+toString;
						sql2=sql2+toString;
						
						selectParam=selectParam+"购买次数（"+tag+"）,";
					}
				}
				//商品均价
				if(null!=average&&average.trim().length()>0){
					if(!average.equals("gm")){
						String averageSql=getAverageSql(average);
						sql=sql+averageSql;
						sql2=sql2+averageSql;
						
						selectParam=selectParam+"商品均价（"+average+"）,";
					}
				}
				if(null!=gzTime&&!gzTime.equals("gm")&&gzTime.trim().length()>0){
					String gzTimeSql = getGzTimeSql(gzTime);
					sql=sql+gzTimeSql;
					sql2=sql2+gzTimeSql;
					
					selectParam=selectParam+"关注时间（"+gzTime+"）,";
				}
				if(null!=latestConsume&&!latestConsume.equals("gm")&&latestConsume.trim().length()>0){
					String lastConsumeSql = getLastConsumeSql(latestConsume);
					sql=sql+lastConsumeSql;
					sql2=sql2+lastConsumeSql;
					
					selectParam=selectParam+"最近消费（"+latestConsume+"）,";
				}
				if(null!=loc&&!loc.equals("m1")&&loc.trim().length()>0){
					String locSql =getLocSql(loc);
					sql=sql+locSql;
					sql2=sql2+locSql;
					
					String[] split = loc.split(",");
					request.setAttribute("locParams", split);
					for (String object : split) {
						selectParam=selectParam+"地域（"+object+"）,";
					}
				}
				sql=sql+" order by addtime DESC ";
				Page<WeixinGzuserinfo> page= weixinGzuserinfoManageImpl.pagedQuerySql(pageNo, pageSize,WeixinGzuserinfo.class,sql, params.toArray());
				request.setAttribute("page", page);
				
				List<WeixinGroup> totalWeixinGroups = weixinGroupManageImpl.getTotalWeixinGroups();
				request.setAttribute("totalWeixinGroups", totalWeixinGroups);
				
				//前台使用会员ID
				String memberIds = weixinGzuserinfoManageImpl.getmemberIds(sql2);
				if(memberIds.trim().length()>0){
					memberIds=memberIds.substring(0, memberIds.length()-1);
					request.setAttribute("memberIds", memberIds);
				}
				//构造发送信息查询条件
				if(selectParam.trim().length()>0){
					selectParam=selectParam.substring(0, selectParam.length()-1);
					request.setAttribute("selectParam", selectParam);
				}
			}
		}catch(Exception e){
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
	public String sendMessage() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer type = ParamUtil.getIntParam(request, "type", 1);
		String memberIds = request.getParameter("qmemberIds");
		String selectParam = request.getParameter("searchparams");
		try {
			String countSql="select count(*) "
					+ "from "
					+ "Weixin_Gzuserinfo gzu,mem_member memb "
					+ "where gzu.dbid=memb.weixinGzuserinfoId and gzu.eventStatus=1 ";
			//关注人员总数
			long  totalNum= weixinGzuserinfoManageImpl.countSqlResult(countSql, null);
			if(null!=memberIds&&memberIds.trim().length()>0){
				String sql="select gzu.dbid as dbid,gzu.nickname,gzu.openid "
						+ "from "
						+ "Weixin_Gzuserinfo gzu,mem_member memb "
						+ "where gzu.dbid=memb.weixinGzuserinfoId 	 and memb.dbid in("+memberIds+") and gzu.eventStatus=1";
				List<WeixinGzuserinfo> weixinGzuserinfos = weixinGzuserinfoManageImpl.getWeixinGzUser(sql);
				request.setAttribute("weixinGzuserinfos", weixinGzuserinfos);
				StringBuffer openIds=new StringBuffer();
				for (WeixinGzuserinfo weixinGzuserinfo : weixinGzuserinfos) {
					openIds.append(weixinGzuserinfo.getOpenid()+",");
				}
				request.setAttribute("openIds", openIds.toString());
				
				if(totalNum>weixinGzuserinfos.size()){
					request.setAttribute("all", false);
				}else{
					request.setAttribute("all", true);
				}
			}
			if(type==2){
				if(null!=selectParam&&selectParam.trim().length()>0){
					String[] split = selectParam.split(",");
					request.setAttribute("selectParams", split);
				}
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "sendMessage";
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
	public String view() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			WeixinGzuserinfo weixinGzuserinfo2 = weixinGzuserinfoManageImpl.get(dbid);
			request.setAttribute("weixinGzUserInfo", weixinGzuserinfo2);
		}
		return "view";
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
		if(dbid>0){
			WeixinGzuserinfo weixinGzuserinfo2 = weixinGzuserinfoManageImpl.get(dbid);
			request.setAttribute("weixinGzuserinfo", weixinGzuserinfo2);
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
		Integer parentMenu = ParamUtil.getIntParam(request, "parentMenu", 5);
		try{
			/*Integer dbid = slide.getDbid();
			if(dbid==null||dbid<=0){
				slide.setCreateTime(new Date());
				slide.setModifyTime(new Date());
				slideManageImpl.save(slide);
			}else{
				slideManageImpl.save(slide);
			}*/
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			renderErrorMsg(e, "");
			return ;
		}
		renderMsg("/slide/queryList?parentMenu="+parentMenu, "保存数据成功！");
		return ;
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
		Integer parentMenu = ParamUtil.getIntParam(request, "parentMenu", 5);
		if(null!=dbids&&dbids.length>0){
			try {
				for (Integer dbid : dbids) {
					weixinGzuserinfoManageImpl.deleteById(dbid);
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
		renderMsg("/weixinGzuserinfo/queryList"+query+"&parentMenu="+parentMenu, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void updateGroup() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer groupid = ParamUtil.getIntParam(request, "groupId", -1);
		Integer userInfoId = ParamUtil.getIntParam(request, "userInfoId", -1);
		try {
				WeixinGzuserinfo weixinGzuserinfo2 = weixinGzuserinfoManageImpl.get(userInfoId);
				if(null==weixinGzuserinfo2){
					 renderErrorMsg(new Throwable("同步组错误，关注用户信息为空！"), "");
					 return ;
				}
				WeixinGroup weixinGroup = weixinGroupManageImpl.get(groupid);
				if(null==weixinGroup){
					 renderErrorMsg(new Throwable("同步组错误，用户组信息为空！"), "");
					 return ;
				}
				weixinGzuserinfo2.setGroupId(weixinGroup.getWechatGroupId());
				weixinGzuserinfoManageImpl.save(weixinGzuserinfo2);
				
				List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
				WeixinAccount weixinAccount=null;
				if(null!=weixinAccounts){
					weixinAccount = weixinAccounts.get(0);
				}
				WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
				 String createApi = Configure.GROUPS_MEMEBERS_API.replace("ACCESS_TOKEN", accessToken.getAccessToken());
				 String outputStr="{\"openid\":\""+weixinGzuserinfo2.getOpenid()+"\",\"to_groupid\":"+weixinGroup.getWechatGroupId()+"}}";
				 JSONObject jsonObject = WeixinUtil.httpRequest(createApi, "POST", outputStr);
				 if(null!=jsonObject){
					 String result = jsonObject.toString();
					 if(result.contains("ok")){
						 renderErrorMsg(new Throwable("更新数据成功!"), "");
						 return ;
					 }else{
						 renderErrorMsg(new Throwable("同步组错误，远程微信服务端返回结果为空！"), "");
						 return ;
					 }
				 }else{
					 renderErrorMsg(new Throwable("同步组错误，远程微信服务端返回结果为空！"), "");
					 return;
				 } 
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(new Throwable("同步组错误，系统抛出异常"), "");
		}
		return;
	}
	/**
	 * 功能描述：批量移动微信用户到用户组
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void updateGroupBatch() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer groupid = ParamUtil.getIntParam(request, "groupId", -1);
		Integer[] userInfoIds = ParamUtil.getIntArrayByIds(request, "userInfoIds");
		try {
			if(null==userInfoIds&&userInfoIds.length<=0){
				 renderErrorMsg(new Throwable("同步组错误，关注用户信息为空！"), "");
				 return ;
			}
			if(userInfoIds.length>50){
				renderErrorMsg(new Throwable("同步组错误，粉丝数量不能超过50！"), "");
				 return ;
			}
			WeixinGroup weixinGroup = weixinGroupManageImpl.get(groupid);
			if(null==weixinGroup){
				 renderErrorMsg(new Throwable("同步组错误，用户组信息为空！"), "");
				 return ;
			}
			StringBuffer openid_list=new StringBuffer();
			for (Integer dbid : userInfoIds) {
				WeixinGzuserinfo weixinGzuserinfo2 = weixinGzuserinfoManageImpl.get(dbid);
				weixinGzuserinfo2.setGroupId(weixinGroup.getWechatGroupId());
				weixinGzuserinfoManageImpl.save(weixinGzuserinfo2);
				openid_list.append("\""+weixinGzuserinfo2.getOpenid()+"\",");
			}
			String openid_list_string = openid_list.toString();
			if(openid_list_string.length()<=0){
				 renderErrorMsg(new Throwable("同步组错误，关注用户信息为空！"), "");
				 return ;
			}
			openid_list_string=openid_list_string.substring(0, openid_list_string.length()-1);
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			 String createApi = Configure.GROUPS_MEMEBERS_BATCH_API.replace("ACCESS_TOKEN", accessToken.getAccessToken());
			 String outputStr="{\"openid_list\":["+openid_list_string+"],\"to_groupid\":"+weixinGroup.getWechatGroupId()+"}";
			 JSONObject jsonObject = WeixinUtil.httpRequest(createApi, "POST", outputStr);
			 if(null!=jsonObject){
				 String result = jsonObject.toString();
				 if(result.contains("ok")){
					 renderErrorMsg(new Throwable("更新数据成功!"), "");
					 return ;
				 }else{
					 renderErrorMsg(new Throwable("同步组错误，远程微信服务端返回结果为空！"), "");
					 return ;
				 }
			 }else{
				 renderErrorMsg(new Throwable("同步组错误，远程微信服务端返回结果为空！"), "");
				 return;
			 } 
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(new Throwable("同步组错误，系统抛出异常"), "");
		}
		return;
	}
	
	/**
	 * 功能描述：同步关注用户
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void sysBathGzuserinfo() throws Exception {
		try {
			Map<String, String> maps = getOpenIdMap();
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			int i=0;
			
			sysGzuserInfo(maps, weixinAccount, accessToken,"",i,status);
			if(status==true){
				renderMsg("", "同步数据成功！");
				return ;
			}else{
				renderErrorMsg(new Throwable("同步数据失败"), "");
				return ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			renderErrorMsg(e, "");
			return ;
		}
	}
	private void sysGzuserInfo(Map<String, String> maps,WeixinAccount weixinAccount, WeixinAccesstoken accessToken,String nextOpenId,int i,Boolean status) {
		i=i+1;
		String usergets="";
		if(null!=nextOpenId&&nextOpenId.trim().length()>0){
			usergets= WeixinUtil.usergets.replace("ACCESS_TOKEN",accessToken.getAccessToken()).replace("NEXT_OPENID",nextOpenId);
		}else{
			usergets= WeixinUtil.usergets.replace("ACCESS_TOKEN",accessToken.getAccessToken()).replace("NEXT_OPENID", "");
		}
		JSONObject object = WeixinUtil.httpRequest(usergets, "GET", "");
		if(null!=object){
			if(object.toString().contains("invalid")){
				LogUtil.error(object.toString());
				status=false;
				return ;
			}else{
				String total = object.getString("total");
				String count = object.getString("count");
				String openids = object.getJSONObject("data").getString("openid");
				if(null!=openids){
					openids=openids.replace("[", "").replace("]", "");
					String[] openIds = openids.split(",");
					for (String openId : openIds) {
						String opId = openId.replace("\"", "");
						String map = maps.get(opId);
						if(null==map||map.trim().length()<=0){
							WeixinGzuserinfo saveGzuserinfo = weixinGzuserinfoManageImpl.saveGzuserinfo(opId, accessToken.getAccessToken(),weixinAccount.getDbid());
							//对关注微信用户创建会员信息
							//memberGzUserUtil.saveMember(saveGzuserinfo);
						}
					}
				}
				LogUtil.error("第"+i+"拉取关注用户，总用户："+total+",拉取格式："+count+",nextOpenId "+nextOpenId);
				i=i+1;
				String next_openid = object.getString("next_openid");
				if(null!=next_openid&&next_openid.trim().length()>0&&count.equals("10000")){
					sysGzuserInfo(maps, weixinAccount, accessToken, nextOpenId,i,status);
				}else{
					return ;
				}
			}
		}
	}
	/**
	 * 功能描述：群发给所用人员时，获取所有人员的OPenId
	 * @return
	 */
	public Map<String, String> getOpenIdMap(){
		Map<String, String> maps=new HashMap<String, String>();
		try {
			String sql="select openId from weixin_gzuserinfo where eventStatus=1";
			DatabaseUnitHelper databaseUnitHelper = new DatabaseUnitHelper();
			Connection jdbcConnection = databaseUnitHelper.getJdbcConnection();
			PreparedStatement createStatement = jdbcConnection.prepareStatement(sql);
			ResultSet resultSet = createStatement.executeQuery();
			if(null==resultSet){
				return null;
			}
			while (resultSet.next()) {
				String openId=resultSet.getString("openid");
				maps.put(openId, openId);
			}
			resultSet.close();
			createStatement.close();
			jdbcConnection.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return maps;
	}
	/**
	 * 积分sql构造
	 * @param points
	 * @return
	 */
	private String getPointSql(String points){
		HttpServletRequest request = this.getRequest();
		String sql="";
		if(points.contains("+")){
			points=points.replace("+", "");
			if(points.trim().length()>0){
				int begin = Integer.parseInt(points);
				sql=sql+" and memb.totalPoint>="+begin;
			}
		}
		
		if(points.contains("-")){
			String[] split = points.split("-");
			if(split.length>1){
				String beginS=split[0];
				String endS=split[1];
				if(null!=beginS&&beginS.trim().length()>0){
					int begin = Integer.parseInt(beginS);
					sql=sql+" and memb.totalPoint>="+begin;
				}
				if(null!=endS&&endS.trim().length()>0){
					int end = Integer.parseInt(endS);
					sql=sql+" and memb.totalPoint<="+end;
				}
			}
			if(split.length==1){
				String beginS=split[0];
				if(null!=beginS&&beginS.trim().length()>0){
					int begin = Integer.parseInt(beginS);
					sql=sql+" and memb.totalPoint<="+begin;
				}
			}
		}
		if(!points.equals("-1")&&!points.equals("0-100")&&!points.equals("101-200")&&!points.equals("201-500")&&!points.equals("501-1000")&&!points.equals("1000+")){
			request.setAttribute("pointStatus", true);
		}
		return sql;
	}
	/**
	 * 关注时间 sql构造
	 * @param points
	 * @return
	 */
	private String getGzTimeSql(String gzTime){
		HttpServletRequest request = getRequest();
		String sql="";
		if(gzTime.equals("1w")){
			sql=" and TO_DAYS(NOW()) - TO_DAYS(gzu.addtime) <7 ";
		}
		if(gzTime.equals("2w")){
			sql=" and TO_DAYS(NOW()) - TO_DAYS(gzu.addtime) <14 ";
		}
		if(gzTime.equals("1m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date(gzu.addtime) "; 
		}
		if(gzTime.equals("2m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 2 MONTH) <= date(gzu.addtime) "; 
		}
		if(gzTime.equals("3m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 3 MONTH) <= date(gzu.addtime) "; 
		}
		if(gzTime.equals("+6m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 6 MONTH) <= date(gzu.addtime) "; 
		}
		if(gzTime.equals("-6m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 6 MONTH) >= date(gzu.addtime) "; 
		}
		if(gzTime.contains("到")){
			String[] split = gzTime.split("到");
			sql= " and gzu.addtime>='"+split[0]+"' and gzu.addtime<='"+split[1]+"' ";
			request.setAttribute("gzTimeStatus", true);
		}
		if(gzTime.contains("内")){
			gzTime=gzTime.replace("内", "");
			sql= " and gzu.addtime<='"+gzTime+"' ";
			request.setAttribute("gzTimeStatus", true);
		}
		if(gzTime.contains("后")){
			gzTime=gzTime.replace("后", "");
			sql= " and gzu.addtime>='"+gzTime+"' ";
			request.setAttribute("gzTimeStatus", true);
		}
		return sql;
	}
	/**
	 * 消费时间 sql构造
	 * @param points
	 * @return
	 */
	private String getLastConsumeSql(String lastConsume){
		HttpServletRequest request = getRequest();
		String sql="";
		if(lastConsume.equals("1w")){
			sql=" and TO_DAYS(NOW()) - TO_DAYS(memb.lastBuyDate) <7 ";
		}
		if(lastConsume.equals("2w")){
			sql=" and TO_DAYS(NOW()) - TO_DAYS(memb.lastBuyDate) <14 ";
		}
		if(lastConsume.equals("+1m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date(memb.lastBuyDate) "; 
		}
		if(lastConsume.equals("-1m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 1 MONTH) >= date(gzu.addtime) "; 
		}
		if(lastConsume.equals("-2m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 2 MONTH) >= date(memb.lastBuyDate) "; 
		}
		if(lastConsume.equals("-3m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 3 MONTH) >= date(memb.lastBuyDate) "; 
		}
		if(lastConsume.equals("-6m")){
			sql= " and DATE_SUB(CURDATE(), INTERVAL 6 MONTH) >= date(memb.lastBuyDate) "; 
		}
		if(lastConsume.contains("到")){
			String[] split = lastConsume.split("到");
			sql= " and memb.lastBuyDate>='"+split[0]+"' and memb.lastBuyDate<='"+split[1]+"' ";
			request.setAttribute("lastConsumeStatus", true);
		}
		if(lastConsume.contains("内")){
			lastConsume=lastConsume.replace("内", "");
			sql= " and memb.lastBuyDate<='"+lastConsume+"' ";
			request.setAttribute("lastConsumeStatus", true);
		}
		if(lastConsume.contains("后")){
			lastConsume=lastConsume.replace("后", "");
			sql= " and memb.lastBuyDate>='"+lastConsume+"' ";
			request.setAttribute("lastConsumeStatus", true);
		}
		return sql;
	}
	/**
	 * 获取购买次数sql
	 * @param totalBuy
	 * @return
	 */
	private String getTotalBuySql(String totalBuy) {
		HttpServletRequest request = this.getRequest();
		String sql="";
		if(totalBuy.contains("+")){
			if(!totalBuy.equals("1+")||!totalBuy.equals("2+")||!totalBuy.equals("3+")||!totalBuy.equals("4+")||!totalBuy.equals("5+")
					||!totalBuy.equals("10+")||!totalBuy.equals("15+")||!totalBuy.equals("30+")||!totalBuy.equals("50+")){
				request.setAttribute("totalBuyStatus", true);
			}
			totalBuy = totalBuy.replace("+", "");
			int parseInt = Integer.parseInt(totalBuy);
			sql=sql+" and memb.totalBuy>="+parseInt;
		}
		if(totalBuy.contains("-")){
			String[] split = totalBuy.split("-");
			if(split.length==1){
				String beginStr = split[0];
				if(null!=beginStr&&beginStr.trim().length()>0){
					int begin=	Integer.parseInt(beginStr);
					sql=sql+" and memb.totalBuy<="+begin;
				}
			}
			if(split.length==2){
				String beginStr = split[0];
				String endStr = split[1];
				if(null!=beginStr&&beginStr.trim().length()>0){
					int begin=	Integer.parseInt(beginStr);
					sql=sql+" and memb.totalBuy>="+begin;
				}
				if(null!=endStr&&endStr.trim().length()>0){
					int end=	Integer.parseInt(endStr);
					sql=sql+" and memb.totalBuy<="+end;
				}
			}
			request.setAttribute("totalBuyStatus", true);
		}
		return sql;
	}
	/**
	 * 评价购买价格
	 * @param average
	 * @return
	 */
	private String getAverageSql(String average) {
		HttpServletRequest request = getRequest();
		String sql="";
		if(average.contains("-")){
			String ava=average.replace("-", "");
			if(!ava.equals("50")||!ava.equals("5080")||!ava.equals("80150")||!ava.equals("150200")||
					!ava.equals("200300")||!ava.equals("300500")||!ava.equals("5001000")){
				request.setAttribute("averageStatus", true);
			}
			if(ava.equals("50")){
				System.err.println("========"+average);
			}
			String[] split = average.split("-");
			if(split.length==1){
				String beginStr = split[0];
				if(null!=beginStr&&beginStr.trim().length()>0){
					int begin=	Integer.parseInt(beginStr);
					sql=sql+" and memb.average<="+begin;
				}
			}
			if(split.length==2){
				String beginStr = split[0];
				String endStr = split[1];
				if(null!=beginStr&&beginStr.trim().length()>0){
					int begin=	Integer.parseInt(beginStr);
					sql=sql+" and memb.average>="+begin;
				}
				if(null!=endStr&&endStr.trim().length()>0){
					int end=	Integer.parseInt(endStr);
					sql=sql+" and memb.average<="+end;
				}
			}
		}
		if(average.contains("+")){
			average = average.replace("+", "");
			int parseInt = Integer.parseInt(average);
			sql=sql+" and memb.average>="+parseInt;
			if(!average.equals("1000+")){
				request.setAttribute("averageStatus", true);
			}
		}
		return sql;
	}
	/**
	 * 区域查询
	 * @param loc
	 * @return
	 */
	private String getLocSql(String loc) {
		String sql="";
		if(loc.contains("江浙沪")){
			loc=loc+",浙江,江苏,上海,";
		}
		if(loc.contains("珠三角")){
			loc=loc+",广州,深圳,珠海,佛山,江门,东莞,中山,惠州市,肇庆市,";
		}
		if(loc.contains("港澳台")){
			loc=loc+",香港,澳门,台湾,";
		}
		if(loc.contains("京津")){
			loc=loc+",北京,天津,";
		}
		sql=sql+" AND city!='' AND province!='' AND (LOCATE(province,'"+loc+"')>0 or LOCATE(city,'"+loc+"')>0)";
		return sql;
	}
}
