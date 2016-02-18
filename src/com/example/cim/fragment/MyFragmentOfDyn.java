package com.example.cim.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.cim.R;

public class MyFragmentOfDyn extends Fragment {
	String TAG = "MyFragmentOfDyn";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dyn, container, false);
		ListView listView = (ListView) view.findViewById(R.id.dynamic);
		Log.e(TAG, "创建dynFragment");
		return view;
	}

}
