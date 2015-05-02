package com.palreddy.reminder.notifications;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.palreddy.reminder.R;

public class ShoppingNotifications extends IntentService implements
		TextToSpeech.OnInitListener {

	private ArrayList<HashMap<String, String>> shoppingList;
	private String jsonStr;

	private TextToSpeech tts;
	private String speech;

	private boolean thereIsEx;

	public ShoppingNotifications() {
		super("ShoppingNotifications");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		shoppingList = new ArrayList<HashMap<String, String>>();
		jsonStr = intent.getStringExtra("shoppinglist");
		tts = new TextToSpeech(this, this);

		// get list of shopping
		shoppingList = getShoppingList(jsonStr);

		// start Notify Service
		startNotifyService();

	}

	private ArrayList<HashMap<String, String>> getShoppingList(String jsonStr) {

		ArrayList<HashMap<String, String>> shoppingListMethod = new ArrayList<HashMap<String, String>>();
		if (jsonStr != null) {
			JSONArray shoppinglist = null;

			try {
				JSONObject jsonObj = new JSONObject(jsonStr);

				// Getting JSON Array node
				shoppinglist = jsonObj.getJSONArray("listShoppings");

				// looping through All Shoppings
				for (int i = 0; i < shoppinglist.length(); i++) {
					JSONObject c = shoppinglist.getJSONObject(i);

					String idS = c.getString("idS");
					String items = c.getString("items");
					String address = c.getString("address");
					String estimatedAmt = c.getString("estimatedAmt");
					String dateShopping = c.getString("dateShopping");
					String title = c.getString("title");

					// tmp hashmap for single contact
					HashMap<String, String> shop = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					shop.put("idS", idS);
					shop.put("items", items);
					shop.put("address", address);
					shop.put("estimatedAmt", estimatedAmt);
					shop.put("dateShopping", dateShopping);
					shop.put("title", title);

					shoppingListMethod.add(shop);
				}

			} catch (JSONException e) {
			}
		}
		return shoppingListMethod;
	}

	private void startNotifyService() {
		/*
		 * Shopping reminder
		 */
		thereIsEx = false;
		// get the next Shopping
		HashMap<String, String> nextShop;

		System.err.println("startNotifyService");
		
		nextShop = findNextShopping();

		if (nextShop != null) {
			// Shopping recorded reminder
			System.err.println("nextShop != null");
			String fileName = nextShop.get("items") + "Shopping";
			System.err.println("fileName "+fileName);
			/*
			 * Next Shopping time
			 */
			String hourStartShop = getCurrentHour();
			System.err.println("hourStartShop "+hourStartShop);
			String hourStopShop = nextShop.get("title") + ":00";
			System.err.println("hourStopShop "+hourStopShop);

			long timeTowait[] = getTimeToWait(hourStartShop, hourStopShop);
			if (timeTowait != null) {
				for (int i = 0; i < timeTowait[0]; i++) {
					sleep(3600);
				}
				sleep((int) (timeTowait[1] * 60));
				sleep((int) timeTowait[2]);

				// Shopping time
				notificationJourney("Item: " + nextShop.get("items"), "Time: "
						+ hourStopShop.subSequence(0, 5));

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
					thereIsEx = false;
					speech = "Remind Me app, Shopping time, Item to by "
							+ nextShop.get("items");
					speakOut();
					sleep(2);
					speakOut();
				}
			}
		}
	}

	private HashMap<String, String> findNextShopping() {

		HashMap<String, String> shop = null;
		Calendar currentCalendar = getCalendarFromStringDate(getCurrentDate());

		for (int i = 0; i < shoppingList.size(); i++) {
			shop = shoppingList.get(i);
			String shoppingDate = shop.get("dateShopping");
			System.err.println(shoppingDate);

			Calendar journeyCalendar = getCalendarFromStringDate(shoppingDate);
			if (journeyCalendar.equals(currentCalendar))
				return shop;
		}
		return shop;
	}

	private String getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return sdf.format(calendar.getTime());
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

	private static String getCurrentHour() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(calendar.getTime());
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
