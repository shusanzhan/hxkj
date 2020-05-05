/**
 * 
 */
package com.ystech.core.ip;

import javax.servlet.http.HttpServletRequest;

/**
 * @author shusanzhan
 * @date 2013-3-6
 */
public class IPHepler {
	private static IPSeeker ipSeeker=new IPSeeker();
	/**
	 * 获取客户端IP地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		ip = request.getRemoteAddr();
		}
		return ip;
	}
	/**
	 * 通过IP地址获取物理地址
	 * @param request
	 * @return
	 */
	public static String getAddressString(HttpServletRequest request){
		String ipAddr = getIpAddr(request);
		String addressStr=new String();
		if(null!=ipSeeker){
			if (ipSeeker.getCountry(ipAddr).contains("局域网")) {
				addressStr=ipSeeker.getCountry(ipAddr);
			}else{
				addressStr=ipSeeker.getCountry(ipAddr)+":"+ipSeeker.getArea(ipAddr);
			}
		}
		return addressStr;
	}
	/**
	 * 获取省份信息
	 * @param contry
	 * @return
	 */
	public static String getProvince(String addressString){
		if(null!=addressString){
			if(addressString.contains("省")){
				int indexOf = addressString.indexOf("省");
				return addressString.substring(0, indexOf+1);
			}else if(addressString.contains("市")){
				int indexOf = addressString.indexOf("市");
				return addressString.substring(0, indexOf+1);
			}else if(addressString.contains("自治区")){
				int indexOf=addressString.indexOf("自治区");
				return addressString.substring(0, indexOf+1);
			}else
				return addressString; 
		}
		return null;
	}
	/**
	 * 获取城市信息
	 * @param contry
	 * @return
	 */
	public static String getCity(String addressString){
		String province = getProvince(addressString);
		if(null!=addressString){
			//去掉省信息
			addressString=addressString.replace(province,"");
			if(province.contains("省")||province.contains("自治区")){
				if(addressString.contains("市")){
					int indexOf = addressString.indexOf("市");
					return addressString.substring(0, indexOf+1);
				}
				else if(addressString.contains("州")){
					int indexOf = addressString.indexOf("州");
					return addressString.substring(0, indexOf+1);
				}
				else if(addressString.contains("区")){
					int indexOf = addressString.indexOf("区");
					return addressString.substring(0, indexOf+1);
				}
				else if(addressString.contains("盟")){
					int indexOf = addressString.indexOf("盟");
					return addressString.substring(0, indexOf+1);
				}
				else if(addressString.contains("县")){
					int indexOf = addressString.indexOf("县");
					return addressString.substring(0, indexOf+1);
				}else{
					return addressString;
				}
			}else if(province.contains("市")){
				if(addressString.contains("区")){
					int indexOf = addressString.indexOf("区");
					return addressString.substring(0, indexOf+1);
				}
				else if(addressString.contains("县")){
					int indexOf = addressString.indexOf("县");
					return addressString.substring(0, indexOf+1);
				}else{
					return addressString;
				}
			}else{
				return addressString;
			}
			
		}
		return null;
	}
	
}
