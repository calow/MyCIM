package com.example.cim.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cim.R;
import com.example.cim.model.MessageItem;

public class MyListViewAdapter extends BaseAdapter {

	private List<MessageItem> list = null;
	private LayoutInflater inflater = null;

	public MyListViewAdapter(FragmentActivity fragmentActivity,
			List<MessageItem> list) {
		this.list = list;
		inflater = (LayoutInflater) fragmentActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int i) {
		return list.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.fragment_message_item, null);
			holder.im = (ImageView) convertView.findViewById(R.id.user_picture);
			holder.time = (TextView) convertView.findViewById(R.id.user_time);
			holder.name = (TextView) convertView.findViewById(R.id.user_name);
			holder.content = (TextView) convertView
					.findViewById(R.id.user_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MessageItem item = list.get(position);
		holder.im.setImageResource(R.drawable.h001);
		holder.name.setText(item.getUser_name());
		holder.content.setText(item.getUser_content());
		holder.time.setText(item.getUser_time());

		return convertView;
	}

	class ViewHolder {
		ImageView im;
		TextView time;
		TextView name;
		TextView content;
	}

}
