/**
 * Copyright 2011 Google
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.appengine.codelab;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.QueryResultList;

/**
 * This servlet responds to the request corresponding to users. The class
 * creates and manages the User Entity
 * 
 * @author
 */
@SuppressWarnings("serial")
public class VehiclePostServlet extends BaseServlet {

	private static final Logger logger = Logger
			.getLogger(VehiclePostServlet.class.getCanonicalName());

	/**
	 * Get the requested company entities in JSON format
	 */

	public static final int PAGE_SIZE = 8;

	@SuppressWarnings("deprecation")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String companyName = req.getParameter("companyName2");
		String vehicleRegNumber = req.getParameter("vehicleRegNumber2");
		String off = req.getParameter("offset");
		String isReg = req.getParameter("reg");
		String isComp = req.getParameter("company");
		String isAll = req.getParameter("all");
		String postId = req.getParameter("postId");
		int endIndex = 0;
		int colCount = 0;

		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		if (!"0".equals(postId)) {
			String postDetail = getSingleVehiclePost(req, resp);
			writer.println(postDetail);
			return;
		}

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Query query = null;
		Key ancestorKey = null;
		PreparedQuery queryString = null;
		Entity vehiclePost = null;

		if ((companyName == null) || "".equals(companyName)) {
			isComp = "false";
			isAll = "true";
		}
		if ((vehicleRegNumber == null) || "".equals(vehicleRegNumber)) {
			isReg = "false";
		}
		if ("false".equals(isReg) && "false".equals(isComp)) {
			isAll = "true";
		}

		if ("true".equals(isReg)) {
			query = new Query("VehiclePostAns");
			ancestorKey = KeyFactory.createKey("Vehicle",
					vehicleRegNumber.toLowerCase());
			query.setAncestor(ancestorKey);
			query.addFilter(Entity.KEY_RESERVED_PROPERTY,
					FilterOperator.GREATER_THAN, ancestorKey);
		} else if ("true".equals(isComp)) {
			query = new Query("VehiclePost");
			query.addFilter("companyName", FilterOperator.EQUAL, companyName);
		} else if ("true".equals(isAll)) {
			query = new Query("VehiclePost");
		}

		queryString = datastore.prepare(query);
		// set offset for the results to be fetched
		int offset = 0;
		if (off != null)
			offset = Integer.parseInt(off);

		// fetch results from datastore based on offset and page size
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(PAGE_SIZE);
		int rowCount = queryString.countEntities();

		QueryResultList<Entity> results = queryString
				.asQueryResultList(fetchOptions.offset(offset));

		// String pageContent = "<tbody>";
		String pageContent = "";

		// set footer for the table
		float pageCount = (float) rowCount / PAGE_SIZE;
		String footer = "";
		if (pageCount > rowCount / PAGE_SIZE)
			pageCount = (int) pageCount + 1;
		else
			pageCount = (int) pageCount;

		for (int i = 0; i < pageCount; i++) {
			footer += "<li><a href=\"#\" onclick=\"fillBody(" + i * PAGE_SIZE
					+ ")\">" + (i + 1) + "</a></li>  ";
		}

		if (rowCount > PAGE_SIZE) {
			if ((offset / PAGE_SIZE) != (pageCount - 1))
				footer += "<li class=\"aroo\"><a href=\"#\" onclick=\"fillBody("
						+ (offset + PAGE_SIZE) + ")\">&gt;&gt;</a></li>";
			else
				footer += "<li class=\"aroo\"><a href=\"#\" onclick=\"fillBody("
						+ (pageCount - 1) * PAGE_SIZE + ")\">&gt;&gt;</a></li>";
		}

		pageContent += "<div id=\"con_leftbottam\"><ul>" + footer
				+ "</ul></div>";

		if ((results == null) || (true == results.isEmpty())) {
			// condition to show message when data is not available
			pageContent += "<div id=\"con_leftbottam\"><ul><li><a>No records found</a></li></ul></div>";
		}

		// set the content of the table
		for (Entity entity : results) {
			colCount++;
			vehiclePost = entity;
			endIndex = vehiclePost.getProperty("errorDetails").toString()
					.length();
			if (endIndex > 10) {
				endIndex = 10;
			}
			pageContent += "<div id=\"left_block\" onclick=\"getBody("
					+ Util.getPostId(vehiclePost) + ")\">";
			pageContent += "<span class=\"paper_img\"> <img src="
					+ vehiclePost.getProperty("userImg") + "></img> </span>";
			pageContent += "<span class=\"con_rect\">";
			pageContent += "<ul>";
			pageContent += "<li class=\"error\">"
					+ vehiclePost.getProperty("companyName").toString()
					+ " Error:  "
					+ vehiclePost.getProperty("errorDetails").toString()
							.substring(0, endIndex) + "</li>";
			pageContent += "<li class=\"name\">Sticker Name : "
					+ vehiclePost.getProperty("uniqueId").toString() + "<li>";
			pageContent += "</ul>";
			pageContent += "</span>";
			pageContent += "</div>";
		}
		for (; colCount < PAGE_SIZE; colCount++) {
			pageContent += "<div id=\"left_block\" >";
			pageContent += "<span class=\"paper_img\"> </span>";
			pageContent += "<span class=\"con_rect\">";
			pageContent += "<ul>";
			pageContent += "<li class=\"error\"> </li>";
			pageContent += "<li class=\"name\"></li>";
			pageContent += "</ul>";
			pageContent += "</span>";
			pageContent += "</div>";

		}

		// pageContent += "</tbody>";
		writer.println(pageContent);
	}

	private String getSingleVehiclePost(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

		String postId = req.getParameter("postId");
		Entity vehiclePost = VehiclePost.getSingleVehiclePost(postId);

		return VehiclePost.createMailMessage(vehiclePost);
		/*
		 * resp.setContentType("application/json; charset=utf-8");
		 * resp.setHeader("Cache-Control", "no-cache"); PrintWriter writer =
		 * resp.getWriter(); writer.print(Util.writeJSON(vehiclePost));
		 * //changed by muturu return;
		 */

	}

	/**
	 * Insert the new vehiclePost
	 */

	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Creating Vehicle Post");
		String errorDetails = req.getParameter("errorDetails");
		String userId = req.getParameter("userId");
		String uniqueId = req.getParameter("uniqueId");
		String date = req.getParameter("date");
		String blobkey = req.getParameter("blobkey");
		String returnVal = VehiclePost.createOrUpdateVehiclePost(userId,
				uniqueId, date, errorDetails, blobkey);
		System.out.println(returnVal); // added by muturu
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();
		writer.println(returnVal);
	}

	/**
	 * Delete the Vehicle
	 */
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.log(Level.SEVERE, "Deleting VehiclePost Not Supported");
	}

	/**
	 * Redirect the call to doDelete or doPut method
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String action = req.getParameter("action");
		if (action.equalsIgnoreCase("delete")) {
			doDelete(req, resp);
			return;
		} else if (action.equalsIgnoreCase("put")) {
			doPut(req, resp);
			return;
		}
	}
}
