package com.example.cim.fragment;

import java.util.HashMap;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.cim.R;
import com.example.cim.adapter.ExpAdapter;
import com.example.cim.asynctask.AsyncTaskBase;
import com.example.cim.cache.FriendListManage;
import com.example.cim.manager.CIMPushManager;
import com.example.cim.model.RecentChat;
import com.example.cim.nio.mutual.SentBody;
import com.example.cim.test.TestData;
import com.example.cim.ui.CIMconstactActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.view.IphoneTreeView;
import com.example.cim.view.LoadingView;

public class ConstactFragment extends Fragment {

	private Context mContext;
	private View mBaseView;

	private View mSearchView;
	private RelativeLayout constacts;
	private IphoneTreeView mIphoneTreeView;
	private LoadingView mLoadingView;
	private ExpAdapter mExpAdapter;
	private HashMap<String, List<RecentChat>> maps = new HashMap<String, List<RecentChat>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_constact, null);
		findView();
		init();
		return mBaseView;
	}

	private void findView() {
		mSearchView = mBaseView.findViewById(R.id.search);// id重复
		constacts = (RelativeLayout) mBaseView.findViewById(R.id.rl_tonxunru);
		mIphoneTreeView = (IphoneTreeView) mBaseView
				.findViewById(R.id.iphone_tree_view);
		mLoadingView = (LoadingView) mBaseView.findViewById(R.id.loadingView);
	}

	private void init() {
		mIphoneTreeView.setHeaderView(LayoutInflater.from(mContext).inflate(
				R.layout.fragment_constact_head_view, mIphoneTreeView, false));
		mIphoneTreeView.setGroupIndicator(null);
		mExpAdapter = new ExpAdapter(mContext, maps, mIphoneTreeView,
				mSearchView);
		mIphoneTreeView.setAdapter(mExpAdapter);
		constacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CIMconstactActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_up,
						R.anim.fade_out);
			}
		});

		new AsyncTaskLoading(mLoadingView).execute(0);
	}

	private class AsyncTaskLoading extends AsyncTaskBase {

		public AsyncTaskLoading(LoadingView loadingView) {
			super(loadingView);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = -1;
			try {
				// 获取缓存里的好友列表----
				maps = FriendListManage.getInstance(mContext).getFriendList(
						null,
						CIMDataConfig.getString(mContext,
								CIMDataConfig.KEY_ACCOUNT));
				// 获取缓存里的好友列表----
				result = 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result == 1){
				mExpAdapter.updateData(maps);
				mExpAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}
	
	public void updateFriendlist(SQLiteDatabase database, String userAccount){
		HashMap<String, List<RecentChat>> mapList = FriendListManage.getInstance(mContext).getFriendList(
				null,
				CIMDataConfig.getString(mContext,
						CIMDataConfig.KEY_ACCOUNT));
		mExpAdapter.updateData(mapList);
		mExpAdapter.notifyDataSetChanged();
	}
}
