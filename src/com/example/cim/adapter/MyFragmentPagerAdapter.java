package com.example.cim.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> listViews;
	public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> listViews) {
		super(fm);
		this.listViews = listViews;
	}

	@Override
	public Fragment getItem(int item) {
		return listViews.get(item);
	}

	@Override
	public int getCount() {
		return listViews.size();
	}

}
