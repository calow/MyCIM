package com.example.cim.fragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
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
import com.example.cim.adapter.ToolsAdapter;
import com.example.cim.asynctask.AsyncTaskBase;
import com.example.cim.model.ToolEntity;
import com.example.cim.network.API;
import com.example.cim.nio.constant.Constant;
import com.example.cim.ui.ToolActivity;
import com.example.cim.view.CustomListView;
import com.example.cim.view.CustomListView.OnRefreshListener;
import com.example.cim.view.LoadingView;
import com.example.testwebview.WebviewActivity;

public class PCToolFragment extends Fragment implements OnItemClickListener {

	private static final String TAG = "PCToolFragment";
	private Context mContext;
	private View mBaseView;
	private View mSearchView;
	private CustomListView mCustomListView;
	private ToolsAdapter adapter;
	private LoadingView mLoadingView;

	LinkedList<ToolEntity> tes = new LinkedList<ToolEntity>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_pctoollist, null);
		mSearchView = inflater.inflate(R.layout.common_search_l, null);
		findView();
		init();
		return mBaseView;
	}

	public void findView() {
		mCustomListView = (CustomListView) mBaseView
				.findViewById(R.id.lv_tools);
		mLoadingView = (LoadingView) mBaseView.findViewById(R.id.loading);
	}

	public void init() {
		adapter = new ToolsAdapter(mContext, tes, mCustomListView);
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
		new ToolsAsyncTask(mLoadingView).execute(0);
	}

	private class ToolsAsyncTask extends AsyncTaskBase {

		List<ToolEntity> recentTes = new ArrayList<ToolEntity>();

		public ToolsAsyncTask(LoadingView loadingView) {
			super(loadingView);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = -1;
			recentTes = new ArrayList<ToolEntity>();
			try {
				String response = API.httpPost(API.PcToolList_URL, null);
				if (response != null && !response.equals("")
						&& !response.equals("null")) {
					JSONObject object = new JSONObject(response);
					JSONArray data = object.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = data.getJSONObject(i);
						ToolEntity entity = new ToolEntity();
						entity.setToolId(jsonObject.getString("toolId"));
						entity.setToolName(jsonObject.getString("toolName"));
						entity.setPlateform(jsonObject.getString("platform"));
						entity.setDescription(jsonObject
								.getString("description"));
						entity.setTvId(jsonObject.getString("tvId"));
						recentTes.add(entity);
					}
				}
				if (recentTes.size() >= 0) {
					result = 1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			tes.clear();
			tes.addAll(recentTes);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	private class AsyncRefresh extends
			AsyncTask<Integer, Integer, List<ToolEntity>> {

		private List<ToolEntity> recentTes = new ArrayList<ToolEntity>();

		@Override
		protected List<ToolEntity> doInBackground(Integer... params) {
			recentTes = new ArrayList<ToolEntity>();
			try {
				String response = API.httpPost(API.PcToolList_URL, null);
				if (response != null && !response.equals("")
						&& !response.equals("null")) {
					JSONObject object = new JSONObject(response);
					JSONArray data = object.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = data.getJSONObject(i);
						ToolEntity entity = new ToolEntity();
						entity.setToolId(jsonObject.getString("toolId"));
						entity.setToolName(jsonObject.getString("toolName"));
						entity.setPlateform(jsonObject.getString("platform"));
						entity.setDescription(jsonObject
								.getString("description"));
						entity.setTvId(jsonObject.getString("tvId"));
						recentTes.add(entity);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			return recentTes;
		}

		@Override
		protected void onPostExecute(List<ToolEntity> result) {
			super.onPostExecute(result);
			if (result != null) {
				updateItemStatu(result);
			}
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
			ToolEntity te = (ToolEntity) adapter.getItem(Integer
					.parseInt(String.valueOf(id)));
			Intent intent = new Intent(mContext, WebviewActivity.class);
			String url = Constant.SERVER_URL + "/tool/tool_run.action?toolId=" + te.getToolId();
			intent.putExtra("url", url);
			intent.putExtra("title", te.getToolName());
			mContext.startActivity(intent);
			Activity activity = (Activity)mContext;
			activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
		} else {
			System.out.println("ËÑË÷¿ò±»µã»÷£¡");
		}
	}

	public void updateItemStatu(List<ToolEntity> list) {
		tes.clear();
		tes.addAll(list);
		adapter.notifyDataSetChanged();
	}

}
