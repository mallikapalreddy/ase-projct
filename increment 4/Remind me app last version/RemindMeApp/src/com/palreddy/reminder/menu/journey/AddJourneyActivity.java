package com.palreddy.reminder.menu.journey;

import java.util.ArrayList;
import java.util.Calendar;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.ServiceHandler;
import com.palreddy.reminder.helper.Validation;
import com.palreddy.reminder.menu.shopping.SearchAStoreActivity;

public class AddJourneyActivity extends ActionBarActivity {

	private TextView textViewActivityTitle;
	private EditText editTextDate;
	private EditText editTextDestination;
	private EditText editTextExpectedArrivalTime;
	private EditText editTextPlanname;
	private EditText editTextSource;
	private Button btnAddUpdate;
	private ImageView calendarImageView;
	private ImageView timeImageView;
	private Button btnRecorderJourney;

	private ImageView mapsImageViewSource;
	private ImageView mapsImageViewDest;
	private boolean isItMapsImageViewSource = false;
	private boolean isItMapsImageViewDest = false;

	private String TextDate = "";
	private String TextDestination = "";
	private String TextExpectedArrivalTime = "";
	private String TextPlanname = "";
	private String TextSource = "";

	private boolean isAdded = false;
	private boolean isUpdate = false;

	static Context ctx;

	private int serverOK = 0;
	private ProgressDialog pDialog;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "";
	private static String addJourneyURL = "/journey/addjourney";
	private static String updateJourneyURL = "/journey/updatejourney";

	private String idJourneyToUpdate = "";

	JSONArray serverJSONResp = null;

	public static final String KEY_SHOW = "showAlertIcon";
	private boolean isShow = true;

