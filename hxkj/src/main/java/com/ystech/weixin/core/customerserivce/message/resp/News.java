package com.ystech.weixin.core.customerserivce.message.resp;

import java.util.List;

public class News {
	private List<Article> articles;
	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
}
