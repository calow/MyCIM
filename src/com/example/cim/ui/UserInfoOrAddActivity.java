package com.example.cim.ui;

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
import java.util.Map;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cim.R;
import com.example.cim.cache.DiskLruCacheManager;
import com.example.cim.cache.LruCacheManager;
import com.example.cim.network.API;
import com.example.cim.network.HttpRequest;
import com.example.cim.network.HttpRequest.HttpCompliteListener;
import com.example.cim.ui.base.CIMMonitorActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.view.TitleBarView;

public class UserInfoOrAddActivity extends CIMMonitorActivity implements
		OnClickListener {

	private TitleBarView mTittleBar;

	private ImageView mUserHead;

	private TextView mTv_UserName;

	private TextView mTv_UserSignature;

	private Button mBtn_AddAbs;

	private Button mBtn_AddSure;

	private Button mBtn_Delete;

	private String userName;

	private String userFeel;

	private String userId;

	private String type;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1024:
				showToast("添加好友成功");
				finish();
				break;
			case 1025:
				showToast("添加好友出错");
				break;
			case 1026:
				showToast("添加好友异常");
				break;
			case 1027:
				int code = msg.getData().getInt("code");
				showToast("添加好友异常:" + code);
				break;
			case 1028:
				String e = msg.getData().getString("e");
				showToast("搜索出错:" + e);
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		findView();
		initTittle();
		init();
	}

	public void findView() {
		mTittleBar = (TitleBarView) findViewById(R.id.title_bar);
		mUserHead = (ImageView) findViewById(R.id.iv_user_info_head);
		mTv_UserName = (TextView) findViewById(R.id.tv_user_info_name);
		mTv_UserSignature = (TextView) findViewById(R.id.tv_user_info_signture);
		mBtn_AddAbs = (Button) findViewById(R.id.btn_user_info_add_abs);
		mBtn_AddSure = (Button) findViewById(R.id.btn_user_info_add_sure);
		mBtn_Delete = (Button) findViewById(R.id.btn_user_info_delete_friend);
	}

	public void initTittle() {
		mTittleBar.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE,
				View.GONE);
		mTittleBar.setTitleText(R.string.string_friend_detail_info);
		mTittleBar.setBtnLeft(R.drawable.boss_unipay_icon_back, R.string.back);
		mTittleBar.setBtnLeftOnclickListener(this);
	}

	public void init() {
		Intent intent = getIntent();
		if (intent != null) {
			type = intent.getStringExtra("type");
			if (type.equals("add")) {
				mBtn_AddAbs.setVisibility(View.VISIBLE);
				mBtn_AddSure.setVisibility(View.VISIBLE);
				mBtn_Delete.setVisibility(View.GONE);
			} else {
				mBtn_AddAbs.setVisibility(View.GONE);
				mBtn_AddSure.setVisibility(View.GONE);
				mBtn_Delete.setVisibility(View.VISIBLE);
			}
			userName = intent.getStringExtra("userName");
			mTv_UserName.setText(userName);
			userFeel = intent.getStringExtra("userFeel");
			mTv_UserSignature.setText(userFeel);
			userId = intent.getStringExtra("userId");
			String path = API.UpAndDown_URL + "download_userPic.action"
					+ "?id=" + userId;
			loadBitmaps(mUserHead, path);
			mBtn_AddAbs.setOnClickListener(this);
			mBtn_AddSure.setOnClickListener(this);
			mBtn_Delete.setOnClickListener(this);
		}
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
					.getDiskLruCacheManager(UserInfoOrAddActivity.this);
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
			if (mUserHead != null && bitmap != null) {
				mUserHead.setImageBitmap(bitmap);
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

	public void addFriendAbs() {
		showProgressDialog("提示", "正在发送添加请求，请稍后......");
		Map<String, String> user = new HashMap<String, String>();
		user.put("ULoginId",
				CIMDataConfig.getString(this, CIMDataConfig.KEY_ACCOUNT) + ":"
						+ userId);
		HttpRequest request = new HttpRequest(new HttpCompliteListener() {

			@Override
			public void onResponseSuccess(String json) {
				hideProgressDialog();
				JSONObject jsonObject = null;
				String code = null;
				try {
					jsonObject = new JSONObject(json);
					code = jsonObject.getString("code");
					if (code != null && code.equals("200")) {
						mHandler.sendEmptyMessage(1024);
					} else {
						mHandler.sendEmptyMessage(1025);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(1026);
				}
			}

			@Override
			public void onResponseError(int code) {
				hideProgressDialog();
				Message msg = new Message();
				msg.what = 1027;
				msg.getData().putInt("code", code);
				mHandler.sendMessage(msg);
			}

			@Override
			public void onRequestException(Exception e) {
				hideProgressDialog();
				Message msg = new Message();
				msg.what = 1028;
				msg.getData().putString("e", e.toString());
				mHandler.sendMessage(msg);
			}
		});
		request.httpPost(API.USEADDFRIENDABS_URL, user);
	}

	public void addFriendSure() {

	}

	public void deleteFriend() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
		case R.id.btn_user_info_add_abs:
			addFriendAbs();
			break;
		case R.id.btn_user_info_add_sure:
			addFriendSure();
			break;
		case R.id.btn_user_info_delete_friend:
			deleteFriend();
			break;
		}
	}
}
