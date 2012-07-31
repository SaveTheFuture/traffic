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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * This servlet responds to the request corresponding to users. The class
 * creates and manages the User Entity
 * 
 * @author
 */
@SuppressWarnings("serial")
public class CompanyServlet extends BaseServlet {

  private static final Logger logger = Logger.getLogger(CompanyServlet.class.getCanonicalName());

	/**
	 * Get the requested company entities in JSON format
	 */
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

    super.doGet(req, resp);
    String userId = req.getParameter("userId");
    String companyName = req.getParameter("companyName");

    PrintWriter out = resp.getWriter();
    Iterable<Entity> entities = null;
    
    if(!((null==companyName) || "".equals(companyName))) {
    	Entity entity = Company.getCompany(companyName);
        out.println(Company.writeJSON(entity));
        return;
    }

    if((null==userId) || "".equals(userId)) {
    	entities = Company.getAllCompanys();
    }
    else {
    	entities = Company.getAllCompanysforUser(userId);    	
    }
    out.println(Company.writeJSON(entities));
    return;
  }
  
	/**
	 * Insert the new company
	 */
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    logger.log(Level.INFO, "Creating Company");
    String companyName = req.getParameter("companyName");
    String count = req.getParameter("count");
    String userId = req.getParameter("userId");
    String address1 = req.getParameter("address1");
    String address2 = req.getParameter("address2");
    String city = req.getParameter("city");
    String state = req.getParameter("state");
    String phone = req.getParameter("phone");
    String email = req.getParameter("email");
    
    
	if(null == email || "".equals(email) || null==phone || "".equals(phone) || null==state || "".equals(state) || null==city || "".equals(city)
			|| null==address2 || "".equals(address2) || null==address1 || "".equals(address1) || null == userId || "".equals(userId)
			|| null ==companyName || "".equals(companyName) || null==count || "".equals(count)) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
        		"Invalid Parameters");
	}

    if(null == Company.createCompany(companyName,count,userId, address1, address2, city, state, phone, email)) {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
        		"The Company " + companyName + " is already Registered by Someone Else");
    }
  }

  
	/**
	 * Delete the company
	 */
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	logger.log(Level.INFO, "Deleting Company");
    String companyName = req.getParameter("name");
    Company.deleteCompany(companyName);
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
