package com.hackathon.myyoutube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hackathon.myyoutube.helper.ServiceHandler;

public class StatisticsActivity extends Activity {

	private ListView shoppingItemList;

	static Context ctx;

	private int serverOK = 0;
	private int deleteOK = 0;
	private String idItemToDelete = "";

	private ProgressDialog pDialog;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "";
	private static String getShoppingListURL = "/getuserlist";

	private JSONArray shoppinglist = null;
	private ArrayList<HashMap<String, String>> shoppingList;
	private ListAdapter adapterListshoppinglist;

	private HashMap<String, String> mapSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		serverURL = loadPrefs(TAG_SERVER_URL);

		shoppingItemList = (ListView) findViewById(R.id.listStatistic);
	}

	private class GetShoppingItems extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(StatisticsActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("idU", "" + 0));
			String jsonStr = sh.makeServiceCall(serverURL + getShoppingListURL,
					ServiceHandler.POST);

			if (jsonStr != null) {
				serverOK = 1;
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// Getting JSON Array node
					shoppinglist = jsonObj.getJSONArray("listSearch");

					// looping through All Shoppings
					for (int i = 0; i < shoppinglist.length(); i++) {
						JSONObject c = shoppinglist.getJSONObject(i);

						String email = c.getString("email");
						String freqlogon = c.getString("freqlogon");

						// tmp hashmap for single contact
						HashMap<String, String> shop = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						shop.put("email", email);
						shop.put("freqlogon", freqlogon);

						shoppingList.add(shop);
					}

				} catch (JSONException e) {
					serverOK = 0;
				}
			} else {
				serverOK = 0;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.cancel();
			/**
			 * Updating parsed JSON data into ListView
			 * */

			if (serverOK == 1) {
				Log.d("srverOK : ", ">  OK ");

				adapterListshoppinglist = new SimpleAdapter(
						StatisticsActivity.this, shoppingList,
						R.layout.show_listview_statistic, new String[] {
								"email", "freqlogon" }, new int[] {
								R.id.textSearchSub, R.id.textSearchNumber });

				shoppingItemList.setAdapter(adapterListshoppinglist);
			} else {
				new AlertDialog.Builder(StatisticsActivity.this)
						.setTitle("Message")
						.setMessage("Connection problem with the server.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK", null).show();
			}
		}

	}

	public String loadPrefs(String KEY) {

		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}
}
