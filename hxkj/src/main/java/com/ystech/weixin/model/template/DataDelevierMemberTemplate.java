package com.ystech.weixin.model.template;

/**
 * 餐饮行业收到新订单通知模板消息实体类
 * @author xjw
 *
 */
public class DataDelevierMemberTemplate {
    private String touser; //用户OpenID
    
    private String template_id; //模板消息ID
    
    private String url; //URL置空，则在发送后，点击模板消息会进入一个空白页面（ios），或无法点击（android）。
    
    private String topcolor; //标题颜色
    
    private DataMemberCar data; //详细内容

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String templateId) {
        template_id = templateId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTopcolor() {
        return topcolor;
    }

    public void setTopcolor(String topcolor) {
        this.topcolor = topcolor;
    }

	public DataMemberCar getData() {
		return data;
	}

	public void setData(DataMemberCar data) {
		this.data = data;
	}



}
