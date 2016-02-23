package com.example.cim.adapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cim.R;
import com.example.cim.cache.DiskLruCacheManager;
import com.example.cim.cache.LruCacheManager;
import com.example.cim.model.RecentChat;
import com.example.cim.network.API;
import com.example.cim.util.DateUtil;
import com.example.cim.util.ImgUtil;
import com.example.cim.util.ImgUtil.OnLoadBitmapListener;
import com.example.cim.util.SystemMethod;
import com.example.cim.view.CustomListView;

public class NewsAdapter extends BaseAdapter {

	protected static final String TAG = "NewsAdapter";
	private Context mContext;
	private List<RecentChat> lists;
	private CustomListView mCustomListView;

	private HashMap<String, SoftReference<Bitmap>> hashMaps = new HashMap<String, SoftReference<Bitmap>>();

	public NewsAdapter(Context context, List<RecentChat> lists,
			CustomListView customListView) {
		this.mContext = context;
		this.lists = lists;
		this.mCustomListView = customListView;
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

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		RecentChat chat = lists.get(position);
		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.fragment_message_item, null);
			holder = new Holder();
			holder.iv = (ImageView) convertView.findViewById(R.id.user_picture);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.tv_feel = (TextView) convertView
					.findViewById(R.id.user_content);
			holder.tv_time = (TextView) convertView
					.findViewById(R.id.user_time);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		String path = chat.getImgPath();
		holder.iv.setTag(path);
		//��ȡ�û�ͷ��
		loadBitmaps(holder.iv, path);
		holder.tv_name.setText(chat.getUserName());
		holder.tv_feel.setText(chat.getUserFeel());
		Date date = new Date(Long.valueOf(chat.getUserTime()));
		boolean isTrue = DateUtil.isSameDay(date, new Date());
		String str = null;
		if(isTrue){
			str = DateUtil.getHourAndMinu(date);
		}else{
			str = DateUtil.getMounthAndDay(date);
		}
		holder.tv_time.setText(str);
		return convertView;
	}

	class Holder {
		ImageView iv;
		TextView tv_name;
		TextView tv_feel;
		TextView tv_time;
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
        	DiskLruCacheManager diskManager = DiskLruCacheManager.getDiskLruCacheManager(mContext);
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
                    fileInputStream = (FileInputStream) snapShot.getInputStream(0);  
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
            ImageView imageView = (ImageView) mCustomListView.findViewWithTag(imageUrl);  
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
        private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {  
            HttpURLConnection urlConnection = null;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            try {
                final URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
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
}
