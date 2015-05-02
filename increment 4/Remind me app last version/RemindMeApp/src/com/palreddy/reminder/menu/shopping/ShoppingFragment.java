package com.palreddy.reminder.menu.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.palreddy.reminder.R;

public class ShoppingFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_shopping, container, false);
		TextView tv = (TextView) v.findViewById(R.id.textViewSearch);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity().getApplicationContext(),
						FindAProductActivity.class);
				startActivity(i);
			}
		});

		TextView tv2 = (TextView) v.findViewById(R.id.textViewDirection);
		tv2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity().getApplicationContext(),
						SearchAStoreActivity.class);
				i.putExtra("goTo", "FindAProductActivity");
				startActivity(i);
			}
		});

		TextView tv3 = (TextView) v.findViewById(R.id.textViewAdd);
		tv3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity().getApplicationContext(),
						ShowShoppingListActivity.class);
				startActivity(i);
			}
		});
		return v;
	}

}
