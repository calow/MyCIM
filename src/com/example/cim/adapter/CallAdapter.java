package com.example.cim.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.cim.R;

public class CallAdapter extends BaseAdapter {

	protected static final String TAG = "CallAdapter";
	private Context mContext;

	public CallAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = View.inflate(mContext, R.layout.fragment_message_item,
				null);
		return convertView;
	}

}
