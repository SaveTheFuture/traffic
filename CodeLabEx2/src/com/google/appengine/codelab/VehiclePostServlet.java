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
import java.util.ArrayList;
import java.util.List;
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
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * This servlet responds to the request corresponding to users. The class
 * creates and manages the User Entity
 * 
 * @author
 */
@SuppressWarnings("serial")
public class VehiclePostServlet extends BaseServlet {

  private static final Logger logger = Logger.getLogger(VehiclePostServlet.class.getCanonicalName());

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


    if(!"0".equals(postId)) {
    	getSingleVehiclePost(req,resp);
    	return;
    }

    resp.setContentType("text/html");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    PrintWriter writer = resp.getWriter();
    Query query = null;
    Key ancestorKey = null;
    PreparedQuery queryString = null;
    Entity vehiclePost = null;
    Key key = null;
/*    
    if(!"".equals(all)) {
    	//results = VehiclePost.getAllVehiclePosts(fetchOptions,offset);
    	//query.addFilter(null, FilterOperator.EQUAL, null);
    }
 */

	 if((companyName==null) || "".equals(companyName)) {
		 isComp = "false";
	 }
	 if((vehicleRegNumber==null) || "".equals(vehicleRegNumber)) {
		 isReg = "false";
	 }		 
	 if("false".equals(isReg) && "false".equals(isComp)) {
		 isAll = "true";
	 }
		 
	 
    if("true".equals(isReg)) {
    	 query = new Query("VehiclePostAns");
    	 ancestorKey = KeyFactory.createKey("Vehicle", vehicleRegNumber.toLowerCase());
    	    query.setAncestor(ancestorKey);
    	    query.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, ancestorKey);

