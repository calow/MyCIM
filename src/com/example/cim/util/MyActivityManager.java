package com.example.cim.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;


public class MyActivityManager {
	
	private static ArrayList<Activity> list = new ArrayList<Activity>();
	
	public static void registerActivity(Activity activity,
			Context mContext) {
		if (!list.contains(activity)) {
			list.add(activity);
		}
	}

	public static void removeActivity(Activity activity) {
		for (int i = 0; i < list.size(); i++) {
			if (activity.getClass() == list.get(i).getClass()) {
				list.remove(i);
			}
		}
	}

	public static ArrayList<Activity> getLists() {
		return list;
	}
}
