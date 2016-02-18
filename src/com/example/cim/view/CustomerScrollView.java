package com.example.cim.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CustomerScrollView extends ScrollView {

	private Context mContext;

	public CustomerScrollView(Context context) {
		super(context);
		this.mContext = context;
	}

	public CustomerScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public CustomerScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

}
