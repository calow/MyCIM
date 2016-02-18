package com.example.cim.fragment;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;

import com.example.cim.R;
import com.example.cim.model.RecentChat;
import com.example.cim.view.TitleBarView;

public class NewsFragmentFather extends Fragment {

	private static final String TAG = "NewsFragment";

	private Context mContext;
	private View mBaseView;
	private View mPopView;
	private PopupWindow mPopupWindow;

	private TitleBarView mTitleBarView;
	private ImageView mChats, mShare, mCamera, mScan;
	private RelativeLayout mCanversLayout;

	private NewsFragment newsFragment;
	private CallFragment callFragment;

	private FragmentManager fm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_news_father, null);
		mPopView = inflater.inflate(R.layout.fragment_news_pop, null);
		findView();
		init();
		return mBaseView;
	}

	private void findView() {
		mTitleBarView = (TitleBarView) mBaseView.findViewById(R.id.title_bar);
		mChats = (ImageView) mPopView.findViewById(R.id.pop_chat);
		mShare = (ImageView) mPopView.findViewById(R.id.pop_sangzhao);
		mCamera = (ImageView) mPopView.findViewById(R.id.pop_camera);
		mScan = (ImageView) mPopView.findViewById(R.id.pop_scan);
		mCanversLayout = (RelativeLayout) mBaseView
				.findViewById(R.id.rl_canvers);
	}

	private void init() {
		mTitleBarView.setCommonTitle(View.GONE, View.GONE, View.VISIBLE,
				View.VISIBLE);
		mTitleBarView.setBtnRight(R.drawable.skin_conversation_title_right_btn);
		mTitleBarView.setBtnRightOnclickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTitleBarView.setPopWindow(mPopupWindow, mTitleBarView);
				mCanversLayout.setVisibility(View.VISIBLE);
			}
		});

		mPopupWindow = new PopupWindow(mPopView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				mTitleBarView
						.setBtnRight(R.drawable.skin_conversation_title_right_btn);
				mCanversLayout.setVisibility(View.GONE);
			}
		});

		mTitleBarView.setTitleLeft(R.string.cnews);
		mTitleBarView.setTitleRight(R.string.call);

		// 添加fragment到framelayout
		newsFragment = new NewsFragment();
		fm = getFragmentManager();
		fm.beginTransaction().add(R.id.child_fragment, newsFragment).commit();

		mTitleBarView.getTitleLeft().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTitleBarView.getTitleLeft().isEnabled()) {
					mTitleBarView.getTitleLeft().setEnabled(false);
					mTitleBarView.getTitleRight().setEnabled(true);
					FragmentTransaction ft = fm.beginTransaction();
					if (newsFragment == null) {
						newsFragment = new NewsFragment();
						ft.add(R.id.child_fragment, newsFragment);
					}
					ft.show(newsFragment);
					if (callFragment != null) {
						ft.hide(callFragment);
					}
					ft.commit();
				}
			}
		});
		mTitleBarView.getTitleRight().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTitleBarView.getTitleRight().isEnabled()) {
					mTitleBarView.getTitleRight().setEnabled(false);
					mTitleBarView.getTitleLeft().setEnabled(true);
					FragmentTransaction ft = fm.beginTransaction();
					if (callFragment == null) {
						callFragment = new CallFragment();
						ft.add(R.id.child_fragment, callFragment);
					}
					ft.show(callFragment);
					if (newsFragment != null) {
						ft.hide(newsFragment);
					}
					ft.commit();
				}
			}
		});

		mTitleBarView.getTitleLeft().performClick();

		mScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 打开扫描界面
			}
		});

		mCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 打开照相机
			}
		});
	}

	public void updateMessageList(List<RecentChat> list) {
		newsFragment.updateItemStatu(list);
	}

}
