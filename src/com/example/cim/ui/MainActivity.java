package com.example.cim.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cim.R;
import com.example.cim.cache.MessageListManage;
import com.example.cim.fragment.ConstactFatherFragment;
import com.example.cim.fragment.DynamicFragment;
import com.example.cim.fragment.NewsFragmentFather;
import com.example.cim.fragment.SettingFragment;
import com.example.cim.fragment.ToolFragmentFather;
import com.example.cim.listener.OnCIMMessageListener;
import com.example.cim.manager.CIMListenerManager;
import com.example.cim.manager.CIMPushManager;
import com.example.cim.model.RecentChat;
import com.example.cim.nio.constant.Constant;
import com.example.cim.nio.mutual.Message;
import com.example.cim.nio.mutual.ReplyBody;
import com.example.cim.ui.base.CIMMonitorFragmentActivity;
import com.example.cim.util.MyActivityManager;
import com.example.cim.util.CIMDataConfig;
import com.example.cim.util.KeyName;

public class MainActivity extends CIMMonitorFragmentActivity {

	protected static final String TAG = "MainActivity";

	private Context mContext;
	private ImageButton mNews, mContact, mDynamic, mSetting;
	private View mPopView;
	private View currentButton;

	private TextView app_cancle;
	private TextView app_exit;
	private TextView app_change;

	private PopupWindow mPopupWindow;
	private LinearLayout buttomBarGroup;


	private NewsFragmentFather newsFragmentFather;
	private ConstactFatherFragment constactFatherFragment;
	private DynamicFragment dynamicFragment;
	private SettingFragment settingFragment;
	private ToolFragmentFather toolFragmentFather;
	private FragmentManager fm;
	
	private boolean exitFlag = false;
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			exitFlag = false;
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		findView();
		init();
	}

	private void findView() {
		mPopView = LayoutInflater.from(mContext).inflate(R.layout.app_exit,
				null);
		buttomBarGroup = (LinearLayout) findViewById(R.id.buttom_bar_group);
		mNews = (ImageButton) findViewById(R.id.buttom_news);
		mContact = (ImageButton) findViewById(R.id.buttom_contact);
		mDynamic = (ImageButton) findViewById(R.id.buttom_dynamic);
		mSetting = (ImageButton) findViewById(R.id.buttom_setting);

		app_cancle = (TextView) mPopView.findViewById(R.id.app_cancle);
		app_exit = (TextView) mPopView.findViewById(R.id.app_exit);
		app_change = (TextView) mPopView.findViewById(R.id.app_change_user);
	}

	private void init() {
		CIMPushManager.detectIsConnected(this);

		newsFragmentFather = new NewsFragmentFather();
		fm = getSupportFragmentManager();
		fm.beginTransaction().add(R.id.fl_content, newsFragmentFather).commit();

		mNews.setOnClickListener(newsOnclickListener);
		mContact.setOnClickListener(contactOnclickListener);
		mDynamic.setOnClickListener(dynamicOnclickListener);
		mSetting.setOnClickListener(settingOnclickListener);

		mNews.performClick();

		mPopupWindow = new PopupWindow(mPopView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);

		app_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});

		app_change.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		app_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private OnClickListener newsOnclickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setButton(v);
			FragmentTransaction ft = fm.beginTransaction();
			if (newsFragmentFather == null) {
				newsFragmentFather = new NewsFragmentFather();
				ft.add(R.id.fl_content, newsFragmentFather);
			}
			ft.show(newsFragmentFather);
			if (constactFatherFragment != null) {
				ft.hide(constactFatherFragment);
			}
			if (dynamicFragment != null) {
				ft.hide(dynamicFragment);
			}
//			if(settingFragment != null){
//				ft.hide(settingFragment);
//			}
			if(toolFragmentFather != null){
				ft.hide(toolFragmentFather);
			}
			ft.commit();
		}
	};

	private OnClickListener contactOnclickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setButton(v);
			FragmentTransaction ft = fm.beginTransaction();
			if (constactFatherFragment == null) {
				constactFatherFragment = new ConstactFatherFragment();
				ft.add(R.id.fl_content, constactFatherFragment);
			}
			ft.show(constactFatherFragment);
			if (newsFragmentFather != null) {
				ft.hide(newsFragmentFather);
			}
			if (dynamicFragment != null) {
				ft.hide(dynamicFragment);
			}
//			if(settingFragment != null){
//				ft.hide(settingFragment);
//			}
			if(toolFragmentFather != null){
				ft.hide(toolFragmentFather);
			}
			ft.commit();
		}
	};

	private OnClickListener dynamicOnclickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setButton(v);
			FragmentTransaction ft = fm.beginTransaction();
			if (dynamicFragment == null) {
				dynamicFragment = new DynamicFragment();
				ft.add(R.id.fl_content, dynamicFragment);
			}
			ft.show(dynamicFragment);
			if (newsFragmentFather != null) {
				ft.hide(newsFragmentFather);
			}
			if (constactFatherFragment != null) {
				ft.hide(constactFatherFragment);
			}
//			if(settingFragment != null){
//				ft.hide(settingFragment);
//			}
			if(toolFragmentFather != null){
				ft.hide(toolFragmentFather);
			}
			ft.commit();
		}
	};

	private OnClickListener settingOnclickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setButton(v);
			FragmentTransaction ft = fm.beginTransaction();
