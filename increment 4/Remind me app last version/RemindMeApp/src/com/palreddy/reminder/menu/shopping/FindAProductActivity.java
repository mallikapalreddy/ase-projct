package com.palreddy.reminder.menu.shopping;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.DataFeedManager;
import com.palreddy.reminder.helper.DownloadAndSetItemImage;
import com.palreddy.reminder.helper.ItemDetails;
import com.palreddy.reminder.helper.Validation;

public class FindAProductActivity extends Activity implements OnInitListener {

	static final String TAG = "SearchAndSelectItem";
	TextToSpeech tts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_aproduct);

		tts = new TextToSpeech(this, this);

		final EditText selectitemtext = (EditText) findViewById(R.id.selectitemtext);
		final ListView list = (ListView) findViewById(R.id.itemlist);
		Button searchButton = (Button) findViewById(R.id.search);

		selectitemtext.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					list.setVisibility(View.GONE);
			}
		});

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String itemToSearch = selectitemtext.getText().toString();

				if (!Validation.hasText(selectitemtext))
					return;

				itemToSearch = itemToSearch.replaceAll(" ", "+");
				new DownLoadItemList(list, FindAProductActivity.this)
						.execute(itemToSearch);
				list.setVisibility(View.VISIBLE);

				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				list.requestFocus();
			}
		});

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Intent i = new Intent(getApplicationContext(),
				// ProductDetails.class);
				// pass ItemDetails obj -- serialize
				ItemDetails itemDetails = (ItemDetails) list.getAdapter()
						.getItem(arg2);

				new AlertDialog.Builder(FindAProductActivity.this)
						.setTitle("Information:")
						.setMessage("" + itemDetails.getName())
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK", null).show();

				// Bundle bundle = new Bundle();
				// bundle.putSerializable("ItemDetails", itemDetails);
				// i.putExtras(bundle);
				// startActivity(i);
			}
		});
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			Log.e(TAG, "Shutting down tts");
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	private class DownLoadItemList extends
			AsyncTask<String, Void, ArrayList<ItemDetails>> {

		ProgressDialog pDialog;
		ListView listItems;

		public DownLoadItemList(ListView list, FindAProductActivity instance) {
			this.listItems = list;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// tts.speak("Please wait", TextToSpeech.QUEUE_FLUSH, null);
			// tts.setSpeechRate(0.5f);
			pDialog = new ProgressDialog(FindAProductActivity.this);
			pDialog.setMessage(Html.fromHtml("Please wait..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected ArrayList<ItemDetails> doInBackground(String... params) {
			return DataFeedManager.getInstance().iItemList(params[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<ItemDetails> listOfSearchedItems) {
			pDialog.cancel();
			listItems.setAdapter(new ItemListArrayAdapter(
					FindAProductActivity.this, listOfSearchedItems));
		}
	}

	public class ItemListArrayAdapter extends ArrayAdapter<ItemDetails> {
		private final Context context;
		private final ArrayList<ItemDetails> values;

		public ItemListArrayAdapter(Context context,
				ArrayList<ItemDetails> values) {
			super(context, R.layout.inflate_item_list_, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.inflate_item_list_,
					parent, false);
			TextView titleView = (TextView) rowView
					.findViewById(R.id.itemtitle);
			TextView priceView = (TextView) rowView.findViewById(R.id.price);
			ImageView itemImage = (ImageView) rowView
					.findViewById(R.id.itemimage);
			if (values.get(position).getName().equalsIgnoreCase("NOITEM")) {
				itemImage.setVisibility(View.GONE);
				priceView.setVisibility(View.GONE);
				titleView.setText("Item Not Found");
				titleView.setTextColor(Color.RED);
			} else {
				titleView.setText(values.get(position).getName());
				titleView.setTextColor(Color.BLACK);
				priceView.setText("$" + values.get(position).getPricing());
				new DownloadAndSetItemImage().execute(itemImage,
						values.get(position).getImageLocation());
			}
			return rowView;
		}
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.US);
		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}
}
