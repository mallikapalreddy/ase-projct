package com.remindme.response;

public class HaltListResponse {

	private int idH;
	private int idJ;
	private String expectedArrivalTime;
	private String expectedDepartureTime;
	private String recorderVoiceH;
	private String station;
	
	
	public int getIdH() {
		return idH;
	}
	public void setIdH(int idH) {
		this.idH = idH;
	}
	public int getIdJ() {
		return idJ;
	}
	public void setIdJ(int idJ) {
		this.idJ = idJ;
	}
	public String getExpectedArrivalTime() {
		return expectedArrivalTime;
	}
	public void setExpectedArrivalTime(String expectedArrivalTime) {
		this.expectedArrivalTime = expectedArrivalTime;
	}
	public String getExpectedDepartureTime() {
		return expectedDepartureTime;
	}
	public void setExpectedDepartureTime(String expectedDepartureTime) {
		this.expectedDepartureTime = expectedDepartureTime;
	}
	public String getRecorderVoiceH() {
		return recorderVoiceH;
	}
	public void setRecorderVoiceH(String recorderVoiceH) {
		this.recorderVoiceH = recorderVoiceH;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	
	@Override
	public String toString() {
		return " { " 
				+ "\"idH\": \"" + idH + "\", "
				+ "\"idJ\": \"" + idJ + "\", "
				+ "\"expectedArrivalTime\": \"" + expectedArrivalTime + "\", "
				+ "\"expectedDepartureTime\": \"" + expectedDepartureTime + "\", "
				+ "\"recorderVoiceH\": \"" + recorderVoiceH + "\", "
				+ "\"station\": \"" + station + "\""
				+ " }";

	}
}
