package com.tmm.android.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FriendListEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2225778534523031507L;
	
	
	List <UserDBObject> friendList;
	public FriendListEntity() {
		super();
		friendList = new ArrayList<UserDBObject>();
	}
	
	
	public List<UserDBObject> getFriendList() {
		return friendList;
	}
	public void setFriendList(List<UserDBObject> friendList) {
		this.friendList = friendList;
	}


	@Override
	public String toString() {
		return "FriendListEntity [friendList=" + friendList + "]";
	}
	
	
}
