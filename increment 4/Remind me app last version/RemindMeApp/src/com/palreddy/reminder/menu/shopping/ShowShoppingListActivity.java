package com.palreddy.reminder.menu.shopping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.palreddy.reminder.R;
import com.palreddy.reminder.helper.ServiceHandler;
import com.palreddy.reminder.notifications.ShoppingNotifications;

public class ShowShoppingListActivity extends ActionBarActivity {

	private Button addButton;
	private ListView shoppingItemList;

	static Context ctx;

	private int serverOK = 0;
	private int deleteOK = 0;
	private String idItemToDelete = "";

	private ProgressDialog pDialog;
	private static final String TAG_SERVER_URL = "serverURL";
	private static String serverURL = "";
	private static String getShoppingListURL = "/shopping/getshoppinglist";
	private static String deleteShopURL = "/shopping/deleteshop";

	private JSONArray shoppinglist = null;
	private ArrayList<HashMap<String, String>> shoppingList;
	private ListAdapter adapterListshoppinglist;
	private String jsonStr;

	private HashMap<String, String> mapSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_shopping_item);
		getSupportActionBar().hide();

		serverURL = loadPrefs(TAG_SERVER_URL);
		ctx = getApplicationContext();

		shoppingList = new ArrayList<HashMap<String, String>>();

		// button
		addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ctx, AddShoppingItemActivity.class);
				intent.putExtra("from", "add");
				startActivity(intent);
			}
		});

		// List View

		shoppingItemList = (ListView) findViewById(R.id.shppingItemList);
		shoppingItemList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {

				mapSelected = (HashMap<String, String>) shoppingItemList
						.getItemAtPosition(position);

				AlertDialog.Builder alertadd = new AlertDialog.Builder(
						ShowShoppingListActivity.this);
				LayoutInflater factory = LayoutInflater
						.from(ShowShoppingListActivity.this);
				final View view = factory.inflate(
						R.layout.showing_read_update_delete_dialog, null);
				alertadd.setView(view);
				// Setting Positive "Delete" Button
				alertadd.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								new AlertDialog.Builder(
										ShowShoppingListActivity.this)
										.setTitle("Confirmation ...")
										.setMessage(
												"Are you sure you want to delete this item ?")
										.setIcon(android.R.drawable.ic_delete)
										.setPositiveButton(
												"YES",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														idItemToDelete = mapSelected
																.get("idS");
														new DeleteShop()
																.execute();
													}
												})
										.setNegativeButton(
												"NO",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog.cancel();
													}
												}).show();

							}
						});
				// Setting Neutral Update Button
				alertadd.setNeutralButton("Update",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(ctx,
										AddShoppingItemActivity.class);
								intent.putExtra("from", "update");
								intent.putExtra("idS", mapSelected.get("idS"));
								intent.putExtra("items",
										mapSelected.get("items"));
								intent.putExtra("address",
										mapSelected.get("address"));
								intent.putExtra("estimatedAmt",
										mapSelected.get("estimatedAmt"));
								intent.putExtra("dateShopping",
										mapSelected.get("dateShopping"));
								intent.putExtra("title",
										mapSelected.get("title"));
								startActivity(intent);
							}
						});
				// Setting Negative "Read" Button
				alertadd.setNegativeButton("Read",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String[] address = (mapSelected.get("address"))
										.split(" # ");
								new AlertDialog.Builder(
										ShowShoppingListActivity.this)
										.setTitle("Message")
										.setMessage(
												"Item Information:  \n * Items: "
														+ mapSelected
																.get("items")
														+ "\n * Time: "
														+ mapSelected
																.get("title")
														+ "\n * Shopping Date: "
														+ mapSelected
																.get("dateShopping")
														+ "\n * Address: "
														+ address[0]
														+ "\n * Estimated price: "
														+ mapSelected
																.get("estimatedAmt"))
										.setIcon(
												android.R.drawable.ic_dialog_info)
										.setNegativeButton("Ok", null).show();

							}
						});

				alertadd.show();
			}
		});

	}

	public String loadPrefs(String KEY) {

		SharedPreferences sh_Pref = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		return sh_Pref.getString(KEY, null);
	}

	private void SavePreferences(String key, String value) {
		SharedPreferences sh_Pref = ctx.getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sh_Pref.edit();
		edit.putString(key, value);
		edit.commit();
	}

	@Override
	protected void onResume() {
		shoppingList.clear();
		new GetShoppingItems().execute();
		super.onResume();
	}

	private class GetShoppingItems extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ShowShoppingListActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters
					.add(new BasicNameValuePair("idU", loadPrefs("userID")));
			jsonStr = sh.makeServiceCall(serverURL + getShoppingListURL,
					ServiceHandler.POST, urlParameters);

			if (jsonStr != null) {
				serverOK = 1;
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// Getting JSON Array node
					shoppinglist = jsonObj.getJSONArray("listShoppings");

					// looping through All Shoppings
					for (int i = 0; i < shoppinglist.length(); i++) {
						JSONObject c = shoppinglist.getJSONObject(i);

						String idS = c.getString("idS");
						String items = c.getString("items");
						String address = c.getString("address");
						String estimatedAmt = c.getString("estimatedAmt");
						String dateShopping = c.getString("dateShopping");
						String title = c.getString("title");

						// tmp hashmap for single contact
						HashMap<String, String> shop = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						shop.put("idS", idS);
						shop.put("items", items);
						shop.put("address", address);
						shop.put("estimatedAmt", estimatedAmt);
						shop.put("dateShopping", dateShopping);
						shop.put("title", title);

						shoppingList.add(shop);
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

			if (serverOK == 1) {
				Log.d("srverOK : ", ">  OK ");

				adapterListshoppinglist = new SimpleAdapter(
						ShowShoppingListActivity.this, shoppingList,
						R.layout.show_shop_listview, new String[] { "items",
								"title", "dateShopping" }, new int[] {
								R.id.title, R.id.items, R.id.date });

				shoppingItemList.setAdapter(adapterListshoppinglist);
				String runShopService = loadPrefs("runShopService");
				if (runShopService != null
						&& Boolean.parseBoolean(runShopService)
						&& !shoppingList.isEmpty()) {
					SavePreferences("runShopService", "false");
					Intent shoppingServiceIntent = new Intent(
							getApplicationContext(),
							ShoppingNotifications.class);
					shoppingServiceIntent.putExtra("shoppinglist", jsonStr);
					startService(shoppingServiceIntent);
				}
			} else {
				new AlertDialog.Builder(ShowShoppingListActivity.this)
						.setTitle("Message")
						.setMessage("Connection problem with the server.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK", null).show();
			}
		}

	}

	private class DeleteShop extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ShowShoppingListActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {

			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

			urlParameters.add(new BasicNameValuePair("idS", idItemToDelete));

			String result = sh.makeServiceCall(serverURL + deleteShopURL,
					ServiceHandler.POST, urlParameters);

			Log.e("result >", "> " + result);
			if (result != null) {
				if (Boolean.parseBoolean(result))
					deleteOK = 1;
				else
					deleteOK = 0;
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

			if (deleteOK == 1) {
				idItemToDelete = "";

				new AlertDialog.Builder(ShowShoppingListActivity.this)
						.setTitle("Message")
						.setMessage("Shopping item has been deleted.")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										shoppingList.clear();
										new GetShoppingItems().execute();
									}
								}).show();

			} else {
				if (serverOK == 1) {
					new AlertDialog.Builder(ShowShoppingListActivity.this)
							.setTitle("Message")
							.setMessage("Erreur, Can you please try again.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				} else {
					new AlertDialog.Builder(ShowShoppingListActivity.this)
							.setTitle("Message")
							.setMessage("Connection problem with the server.")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setNeutralButton("OK", null).show();
				}

			}

		}
	}

}
