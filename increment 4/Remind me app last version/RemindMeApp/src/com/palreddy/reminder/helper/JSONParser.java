package com.palreddy.reminder.helper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {

	private static JSONParser parserObj;

	private JSONParser() {

	}

	public static JSONParser getInstance() {
		if (parserObj == null)
			parserObj = new JSONParser();

		return parserObj;
	}

	public ArrayList<JSONObject> getStoresLocation(String placesJson) {
		ArrayList<JSONObject> storeLocations = new ArrayList<JSONObject>();
		try {
			JSONObject json = new JSONObject(placesJson);
			String status = json.getString("status");
			System.out.println("JSON status: " + json.getString("status"));

			if (status.equals("OK")) {

				JSONArray results = json.getJSONArray("results");
				System.out.println("Number of items in results array: "
						+ results.length());
				for (int i = 0; i < results.length(); i++) {
					storeLocations.add(results.getJSONObject(i)
							.getJSONObject("geometry")
							.getJSONObject("location"));
					storeLocations.get(i).put("name",
							results.getJSONObject(i).getString("name"));
					storeLocations.get(i).put(
							"formatted_address",
							results.getJSONObject(i).getString(
									"formatted_address"));
				}
				System.out.println("Number of items in store Locations array: "
						+ storeLocations.size());
				System.out.println("Location: " + storeLocations.toString());
			} else if (status.equals("ZERO_RESULTS")) {
				System.out
						.println("Sorry no places found. Try to change the types of places");
			} else if (status.equals("UNKNOWN_ERROR")) {
				// "Sorry unknown error occured.",
			} else if (status.equals("OVER_QUERY_LIMIT")) {
				// "Sorry query limit to google places is reached",
			} else if (status.equals("REQUEST_DENIED")) {
				// "Sorry error occured. Request is denied",
			} else if (status.equals("INVALID_REQUEST")) {
				// "Sorry error occured. Invalid Request",
			} else {
				// "Sorry error occured.",
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return storeLocations;
	}
}
