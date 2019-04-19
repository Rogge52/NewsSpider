package com.sqc.news.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataParse {
		//转换日期格式
		public static String yyyyMMDDHHmmss="yyyy-MM-dd HH:mm:ss";
		public static SimpleDateFormat sdf=new SimpleDateFormat(yyyyMMDDHHmmss);
		//从string-->date
		public static Date parseStringToData(String dateString) throws Exception{
			return sdf.parse(dateString);
		}
		//从date-->string
		public static String formatDateToString(Date date) throws Exception{
			return sdf.format(date);
		}
		//获取当前时间
		public static Date getCurrentTime() {
			return new Date();
		}
}
