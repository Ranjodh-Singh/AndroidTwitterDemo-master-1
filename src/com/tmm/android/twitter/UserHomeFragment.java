package com.tmm.android.twitter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tmm.android.twitter.appliaction.TwitterApplication;
import com.tmm.android.twitter.util.Constants;

public class UserHomeFragment extends Fragment {
	public static final int USERHOMEFRAGMENTPOS = 1;
	public static final String TAG = "UserHomeFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.user_home_page, container, false);
		getPhoneNumber();	
		Button logoutButton = (Button) view.findViewById(R.id.buttonlogin);
		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logout();
			}
		});
		
		//Toast.makeText(getActivity(), "Welcome:" + getUsernamePref(), Toast.LENGTH_SHORT).show();
		return view;
	}

	
	private void logout() {
		// Clear the shared preferences
		SharedPreferences settings = getActivity().getSharedPreferences(
				Constants.PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor e = settings.edit();
		e.remove("accessTokenToken");
		e.remove("accessTokenSecret");
		e.remove("username");
		e.commit();
		getActivity().finish();
		Intent intent = new Intent();
		intent.setClass(getActivity(), AuthActivity.class);
		startActivity(intent);

	}

	private void getPhoneNumber() {
		TwitterApplication app = ((TwitterApplication)(getActivity().getApplication()));
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };

		Cursor people = getActivity().getContentResolver().query(uri,
				projection, null, null, null);
		
		int indexNumber = people
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

		people.moveToFirst();
		do {
			String number = people.getString(indexNumber);
			System.out.println("getPhoneNumber:: number--"+number);
			app.getmPhoneNumbersEntity().getPhoneNumbers().add(number);
			// Do work...
		} while (people.moveToNext());		
	}

}

