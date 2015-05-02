package com.remindme.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idU;

	private String answerQuestion;

	private String email;

	private String passWord;

	private String phone;

	private String questionNumber;

	private String userName;

	// bi-directional many-to-one association to Shopping
	@OneToMany(mappedBy = "user")
	private List<Shopping> shoppings;

	// bi-directional many-to-one association to Shopping
	@OneToMany(mappedBy = "user")
	private List<Journey> journeys;

	public User() {
	}

	public int getIdU() {
		return this.idU;
	}

	public void setIdU(int idU) {
		this.idU = idU;
	}

	public String getAnswerQuestion() {
		return this.answerQuestion;
	}

	public void setAnswerQuestion(String answerQuestion) {
		this.answerQuestion = answerQuestion;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassWord() {
		return this.passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getQuestionNumber() {
		return this.questionNumber;
	}

	public void setQuestionNumber(String questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<Shopping> getShoppings() {
		return this.shoppings;
	}

	public void setShoppings(List<Shopping> shoppings) {
		this.shoppings = shoppings;
	}

	public Shopping addShopping(Shopping shopping) {
		getShoppings().add(shopping);
		shopping.setUser(this);

		return shopping;
	}

	public Shopping removeShopping(Shopping shopping) {
		getShoppings().remove(shopping);
		shopping.setUser(null);

		return shopping;
	}

	public List<Journey> getJourneys() {
		return journeys;
	}

	public void setJourneys(List<Journey> journeys) {
		this.journeys = journeys;
	}

	public Journey addJourney(Journey journey) {
		getJourneys().add(journey);
		journey.setUser(this);

		return journey;
	}

	public Journey removeJourney(Journey journey) {
		getJourneys().remove(journey);
		journey.setUser(null);

		return journey;
	}

}