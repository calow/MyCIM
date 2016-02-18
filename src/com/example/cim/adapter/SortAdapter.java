package com.example.cim.adapter;

import java.util.List;
import com.example.cim.R;
import com.example.cim.sort.SortModel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SortAdapter extends BaseAdapter implements SectionIndexer {

	private Context mContext;
	private List<SortModel> list = null;

	public SortAdapter(Context context, List<SortModel> list) {
		this.mContext = context;
		this.list = list;
	}
	
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		final SortModel mContent = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext,
					R.layout.fragment_phone_constacts_item, null);
			holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		if (position == getPositionForSection(section)) {
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(mContent.getSortLetters());
		} else {
			holder.tvLetter.setVisibility(View.GONE);
		}
		holder.tvTitle.setText(mContent.getName());
		return convertView;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (section == firstChar) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
	}

}
