/**
 * 
 */
package com.tmm.android.twitter.appliaction;


import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import android.app.Application;

import com.tmm.android.db.PhoneNumbersEntity;
import com.tmm.android.db.UserDBObject;
import com.tmm.android.db.UserData;

/**
 * @author rob
 *
 */
public class TwitterApplication extends Application{
	
	
	private Twitter twitter;
	private UserDBObject mUserDBObject;
	private PhoneNumbersEntity mPhoneNumbersEntity;
	private String phoneNumber;
	private UserData userData;
	public TwitterApplication(){
		super();
		mUserDBObject = new UserDBObject();
		mPhoneNumbersEntity = new PhoneNumbersEntity();
		phoneNumber = "";
		
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.userData = new UserData(this);
	}
	


	/**
	 * @return the twitter
	 */
	public Twitter getTwitter() {
		return twitter;
	}

	/**
	 * @param twitter the twitter to set
	 */
	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	private OAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;
	

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(OAuthProvider provider) {
		this.provider = provider;
	}

	/**
	 * @return the provider
	 */
	public OAuthProvider getProvider() {
		return provider;
	}

	/**
	 * @param consumer the consumer to set
	 */
	public void setConsumer(CommonsHttpOAuthConsumer consumer) {
		this.consumer = consumer;
	}

	/**
	 * @return the consumer
	 */
	public CommonsHttpOAuthConsumer getConsumer() {
		return consumer;
	}
	
	public UserDBObject getmUserDBObject() {
		return mUserDBObject;
	}

	public void setmUserDBObject(UserDBObject mUserDBObject) {
		this.mUserDBObject = mUserDBObject;
	}





	public PhoneNumbersEntity getmPhoneNumbersEntity() {
		return mPhoneNumbersEntity;
	}





	public String getPhoneNumber() {
		return phoneNumber;
	}





	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}





	public void setmPhoneNumbersEntity(PhoneNumbersEntity mPhoneNumbersEntity) {
		this.mPhoneNumbersEntity = mPhoneNumbersEntity;
	}





	public UserData getUserData() {
		return userData;
	}





	public void setUserData(UserData userData) {
		this.userData = userData;
	}

	



}
