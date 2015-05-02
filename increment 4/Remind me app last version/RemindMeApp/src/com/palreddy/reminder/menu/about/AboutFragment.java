package com.palreddy.reminder.menu.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.palreddy.reminder.R;

public class AboutFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_about, container, false);
		// TextView tv = (TextView) v.findViewById(R.id.text);
		// tv.setText(this.getTag() + " Content");
		return v;
	}

	@Override
	public void onResume() {
		Log.e("onResume", "AboutFragment");
		super.onResume();
	}
}
