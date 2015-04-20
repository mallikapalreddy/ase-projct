package com.myyoutube.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.myyoutube.traitementBD.ConnexionManager;

public abstract class DAO<T> {

	public EntityManager entitymanger = ConnexionManager.initEntityManager();

	/*
	 * permet de recuperer un objet vi son id
	 */

	public abstract T find(long id);

	/*
	 * Permet de cr�er une entr�e dans la base de donn�es par rapport �
	 * un objet
	 */
	public abstract T create(T obj);

	/*
	 * Permet de mettre � jour les donn�es d'une entr�e dans la base
	 */
	public abstract T update(T obj);

	/*
	 * Permet la suppression d'une entr�e de la base
	 */
	public abstract void delete(T obj);

	/*
	 * Permet la recuperation de la liste des admins
	 */
	public abstract List<T> getAll();

}
