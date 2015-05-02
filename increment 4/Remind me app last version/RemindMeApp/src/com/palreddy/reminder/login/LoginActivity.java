package com.palreddy.reminder.login;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.ServiceHandler;
import com.palreddy.reminder.helper.Validation;
import com.palreddy.reminder.menu.MainActivity;

public class LoginActivity extends ActionBarActivity {

	private TextView textViewSignUp;
	private TextView textForgotPassword;
	private EditText username;
	private EditText password;

	private String loginUser = "";
	private String passUser = "";

	static Context ctx;

	private int serverOK = 0;
	private ProgressDialog pDialog;
	private boolean loginOk = false;
	private String userID;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "http://remindmeapp-remindmeapp.rhcloud.com/remindme/androidapp";
	private static String authenticationURL = "/login/authentication";

	JSONArray loginRepJSON = null;
	private static final String TAG_LOGINresp = "LoginResponse";
	private static final String TAG_ID = "id";
	private static final String TAG_ACCEPTED = "accepted";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getSupportActionBar().hide();
		SavePreferences(TAG_SERVER_URL, serverURL);

		username = (EditText) findViewById(R.id.editTextUsername);
		password = (EditText) findViewById(R.id.editTextPassword);

		((Button) findViewById(R.id.btn_login))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loginUser = username.getText().toString().trim();
						passUser = password.getText().toString().trim();

						if (loginUser.length() != 0 && passUser.length() != 0)
							new login().execute();
						else {
							Validation.hasText(username);
							Validation.hasText(password);
						}
					}
				});

		textViewSignUp = (TextView) findViewById(R.id.text_sign_up);
		textViewSignUp.setText(Html.fromHtml(String
				.format(getString(R.string.sign_up_String))));
		textViewSignUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						SignUpActivity.class));
				finish();
			}
		});

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

			urlParameters.add(new BasicNameValuePair("username", loginUser));
			urlParameters.add(new BasicNameValuePair("password", SHA2(passUser)));

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
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				finish();
			} else {
				if (serverOK == 1) {
					makeToast("Username or Password are incorrect.");
					userID = "";
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

	private String SHA2(String text) {
		String result = "";
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(text.getBytes("UTF-8"));
			result = byteArrayToHexString(hash);
		} catch (NoSuchAlgorithmException e) {
			result = e.getMessage();
		} catch (UnsupportedEncodingException e) {
			result = e.getMessage();
		}
		return result;

	}

	@SuppressLint("DefaultLocale")
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}
}
