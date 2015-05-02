package com.remindme.daoImplement;

import java.util.List;

import javax.persistence.EntityManager;

import com.remindme.dao.DAO;
import com.remindme.model.Halt;
import com.remindme.model.Journey;

public class JourneyDAO extends DAO<Journey> {

	public static EntityManager entity;

	public static void main(String[] args) {

		JourneyDAO journeyDAO = new JourneyDAO();
		HaltDAO haltDAO = new HaltDAO();
		Journey journey = new Journey();

		journey = journeyDAO.find(Long.parseLong("2"));

		if (journey != null) {
			List<Halt> haltList = journey.getHalts();

			for (Halt halt : haltList) {
				haltDAO.delete(halt);
				System.out.println("*****  for  ******");
			}

			journeyDAO.delete(journey);
			System.out.println("*****  ok  ******");
		} else {
			System.out.println("*****  not ok  ******");
		}
	}

	@Override
	public Journey find(long idJ) {
		entity = this.entitymanger;
		Journey journey = null;
		try {
			journey = (Journey) entity.createQuery(
					"select p from Journey p WHERE p.idJ =" + idJ)
					.getSingleResult();
		} catch (Exception e) {
			System.out.println("JourneyDAO.find(long idJ= " + idJ + ")"
					+ e.getMessage());
		}
		return journey;
	}

	@Override
	public Journey create(Journey journey) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.persist(journey);
			entity.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("JourneyDAO.create " + e.getMessage());
			return null;
		}
		return journey;

	}

	@Override
	public Journey update(Journey journey) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.merge(journey);
			entity.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("********* ********  JourneyDAO.update "
					+ e.getMessage());
			return null;
		}
		return journey;
	}

	@Override
	public void delete(Journey journey) {
		entitymanger.getTransaction().begin();
		entitymanger.remove(journey);
		entitymanger.getTransaction().commit();
	}

	@Override
	public List<Journey> getAll() {
		@SuppressWarnings("unchecked")
		List<Journey> journeyList = entitymanger.createQuery(
				"select p from Journey p ").getResultList();
		return journeyList;
	}

}
