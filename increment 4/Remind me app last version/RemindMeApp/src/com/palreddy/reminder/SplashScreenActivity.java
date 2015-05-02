package com.palreddy.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.MotionEvent;
import android.widget.TextView;

import com.palreddy.reminder.login.LoginActivity;

public class SplashScreenActivity extends ActionBarActivity {

	/** The _active. */
	protected boolean _active = true;

	/** The _splash time. */
	protected int _splashTime = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		getSupportActionBar().hide();

		TextView textViewSplash = (TextView) findViewById(R.id.text_view_splash);
		textViewSplash.setText(Html.fromHtml(String
				.format(getString(R.string.splash_String))));

		// thread for displaying the SplashScreen
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < _splashTime)) {
						sleep(100);
						if (_active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					// do nothing
				} finally {
					// once the 3s passed, we will start the next Activity
					finish();
					startActivity(new Intent().setClass(
							getApplicationContext(), LoginActivity.class));
				}
			}
		};
		splashTread.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			_active = false;
		}
		return true;
	}

}
