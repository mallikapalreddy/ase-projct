package com.remindme.clientWebService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.remindme.daoImplement.UserDAO;
import com.remindme.model.User;
import com.remindme.response.LoginResponse;
import com.remindme.response.QuestionInfoResponse;
import com.remindme.response.SignUpResponse;

@Path("/androidapp/login/")
public class LoginServices {

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

		String username = "";
		String password = "";

		if (!formParams.isEmpty()) {
			username = ((formParams.get("username")).get(0)).toString();
			password = ((formParams.get("password")).get(0)).toString();
		}

		LoginResponse loginResp = new LoginResponse();

		UserDAO userDAO = new UserDAO();

		User user;
		user = userDAO.authentication(username, password);

		if (user != null) {
			loginResp.setIdUser(user.getIdU());
			loginResp.setAccepted(true);
		} else {
			loginResp.setIdUser(-1);
			loginResp.setAccepted(false);
		}

		String result = "" + loginResp;
		return Response.status(201).entity(result).build();
	}

	@Path("/signup")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response signUp(MultivaluedMap<String, String> formParams) {

		String username = "";
		String password = "";
		String phone = "";
		String email = "";
		String answerQuestion = "";
		String questionNumber = "";

		if (!formParams.isEmpty()) {
			username = ((formParams.get("username")).get(0)).toString();
			password = ((formParams.get("password")).get(0)).toString();
			phone = ((formParams.get("phone")).get(0)).toString();
			email = ((formParams.get("email")).get(0)).toString();
			answerQuestion = ((formParams.get("answerQuestion")).get(0))
					.toString();
			questionNumber = ((formParams.get("questionNumber")).get(0))
					.toString();
		}

		UserDAO userDAO = new UserDAO();
		SignUpResponse signUpResponse = new SignUpResponse();

		if (userDAO.isUsernameExist(username) != -1) {
			signUpResponse.setIdUser(-1);
			signUpResponse.setSignUp(false);
		} else {
			User user = new User();

			user.setAnswerQuestion(answerQuestion);
			user.setEmail(email);
			user.setPassWord(password);
			user.setPhone(phone);
			user.setUserName(username);
			user.setQuestionNumber(questionNumber);

			if (userDAO.create(user) != null) {
				int newID = userDAO.isUsernameExist(username);
				signUpResponse.setIdUser(newID);
				signUpResponse.setSignUp(true);
			} else {
				signUpResponse.setIdUser(-2);
				signUpResponse.setSignUp(false);
			}
		}

		String result = "" + signUpResponse;
		return Response.status(201).entity(result).build();
	}

	@Path("/getquestioninfo")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response getQuestionInfo(MultivaluedMap<String, String> formParams) {

		String username = "";

		if (!formParams.isEmpty()) {
			username = ((formParams.get("username")).get(0)).toString();
		}

		UserDAO userDAO = new UserDAO();
		QuestionInfoResponse questionInfoResponse = new QuestionInfoResponse();

		int id = userDAO.isUsernameExist(username);

		if (id == -1) {
			questionInfoResponse.setIdUser(-1);
			questionInfoResponse.setUsername(username);
		} else {
			User user = new User();
			user = userDAO.find(id);

			if (user != null) {
				questionInfoResponse.setIdUser(id);
				questionInfoResponse
						.setQuestionAnswer(user.getAnswerQuestion());
				questionInfoResponse.setQuestionNumber(Integer.parseInt(user
						.getQuestionNumber()));
				questionInfoResponse.setUsername(username);
			}

		}

		String result = "" + questionInfoResponse;
		return Response.status(201).entity(result).build();
	}

	@Path("/newpassword")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response newPassword(MultivaluedMap<String, String> formParams) {

		String idU = "";
		String password = "";

		if (!formParams.isEmpty()) {
			idU = ((formParams.get("idU")).get(0)).toString();
			password = ((formParams.get("password")).get(0)).toString();
		}

		UserDAO userDAO = new UserDAO();
		User user = new User();

		user = userDAO.find(Long.parseLong(idU));
		String result = "false";
		if (user != null) {

			user.setPassWord(password);
			if (userDAO.update(user) != null)
				result = "true";
			else
				result = "false";
		}
		return Response.status(201).entity(result).build();
	}
}
