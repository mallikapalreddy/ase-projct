package com.remindme.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the halt database table.
 * 
 */
@Entity
@NamedQuery(name="Halt.findAll", query="SELECT h FROM Halt h")
public class Halt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idH;

	private String expectedArrivalTime;

	private String expectedDepartureTime;

	@Lob
	private String recorderVoiceH;

	private String station;

	//bi-directional many-to-one association to Journey
	@ManyToOne
	@JoinColumn(name="idJourney")
	private Journey journey;

	public Halt() {
	}

	public int getIdH() {
		return this.idH;
	}

	public void setIdH(int idH) {
		this.idH = idH;
	}

	public String getExpectedArrivalTime() {
		return this.expectedArrivalTime;
	}

	public void setExpectedArrivalTime(String expectedArrivalTime) {
		this.expectedArrivalTime = expectedArrivalTime;
	}

	public String getExpectedDepartureTime() {
		return this.expectedDepartureTime;
	}

	public void setExpectedDepartureTime(String expectedDepartureTime) {
		this.expectedDepartureTime = expectedDepartureTime;
	}

	public String getRecorderVoiceH() {
		return this.recorderVoiceH;
	}

	public void setRecorderVoiceH(String recorderVoiceH) {
		this.recorderVoiceH = recorderVoiceH;
	}

	public String getStation() {
		return this.station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public Journey getJourney() {
		return this.journey;
	}

	public void setJourney(Journey journey) {
		this.journey = journey;
	}

}