package com.remindme.response;

public class ShoppingListResponse {

	private int idS;
	private String address;
	private String dateEntry;
	private String dateShopping;
	private String estimatedAmt;
	private String items;
	private String title;

	public int getIdS() {
		return idS;
	}

	public void setIdS(int idS) {
		this.idS = idS;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDateEntry() {
		return dateEntry;
	}

	public void setDateEntry(String dateEntry) {
		this.dateEntry = dateEntry;
	}

	public String getDateShopping() {
		return dateShopping;
	}

	public void setDateShopping(String dateShopping) {
		this.dateShopping = dateShopping;
	}

	public String getEstimatedAmt() {
		return estimatedAmt;
	}

	public void setEstimatedAmt(String estimatedAmt) {
		this.estimatedAmt = estimatedAmt;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return " { \"idS\": \"" + idS + "\", " + "\"address\": \"" + address
				+ "\", " + "\"dateEntry\": \"" + dateEntry + "\", "
				+ "\"dateShopping\": \"" + dateShopping + "\", "
				+ "\"estimatedAmt\": \"" + estimatedAmt + "\", "
				+ "\"items\": \"" + items + "\", " + "\"title\": \"" + title
				+ "\" }";

	}

}
