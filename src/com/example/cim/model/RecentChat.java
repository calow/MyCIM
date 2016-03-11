package com.example.cim.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecentChat implements Parcelable {
	private String userName;
	private String userFeel;
	private String userTime;
	private String imgPath;
	private String statu;
	private String groupId;
	private String userId;
	private int groupType;
	private String groupName;
	private int count;
	private String faceToUserId;

	public RecentChat(String userName, String userFeel, String userTime,
			String imgPath, String statu, String groupId, String userId,
			int groupType, String groupName, int count, String faceToUserId) {
		super();
		this.userName = userName;
		this.userFeel = userFeel;
		this.userTime = userTime;
		this.imgPath = imgPath;
		this.statu = statu;
		this.groupId = groupId;
		this.userId = userId;
		this.groupType = groupType;
		this.groupName = groupName;
		this.count = count;
		this.faceToUserId = faceToUserId;
	}

	public RecentChat() {
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserFeel() {
		return userFeel;
	}

	public void setUserFeel(String userFeel) {
		this.userFeel = userFeel;
	}

	public String getUserTime() {
		return userTime;
	}

	public void setUserTime(String userTime) {
		this.userTime = userTime;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getStatu() {
		return statu;
	}

	public void setStatu(String statu) {
		this.statu = statu;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getFaceToUserId() {
		return faceToUserId;
	}

	public void setFaceToUserId(String faceToUserId) {
		this.faceToUserId = faceToUserId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userName);
		dest.writeString(userFeel);
		dest.writeString(userTime);
		dest.writeString(imgPath);
		dest.writeString(statu);
		dest.writeString(groupId);
		dest.writeString(userId);
		dest.writeInt(groupType);
		dest.writeString(groupName);
		dest.writeInt(count);
		dest.writeString(faceToUserId);
	}

	public static final Parcelable.Creator<RecentChat> CREATOR = new Parcelable.Creator<RecentChat>() {

		@Override
		public RecentChat createFromParcel(Parcel source) {
			return new RecentChat(source);
		}

		@Override
		public RecentChat[] newArray(int size) {
			return new RecentChat[size];
		}

	};

	private RecentChat(Parcel in) {
		userName = in.readString();
		userFeel = in.readString();
		userTime = in.readString();
		imgPath = in.readString();
		statu = in.readString();
		groupId = in.readString();
		userId = in.readString();
		groupType = in.readInt();
		groupName = in.readString();
		count = in.readInt();
		faceToUserId = in.readString();

	}

}
