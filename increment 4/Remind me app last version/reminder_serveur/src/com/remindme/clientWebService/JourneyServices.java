package com.remindme.clientWebService;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.remindme.daoImplement.HaltDAO;
import com.remindme.daoImplement.JourneyDAO;
import com.remindme.daoImplement.UserDAO;
import com.remindme.model.Halt;
import com.remindme.model.Journey;
import com.remindme.model.User;
import com.remindme.response.JourneyListResponse;

@Path("/androidapp/journey/")
public class JourneyServices {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle() {

		return "<p> <h1> From JourneyServices <h1><p>";
	}

	@Path("/addjourney")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response addJourney(MultivaluedMap<String, String> formParams) {

		String result = "false";
		try {
			String idU = "";
			String date = "";
			String destination = "";
			String expectedArrivalTime = "";
			String planname = "";
			String recorderVoice = "";
			String source = "";

			if (!formParams.isEmpty()) {
				idU = ((formParams.get("idU")).get(0)).toString();
				date = ((formParams.get("date")).get(0)).toString();
				destination = ((formParams.get("destination")).get(0))
						.toString();
				expectedArrivalTime = ((formParams.get("expectedArrivalTime"))
						.get(0)).toString();
				planname = ((formParams.get("planname")).get(0)).toString();
				recorderVoice = ((formParams.get("recorderVoice")).get(0))
						.toString();
				source = ((formParams.get("source")).get(0)).toString();
			}

			JourneyDAO journayDAO = new JourneyDAO();
			Journey journey = new Journey();
			User user = new User();
			UserDAO userDAO = new UserDAO();

			user = userDAO.find(Long.parseLong(idU));

			journey.setDate(date);
			journey.setDestination(destination);
			journey.setExpectedArrivalTime(expectedArrivalTime);
			journey.setPlanName(planname);
			journey.setRecorderVoice(recorderVoice);
			journey.setSource(source);
			journey.setUser(user);

			if (journayDAO.create(journey) != null) {
				result = "true";
				user.addJourney(journey);
			}

		} catch (Exception e) {
			result = "false";
		}
		return Response.status(201).entity(result).build();
	}

	@Path("/deletejourney")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response deleteJourney(MultivaluedMap<String, String> formParams) {

		String result = "true";
		try {
			String idJ = "";

			if (!formParams.isEmpty()) {
				idJ = ((formParams.get("idJ")).get(0)).toString();
			}

			JourneyDAO journeyDAO = new JourneyDAO();
			HaltDAO haltDAO = new HaltDAO();
			Journey journey = new Journey();
			journey = journeyDAO.find(Long.parseLong(idJ));

			if (journey != null) {
				List<Halt> haltList = journey.getHalts();

				for (Halt halt : haltList)
					haltDAO.delete(halt);

				journeyDAO.delete(journey);
				User user = new User();
				user = journey.getUser();
				user.removeJourney(journey);
			} else
				result = "false";

		} catch (Exception e) {
			result = "false";
		}

		return Response.status(201).entity(result).build();
	}

	@Path("/updatejourney")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response updateJourney(MultivaluedMap<String, String> formParams) {

		String result = "false";
		try {
			String idJ = "";
			String idU = "";
			String date = "";
			String destination = "";
			String expectedArrivalTime = "";
			String planname = "";
			String recorderVoice = "";
			String source = "";

			if (!formParams.isEmpty()) {
				idJ = ((formParams.get("idJ")).get(0)).toString();
				idU = ((formParams.get("idU")).get(0)).toString();
				date = ((formParams.get("date")).get(0)).toString();
				destination = ((formParams.get("destination")).get(0))
						.toString();
				expectedArrivalTime = ((formParams.get("expectedArrivalTime"))
						.get(0)).toString();
				planname = ((formParams.get("planname")).get(0)).toString();
				recorderVoice = ((formParams.get("recorderVoice")).get(0))
						.toString();
				source = ((formParams.get("source")).get(0)).toString();
			}

			JourneyDAO journeyDAO = new JourneyDAO();
			Journey journey = new Journey();
			User user = new User();
			UserDAO userDAO = new UserDAO();

			journey = journeyDAO.find(Long.parseLong(idJ));

			if (journey != null) {
				user = userDAO.find(Long.parseLong(idU));

				if (verify(date))
					journey.setDate(date);
				if (verify(destination))
					journey.setDestination(destination);
				if (verify(expectedArrivalTime))
					journey.setExpectedArrivalTime(expectedArrivalTime);
				if (verify(planname))
					journey.setPlanName(planname);
				if (verify(recorderVoice))
					journey.setRecorderVoice(recorderVoice);
				if (verify(source))
					journey.setSource(source);
				if (user != null)
					journey.setUser(user);

				if (journeyDAO.update(journey) != null)
					result = "true";
			}
		} catch (Exception e) {
			result = "false";
		}
		return Response.status(201).entity(result).build();
	}

