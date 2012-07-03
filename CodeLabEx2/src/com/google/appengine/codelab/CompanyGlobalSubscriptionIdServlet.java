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
public class CompanyGlobalSubscriptionIdServlet extends BaseServlet {

  private static final Logger logger = Logger.getLogger(CompanyGlobalSubscriptionIdServlet.class.getCanonicalName());

	/**
	 * Get the requested company entities in JSON format
	 */
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

    super.doGet(req, resp);
    PrintWriter out = resp.getWriter();
    Iterable<Entity> entities = null;
    entities = CompanyGlobalSubscriptionId.getAllIds();
    out.println(Util.writeJSON(entities));
    return;
  }
	/**
	 * Insert the new company
	 */
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    logger.log(Level.SEVERE, "PUT Not Supported");
  }

  
	/**
	 * Delete the company
	 */
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	    logger.log(Level.SEVERE, "DELETE Not Supported");

  }

	/**
	 * Redirect the call to doDelete or doPut method
	 */
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	    logger.log(Level.SEVERE, "POST Not Supported");

  }
}
