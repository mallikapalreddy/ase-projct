package com.myyoutube.response;

public class UserResponse {

	private String email;
	private String freqlogon;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFreqlogon() {
		return freqlogon;
	}

	public void setFreqlogon(String freqlogon) {
		this.freqlogon = freqlogon;
	}

	@Override
	public String toString() {
		return " { \"email\": \"" + email + "\", " + "\"freqlogon\": \""
				+ freqlogon + "\" }";

	}

}
