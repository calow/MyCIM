package com.example.cim.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.cim.R;
import com.example.cim.model.ChatEmoji;

public class GridViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<ChatEmoji> data;
	private LayoutInflater mInflater;

	public GridViewAdapter(Context context, List<ChatEmoji> data) {
		this.data = data;
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatEmoji emoji = data.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_face, null);
			holder.mImageView = (ImageView) convertView.findViewById(R.id.item_iv_face);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(emoji.getId() == R.drawable.face_del_icon){
			convertView.setBackgroundDrawable(null);
			holder.mImageView.setImageResource(emoji.getId());
		}else if(TextUtils.isEmpty(emoji.getCharacter())){
			convertView.setBackgroundDrawable(null);
			holder.mImageView.setImageDrawable(null);
		}else{
			holder.mImageView.setTag(emoji);
			holder.mImageView.setImageResource(emoji.getId());
		}
		return convertView;
	}

	class ViewHolder {
		public ImageView mImageView;
	}

}
