package com.tmm.android.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhoneNumbersEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1197541481573050033L;
	List<String> phoneNumbers;
	public PhoneNumbersEntity() {
		super();
		this.phoneNumbers = new ArrayList<String>();
	}
	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}
	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

}
