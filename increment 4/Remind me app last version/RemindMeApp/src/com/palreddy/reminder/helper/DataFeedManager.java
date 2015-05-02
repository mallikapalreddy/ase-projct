package com.palreddy.reminder.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

public class DataFeedManager {

	private static final String TAG = "DataFeedManager";

	private static DataFeedManager parser;

	//private String ITEM_SEARCH_BASE_URL = "http://www.supermarketapi.com/api.asmx/COMMERCIAL_SearchByProductName?APIKEY=a785cac0f3&ItemName=";

	private final String BASE_URL = "http://api.walmartlabs.com/v1/search?apiKey=a4fp8k5c23j8apczj5se2yzg&query=";
	
	private DataFeedManager() {

	}

	public static DataFeedManager getInstance() {
		if (parser == null)
			parser = new DataFeedManager();

		return parser;
	}

	public Document getDocumentFromUrlData(String url) {

		Document doc = null;
		try {
			String dataRead = getDataFromURL(url);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(dataRead));
			doc = db.parse(is);
			doc.getDocumentElement().normalize();

		} catch (MalformedURLException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	private String getDataFromURL(String url) {

		String dataRead = "";// = new StringBuilder();
		try {
		
			URL urlToRead = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) (urlToRead).openConnection();

			connection.setRequestMethod("GET");
			connection.connect();
			InputStream inputstream = connection.getInputStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(inputstream));

			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				dataRead += inputLine;
			}

			in.close();
	
		} catch (MalformedURLException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataRead;
	}

	public ArrayList<ItemDetails> iItemList(String itemToSearch) {
		return getItemListFromUrl(itemToSearch);
	}

	private ArrayList<ItemDetails> getItemListFromUrl(String itemToSearch) {
	
		ArrayList<ItemDetails> itemList = new ArrayList<ItemDetails>();
		
		String data = getDataFromURL(BASE_URL+itemToSearch);
		try {
			JSONObject obj = new JSONObject(data);
			Log.d(TAG, "Number of items: " + obj.getInt("numItems"));
			Log.d(TAG, "Item searched: " + obj.getString("query"));
			
			if(obj.getInt("numItems") > 0)
			{
				JSONArray itemJSONArray = obj.getJSONArray("items");
				Log.d(TAG, "Number of items in json array: " + itemJSONArray.length());
				for(int i = 0; i < itemJSONArray.length(); i++)
				{
					ItemDetails itemDetails = new ItemDetails();

					JSONObject object = itemJSONArray.getJSONObject(i);
//					Log.d(TAG, "Item name: " + object.getString("name"));
//					Log.d(TAG, "Item price: " + object.getDouble("salePrice"));
//					Log.d(TAG, "Item thumbnailImage: " + object.getString("thumbnailImage"));
//					Log.d(TAG, "Item shortDescription: " + object.getString("shortDescription"));
					itemDetails.setDescription(object.has("shortDescription") ? object.getString("shortDescription")
							: "No Description available");
					itemDetails.setPricing(object.getDouble("salePrice"));
					itemDetails.setImageLocation(object.getString("thumbnailImage"));
					itemDetails.setName(object.getString("name"));
					itemDetails.setUrl(object.getString("productUrl"));
					
					itemList.add(itemDetails);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return itemList;
	}
	
	/*private ArrayList<ItemDetails> getItemListFromUrl(String itemToSearch) {
		ArrayList<ItemDetails> itemList = new ArrayList<ItemDetails>();

		Document doc = getDocumentFromUrlData(ITEM_SEARCH_BASE_URL
				+ itemToSearch);

		if (doc == null)
			return itemList;

		NodeList nList = doc.getElementsByTagName("Product_Commercial");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			NodeList nl = nNode.getChildNodes();

			ItemDetails itemDetails = new ItemDetails();

			for (int i = 0; i < nl.getLength(); i++) {

				if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Node eElement = (Node) nl.item(i);
					if (eElement.getNodeName().equalsIgnoreCase("Itemname")) {
						itemDetails.setName(eElement.getTextContent());
					} else if (eElement.getNodeName()
							.equalsIgnoreCase("ItemID"))
						itemDetails.setID(eElement.getTextContent());

					else if (eElement.getNodeName().equalsIgnoreCase(
							"ItemDescription"))
						itemDetails.setDescription(eElement.getTextContent());

					else if (eElement.getNodeName().equalsIgnoreCase(
							"ItemCategory"))
						itemDetails.setCategory(eElement.getTextContent());

					else if (eElement.getNodeName().equalsIgnoreCase(
							"ItemImage"))
						itemDetails.setImageLocation(eElement.getTextContent());

					else if (eElement.getNodeName().equalsIgnoreCase("Pricing")
							&& eElement.getTextContent() != "NOPRICE") {
						try {
							itemDetails.setPricing(Double.parseDouble(eElement
									.getTextContent()));
						} catch (Exception e) {
							itemDetails.setPricing(0.0);
						}
					}

					else if (eElement.getNodeName().equalsIgnoreCase(
							"AisleNumber"))
						itemDetails.setAisleNumber(eElement.getTextContent());
				}
			}
			itemList.add(itemDetails);
		}

		return itemList;
	}*/
}
