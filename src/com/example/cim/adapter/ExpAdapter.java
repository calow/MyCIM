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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cim.R;
import com.example.cim.cache.DiskLruCacheManager;
import com.example.cim.cache.LruCacheManager;
import com.example.cim.model.RecentChat;
import com.example.cim.network.API;
import com.example.cim.view.IphoneTreeView;
import com.example.cim.view.IphoneTreeView.IphoneTreeHeaderAdapter;

public class ExpAdapter extends BaseExpandableListAdapter implements
		IphoneTreeHeaderAdapter {

	private static final String TAG = "ExpAdapter";
	private Context mContext;
	private List<String> groupNames;
	private HashMap<String, List<RecentChat>> maps;
	private IphoneTreeView mIphoneTreeView;
	private View mSearchView;

	private Map<Integer, Integer> groupStatusMap = new HashMap<Integer, Integer>();
	public ExpAdapter(Context context, List<String> groupNames,
			HashMap<String, List<RecentChat>> maps,
			IphoneTreeView mIphoneTreeView, View searchView) {
		this.mContext = context;
		this.groupNames = groupNames;
		this.maps = maps;
		this.mIphoneTreeView = mIphoneTreeView;
		mSearchView = searchView;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return maps.get(groupNames.get(groupPosition)).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		if(groupPosition >= 0){
			return maps.get(groupNames.get(groupPosition)).size();
		}
		return 0;
	}

	public Object getGroup(int groupPosition) {
		if(groupPosition >= 0){
			return groupNames.get(groupPosition);
		}
		return null;
	}

	public int getGroupCount() {
		if (groupNames != null) {
			return groupNames.size();
		} else {
			return 0;
		}
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.fragment_constact_child, null);
			holder = new GroupHolder();
			holder.nameView = (TextView) convertView
					.findViewById(R.id.contact_list_item_name);
			holder.feelView = (TextView) convertView
					.findViewById(R.id.cpntact_list_item_state);
			holder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}

		RecentChat rc = maps.get(groupNames.get(groupPosition)).get(
				childPosition);
		String path = API.UpAndDown_URL + "download_userPic.action" + "?id="
				+ rc.getUserId();
		holder.iconView.setTag(path);
		loadBitmaps(holder.iconView, path);
		holder.nameView.setText(rc.getUserName());
		holder.feelView.setText(rc.getUserFeel());
		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ChildHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.fragment_constact_head_view, null);
			holder = new ChildHolder();
			holder.nameView = (TextView) convertView
					.findViewById(R.id.group_name);
			holder.onLineView = (TextView) convertView
					.findViewById(R.id.online_count);
			holder.iconView = (ImageView) convertView
					.findViewById(R.id.group_indicator);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		String groupName = groupNames.get(groupPosition);
		List<RecentChat> list = maps.get(groupName);
		int count = list.size();
		int t = 0;
		for (int i = 0; i < count; i++) {
			RecentChat rc = list.get(i);
			if (rc.getStatu() == "1") {
				t++;
			}
		}
		holder.nameView.setText(groupName);
		holder.onLineView.setText(t + "/" + getChildrenCount(groupPosition));
		if (isExpanded) {
			holder.iconView.setImageResource(R.drawable.qb_down);
		} else {
			holder.iconView.setImageResource(R.drawable.qb_right);
		}
		return convertView;
	}

	@Override
	public int getTreeHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			// mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1
				&& !mIphoneTreeView.isGroupExpanded(groupPosition)) {
			// mSearchView.setVisibility(View.VISIBLE);
			return PINNED_HEADER_GONE;
		} else {
			// mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureTreeHeader(View header, int groupPosition,
			int childPosition, int alpha) {
		((TextView) header.findViewById(R.id.group_name)).setText(groupNames
				.get(groupPosition));
		String groupName = groupNames.get(groupPosition);
		List<RecentChat> list = maps.get(groupName);
		int count = list.size();
		int t = 0;
		for (int i = 0; i < count; i++) {
			RecentChat rc = list.get(i);
			if (rc.getStatu() == "1") {
				t++;
			}
		}
		((TextView) header.findViewById(R.id.online_count)).setText(t + "/"
				+ getChildrenCount(groupPosition));
	}

	@Override
	public void onHeadViewClick(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getHeadViewClickStatus(int groupPosition) {
		if (groupStatusMap.containsKey(groupPosition)) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}

	class GroupHolder {
		TextView nameView;
		TextView feelView;
		ImageView iconView;
	}

	class ChildHolder {
		TextView nameView;
		TextView onLineView;
		ImageView iconView;
	}

	public void updateData(List<String> groupName,
			HashMap<String, List<RecentChat>> map) {
		this.groupNames = groupName;
		this.maps = map;
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
			ImageView imageView = (ImageView) mIphoneTreeView
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
	
	/**
	 * ���expandable��ǩ���ӱ�ǩʱ��ȡ�ӱ�ǩ����
	 * @param groupPosition expandable�ĸ���ǩ�±�
	 * @param childId �ӱ�ǩ�±�
	 * @return �����ӱ�ǩ���ݶ���
	 */
	public RecentChat getRecentChatByGroupIdAndChildId(int groupPosition, int childPosition){
		RecentChat result = null;
		if(groupNames != null && groupNames.size() > 0){
			String groupName = groupNames.get(groupPosition);
			List<RecentChat> list = maps.get(groupName);
			result = list.get(childPosition);
		}
		return result;
	}
}
