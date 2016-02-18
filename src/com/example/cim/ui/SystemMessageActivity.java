package com.example.cim.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.cim.R;
import com.example.cim.nio.mutual.Message;
import com.example.cim.ui.base.CIMMonitorActivity;

public class SystemMessageActivity extends CIMMonitorActivity implements
		OnClickListener {
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_chat);
		initViews();
	}
	
	public void initViews(){
		
	}

	@Override
	public void onMessageReceived(Message message) {
		
	}

	@Override
	public void onClick(View v) {

	}

}
