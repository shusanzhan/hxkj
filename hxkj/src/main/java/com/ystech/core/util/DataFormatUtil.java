package com.ystech.core.util;

import java.math.BigDecimal;

public class DataFormatUtil {
	/**
	 * 格式化float，并保留两位小数，同时采用4舍5入
	 * @param value
	 * @return
	 */
	public static Float formatFloat(Float value){
		BigDecimal b = new BigDecimal(value); 
		float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue(); 
		return f1;
	}
	/**
	 * 格式化Double，并保留两位小数，同时采用4舍5入
	 * @param value
	 * @return
	 */
	public static Double formatDouble(Double value){
		BigDecimal b = new BigDecimal(value); 
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
}
