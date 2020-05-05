package com.ystech.weixin.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ystech.core.util.ParamUtil;
import com.ystech.weixin.core.util.SignUtil;
import com.ystech.weixin.model.WeixinAccount;
import com.ystech.weixin.service.WechatService;
import com.ystech.weixin.service.WeixinAccountManageImpl;


/**
 * 核心请求处理类
 * 
 * @author 
 * @date 2013-05-18
 */
public class WeixinServlet extends HttpServlet {
	private static final long serialVersionUID = 4440739483644821986L;
	private WeixinAccountManageImpl weixinAccountManageImpl;
	@Resource
	public void setWeixinAccountManageImpl(
			WeixinAccountManageImpl weixinAccountManageImpl) {
		this.weixinAccountManageImpl = weixinAccountManageImpl;
	}

	/**
	 * 确认请求来自微信服务器
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		Integer code = ParamUtil.getIntParam(request, "code",-1);
		if(null!=code&&code>0){
			PrintWriter out = response.getWriter();
			WeixinAccount weixinAccount = weixinAccountManageImpl.get(code);
			// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
			if (SignUtil.checkSignature(weixinAccount.getAccounttoken(), signature,
					timestamp, nonce)) {
				out.print(echostr);
			}
			out.close();
			out = null;
		}
		
	}

	/**
	 * 处理微信服务器发来的消息
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		WechatService wechatService = new WechatService();
		// 调用核心业务类接收消息、处理消息
		String respMessage = wechatService.coreService(request);
		// 响应消息
		PrintWriter out = response.getWriter();
		out.print(respMessage);
		out.close();
	}

}
