package com.remindme.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the journey database table.
 * 
 */
@Entity
@NamedQuery(name="Journey.findAll", query="SELECT j FROM Journey j")
public class Journey implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idJ;

	private String date;

	private String destination;

	private String expectedArrivalTime;

	private String planName;

	@Lob
	private String recorderVoice;

	private String source;

	//bi-directional many-to-one association to Halt
	@OneToMany(mappedBy="journey")
	private List<Halt> halts;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="idUser")
	private User user;

	public Journey() {
	}

	public int getIdJ() {
		return this.idJ;
	}

	public void setIdJ(int idJ) {
		this.idJ = idJ;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDestination() {
		return this.destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getExpectedArrivalTime() {
		return this.expectedArrivalTime;
	}

	public void setExpectedArrivalTime(String expectedArrivalTime) {
		this.expectedArrivalTime = expectedArrivalTime;
	}

	public String getPlanName() {
		return this.planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getRecorderVoice() {
		return this.recorderVoice;
	}

	public void setRecorderVoice(String recorderVoice) {
		this.recorderVoice = recorderVoice;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<Halt> getHalts() {
		return this.halts;
	}

	public void setHalts(List<Halt> halts) {
		this.halts = halts;
	}

	public Halt addHalt(Halt halt) {
		getHalts().add(halt);
		halt.setJourney(this);

		return halt;
	}

	public Halt removeHalt(Halt halt) {
		getHalts().remove(halt);
		halt.setJourney(null);

		return halt;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}