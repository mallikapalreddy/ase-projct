package com.remindme.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the shopping database table.
 * 
 */
@Entity
@NamedQuery(name="Shopping.findAll", query="SELECT s FROM Shopping s")
public class Shopping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idS;

	private String address;

	private String dateEntry;

	private String dateShopping;

	private String estimatedAmt;

	private String items;

	private String title;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="idUser")
	private User user;

	public Shopping() {
	}

	public int getIdS() {
		return this.idS;
	}

	public void setIdS(int idS) {
		this.idS = idS;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDateEntry() {
		return this.dateEntry;
	}

	public void setDateEntry(String dateEntry) {
		this.dateEntry = dateEntry;
	}

	public String getDateShopping() {
		return this.dateShopping;
	}

	public void setDateShopping(String dateShopping) {
		this.dateShopping = dateShopping;
	}

	public String getEstimatedAmt() {
		return this.estimatedAmt;
	}

	public void setEstimatedAmt(String estimatedAmt) {
		this.estimatedAmt = estimatedAmt;
	}

	public String getItems() {
		return this.items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}