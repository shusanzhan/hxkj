package com.ystech.pllm.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ystech.core.util.DateUtil;
import com.ystech.core.util.FileNameUtil;
import com.ystech.core.util.LogUtil;
import com.ystech.core.util.Md5;
import com.ystech.core.util.PathUtil;
import com.ystech.mem.model.Member;
import com.ystech.pllm.model.Spread;
import com.ystech.pllm.model.SpreadDetail;
import com.ystech.pllm.model.SpreadGroup;
import com.ystech.sys.model.Enterprise;
import com.ystech.sys.service.EnterpriseManageImpl;
import com.ystech.weixin.core.util.WeixinUtil;
import com.ystech.weixin.model.WeixinAccesstoken;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.model.WeixinGzuserinfo;
import com.ystech.weixin.model.WeixinKeyWordRole;
import com.ystech.weixin.service.WeixinAccesstokenManageImpl;
import com.ystech.weixin.service.WeixinAccountManageImpl;
import com.ystech.weixin.service.WeixinKeyWordRoleManageImpl;

import net.sf.json.JSONObject;

@Component("spreadDetailUtil")
public class SpreadDetailUtil {
	private WeixinAccountManageImpl weixinAccountManageImpl;
	private SpreadManageImpl spreadManageImpl;
	private SpreadDetailManageImpl spreadDetailManageImpl;
	private SpreadGroupManageImpl  spreadGroupManageImpl;
	private WeixinAccesstokenManageImpl weixinAccesstokenManageImpl;
	private WeixinKeyWordRoleManageImpl weixinKeyWordRoleManageImpl;
	private EnterpriseManageImpl enterpriseManageImpl;
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}
	@Resource
	public void setSpreadManageImpl(SpreadManageImpl spreadManageImpl) {
		this.spreadManageImpl = spreadManageImpl;
	}
	@Resource
	public void setSpreadDetailManageImpl(
			SpreadDetailManageImpl spreadDetailManageImpl) {
		this.spreadDetailManageImpl = spreadDetailManageImpl;
	}
	@Resource
	public void setSpreadGroupManageImpl(SpreadGroupManageImpl spreadGroupManageImpl) {
		this.spreadGroupManageImpl = spreadGroupManageImpl;
	}
	@Resource
	public void setWeixinAccesstokenManageImpl(
			WeixinAccesstokenManageImpl weixinAccesstokenManageImpl) {
		this.weixinAccesstokenManageImpl = weixinAccesstokenManageImpl;
	}
	@Resource
	public void setWeixinKeyWordRoleManageImpl(
			WeixinKeyWordRoleManageImpl weixinKeyWordRoleManageImpl) {
		this.weixinKeyWordRoleManageImpl = weixinKeyWordRoleManageImpl;
	}
	@Resource
	public void setEnterpriseManageImpl(EnterpriseManageImpl enterpriseManageImpl) {
		this.enterpriseManageImpl = enterpriseManageImpl;
	}
	/**
	 * 功能描述： 
	 * 参数描述： 
	 * 逻辑描述：
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean saveSpreadDetail(Member member) throws Exception {
		try{
			WeixinGzuserinfo weixinGzuserinfo = member.getWeixinGzuserinfo();
			if(null==weixinGzuserinfo){
				LogUtil.info("二维码生成失败，会员关注信息为空。");
				return false;
			}
			List<SpreadDetail> spreadDetails = spreadDetailManageImpl.findBy("weixinGzuserinfo.dbid", weixinGzuserinfo.getDbid());
			if(null!=spreadDetails&&spreadDetails.size()>0){
				LogUtil.info("二维码生成失败，会员已生成专属二维码。");
				return false;
			}
			Integer enterpriseId = member.getEnterpriseId();
			if(null==enterpriseId||enterpriseId<=0){
				LogUtil.info("二维码生成失败，会员企业号信息为空。");
				return false;
			}
			Enterprise enterprise = enterpriseManageImpl.get(enterpriseId);
			List<WeixinAccount> weixinAccounts = weixinAccountManageImpl.findBy("enterpriseId", enterprise.getDbid());
			if(null==weixinAccounts||weixinAccounts.size()<=0){
				LogUtil.info("二维码生成失败，微信公众信息为空。");
				return false;
			}
			WeixinAccount weixinAccount = weixinAccounts.get(0);
			List<Spread> spreads = spreadManageImpl.find("from Spread where weixinAccountId=? AND name='"+Spread.COMMJJR+"'", new Object[]{weixinAccount.getDbid()});
			SpreadDetail spreadDetail=new SpreadDetail();
			if(null!=spreads&&spreads.size()>0){
				Spread spread = spreads.get(0);
				spreadDetail.setSpread(spread);
				List<SpreadGroup> spreadGroups = spreadGroupManageImpl.findBy("spread.dbid", spread.getDbid());
				if(null!=spreadGroups&&spreadGroups.size()>0){
					SpreadGroup spreadGroup = spreadGroups.get(0);
					spreadDetail.setSpreadGroup(spreadGroup);
				}
			}
			String formatFile = DateUtil.formatFile(new Date());
			String calcMD5 = Md5.calcMD5(formatFile);
			spreadDetail.setName(member.getName());
			spreadDetail.setNote("会员验证生成经纪人专属二维码");
			spreadDetail.setCreateDate(new Date());
			spreadDetail.setModifyDate(new Date());
			spreadDetail.setSpreadNum(0);
			spreadDetail.setScanNum(0);
			spreadDetail.setStatus(1);
			spreadDetail.setSceneStr(calcMD5);
			spreadDetail.setWeixinGzuserinfo(weixinGzuserinfo);
			spreadDetailManageImpl.save(spreadDetail);
			
			//设置回复规则
			WeixinKeyWordRole weixinKeyWordRole=new WeixinKeyWordRole();
			weixinKeyWordRole.setCreateDate(new Date());
			weixinKeyWordRole.setModifyDate(new Date());
			//参数二维码
			weixinKeyWordRole.setType(2);
			weixinKeyWordRole.setSpreadDetail(spreadDetail);
			weixinKeyWordRole.setName(spreadDetail.getName());
			weixinKeyWordRole.setAccountid(weixinAccount.getDbid());
			weixinKeyWordRoleManageImpl.save(weixinKeyWordRole);
			
			WeixinAccesstoken accessToken = WeixinUtil.getAccessToken(weixinAccesstokenManageImpl, weixinAccount);
			String createQrCodeUrl = WeixinUtil.CREATEQRCODE.replace("ACCESS_TOKEN", accessToken.getAccessToken());
			String json="{\"action_name\":\"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \""+calcMD5+"\"}}}";
			JSONObject jsonObject = WeixinUtil.httpRequest(createQrCodeUrl, "POST", json);
			if(null!=jsonObject){
				String ticket = jsonObject.getString("ticket");
				String ticketUrl = URLEncoder.encode(ticket, "UTF-8");
				String showCodeUrl = WeixinUtil.SHOWQRCODE.replace("TICKET", ticketUrl);
				File resourceFile = FileNameUtil.getResourceFile(spreadDetail.getSceneStr()+".jpg");
				saveUrlFile(showCodeUrl,resourceFile);
				String filePath = resourceFile.getAbsolutePath();
				String replace = filePath.replaceAll("\\\\", "/").replace(PathUtil.getWebRootPath(), "");
				spreadDetail.setTicket(ticket);
				spreadDetail.setQrcode(replace);
				spreadDetailManageImpl.save(spreadDetail);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//获取网络文件，转存到fileDes中，fileDes需要带文件后缀名    
    private static void saveUrlFile(String fileUrl,File toFile) throws Exception    
    {    
        if (toFile.exists())    
        {    
        	//throw new Exception("file exist");    
            return;    
        }    
        toFile.createNewFile();    
        FileOutputStream outImgStream = new FileOutputStream(toFile);    
        outImgStream.write(getUrlFileData(fileUrl));    
        outImgStream.close();    
    }
  //获取链接地址文件的byte数据    
    public static byte[] getUrlFileData(String fileUrl) throws Exception    
    {    
        URL url = new URL(fileUrl);    
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();    
        httpConn.connect();    
        InputStream cin = httpConn.getInputStream();    
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        while ((len = cin.read(buffer)) != -1) {    
            outStream.write(buffer, 0, len);    
        }    
        cin.close();    
        byte[] fileData = outStream.toByteArray();    
        outStream.close();    
        return fileData;    
    }
}
