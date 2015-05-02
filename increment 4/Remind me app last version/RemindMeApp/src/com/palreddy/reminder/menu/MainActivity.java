package com.palreddy.reminder.menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;

import com.palreddy.reminder.R;
import com.palreddy.reminder.menu.about.AboutFragment;
import com.palreddy.reminder.menu.journey.JourneyFragment;
import com.palreddy.reminder.menu.shopping.ShoppingFragment;

public class MainActivity extends FragmentActivity {
	private FragmentTabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(),
				android.R.id.tabcontent);

		mTabHost.addTab(
				mTabHost.newTabSpec("shopping").setIndicator("Shopping", null),
				ShoppingFragment.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec("journey").setIndicator("Journey", null),
				JourneyFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("about")
				.setIndicator("About", null), AboutFragment.class, null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("Confirm logout ...")
					.setMessage("Are you sure you want to exit?")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("YES",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							})
					.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
