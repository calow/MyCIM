package com.example.cim.listener;

import android.net.NetworkInfo;

import com.example.cim.nio.mutual.Message;
import com.example.cim.nio.mutual.ReplyBody;


public interface OnCIMMessageListener {

	
	/**
	 * 当收到服务端推送过来的消息时调用
	 * @param message
	 */
	public abstract void onMessageReceived(Message message);
	
	/**
	 * 当调用CIMPushManager.sendRequest()向服务端发送请求，获得相应时调用
	 * @param replyBody
	 */
	public abstract void onReplyReceived(ReplyBody replyBody);
	
	/**
	 * 当网络状态发生变化时调用
	 * @param networkInfo
	 */
	public abstract void onNetworkChanged(NetworkInfo networkInfo);
	
	/**
	 * 获取到是否连接到服务器
	 * 通过调用CIMPushManager.detectIsConnected()来异步获取
	 * @param isConnected
	 */
	public abstract void onConnectionStatus(boolean isConnected);
	
	/**
	 * 连接服务端成功
	 */
	public abstract void onConnectionSucceed();
	
	/**
	 * 连接断开
	 */
	public abstract void onConnectionClosed();
	
	/**
	 * 让主页面更新UI
	 */
	public abstract void notifyUIChanged(String flag);
}
