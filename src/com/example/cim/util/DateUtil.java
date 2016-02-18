package com.example.cim.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	public static boolean isSameDay(Date day1, Date day2) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String ds1 = sdf.format(day1);
	    String ds2 = sdf.format(day2);
	    if (ds1.equals(ds2)) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	public static String getHourAndMinu(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String parse = sdf.format(date);
		return parse;
	}
	
	public static String getMounthAndDay(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String parse = sdf.format(date);
		return parse;
	}
	
	public static String getStringOfDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String parse = sdf.format(date);
		return parse;
	}

}
