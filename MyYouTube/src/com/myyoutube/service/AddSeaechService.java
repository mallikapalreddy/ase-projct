package com.myyoutube.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.content.Intent;

import com.hackathon.myyoutube.helper.ServiceHandler;

public class AddSeaechService extends IntentService {

	private static String serverURL = "http://myyoutube-remindmeapp.rhcloud.com/myyoutube/androidapp";
	private static String addSearchURL = "/addsearch";

	public AddSeaechService() {
		super("AddSeaechService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		ServiceHandler sh = new ServiceHandler();
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

		urlParameters.add(new BasicNameValuePair("search", intent
				.getStringExtra("keySearch")));

		sh.makeServiceCall(serverURL + addSearchURL, ServiceHandler.POST,
				urlParameters);

	}

}
