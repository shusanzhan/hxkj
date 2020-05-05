package com.ystech.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {

	/**
	 * 将字符串形式的日期转换为java.util.Date 格式 yyyy-MM-dd
	 * 
	 * @param string
	 * @return
	 */
	public static Date string2Date(String string) {

		if (string.equals("")) {
			return null;
		}
		SimpleDateFormat format =null;
		if(string.length()==7){
			format=new SimpleDateFormat("yyyy-MM");
		}
		if(string.length()==10){
			format=new SimpleDateFormat("yyyy-MM-dd");
		}
		if(string.length()==13){
			format=new SimpleDateFormat("yyyy-MM-dd HH");
		}
		if(string.length()==16){
			format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
		if(string.length()==19){
			format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		Date date = null;
		try {
			date = format.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 过滤掉日期后缀
	 */
	public static Date formatDate(Date date) {
		String format = DateFormat.getDateInstance(DateFormat.DEFAULT).format(date);
		return string2Date(format);
	}
	/**
	 * 将字符串形式的日期转换为java.util.Date 格式 yyyy-MM-dd
	 * 
	 * @param string
	 * @return
	 */
	public static Date stringDatePatten(String string,String pattern) {
		if (string.equals("")) {
			return null;
		}
		SimpleDateFormat sdDateFormat = new SimpleDateFormat(pattern);
		try {
			return sdDateFormat.parse(string);
		} catch (ParseException e) {
			LogUtil.info("===========>日期转换格式出错！");
			e.printStackTrace();
			return null;
		}
	}
	
	/**

	/**
	 * 格式化Data:yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		return f.format(date);
	}
	/**
	 * 功能描述：通过自定义样式，格式日期
	 * @param date
	 * @param patten
	 * @return
	 */
	public static String formatPatten(Date date,String patten) {
		SimpleDateFormat format=new SimpleDateFormat(patten);
		return format.format(date);
	}
	/**
	 * 功能描述：通过自定义样式，格式日期
	 * @param date
	 * @param patten
	 * @return
	 */
	public static String formatPatten(String date,String patten) {
		SimpleDateFormat format=new SimpleDateFormat(patten);
		Date string2Date = string2Date(date);
		return format.format(string2Date);
	}
/**
 * 格式化Data:yyyy-1-1
 * @param date
 * @return
 */
	public static String format2Date(Date date) {
	SimpleDateFormat f = new SimpleDateFormat("yyyy-1-1");
	String format = f.format(date);
		return format;
}
	/**
	 * 格式化Data:yyyy-1-1
	 * 
	 * @param date
	 * @return
	 */
	public static Date format2Date11(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-1-1");
		String format = f.format(date);
		System.err.println(format);
		return string2Date(format);
	}
	

	/**
	 * 格式化Data:yyyy-MM-dd hh:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String format3(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return f.format(date);
	}

	/**
	 * 格式化Data:yyyy-MM-dd hh:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String format2(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(date);
	}
	public static String formatFile(Date date){
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		return f.format(date);
	}
	/**
	 * 计算所给出的2个日期之间相差多少天
	 * 
	 * @param startday
	 * @param endday
	 * @return
	 */
	public static int getIntervalDays(Date startday, Date endday) {
		if (startday.after(endday)) {
			Date cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTime();
		long el = endday.getTime();
		long ei = el - sl;
		return (int) (ei / (1000 * 60 * 60 * 24));
	}

	/**
	 * 计算所给出的2个日期之间相差多少小时
	 * 
	 * @param startday
	 * @param endday
	 * @return
	 */
	public static int getIntervalHours(Date startday, Date endday) {
		if (startday.after(endday)) {
			Date cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTime();
		long el = endday.getTime();
		long ei = el - sl;
		return (int) (ei / (1000 * 60 * 60));
	}

	public static int getIntervalMinute(Date statDay,Date endDay){
		if (statDay.after(endDay)) {
			Date cal = statDay;
			statDay = endDay;
			endDay = cal;
		}
		long sl = statDay.getTime();
		long el = endDay.getTime();
		long ei = el - sl;
		return (int) (ei / (1000));
	}
	public static Date stringDateWithHHMM(String date) {
		SimpleDateFormat sdDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return sdDateFormat.parse(date);
		} catch (ParseException e) {
			System.out.println("===========>日期转换格式出错！");
			e.printStackTrace();
			return null;
		}
	}
	public static Date stringDateWithHHMMSS(String date) {
		SimpleDateFormat sdDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdDateFormat.parse(date);
		} catch (ParseException e) {
			System.out.println("===========>日期转换格式出错！");
			e.printStackTrace();
			return null;
		}
	}
	public static Date stringDateWithYyyyHH(String date) {
		SimpleDateFormat sdDateFormat = new SimpleDateFormat("yyyy年MM月");
		try {
			return sdDateFormat.parse(date);
		} catch (ParseException e) {
			System.out.println("===========>日期转换格式出错！");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 计算所给出的2个日期之间相差多少月 参数无日期前后顺序
	 * 
	 * @param day1
	 * @param day2
	 * @return
	 */
	public static int getIntervalMonths(Date day1, Date day2) {
		// 确认day1 早于day2
		if (day1.after(day2)) {
			Date cal = day1;
			day1 = day2;
			day2 = cal;
		}
		Calendar dateStart = Calendar.getInstance();
		Calendar dateEnd = Calendar.getInstance();
		dateStart.setTime(day1);
		dateEnd.setTime(day2);
		int month = dateEnd.get(Calendar.MONTH) - dateStart.get(Calendar.MONTH);
		int year = dateEnd.get(Calendar.YEAR) - dateStart.get(Calendar.YEAR);
		int result = 12 * year + month;
		return result;
	}

	/**
	 * 计算所给出的2个日期之间相差多少年 参数无日期前后顺序
	 * 
	 * @param day1
	 * @param day2
	 * @return
	 */
	public static int getIntervalYears(Date day1, Date day2) {
		if (day1.after(day2)) {
			Date cal = day1;
			day1 = day2;
			day2 = cal;
		}
		Calendar dateStart = Calendar.getInstance();
		Calendar dateEnd = Calendar.getInstance();
		dateStart.setTime(day1);
		dateEnd.setTime(day2);

		int year = dateEnd.get(Calendar.YEAR) - dateStart.get(Calendar.YEAR);
		// System.out.println("dateEnd.get(dateEnd.MONTH)==>" +
		// dateEnd.get(dateEnd.YEAR));
		// System.out.println("dateStart.get(dateStart.MONTH)==>" +
		// dateStart.get(dateStart.YEAR));
		return year;
	}
	/**
	 * 计算所给出的2个日期之间相差多少年 参数无日期前后顺序
	 * 
	 * @param day1
	 * @param day2
	 * @return
	 */
	 public static Integer getIntervalYears2(Date day1, Date day2) {
		if (day1.after(day2)) {
			Date cal = day1;
			day1 = day2;
			day2 = cal;
		}
		Calendar dateStart = Calendar.getInstance();
		Calendar dateEnd = Calendar.getInstance();
		dateStart.setTime(day1);
		dateEnd.setTime(day2);
		int day = dateEnd.get(Calendar.DATE) - dateStart.get(Calendar.DATE);
		int month = dateEnd.get(Calendar.MONTH) - dateStart.get(Calendar.MONTH);
		int year = dateEnd.get(Calendar.YEAR) - dateStart.get(Calendar.YEAR);
		
		if (day < 0) {
			month = month - 1;
		}
		
		int months = 12 * year + month;
		
		return months / 12;
	}


	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}
	
	public static String DateToStrChina(Date date){
		SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日");
		String str=format.format(date);
		return str;
	}
	public static String dateToString(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String str = format.format(date);
		return str;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {
		if (str == null) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * @param date
	 * @param oldName
	 * @return 新名字，格式如下 日期(20101001)+毫秒末6位(123456)+原来的名字<br>
	 *         (oldName,长度已固定为4位字符长度-页面验证字符长度必须为4位 ) 20101001123456name
	 */
	public static String generatedName(Date date, String oldName) {
		String time = "" + System.currentTimeMillis();
		int beginIndex = time.length() - 6;
		int endIndex = time.length();
		String newName = DateUtil.dateToString(new Date()) + time.substring(beginIndex, endIndex) + oldName;
		return newName;
	}

	/**
	 * 
	 * @param date
	 * @param oldName
	 * @return 新名字，格式如下 日期(20101001)+毫秒末6位(123456)<br>
	 *         20101001123456
	 */
	public static String generatedName(Date date) {
		String time = "" + System.currentTimeMillis();
		int beginIndex = time.length() - 6;
		int endIndex = time.length();
		String newName = DateUtil.dateToString(new Date()) + time.substring(beginIndex, endIndex);
		return newName;
	}

	/**
	 * 
	 * 
	 * @param weekday
	 * @return
	 */
	public static String getMondayOfWeek() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		return format(currentDate.getTime());
	}

	public static String getFridayOfWeek() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 4);
		return format(currentDate.getTime());
	}
	/**
	 * 设置日期上一年，通过字符串
	 * 格式 yyyy-MM-dd
	 * @param string
	 * @return
	 */
	
	public static Date preYear(String string) {
		Calendar date = Calendar.getInstance();
		date.setTime(DateUtil.string2Date(string));
		date.add(Calendar.YEAR, -1);
		Date LastYear = date.getTime();
		return LastYear;
	}
	/**
	 * 设置日期的上一年，通过日期
	 * 格式 yyyy-MM-dd
	 * @param string
	 * @return
	 */
	
	public static Date preYear(Date tempDate) {
		Calendar date = Calendar.getInstance();
		date.setTime(tempDate);
		date.add(Calendar.YEAR, -1);
		Date LastYear = date.getTime();
		return LastYear;
	}
	/**
	 * 设置日期的下一年，通过字符串
	 * @param string
	 * @return
	 */
	
	public static Date nextYear(String string) {
		Calendar date = Calendar.getInstance();
		date.setTime(DateUtil.string2Date(string));
		date.add(Calendar.YEAR, 1);
		Date LastYear = date.getTime();
		return LastYear;
	}
	/**
	 * 设置日期的下一年，通过日期
	 * 格式 yyyy-MM-dd
	 * @param string
	 * @return
	 */
	
	public static Date nextYear(Date tempDate) {
		Calendar date = Calendar.getInstance();
		date.setTime(tempDate);
		date.add(Calendar.YEAR, 1);
		Date LastYear = date.getTime();
		return LastYear;
	}
	
	/**
	 * 设置当前日期加一年
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date addYear(Date date, Integer year) {
		Calendar temp = Calendar.getInstance();
		temp.setTime(date);
		temp.add(Calendar.YEAR, year);
		date = temp.getTime();
		return date;
	}
	
	/**
	 * 获取当前时间上一个月时间
	 * @return
	 */
	public static String preMonth() {
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.MONTH, -1);
		return format(currentDate.getTime());
	}
	//设置为1号,当前日期既为本月第一天 
	public static String getNowMonthFirstDay(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		Calendar   cal_1=Calendar.getInstance();//获取当前日期 
        cal_1.add(Calendar.MONTH, 0);
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
        String format2 = format.format(cal_1.getTime());
        return format2;
	}
	//设置为1号,当前日期既为本月第一天 
	public static String getNowMonth(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM"); 
		Calendar   cal_1=Calendar.getInstance();//获取当前日期 
		cal_1.add(Calendar.MONTH, 0);
		String format2 = format.format(cal_1.getTime());
		return format2;
	}
	/**
	 * 一年的开始日期：2013-01-01
	 * @param date
	 * @return
	 */
	public static Date startYear(Date date) {
		Calendar temp = Calendar.getInstance();
		temp.set(Calendar.MONTH, 0);
		temp.set(Calendar.DATE, 1);
		date = temp.getTime();
		return date;
	}
	/**
	 * 获取当前时间下一个月时间
	 * @return
	 */
	public static String nextMonth() {
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.MONTH, 1);
		return format(currentDate.getTime());
	}
	/**
	 * 设置当前日期加一个月
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date addMonth(Date date, Integer month) {
		Calendar temp = Calendar.getInstance();
		temp.setTime(date);
		temp.add(Calendar.MONTH, month);
		date = temp.getTime();
		return date;
	}
	/**
	 * 通过传入日期获取下一个月初始日期2013-08-09，获取值为2013-09-01
	 * @param date
	 * @return
	 */
	public static Date nextMonthFrist(Date date) {
		Calendar temp = Calendar.getInstance();
		temp.add(Calendar.MONTH, 1);
		temp.set(Calendar.DATE, 1);
		date = temp.getTime();
		return date;
	}
	/**
	 * 通过传入日期获取下一个月初始日期2013-08-09，获取值为2013-09-01
	 * @param date
	 * @return
	 */
	public static Date preMonthLast(Date date) {
		Calendar temp = Calendar.getInstance();
		temp.set(Calendar.DATE, 1);
		temp.add(Calendar.DATE, -1);
		date = temp.getTime();
		return date;
	}
	
	/**
	 * 获得当前日期与本周日相差的天数
	 * 
	 * @return
	 */
	private static int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek == 1) {
			return 0;
		} else {
			return 1 - dayOfWeek;
		}
	}
	/**
	 * 格式 yyyy-MM-dd
	 * 
	 * @param string
	 * @return
	 */
	public static Date nextDay(String string) {
		Calendar date = Calendar.getInstance();
		date.setTime(DateUtil.string2Date(string));
		date.add(Calendar.DATE, 1);
		Date nextday = date.getTime();
		return nextday;
	}
	/**
	 * 下一天
	 * @param tempDate
	 * @return
	 */
	public static Date nextDay(Date tempDate) {
		Calendar date = Calendar.getInstance();
		date.setTime(tempDate);
		date.add(Calendar.DATE, 1);
		Date nextday = date.getTime();
		return nextday;
	}
	/**
	 * 格式 yyyy-MM-dd
	 * 
	 * @param string
	 * @return
	 */
	
	public static Date preDay(String string) {
		Calendar date = Calendar.getInstance();
		date.setTime(DateUtil.string2Date(string));
		date.add(Calendar.DATE, -1);
		Date nextday = date.getTime();
		return nextday;
	}
	/**
	 * 前一天
	 * @param tempDate
	 * @return
	 */
	public static Date preDay(Date tempDate) {
		Calendar date = Calendar.getInstance();
		date.setTime(tempDate);
		date.add(Calendar.DATE, -1);
		Date nextday = date.getTime();
		return nextday;
	}

	 /**
	  * 计算两个日期的时间差
	  * @param formatTime1
	  * @param formatTime2
	  * @return
	  */
	 public static String getTimeDifference(Date nowDate, Date oldDate) {
	        SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
	        long t1 = 0L;
	        long t2 = 0L;
	        try {
	            t1 = timeformat.parse(getTimeStampNumberFormat(nowDate)).getTime();
	        } catch (ParseException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        try {
	            t2 = timeformat.parse(getTimeStampNumberFormat(oldDate)).getTime();
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        //因为t1-t2得到的是毫秒级,所以要初3600000得出小时.算天数或秒同理
	        int hours=(int) ((t1 - t2)/3600000);
	        int minutes=(int) (((t1 - t2)/1000-hours*3600)/60);
	        if(hours>0){
	        	if(minutes==0){
	        		return ""+hours+"小时";
	        	}else if(minutes>0){
	        		return ""+hours+"小时"+minutes+"分";
	        	}
	        }else{
	        	 return minutes+"分";
	        }
	        /*int second=(int) ((t1 - t2)/1000-hours*3600-minutes*60);*/
	        return ""+hours+"小时"+minutes+"分";
	    }
	 /**
	  * 格式化时间
	  * Locale是设置语言敏感操作
	  * @param formatTime
	  * @return
	  */
	 public static String getTimeStampNumberFormat(Date formatTime) {
	        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
	        return m_format.format(formatTime);
	 }
	 
	 public static void main(String[] args) {
		 Date statDay = new Date();
		 Calendar date = Calendar.getInstance();
		 date.set(2014, 6, 4, 3, 0);
		 Date time = date.getTime();
			if (statDay.after(time)) {
				System.out.println("lastMonth:1");
			}else{
				System.out.println("lastMonth:2");
			}
	}
	 public static int getIntervalMinute1(Date statDay,Date endDay){
			long sl = statDay.getTime();
			long el = endDay.getTime();
			long ei = el - sl;
			return (int) (ei / (1000));
		}

	 /** 
	     *  
	     * 1 第一季度 2 第二季度 3 第三季度 4 第四季度 
	     *  
	     * @param date 
	     * @return 
	     */  
	    public static int getSeason(Date date) {  
	        int season = 0;  
	        Calendar c = Calendar.getInstance();  
	        c.setTime(date);  
	        int month = c.get(Calendar.MONTH);  
	        switch (month) {  
	        case Calendar.JANUARY:  
	        case Calendar.FEBRUARY:  
	        case Calendar.MARCH:  
	            season = 1;  
	            break;  
	        case Calendar.APRIL:  
	        case Calendar.MAY:  
	        case Calendar.JUNE:  
	            season = 2;  
	            break;  
	        case Calendar.JULY:  
	        case Calendar.AUGUST:  
	        case Calendar.SEPTEMBER:  
	            season = 3;  
	            break;  
	        case Calendar.OCTOBER:  
	        case Calendar.NOVEMBER:  
	        case Calendar.DECEMBER:  
	            season = 4;  
	            break;  
	        default:  
	            break;  
	        }  
	        return season;  
	    }

		/**
		 * 功能描述：对日期价对应的天数
		 * @param date
		 * @param month
		 * @return
		 */
		public static Date addDay(Date date, Integer day) {
			Calendar temp = Calendar.getInstance();
			temp.setTime(date);
			temp.add(Calendar.DATE, day);
			date = temp.getTime();
			return date;
		}

			/**
	 * 计算所给出的2个日期之间相差多少天
	 * 
	 * @param startday
	 * @param endday
	 * @return
	 */
	public static int getTowIntervalDays(Date startday, Date endday) {
		if(endday==null){
			return 0;
		}
		Date start = DateUtil.string2Date(DateUtil.format(startday));
		Date end = DateUtil.string2Date(DateUtil.format(endday));
		long sl = start.getTime();
		long el = end.getTime();
		long ei = el - sl;
		return (int) (ei / (1000 * 60 * 60 * 24));
	}
}
