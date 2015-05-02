package com.remindme.response;

public class SignUpResponse {

	private int idUser;
	private boolean signUp;

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public boolean isSignUp() {
		return signUp;
	}

	public void setSignUp(boolean signUp) {
		this.signUp = signUp;
	}

	@Override
	public String toString() {
		return "{ \"SignUpResponse\": [ { \"id\": \"" + idUser
				+ "\", \"isSignUp\": \"" + signUp + "\" } ] }";

	}

}
