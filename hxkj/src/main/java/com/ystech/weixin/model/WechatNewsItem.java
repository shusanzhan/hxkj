package com.ystech.weixin.model;

// Generated 2015-9-9 17:17:16 by Hibernate Tools 4.0.0

/**
 * WeixinWechatnewsitem generated by hbm2java
 */
public class WechatNewsItem implements java.io.Serializable {

	private Integer dbid;
	//图文封面 素材Id
	private String thumb_media_id;
	//标题
	private String title;
	//作者
	private String author;
	//原文链接
	private String content_source_url;
	//内容
	private String content;
	//描述
	private String digest;
	//是否在文章显示图文封面图片 ，0为false，即不显示，1为true，即显示
	private Integer show_cover_pic;
	//微信端ID
	private String thumbWechatUrl;
	//本地上传图片
	private String thumbUrl;
	
	private WechatNewsTemplate wechatNewsTemplate;

	public WechatNewsItem() {
	}

	public WechatNewsItem(int dbid) {
		this.dbid = dbid;
	}

	public WechatNewsItem(int dbid, String thumbMediaId, String title,
			String author, String contentSourceUrl, String content,
			String digest, Integer showCoverPic, Integer templateid) {
		this.dbid = dbid;
		this.title = title;
		this.author = author;
		this.content = content;
		this.digest = digest;
	}

	public Integer getDbid() {
		return this.dbid;
	}

	public void setDbid(Integer dbid) {
		this.dbid = dbid;
	}


	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}


	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDigest() {
		return this.digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getThumb_media_id() {
		return thumb_media_id;
	}

	public void setThumb_media_id(String thumb_media_id) {
		this.thumb_media_id = thumb_media_id;
	}

	public String getContent_source_url() {
		return content_source_url;
	}

	public void setContent_source_url(String content_source_url) {
		this.content_source_url = content_source_url;
	}

	public Integer getShow_cover_pic() {
		return show_cover_pic;
	}

	public void setShow_cover_pic(Integer show_cover_pic) {
		this.show_cover_pic = show_cover_pic;
	}

	public WechatNewsTemplate getWechatNewsTemplate() {
		return wechatNewsTemplate;
	}

	public void setWechatNewsTemplate(WechatNewsTemplate wechatNewsTemplate) {
		this.wechatNewsTemplate = wechatNewsTemplate;
	}

	public String getThumbWechatUrl() {
		return thumbWechatUrl;
	}

	public void setThumbWechatUrl(String thumbWechatUrl) {
		this.thumbWechatUrl = thumbWechatUrl;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}


}
