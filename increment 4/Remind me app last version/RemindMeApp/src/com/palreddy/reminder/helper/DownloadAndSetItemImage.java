package com.palreddy.reminder.helper;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadAndSetItemImage extends AsyncTask<Object, Void, Void> {

	Bitmap iconBmp;
	ImageView	iconHolder;
	
	@Override
	protected Void doInBackground(Object... params) {
		try {
			iconHolder = (ImageView)params[0];
			URL iconURL = new URL((String)params[1]);
			iconBmp = BitmapFactory.decodeStream(iconURL.openConnection().getInputStream());
		} catch(Exception ex) {
			ex.printStackTrace();
			iconBmp = null;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		try {
			if(iconBmp != null && iconHolder != null) {
				iconHolder.setImageBitmap(iconBmp);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}