	private boolean verify(String str) {
		if (str != null && str.length() != 0)
			return true;
		return false;
	}

	@Path("/getjourneylist")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response getJourneyList(MultivaluedMap<String, String> formParams) {

		String idU = "";
		String result = null;

		try {
			if (!formParams.isEmpty()) {
				idU = ((formParams.get("idU")).get(0)).toString();
			}

			UserDAO userDAO = new UserDAO();
			User user = new User();

			user = userDAO.find(Long.parseLong(idU));

			List<Journey> listJourneys = user.getJourneys();

			if (listJourneys != null) {
				result = "{ \"listJourney\": [";
				boolean first = true;
				for (int i = 0; i < listJourneys.size(); i++) {
					JourneyListResponse journeyListResponse = new JourneyListResponse();

					journeyListResponse
							.setDate((listJourneys.get(i)).getDate());
					journeyListResponse.setDestination((listJourneys.get(i))
							.getDestination());
					journeyListResponse.setExpectedArrivalTime((listJourneys
							.get(i)).getExpectedArrivalTime());
					journeyListResponse.setIdJ((listJourneys.get(i)).getIdJ());
					journeyListResponse.setPlanName((listJourneys.get(i))
							.getPlanName());
					journeyListResponse.setRecorderVoice((listJourneys.get(i))
							.getRecorderVoice());
					journeyListResponse.setSource((listJourneys.get(i))
							.getSource());
					journeyListResponse.setHaltsNumber(((listJourneys.get(i))
							.getHalts()).size());

					if (first) {
						result += "" + journeyListResponse;
						first = false;
					} else
						result += ", " + journeyListResponse;
				}
				result += " ] }";
			}
		} catch (Exception e) {
			result = null;
		}

		return Response.status(201).entity(result).build();
	}

	@Path("/getonejourney")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response getOneJourney(MultivaluedMap<String, String> formParams) {

		String idJ = "";
		String result = null;

		try {
			if (!formParams.isEmpty()) {
				idJ = ((formParams.get("idJ")).get(0)).toString();
			}

			JourneyDAO journeyDAO = new JourneyDAO();
			Journey journey = new Journey();
			journey = journeyDAO.find(Long.parseLong(idJ));

			if (journey != null) {
				JourneyListResponse journeyListResponse = new JourneyListResponse();
				journeyListResponse.setDate(journey.getDate());
				journeyListResponse.setDestination(journey.getDestination());
				journeyListResponse.setExpectedArrivalTime(journey
						.getExpectedArrivalTime());
				journeyListResponse.setIdJ(journey.getIdJ());
				journeyListResponse.setPlanName(journey.getPlanName());
				journeyListResponse
						.setRecorderVoice(journey.getRecorderVoice());
				journeyListResponse.setSource(journey.getSource());
				journeyListResponse.setHaltsNumber((journey.getHalts()).size());

				result = "{ \"oneJourney\": [" + journeyListResponse + " ] }";
			}
		} catch (Exception e) {
			result = null;
		}

		return Response.status(201).entity(result).build();
	}

}