		 //entities = VehiclePostAns.getAllVehiclePostAns(vehicleRegNumber);
/*
    	 vechicle = Vehicle.getSingleVehicle(vehicleRegNumber);
    	 if(null != vechicle) {
    		 entities = VehiclePostAns.getAllVehiclePostAns(vehicleRegNumber);
    	 }
*/    	 
    }
    else if("true".equals(isComp)) {
    	query = new Query("VehiclePost");
    	query.addFilter("companyName", FilterOperator.EQUAL, companyName);
    	/*
    	 query = new Query("CompanyPostAns");
    	 ancestorKey = KeyFactory.createKey("Company", companyName.toLowerCase());
    	    query.setAncestor(ancestorKey);
    	    query.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, ancestorKey);
    	    */
    	 //entities = CompanyPostAns.getAllCompanyPostAns(companyName);
    	
    }
    else if("true".equals(isAll)) {
   	 query = new Query("VehiclePost");
    }
    
    queryString = datastore.prepare(query);
    
    //queryString = datastore.prepare(query);
    //int rowCount = queryString.countEntities();
    
    // set offset for the results to be fetched
    int offset = 0;
    if (off != null)
      offset = Integer.parseInt(off);
    
    // fetch results from datastore based on offset and page size
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(PAGE_SIZE);
    int rowCount = queryString.countEntities();
    
    QueryResultList<Entity> results = queryString
        .asQueryResultList(fetchOptions.offset(offset));      
     //return (Iterable<Entity>) results;

     
    String pageContent = "<tbody>";

    
    
    //int rowCount = queryString.countEntities(fetchOptions);

    // set the header content
    /*
    StringBuilder header = new StringBuilder("<thead><tr>"
        + "<th scope=\"col\">Order Id</th>"
        + "<th scope=\"col\">Item Name</th>"
        + "<th scope=\"col\">Customer</th>" + "<th scope=\"col\">Quantity</th>"
        + "<th scope=\"col\">Price</th>" + "<th scope=\"col\">Status</th>"
        + "<th scope=\"col\">Mark</th>" + "</tr></thead>");
    writer.println(header);
    */
    // set footer for the table
    float pageCount = (float) rowCount / PAGE_SIZE;
    String footer = "";
    if (pageCount > rowCount / PAGE_SIZE)
      pageCount = (int) pageCount + 1;
    else
      pageCount = (int) pageCount;

    for (int i = 0; i < pageCount; i++) {
      footer += "<a href=\"#\" onclick=\"fillBody(" + i * PAGE_SIZE + ")\">"
          + (i + 1) + "</a>  ";
    }

    if (rowCount > PAGE_SIZE) {
      if ((offset / PAGE_SIZE) != (pageCount - 1))
        footer += "<a href=\"#\" onclick=\"fillBody(" + (offset + PAGE_SIZE)
            + ")\">Next</a>";
      else
        footer += "<a href=\"#\" onclick=\"fillBody(" + (pageCount - 1)
            * PAGE_SIZE + ")\">Next</a>";
    }
    writer.println("<tfoot><tr><td colspan=7 class=\"id\" style=\"text-align:right\">"
            + footer + "</td></tr></tfoot>");
    if ((results == null) || (true == results.isEmpty())) {
        // condition to show message when data is not available
        pageContent += "<tbody><tr><td colspan=7>No records found</td></tr>";
      }
    
    // set the content of the table
    for (Entity entity : results) {
/*    	
      if("true".equals(isAll)) {
    	  vehiclePost = entity;
      }
      else if("true".equals(isReg)) {
    	 key = KeyFactory.createKey("VehiclePost",(Long) entity.getProperty("postKey"));
    	 vehiclePost = Util.findEntity(key);
      }
      else if("true".equals(isComp)) {
     	 key = KeyFactory.createKey("VehiclePost",(Long) entity.getProperty("postKey"));
     	 vehiclePost = Util.findEntity(key);
      }
*/
    	vehiclePost = entity;
      pageContent += "<tr>";
      pageContent += "<td onclick=\"getBody(" + Util.getPostId(vehiclePost) + ")\"> <img src=" + vehiclePost.getProperty("userImg") + "></img> <b>" + "  " + vehiclePost.getProperty("companyName").toString() +"</td>";   
      /*
      pageContent += "<td>" + entity.getKey().getId() + "</td><td>"
          + entity.getProperty("itemName") + "</td><td>"
          + entity.getProperty("customerName") + "</td><td>"
          + entity.getProperty("quantity") + "</td><td>"
          + entity.getProperty("price") + "</td><td>"
          + entity.getProperty("status") + "</td>";
      if (entity.getProperty("status").toString()
          .equalsIgnoreCase("Processing")
          || entity.getProperty("status").toString()
              .equalsIgnoreCase("processed")) {
        pageContent += "<td><INPUT id=\"orders\" NAME=\"orders\" TYPE=\"CHECKBOX\" DISABLED VALUE="
            + entity.getKey().getId() + "></td></tr>";
      } else {
        pageContent += "<td><INPUT id=\"orders\" NAME=\"orders\" TYPE=\"CHECKBOX\" VALUE="
            + entity.getKey().getId() + "></td></tr>";
      }
      */
    }
    pageContent += "</tbody>";
    writer.println(pageContent);
  }
  
  private void getSingleVehiclePost(HttpServletRequest req,
		HttpServletResponse resp) throws IOException {
	  
	    String postId = req.getParameter("postId");
	    String desc;
	    String company;
	    String vehicle;
	    Entity vehiclePost = VehiclePost.getSingleVehiclePost(postId);
	    resp.setContentType("application/json; charset=utf-8");
		resp.setHeader("Cache-Control", "no-cache");
	    
	    PrintWriter writer = resp.getWriter();
	    String pageContent;
/*	    
	    if(null == vehiclePost) {
	    	pageContent = "<tbody><tr><td colspan=7>No records found</td></tr>";
	    	pageContent += "</tbody>";
	        writer.println(pageContent);
	        return;
	    }
	    desc = vehiclePost.getProperty("errorDetails").toString();
	    company =vehiclePost.getProperty("companyName").toString();
	    vehicle = vehiclePost.getProperty("vehicleRegNumber").toString();

	    pageContent = "<script>";
	    pageContent += "document.getElementById('fb-post').onclick = function() {" +
			"fbPost();" +
		"};" +	
		"var fbPost = function() {" +
			"alert('doing post'); " +
	       " var obj = { " +
	          "method: 'feed',"+
	          "link: 'https://developers.facebook.com/docs/reference/dialogs/',"+
	          "picture: 'http://fbrell.com/f8.jpg',"+
	          "name: " + desc +","+
	          "caption: "+vehicle+"," +
	          "description:" + company + "" +
	        "};" +
	        "function callback(response) {"+
	          "document.getElementById('user-show-message').innerHTML = 'Post ID: ' + response['post_id'];"+
	        "}" +
	        "FB.ui(obj, callback);"+
	      "};" +
		"</script>";  
		pageContent += "<button id='fb-post'>Post to Facebook</button>";
		*/
        writer.println(Util.writeJSON(vehiclePost));
        return;
}

	/*
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

    super.doGet(req, resp);
    logger.log(Level.INFO, "Obtaining VehiclePost listing");
    String searchFor = req.getParameter("q");
    String searchForAll = req.getParameter("all");
    String searchForVehiclePost = req.getParameter("companyName");
    String searchForUser = req.getParameter("eMail");
    String searchForRegNum = req.getParameter("vehicleRegNumber");
    Iterable<Entity> entities = null;
	Entity rowCount = new Entity("String");  
    
    PrintWriter out = resp.getWriter();
    

    if( (searchFor != null) &&(!(searchFor.equals("")))) {
        Entity entity = null;
        entity = VehiclePost.getSingleVehiclePost(searchFor);
        out.println(Util.writeJSON(entity));
        return;
    }
    
    if((searchForAll != null) && (!(searchForAll.equals("")))) {
        entities = VehiclePost.getAllVehiclePosts(rowCount);
        out.println(Util.writeJSON(entities));
        return;
    }
    
    if((searchForVehiclePost != null) && (!(searchForVehiclePost.equals("")))) {
        entities = VehiclePost.getVehiclePostforCompany(searchForVehiclePost);
        out.println(Util.writeJSON(entities));
        return;
    }

    if((searchForUser != null) && (!(searchForUser.equals("")))) {
        entities = VehiclePost.getVehiclePostforUser(searchForUser);
        out.println(Util.writeJSON(entities));
        return;
    }
    
    if( (searchForRegNum != null) &&(!(searchForRegNum.equals("")))) {
        entities = VehiclePost.getVehiclePostforRegNum(searchForRegNum);
        out.println(Util.writeJSON(entities));
        return;
    }


    entities = VehiclePost.getAllVehiclePosts(rowCount);
    out.println(Util.writeJSON(entities));

    return;
  }
*/  
	/**
	 * Insert the new vehiclePost
	 */
  
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    logger.log(Level.INFO, "Creating Vehicle Post");
    /*
    String companyName = req.getParameter("companyName");
    String leasedToCompany = req.getParameter("leasedToCompany");
    String vehicleRegNumber = req.getParameter("vehicleRegNumber");
    String VehicleDetails = req.getParameter("vehicleDetails");
    String errorDetails = req.getParameter("errorDetails");
    String state = req.getParameter("state");
    String district = req.getParameter("district");
    String location = req.getParameter("location");
    */
    String errorDetails = req.getParameter("errorDetails");
    String userId = req.getParameter("userId");
    String uniqueId = req.getParameter("uniqueId");
    String date = req.getParameter("date");
    /*
    String returnVal = VehiclePost.createOrUpdateVehiclePost( PostId,companyName, leasedToCompany, vehicleRegNumber, VehicleDetails, errorDetails,
    			state, district, location,userKey,uniqueId,date);
    */
    String returnVal = VehiclePost.createOrUpdateVehiclePost(userId,uniqueId,date,errorDetails);

    resp.setContentType("text/html");
    PrintWriter writer = resp.getWriter();
    writer.println(returnVal);

  }

  
	/**
	 * Delete the Vehicle
	 */
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	  
    String id = req.getParameter("name");
    logger.log(Level.INFO, "Deleting VehiclePost");
    
    
    Key key = KeyFactory.createKey("VehiclePost", Integer.parseInt(id));	
	try {
        //CASCADE_ON_DELETE
	  Iterable<Entity> entities = Util.listChildren("vehiclePostComments", key);
      final List<Key> commentkeys = new ArrayList<Key>();
      for (Entity e : entities) {
    	  commentkeys.add(e.getKey());
      }
      Util.deleteEntity(commentkeys);
      Util.deleteEntity(key);
    } catch (Exception e) {
      String msg = Util.getErrorResponse(e);
      resp.getWriter().print(msg);
    }
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
