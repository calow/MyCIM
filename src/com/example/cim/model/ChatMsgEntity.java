package com.example.cim.model;

public class ChatMsgEntity {

	private String date;

	private int resId;//Í·ÏñÍ¼Æ¬Id
	
	private String name;

	private String text;

	private boolean isComMeg = true;
	
	private int sendStatu = 0;
	
	private int messageId;

	public ChatMsgEntity() {
	}

	public ChatMsgEntity(String date, int resId, String name, String text, boolean isComMeg, int sendStatu, int messageId) {
		this.date = date;
		this.resId = resId;
		this.name = name;
		this.text = text;
		this.isComMeg = isComMeg;
		this.sendStatu = sendStatu;
		this.messageId = messageId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isComMeg() {
		return isComMeg;
	}

	public void setComMeg(boolean isComMeg) {
		this.isComMeg = isComMeg;
	}

	public int getSendStatu() {
		return sendStatu;
	}

	public void setSendStatu(int sendStatu) {
		this.sendStatu = sendStatu;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	
}
