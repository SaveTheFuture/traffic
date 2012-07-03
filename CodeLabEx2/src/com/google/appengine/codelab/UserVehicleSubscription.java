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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 * This class defines the methods for basic operations of create, update & retrieve
 * for UserVehicleSubscription entity
 *  
 * @author
 *
 */

public class UserVehicleSubscription {
  public static void createUserVehicleSubscription(String subUserId, String gUniqueId) throws IOException {
  	  Key gidAncestorKey = GlobalSubscriptionId.getKey(gUniqueId);
  	  Entity globalSubscriptionId = Util.findEntity(gidAncestorKey);
      Entity vehicleSubscription = new Entity("UserVehicleSubscription",gidAncestorKey);    
      vehicleSubscription.setProperty("subUserId", subUserId);
      vehicleSubscription.setProperty("vehicleRegNum", globalSubscriptionId.getProperty("vehicleRegNum").toString());
      vehicleSubscription.setProperty("gUniqueId",gUniqueId);
      Util.persistEntity(vehicleSubscription);
  }



/**
 * Delete single entity
 * @param parent 
 * 
 */  
public static void deleteSingleVehicleSubscription(String subUserId, String gUniqueId) {
    Key gidAncestorKey = GlobalSubscriptionId.getKey(gUniqueId);
    Iterable<Entity> entities = Util.listEntities("UserVehicleSubscription", "subUserId", subUserId, gidAncestorKey);
    Util.deleteEntities(entities);
}


private static Key getVehicleSubscriptionKey(Key gidAncestorKey, String name) {
	    Key key = KeyFactory.createKey(gidAncestorKey,"UserVehicleSubscription", Integer.parseInt(name));
	    return key;
}

public static Iterable<Entity> getAllSubscriptionForUser(String subUserId) {	  
	return Util.listEntities("UserVehicleSubscription", "subUserId",subUserId);
}

public static Iterable<Entity> getListforsubId(String gUniqueId) {
    Key gidAncestorKey = GlobalSubscriptionId.getKey(gUniqueId);
    return Util.listChildren("UserVehicleSubscription", gidAncestorKey);
}

public static void deleteListforsubId(String gUniqueId) {
	Iterable<Entity> entities = getListforsubId(gUniqueId);
	Util.deleteEntities(entities);
}
}
