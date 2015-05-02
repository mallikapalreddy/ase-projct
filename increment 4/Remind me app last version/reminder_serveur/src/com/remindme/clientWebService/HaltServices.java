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
import com.remindme.model.Halt;
import com.remindme.model.Journey;
import com.remindme.response.HaltListResponse;

@Path("/androidapp/halt/")
public class HaltServices {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle() {

		return "<p> <h1> From HaltServices <h1><p>";
	}

	@Path("/addhalt")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response addHalt(MultivaluedMap<String, String> formParams) {

		String result = "false";
		try {
			String idJ = "";
			String expectedArrivalTime = "";
			String expectedDepartureTime = "";
			String recorderVoiceH = "";
			String station = "";

			if (!formParams.isEmpty()) {
				idJ = ((formParams.get("idJ")).get(0)).toString();
				expectedArrivalTime = ((formParams.get("expectedArrivalTime"))
						.get(0)).toString();
				expectedDepartureTime = ((formParams
						.get("expectedDepartureTime")).get(0)).toString();
				recorderVoiceH = ((formParams.get("recorderVoiceH")).get(0))
						.toString();
				station = ((formParams.get("station")).get(0)).toString();
			}

			Halt halt = new Halt();
			HaltDAO haltDAO = new HaltDAO();
			Journey journey = new Journey();
			JourneyDAO journeyDAO = new JourneyDAO();

			journey = journeyDAO.find(Long.parseLong(idJ));

			halt.setExpectedArrivalTime(expectedArrivalTime);
			halt.setExpectedDepartureTime(expectedDepartureTime);
			halt.setRecorderVoiceH(recorderVoiceH);
			halt.setStation(station);
			halt.setJourney(journey);

			if (haltDAO.create(halt) != null) {
				result = "true";
				journey.addHalt(halt);
			}

		} catch (Exception e) {
			result = "false";
		}
		return Response.status(201).entity(result).build();
	}

	@Path("/deletehalt")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response deleteHalt(MultivaluedMap<String, String> formParams) {

		String result = "true";
		try {
			String idH = "";

			if (!formParams.isEmpty()) {
				idH = ((formParams.get("idH")).get(0)).toString();
			}

			Halt halt = new Halt();
			HaltDAO haltDAO = new HaltDAO();

			halt = haltDAO.find(Long.parseLong(idH));
			if (halt != null) {
				haltDAO.delete(halt);
				Journey journey = new Journey();
				journey = halt.getJourney();
				journey.removeHalt(halt);
			} else
				result = "false";

		} catch (Exception e) {
			result = "false";
		}

		return Response.status(201).entity(result).build();
	}

	@Path("/updatehalt")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response updateHalt(MultivaluedMap<String, String> formParams) {

		String result = "false";
		try {
			String idH = "";
			String idJ = "";
			String expectedArrivalTime = "";
			String expectedDepartureTime = "";
			String recorderVoiceH = "";
			String station = "";

			if (!formParams.isEmpty()) {
				idH = ((formParams.get("idH")).get(0)).toString();
				idJ = ((formParams.get("idJ")).get(0)).toString();
				expectedArrivalTime = ((formParams.get("expectedArrivalTime"))
						.get(0)).toString();
				expectedDepartureTime = ((formParams
						.get("expectedDepartureTime")).get(0)).toString();
				recorderVoiceH = ((formParams.get("recorderVoiceH")).get(0))
						.toString();
				station = ((formParams.get("station")).get(0)).toString();
			}

			Halt halt = new Halt();
			HaltDAO haltDAO = new HaltDAO();

			halt = haltDAO.find(Long.parseLong(idH));

			if (halt != null) {
				Journey journey = new Journey();
				JourneyDAO journeyDAO = new JourneyDAO();
				if (verify(idJ))
					journey = journeyDAO.find(Long.parseLong(idJ));

				if (verify(expectedArrivalTime))
					halt.setExpectedArrivalTime(expectedArrivalTime);
				if (verify(expectedDepartureTime))
					halt.setExpectedDepartureTime(expectedDepartureTime);
				if (verify(recorderVoiceH))
					halt.setRecorderVoiceH(recorderVoiceH);
				if (verify(station))
					halt.setStation(station);
				if (journey != null)
					halt.setJourney(journey);

				if (haltDAO.update(halt) != null) {
					result = "true";
				}
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

	@Path("/gethaltlist")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response getHaltList(MultivaluedMap<String, String> formParams) {

		String idJ = "";
		String result = null;

		try {
			if (!formParams.isEmpty()) {
				idJ = ((formParams.get("idJ")).get(0)).toString();
			}

			JourneyDAO journeyDAO = new JourneyDAO();
			Journey journey = new Journey();

			journey = journeyDAO.find(Long.parseLong(idJ));

			List<Halt> listHalts = journey.getHalts();

			if (listHalts != null) {
				result = "{ \"listHalt\": [";
				boolean first = true;
				for (int i = 0; i < listHalts.size(); i++) {
					HaltListResponse haltListResponse = new HaltListResponse();

					haltListResponse.setExpectedArrivalTime((listHalts.get(i))
							.getExpectedArrivalTime());
					haltListResponse
							.setExpectedDepartureTime((listHalts.get(i))
									.getExpectedDepartureTime());
					haltListResponse.setIdH((listHalts.get(i)).getIdH());
					haltListResponse.setIdJ((listHalts.get(i)).getJourney()
							.getIdJ());
					haltListResponse.setRecorderVoiceH((listHalts.get(i))
							.getRecorderVoiceH());
					haltListResponse
							.setStation((listHalts.get(i)).getStation());

					if (first) {
						result += "" + haltListResponse;
						first = false;
					} else
						result += ", " + haltListResponse;
				}
				result += " ] }";
			}
		} catch (Exception e) {
			result = null;
		}

		return Response.status(201).entity(result).build();
	}

}