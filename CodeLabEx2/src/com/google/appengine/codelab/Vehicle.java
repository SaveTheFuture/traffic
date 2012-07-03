package com.google.appengine.codelab;

import java.io.IOException;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 * This class defines the methods for basic operations of create, update & retrieve
 * for Vehicle Entity.
 * Ancestor : User Entity
 *  
 * @author Sachin Gupta
 *  USER--> VEHICLE --> UNIQUEID ---> VEHICLE SUBSCRIPTION
 */

public class Vehicle {
	
/*
 * Creates a vehicle with used as ancestor. 
 * Creates a Unique ID DB with vehicle as ancestor
 */
	
  public static Entity createVehicle(String vehicleRegNumber, String userId ) throws IOException {
	Key  userAncestorKey = User.getUserKey(userId);	
	String lvehicleRegNumber  = vehicleRegNumber.toLowerCase().replace('-', ' ').trim();
    Entity vehicle = Vehicle.getSingleVehicle(lvehicleRegNumber,userAncestorKey);
    if(null == vehicle) {
      vehicle = new Entity("Vehicle", lvehicleRegNumber,userAncestorKey);
      vehicle.setProperty("vehicleReg", vehicleRegNumber);
      vehicle.setProperty("ownerUserId", userId);
      vehicle.setProperty("subId",null);
      Util.persistEntity(vehicle);
      Key vehicleKey  = getVehicleKey(lvehicleRegNumber, userAncestorKey);
      Entity  gId = GlobalSubscriptionId.createGlobalSubscriptionId(lvehicleRegNumber);
      vehicle.setProperty("gUniqueId",gId.getProperty("name").toString());
      Util.persistEntity(vehicle);
      UserVehicleSubscription.createUserVehicleSubscription(userId, vehicle.getProperty("gUniqueId").toString());
    }
    return vehicle;
  }

  public static Entity getSingleVehicle(String vehicleRegNumber, Key userAncestorKey) {	  
	  Key key = Vehicle.getVehicleKey(vehicleRegNumber,userAncestorKey);
	  return Util.findEntity(key);
  }
  
	/**
	 * Get all the vehicles
	 * 
	 * @return : list of all vehicles
	 */
    public static Iterable<Entity> getAllVehicles() {
       Iterable<Entity> entities = Util.listEntities("Vehicle", null, null);
       return entities;
    }


	/**
	 * Get all the vehicles for User
	 * 
	 * @return : list of all vehicles
	 */
    public static Iterable<Entity> getAllVehiclesForUser(String userId) {
		Key  userAncestorKey = User.getUserKey(userId);	
       Iterable<Entity> entities = Util.listChildren("Vehicle", userAncestorKey);
       return entities;
    }

	public static Key getVehicleKey(String vehicleRegNumber, Key userAncestorKey) {
		// TODO Auto-generated method stub
		String lvehicleRegNumber  = vehicleRegNumber.toLowerCase().replace('-', ' ').trim();
	    return KeyFactory.createKey(userAncestorKey,"Vehicle", lvehicleRegNumber);
	}

	public static void deleteVehicle(String vehicleRegNumber, String userId) {
		// TODO Auto-generated method stub
		Key  userAncestorKey = User.getUserKey(userId);
     	Key vehicleKey = Vehicle.getVehicleKey(vehicleRegNumber,userAncestorKey);
     	Entity vehicle = Util.findEntity(vehicleKey);
     	GlobalSubscriptionId.deleteIds(vehicle.getProperty("name").toString(), vehicle.getProperty("gUniqueId").toString());
	    Util.deleteEntity(vehicleKey);
	}
 }