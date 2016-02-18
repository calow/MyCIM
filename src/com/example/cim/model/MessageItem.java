package com.example.cim.model;

import java.io.Serializable;

public class MessageItem implements Serializable {

	private static final long serialVersionUID = 7609031526811882573L;

	public String user_name;
	public String user_content;
	public String user_time;
	public String user_picture;

	public MessageItem() {
	}

	public MessageItem(String user_name, String user_content, String user_time,
			String user_picture) {
		super();
		this.user_name = user_name;
		this.user_content = user_content;
		this.user_time = user_time;
		this.user_picture = user_picture;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_content() {
		return user_content;
	}

	public void setUser_content(String user_content) {
		this.user_content = user_content;
	}

	public String getUser_time() {
		return user_time;
	}

	public void setUser_time(String user_time) {
		this.user_time = user_time;
	}

	public String getUser_picture() {
		return user_picture;
	}

	public void setUser_picture(String user_picture) {
		this.user_picture = user_picture;
	}
}
