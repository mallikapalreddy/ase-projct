package com.remindme.daoImplement;

import java.util.List;

import javax.persistence.EntityManager;

import com.remindme.dao.DAO;
import com.remindme.model.User;

public class UserDAO extends DAO<User> {
	public static EntityManager entity;

	@Override
	public User find(long idU) {
		entity = this.entitymanger;
		User u = null;
		try {
			u = (User) entity.createQuery(
					"select p from User p WHERE p.idU =" + idU)
					.getSingleResult();
		} catch (Exception e) {
			System.out.println("dans UserDAO.find(long idU= " + idU + ")"
					+ e.getMessage());
		}
		return u;
	}

	@Override
	public User create(User u) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.persist(u);
			entity.getTransaction().commit();
		} catch (Exception e) {
			return null;
		}
		return u;
	}

	@Override
	public User update(User u) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.merge(u);
			entity.getTransaction().commit();
		} catch (Exception e) {
			return null;
		}
		return u;
	}

	@Override
	public void delete(User u) {
		try {
			entitymanger.getTransaction().begin();
			entitymanger.remove(u);
			entitymanger.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("dans UserDAO.delete(u)" + e.getMessage());
		}
	}

	@Override
	public List<User> getAll() {
		@SuppressWarnings("unchecked")
		List<User> Users = entitymanger.createQuery("select p from User p ")
				.getResultList();
		return Users;
	}

	public User authentication(String username, String password) {
		entity = this.entitymanger;
		@SuppressWarnings("unchecked")
		List<User> users = entity.createQuery(
				"select p from User p WHERE p.userName='" + username
						+ "' AND p.passWord='" + password + "'")
				.getResultList();
		User u = null;
		for (User user : users) {
			u = user;
		}

		return u;
	}

	public int isUsernameExist(String username) {
		entity = this.entitymanger;
		@SuppressWarnings("unchecked")
		List<User> users = entity.createQuery(
				"select p from User p WHERE p.userName='" + username + "'")
				.getResultList();

		for (User user : users) {
			return user.getIdU();
		}

		return -1;
	}

}
