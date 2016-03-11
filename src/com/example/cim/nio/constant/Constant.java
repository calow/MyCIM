package com.example.cim.nio.constant;

/**
 * 
 * @version 1.0
 */
public interface Constant {

	// 服务端web地址
	// public static final String SERVER_URL =
	// "http://10.0.2.2:8080/ichat-server";
	// //服务端IP地址
	// public static final String CIM_SERVER_HOST = "10.0.2.2";
	
	public static String locationUrl = "file://";
	
	// 服务端web地址
	public static final String SERVER_URL = "http://222.201.139.178:8080/MinaServer";
	// 服务端IP地址
	public static final String CIM_SERVER_HOST = "222.201.139.178";

	// 注意，这里的端口不是tomcat的端口，CIM端口在服务端spring-cim.xml中配置的，没改动就使用默认的23456
	public static final int CIM_SERVER_PORT = 23456;

	public static interface MessageType {

		// 用户之间的普通消�?
		public static final String TYPE_0 = "0";

		// 下线类型
		String TYPE_999 = "999";
	}

	public static interface MessageStatus {

		// 消息未读
		public static final String STATUS_0 = "0";
		// 消息已经读取
		public static final String STATUS_1 = "1";
	}
	
	public static final String NOTIFY_UI_CHANGED = "com.example.cim.NOTIFY_CHANGED";
	
	public static final String NOTIFY_UI_CHANGED_FLAG = "FLAG_TYPE";

	public static interface UIChangeType {
		
		public static final String MESSAGELIST = "messagelist";
		
	}

}
