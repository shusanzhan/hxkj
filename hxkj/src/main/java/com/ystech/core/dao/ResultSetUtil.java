package com.ystech.core.dao;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ystech.core.util.DateUtil;

public class ResultSetUtil<T> {
	
	/** 
	   * 匹配指定class中数据,并返回包含get和set方法的object 
	   *  
	   * @author chenpeng 
	   * @param clazz 
	   * @param beanProperty 
	   * @return 
	   */  
	   private static Object[] beanMatch(Class clazz, String beanProperty) {  
	       Object[] result = new Object[2];  
	       char beanPropertyChars[] = beanProperty.toCharArray();  
	       beanPropertyChars[0] = Character.toUpperCase(beanPropertyChars[0]);  
	       String s = new String(beanPropertyChars);  
	       String names[] = { ("set" + s).intern(), ("get" + s).intern(),  
	               ("is" + s).intern(), ("write" + s).intern(),  
	               ("read" + s).intern() };  
	       Method getter = null;  
	       Method setter = null;  
	       Method methods[] = clazz.getMethods();  
	       for (int i = 0; i < methods.length; i++) {  
	           Method method = methods[i];  
	           // 只取公共字段  
	           if (!Modifier.isPublic(method.getModifiers()))  
	               continue;  
	           String methodName = method.getName().intern();  
	           for (int j = 0; j < names.length; j++) {  
	               String name = names[j];  
	               if (!name.equals(methodName))  
	                   continue;  
	               if (methodName.startsWith("set")  
	                       || methodName.startsWith("read"))  
	                   setter = method;  
	               else  
	                   getter = method;  
	           }  
	       }  
	       result[0] = getter;  
	       result[1] = setter;  
	       return result;  
	   }  
	  
	   /** 
	   * 为bean自动注入数据 
	   *  
	   * @author chenpeng 
	   * @param object 
	   * @param beanProperty 
	   */  
	   private static void beanRegister(Object object, String beanProperty, String value) {  
	       Object[] beanObject = beanMatch(object.getClass(), beanProperty);  
	       Object[] cache = new Object[1];  
	       Method getter = (Method) beanObject[0];  
	       Method setter = (Method) beanObject[1];  
	       try {  
	           // 通过get获得方法类型  
	    	   if(null==getter){
	    		   return ;
	    	   }
	           String methodType = getter.getReturnType().getName().toLowerCase();  
	           if(null==value){
	        	   return ;
	           }
	           if (methodType.contains("long")) {  
	               cache[0] = new Long(value);  
	               setter.invoke(object, cache);  
	           } else if (methodType.contains("int")  
	                   || methodType.contains("integer")) {
	               cache[0] = new Integer(value);  
	               setter.invoke(object, cache);  
	           } else if (methodType.contains("short")) {  
	               cache[0] = new Short(value);  
	               setter.invoke(object, cache);  
	           } else if (methodType.contains("float")) {  
	               cache[0] = new Float(value);  
	               setter.invoke(object, cache);  
	           } else if (methodType.contains("double")) {  
	               cache[0] = new Double(value);  
	               setter.invoke(object, cache);  
	           } else if (methodType.contains("boolean")) {  
	               cache[0] = new Boolean(value);  
	               setter.invoke(object, cache);  
	           } else if (methodType.contains("java.lang.string")) {  
	               cache[0] = value;  
	               setter.invoke(object, cache);  
	           } else if (methodType.contains("java.io.inputstream")) {  
	           } else if (methodType.contains("char")) {  
	               cache[0] = (Character.valueOf(value.charAt(0)));  
	               setter.invoke(object, cache);  
	           }  
	           else if (methodType.contains("date")) {  
	               cache[0] = DateUtil.string2Date(value);
	               setter.invoke(object, cache);  
	           } 
	       } catch (Exception e) {  
	           e.printStackTrace();  
	       }  
	   }  
	  
	  
	   @SuppressWarnings("unchecked")
	public static List getDateResult(List result, ResultSet resultSet, Class clazz) {  
	       // 创建collection  
	       try {  
	           ResultSetMetaData rsmd = resultSet.getMetaData();  
	           // 获得数据列数  
	           int cols = rsmd.getColumnCount();  
	           // 创建等同数据列数的arraylist类型collection实例  
	           result = new ArrayList(cols);  
	           // 遍历结果集  
	           while (resultSet.next()) {  
	               // 创建对象  
	               Object object = null;  
	               try {  
	                   // 从class获得对象实体  
	                   object = clazz.newInstance();  
	               } catch (Exception e) {  
	               }  
	               // 循环每条记录  
	               for (int i = 1; i <= cols; i++) {  
	                   beanRegister(object, rsmd.getColumnName(i), resultSet.getString(i));  
	               }  
	               // 将数据插入collection  
	               result.add(object);  
	           }  
	       } catch (SQLException e) {  
	           System.err.println(e.getMessage());  
	       } finally {  
	  
	       }  
	       return result;  
	   }  
	  
	
	/**
	 * 将基本数据类型转换成封装类
	 * 
	 * 1.基本类型：存储在栈中，存储速度比较快些
			byte,char,short,int,long,float,double，boolean
	   2.包装类，
			Integer(4字节), Double(8字节), Long(8字节), Short(2字节),
			Boolean,Float(4字节),character(2字节),Byte(1字节), 
	 */
	private static Class<?> getChangeType(Class<?> type){
		if( (type == Integer.class) || (type == int.class)){
			return Integer.class;
		}else if( (type == Double.class ) || (type == double.class)){
			return Double.class;
		}else if( (type == Float.class ) || (type == float.class)){
			return Float.class;
		}else if( (type == Boolean.class ) || (type == boolean.class)){
			return Boolean.class;
		}else if( (type == Long.class) || (type == long.class)){
			return Long.class;
		}else if( (type == Byte.class) || (type == byte.class)){
			return Byte.class;
		}else if( (type == Character.class) || (type == char.class)){
			return Character.class;
		}else if( (type == Short.class) || (type == short.class)){
			return Short.class;
		}else{
			return type;
		}

	}
	
}
