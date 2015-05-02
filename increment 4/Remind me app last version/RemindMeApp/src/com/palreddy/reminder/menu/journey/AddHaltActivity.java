package com.palreddy.reminder.menu.journey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.ServiceHandler;
import com.palreddy.reminder.helper.Validation;
import com.palreddy.reminder.menu.shopping.SearchAStoreActivity;

public class AddHaltActivity extends ActionBarActivity {

	private TextView textViewActivityTitle;
	private EditText editTextStation;
	private EditText editTextArrivaleTime;
	private EditText editTextDepartureTime;
	private ImageView timeImageViewArrival;
	private ImageView timeImageViewDeparture;
	private ImageView mapsImageView;

	private boolean isItTimeImageViewDeparture = false;
	private boolean isItTimeImageViewArrival = false;

	private Button btnAddUpdate;
	private Button btnRecorderHalt;

	private String TextStation = "";
	private String TextArrivaleTime = "";
	private String TextDepartureTime = "";

	private boolean isAdded = false;
	private boolean isUpdate = false;

	static Context ctx;

	private int serverOK = 0;
	private ProgressDialog pDialog;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "";
	private static String addHaltURL = "/halt/addhalt";
	private static String updateHaltURL = "/halt/updatehalt";

	private String idHaltToUpdate = "";
	private String idJourney = "";
	private String journeyTime = "";

	boolean isAudioRecorded = false;

	JSONArray serverJSONResp = null;

