package com.ystech.weixin.action;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ystech.core.dao.Page;
import com.ystech.core.security.SecurityUserHolder;
import com.ystech.core.util.Md5;
import com.ystech.core.util.ParamUtil;
import com.ystech.core.util.PathUtil;
import com.ystech.core.web.BaseController;
import com.ystech.sys.model.User;
import com.ystech.weixin.core.util.WeixinUploadMidea;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.KfAccount;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.service.KfAccountManageImpl;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;

import net.sf.json.JSONObject;

@Component("kfAccountAction")
@Scope("prototype")
public class KfAccountAction extends BaseController{
	private KfAccount kfAccount;
	private KfAccountManageImpl kfAccountManageImpl;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	public KfAccount getKfAccount() {
		return kfAccount;
	}
	public void setKfAccount(KfAccount kfAccount) {
		this.kfAccount = kfAccount;
	}
	@Resource
	public void setKfAccountManageImpl(KfAccountManageImpl kfAccountManageImpl) {
		this.kfAccountManageImpl = kfAccountManageImpl;
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
		try{
			 Page<KfAccount> page = kfAccountManageImpl.pagedQuerySql(pageNo, pageSize, KfAccount.class, "select * from weixin_KfAccount ",null);
			 request.setAttribute("page", page);
		}catch (Exception e) {
			e.printStackTrace();
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
		HttpServletRequest request = this.getRequest();
		try{
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			 request.setAttribute("weixinAccount", weixinAccount);
		}catch (Exception e) {
			// TODO: handle exception
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
	public String edit() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer dbid = ParamUtil.getIntParam(request, "dbid", -1);
		if(dbid>0){
			KfAccount kfAccount2 = kfAccountManageImpl.get(dbid);
			request.setAttribute("kfAccount", kfAccount2);
			
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			request.setAttribute("weixinAccount", weixinAccount);
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
		try{
		
			 boolean kfAccountAdd=false;
			 List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			 String kfAccount2 = kfAccount.getKfAccount();
			 kfAccount.setKfAccount(kfAccount2+"@"+weixinAccount.getAccountnumber());
			 kfAccount.setAccountid(weixinAccount.getDbid());
			 Integer dbid = kfAccount.getDbid();
			 //密码md5加密
			 if(null==dbid){
				 if(null!=kfAccount.getPassword()){
					 String calcMD5 = Md5.calcMD5(kfAccount.getPassword());
					 kfAccount.setPassword(calcMD5);
				 }
				 kfAccountManageImpl.save(kfAccount);
				 //更新数据到微信公众平台
				 kfAccountAdd = kfAccountAdd(kfAccount);
				 uploadImage(kfAccount);
			 }else{
				 KfAccount kfAccount3 = kfAccountManageImpl.get(dbid);
				 String password = kfAccount.getPassword();
				 if(!password.equals("123456")){
					 String calcMD5 = Md5.calcMD5(password);
					 kfAccount3.setPassword(calcMD5); 
				 }
				 kfAccount3.setHeadImg(kfAccount.getHeadImg());
				 kfAccount3.setNickname(kfAccount.getNickname());
				 kfAccountManageImpl.save(kfAccount3);
				 //更新客服资料到微信公众平台
				 kfAccountAdd = kfAccountUpdate(kfAccount3);
				 uploadImage(kfAccount3);
			 }
			 if(kfAccountAdd==true){
				 renderMsg("/kfAccount/queryList", "保存数据成功！");
			 }else{
				 renderMsg("/kfAccount/queryList", "保存数据成功,同步至微信公众平台发生错误！");
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
	 * 功能描述： 
	 * 参数描述：
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public void delete() throws Exception {
		HttpServletRequest request = this.getRequest();
		Integer[] dbids = ParamUtil.getIntArraryByDbids(request,"dbids");
		if(null!=dbids&&dbids.length>0){
			try {
				for (Integer dbid : dbids) {
					KfAccount kfAccount2 = kfAccountManageImpl.get(dbid);
					kfAccountManageImpl.deleteById(dbid);
					boolean kfAccountDelete = kfAccountDelete(kfAccount2);
					 if(kfAccountDelete==true){
						 renderMsg("/kfAccount/queryList", "删除数据成功！");
						 return ;
					 }else{
						 renderMsg("/kfAccount/queryList", "删除数据成功,同步至微信公众平台发生错误！");
						 return ;
					 }
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
		renderMsg("/kfAccount/queryList"+query, "删除数据成功！");
		return;
	}
	/**
	 * 功能描述：同步客户信息至微信公众平台
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public boolean kfAccountAdd(KfAccount kfAccount) throws Exception {
		try {
			JSONObject fromObject = JSONObject.fromObject(kfAccount);
			String targetJson = fromObject.toString().replace("kfAccount", "kf_account");
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			if(null==weixinAccount){
				User user = SecurityUserHolder.getCurrentUser();
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
			String kfaccountAdd = WeixinUtil.KFACCOUNTADD.replace("ACCESS_TOKEN", accessToken.getAccessToken());
			JSONObject jsonObject = WeixinUtil.httpRequest(kfaccountAdd,"POST", targetJson);
			System.err.println("======"+jsonObject);
			if(null!=jsonObject){
				String errcode = jsonObject.getString("errcode");
				if(errcode.equals("0")){
					return true;
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
	/**
	 * 功能描述：同步客户信息至微信公众平台
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public boolean kfAccountUpdate(KfAccount kfAccount) throws Exception {
		try {
			JSONObject fromObject = JSONObject.fromObject(kfAccount);
			String targetJson = fromObject.toString().replace("kfAccount", "kf_account");
			System.out.println(targetJson);
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			if(null==weixinAccount){
				User user = SecurityUserHolder.getCurrentUser();
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
			String kfaccountAdd = WeixinUtil.KFACCOUNTUPDATE.replace("ACCESS_TOKEN", accessToken.getAccessToken());
			JSONObject jsonObject = WeixinUtil.httpRequest(kfaccountAdd,"POST", targetJson);
			System.err.println("========="+jsonObject.toString());
			if(null!=jsonObject){
				String errcode = jsonObject.getString("errcode");
				if(errcode.equals("0")){
					return true;
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
	/**
	 * 功能描述：删除客户信息至微信公众平台
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public boolean kfAccountDelete(KfAccount kfAccount) throws Exception {
		try {
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			if(null==weixinAccount){
				User user = SecurityUserHolder.getCurrentUser();
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
			String kfaccountDelete = WeixinUtil.KFACCOUNTDELETE.replace("ACCESS_TOKEN", accessToken.getAccessToken()).replace("KFACCOUNT", kfAccount.getKfAccount());
			JSONObject jsonObject = WeixinUtil.httpRequest(kfaccountDelete,"GET", null);
			if(null!=jsonObject){
				String errcode = jsonObject.getString("errcode");
				if(errcode.equals("0")){
					return true;
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return false;
	}
	/**
	 * 功能描述：上传客服头像
	 * 参数描述：
	 * 逻辑描述：
	 * @return
	 * @throws Exception
	 */
	public void uploadImage(KfAccount kfAccount) throws Exception {
		try {
			 if(null!=kfAccount.getHeadImg()&&kfAccount.getHeadImg().trim().length()>0){
				 List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
				WeixinAccount weixinAccount=null;
				if(null!=weixinAccounts){
					weixinAccount = weixinAccounts.get(0);
				}
				 if(null==weixinAccount){
					User user = SecurityUserHolder.getCurrentUser();
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
				 String headImg = kfAccount.getHeadImg();
				 String webRootPath = PathUtil.getWebRootPath();
				 File fileMedia=new File(webRootPath+headImg);
				 JSONObject kfAccountUploadImage = WeixinUploadMidea.kfAccountUploadImage(accessToken.getAccessToken(), fileMedia, kfAccount.getKfAccount());
				 System.out.println("============="+kfAccountUploadImage.toString());
			 }
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return;
	}
	/**
	 * 验证客户是否存在系统
	 */
	public void validateKfAccount() {
		HttpServletRequest request = this.getRequest();
		try {
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.getAll();
			WeixinAccount weixinAccount=null;
			if(null!=weixinAccounts){
				weixinAccount = weixinAccounts.get(0);
			}
			String kfAccount =request.getParameter("kfAccount.kfAccount");
			List<KfAccount> kfAccounts=null;
			if(null!=kfAccount&&kfAccount.trim().length()>0){
				kfAccount=kfAccount+"@"+weixinAccount.getAccountnumber();
				kfAccounts = kfAccountManageImpl.findBy("kfAccount", kfAccount);
			}else{
				renderText("系统已经存在该工号了!请换一个工号!");//输入的账户类型不匹配！
				return ;
			}
			
			if (null!=kfAccounts&&kfAccounts.size()>0) {
				renderText("系统已经存在该工号了!请换一个工号!");//已经注册，请直接登录！
			}else{
				renderText("success");//
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
