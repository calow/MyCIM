package com.example.cim.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cim.R;

public class MyFragmentOfList extends Fragment {
	String TAG = "MyFragmentOfList";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_friend, container, false);
		ListView listView = (ListView) view.findViewById(R.id.listFriend);
		Log.e(TAG, "创建listFragment");
		return view;
	}

}
