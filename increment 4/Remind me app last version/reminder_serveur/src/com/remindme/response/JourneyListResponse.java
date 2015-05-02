package com.remindme.response;

public class JourneyListResponse {

	private int idJ;
	private String date;
	private String destination;
	private String expectedArrivalTime;
	private String planName;
	private String recorderVoice;
	private String source;
	private int haltsNumber;

	public int getIdJ() {
		return idJ;
	}

	public void setIdJ(int idJ) {
		this.idJ = idJ;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getExpectedArrivalTime() {
		return expectedArrivalTime;
	}

	public void setExpectedArrivalTime(String expectedArrivalTime) {
		this.expectedArrivalTime = expectedArrivalTime;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getRecorderVoice() {
		return recorderVoice;
	}

	public void setRecorderVoice(String recorderVoice) {
		this.recorderVoice = recorderVoice;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getHaltsNumber() {
		return haltsNumber;
	}

	public void setHaltsNumber(int haltsNumber) {
		this.haltsNumber = haltsNumber;
	}

	@Override
	public String toString() {
		return " { " + "\"idJ\": \"" + idJ + "\", " + "\"date\": \"" + date
				+ "\", " + "\"destination\": \"" + destination + "\", "
				+ "\"expectedArrivalTime\": \"" + expectedArrivalTime + "\", "
				+ "\"planName\": \"" + planName + "\", "
				+ "\"recorderVoice\": \"" + recorderVoice + "\", "
				+ "\"source\": \"" + source + "\", " + "\"haltsNumber\": \""
				+ haltsNumber + "\"" + " }";

	}

}
