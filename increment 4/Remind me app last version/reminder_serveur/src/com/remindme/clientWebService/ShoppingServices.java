package com.remindme.clientWebService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.remindme.daoImplement.ShoppingDAO;
import com.remindme.daoImplement.UserDAO;
import com.remindme.model.Shopping;
import com.remindme.model.User;
import com.remindme.response.ShoppingListResponse;

@Path("/androidapp/shopping/")
public class ShoppingServices {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle() {

		return "<p> <h1> From ShoppingServices <h1><p>";
	}

	@Path("/addshoppinglist")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response addShoppinglist(MultivaluedMap<String, String> formParams) {

		String idU = "";
		String items = "";
		String address = "";
		String estimatedAmt = "";
		String dateShopping = "";
		String title = "";

		String result = "false";

		try {

			if (!formParams.isEmpty()) {
				idU = ((formParams.get("idU")).get(0)).toString();
				items = ((formParams.get("items")).get(0)).toString();
				address = ((formParams.get("address")).get(0)).toString();
				estimatedAmt = ((formParams.get("estimatedAmt")).get(0))
						.toString();
				dateShopping = ((formParams.get("dateShopping")).get(0))
						.toString();
				title = ((formParams.get("title")).get(0)).toString();
			}

			Shopping shop = new Shopping();

			shop.setAddress(address);
			shop.setDateShopping(dateShopping);
			shop.setEstimatedAmt(estimatedAmt);
			shop.setItems(items);
			shop.setTitle(title);

			UserDAO userDAO = new UserDAO();
			shop.setUser(userDAO.find(Long.parseLong(idU)));

			DateFormat dateFormat = new SimpleDateFormat("MMMM dd, YYYY");
			Date date = new Date();
			shop.setDateEntry(dateFormat.format(date));

			ShoppingDAO shopDAO = new ShoppingDAO();
			if (shopDAO.create(shop) != null) {
				result = "true";
				(shop.getUser()).addShopping(shop);
			}

		} catch (Exception e) {
			result = "false";
		}
		return Response.status(201).entity(result).build();
	}

	@Path("/getshoppinglist")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response getShoppingList(MultivaluedMap<String, String> formParams) {

		String idU = "";

		if (!formParams.isEmpty()) {
			idU = ((formParams.get("idU")).get(0)).toString();
		}

		UserDAO userDAO = new UserDAO();
		User user = new User();

		user = userDAO.find(Long.parseLong(idU));

		List<Shopping> listShoppings = user.getShoppings();

		String result = null;
		if (listShoppings != null) {
			result = "{ \"listShoppings\": [";
			boolean first = true;
			for (int i = 0; i < listShoppings.size(); i++) {
				ShoppingListResponse listShoppingResponse = new ShoppingListResponse();

				listShoppingResponse.setAddress((listShoppings.get(i))
						.getAddress());
				listShoppingResponse.setDateEntry((listShoppings.get(i))
						.getDateEntry());
				listShoppingResponse.setDateShopping((listShoppings.get(i))
						.getDateShopping());
				listShoppingResponse.setEstimatedAmt((listShoppings.get(i))
						.getEstimatedAmt());
				listShoppingResponse.setIdS((listShoppings.get(i)).getIdS());
				listShoppingResponse
						.setItems((listShoppings.get(i)).getItems());
				listShoppingResponse
						.setTitle((listShoppings.get(i)).getTitle());

				if (first) {
					result += "" + listShoppingResponse;
					first = false;
				} else
					result += ", " + listShoppingResponse;
			}
			result += " ] }";
		}

		return Response.status(201).entity(result).build();
	}

	@Path("/updateshop")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response updateShop(MultivaluedMap<String, String> formParams) {

		String idS = "";
		String items = "";
		String address = "";
		String estimatedAmt = "";
		String dateShopping = "";
		String title = "";

		String result = "false";

		try {
			if (!formParams.isEmpty()) {
				idS = ((formParams.get("idS")).get(0)).toString();
				items = ((formParams.get("items")).get(0)).toString();
				address = ((formParams.get("address")).get(0)).toString();
				estimatedAmt = ((formParams.get("estimatedAmt")).get(0))
						.toString();
				dateShopping = ((formParams.get("dateShopping")).get(0))
						.toString();
				title = ((formParams.get("title")).get(0)).toString();
			}
			ShoppingDAO shopDAO = new ShoppingDAO();
			Shopping shop = new Shopping();
			Shopping oldShop = new Shopping();
			User user = new User();

			oldShop = shopDAO.find(Integer.parseInt(idS));
			user = oldShop.getUser();

			shop.setIdS(Integer.parseInt(idS));
			shop.setDateEntry(oldShop.getDateEntry());
			shop.setUser(user);

			if (address.equals(""))
				shop.setAddress(oldShop.getAddress());
			else
				shop.setAddress(address);

			if (dateShopping.equals(""))
				shop.setDateShopping(oldShop.getDateShopping());
			else
				shop.setDateShopping(dateShopping);

			if (estimatedAmt.equals(""))
				shop.setEstimatedAmt(oldShop.getEstimatedAmt());
			else
				shop.setEstimatedAmt(estimatedAmt);

			if (items.equals(""))
				shop.setItems(oldShop.getItems());
			else
				shop.setItems(items);

			if (title.equals(""))
				shop.setTitle(oldShop.getTitle());
			else
				shop.setTitle(title);

			if (shopDAO.update(shop) != null)
				result = "true";

		} catch (Exception e) {
			result = "false";
		}
		return Response.status(201).entity(result).build();
	}

	@Path("/deleteshop")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public Response deleteShop(MultivaluedMap<String, String> formParams) {

		String idS = "";

		String result = "false";

		try {
			if (!formParams.isEmpty()) {
				idS = ((formParams.get("idS")).get(0)).toString();
			}

			ShoppingDAO shoppingDAO = new ShoppingDAO();

			Shopping shop = new Shopping();

			shop = shoppingDAO.find(Long.parseLong(idS));

			if (shop != null) {
				shoppingDAO.delete(shoppingDAO.find(Long.parseLong(idS)));
				(shop.getUser()).removeShopping(shop);
				result = "true";
			}

		} catch (Exception e) {
			result = "false";
		}
		return Response.status(201).entity(result).build();
	}
}
