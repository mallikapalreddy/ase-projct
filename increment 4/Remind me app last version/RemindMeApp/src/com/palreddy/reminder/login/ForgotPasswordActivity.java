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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.ServiceHandler;
import com.palreddy.reminder.helper.Validation;
import com.palreddy.reminder.menu.MainActivity;

public class ForgotPasswordActivity extends ActionBarActivity implements
		OnItemSelectedListener, android.view.View.OnClickListener {

	private String[] questions = { "What is your pet’s name?",
			"What was your childhood nickname?",
			"What is your favorite movie?",
			"What was your favorite food as a child?",
			"Who is your childhood sports hero?" };

	private EditText username;
	private EditText answer;
	private EditText newPassword;
	private Spinner spinner;
	private Button submit;
	private TextView textViewSelect;

	private String selQuestion = "";

	private String usernameStr = "";

	static Context ctx;

	private int serverOK = 0;
	private int questionOK = 0;
	private int passwordOK = 0;
	private ProgressDialog pDialog;
	private String userID;
	private String questionNumber;
	private String questionAnswer;
	private String serverUsername;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "";
	private static String getQuestionInfoURL = "/login/getquestioninfo";
	private static String newPasswordURL = "/login/newpassword";

	JSONArray questionInfoResponse = null;
	private static final String TAG_QuestionInfoResponse = "QuestionInfoResponse";
	private static final String TAG_ID = "id";
	private static final String TAG_QUESTION_NUMBER = "questionNumber";
	private static final String TAG_QUESTION_ANSWER = "questionAnswer";
	private static final String TAG_USERNAME = "username";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		getSupportActionBar().hide();

		serverURL = loadPrefs(TAG_SERVER_URL);

		// username
		username = (EditText) findViewById(R.id.editTextUsername);

		// new Password
		newPassword = (EditText) findViewById(R.id.editTextNewPassword);

		// spinner
		spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(this);

		// answer
		answer = (EditText) findViewById(R.id.editTextAnswer);

		// TextView
		textViewSelect = (TextView) findViewById(R.id.TextViewSelect);

		// submit button
		submit = (Button) findViewById(R.id.btn_confirm_submit);
		submit.setOnClickListener(this);
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

	public String loadPrefs(String KEY) {

		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}

	private class getQuestionInfo extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ForgotPasswordActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("username", usernameStr));

			String result = sh.makeServiceCall(serverURL + getQuestionInfoURL,
					ServiceHandler.POST, urlParameters);

			if (result != null) {
				try {
					serverOK = 1;
					JSONObject jsonObj = new JSONObject(result);

					// Getting JSON Array node
					questionInfoResponse = jsonObj
							.getJSONArray(TAG_QuestionInfoResponse);

					// looping through All Contacts

					JSONObject c = questionInfoResponse.getJSONObject(0);

					serverUsername = c.getString(TAG_USERNAME);
					userID = c.getString(TAG_ID);
					if (usernameStr.equals(serverUsername)
							&& !userID.equals("-1") && !userID.equals("0")) {
						questionOK = 1;
						questionAnswer = c.getString(TAG_QUESTION_ANSWER);
						questionNumber = c.getString(TAG_QUESTION_NUMBER);
					} else {
						questionOK = 0;
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

			if (questionOK == 1) {
				answer.setVisibility(View.VISIBLE);
				newPassword.setVisibility(View.VISIBLE);
				textViewSelect.setVisibility(View.VISIBLE);
				submit.setText("Submit");
				ArrayAdapter<String> adapter_questions = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.simple_spinner_item,
						questions);
				adapter_questions
						.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter_questions);
			} else {
				if (serverOK == 1) {
					new AlertDialog.Builder(ForgotPasswordActivity.this)
							.setTitle("Message")
							.setMessage("Username is incorrect.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				} else {
					new AlertDialog.Builder(ForgotPasswordActivity.this)
							.setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			}

		}
	}

	private class newPassword extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ForgotPasswordActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			String pass = newPassword.getText().toString().trim();

			urlParameters.add(new BasicNameValuePair("password", SHA2(pass)));
			urlParameters.add(new BasicNameValuePair("idU", userID));

			String result = sh.makeServiceCall(serverURL + newPasswordURL,
					ServiceHandler.POST, urlParameters);

			Log.e("result >", "> " + result);
			if (result != null) {
				if (Boolean.parseBoolean(result))
					passwordOK = 1;
				else
					passwordOK = 0;
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

			if (passwordOK == 1) {
				new AlertDialog.Builder(ForgotPasswordActivity.this)
						.setTitle("Message")
						.setMessage("Password has been changed.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK", new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								SavePreferences("userID", userID);
								startActivity(new Intent(
										ForgotPasswordActivity.this,
										MainActivity.class));
								finish();
							}
						}).show();

			} else {
				if (serverOK == 1) {
					new AlertDialog.Builder(ForgotPasswordActivity.this)
							.setTitle("Message")
							.setMessage("Erreur, Can you please try again.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				} else {
					new AlertDialog.Builder(ForgotPasswordActivity.this)
							.setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm_submit:
			if ((submit.getText().toString()).equals("Confirm")) {
				usernameStr = username.getText().toString().trim();
				if (Validation.hasText(username))
					new getQuestionInfo().execute();
			}
			if ((submit.getText().toString()).equals("Submit")) {

				if (!(Validation.hasText(username)
						&& Validation.hasText(answer) && Validation
							.hasText(newPassword)))
					return;

				String userAnswer = answer.getText().toString().trim();
				String userQuestinNumber = getQuestionNumber(selQuestion);
				String userUsername = username.getText().toString().trim();

				if (usernameStr.equals(userUsername)
						&& userAnswer.equals(questionAnswer)
						&& userQuestinNumber.equals(questionNumber)) {

					new newPassword().execute();

				} else {
					new AlertDialog.Builder(ForgotPasswordActivity.this)
							.setTitle("Message")
							.setMessage(
									"Username, Question or Answer are incorrect.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}
			}
			break;

		default:
			break;
		}

	}

	private String getQuestionNumber(String selQuestion2) {

		for (int i = 0; i < questions.length; i++) {
			if (selQuestion.equals(questions[i])) {
				i++;
				return "" + i;
			}
		}
		return "" + 0;
	}

	private void SavePreferences(String key, String value) {
		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sh_Pref.edit();
		edit.putString(key, value);
		edit.commit();
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
