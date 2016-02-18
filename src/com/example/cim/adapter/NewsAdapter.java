package com.example.cim.adapter;

import java.lang.ref.SoftReference;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cim.R;
import com.example.cim.model.RecentChat;
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
		if (hashMaps.containsKey(path)) {
			holder.iv.setImageBitmap(hashMaps.get(path).get());
			ImgUtil.getInstance().reomoveCache(path);
		} else {
			holder.iv.setTag(chat.getImgPath());
			ImgUtil.getInstance().loadBitmap(path, new OnLoadBitmapListener() {

				@Override
				public void loadImage(Bitmap bitmap, String path) {
					ImageView iv = (ImageView) mCustomListView
							.findViewWithTag(path);
					if (iv != null && bitmap != null) {
						bitmap = SystemMethod.toRoundCorner(bitmap, 15);
						iv.setImageBitmap(bitmap);
						if (!hashMaps.containsKey(path)) {
							hashMaps.put(path,
									new SoftReference<Bitmap>(bitmap));
						}
					}
				}
			});
		}

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

}
