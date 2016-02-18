package com.example.cim.listener;

import android.net.NetworkInfo;

import com.example.cim.nio.mutual.Message;
import com.example.cim.nio.mutual.ReplyBody;


public interface OnCIMMessageListener {

	
	/**
	 * ���յ���������͹�������Ϣʱ����
	 * @param message
	 */
	public abstract void onMessageReceived(Message message);
	
	/**
	 * ������CIMPushManager.sendRequest()�����˷������󣬻����Ӧʱ����
	 * @param replyBody
	 */
	public abstract void onReplyReceived(ReplyBody replyBody);
	
	/**
	 * ������״̬�����仯ʱ����
	 * @param networkInfo
	 */
	public abstract void onNetworkChanged(NetworkInfo networkInfo);
	
	/**
	 * ��ȡ���Ƿ����ӵ�������
	 * ͨ������CIMPushManager.detectIsConnected()���첽��ȡ
	 * @param isConnected
	 */
	public abstract void onConnectionStatus(boolean isConnected);
	
	/**
	 * ���ӷ���˳ɹ�
	 */
	public abstract void onConnectionSucceed();
	
	/**
	 * ���ӶϿ�
	 */
	public abstract void onConnectionClosed();
	
	/**
	 * ����ҳ�����UI
	 */
	public abstract void notifyUIChanged(String flag);
}
