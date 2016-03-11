package com.example.cim.fragment;

import com.example.cim.R;
import com.example.cim.ui.ChatActivity;
import com.example.cim.view.TitleBarView;
import com.example.testwebview.WebviewActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class DynamicFragment extends Fragment implements OnClickListener {

	private Context mContext;
	private View mBaseView;
	private TitleBarView mTitleBarView;
	private RelativeLayout localplugin;
	private RelativeLayout baidu;
	private RelativeLayout xinlang;
	private RelativeLayout tencent;
	private RelativeLayout souhu;
	private RelativeLayout fenghuang;
	private RelativeLayout wangyi;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_dynamic, null);
		findView();
		initTitleView();
		init();
		return mBaseView;
	}

	private void findView() {
		mTitleBarView = (TitleBarView) mBaseView.findViewById(R.id.title_bar);
		localplugin = (RelativeLayout) mBaseView.findViewById(R.id.rl_localplugin);
		baidu = (RelativeLayout) mBaseView.findViewById(R.id.rl_baidu);
		xinlang = (RelativeLayout) mBaseView.findViewById(R.id.rl_xinlang);
		tencent = (RelativeLayout) mBaseView.findViewById(R.id.rl_tencent);
		souhu = (RelativeLayout) mBaseView.findViewById(R.id.rl_souhu);
		fenghuang = (RelativeLayout) mBaseView.findViewById(R.id.rl_fenghuang);
		wangyi = (RelativeLayout) mBaseView.findViewById(R.id.rl_wangyi);
	}

	private void initTitleView() {
		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE,
				View.GONE);
		mTitleBarView.setTitleText(R.string.dynamic);
	}

	private void init() {
		baidu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
		});
		localplugin.setOnClickListener(this);
		baidu.setOnClickListener(this);
		xinlang.setOnClickListener(this);
		tencent.setOnClickListener(this);
		souhu.setOnClickListener(this);
		fenghuang.setOnClickListener(this);
		wangyi.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		 switch (v.getId()) {
		 case R.id.rl_localplugin:
			 openAvtivity("本地插件列表", "file:///android_asset/html/main.jsp");
			 break;
		 case R.id.rl_baidu:
			 openAvtivity("百度", "http://wap.baidu.com/");
			 break;
		 case R.id.rl_xinlang:
			 openAvtivity("新浪", "http://www.sina.cn/");
			 break;
		 case R.id.rl_tencent:
			 openAvtivity("腾讯", "http://3gqq.qq.com/");
			 break;
		 case R.id.rl_souhu:
			 openAvtivity("搜狐", "http://m.sohu.com/");
		 	break;
		 case R.id.rl_fenghuang:
			 openAvtivity("凤凰", "http://i.ifeng.com/");
			 break;
		 case R.id.rl_wangyi:
			 openAvtivity("网易", "http://3g.163.com/news/");
			 break;
		 }
	}
	
	public void openAvtivity(String title, String url){
		Intent intent = new Intent(mContext, WebviewActivity.class);
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		mContext.startActivity(intent);
		Activity activity = (Activity)mContext;
		activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
	}
}
