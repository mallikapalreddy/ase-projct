package com.myyoutube.response;

public class SearchResp {

	private String number;
	private String subject;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return " { \"subject\": \"" + subject + "\", " + "\"number\": \""
				+ number + "\" }";

	}

}
