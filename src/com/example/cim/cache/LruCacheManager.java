package com.example.cim.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruCacheManager {

	private static LruCacheManager sCacheManager = null;
	
	private LruCache<String, Bitmap> mMemoryCache;

	private LruCacheManager() {
		// ��ȡ�������ڴ�����ֵ��ʹ���ڴ泬�����ֵ������OutOfMemory�쳣��
		// LruCacheͨ�����캯�����뻺��ֵ����KBΪ��λ��
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// ʹ���������ڴ�ֵ��1/8��Ϊ����Ĵ�С��
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// ��д�˷���������ÿ��ͼƬ�Ĵ�С��Ĭ�Ϸ���ͼƬ������
				return bitmap.getByteCount() / 1024;
			}
		};
	}

	public static LruCacheManager getLruCacheManager(){
		if (sCacheManager == null) {
			synchronized (LruCacheManager.class) {
				if (sCacheManager == null) {
					sCacheManager = new LruCacheManager();
				}
			}
		}
		return sCacheManager;
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
	    if (getBitmapFromMemCache(key) == null) {  
	        mMemoryCache.put(key, bitmap);  
	    }
	}
	
	public Bitmap getBitmapFromMemCache(String key) {  
	    return mMemoryCache.get(key);  
	}
}
