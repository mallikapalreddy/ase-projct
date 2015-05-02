package com.remindme.daoImplement;

import java.util.List;

import javax.persistence.EntityManager;

import com.remindme.dao.DAO;
import com.remindme.model.Halt;

public class HaltDAO extends DAO<Halt> {

	public static EntityManager entity;

	public static void main(String[] args) {

		Halt halt = new Halt();
		HaltDAO haltDAO = new HaltDAO();

		halt = haltDAO.find(Long.parseLong("2"));

		try {
			haltDAO.delete(halt);
			System.out.println("*****  ok   ******");
		} catch (Exception e) {
			System.out.println("*****  not ok   ******");
		}

	}

	@Override
	public Halt find(long idH) {
		entity = this.entitymanger;
		Halt halt = null;
		try {
			halt = (Halt) entity.createQuery(
					"select p from Halt p WHERE p.idH =" + idH)
					.getSingleResult();
		} catch (Exception e) {
			System.out.println("HaltDAO.find(long idH= " + idH + ")"
					+ e.getMessage());
		}
		return halt;
	}

	@Override
	public Halt create(Halt halt) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.persist(halt);
			entity.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("HaltDAO.create " + e.getMessage());
			return null;
		}
		return halt;
	}

	@Override
	public Halt update(Halt halt) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.merge(halt);
			entity.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("HaltDAO.update " + e.getMessage());
			return null;
		}
		return halt;
	}

	@Override
	public void delete(Halt halt) {
		entitymanger.getTransaction().begin();
		entitymanger.remove(halt);
		entitymanger.getTransaction().commit();
	}

	@Override
	public List<Halt> getAll() {
		@SuppressWarnings("unchecked")
		List<Halt> haltList = entitymanger.createQuery("select p from Halt p ")
				.getResultList();
		return haltList;
	}

}
