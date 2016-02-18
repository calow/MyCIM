package com.example.cim.util;

public enum KeyName {
	client_bind, client_logout, client_heartbeat, sessionClosedHander, client_get_offline_message, client_get_unread_message, client_get_online_friends, client_update_offline_message, client_get_group_message;

	public static KeyName getKey(String keyName) {
		return valueOf(keyName.toLowerCase());
	}
}