	boolean isAudioRecorded = false;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_journey);
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
					AddJourneyActivity.this).create();
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
		editTextDate = (EditText) findViewById(R.id.editTextDate);
		editTextDestination = (EditText) findViewById(R.id.editTextDestination);
		editTextExpectedArrivalTime = (EditText) findViewById(R.id.editTextExpectedArrivalTime);
		editTextPlanname = (EditText) findViewById(R.id.editTextPlanName);
		editTextSource = (EditText) findViewById(R.id.editTextSource);

		// button Add or update
		btnAddUpdate = (Button) findViewById(R.id.btn_add_update);
		btnAddUpdate.setText("Add");

		Intent intent = getIntent();
		String from = intent.getStringExtra("from");

		if (from != null && from.equals("update")) {
			btnAddUpdate.setText("Update");
			textViewActivityTitle.setText("Update Journey");
			editTextDate.setText(intent.getStringExtra("date"));
			editTextDestination.setText(intent.getStringExtra("destination"));
			editTextExpectedArrivalTime.setText(intent
					.getStringExtra("expectedArrivalTime"));
			editTextPlanname.setText(intent.getStringExtra("planname"));
			editTextSource.setText(intent.getStringExtra("source"));
			idJourneyToUpdate = intent.getStringExtra("idJ");
		}

		// when user click the add or the update button
		btnAddUpdate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String text = btnAddUpdate.getText().toString();
				if (text.equals("Add")) {
					if (Validation.hasText(editTextPlanname)
							&& Validation.hasText(editTextSource)
							&& Validation.hasText(editTextDestination)
							&& Validation.hasText(editTextDate)
							&& Validation.hasText(editTextExpectedArrivalTime)) {

						if (!isAudioRecorded) {
							new AlertDialog.Builder(AddJourneyActivity.this)
									.setTitle("Message")
									.setMessage(
											"Please, Record your reminder first.")
									.setIcon(android.R.drawable.ic_dialog_info)
									.setNegativeButton("Ok", null).show();
							return;
						}

						TextDate = editTextDate.getText().toString().trim();
						TextDestination = editTextDestination.getText()
								.toString().trim();
						TextExpectedArrivalTime = editTextExpectedArrivalTime
								.getText().toString().trim();
						TextPlanname = editTextPlanname.getText().toString()
								.trim();
						TextSource = editTextSource.getText().toString().trim();

						new addJourney().execute();
					}
				}
				if (text.equals("Update")) {
					if (!isAudioRecorded) {
						new AlertDialog.Builder(AddJourneyActivity.this)
								.setTitle("Message")
								.setMessage(
										"Please, Record your reminder first.")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setNegativeButton("Ok", null).show();
						return;
					}

					TextDate = editTextDate.getText().toString().trim();
					TextDestination = editTextDestination.getText().toString()
							.trim();
					TextExpectedArrivalTime = editTextExpectedArrivalTime
							.getText().toString().trim();
					TextPlanname = editTextPlanname.getText().toString().trim();
					TextSource = editTextSource.getText().toString().trim();

					new updateJourney().execute();
				}
			}
		});

		calendarImageView = (ImageView) findViewById(R.id.calendarImageView);
		calendarImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDatePicker();
			}
		});

		timeImageView = (ImageView) findViewById(R.id.timeImageView);
		timeImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertTimePicker();
			}
		});

		// button Recorder for shopping
		btnRecorderJourney = (Button) findViewById(R.id.btnRecorderJourney);
		btnRecorderJourney.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (Validation.hasText(editTextPlanname)
						&& Validation.hasText(editTextSource)
						&& Validation.hasText(editTextDestination)
						&& Validation.hasText(editTextDate)
						&& Validation.hasText(editTextExpectedArrivalTime)) {
					isAudioRecorded = true;
					String fileName = editTextPlanname.getText().toString()
							.trim()
							+ "Journey";
					recordAudio(fileName, ctx);
				}
			}
		});

		// maps Image View Source
		mapsImageViewSource = (ImageView) findViewById(R.id.mapImageViewSource);
		mapsImageViewSource.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isItMapsImageViewSource = true;
				startActivity(new Intent(getApplicationContext(),
						SearchAStoreActivity.class));
			}
		});

		// maps Image View Dest
		mapsImageViewDest = (ImageView) findViewById(R.id.mapImageViewDest);
		mapsImageViewDest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isItMapsImageViewDest = true;
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
			SavePreferences("isAddressSaved", "false");
			if (isItMapsImageViewDest) {
				isItMapsImageViewDest = false;
				editTextDestination.setText(loadPrefs("address"));
			}
			if (isItMapsImageViewSource) {
				isItMapsImageViewSource = false;
				editTextSource.setText(loadPrefs("address"));
			}
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
				AddJourneyActivity.this);
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

	private class addJourney extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(AddJourneyActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters
					.add(new BasicNameValuePair("idU", loadPrefs("userID")));
			urlParameters.add(new BasicNameValuePair("date", TextDate));
			urlParameters.add(new BasicNameValuePair("destination",
					TextDestination));
			urlParameters.add(new BasicNameValuePair("recorderVoice",
					"recorderVoice"));
			urlParameters.add(new BasicNameValuePair("expectedArrivalTime",
					TextExpectedArrivalTime));
			urlParameters.add(new BasicNameValuePair("planname", TextPlanname));
			urlParameters.add(new BasicNameValuePair("source", TextSource));

			String result = sh.makeServiceCall(serverURL + addJourneyURL,
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
					new AlertDialog.Builder(AddJourneyActivity.this)
							.setTitle("Message")
							.setMessage(
									"The Journey has not been added. Try again please.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				} else {
					new AlertDialog.Builder(AddJourneyActivity.this)
							.setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			} else {
				new AlertDialog.Builder(AddJourneyActivity.this)
						.setTitle("Message")
						.setMessage(
								"Journey added! \nTo add a halt, choose your journey from the list and click \"update\"")
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

	private class updateJourney extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(AddJourneyActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters
					.add(new BasicNameValuePair("idU", loadPrefs("userID")));
			urlParameters.add(new BasicNameValuePair("idJ", idJourneyToUpdate));
			urlParameters.add(new BasicNameValuePair("date", TextDate));
			urlParameters.add(new BasicNameValuePair("destination",
					TextDestination));
			urlParameters.add(new BasicNameValuePair("recorderVoice",
					"recorderVoice"));
			urlParameters.add(new BasicNameValuePair("expectedArrivalTime",
					TextExpectedArrivalTime));
			urlParameters.add(new BasicNameValuePair("planname", TextPlanname));
			urlParameters.add(new BasicNameValuePair("source", TextSource));

			String result = sh.makeServiceCall(serverURL + updateJourneyURL,
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
					new AlertDialog.Builder(AddJourneyActivity.this)
							.setTitle("Message")
							.setMessage(
									"The Journey has not been updated. Try again please.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				} else {
					new AlertDialog.Builder(AddJourneyActivity.this)
							.setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			} else {
				new AlertDialog.Builder(AddJourneyActivity.this)
						.setTitle("Message")
						.setMessage("Journey updated!")
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
	 * Show AlertDialog with date picker.
	 */
	public void alertDatePicker() {

		/*
		 * Inflate the XML view.
		 */
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_picker, null, false);

		// the time picker on the alert dialog, this is how to get the value
		final DatePicker myDatePicker = (DatePicker) view
				.findViewById(R.id.myDatePicker);

		// so that the calendar view won't appear
		myDatePicker.setCalendarViewShown(false);

		// the alert dialog
		new AlertDialog.Builder(AddJourneyActivity.this).setView(view)
				.setTitle("Set Date")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@TargetApi(11)
					public void onClick(DialogInterface dialog, int id) {

						int month = myDatePicker.getMonth() + 1;
						int day = myDatePicker.getDayOfMonth();
						int year = myDatePicker.getYear();

						String monthstr = String.valueOf(month);
						String daystr = String.valueOf(day);

						if (monthstr.length() == 1)
							monthstr = "0" + monthstr.charAt(0);
						if (daystr.length() == 1)
							daystr = "0" + daystr.charAt(0);

						editTextDate.setText(monthstr + "/" + daystr + "/"
								+ year);
						dialog.cancel();
					}

				}).show();
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
		new AlertDialog.Builder(AddJourneyActivity.this).setView(view)
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

						editTextExpectedArrivalTime.setText(currentHourText
								+ ":" + currentMinuteText);
						dialog.cancel();
					}

				}).show();
	}
}
