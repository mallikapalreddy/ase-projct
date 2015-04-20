package com.myyoutube.response;

public class LoginResponse {

	private int idUser;
	private boolean accepted;

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public String toString() {
		return "{ \"LoginResponse\": [ { \"id\": \"" + idUser
				+ "\", \"accepted\": \"" + accepted + "\" } ] }";
	}

}
