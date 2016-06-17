package com.example.cim.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchUserItem implements Parcelable {

	private String userId;

	private String userName;

	private String userFeel;
	
	private String userState;
	
	public SearchUserItem(){
		
	}

	public SearchUserItem(String userId, String userName, String userFeel, String userState) {
		this.userId = userId;
		this.userName = userName;
		this.userFeel = userFeel;
		this.userState = userState;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
	
	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userId);
		dest.writeString(userName);
		dest.writeString(userFeel);
		dest.writeString(userState);
	}

	public static final Parcelable.Creator<SearchUserItem> CREATOR = new Parcelable.Creator<SearchUserItem>() {

		@Override
		public SearchUserItem createFromParcel(Parcel source) {
			return new SearchUserItem(source);
		}

		@Override
		public SearchUserItem[] newArray(int size) {
			return new SearchUserItem[size];
		}

	};

	private SearchUserItem(Parcel in) {
		userId = in.readString();
		userName = in.readString();
		userFeel = in.readString();
		userState = in.readString();
	}

}
