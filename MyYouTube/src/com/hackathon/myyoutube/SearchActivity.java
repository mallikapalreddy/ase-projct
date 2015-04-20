package com.hackathon.myyoutube;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hackathon.myyoutube.helper.VideoItem;
import com.hackathon.myyoutube.helper.YoutubeConnector;
import com.myyoutube.service.AddSeaechService;
import com.squareup.picasso.Picasso;

public class SearchActivity extends Activity {

	private EditText searchInput;
	private ListView videosFound;

	private Handler handler;
	String keySearch;

	private List<VideoItem> searchResults;

	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "http://myyoutube-remindmeapp.rhcloud.com/myyoutube/androidapp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		SavePreferences(TAG_SERVER_URL, serverURL);

		((Button) findViewById(R.id.btn_dash))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						startActivity(new Intent(getApplicationContext(),
								DashBoardActivity.class));
					}
				});

		((TextView) findViewById(R.id.text_logout))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						new AlertDialog.Builder(SearchActivity.this)
								.setTitle("Confirm logOff ...")
								.setMessage("Are you sure you want to LogOff?")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setPositiveButton("YES",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												SavePreferences("isRememberMe",
														"" + false);
												finish();
											}
										})
								.setNegativeButton("NO",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
											}
										}).show();
					}
				});

		searchInput = (EditText) findViewById(R.id.search_input);
		videosFound = (ListView) findViewById(R.id.videos_found);

		handler = new Handler();

		searchInput
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							keySearch = v.getText().toString().trim();
							searchOnYoutube(keySearch);
							return false;
						}
						return true;
					}
				});
		//addClickListener();
	}

	private void searchOnYoutube(final String keywords) {
		System.err.println("searchOnYoutube >>> " + keywords);
		new Thread() {
			public void run() {
				YoutubeConnector yc = new YoutubeConnector(
						getApplicationContext());
				searchResults = yc.search(keywords);
				handler.post(new Runnable() {
					public void run() {
						updateVideosFound();
					}
				});
			}
		}.start();
	}

	private void updateVideosFound() {
		System.err.println("updateVideosFound");
		ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(
				getApplicationContext(), R.layout.video_item, searchResults) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.video_item, parent, false);
				}
				ImageView thumbnail = (ImageView) convertView
						.findViewById(R.id.video_thumbnail);
				TextView title = (TextView) convertView
						.findViewById(R.id.video_title);
				TextView description = (TextView) convertView
						.findViewById(R.id.video_description);

				VideoItem searchResult = searchResults.get(position);

				Picasso.with(getApplicationContext())
						.load(searchResult.getThumbnailURL()).into(thumbnail);
				title.setText(searchResult.getTitle());
				description.setText(searchResult.getDescription());
				return convertView;
			}
		};

		videosFound.setAdapter(adapter);
//		Intent in = new Intent(getApplicationContext(), AddSeaechService.class);
//		in.putExtra("keySearch", keySearch);
//		startService(in);
	}

	private void addClickListener() {
		videosFound
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> av, View v, int pos,
							long id) {
//						Intent intent = new Intent(getApplicationContext(),
//								PlayerActivity.class);
//						intent.putExtra("VIDEO_ID", searchResults.get(pos)
//								.getId());
//						startActivity(intent);
					}

				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			new AlertDialog.Builder(SearchActivity.this)
					.setTitle("Confirm exit ...")
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

	private void SavePreferences(String key, String value) {
		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sh_Pref.edit();
		edit.putString(key, value);
		edit.commit();
	}
}
