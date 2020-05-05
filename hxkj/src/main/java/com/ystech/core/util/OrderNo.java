package com.ystech.core.util;

import java.util.Random;

public class OrderNo {
	public String getOrderNo(){
		long currentTime= System.currentTimeMillis();
		int flag = new Random().nextInt(999999);  
	    if (flag < 100000)  
	    {  
	        flag += 100000;  
	    }  
		String orderNo = currentTime+""+flag;
		return orderNo;
	}
}
