package com.ystech.weixin.core.util;

import com.ystech.weixin.core.customerserivce.message.resp.ImageMessageResp;
import com.ystech.weixin.core.customerserivce.message.resp.MpnewsMessageResp;
import com.ystech.weixin.core.customerserivce.message.resp.MusicMessageResp;
import com.ystech.weixin.core.customerserivce.message.resp.NewsMessageResp;
import com.ystech.weixin.core.customerserivce.message.resp.TextMessageResp;
import com.ystech.weixin.core.customerserivce.message.resp.VideoMessageResp;
import com.ystech.weixin.core.customerserivce.message.resp.WxcardMessageResp;

import net.sf.json.JSONObject;

public class CustomerServiceMessageUtil {
	/**
	 * 功能描述：文本消息转换为json
	 * @param textMessageResp 文本对象
	 * @return
	 */
	public static String textMessageToJson(TextMessageResp textMessageResp){
    	String string = JSONObject.fromObject(textMessageResp).toString();
    	return string;
    }
	/**
	 * 功能描述：图片消息做完json
	 * @param imageMessageResp 图片对象
	 * @return
	 */
	public static String imageMessageToJson(ImageMessageResp imageMessageResp){
		String string = JSONObject.fromObject(imageMessageResp).toString();
		return string;
	}
	/**
	 * 功能描述：音乐消息做完json
	 * @param musicMessageResp 音乐对象
	 * @return
	 */
	public static String musicMessageToJson(MusicMessageResp musicMessageResp){
		String string = JSONObject.fromObject(musicMessageResp).toString();
		return string;
	}
	/**
	 * 功能描述：多图文消息转为json
	 * @param newsMessageResp 多图对象
	 * @return
	 */
	public static String newsMessageToJson(NewsMessageResp newsMessageResp){
		String news = JSONObject.fromObject(newsMessageResp).toString();
		return news;
	}
	/**
	 * 功能描述：语音文消息转为json
	 * @param videoMessageResp 语音对象
	 * @return
	 */
	public static String videoMessageToJson(VideoMessageResp videoMessageResp){
		String video = JSONObject.fromObject(videoMessageResp).toString();
		return video;
	}
	/**
	 * 功能描述：卡券文消息转为json
	 * @param wxcardMessageResp 卡券对象
	 * @return
	 */
	public static String wxcardMessageToJson(WxcardMessageResp wxcardMessageResp){
		String wxcard = JSONObject.fromObject(wxcardMessageResp).toString();
		return wxcard;
	}
	/**
	 * 功能描述：微信图文消息转为json
	 * @param mpnewsMessageResp 微信图文对象
	 * @return
	 */
	public static String mpnewsMessageToJson(MpnewsMessageResp mpnewsMessageResp){
		String mpnews = JSONObject.fromObject(mpnewsMessageResp).toString();
		return mpnews;
	}
	
}
