package com.example.cim.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cim.R;
import com.example.cim.adapter.SortAdapter;
import com.example.cim.asynctask.AsyncTaskBase;
import com.example.cim.sort.CharacterParser;
import com.example.cim.sort.PinyinComparator;
import com.example.cim.sort.SortModel;
import com.example.cim.util.ConstactUtil;
import com.example.cim.view.ClearEditText;
import com.example.cim.view.LoadingView;
import com.example.cim.view.SideBar;
import com.example.cim.view.SideBar.OnTouchingLetterChangedListener;

public class PhoneConstactFragment extends Fragment {

	private Context mContext;
	private View mBaseView;
	private ClearEditText mClearEditText;
	private ListView sortListView;
	private SideBar sideBar;
	private LoadingView mLoadingView;
	private TextView dialog;
	private SortAdapter adapter;

	private Map<String, String> callRecords;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_phone_constact, null);
		findView();
		init();
		return mBaseView;
	}

	private void findView() {
		mClearEditText = (ClearEditText) mBaseView
				.findViewById(R.id.filter_edit);
		sortListView = (ListView) mBaseView
				.findViewById(R.id.country_lvcountry);
		sideBar = (SideBar) mBaseView.findViewById(R.id.sidebar);
		mLoadingView = (LoadingView) mBaseView.findViewById(R.id.loading);
		dialog = (TextView) mBaseView.findViewById(R.id.dialog);
	}

	private void init() {
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		sideBar.setTextView(dialog);
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}
			}
		});

		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String name = ((SortModel) adapter.getItem(position)).getName();
				Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
			}
		});

		new AsyncTaskConstact(mLoadingView).execute(0);

	}

	private class AsyncTaskConstact extends AsyncTaskBase {

		public AsyncTaskConstact(LoadingView loadingView) {
			super(loadingView);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = -1;
			callRecords = ConstactUtil.getAllCallRecords(mContext);
			result = 1;
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == 1) {
				List<String> constact = new ArrayList<String>();
				for (Iterator<String> keys = callRecords.keySet().iterator(); keys
						.hasNext();) {
					String key = keys.next();
					constact.add(key);
				}
				String[] names = new String[] {};
				names = constact.toArray(names);
				SourceDateList = findData(names);
				Collections.sort(SourceDateList, new PinyinComparator());

				adapter = new SortAdapter(mContext, SourceDateList);
				sortListView.setAdapter(adapter);
				mClearEditText.setMyTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
						filterData(s.toString());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});

			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}

	/**
	 * @描述：将名字转化为拼音的model对象
	 * @param names
	 *            名字的数组
	 * @return 返回名字及其拼音的model对象的list集合
	 */
	public List<SortModel> findData(String[] names) {
		List<SortModel> mSortList = new ArrayList<SortModel>();
		for (int i = 0; i < names.length; i++) {
			SortModel model = new SortModel();
			model.setName(names[i]);

			String pinyin = characterParser.getSelling(names[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if (sortString.matches("[A-Z]")) {
				model.setSortLetters(sortString);
			} else {
				model.setSortLetters("#");
			}
			mSortList.add(model);
		}
		return mSortList;
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel model : SourceDateList) {
				String name = model.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(model);
				}
			}
		}
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

}
