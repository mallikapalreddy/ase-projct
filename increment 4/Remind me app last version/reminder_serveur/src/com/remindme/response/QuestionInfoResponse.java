package com.remindme.response;

public class QuestionInfoResponse {

	private int idUser;
	private int questionNumber;
	private String questionAnswer;
	private String username;

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getQuestionAnswer() {
		return questionAnswer;
	}

	public void setQuestionAnswer(String questionAnswer) {
		this.questionAnswer = questionAnswer;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "{ \"QuestionInfoResponse\": [ {" + " \"id\": \"" + idUser + "\","
				+ " \"questionNumber\": \"" + questionNumber + "\","
				+ " \"questionAnswer\": \"" + questionAnswer + "\","
				+ " \"username\": \"" + username + "\" } ] }";

	}

}
