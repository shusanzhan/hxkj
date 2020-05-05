package com.ystech.weixin.core.util;

import java.io.UnsupportedEncodingException;

public class WeixinAuth2Util {
	public static String urlEncodeUTF8(String source){
        String result = source;
        try {
                result = java.net.URLEncoder.encode(source,"UTF-8");
        } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
        }
        return result;
}
}
