package com.example.cim.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;

public class FileUtil {

	private static String path = "";
	static {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory() + "/ZWH";
		} else {
			path = Environment.getDataDirectory() + "/ZWH";
		}
	}

	public static String getRecentChatPath() {
		File file = new File(path + "/RecentChat/");
		if (!file.exists()) {
			file.mkdirs();
		}
		return path + "/RecentChat/";
	}

	public static String getWaterPhotoPath() {
		File file = new File(path + "/WaterPhoto/");
		if (!file.exists()) {
			file.mkdirs();
		}
		return path + "/WaterPhoto/";
	}

	public static List<String> getEmojiFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream inputStream = context.getResources().getAssets()
					.open("emoji");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			String str = null;
			while ((str = reader.readLine()) != null) {
				list.add(str);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
