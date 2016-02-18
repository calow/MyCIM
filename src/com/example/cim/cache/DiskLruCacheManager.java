package com.example.cim.cache;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import libcore.io.DiskLruCache;

public class DiskLruCacheManager {

	private static DiskLruCacheManager sCacheManager = null;

	private Map<String, DiskLruCache> mMap = new HashMap<String, DiskLruCache>();

	private Context mContext;

	private DiskLruCacheManager(Context context) {
		mContext = context;
	}

	public static DiskLruCacheManager getDiskLruCacheManager(Context context) {
		if (sCacheManager == null) {
			synchronized (DiskLruCacheManager.class) {
				if (sCacheManager == null) {
					sCacheManager = new DiskLruCacheManager(context);
				}
			}
		}
		return sCacheManager;
	}

	public DiskLruCache getDiskLruCache(String folder) {
		DiskLruCache diskLruCache = null;
		if (!mMap.containsKey(folder)) {
			diskLruCache = newDiskLruCache(folder);
			if(diskLruCache != null){
				mMap.put(folder, diskLruCache);
			}
		} else {
			diskLruCache = mMap.get(folder);
		}
		return diskLruCache;
	}

	public DiskLruCache newDiskLruCache(String folder) {
		DiskLruCache diskLruCache = null;
		try {
			// 获取图片缓存路径
			File cacheDir = getDiskCacheDir(mContext, folder);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// 创建DiskLruCache实例，初始化缓存数据
			diskLruCache = DiskLruCache.open(cacheDir, getAppVersion(mContext),
					1, 10 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return diskLruCache;
	}

	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}
}
