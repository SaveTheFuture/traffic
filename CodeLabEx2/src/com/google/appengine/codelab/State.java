package com.google.appengine.codelab;


import java.io.IOException;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 * This class defines the methods for basic operations of create, update & retrieve
 * for order entity
 *  
 * @author
 *
 */
public class State {

	/**
	 * Create an entity if it does not exist, else update the existing entity.
	 * The order has header and line item. Both needs to be added in a single transaction.
	 * 
	 * @param stateName
	 *          : Registration Number of State
	 * @throws IOException 
	 */
  public static void createState(String stateName) throws IOException {
	if(null == stateName || "".equals(stateName)) {
		return;
	}
	String lstateName = stateName.toLowerCase().trim();  
    Entity state = State.getState(lstateName);
    if(null == state) {
      state = new Entity("State", lstateName);
      Util.persistEntity(state);
    }
  }

  public static Entity getState(String stateName) {
	  
    Key key = KeyFactory.createKey("State", stateName);
	return Util.findEntity(key);
  }
  
	/**
	 * Get all the states
	 * 
	 * @return : list of all states
	 */
  public static Iterable<Entity> getAllStates() {
    Iterable<Entity> entities = Util.listEntities("State", null, null);
    return entities;
  }
  
}