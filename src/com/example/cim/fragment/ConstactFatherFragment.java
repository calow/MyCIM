package com.example.cim.fragment;

import net.sqlcipher.database.SQLiteDatabase;

import com.example.cim.R;
import com.example.cim.ui.SearchActivity;
import com.example.cim.view.TitleBarView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ConstactFatherFragment extends Fragment {

	private Context mContext;
	private View mBaseView;
	private TitleBarView mTitleBarView;
	private FragmentManager fm;
	private ConstactFragment constactFragment;
	private CIMfriendFragment cimFriendFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_constact_father, null);
		findView();
		init();
		return mBaseView;
	}

	private void findView() {
		mTitleBarView = (TitleBarView) mBaseView.findViewById(R.id.title_bar);
	}

	private void init() {
		mTitleBarView.setCommonTitle(View.VISIBLE, View.GONE, View.VISIBLE,
				View.VISIBLE);
		mTitleBarView.setBtnLeft(R.string.control);
		mTitleBarView.setBtnRight(R.drawable.qq_constact);
		mTitleBarView.setBtnRightOnclickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SearchActivity.class);
				mContext.startActivity(intent);
			}
		});

		mTitleBarView.setTitleLeft(R.string.group);
		mTitleBarView.setTitleRight(R.string.all);
		
		constactFragment = new ConstactFragment();
		fm = getFragmentManager();
		fm.beginTransaction().add(R.id.rl_content, constactFragment).commit();
		
		mTitleBarView.getTitleLeft().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTitleBarView.getTitleLeft().isEnabled()) {
					mTitleBarView.getTitleLeft().setEnabled(false);
					mTitleBarView.getTitleRight().setEnabled(true);
					
					FragmentTransaction ft = fm.beginTransaction();
					if(constactFragment == null){
						constactFragment = new ConstactFragment();
						ft.add(R.id.rl_content, constactFragment);
					}
					ft.show(constactFragment);
					if(cimFriendFragment != null){
						ft.hide(cimFriendFragment);
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
					if(cimFriendFragment == null){
						cimFriendFragment = new CIMfriendFragment();
						ft.add(R.id.rl_content, cimFriendFragment);
					}
					ft.show(cimFriendFragment);
					if(constactFragment != null){
						ft.hide(constactFragment);
					}
					ft.commit();
				}
			}
		});
		mTitleBarView.getTitleLeft().performClick();
	}
	
	public boolean isConstactFragmentExit(){
		if(constactFragment != null){
			return true;
		}
		return false;
	}
	
	public boolean isCimFriendFragmentExit(){
		if(cimFriendFragment != null){
			return true;
		}
		return false;
	}
	
	public void updateFriendlist(SQLiteDatabase database, String userAccount){
		if(constactFragment != null){
			constactFragment.updateFriendlist(database, userAccount);
		}
	}
}
