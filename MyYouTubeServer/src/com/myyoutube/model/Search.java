package com.myyoutube.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistent class for the search database table.
 * 
 */
@Entity
@NamedQuery(name = "Search.findAll", query = "SELECT s FROM Search s")
public class Search implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idS;

	private String number;

	private String subject;

	public Search() {
	}

	public int getIdS() {
		return this.idS;
	}

	public void setIdS(int idS) {
		this.idS = idS;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}