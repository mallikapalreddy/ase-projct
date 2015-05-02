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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.ServiceHandler;
import com.palreddy.reminder.helper.Validation;
import com.palreddy.reminder.menu.MainActivity;

public class SignUpActivity extends ActionBarActivity implements
		OnItemSelectedListener {

	private EditText signupUsername;
	private EditText signupPassword;
	private EditText signupConfirmPassword;
	private EditText signupPhone;
	private EditText signupEmail;
	private EditText signupAnswer;
	private Spinner spinner;
	private Button submitBtn;

	private String username = "";
	private String password = "";
	private String confirmPassword = "";
	private String phone = "";
	private String email = "";
	private String answer = "";
	private String numberSelQuestion = "";

	private String[] questions = { "What is your pet’s name?",
			"What was your childhood nickname?",
			"What is your favorite movie?",
			"What was your favorite food as a child?",
			"Who is your childhood sports hero?" };

	private String selQuestion = "";

	static Context ctx;

	private int serverOK = 0;
	private int signupOK = 0;
	private String userID = "";
	private ProgressDialog pDialog;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "";
	private static String signUpURL = "/login/signup";

	JSONArray loginRepJSON = null;
	private static final String TAG_SIGNUP_RESPONSE = "SignUpResponse";
	private static final String TAG_ID = "id";
	private static final String TAG_IS_SIGN_UP = "isSignUp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		getSupportActionBar().hide();

		serverURL = loadPrefs(TAG_SERVER_URL);
		ctx = getApplicationContext();

		// Edit Text
		signupUsername = (EditText) findViewById(R.id.editTextUsername);
		signupPassword = (EditText) findViewById(R.id.editTextPassword);
		signupConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
		signupPhone = (EditText) findViewById(R.id.editTextPhonNumber);
		signupEmail = (EditText) findViewById(R.id.editTextEmail);
		signupAnswer = (EditText) findViewById(R.id.editTextAnswer);

		// spinner
		spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> adapter_questions = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_item, questions);
		adapter_questions
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter_questions);
		spinner.setOnItemSelectedListener(this);

		// submit button
		submitBtn = (Button) findViewById(R.id.btn_sign_up);
		submitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (Validation.hasText(signupUsername)
						&& Validation.hasText(signupPassword)
						&& Validation.hasText(signupConfirmPassword)
						&& Validation.hasText(signupPhone)
						&& Validation.isEmailAddress(signupEmail, true)
						&& Validation.hasText(signupAnswer)) {

					username = signupUsername.getText().toString().trim();
					password = signupPassword.getText().toString().trim();
					confirmPassword = signupConfirmPassword.getText()
							.toString().trim();
					phone = signupPhone.getText().toString().trim();
					email = signupEmail.getText().toString().trim();
					answer = signupAnswer.getText().toString().trim();
					numberSelQuestion = getQuestionNumber(selQuestion);

					if (password.equals(confirmPassword)) {
						new signUp().execute();
					} else {
						new AlertDialog.Builder(SignUpActivity.this)
								.setTitle("Message")
								.setMessage("Verify your Password please.")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setNeutralButton("OK", null).show();
					}
				}
			}
		});

	}

	public String loadPrefs(String KEY) {

		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}

	private void SavePreferences(String key, String value) {
		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sh_Pref.edit();
		edit.putString(key, value);
		edit.commit();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		spinner.setSelection(position);
		selQuestion = (String) spinner.getSelectedItem();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private String getQuestionNumber(String selQuestion) {

		for (int i = 0; i < questions.length; i++) {
			if (selQuestion.equals(questions[i])) {
				i++;
				return "" + i;
			}
		}
		return "" + 0;
	}

	private class signUp extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(SignUpActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters
					.add(new BasicNameValuePair("password", SHA2(password)));
			urlParameters.add(new BasicNameValuePair("phone", phone));
			urlParameters.add(new BasicNameValuePair("answerQuestion", answer));
			urlParameters.add(new BasicNameValuePair("email", email));
			urlParameters.add(new BasicNameValuePair("questionNumber",
					numberSelQuestion));

			String result = sh.makeServiceCall(serverURL + signUpURL,
					ServiceHandler.POST, urlParameters);

			Log.e("result >", "> " + result);
			if (result != null) {
				try {
					serverOK = 1;
					JSONObject jsonObj = new JSONObject(result);

					// Getting JSON Array node
					loginRepJSON = jsonObj.getJSONArray(TAG_SIGNUP_RESPONSE);

					// looping through All Contacts

					JSONObject c = loginRepJSON.getJSONObject(0);

					if ((c.getString(TAG_IS_SIGN_UP)).equals("true")) {
						signupOK = 1;
						userID = c.getString(TAG_ID);
					} else {
						signupOK = Integer.parseInt(c.getString(TAG_ID));
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

				switch (signupOK) {
				case 1:
					SavePreferences("userID", userID);
					startActivity(new Intent(SignUpActivity.this,
							MainActivity.class));
					finish();

					break;

				case -1:
					new AlertDialog.Builder(SignUpActivity.this)
							.setTitle("Message")
							.setMessage("Can you please change the username.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
					break;

				case -2:
					new AlertDialog.Builder(SignUpActivity.this)
							.setTitle("Message")
							.setMessage(
									"User Can not be added, try again please, or contact the admin.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();

					break;

				default:
					break;
				}
			} else {
				new AlertDialog.Builder(SignUpActivity.this)
						.setTitle("Message")
						.setMessage("Connection problem with the server.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK", null).show();
			}

		}
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
