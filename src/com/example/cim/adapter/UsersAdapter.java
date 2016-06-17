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
		if (state.equals("����")) {
			holder.mState.setVisibility(View.VISIBLE);
		} else {
			holder.mState.setVisibility(View.GONE);
		}
		holder.mUserName.setText(item.getUserName());
		holder.mDescription.setText(item.getUserFeel());
		return convertView;
	}
	
	/**
	 * ����Bitmap���󡣴˷�������LruCache�м��������Ļ�пɼ���ImageView��Bitmap����
	 * ��������κ�һ��ImageView��Bitmap�����ڻ����У��ͻῪ���첽�߳�ȥ����ͼƬ��
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
		 * ͼƬ��URL��ַ
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
				// ����ͼƬURL��Ӧ��key
				final String key = hashKeyForDisk(imageUrl);
				// ����key��Ӧ�Ļ���
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					// ���û���ҵ���Ӧ�Ļ��棬��׼�����������������ݣ���д�뻺��
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						if (downloadUrlToStream(imageUrl, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// ���汻д����ٴβ���key��Ӧ�Ļ���
					snapShot = mDiskLruCache.get(key);
				}
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot
							.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}
				// ���������ݽ�����Bitmap����
				Bitmap bitmap = null;
				if (fileDescriptor != null) {
					bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
				}
				if (bitmap != null) {
					// ��Bitmap������ӵ��ڴ滺�浱��
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
			// ����Tag�ҵ���Ӧ��ImageView�ؼ��������غõ�ͼƬ��ʾ����.
			ImageView imageView = (ImageView) mListView
					.findViewWithTag(imageUrl);
			if (imageView != null && bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
		}

		/**
		 * ����HTTP���󣬲���ȡBitmap����
		 * 
		 * @param imageUrl
		 *            ͼƬ��URL��ַ
		 * @return �������Bitmap����
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
	 * ʹ��MD5�㷨�Դ����key���м��ܲ����ء�
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
