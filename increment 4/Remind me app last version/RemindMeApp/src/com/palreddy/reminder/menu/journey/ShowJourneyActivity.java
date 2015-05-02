package com.palreddy.reminder.menu.journey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.ServiceHandler;

public class ShowJourneyActivity extends ActionBarActivity {

	private TextView planName;
	private TextView Source;
	private TextView Destination;
	private TextView Date;
	private TextView ArrivalTime;
	private TextView haltNumber;
	private ImageView navigate;

	private ListView haltItemList;

	static Context ctx;

	private int serverOK = 0;
	private int deleteOK = 0;
	private String idItemToDelete = "";

	private String idJ;

	private String jsonStrHalts;

	private String jsonStrJourney;

	private ProgressDialog pDialog;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "";
	private static String getHaltListURL = "/halt/gethaltlist";
	private static String deleteHaltURL = "/halt/deletehalt";
	private static String getOneJourneyURL = "/journey/getonejourney";

	private JSONArray haltListJSONArray = null;
	private ArrayList<HashMap<String, String>> haltList;
	private ListAdapter adapterHaltlist;

	private HashMap<String, String> mapSelected;

	private JSONObject journey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_journey);
		getSupportActionBar().hide();

		serverURL = loadPrefs(TAG_SERVER_URL);
		ctx = getApplicationContext();

		Intent intent = getIntent();
		idJ = intent.getStringExtra("idJ");

		haltList = new ArrayList<HashMap<String, String>>();

		// add new Halt button
		((Button) findViewById(R.id.btn_add_halt))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (serverOK == 0)
							return;
						Intent intentAddHalt = new Intent(ctx,
								AddHaltActivity.class);

						try {
							intentAddHalt.putExtra("from", "add");
							intentAddHalt.putExtra("idJ",
									journey.getString("idJ"));
							intentAddHalt.putExtra("journeyTime",
									journey.getString("expectedArrivalTime"));
						} catch (JSONException e) {
						}
						startActivity(intentAddHalt);
					}
				});

		// update journey button
		((Button) findViewById(R.id.btn_update_joutney))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (serverOK == 0)
							return;
						Intent intentUpdate = new Intent(ctx,
								AddJourneyActivity.class);

						intentUpdate.putExtra("from", "update");
						try {
							intentUpdate.putExtra("idJ",
									journey.getString("idJ"));
							intentUpdate.putExtra("date",
									journey.getString("date"));
							intentUpdate.putExtra("destination",
									journey.getString("destination"));
							intentUpdate.putExtra("expectedArrivalTime",
									journey.getString("expectedArrivalTime"));
							intentUpdate.putExtra("planname",
									journey.getString("planName"));
							intentUpdate.putExtra("source",
									journey.getString("source"));
						} catch (JSONException e) {
						}
						startActivity(intentUpdate);

					}
				});

		// Text View to show journey information
		planName = (TextView) findViewById(R.id.PlanName);
		Source = (TextView) findViewById(R.id.Source);
		Destination = (TextView) findViewById(R.id.Destination);
		Date = (TextView) findViewById(R.id.Date);
		ArrivalTime = (TextView) findViewById(R.id.ArrivalTime);
		haltNumber = (TextView) findViewById(R.id.haltNumber);

		// List View
		haltItemList = (ListView) findViewById(R.id.haltItemList);
		haltItemList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				mapSelected = (HashMap<String, String>) haltItemList
						.getItemAtPosition(position);

				AlertDialog.Builder alertadd = new AlertDialog.Builder(
						ShowJourneyActivity.this);
				LayoutInflater factory = LayoutInflater
						.from(ShowJourneyActivity.this);
				final View view = factory.inflate(
						R.layout.showing_read_update_delete_dialog, null);
				alertadd.setView(view);
				// Setting Positive "Delete" Button
				alertadd.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								new AlertDialog.Builder(
										ShowJourneyActivity.this)
										.setTitle("Confirmation ...")
										.setMessage(
												"Are you sure you want to delete this item ?")
										.setIcon(android.R.drawable.ic_delete)
										.setPositiveButton(
												"YES",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														idItemToDelete = mapSelected
																.get("idH");
														new DeleteHalt()
																.execute();
													}
												})
										.setNegativeButton(
												"NO",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog.cancel();
													}
												}).show();

							}
						});
				// Setting Neutral Update Button
				alertadd.setNeutralButton("Update",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intentupdateHalt = new Intent(ctx,
										AddHaltActivity.class);

								intentupdateHalt.putExtra("from", "update");
								intentupdateHalt.putExtra("idJ",
										mapSelected.get("idJ"));
								intentupdateHalt.putExtra("idH",
										mapSelected.get("idH"));
								intentupdateHalt.putExtra(
										"expectedArrivalTime",
										mapSelected.get("expectedArrivalTime"));
								intentupdateHalt.putExtra(
										"expectedDepartureTime", mapSelected
												.get("expectedDepartureTime"));
								intentupdateHalt.putExtra("station",
										mapSelected.get("station"));
								try {
									intentupdateHalt.putExtra(
											"journeyTime",
											journey.getString("expectedArrivalTime"));
								} catch (JSONException e) {
								}

								startActivity(intentupdateHalt);

							}
						});
				// Setting Negative "Read" Button
				alertadd.setNegativeButton("Show",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String[] station = (mapSelected.get("station"))
										.split(" # ");
								new AlertDialog.Builder(
										ShowJourneyActivity.this)
										.setTitle("Message")
										.setMessage(
												"Halt Information:  \n\n "
														+ "* Station: \n     "
														+ station[0]
														+ "\n "
														+ "* Arrival Time: \n     "
														+ mapSelected
																.get("expectedArrivalTime")
														+ "\n "
														+ "* Departure Time: \n     "
														+ mapSelected
																.get("expectedDepartureTime"))
										.setIcon(
												android.R.drawable.ic_dialog_info)
										.setNegativeButton("Ok", null).show();

							}
						});

				alertadd.show();
			}
		});

		navigate = (ImageView) findViewById(R.id.navigate);
		navigate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent navigateIntent = new Intent(getApplicationContext(),
						NavigationActivity.class);
				navigateIntent.putExtra("jsonStrHalts", jsonStrHalts);
				navigateIntent.putExtra("jsonStrJourney", jsonStrJourney);
				startActivity(navigateIntent);
			}
		});

	}

	public String loadPrefs(String KEY) {

		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}

	@Override
	protected void onResume() {
		haltList.clear();
		new GetHaltItemsAndJourneyInfo().execute();
		super.onResume();
	}

	private class GetHaltItemsAndJourneyInfo extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ShowJourneyActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("idJ", idJ));
			jsonStrHalts = sh.makeServiceCall(serverURL + getHaltListURL,
					ServiceHandler.POST, urlParameters);

			jsonStrJourney = sh.makeServiceCall(serverURL + getOneJourneyURL,
					ServiceHandler.POST, urlParameters);

			if (jsonStrHalts != null && jsonStrJourney != null) {
				serverOK = 1;
				try {
					JSONObject jsonObj = new JSONObject(jsonStrHalts);

					// Getting JSON Array node
					haltListJSONArray = jsonObj.getJSONArray("listHalt");

					// looping through All Shoppings
					for (int i = 0; i < haltListJSONArray.length(); i++) {
						JSONObject c = haltListJSONArray.getJSONObject(i);

						String idH = c.getString("idH");
						String idJserver = c.getString("idJ");
						String expectedArrivalTime = c
								.getString("expectedArrivalTime");
						String expectedDepartureTime = c
								.getString("expectedDepartureTime");
						String recorderVoiceH = c.getString("recorderVoiceH");
						String station = c.getString("station");

						// tmp hashmap for single contact
						HashMap<String, String> halt = new HashMap<String, String>();

						if (idJ.equals(idJserver)) {
							// adding each child node to HashMap key => value
							halt.put("idH", idH);
							halt.put("idJ", idJ);
							halt.put("expectedArrivalTime", expectedArrivalTime);
							halt.put("expectedDepartureTime",
									expectedDepartureTime);
							halt.put("recorderVoiceH", recorderVoiceH);
							halt.put("station", station);

							haltList.add(halt);
						}
					}

					jsonObj = new JSONObject(jsonStrJourney);
					haltListJSONArray = jsonObj.getJSONArray("oneJourney");
					journey = haltListJSONArray.getJSONObject(0);

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

				try {
					if ((journey.getString("idJ").equals(idJ))) {
						planName.setText(": " + journey.getString("planName"));
						Destination.setText(": "
								+ journey.getString("destination"));
						Date.setText(": " + journey.getString("date"));
						ArrivalTime.setText(": "
								+ journey.getString("expectedArrivalTime"));
						Source.setText(": " + journey.getString("source"));
						haltNumber.setText(": "
								+ journey.getString("haltsNumber"));
					}
				} catch (JSONException e) {
				}

				adapterHaltlist = new SimpleAdapter(ShowJourneyActivity.this,
						haltList, R.layout.show_halt_listview, new String[] {
								"station", "expectedArrivalTime",
								"expectedDepartureTime" }, new int[] {
								R.id.station, R.id.arrivalTime,
								R.id.departureTime });

				haltItemList.setAdapter(adapterHaltlist);
			} else {
				new AlertDialog.Builder(ShowJourneyActivity.this)
						.setTitle("Message")
						.setMessage("Connection problem with the server.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK", null).show();
			}
		}
	}

	private class DeleteHalt extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ShowJourneyActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("idH", idItemToDelete));

			String result = sh.makeServiceCall(serverURL + deleteHaltURL,
					ServiceHandler.POST, urlParameters);

			Log.e("result >", "> " + result);
			if (result != null) {
				if (Boolean.parseBoolean(result))
					deleteOK = 1;
				else
					deleteOK = 0;
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

			if (deleteOK == 1) {
				idItemToDelete = "";
				new AlertDialog.Builder(ShowJourneyActivity.this)
						.setTitle("Message")
						.setMessage("Halt has been deleted.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										haltList.clear();
										new GetHaltItemsAndJourneyInfo()
												.execute();
									}
								}).show();

			} else {
				if (serverOK == 1) {
					new AlertDialog.Builder(ShowJourneyActivity.this)
							.setTitle("Message")
							.setMessage("Erreur, Can you please try again.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				} else {
					new AlertDialog.Builder(ShowJourneyActivity.this)
							.setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			}

		}
	}
}
