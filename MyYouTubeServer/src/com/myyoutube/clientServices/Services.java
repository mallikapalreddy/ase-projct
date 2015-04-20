package com.myyoutube.clientServices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.myyoutube.daoImplement.SearchDAO;
import com.myyoutube.daoImplement.UserDAO;
import com.myyoutube.model.Search;
import com.myyoutube.model.User;
import com.myyoutube.response.LoginResponse;
import com.myyoutube.response.SearchResp;
import com.myyoutube.response.UserResponse;

@Path("/androidapp/")
public class Services {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle() {

		return "<p> <h1> From LoginServices <h1><p>";
	}

	@Path("/authentication")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response authentication(MultivaluedMap<String, String> formParams) {

		String email = "";
		String password = "";

		if (!formParams.isEmpty()) {
			email = ((formParams.get("email")).get(0)).toString();
			password = ((formParams.get("password")).get(0)).toString();
		}

		LoginResponse loginResp = new LoginResponse();

		UserDAO userDAO = new UserDAO();

		User user;
		user = userDAO.authentication(email, password);

		if (user != null) {
			int j = Integer.parseInt(user.getDeactivated());
			if (j < 3) {
				loginResp.setIdUser(user.getIdU());
				loginResp.setAccepted(true);
				int i = Integer.parseInt(user.getFreqlogon());
				i++;
				user.setFreqlogon("" + i);
				user.setDeactivated("" + 0);
				userDAO.update(user);
			} else {
				loginResp.setIdUser(-2);
				loginResp.setAccepted(false);
			}
		} else {
			user = userDAO.isEmailExist(email);
			if (user != null) {
				int j = Integer.parseInt(user.getDeactivated());
				j++;
				user.setDeactivated("" + j);
				userDAO.update(user);
			}
			loginResp.setIdUser(-1);
			loginResp.setAccepted(false);
		}

		String result = "" + loginResp;
		return Response.status(201).entity(result).build();
	}

	@Path("/getuserlist")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response getUserList(MultivaluedMap<String, String> formParams) {

		String idU = "";

		if (!formParams.isEmpty()) {
			idU = ((formParams.get("idU")).get(0)).toString();
		}

		UserDAO userDAO = new UserDAO();

		List<User> users = userDAO.getAll();
		String result = null;
		if (users != null) {
			result = "{ \"listUsers\": [";
			boolean first = true;
			for (int i = 0; i < users.size(); i++) {

				if (!idU.equals(String.valueOf(users.get(i).getIdU()))) {
					UserResponse userResp = new UserResponse();
					userResp.setEmail(users.get(i).getEmail());
					userResp.setFreqlogon(users.get(i).getFreqlogon());
					if (first) {
						result += "" + userResp;
						first = false;
					} else
						result += ", " + "" + userResp;
				}
			}
			result += " ] }";
		}
		return Response.status(201).entity(result).build();
	}

	@Path("/getsearchlist")
	@POST
	@Produces("application/json")
	public Response getSearchList() {

		SearchDAO searchDAO = new SearchDAO();

		List<Search> searchs = searchDAO.getAll();
		String result = null;
		if (searchs != null) {
			result = "{ \"listSearch\": [";
			boolean first = true;
			for (int i = 0; i < searchs.size(); i++) {
				SearchResp searchResp = new SearchResp();
				searchResp.setSubject(searchs.get(i).getSubject());
				searchResp.setNumber(searchs.get(i).getNumber());
				if (first) {
					result += "" + searchResp;
					first = false;
				} else
					result += ", " + "" + searchResp;
			}
			result += " ] }";
		}
		return Response.status(201).entity(result).build();
	}

	@Path("/addsearch")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response addSearch(MultivaluedMap<String, String> formParams) {

		String search = "";

		if (!formParams.isEmpty()) {
			search = ((formParams.get("search")).get(0)).toString();
		}

		SearchDAO searchDAO = new SearchDAO();
		List<Search> searchs = searchDAO.getAll();
		Search ser = new Search();
		boolean searchExist = false;
		if (searchs != null) {
			for (int i = 0; i < searchs.size(); i++) {
				ser = searchs.get(i);
				if (search.equals(ser.getSubject())) {
					searchExist = true;
					int j = Integer.parseInt(ser.getNumber());
					j++;
					ser.setNumber("" + j);
					searchDAO.update(ser);
					break;
				}
			}
			if (!searchExist) {
				Search newSer = new Search();
				newSer.setNumber("" + 1);
				newSer.setSubject(search);
				searchDAO.create(newSer);
			}

		}
		return Response.status(201).entity(true).build();
	}

	@Path("/adduser")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response addUser(MultivaluedMap<String, String> formParams) {

		String email = "";
		String pass = "";

		if (!formParams.isEmpty()) {
			email = ((formParams.get("email")).get(0)).toString();
			pass = ((formParams.get("password")).get(0)).toString();
		}

		UserDAO userDAO = new UserDAO();
		User user = new User();

		user.setDeactivated("" + 0);
		user.setEmail(email);
		user.setFreqlogon("" + 0);
		user.setPassWord(pass);
		
		boolean result = false;
		if(userDAO.create(user)!=null)
			result = true;
		return Response.status(201).entity(result).build();
	}
}
