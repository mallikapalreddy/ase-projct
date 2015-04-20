package com.hackathon.myyoutube.login;

import java.util.ArrayList;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hackathon.myyoutube.R;
import com.hackathon.myyoutube.SearchActivity;
import com.hackathon.myyoutube.StatisticsActivity;
import com.hackathon.myyoutube.helper.ServiceHandler;
import com.hackathon.myyoutube.helper.Validation;

public class LoginActivity extends Activity {

	private TextView textForgotPassword;
	private TextView textlogoff;
	private EditText username;
	private EditText password;

	private CheckBox checkBoxRememberMe;
	private boolean isRememberMe = false;

	private String loginUser = "";
	private String passUser = "";

	static Context ctx;

	private int serverOK = 0;
	private ProgressDialog pDialog;
	private boolean loginOk = false;
	private String userID;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "http://myyoutube-remindmeapp.rhcloud.com/myyoutube/androidapp";
	private static String authenticationURL = "/authentication";

	JSONArray loginRepJSON = null;
	private static final String TAG_LOGINresp = "LoginResponse";
	private static final String TAG_ID = "id";
	private static final String TAG_ACCEPTED = "accepted";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		SavePreferences(TAG_SERVER_URL, serverURL);

		// Edit Text
		username = (EditText) findViewById(R.id.editTextUsername);
		password = (EditText) findViewById(R.id.editTextPassword);

		//

		((TextView) findViewById(R.id.statistics))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						startActivity(new Intent(getApplicationContext(),
								StatisticsActivity.class));

					}
				});

		// login Button
		((Button) findViewById(R.id.btn_login))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						if (Validation.isEmailAddress(username, true)
								&& Validation.hasText(password)) {

							loginUser = username.getText().toString().trim();
							passUser = password.getText().toString().trim();
							isRememberMe = checkBoxRememberMe.isChecked();
							new login().execute();
						}

					}
				});

		// Text View Forgot password
		textForgotPassword = (TextView) findViewById(R.id.text_forgot_password);
		textForgotPassword.setText(Html.fromHtml(String
				.format(getString(R.string.forgot_password))));
		textForgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ForgotPasswordActivity.class));
				finish();

			}
		});

		// Check Box Remember Me
		checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberdMe);

	}

	private class login extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("email", loginUser));
			urlParameters.add(new BasicNameValuePair("password", passUser));

			String result = sh.makeServiceCall(serverURL + authenticationURL,
					ServiceHandler.POST, urlParameters);

			Log.e("result >", "> " + result);
			if (result != null) {
				try {
					serverOK = 1;
					JSONObject jsonObj = new JSONObject(result);

					// Getting JSON Array node
					loginRepJSON = jsonObj.getJSONArray(TAG_LOGINresp);

					// looping through All Contacts

					JSONObject c = loginRepJSON.getJSONObject(0);

					loginOk = c.getBoolean(TAG_ACCEPTED);
					userID = c.getString(TAG_ID);

				} catch (JSONException e) {
					e.printStackTrace();
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

			if (loginOk) {
				makeToast("Welcome!");
				SavePreferences("userID", userID);
				SavePreferences("isRememberMe", "" + isRememberMe);
				startActivity(new Intent(LoginActivity.this,
						SearchActivity.class));
				finish();
			} else {
				if (serverOK == 1) {
					switch (Integer.parseInt(userID)) {
					case -1:
						makeToast("Username or Password are incorrect.");
						userID = "";
						break;
					case -2:
						makeToast("Account Deactivated.");
						userID = "";
						break;

					default:
						break;
					}

				} else {
					new AlertDialog.Builder(LoginActivity.this)
							.setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			}

		}
	}

	private void SavePreferences(String key, String value) {
		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sh_Pref.edit();
		edit.putString(key, value);
		edit.commit();
	}

	private void makeToast(String text) {
		Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT).show();
	}
}
