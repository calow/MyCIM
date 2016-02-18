package com.example.cim.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cim.R;
import com.example.cim.adapter.MyFragmentPagerAdapter;
import com.example.cim.fragment.MyFragmentOfDyn;
import com.example.cim.fragment.MyFragmentOfList;
import com.example.cim.fragment.MyFragmentOfMsg;
import com.example.cim.fragment.VpSimpleFragment;
import com.example.cim.view.ViewPagerIndicator;

public class ViewPagerActivity extends FragmentActivity {

	private TextView t1, t2, t3;// menu
	private ImageView v1;// 图片
	private ViewPager viewPager;
	private List<Fragment> listViews;
	private int bmpW;// 图片宽度
	private int offset = 0;// 偏移�?
	private int currIndex = 0;

	private ViewPager pager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mTabContents = new ArrayList<Fragment>();
	private ViewPagerIndicator indicator;
	private List<String> mDatas = Arrays.asList("短信1", "短信2", "短信3", "短信4",
			"短信5", "短信6", "短信7", "短信8", "短信9");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//------------------------ 第二----------------
//		initDatas();
//		initView();
//		
//		pager.setAdapter(mAdapter);
//		indicator.setTabTittles(mDatas);
//		indicator.setViewPager(pager, 0);
//------------------------ 第一----------------
		// initTextView();
		// initImageView();
		// initViewPager();
	}

	// 初始化文字点击事�?
	public void initTextView() {
		t1 = (TextView) findViewById(R.id.tileOne);
		t2 = (TextView) findViewById(R.id.tileTwo);
		t3 = (TextView) findViewById(R.id.titleThree);
		t1.setOnClickListener(new MyOnclickListener(0));
		t2.setOnClickListener(new MyOnclickListener(1));
		t3.setOnClickListener(new MyOnclickListener(2));
	}

	// 初始化图片显示位�?
	public void initImageView() {
		v1 = (ImageView) findViewById(R.id.pic);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		v1.setImageMatrix(matrix);
	}

	public void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		listViews = new ArrayList<Fragment>();
		listViews.add(new MyFragmentOfMsg());
		listViews.add(new MyFragmentOfList());
		listViews.add(new MyFragmentOfDyn());
		viewPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), listViews));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	// 标题点击后相应事�?
	class MyOnclickListener implements OnClickListener {
		private int index;

		public MyOnclickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View arg0) {
			viewPager.setCurrentItem(index);
		}
	}

	class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移�?
		int two = one * 2;// 页卡1 -> 页卡3 偏移�?

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			v1.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.e("mainactivity", "onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("mainactivity", "onResume");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e("mainactivity", "onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e("mainactivity", "onStop");
	}
	
	private void initDatas()
	{

		for (String data : mDatas)
		{
			VpSimpleFragment fragment = VpSimpleFragment.newInstance(data);
			mTabContents.add(fragment);
		}

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public int getCount()
			{
				return mTabContents.size();
			}

			@Override
			public Fragment getItem(int position)
			{
				return mTabContents.get(position);
			}
		};
	}

	private void initView()
	{
		pager = (ViewPager) findViewById(R.id.id_vp);
		indicator = (ViewPagerIndicator) findViewById(R.id.id_indicator);
	}

}
