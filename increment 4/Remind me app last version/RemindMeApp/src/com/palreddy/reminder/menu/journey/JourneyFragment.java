package com.palreddy.reminder.menu.journey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.ServiceHandler;
import com.palreddy.reminder.notifications.JourneyNotifications;

public class JourneyFragment extends Fragment {

	private Button addJuorneyButton;
	private ListView journeyItemList;

	private Button startButton;
	private Button exitButton;

	static Context ctx;

	private int serverOK = 0;
	private int deleteOK = 0;
	private String idItemToDelete = "";

	private ProgressDialog pDialog;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "";
	private static String getJourneyListURL = "/journey/getjourneylist";
	private static String deleteJourneyURL = "/journey/deletejourney";

	private JSONArray journeylistJSONArray = null;
	private ArrayList<HashMap<String, String>> journeyList;
	private ListAdapter adapterJourneylist;

	private HashMap<String, String> mapSelected;
	private HashMap<String, String> startedJourney;

	public static final String KEY_SHOW = "show";
	private boolean isFirst = true;
	private boolean isFirstOnResume = true;
	private boolean isShow = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.fragment_journey, container,
				false);

		serverURL = loadPrefs(TAG_SERVER_URL);
		ctx = getActivity();

		// Show the alert dialog only one time when the user run the app
		// (journey tab)
		// and never when he choose "Never show this message again."

		String valueShow = loadPrefs(KEY_SHOW);
		if (valueShow != null)
			isShow = Boolean.parseBoolean(valueShow);

		if (isShow && isFirst) {
			isFirst = false;
			LayoutInflater alertInflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View alertView = alertInflater.inflate(
					R.layout.alert_dialog_journey, null, false);
			final CheckBox myCheckBox = (CheckBox) alertView
					.findViewById(R.id.checkBoxShowAbout);

			AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
					.create();
			alertDialog.setView(alertView);
			alertDialog.setCancelable(false);
			alertDialog.setTitle("  Information:");
			alertDialog.setIcon(android.R.drawable.ic_dialog_info);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					journeyList.clear();
					new GetJourneyList().execute();
					if (myCheckBox.isChecked()) {
						SavePreferences(KEY_SHOW, "false");
					}
					dialog.dismiss();
				}
			});
			alertDialog.show();
		}

		// journey list
		journeyList = new ArrayList<HashMap<String, String>>();

		// button
		addJuorneyButton = (Button) mainView.findViewById(R.id.addButton);
		addJuorneyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ctx, AddJourneyActivity.class));
			}
		});

		// Start Button
		startButton = (Button) mainView.findViewById(R.id.startButton);
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (journeyList.isEmpty()) {
					new AlertDialog.Builder(ctx)
							.setTitle("Message")
							.setMessage(
									"Please add a new journey, then click the start button.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
					return;
				}

				final String idNextJourney = findNextJourney();
				if (idNextJourney == null) {
					new AlertDialog.Builder(ctx)
							.setTitle("Message")
							.setMessage(
									"Please add today's journey, then click the start button.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
					return;
				}

				String[] source = (startedJourney.get("source")).split(" # ");
				String[] dest = (startedJourney.get("destination"))
						.split(" # ");

				String meassageDialog = "Start the Journey: \n "
						+ "* Plan Name: " + startedJourney.get("planName")
						+ "\n " + "* Source: " + source[0] + "\n "
						+ "* Destination: " + dest[0] + "\n " + "* Date: "
						+ startedJourney.get("date") + "\n "
						+ "* Expected Arrival Time: "
						+ startedJourney.get("expectedArrivalTime") + "\n "
						+ "* Halts Number: "
						+ startedJourney.get("haltsNumber");

				new AlertDialog.Builder(ctx)
						.setTitle("Message")
						.setMessage(meassageDialog)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("YES",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										exitButton.setEnabled(true);
										startButton.setEnabled(false);
										SavePreferences("keepserviceRunning",
												"true");
										Intent journeyServiceIntent = new Intent(
												ctx, JourneyNotifications.class);
										journeyServiceIntent.putExtra(
												"JourneyID", idNextJourney);
										getActivity().startService(
												journeyServiceIntent);
									}
								})
						.setNegativeButton("NO",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();
			}

			private String findNextJourney() {
				String id = null;
				Calendar currentCalendar = getCalendarFromStringDate(getCurrentDate());
				// String dateToSave = currentDate;
				// boolean firstTime = true;
				for (int i = 0; i < journeyList.size(); i++) {
					startedJourney = journeyList.get(i);
					String journeyIDate = startedJourney.get("date");

					Calendar journeyCalendar = getCalendarFromStringDate(journeyIDate);
					if (journeyCalendar.equals(currentCalendar))
						return startedJourney.get("idJ");

				}
				return id;
			}

			private Calendar getCalendarFromStringDate(String str) {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date date1 = null;
				try {
					date1 = sdf.parse(str);
				} catch (ParseException e) {
				}
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(date1);
				return cal1;
			}

			private String getCurrentDate() {
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				return sdf.format(calendar.getTime());
			}
		}); // end start button Listener

		exitButton = (Button) mainView.findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				new AlertDialog.Builder(ctx)
						.setTitle("Confirmation...")
						.setMessage(
								"Are you sure you want to stop the Journey Reminder Service?")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("YES",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										SavePreferences("keepserviceRunning",
												"false");
										exitButton.setEnabled(false);
										startButton.setEnabled(true);
									}
								})
						.setNegativeButton("NO",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();
			}
		});

		// List View
		journeyItemList = (ListView) mainView
				.findViewById(R.id.journeyItemList);
		journeyItemList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				mapSelected = (HashMap<String, String>) journeyItemList
						.getItemAtPosition(position);

				AlertDialog.Builder alertadd = new AlertDialog.Builder(ctx);
				LayoutInflater factory = LayoutInflater.from(ctx);
				final View view = factory.inflate(
						R.layout.showing_read_update_delete_dialog, null);
				alertadd.setView(view);
				// Setting Positive "Delete" Button
				alertadd.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								new AlertDialog.Builder(ctx)
										.setTitle("Confirmation ...")
										.setMessage(
												"Are you sure you want to delete this Journey ?")
										.setIcon(android.R.drawable.ic_delete)
										.setPositiveButton(
												"YES",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														idItemToDelete = mapSelected
																.get("idJ");
														new DeleteJourney()
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
								Intent nextIntent = new Intent(ctx,
										ShowJourneyActivity.class);
								nextIntent.putExtra("idJ",
										mapSelected.get("idJ"));
								startActivity(nextIntent);
							}
						});
				// Setting Negative "Read" Button
				alertadd.setNegativeButton("Show",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent nextIntent = new Intent(ctx,
										ShowJourneyActivity.class);
								nextIntent.putExtra("idJ",
										mapSelected.get("idJ"));
								startActivity(nextIntent);
							}
						});

				alertadd.show();
			}
		});

		return mainView;
	}

	@Override
	public void onResume() {
		if (isFirstOnResume && isShow) {
			isFirstOnResume = false;
		} else {
			journeyList.clear();
			new GetJourneyList().execute();
		}

		super.onResume();
	}

	public String loadPrefs(String KEY) {

		SharedPreferences sh_Pref = getActivity().getSharedPreferences(
				"MyData", Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}

	private void SavePreferences(String key, String value) {
		SharedPreferences sh_Pref = getActivity().getSharedPreferences(
				"MyData", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sh_Pref.edit();
		edit.putString(key, value);
		edit.commit();
	}

	private class GetJourneyList extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ctx);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters
					.add(new BasicNameValuePair("idU", loadPrefs("userID")));
			String jsonStr = sh.makeServiceCall(serverURL + getJourneyListURL,
					ServiceHandler.POST, urlParameters);

			if (jsonStr != null) {

				serverOK = 1;
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// Getting JSON Array node
					journeylistJSONArray = jsonObj.getJSONArray("listJourney");

					// looping through All Journeys
					for (int i = 0; i < journeylistJSONArray.length(); i++) {
						JSONObject c = journeylistJSONArray.getJSONObject(i);

						String idJ = c.getString("idJ");

						String date = c.getString("date");
						String destination = c.getString("destination");
						String expectedArrivalTime = c
								.getString("expectedArrivalTime");
						String planName = c.getString("planName");
						String recorderVoice = c.getString("recorderVoice");
						String source = c.getString("source");
						String haltsNumber = c.getString("haltsNumber");

						// tmp hashmap for single contact
						HashMap<String, String> journeyHash = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						journeyHash.put("idJ", idJ);
						journeyHash.put("date", date);
						journeyHash.put("destination", destination);
						journeyHash.put("expectedArrivalTime",
								expectedArrivalTime);
						journeyHash.put("planName", planName);
						journeyHash.put("recorderVoice", recorderVoice);
						journeyHash.put("source", source);
						journeyHash.put("haltsNumber", haltsNumber);

						journeyList.add(journeyHash);

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
				adapterJourneylist = new SimpleAdapter(ctx, journeyList,
						R.layout.show_journey_listview, new String[] {
								"planName", "source", "destination", "date",
								"haltsNumber" }, new int[] { R.id.planName,
								R.id.source, R.id.destination, R.id.date,
								R.id.halts });

				journeyItemList.setAdapter(adapterJourneylist);
			} else {
				new AlertDialog.Builder(ctx).setTitle("Message")
						.setMessage("Connection problem with the server.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK", null).show();
			}
		}

	}

	private class DeleteJourney extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ctx);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("idJ", idItemToDelete));

			String result = sh.makeServiceCall(serverURL + deleteJourneyURL,
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
				new AlertDialog.Builder(ctx)
						.setTitle("Message")
						.setMessage("Journey has been deleted.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										journeyList.clear();
										new GetJourneyList().execute();
									}
								}).show();

			} else {
				if (serverOK == 1) {
					new AlertDialog.Builder(ctx).setTitle("Message")
							.setMessage("Erreur, Can you please try again.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				} else {
					new AlertDialog.Builder(ctx).setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			}

		}
	}
}
