package com.myyoutube.model;

import java.io.Serializable;

import javax.persistence.*;

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

	private String deactivated;

	private String email;

	private String freqlogon;

	private String password;

	public User() {
	}

	public int getIdU() {
		return this.idU;
	}

	public void setIdU(int idU) {
		this.idU = idU;
	}

	public String getDeactivated() {
		return this.deactivated;
	}

	public void setDeactivated(String deactivated) {
		this.deactivated = deactivated;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFreqlogon() {
		return this.freqlogon;
	}

	public void setFreqlogon(String freqlogon) {
		this.freqlogon = freqlogon;
	}

	public String getPassWord() {
		return this.password;
	}

	public void setPassWord(String passWord) {
		this.password = passWord;
	}

}