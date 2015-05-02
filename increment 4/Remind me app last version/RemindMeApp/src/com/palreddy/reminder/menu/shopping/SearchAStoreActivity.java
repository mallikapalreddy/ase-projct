package com.palreddy.reminder.menu.shopping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.JSONParser;
import com.palreddy.reminder.helper.Validation;

public class SearchAStoreActivity extends ActionBarActivity implements
		OnMarkerClickListener {

	private static String TAG = SearchAStoreActivity.class.toString();

	EditText searchStore;
	Button searchSubmit;

	private String goTo = null;

	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_astore);
		getSupportActionBar().hide();

		SavePreferences("isAddressSaved", "false");

		Intent i = getIntent();
		goTo = i.getStringExtra("goTo");

		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();

		Log.d(TAG, " setMyLocationEnabled ");
		googleMap.setMyLocationEnabled(true);

		LatLng myPosition = new LatLng(37, -95);// googleMap.getMyLocation().getLatitude(),
												// googleMap.getMyLocation().getLongitude());
		MarkerOptions marker = new MarkerOptions().position(myPosition);
		marker.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

		// adding marker
		googleMap.addMarker(marker);

		googleMap.setOnMarkerClickListener(this);

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(myPosition).zoom(5).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		googleMap.getUiSettings().setMyLocationButtonEnabled(true);

		searchStore = (EditText) findViewById(R.id.searchstore);
		searchSubmit = (Button) findViewById(R.id.searchsubmit);
		searchSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!Validation.hasText(searchStore))
					return;

				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				new DownloadStoreStates().execute(searchStore.getText()
						.toString().replaceAll(" ", "+"));
			}
		});
	}

	// private class DownloadStoreStates extends AsyncTask<String, Void,
	// ArrayList<StoreStates>>{
	private class DownloadStoreStates extends
			AsyncTask<String, Void, ArrayList<JSONObject>> {

		@Override
		protected ArrayList<JSONObject> doInBackground(String... params) {
			// AIzaSyBPwwCIYG94ejZB0i_mJrjU3r_MHNWLRhs
			String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyBPwwCIYG94ejZB0i_mJrjU3r_MHNWLRhs&query="
					+ params[0];
			System.out.println("url: " + url);
			ArrayList<JSONObject> stores = new ArrayList<JSONObject>();
			try {
				URL urlToRead = new URL(url);

				HttpURLConnection connection = (HttpURLConnection) (urlToRead)
						.openConnection();

				connection.setRequestMethod("GET");
				connection.connect();
				InputStream inputstream = connection.getInputStream();

				BufferedReader in = new BufferedReader(new InputStreamReader(
						inputstream));

				StringBuilder dataRead = new StringBuilder();
				String inputLine = null;
				while ((inputLine = in.readLine()) != null) {
					dataRead.append(inputLine);
				}

				in.close();
				// System.out.println("Data read: " + dataRead.toString());

				stores = JSONParser.getInstance().getStoresLocation(
						dataRead.toString());
			} catch (MalformedURLException e) {
				System.out.println("Error: " + e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return stores;
		}

		@Override
		protected void onPostExecute(ArrayList<JSONObject> stores) {

			System.out.println("Number of stores: " + stores.size());
			ArrayList<MarkerOptions> marker = new ArrayList<MarkerOptions>();
			googleMap.clear();
			LatLng position = new LatLng(0, 0);
			for (int i = 0; i < stores.size(); i++) {
				try {
					position = new LatLng(stores.get(i).getDouble("lat"),
							stores.get(i).getDouble("lng"));
					// marker.add(new MarkerOptions().position(position));
					Log.d(TAG,
							"Addind store: " + stores.get(i).getString("name"));
					marker.add(new MarkerOptions()
							.position(position)
							.title(stores.get(i).getString("name"))
							.snippet(
									stores.get(i)
											.getString("formatted_address")));
					System.out.println("cleared and Adding marker " + i);
					googleMap.addMarker(marker.get(i));
				} catch (NumberFormatException e) {

					e.printStackTrace();
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(position).zoom(12).build();

			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {

		if (goTo != null && goTo.equals("FindAProductActivity")) {
			Intent i = new Intent(getApplicationContext(),
					FindAProductActivity.class);
			startActivity(i);
		} else {
			Toast.makeText(getApplicationContext(),
					"" + arg0.getTitle() + " \n" + arg0.getSnippet(),
					Toast.LENGTH_LONG).show();
			SavePreferences("isAddressSaved", "true");
			SavePreferences("address",
					arg0.getSnippet() + " # " + arg0.getPosition().latitude
							+ " # " + arg0.getPosition().longitude);
			finish();
		}
		return false;
	}

	private void SavePreferences(String key, String value) {
		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sh_Pref.edit();
		edit.putString(key, value);
		edit.commit();
	}
}
