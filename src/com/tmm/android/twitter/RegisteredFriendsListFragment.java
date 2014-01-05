package com.tmm.android.twitter;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tmm.android.db.FriendListEntity;
import com.tmm.android.db.PhoneNumbersEntity;
import com.tmm.android.db.UserDBObject;
import com.tmm.android.twitter.appliaction.TwitterApplication;

public class RegisteredFriendsListFragment extends ListFragment {

	private ListView friendList;
	public boolean flagFromMapsFragment = false;
	public static String TAG = "RegisteredFriendsListFragment";
	public RegisteredFriendsListFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.setProperty("http.keepAlive", "false");
		System.out.println("In RegisteredFriendsListFragment");
		View view = inflater.inflate(R.layout.friend_list, container, false);
		friendList = (ListView) view.findViewById(android.R.id.list);
		flagFromMapsFragment = false;
		runTask(getActivity().getApplication());
		return view;
	}

	public void runTask(Application app) {
		((TwitterApplication) app)
				.getmUserDBObject().getFriendList().clear();
		new MakeDBQueryTask(getActivity(),app).execute();
	}

	/**
	 * This class uses REST API to work with My SQL
	 */
	private class MakeDBQueryTask extends AsyncTask<Void, String, String> {
		private Context mContext;
		private Application mApp;
		public MakeDBQueryTask(Context context, Application app) {
			mContext = context;
			mApp = app;
		}

		@Override
		protected String doInBackground(Void... param) {
			try {
				System.out.println("MakeDBQueryTask:doInBackground");
				DefaultHttpClient httpClient = new DefaultHttpClient();
				ObjectMapper mapper = new ObjectMapper();
				String restURL = "http://ec2-107-22-127-7.compute-1.amazonaws.com:8080/AndroidRestServer/rest/rdsobjectretrieve";
				HttpPost restRequest = new HttpPost(restURL);				
				PhoneNumbersEntity phoneNumbersEntity = ((TwitterApplication)mApp).getmPhoneNumbersEntity();

				String JsonObjectString = mapper
						.writeValueAsString(phoneNumbersEntity);
				System.out.println("MakeDBQueryTask:JsonObjectString--"
						+ JsonObjectString);
				StringEntity params = new StringEntity(JsonObjectString);
				restRequest.addHeader("Content-Type", "application/json");
				restRequest.setEntity(params);
				HttpResponse restResponse = httpClient.execute(restRequest);
				System.out.println("HTTP RESPONSE ENTITY IS ====="
						+ restResponse.getEntity());				

				InputStream source = restResponse.getEntity().getContent();
				Reader reader = new InputStreamReader(source);
				StringBuilder builder = new StringBuilder();
				char[] buffer = new char[reader.toString().length()];
				int read;
				while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
					builder.append(buffer, 0, read);
				}

				Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL,
						DateFormat.FULL).create();
				Type typeOfCollectionOfQuizDBObject = new TypeToken<FriendListEntity>() {
				}.getType();
				((TwitterApplication) mApp)
						.getmUserDBObject().setFriendList(
								((FriendListEntity) gson.fromJson(
										builder.toString(),
										typeOfCollectionOfQuizDBObject)).getFriendList());
				System.out.println("######:"+((TwitterApplication) mApp)
						.getmUserDBObject().getFriendList());

				return "success";
			} catch (Exception e) {
				e.printStackTrace();
				return "exception";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			if(!flagFromMapsFragment){
			if (result.equals("success")) {
				System.out.println(">>>>>>>>."
						+ ((TwitterApplication) getActivity().getApplication())
								.getmUserDBObject().getFriendList());
				friendList.setAdapter(new EfficientAdapter(getActivity()
						.getApplication(), getActivity()));
			} else {
				TextView emptyView = new TextView(mContext);
				emptyView.setText("No Items to display");
				emptyView.setTextColor(Color.BLACK);
				friendList.setEmptyView(emptyView);
			}
			}else{
				System.out.println("Its Done");
			}
		}
	}

	private static class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<UserDBObject> friendsListItems;

		public EfficientAdapter(Application application, Context context) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			friendsListItems = ((TwitterApplication) application)
					.getmUserDBObject().getFriendList();
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return friendsListItems.size();
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position) {
			return position;
		}

		/**
		 * Use the array index as a unique id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder = null;
			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.friend_list_item, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.username = (TextView) convertView
						.findViewById(android.R.id.text1);
				holder.phonenumber = (TextView) convertView
						.findViewById(android.R.id.text2);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			holder.username.setText(friendsListItems.get(position)
					.getUsername());
			holder.phonenumber.setText(friendsListItems.get(position)
					.getPhoneNumber());

			return convertView;
		}

		static class ViewHolder {
			TextView username;
			TextView phonenumber;
		}

	}

}