	public static final String KEY_SHOW = "showAlertIcon";
	private boolean isShow = true;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_halt);
		getSupportActionBar().hide();

		serverURL = loadPrefs(TAG_SERVER_URL);
		ctx = getApplicationContext();

		// alert dialog for using icons
		String valueShow = loadPrefs(KEY_SHOW);
		if (valueShow != null)
			isShow = Boolean.parseBoolean(valueShow);

		if (isShow) {
			LayoutInflater alertInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View alertView = alertInflater.inflate(
					R.layout.alert_dialog_use_icons, null, false);
			final CheckBox myCheckBox = (CheckBox) alertView
					.findViewById(R.id.checkBoxShowAbout);

			AlertDialog alertDialog = new AlertDialog.Builder(
					AddHaltActivity.this).create();
			alertDialog.setView(alertView);
			alertDialog.setCancelable(false);
			alertDialog.setTitle("  Information:");
			alertDialog.setIcon(android.R.drawable.ic_dialog_info);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					if (myCheckBox.isChecked()) {
						SavePreferences(KEY_SHOW, "false");
					}
				}
			});
			alertDialog.show();
		}

		// Text View Activity Title
		textViewActivityTitle = (TextView) findViewById(R.id.textViewActivityTitle);

		// Edit Text
		editTextStation = (EditText) findViewById(R.id.editStation);
		editTextArrivaleTime = (EditText) findViewById(R.id.editArrivaleTime);
		editTextDepartureTime = (EditText) findViewById(R.id.editDepartureTime);

		// button Add or update
		btnAddUpdate = (Button) findViewById(R.id.btn_add_update);
		btnAddUpdate.setText("Add");

		Intent intent = getIntent();
		String from = intent.getStringExtra("from");
		idJourney = intent.getStringExtra("idJ");
		journeyTime = intent.getStringExtra("journeyTime");

		if (from != null && from.equals("update")) {
			btnAddUpdate.setText("Update");
			textViewActivityTitle.setText("Update Halt");
			editTextStation.setText(intent.getStringExtra("station"));
			editTextArrivaleTime.setText(intent
					.getStringExtra("expectedArrivalTime"));
			editTextDepartureTime.setText(intent
					.getStringExtra("expectedDepartureTime"));
			idHaltToUpdate = intent.getStringExtra("idH");
		}

		// when user click the add or the update button
		btnAddUpdate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!Validation.hasText(editTextStation)
						|| !Validation.hasText(editTextArrivaleTime)
						|| !Validation.hasText(editTextDepartureTime))
					return;

				if (!isAudioRecorded) {
					new AlertDialog.Builder(AddHaltActivity.this)
							.setTitle("Message")
							.setMessage("Please, Record your reminder first.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNegativeButton("Ok", null).show();
					return;
				}

				TextStation = editTextStation.getText().toString().trim();
				TextArrivaleTime = editTextArrivaleTime.getText().toString()
						.trim();
				TextDepartureTime = editTextDepartureTime.getText().toString()
						.trim();

				Calendar firstHour = getCalendarFromStringTime(TextArrivaleTime);
				Calendar secondtHour = getCalendarFromStringTime(TextDepartureTime);
				Calendar thirdHour = getCalendarFromStringTime(journeyTime);

				if (!(firstHour.before(secondtHour) && secondtHour
						.before(thirdHour))) {
					new AlertDialog.Builder(AddHaltActivity.this)
							.setTitle("Message")
							.setMessage(
									"Please verify the timing.\n"
											+ "Halt Arrivale Time: "
											+ TextArrivaleTime + "\n"
											+ "Halt Departure Time: "
											+ TextDepartureTime + "\n"
											+ "Journey Arrivale Time: "
											+ journeyTime)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNegativeButton("Ok", null).show();
					return;
				}

				String text = btnAddUpdate.getText().toString();
				if (text.equals("Add")) {
					new addHalt().execute();
				}
				if (text.equals("Update")) {
					if (!isAudioRecorded) {
						new AlertDialog.Builder(AddHaltActivity.this)
								.setTitle("Message")
								.setMessage(
										"Please, Record your reminder first.")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setNegativeButton("Ok", null).show();
						return;
					}
					new updateHalt().execute();
				}

			}
		});

		timeImageViewArrival = (ImageView) findViewById(R.id.timeImageViewArrival);
		timeImageViewArrival.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isItTimeImageViewArrival = true;
				alertTimePicker();
			}
		});

		timeImageViewDeparture = (ImageView) findViewById(R.id.timeImageViewDeparture);
		timeImageViewDeparture.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isItTimeImageViewDeparture = true;
				alertTimePicker();
			}
		});

		// button Recorder for Halt
		btnRecorderHalt = (Button) findViewById(R.id.btnRecorderHalt);
		btnRecorderHalt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (!Validation.hasText(editTextStation)
						|| !Validation.hasText(editTextArrivaleTime)
						|| !Validation.hasText(editTextDepartureTime))
					return;

				isAudioRecorded = true;
				String fileName = editTextStation.getText().toString()
						+ "HaltReminder" + idJourney;
				recordAudio(fileName, ctx);
			}
		});

		// maps Image View
		mapsImageView = (ImageView) findViewById(R.id.mapImageView);
		mapsImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						SearchAStoreActivity.class));
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		String isAddressSaved = loadPrefs("isAddressSaved");
		if (isAddressSaved != null && Boolean.parseBoolean(isAddressSaved)) {
			editTextStation.setText(loadPrefs("address"));
			SavePreferences("isAddressSaved", "false");
		}
	}

	public String loadPrefs(String KEY) {

		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}

	private void SavePreferences(String key, String value) {
		SharedPreferences sh_Pref = ctx.getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sh_Pref.edit();
		edit.putString(key, value);
		edit.commit();
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

	public void recordAudio(String fileName, Context ctx) {
		final MediaRecorder recorder = new MediaRecorder();
		ContentValues values = new ContentValues(3);
		values.put(MediaStore.MediaColumns.TITLE, fileName);
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		fileName = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + fileName;
		recorder.setOutputFile(fileName);
		try {
			recorder.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}

		final ProgressDialog mProgressDialog = new ProgressDialog(
				AddHaltActivity.this);
		mProgressDialog.setTitle("The record has been started: ");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage("Go ahead, Speak...");
		mProgressDialog.setButton("Stop recording",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mProgressDialog.dismiss();
						recorder.stop();
						recorder.release();
					}
				});

		mProgressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface p1) {
						recorder.stop();
						recorder.release();
					}
				});
		recorder.start();
		mProgressDialog.show();
	}

	private class addHalt extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(AddHaltActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("idJ", idJourney));
			urlParameters.add(new BasicNameValuePair("station", TextStation));
			urlParameters.add(new BasicNameValuePair("expectedArrivalTime",
					TextArrivaleTime));
			urlParameters.add(new BasicNameValuePair("recorderVoiceH",
					"recorderVoice"));
			urlParameters.add(new BasicNameValuePair("expectedDepartureTime",
					TextDepartureTime));

			String result = sh.makeServiceCall(serverURL + addHaltURL,
					ServiceHandler.POST, urlParameters);

			if (result != null) {
				serverOK = 1;
				isAdded = Boolean.parseBoolean(result);
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

			if (!isAdded) {
				if (serverOK == 1) {
					new AlertDialog.Builder(AddHaltActivity.this)
							.setTitle("Message")
							.setMessage(
									"The Halt has not been added. Try again please.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				} else {
					new AlertDialog.Builder(AddHaltActivity.this)
							.setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			} else {
				new AlertDialog.Builder(AddHaltActivity.this)
						.setTitle("Message")
						.setMessage("Halt added!")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNegativeButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).show();

			}

		}
	}

	private class updateHalt extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(AddHaltActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("idH", idHaltToUpdate));
			urlParameters.add(new BasicNameValuePair("idJ", idJourney));
			urlParameters.add(new BasicNameValuePair("station", TextStation));
			urlParameters.add(new BasicNameValuePair("expectedArrivalTime",
					TextArrivaleTime));
			urlParameters.add(new BasicNameValuePair("recorderVoiceH",
					"recorderVoice"));
			urlParameters.add(new BasicNameValuePair("expectedDepartureTime",
					TextDepartureTime));

			String result = sh.makeServiceCall(serverURL + updateHaltURL,
					ServiceHandler.POST, urlParameters);

			if (result != null) {
				serverOK = 1;
				isUpdate = Boolean.parseBoolean(result);
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

			if (!isUpdate) {
				if (serverOK == 1) {
					new AlertDialog.Builder(AddHaltActivity.this)
							.setTitle("Message")
							.setMessage(
									"The Halt has not been updated. Try again please.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				} else {
					new AlertDialog.Builder(AddHaltActivity.this)
							.setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			} else {
				new AlertDialog.Builder(AddHaltActivity.this)
						.setTitle("Message")
						.setMessage("Halt updated!")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNegativeButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).show();

			}

		}
	}

	/*
	 * Show AlertDialog with time picker.
	 */
	public void alertTimePicker() {

		/*
		 * Inflate the XML view.
		 */
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.time_picker, null, false);

		// the time picker on the alert dialog, this is how to get the value
		final TimePicker myTimePicker = (TimePicker) view
				.findViewById(R.id.myTimePicker);

		/*
		 * To remove option for AM/PM, add the following line:
		 * myTimePicker.setIs24HourView(true);
		 */
		myTimePicker.setIs24HourView(true);

		Calendar calendar = Calendar.getInstance();
		myTimePicker.setCurrentHour(calendar.getTime().getHours());
		myTimePicker.setCurrentMinute(calendar.getTime().getMinutes());

		// the alert dialog
		new AlertDialog.Builder(AddHaltActivity.this).setView(view)
				.setTitle("Set Time")
				.setPositiveButton("Go", new DialogInterface.OnClickListener() {
					@TargetApi(11)
					public void onClick(DialogInterface dialog, int id) {

						String currentHourText = myTimePicker.getCurrentHour()
								.toString();
						String currentMinuteText = myTimePicker
								.getCurrentMinute().toString();

						if (currentHourText.length() == 1)
							currentHourText = "0" + currentHourText.charAt(0);
						if (currentMinuteText.length() == 1)
							currentMinuteText = "0"
									+ currentMinuteText.charAt(0);

						if (isItTimeImageViewArrival) {
							isItTimeImageViewArrival = false;
							editTextArrivaleTime.setText(currentHourText + ":"
									+ currentMinuteText);
						}
						if (isItTimeImageViewDeparture) {
							isItTimeImageViewDeparture = false;
							editTextDepartureTime.setText(currentHourText + ":"
									+ currentMinuteText);
						}

						dialog.cancel();
					}

				}).show();
	}
}
