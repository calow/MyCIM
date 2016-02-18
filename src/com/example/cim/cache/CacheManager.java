package com.example.cim.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;

public class CacheManager {

	public static CacheManager sCacheManager;

	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	private Set<BitmapWorkerTask> taskCollection;

	private LruCacheManager mLruCacheManager;

	private DiskLruCacheManager mDiskLruCacheManager;

	private CacheManager(Context context) {
		taskCollection = new HashSet<CacheManager.BitmapWorkerTask>();
		mLruCacheManager = LruCacheManager.getLruCacheManager();
		mDiskLruCacheManager = DiskLruCacheManager
				.getDiskLruCacheManager(context);
	}

	public static CacheManager getInstance(Context context) {
		if (sCacheManager == null) {
			synchronized (CacheManager.class) {
				if (sCacheManager == null) {
					sCacheManager = new CacheManager(context);
				}
			}
		}
		return sCacheManager;
	}

	public void loadBitmap(String imageUrl, int placeholder,
			ImageView imageView, View view) {
		try {
			Bitmap bitmap = mLruCacheManager.getBitmapFromMemCache(imageUrl);
			if (bitmap == null) {
				String tag = imageUrl
						+ String.valueOf(SystemClock.currentThreadTimeMillis());
				imageView.setTag(tag);
				BitmapWorkerTask task = new BitmapWorkerTask(view, tag);
				taskCollection.add(task);
				task.execute(imageUrl);
			} else {
				if (bitmap != null && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		private View mView;

		private String tag;

		public BitmapWorkerTask(View view, String tag) {
			mView = view;
			this.tag = tag;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String imageUrl = params[0];
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			Snapshot snapShot = null;

			try {
				// 生成图片URL对应的key
				final String key = hashKeyForDisk(imageUrl);
				// 查找key对应的缓存
				snapShot = mDiskLruCacheManager.getDiskLruCache("bitmap").get(
						key);
				if (snapShot == null) {
					// 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
					DiskLruCache.Editor editor = mDiskLruCacheManager
							.getDiskLruCache("bitmap").edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					snapShot = mDiskLruCacheManager.getDiskLruCache("bitmap")
							.get(key);
				}
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot
							.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				// 将缓存数据解析成Bitmap对象
				Bitmap bitmap = null;
				if (fileDescriptor != null) {
					bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
				if (bitmap != null) {
					mLruCacheManager.addBitmapToMemoryCache(imageUrl, bitmap);
				}
				return bitmap;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileInputStream != null && fileDescriptor != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			ImageView imageView = (ImageView) mView.findViewWithTag(tag);
			if (imageView != null && bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
			taskCollection.remove(this);
		}

		/**
		 * 建立HTTP请求，并获取Bitmap对象。
		 * 
		 * @param imageUrl
		 *            图片的URL地址
		 * @return 解析后的Bitmap对象
		 */
		private boolean downloadUrlToStream(String urlString,
				OutputStream outputStream) {

			HttpURLConnection urlConnection = null;
			BufferedOutputStream out = null;
			BufferedInputStream in = null;

			try {
				final URL url = new URL(urlString);
				urlConnection = (HttpURLConnection) url.openConnection();
				in = new BufferedInputStream(urlConnection.getInputStream(),
						8 * 1024);
				out = new BufferedOutputStream(outputStream, 8 * 1024);
				int b;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}

	/**
	 * 使用MD5算法对传入的key进行加密并返回。
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

}
