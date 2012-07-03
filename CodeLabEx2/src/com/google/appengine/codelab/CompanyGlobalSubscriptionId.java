package com.google.appengine.codelab;

import java.io.IOException;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class CompanyGlobalSubscriptionId {
	  public static Entity createCompanyGlobalSubscriptionId(String companyName, String idName) throws IOException {
		      Entity companyGlobalSubscriptionId = new Entity("CompanyGlobalSubscriptionId",idName);
		      companyGlobalSubscriptionId.setProperty("companyName", companyName);
		      Util.persistEntity(companyGlobalSubscriptionId);
		      return companyGlobalSubscriptionId;
		    }

	  public static Entity getCompanyGlobalSubscriptionId(String Id) {
		    if(null == Id || Id.equals("")) {
		    	return null;
		    }
		    Key key = KeyFactory.createKey("CompanyGlobalSubscriptionId", Id);
		    return Util.findEntity(key);
	  }
	  
	  public static Key getCompanyKey(String Id) {
		  	Entity companyGlobalSubscriptionId = getCompanyGlobalSubscriptionId(Id);
		  	return companyGlobalSubscriptionId.getParent();
	  }
	  
	public static Iterable<Entity> getAllIds() {
	    Iterable<Entity> entities = Util.listEntities("CompanyGlobalSubscriptionId", null, null);
	    return entities;
	  }

	public static Iterable<Entity> getAllIdsforCompany(String companyName) {
	    Iterable<Entity> entities = Util.listEntities("CompanyGlobalSubscriptionId", "companyName", companyName);
	    return entities;
	  }
	
	public static void deleteAllIdsForCompany(String companyName) {
		Util.deleteEntities(getAllIdsforCompany(companyName));
	}
}
