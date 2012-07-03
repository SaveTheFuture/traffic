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
public class District {

	/**
	 * Create an entity if it does not exist, else update the existing entity.
	 * The order has header and line item. Both needs to be added in a single transaction.
	 * 
	 * @param districtName
	 *          : Registration Number of District
	 * @throws IOException 
	 */
  public static void createDistrict(String districtName) throws IOException {
		if(null == districtName || "".equals(districtName)) {
			return ;
		}

	String ldistrictName = districtName.toLowerCase().trim();  
    Entity district = District.getDistrict(ldistrictName);
    if(null == district) {
      district = new Entity("District", ldistrictName);
      Util.persistEntity(district);
    }
  }

  public static Entity getDistrict(String districtName) {
    Key key = KeyFactory.createKey("District", districtName);
	return Util.findEntity(key);
  }
  
	/**
	 * Get all the districts
	 * 
	 * @return : list of all districts
	 */
  public static Iterable<Entity> getAllDistricts() {
    Iterable<Entity> entities = Util.listEntities("District", null, null);
    return entities;
  }
  
}