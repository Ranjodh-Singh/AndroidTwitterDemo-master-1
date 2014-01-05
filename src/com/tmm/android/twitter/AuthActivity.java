package com.tmm.android.twitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmm.android.db.UserDBObject;
import com.tmm.android.db.UserData;
import com.tmm.android.twitter.appliaction.TwitterApplication;
import com.tmm.android.twitter.util.Constants;

public class AuthActivity extends Activity {

	private ProgressDialog pDialog;
	private DefaultHttpClient httpClient;
	private ObjectMapper mapper;
	private Twitter twitter;
	private OAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;

	private String CONSUMER_KEY = Constants.CONSUMER_KEY;
	private String CONSUMER_SECRET = Constants.CONSUMER_SECRET;
	private String CALLBACK_URL = "callback://tweeter";

	private Button buttonLogin;
	private Button login;
	private EditText phoneNumberField;
	private EditText userNameField;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		System.setProperty("http.keepAlive", "false");
		super.onCreate(savedInstanceState);
		System.out.println("On Create");
		setContentView(R.layout.main_oauth);
		mContext = this;
		// check for saved log in details..
		checkForSavedLogin();

		// set consumer and provider on teh Application service
		getConsumerProvider();

		//Phone number field
		phoneNumberField = (EditText)findViewById(R.id.userPhoneFieldText);
		userNameField = (EditText)findViewById(R.id.userNameText);
		
		// Define login button and listener
		buttonLogin = (Button) findViewById(R.id.ButtonLogin);
		
