package com.remindme.daoImplement;

import java.util.List;

import javax.persistence.EntityManager;

import com.remindme.dao.DAO;
import com.remindme.model.Shopping;

public class ShoppingDAO extends DAO<Shopping> {
	public static EntityManager entity;

	public static void main(String[] args) {

	}

	@Override
	public Shopping find(long id) {
		entity = this.entitymanger;
		Shopping shopping = null;
		try {
			shopping = (Shopping) entity.createQuery(
					"select p from Shopping p WHERE p.idS =" + id)
					.getSingleResult();
		} catch (Exception e) {
			System.out.println("Dans ShoppingDAO.find(long idP= " + id + ")"
					+ e.getMessage());
		}
		return shopping;
	}

	@Override
	public Shopping create(Shopping shop) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.persist(shop);
			entity.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("Dans ShoppingDAO.create " + e.getMessage());
			return null;
		}
		return shop;
	}

	@Override
	public Shopping update(Shopping shop) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.merge(shop);
			entity.getTransaction().commit();
		} catch (Exception e) {
			return null;
		}
		return shop;
	}

	@Override
	public void delete(Shopping shop) {
		try {
			entitymanger.getTransaction().begin();
			entitymanger.remove(shop);
			entitymanger.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("Dans ShoppingDAO.delete " + e.getMessage());
		}

	}

	@Override
	public List<Shopping> getAll() {
		@SuppressWarnings("unchecked")
		List<Shopping> shoppingList = entitymanger.createQuery(
				"select p from Shopping p ").getResultList();
		return shoppingList;
	}

	Shopping myMerge(Shopping entityToSave) {
		Shopping attached = entitymanger.find(Shopping.class,
				entityToSave.getIdS());
		if (attached == null) {
			attached = new Shopping();
			entitymanger.persist(attached);
		}
		copyProperties(attached, entityToSave);

		return attached;
	}

	private void copyProperties(Shopping attached, Shopping entityToSave) {

		attached.setAddress(entityToSave.getAddress());
		attached.setDateEntry(entityToSave.getDateEntry());
		attached.setDateShopping(entityToSave.getDateShopping());
		attached.setEstimatedAmt(entityToSave.getItems());
		attached.setTitle(entityToSave.getTitle());
		attached.setIdS(entityToSave.getIdS());
		attached.setItems(entityToSave.getItems());

	}

}
