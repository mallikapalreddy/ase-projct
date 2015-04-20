package com.hackathon.myyoutube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;

import com.hackathon.myyoutube.login.LoginActivity;

public class SplashScreenActivity extends Activity {

	/** The _active. */
	protected boolean _active = true;

	/** The _splash time. */
	protected int _splashTime = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

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
					String isRememberMeStr = loadPrefs("isRememberMe");
					boolean isRememberMe = false;
					if (isRememberMeStr != null)
						isRememberMe = Boolean.parseBoolean(isRememberMeStr);
					if (isRememberMe)
						startActivity(new Intent().setClass(
								getApplicationContext(), SearchActivity.class));
					else
						startActivity(new Intent().setClass(
								getApplicationContext(), LoginActivity.class));
				}
			}
		};
		splashTread.start();
	}

	public String loadPrefs(String KEY) {

		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			_active = false;
		}
		return true;
	}

}
