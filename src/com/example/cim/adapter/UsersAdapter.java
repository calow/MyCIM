package com.example.cim.adapter;

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
import java.util.List;
import java.util.zip.Inflater;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;

import com.example.cim.R;
import com.example.cim.adapter.ExpAdapter.BitmapWorkerTask;
import com.example.cim.cache.DiskLruCacheManager;
import com.example.cim.cache.LruCacheManager;
import com.example.cim.model.SearchUserItem;
import com.example.cim.network.API;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UsersAdapter extends BaseAdapter {

	private Context mContext;

	private List<SearchUserItem> lists;

	private ListView mListView;

	public UsersAdapter(Context context, List<SearchUserItem> list,
			ListView listView) {
		this.mContext = context;
		this.lists = list;
		this.mListView = listView;
	}

	@Override
	public int getCount() {
		if (lists != null) {
			return lists.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.search_user_item, null);
			holder = new ViewHolder();
			holder.mHeader = (ImageView) convertView.findViewById(R.id.icon);
			holder.mState = (ImageView) convertView
					.findViewById(R.id.stateicon);
			holder.mUserName = (TextView) convertView
					.findViewById(R.id.search_list_item_name);
			holder.mDescription = (TextView) convertView
					.findViewById(R.id.search_list_item_state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SearchUserItem item = lists.get(position);
		String path = API.UpAndDown_URL + "download_userPic.action" + "?id="
				+ item.getUserId();
		holder.mHeader.setTag(path);
		loadBitmaps(holder.mHeader, path);
		String state = item.getUserState();
		if (state.equals("在线")) {
			holder.mState.setVisibility(View.VISIBLE);
		} else {
			holder.mState.setVisibility(View.GONE);
		}
		holder.mUserName.setText(item.getUserName());
		holder.mDescription.setText(item.getUserFeel());
		return convertView;
	}
	
	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 */
	public void loadBitmaps(ImageView imageView, String imageUrl) {
		try {
			LruCacheManager lruManager = LruCacheManager.getLruCacheManager();
			Bitmap bitmap = lruManager.getBitmapFromMemCache(imageUrl);
			if (bitmap == null) {
				BitmapWorkerTask task = new BitmapWorkerTask();
				task.execute(imageUrl);
			} else {
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * 图片的URL地址
		 */
		private String imageUrl;

		@Override
		protected Bitmap doInBackground(String... params) {
			LruCacheManager lruManager = LruCacheManager.getLruCacheManager();
			DiskLruCacheManager diskManager = DiskLruCacheManager
					.getDiskLruCacheManager(mContext);
			DiskLruCache mDiskLruCache = diskManager.getDiskLruCache("picture");
			imageUrl = params[0];
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			Snapshot snapShot = null;
			try {
				// 生成图片URL对应的key
				final String key = hashKeyForDisk(imageUrl);
				// 查找key对应的缓存
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					// 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// 缓存被写入后，再次查找key对应的缓存
					snapShot = mDiskLruCache.get(key);
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
					// 将Bitmap对象添加到内存缓存当中
					lruManager.addBitmapToMemoryCache(params[0], bitmap);
				}
				return bitmap;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileDescriptor == null && fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			// 根据Tag找到相应的ImageView控件，将下载好的图片显示出来.
			ImageView imageView = (ImageView) mListView
					.findViewWithTag(imageUrl);
			if (imageView != null && bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
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
			} catch (final IOException e) {
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
				} catch (final IOException e) {
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

	class ViewHolder {
		ImageView mHeader;
		ImageView mState;
		TextView mUserName;
		TextView mDescription;
	}
	
}