//			if(settingFragment == null){
//				settingFragment = new SettingFragment();
//				ft.add(R.id.fl_content, settingFragment);
//			}
//			ft.show(settingFragment);
			if(toolFragmentFather == null){
				toolFragmentFather = new ToolFragmentFather();
				ft.add(R.id.fl_content, toolFragmentFather);
			}
			ft.show(toolFragmentFather);
			if (newsFragmentFather != null) {
				ft.hide(newsFragmentFather);
			}
			if (constactFatherFragment != null) {
				ft.hide(constactFatherFragment);
			}
			if (dynamicFragment != null) {
				ft.hide(dynamicFragment);
			}
			ft.commit();
		}
	};

	private void setButton(View v) {
		if (currentButton != null && currentButton.getId() != v.getId()) {
			currentButton.setEnabled(true);
		}
		v.setEnabled(false);
		currentButton = v;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#b0000000")));
			mPopupWindow.showAtLocation(buttomBarGroup, Gravity.BOTTOM, 0, 0);
			mPopupWindow.setAnimationStyle(R.style.app_pop);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			mPopupWindow.update();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConnectionStatus(boolean isConnected) {
		if (isConnected) {
			CIMPushManager.setAccount(this, CIMDataConfig.getString(mContext,
					CIMDataConfig.KEY_ACCOUNT));
		} else {
			CIMPushManager.init(MainActivity.this, Constant.CIM_SERVER_HOST,
					Constant.CIM_SERVER_PORT);
		}
	}

	/**
	 * 收到消息后更新消息列表
	 */
	@Override
	public void onMessageReceived(Message message) {
		if (message != null) {
			ArrayList<RecentChat> list = MessageListManage.getInstance(mContext)
					.getChatRoomList(
							null,
							CIMDataConfig.getString(mContext,
									CIMDataConfig.KEY_ACCOUNT));
			newsFragmentFather.updateMessageList(list);
		}
	}

	@Override
	public void onReplyReceived(ReplyBody replyBody) {
		String keyName = replyBody.getKey();
		switch (KeyName.getKey(keyName)) {
		case client_bind:
			System.out.println("收到绑定回复");
			break;
		case client_logout:
			System.out.println("收到退出回复");
			break;
		case client_heartbeat:
			System.out.println("收到心跳回复");
			break;
		case sessionClosedHander:
			System.out.println("收到关闭session回复");
			break;
		case client_get_unread_message:
			System.out.println("收到未读消息列表回复");
			// 显示未读消息列表到第一个fragment中
			String unReadList = replyBody.getMessage();
			dispatchUnReadMessage(unReadList);
			break;
		case client_get_offline_message:
			System.out.println("收到离线消息列表回复");
			break;
		case client_get_online_friends:
			System.out.println("收到好友分组列表回复");
			String friendList = replyBody.getMessage();
			dispatchFriendList(friendList);
			break;
		case client_update_offline_message:
			System.out.println("收到更新离线消息回复");
			break;
		case client_get_group_message:
			System.out.println("收到某个聊天室未读消息");
		}
	}

	public void dispatchUnReadMessage(String message) {
		if (message != null && !message.equals("") && !message.equals("null")) {
			ArrayList<RecentChat> list = MessageListManage.getInstance(mContext)
					.getChatRoomList(
							null,
							CIMDataConfig.getString(mContext,
									CIMDataConfig.KEY_ACCOUNT));
			newsFragmentFather.updateMessageList(list);
		}
	}

	public void dispatchFriendList(String message) {
		if (message != null && !message.equals("") && !message.equals("null")) {
			if (constactFatherFragment != null
					&& constactFatherFragment.isConstactFragmentExit()) {
				// 更新好友列表
				constactFatherFragment.updateFriendlist(null, CIMDataConfig
						.getString(mContext, CIMDataConfig.KEY_ACCOUNT));
			}
		}
	}

	@Override
	public void notifyUIChanged(String flag) {
		if (flag.equals(Constant.UIChangeType.MESSAGELIST)) {
			ArrayList<RecentChat> list = MessageListManage.getInstance(mContext)
					.getChatRoomList(
							null,
							CIMDataConfig.getString(mContext,
									CIMDataConfig.KEY_ACCOUNT));
			newsFragmentFather.updateMessageList(list);
		}
	}
	
	/**
     * 监听Back键按下事件,方法1:
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */ 
    @Override 
    public void onBackPressed() {
    	if(!exitFlag){//第一次点击返回键
    		exitFlag = true;
    		runOnUiThread(new Runnable() {
    			
    			@Override
    			public void run() {
    				Toast.makeText(getApplicationContext(), "再按一次退出程序",
    	                    Toast.LENGTH_SHORT).show();
    	            // 利用handler延迟发送更改状态信息
    			}
    		});
    		mHandler.sendEmptyMessageDelayed(0, 2000);
    	}else{
    		ArrayList<Activity> list = MyActivityManager.getLists();
    		for(int i = 0; i < list.size(); i++){
    			if(MainActivity.this != list.get(i)){
    				list.get(i).finish();
    			}
    		}
    		finish();
    		System.exit(0);
    	}
    }
}
