package com.myyoutube.daoImplement;

import java.util.List;

import javax.persistence.EntityManager;

import com.myyoutube.dao.DAO;
import com.myyoutube.model.Search;

public class SearchDAO extends DAO<Search> {
	public static EntityManager entity;

	@Override
	public Search find(long idS) {
		entity = this.entitymanger;
		Search search = null;
		try {
			search = (Search) entity.createQuery(
					"select p from Search p WHERE p.idS =" + idS)
					.getSingleResult();
		} catch (Exception e) {
			System.out.println(" SearchDAO.find(long idS= " + idS + ")"
					+ e.getMessage());
		}
		return search;
	}

	@Override
	public Search create(Search search) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.persist(search);
			entity.getTransaction().commit();
		} catch (Exception e) {
			return null;
		}
		return search;
	}

	@Override
	public Search update(Search search) {
		entity = this.entitymanger;
		try {
			entity.getTransaction().begin();
			entity.merge(search);
			entity.getTransaction().commit();
		} catch (Exception e) {
			return null;
		}
		return search;
	}

	@Override
	public void delete(Search search) {
		try {
			entitymanger.getTransaction().begin();
			entitymanger.remove(search);
			entitymanger.getTransaction().commit();
		} catch (Exception e) {
			System.out.println("SearchDAO.delete(search)" + e.getMessage());
		}
	}

	@Override
	public List<Search> getAll() {
		@SuppressWarnings("unchecked")
		List<Search> Searchs = entitymanger.createQuery(
				"select p from Search p ").getResultList();
		return Searchs;
	}

}
