package com.example.cim.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatMsgEntity implements Parcelable {

	private String date;

	private int resId;// Í·ÏñÍ¼Æ¬Id

	private String name;

	private String text;

	private boolean isComMeg = true;

	private int sendStatu = 0;

	private int messageId;

	public ChatMsgEntity() {
	}

	public ChatMsgEntity(String date, int resId, String name, String text,
			boolean isComMeg, int sendStatu, int messageId) {
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(date);
		dest.writeInt(resId);
		dest.writeString(name);
		dest.writeString(text);
		dest.writeByte((byte)(isComMeg ? 1 : 0));
		dest.writeInt(sendStatu);
		dest.writeInt(messageId);
	}

	public static final Parcelable.Creator<ChatMsgEntity> CREATOR = new Parcelable.Creator<ChatMsgEntity>() {

		@Override
		public ChatMsgEntity createFromParcel(Parcel source) {
			return new ChatMsgEntity(source);
		}

		@Override
		public ChatMsgEntity[] newArray(int size) {
			return new ChatMsgEntity[size];
		}

	};

	private ChatMsgEntity(Parcel in) {
		date = in.readString();
		resId = in.readInt();
		name = in.readString();
		text = in.readString();
		isComMeg = in.readByte() == 0 ? false : true;
		sendStatu = in.readInt();
		messageId = in.readInt();
	}

}
