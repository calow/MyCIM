package com.example.cim.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.cim.R;
import com.example.cim.adapter.UsersAdapter;
import com.example.cim.model.SearchUserItem;
import com.example.cim.network.API;
import com.example.cim.network.HttpRequest;
import com.example.cim.network.HttpRequest.HttpCompliteListener;
import com.example.cim.ui.base.CIMMonitorActivity;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.view.ClearEditText;
import com.example.cim.view.TitleBarView;

public class SearchActivity extends CIMMonitorActivity implements
		OnClickListener {

	private LinearLayout mLinearLayout;

	private RelativeLayout mRelativeLayout;

	private TitleBarView mTittleBar;

	private ListView mListView;

	private ClearEditText mEditext;

	private UsersAdapter mAdapter;

	private List<SearchUserItem> mLists;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1024:
				mAdapter.notifyDataSetChanged();
				break;
			case 1025:
				showToast("查找出错");
				break;
			case 1026:
				showToast("查找异常");
				break;
			case 1027:
				int code = msg.getData().getInt("code");
				showToast("搜索异常:" + code);
				break;
			case 1028:
				String e = msg.getData().getString("e");
				showToast("搜索出错:" + e);
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		findView();
		init();
	}

	public void findView() {
		mTittleBar = (TitleBarView) findViewById(R.id.title_bar);
		mLinearLayout = (LinearLayout) findViewById(R.id.ll_constact_search);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_search_second);
		mListView = (ListView) findViewById(R.id.lv_search_result);
		mEditext = (ClearEditText) findViewById(R.id.filter_edit);
	}

	public void init() {
		mTittleBar.setCommonTitle(View.VISIBLE, View.VISIBLE, View.GONE,
				View.GONE);
		mTittleBar.setTitleText(R.string.string_addFriend);
		mTittleBar.setBtnLeft(R.drawable.boss_unipay_icon_back, R.string.back);
		mTittleBar.setBtnLeftOnclickListener(this);
		mLinearLayout.setOnClickListener(this);
		mEditext.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
						&& actionId == 0) {
					String searchValue = v.getText().toString().trim();
					if (searchValue != null && !searchValue.equals("")) {
						search(searchValue);
					} else {
						Toast.makeText(SearchActivity.this, "搜索内容不能为空",
								Toast.LENGTH_SHORT).show();
					}
					return true;
				}
				return false;
			}
		});
		mLists = new ArrayList<SearchUserItem>();
		mAdapter = new UsersAdapter(this, mLists, mListView);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SearchUserItem item = mLists.get(position);
				Intent intent = new Intent(SearchActivity.this,
						UserInfoOrAddActivity.class);
				intent.putExtra("userId", item.getUserId());
				intent.putExtra("userName", item.getUserName());
				intent.putExtra("userFeel", item.getUserFeel());
				intent.putExtra("userState", item.getUserState());
				intent.putExtra("type", "add");
				startActivity(intent);
			}

		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_constact_search:
			mRelativeLayout.setVisibility(View.VISIBLE);
			mEditext.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		case R.id.title_btn_left:
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		if (mRelativeLayout.getVisibility() == View.VISIBLE) {
			mRelativeLayout.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	public void search(String searchValue) {
		showProgressDialog("提示", "正在搜索，请稍后......");
		Map<String, String> user = new HashMap<String, String>();
		user.put("ULoginId", searchValue);
		user.put("UNickName",
				CIMDataConfig.getString(this, CIMDataConfig.KEY_ACCOUNT));
		HttpRequest request = new HttpRequest(new HttpCompliteListener() {

			@Override
			public void onResponseSuccess(String json) {
				hideProgressDialog();
				JSONObject jsonObject = null;
				String code = null;
				try {
					jsonObject = new JSONObject(json);
					code = jsonObject.getString("code");
					if (code != null && code.equals("200")) {
						String result = null;
						JSONArray array = null;
						result = jsonObject.getString("result");
						array = new JSONArray(result);
						System.out.println("result = " + array.toString());
						List<SearchUserItem> items = new ArrayList<SearchUserItem>();
						SearchUserItem item;
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = (JSONObject) array.get(i);
							item = new SearchUserItem();
							item.setUserId(object.getString("ULoginId"));
							item.setUserName(object.getString("UNickName"));
							item.setUserFeel(object.getString("USignture"));
							item.setUserState(object.getString("UState"));
							items.add(item);
						}
						mLists.clear();
						mLists.addAll(items);
						mHandler.sendEmptyMessage(1024);
					} else {
						mHandler.sendEmptyMessage(1025);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(1026);
				}
			}

			@Override
			public void onResponseError(int code) {
				hideProgressDialog();
				Message msg = new Message();
				msg.what = 1027;
				msg.getData().putInt("code", code);
				mHandler.sendMessage(msg);
			}

			@Override
			public void onRequestException(Exception e) {
				hideProgressDialog();
				Message msg = new Message();
				msg.what = 1028;
				msg.getData().putString("e", e.toString());
				mHandler.sendMessage(msg);
			}
		});
		request.httpPost(API.USERSEARCH_URL, user);
	}

}
