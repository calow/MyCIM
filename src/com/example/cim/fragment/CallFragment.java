package com.example.cim.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.cim.R;
import com.example.cim.adapter.CallAdapter;
import com.example.cim.asynctask.AsyncTaskBase;
import com.example.cim.ui.CIMconstactActivity;
import com.example.cim.view.CustomListView;
import com.example.cim.view.CustomListView.OnRefreshListener;
import com.example.cim.view.LoadingView;

public class CallFragment extends Fragment {
	
	private Context mContext;
	private View mBaseView;
	private CustomListView mCustomListView;
	private View mSearchView;
	private RelativeLayout rlView;
	private LoadingView mLoadingView;
	private Button constact;
	private CallAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_news_call, null);
		mSearchView = inflater.inflate(R.layout.common_search_xl, null);
		findView();
		init();
		return mBaseView;
	}
	
	private void findView(){
		mCustomListView = (CustomListView) mBaseView.findViewById(R.id.callListView);
		mLoadingView = (LoadingView) mSearchView.findViewById(R.id.loading);
		rlView = (RelativeLayout) mSearchView.findViewById(R.id.rl_call);
		constact = (Button) mSearchView.findViewById(R.id.btn_constact);
	}
	
	private void init(){
		mAdapter = new CallAdapter(mContext);
		mCustomListView.setAdapter(mAdapter);
		mCustomListView.addHeaderView(mSearchView);
		mCustomListView.setCanLoadMore(false);
		mCustomListView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				new CallAsyncTask(mLoadingView).execute(0);
			}
		});
		constact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CIMconstactActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.activity_up, R.anim.fade_out);
			}
		});
	}
	
	private class CallAsyncTask extends AsyncTaskBase{

		public CallAsyncTask(LoadingView loadingView) {
			super(loadingView);
			rlView.setVisibility(View.GONE);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = -1;
			try {
				Thread.sleep(100);
				result=1;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result==1){
				mCustomListView.onRefreshComplete();
				mAdapter.notifyDataSetChanged();
			}
			rlView.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		
	}
	
}
