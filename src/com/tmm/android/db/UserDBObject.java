package com.tmm.android.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;






public class UserDBObject implements Serializable {

	
	private static final long serialVersionUID = 4011988L;
	private String operationType;
		
	private List<UserDBObject> friendList ; 
	private String phoneNumber;
	private boolean isError;
	private String errorDesc;
	
	public UserDBObject(){
		friendList = new  ArrayList<UserDBObject>();
	}
	
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}


	/**
	 * this class will work as the place holder for the quiz object.
	 * An extra variable is added to store answer given by user.
	 */
	
	 Id _id;

	
       
	public Id get_id() {
		return _id;
	}
	public void set_id(Id _id) {
		this._id = _id;
	}
	
	
	private String username;
	private String oauthprovider;
	private String location_long;
	private String location_lat;	
	
	public String getLocation_long() {
		return location_long;
	}
	public void setLocation_long(String location_long) {
		this.location_long = location_long;
	}
	public String getLocation_lat() {
		return location_lat;
	}
	public void setLocation_lat(String location_lat) {
		this.location_lat = location_lat;
	}
	public String getUserName() {
		return username;
	}
	public void setUserName(String username) {
		this.username = username;
	}
	
	
	public String getOauthProvider() {
		return oauthprovider;
	}
	public void setOauthProvider(String oauthprovider) {
		this.oauthprovider = oauthprovider;
	}
	
	static class Id{
		String $id;

		public String get$id() {
			return $id;
		}

		public void set$id(String $id) {
			this.$id = $id;
		}
		
	}

	public List<UserDBObject> getFriendList() {
		return friendList;
	}
	public void setFriendList(List<UserDBObject> friendList) {
		this.friendList = friendList;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getOauthprovider() {
		return oauthprovider;
	}
	public void setOauthprovider(String oauthprovider) {
		this.oauthprovider = oauthprovider;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	@Override
	public String toString() {
		return "UserDBObject [operationType=" + operationType + ", friendList="
				+ friendList + ", phoneNumber=" + phoneNumber + ", isError="
				+ isError + ", errorDesc=" + errorDesc + ", _id=" + _id
				+ ", username=" + username + ", oauthprovider=" + oauthprovider
				+ ", location_long=" + location_long + ", location_lat="
				+ location_lat + "]";
	}

	
	
	
	
	
}