		login = (Button) findViewById(R.id.login);
		
	}

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		buttonLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String phoneNumberText = phoneNumberField.getText().toString();
				((TwitterApplication)getApplication()).setPhoneNumber(phoneNumberText);
				if(!(((TwitterApplication)getApplication()).getPhoneNumber()).isEmpty()){
					new DownloadFilesTask().execute(getApplication());
					Toast.makeText(mContext, "Your phone number:"+((TwitterApplication)getApplication()).getPhoneNumber(), Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(mContext, "Please enter your phone number", Toast.LENGTH_SHORT).show();
				}
			}	
		});
		
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loginUsingTwitter();
			}
		});
	}
	
	
	private void loginUsingTwitter() {
		getFromProvider(userNameField.getText().toString());
	}

	
	
	

	private void checkForSavedLogin() {
		// Get Access Token and persist it
		AccessToken a = getAccessToken();
		if (a == null)
			return; // if there are no credentials stored then return to usual
					// activity

		// initialize Twitter4J
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		((TwitterApplication) getApplication()).setTwitter(twitter);

		finish();
		startFirstActivity();

	}

	/**
	 * Kick off the activity to display
	 */
	private void startFirstActivity() {
		//Finishing Current Activity
		this.finish();
		System.out.println("STARTING FIRST ACTIVITY!");
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
		// startActivityForResult(i, Constants.ACTIVITY_LATEST_TWEETS);
	}

	/**
	 * This method checks the shared prefs to see if we have persisted a user
	 * token/secret if it has then it logs on using them, otherwise return null
	 * 
	 * @return AccessToken from persisted prefs
	 */
	private AccessToken getAccessToken() {
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
				MODE_PRIVATE);
		String token = settings.getString("accessTokenToken", "");
		String tokenSecret = settings.getString("accessTokenSecret", "");
		if (token != null && tokenSecret != null && !"".equals(tokenSecret)
				&& !"".equals(token)) {
			return new AccessToken(token, tokenSecret);
		}
		return null;
	}

	/**
	 * @param context
	 *            the context
	 * @return the consumer (initialize on the first call)
	 */
	public CommonsHttpOAuthConsumer getConsumer(Context context) {
		if (consumer == null) {
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
					CONSUMER_SECRET);
			// consumer.setMessageSigner(new HmacSha1MessageSigner());
		}
		//((TwitterApplication) getApplication()).setConsumer(consumer);
		return consumer;
	}

	/**
	 * @return the provider (initialize on the first call)
	 */
	public OAuthProvider getProvider() {
		if (provider == null) {
			provider = new DefaultOAuthProvider(
					"https://twitter.com/oauth/request_token",
					"https://twitter.com/oauth/access_token",
					"https://twitter.com/oauth/authorize");
		}
		//((TwitterApplication) getApplication()).setProvider(provider);
		
		return provider;
	}

	/**
	 * Open the browser and asks the user to authorize the app. Afterwards, we
	 * redirect the user back here!
	 */
	private void askOAuth() {
		try {
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
					CONSUMER_SECRET);
			provider = new DefaultOAuthProvider(
					"https://twitter.com/oauth/request_token",
					"https://twitter.com/oauth/access_token",
					"https://twitter.com/oauth/authorize");
			String authUrl = provider.retrieveRequestToken(consumer,
					CALLBACK_URL);
			Toast.makeText(this, "Please authorize this app!",
					Toast.LENGTH_LONG).show();
			setConsumerProvider();
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		} catch (Exception e) {
			// Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private class DownloadFilesTask extends AsyncTask<Context, String, String> {
		private Context context;

		// Do the long-running work in here
		protected String doInBackground(Context... params) {
			this.context = params[0];
			try {
				return getProvider().retrieveRequestToken(
						getConsumer(params[0]), CALLBACK_URL);
			} catch (Exception e) {
				// MyLog.e(TAG, e,
				// "Error while trying to launch Twitter Authentication!");
				e.printStackTrace();
				return null;
			}
		}

		// This is called each time you call publishProgress()
		protected void onProgressUpdate(Integer... progress) {
			System.out.println("Its working!!!");
			// setProgressPercent(progress[0]);
		}

		// This is called when doInBackground() is finished
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				System.out.println("Twitter OAuth URL: " + result);
				// launching the browser
				setConsumerProvider();
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(result));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				this.context.startActivity(intent);
			}
		}

	}

	/**
	 * Retrieve Access Token task.
	 */
	private class RetrieveAccessToken extends
			AsyncTask<Context, String, Boolean> {

		/**
		 * The context.
		 */
		private Context context;
		/**
		 * The Twitter OAuth verifier.
		 */
		private String oauth_verifier;
		
		private Context mActivityContext;
		/**
		 * Default constructor.
		 * 
		 * @param oauth_verifier
		 *            Twitter OAuth verifier
		 */
		public RetrieveAccessToken(String oauth_verifier) {
			this.oauth_verifier = oauth_verifier;
		}

		@Override
		protected Boolean doInBackground(Context... params) {
			this.context = params[0];
			this.mActivityContext = params[1];
			try {
				// retrieve the access token from the consumer and the OAuth
				// verifier returner by the Twitter Callback URL
				getProvider().retrieveAccessToken(getConsumer(this.context),
						this.oauth_verifier);
				return true;
			} catch (OAuthException oae) {
				// MyLog.w(TAG, oae, "Twitter OAuth error!");
				oae.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				AccessToken a = new AccessToken(consumer.getToken(),
						consumer.getTokenSecret());
				storeAccessToken(a);				
				twitter = new TwitterFactory().getInstance();				
				twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
				twitter.setOAuthAccessToken(a);
				((TwitterApplication) getApplication()).setTwitter(twitter);
								
				//startFirstActivity();
				
			} 
		}

	}

	/**
	 * As soon as the user successfully authorized the app, we are notified
	 * here. Now we need to get the verifier from the callback URL, retrieve
	 * token and token_secret and feed them to twitter4j (as well as consumer
	 * key and secret).
	 */
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("RESUMING!!");
		if (this.getIntent() != null && this.getIntent().getData() != null) {
			Uri uri = this.getIntent().getData();
			if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
				String verifier = uri
						.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
				try {
					// this will populate token and token_secret in consumer
					// provider.retrieveAccessToken(consumer, verifier);
					new RetrieveAccessToken(verifier).execute(getApplication(), this);
					// Get Access Token and persist it
					// AccessToken a = new AccessToken(consumer.getToken(),
					// consumer.getTokenSecret());
					// storeAccessToken(a);

					

				} catch (Exception e) {
					// Log.e(APP, e.getMessage());
					e.printStackTrace();
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
							.show();
				}
			}
		}
	}

	/**
	 * This class uses REST API to work with My SQL
	 */

	private class MakeDBQueryTask extends AsyncTask<Void, String, String> {
		private Context mContext;
		public MakeDBQueryTask(Context context){
			mContext = context;
		}
		@Override
		protected String doInBackground(Void... param) {
			try {
				System.out.println("MakeDBQueryTask:doInBackground");
				DefaultHttpClient httpClient = new DefaultHttpClient();
				ObjectMapper mapper = new ObjectMapper();
				String restURL = "http://ec2-107-22-127-7.compute-1.amazonaws.com:8080/AndroidRestServer/rest/rdsobjectupdate";
				
				HttpPost restRequest = new HttpPost(restURL);
				Twitter t = ((TwitterApplication) getApplication())
						.getTwitter();
				UserDBObject userDbObject = ((TwitterApplication)getApplication()).getmUserDBObject();
				userDbObject.setOperationType("INSERT");
				userDbObject.setUserName(t.getScreenName());
				storeUsername(userDbObject.getUserName());
				userDbObject.setPhoneNumber(((TwitterApplication)getApplication()).getPhoneNumber());
				userDbObject.setOauthProvider(provider
						.getAuthorizationWebsiteUrl());
				//((TwitterApplication) getApplication()).setmUserDBObject(userDbObject);
				String JsonObjectString = mapper
						.writeValueAsString(userDbObject);
				System.out.println("MakeDBQueryTask:JsonObjectString--"+JsonObjectString);
				StringEntity params = new StringEntity(JsonObjectString);
				restRequest.addHeader("Content-Type", "application/json");
				restRequest.setEntity(params);
				HttpResponse restResponse = httpClient.execute(restRequest);
				System.out.println("HTTP RESPONSE ENTITY IS ====="
						+ restResponse.getEntity());
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						restResponse.getEntity().getContent()));
				UserDBObject responseDTO = mapper.readValue(rd, UserDBObject.class);
				System.out.println("Obtained :" + responseDTO.toString());
				if(responseDTO.isError()){
					if(responseDTO.getErrorDesc().equals("Duplicate Entry"))
						return "User Already Registered";
					else
						return "Something Crashed";
				}
				else{
					return responseDTO.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if(result!=null){
				System.out.println("onPostExecute:result:"+result);
				if(result.equals("User Already Registered") || result.equals("Something Crashed")){
					AlertDialog.Builder db = new AlertDialog.Builder(mContext);
					db.setTitle(result);
					db.setPositiveButton("OK", new 
					    DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) {
					            }
					        });

					AlertDialog dialog = db.show();
				}else{
					saveInProvider();
					startFirstActivity();
				}
			}
			else{
				System.out.println("Operation failed");
			}
		}
	}

	/**
	 * This method persists the Access Token information so that a user is not
	 * required to re-login every time the app is used
	 * 
	 * @param a
	 *            - the access token
	 */
	private void storeAccessToken(AccessToken a) {
		System.out.println("storeAccessToken");
		
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("accessTokenToken", a.getToken());
		editor.putString("accessTokenSecret", a.getTokenSecret());
		editor.commit();
		//Storing User details in Database
		new MakeDBQueryTask(this).execute();
	}
	
	public void saveInProvider() {
		ContentValues values = new ContentValues();
		try{
			values.put(UserData.C_USERNAME, ((TwitterApplication)getApplication()).getmUserDBObject().getUserName());
			values.put(UserData.C_OAUTH, ((TwitterApplication)getApplication()).getmUserDBObject().getOauthprovider());
			values.put(UserData.C_LAT, ((TwitterApplication)getApplication()).getmUserDBObject().getLocation_lat());
			values.put(UserData.C_LONG, ((TwitterApplication)getApplication()).getmUserDBObject().getLocation_long());
			values.put(UserData.C_PHONENUMBER, ((TwitterApplication)getApplication()).getmUserDBObject().getPhoneNumber());
			((TwitterApplication)getApplication()).getUserData().insertOrIgnore(values);
		}catch(Exception e){
			e.printStackTrace();
			((TwitterApplication)getApplication()).getUserData().close();
		}
		finally{
			
		}
	}
	
	private void getFromProvider(String username){
		UserDBObject userDbObject = ((TwitterApplication)getApplication()).getUserData().getUsername(username);
		if(userDbObject == null){
			AlertDialog.Builder db = new AlertDialog.Builder(mContext);
			db.setTitle("!!!Error!!! \n User Not registered ");
			db.setPositiveButton("OK", new 
			    DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			            }
			        });

			AlertDialog dialog = db.show();
		}else{
			((TwitterApplication)getApplication()).setmUserDBObject(userDbObject);
			startFirstActivity();
		}
		
	}


	private void storeUsername(String username){
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("username", username);
		editor.commit();
	}

	/**
	 * Get the consumer and provider from the application service (in the case
	 * that the activity is restarted so the objects are not lost
	 */
	private void getConsumerProvider() {
		OAuthProvider p = ((TwitterApplication) getApplication()).getProvider();
		if (p != null) {
			provider = p;
		}
		CommonsHttpOAuthConsumer c = ((TwitterApplication) getApplication())
				.getConsumer();
		if (c != null) {
			consumer = c;
		}
	}

	/**
	 * Set the consumer and provider from the application service (in the case
	 * that the activity is restarted so the objects are not lost)
	 */
	private void setConsumerProvider() {
		if (provider != null) {
			((TwitterApplication) getApplication()).setProvider(provider);
		}
		if (consumer != null) {
			((TwitterApplication) getApplication()).setConsumer(consumer);
		}
	}

}
