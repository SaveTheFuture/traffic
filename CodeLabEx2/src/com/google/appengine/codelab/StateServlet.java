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
public class StateServlet extends BaseServlet {

  private static final Logger logger = Logger.getLogger(StateServlet.class.getCanonicalName());

	/**
	 * Get the requested state entities in JSON format
	 */
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

    super.doGet(req, resp);
    logger.log(Level.INFO, "Obtaining State listing");
    String searchFor = req.getParameter("q");
    PrintWriter out = resp.getWriter();
    if (searchFor == null || searchFor.equals("")) {
      Iterable<Entity> entities = null;
      entities = State.getAllStates();
      out.println(Util.writeJSON(entities));
    } else {
      Entity entity = State.getState(searchFor);
      out.println(Util.writeJSON(entity));
    }
    return;
  }
  
	/**
	 * Insert the new state
	 */
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    logger.log(Level.INFO, "Creating State");
    String stateName = req.getParameter("stateName");
    State.createState(stateName);
  }

  
	/**
	 * Delete the state
	 */
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    String stateName = req.getParameter("id");
    String lstateName = stateName.toLowerCase();
    logger.log(Level.INFO, "Deleting State", lstateName);
    Key key = KeyFactory.createKey("State", lstateName);
    Util.deleteEntity(key);
    
    /*
    try {
        //CASCADE_ON_DELETE
      Iterable<Entity> entities = Util.listChildKeys("Order", key);
      final List<Key> orderkeys = new ArrayList<Key>();
      final List<Key> linekeys = new ArrayList<Key>();
      for (Entity e : entities) {
        orderkeys.add(e.getKey());
        Iterable<Entity> lines = Util.listEntities("LineItem", "orderID",String.valueOf(e.getKey().getId()));
        for(Entity en : lines){
          linekeys.add(en.getKey());
        }
      }
      Util.deleteEntity(linekeys);
      Util.deleteEntity(orderkeys);
      Util.deleteFromCache(key);
      Util.deleteEntity(key);
    } catch (Exception e) {
      String msg = Util.getErrorResponse(e);
      resp.getWriter().print(msg);
    }
    */
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
