package com.palreddy.reminder.notifications;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.ServiceHandler;

public class JourneyNotifications extends IntentService implements
		TextToSpeech.OnInitListener {

	static Context ctx;

	private String idJ;

	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "";
	private static String getHaltListURL = "/halt/gethaltlist";
	private static String getOneJourneyURL = "/journey/getonejourney";

	private JSONArray haltListJSONArray = null;
	private ArrayList<HashMap<String, String>> haltList;

	private JSONObject journey;

	private TextToSpeech tts;
	private String speech;

	private boolean thereIsEx;
	private boolean lateForJourney = true;
	private boolean keepServiceRunning = true;

	public JourneyNotifications() {
		super("JourneyNotifications");
	}

	public static final String EVENT_ACTION = "JourneyTime";

	@Override
	protected void onHandleIntent(Intent intent) {

		serverURL = loadPrefs(TAG_SERVER_URL);

		ctx = getApplicationContext();
		idJ = intent.getStringExtra("JourneyID");
		haltList = new ArrayList<HashMap<String, String>>();
		
		tts = new TextToSpeech(this, this);

		GetHaltItemsAndJourneyInfo();
		startNotifyService();
	}

	private void startNotifyService() {

		/*
		 * halts reminder
		 */
		while (!haltList.isEmpty()) {
			thereIsEx = false;
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

			// Halt recorded reminder
			String fileName = nextHalt.get("station") + "HaltReminder"
					+ nextHalt.get("idJ");
			/*
			 * Next Halt Arrival Time
			 */
			String hourStartHalt = getCurrentHour();
			String hourStopHalt = nextHalt.get("expectedArrivalTime") + ":00";

			long timeTowait[] = getTimeToWait(hourStartHalt, hourStopHalt);
			if (timeTowait != null) {
				for (int i = 0; i < timeTowait[0]; i++) {
					sleep(3600);
				}
				sleep((int) (timeTowait[1] * 60));
				sleep((int) timeTowait[2]);

				// check if the use did chose to exit (stop the service)
				// if true ::> return
				String keepServiceRunningString = loadPrefs("keepserviceRunning");
				if (keepServiceRunningString != null)
					keepServiceRunning = Boolean
							.parseBoolean(keepServiceRunningString);
				if (!keepServiceRunning)
					return;

				// Halt time
				notificationJourney(
						"Halt Station: " + nextHalt.get("station"),
						"Expected Arrival Time: "
								+ hourStopHalt.subSequence(0, 5));

				sleep(2);
				speech = "Remind Me app";
				speakOut();
				sleep(3);

				try {
					int time = play(fileName);
					System.err.println("time >>>  " + time);
					sleep(time / 1000);
				} catch (IllegalArgumentException e) {
					thereIsEx = true;
				} catch (SecurityException e) {
					thereIsEx = true;
				} catch (IllegalStateException e) {
					thereIsEx = true;
				} catch (IOException e) {
					thereIsEx = true;
				}

				if (thereIsEx) {
					thereIsEx = false;
					speech = "Remind Me app, Next station is "
							+ nextHalt.get("station");
					speakOut();
					sleep(2);
					speakOut();
				}
			}
			/*
			 * Next Halt Departure Time
			 */
			hourStartHalt = getCurrentHour();
			hourStopHalt = nextHalt.get("expectedDepartureTime") + ":00";

			timeTowait = getTimeToWait(hourStartHalt, hourStopHalt);
			if (timeTowait != null) {
				for (int i = 0; i < timeTowait[0]; i++) {
					sleep(3600);
				}
				sleep((int) (timeTowait[1] * 60));
				sleep((int) timeTowait[2]);

				// check if the use did chose to exit (stop the service)
				// if true ::> return
				String keepServiceRunningString = loadPrefs("keepserviceRunning");
				if (keepServiceRunningString != null)
					keepServiceRunning = Boolean
							.parseBoolean(keepServiceRunningString);
				if (!keepServiceRunning)
					return;

				// Halt time
				notificationJourney(
						"Halt Station: " + nextHalt.get("station"),
						"Expected Departure Time: "
								+ hourStopHalt.subSequence(0, 5));

				sleep(2);
				speech = "Remind Me app";
				speakOut();
				sleep(3);
				try {
					int time = play(fileName);
					sleep(time / 1000);
				} catch (IllegalArgumentException e) {
					thereIsEx = true;
				} catch (SecurityException e) {
					thereIsEx = true;
				} catch (IllegalStateException e) {
					thereIsEx = true;
				} catch (IOException e) {
					thereIsEx = true;
				}

				if (thereIsEx) {
					speech = "Remind Me app, Next station is "
							+ nextHalt.get("station");
					speakOut();
					sleep(2);
					speakOut();
				}
			}
			haltList.remove(nextHalt);
		}

		/*
		 * Journey reminder
		 */
		String planName;
		try {
			planName = journey.getString("planName");
			String journeyArrivalTime = journey
					.getString("expectedArrivalTime");
			String journeyDest = journey.getString("destination");

			// Journey recorded reminder
			String journeyFileName = planName + "Journey";

			String hourStartJourney = getCurrentHour();
			String hourStopJourney = journeyArrivalTime + ":00";

			long timeTowait[] = getTimeToWait(hourStartJourney, hourStopJourney);
			if (timeTowait != null) {
				lateForJourney = false;
				for (int i = 0; i < timeTowait[0]; i++) {
					sleep(3600);
				}
				sleep((int) (timeTowait[1] * 60));
				sleep((int) timeTowait[2]);

				// check if the use did chose to exit (stop the service)
				// if true ::> return
				String keepServiceRunningString = loadPrefs("keepserviceRunning");
				if (keepServiceRunningString != null)
					keepServiceRunning = Boolean
							.parseBoolean(keepServiceRunningString);
				if (!keepServiceRunning)
					return;

				// Journey time
				notificationJourney(
						"Journey Destination: " + journeyDest,
						"Expected Arrival Time: "
								+ hourStopJourney.subSequence(0, 5));

				sleep(2);
				speech = "Remind Me app";
				speakOut();
				sleep(3);

				try {
					int time = play(journeyFileName);
					System.err.println("time >>>  " + time);
					sleep(time / 1000);
				} catch (IllegalArgumentException e) {
					thereIsEx = true;
				} catch (SecurityException e) {
					thereIsEx = true;
				} catch (IllegalStateException e) {
					thereIsEx = true;
				} catch (IOException e) {
					thereIsEx = true;
				}

				if (thereIsEx) {
					thereIsEx = false;
					speech = "Remind Me app, Next station is " + journeyDest;
					speakOut();
					sleep(2);
					speakOut();
				}
			}

		} catch (JSONException e1) {
			lateForJourney = true;
		}

		if (lateForJourney) {

			speech = "It is already late to start journey.";
			speakOut();
			sleep(2);
			speakOut();

		}

	}

	private static long[] getTimeToWait(String dateStart, String dateStop) {

		long[] time = new long[3];
		// HH converts hour in 24 hours format (0-23), day calculation
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

		Date d1 = null;
		Date d2 = null;

		try {
			d1 = format.parse(dateStart);
			d2 = format.parse(dateStop);

			// in milliseconds
			long diff = d2.getTime() - d1.getTime();
			if (!(diff < 0)) {
				time[0] = diff / (60 * 60 * 1000);
				time[1] = diff / (60 * 1000) % 60;
				time[2] = diff / 1000 % 60;
			} else {
				time = null;
			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}

		return time;
	}

	private static String getCurrentHour() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(calendar.getTime());
	}

	private void GetHaltItemsAndJourneyInfo() {
		// Creating service handler class instance
		ServiceHandler sh = new ServiceHandler();

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

		urlParameters.add(new BasicNameValuePair("idJ", idJ));
		String jsonStrHalts = sh.makeServiceCall(serverURL + getHaltListURL,
				ServiceHandler.POST, urlParameters);

		System.err.println("jsonStrHalts " + jsonStrHalts);

		String jsonStrJourney = sh.makeServiceCall(
				serverURL + getOneJourneyURL, ServiceHandler.POST,
				urlParameters);

		System.err.println("jsonStrJourney " + jsonStrJourney);

		if (jsonStrHalts != null && jsonStrJourney != null) {
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
						halt.put("expectedDepartureTime", expectedDepartureTime);
						halt.put("recorderVoiceH", recorderVoiceH);
						halt.put("station", station);

						haltList.add(halt);
					}
				}

				jsonObj = new JSONObject(jsonStrJourney);
				haltListJSONArray = jsonObj.getJSONArray("oneJourney");
				journey = haltListJSONArray.getJSONObject(0);

			} catch (JSONException e) {
			}
		} else {
		}

	}

	public String loadPrefs(String KEY) {

		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}

	private void sleep(int seconds) {

		for (int i = 0; i < 1000; i++) {
			new Thread((new Runnable() {
				public void run() {
				}
			})).start();

			try {
				Thread.sleep(seconds);
			} catch (InterruptedException e) {
			}
		}
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

	@SuppressWarnings("deprecation")
	private void notificationJourney(String messageTop, String messageBottom) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				"RemindMe Journey.", 0);
		// Uses the default lighting scheme
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		// Uses the default vibrate (yes or no)
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		// Uses the default sound
		notification.defaults |= Notification.DEFAULT_SOUND;
		// Will show lights and make the notification disappear when the presses
		// it
		notification.flags |= Notification.FLAG_AUTO_CANCEL
				| Notification.FLAG_SHOW_LIGHTS;

		notification.setLatestEventInfo(getApplicationContext(), messageTop,
				messageBottom, null);
		notificationManager.notify(123456789, notification);

	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			// tts.setPitch(5); // set pitch level

			// tts.setSpeechRate(2); // set speech speed rate

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "Language is not supported");
			} else {
				speakOut();
			}

		} else {
			Log.e("TTS", "Initilization Failed");
		}

	}

	private void speakOut() {

		tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
	}

	public int play(String fileName) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

		fileName = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/" + fileName;

		MediaPlayer m = new MediaPlayer();
		m.setDataSource(fileName);
		m.prepare();
		m.start();
		return m.getDuration();
	}

}
