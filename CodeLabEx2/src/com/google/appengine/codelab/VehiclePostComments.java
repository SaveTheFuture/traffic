package com.google.appengine.codelab;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class VehiclePostComments {

	/**
	 * Create an entity if it does not exist, else update the existing entity.
	 * The order has header and line item. Both needs to be added in a single transaction.
	 * 
	 * @param comments
	 * @param eMail
	 *          : 
	 */
  public static void createVehiclePostComments(String parentId, String comments, String eMail) throws IOException {

    Key ancestorKey = KeyFactory.createKey("VehiclePost", Integer.parseInt(parentId));
  
    Entity vehiclePostComments = new Entity("vehiclePostComments",ancestorKey);
    vehiclePostComments.setProperty("comment", comments);
    vehiclePostComments.setProperty("eMail", eMail);
    Util.persistEntity(vehiclePostComments);
  }

  	/**
	 * Get all the posts
	 * 
	 * @return : list of all posts
	 */
  public static Iterable<Entity> getAllVehiclePostComments(String parentId) {
	Key ancestorKey = KeyFactory.createKey("VehiclePost", Integer.parseInt(parentId));
	return Util.listChildren("vehiclePostComments", ancestorKey);
  }
}