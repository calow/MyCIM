package com.example.cim.fragment;

import java.util.HashMap;
import java.util.Map;

import com.example.cim.R;
import com.example.cim.network.API;
import com.example.cim.network.HttpRequest;
import com.example.cim.network.HttpRequest.HttpCompliteListener;
import com.example.cim.view.TitleBarView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class SettingFragment extends Fragment {

	private Context mContext;
	private View mBaseView;
	private TitleBarView mTitleBarView;
	private RelativeLayout mRelativeLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_mine, null);
		findView();
		initTitleView();
		init();
		return mBaseView;
	}

	public void findView() {
		mTitleBarView = (TitleBarView) mBaseView.findViewById(R.id.title_bar);
		mRelativeLayout = (RelativeLayout) mBaseView
				.findViewById(R.id.rl_userpic);
	}

	public void initTitleView() {
		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE,
				View.GONE);
		mTitleBarView.setTitleText(R.string.setting);
	}

	public void init() {
		mRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				HttpRequest request = new HttpRequest(
						new HttpCompliteListener() {

							@Override
							public void onResponseSuccess(String json) {
								System.out.println("上传图片成功");
							}

							@Override
							public void onResponseError(int code) {
								System.out.println("上传图片失败");
							}

							@Override
							public void onRequestException(Exception e) {
								System.out.println("上传图片异常");
							}
						});
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", "pic");
				map.put("format", "jpg");
				request.httpPost(API.UpAndDown_URL + "upload_file.action", map, null);
			}
		});
	}

}
