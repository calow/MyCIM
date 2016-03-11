package com.example.cim.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.cim.R;
import com.example.cim.view.TitleBarView;

public class ToolFragmentFather extends Fragment {

	private static final String TAG = "ToolFragmentFather";

	private Context mContext;
	private View mBaseView;

	private TitleBarView mTitleBarView;

	private PCToolFragment pcToolFragment;
	private PhoneToolFragment phoneToolFragment;

	private FragmentManager fm;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_tool_father, null);
		findView();
		init();
		return mBaseView;
	}
	
	private void findView() {
		mTitleBarView = (TitleBarView) mBaseView.findViewById(R.id.title_bar);
	}
	
	private void init() {
		mTitleBarView.setCommonTitle(View.GONE, View.GONE, View.VISIBLE,
				View.GONE);
		mTitleBarView.setTitleLeft(R.string.pc_side);
		mTitleBarView.setTitleRight(R.string.terminal_side);

		// Ìí¼Ófragmentµ½framelayout
		pcToolFragment = new PCToolFragment();
		fm = getFragmentManager();
		fm.beginTransaction().add(R.id.tool_fragment, pcToolFragment).commit();

		mTitleBarView.getTitleLeft().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTitleBarView.getTitleLeft().isEnabled()) {
					mTitleBarView.getTitleLeft().setEnabled(false);
					mTitleBarView.getTitleRight().setEnabled(true);
					FragmentTransaction ft = fm.beginTransaction();
					if (pcToolFragment == null) {
						pcToolFragment = new PCToolFragment();
						ft.add(R.id.tool_fragment, pcToolFragment);
					}
					ft.show(pcToolFragment);
					if (phoneToolFragment != null) {
						ft.hide(phoneToolFragment);
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
					if (phoneToolFragment == null) {
						phoneToolFragment = new PhoneToolFragment();
						ft.add(R.id.tool_fragment, phoneToolFragment);
					}
					ft.show(phoneToolFragment);
					if (pcToolFragment != null) {
						ft.hide(pcToolFragment);
					}
					ft.commit();
				}
			}
		});

		mTitleBarView.getTitleLeft().performClick();
	}
}
