package com.example.cim.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cim.R;
import com.example.cim.adapter.MyListViewAdapter;
import com.example.cim.model.MessageItem;

public class MyFragmentOfMsg extends Fragment {
	
	private static final String TAG = "MyFragmentOfMsg";

	private ListView listView;

	private MyListViewAdapter adapter;

	private List<MessageItem> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_msg, container, false);
		list = getData();
		adapter = new MyListViewAdapter(getActivity(), list);
		listView = (ListView) view.findViewById(R.id.listMsg);
		listView.setAdapter(adapter);
		
		return view;
	}

	public List<MessageItem> getData() {
		MessageItem item;
		List<MessageItem> list = new ArrayList<MessageItem>();
		for (int i = 0; i < 30; i++) {
			item = new MessageItem();
			item.setUser_name("name" + i);
			item.setUser_content("content" + i);
			item.setUser_time("12:30");
			list.add(item);
		}
		return list;
	}

}
