package com.example.cim.fragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.cim.R;
import com.example.cim.adapter.NewsAdapter;
import com.example.cim.asynctask.AsyncTaskBase;
import com.example.cim.cache.MessageListManage;
import com.example.cim.model.RecentChat;
import com.example.cim.test.TestData;
import com.example.cim.ui.ChatActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.view.CustomListView;
import com.example.cim.view.CustomListView.OnRefreshListener;
import com.example.cim.view.LoadingView;

public class NewsFragment extends Fragment implements OnItemClickListener {
	private static final String TAG = "NewsFragment";
	private Context mContext;
	private View mBaseView;
	private View mSearchView;
	private CustomListView mCustomListView;
	private NewsAdapter adapter;
	private LoadingView mLoadingView;

	LinkedList<RecentChat> chats = new LinkedList<RecentChat>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_news, null);
		mSearchView = inflater.inflate(R.layout.common_search_l, null);
		findView();
		init();
		return mBaseView;
	}

	private void findView() {
		mCustomListView = (CustomListView) mBaseView.findViewById(R.id.lv_news);
		mLoadingView = (LoadingView) mBaseView.findViewById(R.id.loading);
	}

	private void init() {
		adapter = new NewsAdapter(mContext, chats, mCustomListView);
		mCustomListView.setAdapter(adapter);

		mCustomListView.addHeaderView(mSearchView);

		mCustomListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new AsyncRefresh().execute(0);
			}
		});
		mCustomListView.setOnItemClickListener(this);

		mCustomListView.setCanLoadMore(false);
		new NewsAsyncTask(mLoadingView).execute(0);
	}

	private class NewsAsyncTask extends AsyncTaskBase {

		List<RecentChat> recentChats = new ArrayList<RecentChat>();

		public NewsAsyncTask(LoadingView loadingView) {
			super(loadingView);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = -1;
			recentChats = MessageListManage.getInstance(mContext)
					.getChatRoomList(
							null,
							CIMDataConfig.getString(mContext,
									CIMDataConfig.KEY_ACCOUNT));
			if (recentChats.size() >= 0) {
				result = 1;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			chats.addAll(recentChats);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	private class AsyncRefresh extends
			AsyncTask<Integer, Integer, List<RecentChat>> {

		private List<RecentChat> recentchats = new ArrayList<RecentChat>();

		@Override
		protected List<RecentChat> doInBackground(Integer... params) {
			recentchats = TestData.getRecentChats();
			return recentchats;
		}

		@Override
		protected void onPostExecute(List<RecentChat> result) {
			super.onPostExecute(result);
			if (result != null) {
				for (RecentChat rc : result) {
					chats.addFirst(rc);
				}
				adapter.notifyDataSetChanged();
				mCustomListView.onRefreshComplete();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (id > -1) {
			RecentChat chat = (RecentChat) adapter.getItem(Integer
					.parseInt(String.valueOf(id)));
			Intent intent = new Intent(mContext, ChatActivity.class);
			intent.putExtra("id", chat.getGroupId());
			intent.putExtra("userName", chat.getUserName());
			intent.putExtra("groupType", chat.getGroupType());
			intent.putExtra("groupName", chat.getGroupName());
			mContext.startActivity(intent);
		}else{
			System.out.println("ËÑË÷¿ò±»µã»÷£¡");
		}
	}

	public void updateItemStatu(List<RecentChat> list) {
		chats.clear();
		chats.addAll(list);
		adapter.notifyDataSetChanged();
	}

}
