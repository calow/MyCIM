package com.example.cim.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.example.cim.R;
import com.example.cim.fragment.CIMconstactFragment;
import com.example.cim.view.TitleBarView;

public class CIMconstactActivity extends FragmentActivity {

	private Context mContext;

	private TitleBarView mTitleBarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_cim_constact);
		findView();
		init();
	}

	private void findView() {
		mTitleBarView = (TitleBarView) findViewById(R.id.title_bar);
	}

	private void init() {
		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE,
				View.GONE);
		mTitleBarView.setTitleText(R.string.choose_constact);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		CIMconstactFragment ciMconstactFragment = new CIMconstactFragment();
		ft.replace(R.id.rl_constact, ciMconstactFragment);
		ft.commit();
	}

}
