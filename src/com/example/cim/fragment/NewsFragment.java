package com.example.cim.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.cim.ui.ChatActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.view.CustomListView;
import com.example.cim.view.CustomListView.OnRefreshListener;
import com.example.cim.view.LoadingView;

public class NewsFragment extends Fragment implements OnItemClickListener {
	private static final String TAG = "NewsFragment";
	private static final int TAG_FRESH = 1;
	private Context mContext;
	private View mBaseView;
	private View mSearchView;
	private CustomListView mCustomListView;
	private NewsAdapter adapter;
	private LoadingView mLoadingView;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
				case TAG_FRESH:
					Bundle bundle = msg.getData();
					ArrayList<RecentChat> list = bundle.getParcelableArrayList("list");
					chats.clear();
					chats.addAll(list);
					adapter.notifyDataSetChanged();
			}
		}
	};

	List<RecentChat> chats = new ArrayList<RecentChat>();

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
		updateItemStatu();
	}

	private class NewsAsyncTask extends AsyncTaskBase {
		
		ArrayList<RecentChat> chatList = new ArrayList<RecentChat>();

		public NewsAsyncTask(LoadingView loadingView) {
			super(loadingView);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			chatList = MessageListManage.getInstance(mContext).getChatRoomList(null,
					CIMDataConfig.getString(mContext, CIMDataConfig.KEY_ACCOUNT));
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			updateItemStatu(chatList);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	private class AsyncRefresh extends
			AsyncTask<Integer, Integer, ArrayList<RecentChat>> {

		private ArrayList<RecentChat> recentchats = new ArrayList<RecentChat>();

		@Override
		protected ArrayList<RecentChat> doInBackground(Integer... params) {
			recentchats = MessageListManage.getInstance(mContext)
					.getChatRoomList(
							null,
							CIMDataConfig.getString(mContext,
									CIMDataConfig.KEY_ACCOUNT));
			return recentchats;
		}

		@Override
		protected void onPostExecute(ArrayList<RecentChat> result) {
			super.onPostExecute(result);
			updateItemStatu(result);
			mCustomListView.onRefreshComplete();
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
			intent.putExtra("faceToUserId", chat.getFaceToUserId());
			mContext.startActivity(intent);
		} else {
			System.out.println("ËÑË÷¿ò±»µã»÷£¡");
		}
	}

	public void updateItemStatu(ArrayList<RecentChat> list) {
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("list", list);
		message.setData(bundle);
		message.what = TAG_FRESH;
		mHandler.sendMessage(message);
	}
	
	public synchronized void updateItemStatu(){
		new NewsAsyncTask(mLoadingView).execute(0);
	}

}
