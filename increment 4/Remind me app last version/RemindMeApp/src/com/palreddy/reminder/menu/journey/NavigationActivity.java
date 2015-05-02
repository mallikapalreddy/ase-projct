package com.palreddy.reminder.menu.journey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.palreddy.reminder.R;

public class NavigationActivity extends FragmentActivity {

	private GoogleMap googleMap;
	private ArrayList<LatLng> arrayPoints = null;
	PolylineOptions polylineOptions;

	private JSONArray haltListJSONArray = null;
	private ArrayList<HashMap<String, String>> haltList;
	private JSONObject journey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);

		haltList = new ArrayList<HashMap<String, String>>();

		Intent intent = getIntent();
		String jsonStrHalts = intent.getStringExtra("jsonStrHalts");
		String jsonStrJourney = intent.getStringExtra("jsonStrJourney");

		if (jsonStrHalts != null && jsonStrJourney != null) {
			try {
				JSONObject jsonObj = new JSONObject(jsonStrHalts);

				// Getting JSON Array node
				haltListJSONArray = jsonObj.getJSONArray("listHalt");

				// looping through All Shoppings
				for (int i = 0; i < haltListJSONArray.length(); i++) {
					JSONObject c = haltListJSONArray.getJSONObject(i);

					String idH = c.getString("idH");
					String expectedArrivalTime = c
							.getString("expectedArrivalTime");
					String expectedDepartureTime = c
							.getString("expectedDepartureTime");
					String recorderVoiceH = c.getString("recorderVoiceH");
					String station = c.getString("station");

					// tmp hashmap for single contact
					HashMap<String, String> halt = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					halt.put("idH", idH);
					halt.put("expectedArrivalTime", expectedArrivalTime);
					halt.put("expectedDepartureTime", expectedDepartureTime);
					halt.put("recorderVoiceH", recorderVoiceH);
					halt.put("station", station);

					haltList.add(halt);

				}

				jsonObj = new JSONObject(jsonStrJourney);
				haltListJSONArray = jsonObj.getJSONArray("oneJourney");
				journey = haltListJSONArray.getJSONObject(0);

			} catch (JSONException e) {
			}
		}

		arrayPoints = new ArrayList<LatLng>();
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		MarkerOptions marker = new MarkerOptions();
		polylineOptions = new PolylineOptions();
		polylineOptions.color(Color.RED);
		polylineOptions.width(5);

		String[] address = null;
		LatLng point;
		String snippet = null;

		/*
		 * Show Journey Source
		 */
		try {
			address = (journey.getString("source")).split(" # ");
			snippet = "Plan Name: " + journey.getString("planName")
					+ "\n Destination: " + journey.getString("destination");

		} catch (JSONException e) {
		}
		point = new LatLng(Double.parseDouble(address[1]),
				Double.parseDouble(address[2]));
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13));
		marker.position(point);
		marker.title("Journey Source");

		marker.snippet(snippet);
		googleMap.addMarker(marker);
		arrayPoints.add(point);

		/*
		 * Show Halts
		 */
		String[] number = { "First", "Second", "Third", "Fourth", "Fifth",
				"Sixth" };
		int i = 0;
		while (!haltList.isEmpty()) {
			// get the next halt
			HashMap<String, String> nextHalt;
			nextHalt = haltList.get(0);
			for (int j = 1; j < haltList.size(); j++) {
				Calendar firstHour = getCalendarFromStringTime((haltList.get(j))
						.get("expectedArrivalTime"));
				Calendar secondtHour = getCalendarFromStringTime(nextHalt
						.get("expectedArrivalTime"));
				if (firstHour.before(secondtHour))
					nextHalt = haltList.get(j);
			}

			address = (nextHalt.get("station")).split(" # ");
			snippet = "Arrival Time: " + nextHalt.get("expectedArrivalTime")
					+ "\n Departure Time: "
					+ nextHalt.get("expectedDepartureTime");

			point = new LatLng(Double.parseDouble(address[1]),
					Double.parseDouble(address[2]));
			marker.position(point);
			marker.title(number[i] + " Halt");
			i++;

			marker.snippet(snippet);
			googleMap.addMarker(marker);
			arrayPoints.add(point);

			haltList.remove(nextHalt);
		}

		/*
		 * Show Journey Destination
		 */
		try {
			address = (journey.getString("destination")).split(" # ");
			snippet = "Plan Name: " + journey.getString("planName")
					+ "\n Source: " + journey.getString("source");

		} catch (JSONException e) {
		}
		point = new LatLng(Double.parseDouble(address[1]),
				Double.parseDouble(address[2]));
		marker.position(point);
		marker.title("Journey Destination");

		marker.snippet(snippet);
		googleMap.addMarker(marker);
		arrayPoints.add(point);

		polylineOptions.addAll(arrayPoints);
		googleMap.addPolyline(polylineOptions);
	}

	private Calendar getCalendarFromStringTime(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date date1 = null;
		try {
			date1 = sdf.parse(str);
		} catch (ParseException e) {
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		return cal1;
	}
}
