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
public class AdminPanelServlet extends BaseServlet {

private static final Logger logger = Logger.getLogger(AdminPanelServlet.class.getCanonicalName());

	/**
	 * Get the requested user entities in JSON format
	 */
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    super.doGet(req, resp);
    PrintWriter out = resp.getWriter();
    Iterable<Entity> entities = null;

	String entity = req.getParameter("entity");	  
	if ("company".equalsIgnoreCase(entity)) {
		entities = Company.getAllCompanysToValidate();
	    out.println(Company.writeJSON(entities));
	}
  }

	/**
	 * Insert the new user
	 */
protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	  String entity = req.getParameter("entity");	  
	  if ("company".equalsIgnoreCase(entity)) {
		  Company.handleAdminPanel(req);
	  }
  }

  
/**
* Delete the user
*/
protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	  String entity = req.getParameter("entity");	  
	  if ("company".equalsIgnoreCase(entity)) {
		  String companyName = req.getParameter("companyName");
		  Company.deleteCompany(companyName);
	  }
}

	/**
	 * Redirect the call to doDelete or doPut method
	 */
 protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    String action = req.getParameter("action");
    if(null == action) {
    	action = "put";
    }
    if (action.equalsIgnoreCase("delete")) {
      doDelete(req, resp);
      return;
    } else if (action.equalsIgnoreCase("put")) {
      doPut(req, resp);
      return;
    }
    return;
  }
}