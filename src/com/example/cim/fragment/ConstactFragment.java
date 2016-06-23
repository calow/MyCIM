package com.example.cim.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.RelativeLayout;

import com.example.cim.R;
import com.example.cim.adapter.ExpAdapter;
import com.example.cim.asynctask.AsyncTaskBase;
import com.example.cim.cache.FriendListManage;
import com.example.cim.model.RecentChat;
import com.example.cim.ui.CIMconstactActivity;
import com.example.cim.ui.ChatActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.util.LogUtils;
import com.example.cim.view.IphoneTreeView;
import com.example.cim.view.LoadingView;

public class ConstactFragment extends Fragment {

	protected static final String TAG = "ConstactFragment";
	private Context mContext;
	private View mBaseView;

	private View mSearchView;
	private RelativeLayout constacts;
	private IphoneTreeView mIphoneTreeView;
	private LoadingView mLoadingView;
	private ExpAdapter mExpAdapter;
	private HashMap<String, List<RecentChat>> maps = new HashMap<String, List<RecentChat>>();
	private List<String> groupNames = new ArrayList<String>();

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
		mExpAdapter = new ExpAdapter(mContext, groupNames, maps, mIphoneTreeView,
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
		
		/**
		 * 点击好友条目，跳转到私聊室
		 */
		mIphoneTreeView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				RecentChat chat = mExpAdapter.getRecentChatByGroupIdAndChildId(groupPosition, childPosition);
				Intent intent = new Intent(mContext, ChatActivity.class);
				intent.putExtra("id", chat.getGroupId());
				intent.putExtra("userName", chat.getUserName());
				intent.putExtra("groupType", 1);
				intent.putExtra("groupName", chat.getGroupName());
				mContext.startActivity(intent);
				return true;
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
				Set<String> keys = maps.keySet();
				List<String> gn = new ArrayList<String>(); 
				for(String s : keys){
					gn.add(s);
				}
				groupNames = gn;
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
				mExpAdapter.updateData(groupNames, maps);
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
		Set<String> keys = maps.keySet();
		List<String> gn = new ArrayList<String>(); 
		for(String s : keys){
			gn.add(s);
		}
		mExpAdapter.updateData(gn, mapList);
		mExpAdapter.notifyDataSetChanged();
	}
}
