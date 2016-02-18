package com.example.cim.model;

public class RecentChat {
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

	public RecentChat(String userName, String userFeel, String userTime,
			String imgPath, String statu, String groupId, String userId,
			int groupType, String groupName, int count) {
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

}
