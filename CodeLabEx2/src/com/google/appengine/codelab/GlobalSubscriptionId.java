package com.google.appengine.codelab;

import java.io.IOException;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * This class defines the methods for basic operations of create, update & retrieve
 * for GlobalSubscription Entity.
 * Ancestor : NONE
 *  
 * @author
 *
 */

public class GlobalSubscriptionId {
	  public static Entity createGlobalSubscriptionId(String vehicleRegNumber) throws IOException {
		      Entity globalSubscriptionId = new Entity("GlobalSubscriptionId");
		      globalSubscriptionId.setProperty("vehicleRegNumber", vehicleRegNumber);
		      Util.persistEntity(globalSubscriptionId);
		      return globalSubscriptionId;
	  }
	public static Iterable<Entity> getAllIds() {
	    Iterable<Entity> entities = Util.listEntities("GlobalSubscriptionId", null, null);
	    return entities;
	  }
	
	public static Key getKey(String name) {
	    return KeyFactory.createKey("GlobalSubscriptionId", Integer.parseInt(name));
	  }
	
	public static void deleteIds(String vehicleRegNumber, String gUniqueId) {
		UserVehicleSubscription.deleteListforsubId(gUniqueId);
	    Iterable<Entity> entities = Util.listEntities("GlobalSubscriptionId", "vehicleRegNumber",vehicleRegNumber);
	    Util.deleteEntities(entities);
	}
}
