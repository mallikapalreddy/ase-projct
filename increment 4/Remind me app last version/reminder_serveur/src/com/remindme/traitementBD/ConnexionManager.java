package com.remindme.traitementBD;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class ConnexionManager {

	private static EntityManager em;
	private static final String PERSISTENCE_UNIT_NAME = "reminder_serveur";

	public static EntityManager initEntityManager() {
		if (em == null) {
			em = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME)
					.createEntityManager();
		}
		return em;
	}

	public void closeEntityManager(EntityManager emClose) {
		emClose.close();
	}

}
