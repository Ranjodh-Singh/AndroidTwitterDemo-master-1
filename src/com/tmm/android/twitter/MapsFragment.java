package com.tmm.android.twitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import twitter4j.Twitter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tmm.android.db.UserDBObject;
import com.tmm.android.twitter.WorkaroundMapFragment.OnMapReadyListener;
import com.tmm.android.twitter.appliaction.TwitterApplication;

public class MapsFragment extends Fragment implements OnMapReadyListener,
		LocationListener, OnMarkerDragListener {
	public static int MAPSFRAGMENTPOS = 2;
	public static String TAG = "MapsFragment";
	private WorkaroundMapFragment mMapFragment;
	private LocationManager lm;
	private GoogleMap mMap;
	private String longitude;
	private String latitude;
	private GPSTracker gps;

	public MapsFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		System.out.println("In Maps Fragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View root = inflater.inflate(R.layout.map_fragment, container, false);
		getActivity();
		lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		container.requestTransparentRegion(container);
		android.support.v4.app.FragmentManager myFragmentManager = getChildFragmentManager();
		mMapFragment = WorkaroundMapFragment.newInstance();
		
		android.support.v4.app.FragmentTransaction fragmentTransaction = myFragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.map, mMapFragment).commit();
		myFragmentManager.executePendingTransactions();
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onMapReady(GoogleMap map) {
		// TODO Auto-generated method stub
		mMap = map;
		//if (map != null)
		mMap.setMyLocationEnabled(true);
		mMap.setOnMarkerDragListener(this);
		
		gps = new GPSTracker(getActivity());
		if (gps.canGetLocation()) {

			latitude = String.valueOf(gps.getLatitude());
			longitude = String.valueOf(gps.getLongitude());
			// \n is for new line
			Toast.makeText(
					getActivity(),
					"Your Location is - \nLat: " + latitude + "\nLong: "
							+ longitude, Toast.LENGTH_LONG).show();
			mMap.addMarker(new MarkerOptions()
					.position(new LatLng(gps.getLatitude(), gps.getLongitude()))
					.title("my position")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(gps.getLatitude(), gps.getLongitude()), 15.0f));
			new MakeDBQueryTask().execute();
			//mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(
				//	new LatLng(gps.getLatitude(), gps.getLongitude()), 15.0f)));
			//createGeofence(gps.getLatitude(), gps.getLongitude(),100, "CIRCLE", "GEOFENCE");
			//showFriendsLocation();
			setUpMap();
			
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}		

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 10, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/**
	 * This class uses REST API to work with My SQL
	 */

	private class MakeDBQueryTask extends AsyncTask<Void, String, String> {

		@Override
		protected String doInBackground(Void... param) {
			try {
				System.out.println("MakeDBQueryTask:doInBackground");
				DefaultHttpClient httpClient = new DefaultHttpClient();
				ObjectMapper mapper = new ObjectMapper();
				String restURL = "http://ec2-107-22-127-7.compute-1.amazonaws.com:8080/AndroidRestServer/rest/rdsobjectupdate";
				Twitter t = ((TwitterApplication) getActivity()
						.getApplication())
						.getTwitter();
				HttpPost restRequest = new HttpPost(restURL);
				UserDBObject userDbObject = ((TwitterApplication) getActivity()
						.getApplication()).getmUserDBObject();
				userDbObject.setOperationType("UPDATE");
				userDbObject.setUserName(t.getScreenName());
				userDbObject.setLocation_lat(latitude);
				userDbObject.setLocation_long(longitude);
				String JsonObjectString = mapper
						.writeValueAsString(userDbObject);
				System.out.println("MakeDBQueryTask:JsonObjectString--"
						+ JsonObjectString);
				StringEntity params = new StringEntity(JsonObjectString);
				restRequest.addHeader("Content-Type", "application/json");
				restRequest.setEntity(params);
				HttpResponse restResponse = httpClient.execute(restRequest);
				System.out.println("HTTP RESPONSE ENTITY IS ====="
						+ restResponse.getEntity());
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						restResponse.getEntity().getContent()));
				UserDBObject responseDTO = mapper.readValue(rd,
						UserDBObject.class);
				System.out.println("Obtained :" + responseDTO.toString());
				if(responseDTO.isError()){
					if(responseDTO.getErrorDesc().equals("Error"))
						return "Error";
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
			System.out.println("onPostExecute:result:" + result);
		}
	}

	
	private void setUpMap() {
		
		if(((TwitterApplication)(getActivity().getApplication())).getmUserDBObject().getFriendList().size()==0){
			AlertDialog.Builder db = new AlertDialog.Builder(getActivity());
			db.setTitle("Go to Friends List in the Navigation Panel to view friends");
			db.setPositiveButton("OK", new 
			    DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			            }
			        });

			AlertDialog dialog = db.show();
			//Toast.makeText(getActivity(), "Go to Friends List in the Navigation Panel to view firends", Toast.LENGTH_SHORT).show();
			return;
		}
		
	    // Hide the zoom controls as the button panel will cover it.
	    mMap.getUiSettings().setZoomControlsEnabled(true);	    
	    
	    final List<UserDBObject> list = ((TwitterApplication)(getActivity().getApplication())).getmUserDBObject().getFriendList();
	    // Pan to see all markers in view.
	    // Cannot zoom to bounds until the map has a size.
	    final View mapView = mMapFragment.getView();
	    if (mapView.getViewTreeObserver().isAlive()) {
	        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	            
	            // We check which build version we are using.
	            @SuppressWarnings("deprecation")
				@Override
	            public void onGlobalLayout() {
	            	LatLngBounds.Builder bld = new LatLngBounds.Builder();
	            	for (int i = 0; i < list.size(); i++) {         
	            		double lati = Double.parseDouble(list.get(i).getLocation_lat());
	            		double longLat = Double.parseDouble(list.get(i).getLocation_long());
	            		LatLng ll = new LatLng(lati,longLat);
	            		bld.include(ll);            
	            		mMap.addMarker(new MarkerOptions()
						.position(new LatLng(lati, longLat))
						.title(list.get(i).getUsername()));
	            	}
	            	LatLngBounds bounds = bld.build();          
	            	mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 40));
	            	mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

	            }
	        });
	    }
	} 
	
	
	private void createGeofence(double latitude, double longitude, int radius,
			String geofenceType, String title) {

		mMap.addMarker(new MarkerOptions()
				.draggable(true)
				.position(new LatLng(latitude, longitude))
				.title(title)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon)));

		mMap.addCircle(new CircleOptions()
				.center(new LatLng(latitude, longitude)).radius(radius));
	}
			 @Override
			 public void onMarkerDrag(Marker marker) {
			 }
			 @Override
			 public void onMarkerDragEnd(Marker marker) {
			  LatLng dragPosition = marker.getPosition();
			  double dragLat = dragPosition.latitude;
			  double dragLong = dragPosition.longitude;
			  mMap.clear();
			  createGeofence(dragLat, dragLong, 100, "CIRCLE", "GEOFENCE");
			  Toast.makeText(
			    getActivity(),
			    "onMarkerDragEnd dragLat :" + dragLat + " dragLong :"
			      + dragLong, Toast.LENGTH_SHORT).show();
			  Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLong);

			 }
			 @Override
			 public void onMarkerDragStart(Marker marker) {
			 }

			
}
