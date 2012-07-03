package com.google.appengine.codelab;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 * This class is used to search vehicle within Vehicle POst. So it key is Vehicle Post Key and ancentor is vehicle
 *  
 * @author
 *
 */
public class VehiclePostAns {

	/**
	 * Create an entity if it does not exist, else update the existing entity.
	 * The order has header and line item. Both needs to be added in a single transaction.
	 * 
	 *          : 
	 */
  public static void createVehiclePostAns(Key postKey, String vehicleRegNumber) throws IOException {
		if(null == vehicleRegNumber || "".equals(vehicleRegNumber)) {
			return ;
		}

	  String lvehicleRegNumber  = vehicleRegNumber.replace('-', ' ').trim();
  
    Entity VehiclePostAns = new Entity("VehiclePostAns",KeyFactory.createKey("Vehicle", lvehicleRegNumber));
    VehiclePostAns.setProperty("postKey",postKey.getId());
    Util.persistEntity(VehiclePostAns);
  }

  	/**
	 * Get all the posts
	 * 
	 * @return : list of all posts
	 */
  public static Iterable<Entity> getAllVehiclePostAns(String  vehicleRegNumber) {
	  String lvehicleRegNumber  = vehicleRegNumber.replace('-', ' ').trim();

	Key ancestorKey = KeyFactory.createKey("Vehicle", lvehicleRegNumber);
	return Util.listChildren("VehiclePostAns", ancestorKey);
  }
}