package com.google.appengine.codelab;


import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

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

public class Company {

	/**
	 * Create an entity if it does not exist, else update the existing entity.
	 * The order has header and line item. Both needs to be added in a single transaction.
	 * 
	 * @param companyName
	 *          : Registration Number of Company
	 * @throws IOException 
	 */
	
  public static Entity createCompany(String companyName, String subscriptionCount, String userId) throws IOException {
	Integer i = 0;
	//Key  userAncestorKey = User.getUserKey(userId);	
	String lcompanyName = companyName.toLowerCase().replace('-', ' ').trim();  
    Entity company = Company.getCompany(lcompanyName);
    Integer count = Integer.parseInt(subscriptionCount);
    if(null == company) {
      Entity sticker =	StickerName.createStickerName(lcompanyName);
      String stickerName = sticker.getProperty("companyName").toString();
      company = new Entity("Company", lcompanyName);
      company.setProperty("companyName", companyName);
      company.setProperty("vehicleCount",subscriptionCount);  
      company.setProperty("origUserId",userId);
      company.setProperty("stickerName",stickerName);    
      company.setProperty("validated","true");
      Util.persistEntity(company);
	  //CompanyGlobalSubscriptionId.createCompanyGlobalSubscriptionId(lcompanyName, lcompanyName.concat("All"));
      for (i=1;i<count+1;i++) {
    	  CompanyGlobalSubscriptionId.createCompanyGlobalSubscriptionId(companyName, stickerName.concat("-"+i));
      }
      UserCompanySubscription.createUserCompanySubscription(userId, companyName);
      return company;

    } else if(company.getProperty("origUserId").toString().equals(userId)){
    	Integer oldCount = Integer.parseInt(company.getProperty("vehicleCount").toString());
    	Integer newCount  = count + oldCount;
        String stickerName = company.getProperty("stickerName").toString();
        company.setProperty("vehicleCount",newCount.toString());
        Util.persistEntity(company);
        for (i=oldCount;i<newCount+1;i++) {
      	  CompanyGlobalSubscriptionId.createCompanyGlobalSubscriptionId(lcompanyName, stickerName.concat("-"+i));
        }
        return company;
    }
    return null;
  }

  public static Entity getCompany(String companyName) {
	Key companyKey = getCompanyKey(companyName);
	return Util.findEntity(companyKey);
  }

  public static Key getCompanyKey(String companyName) {
	String lcompanyName = companyName.toLowerCase().replace('-', ' ').trim();  
    return KeyFactory.createKey("Company", lcompanyName);
  }
  
  public static Iterable<Entity> getAllCompanys() {
    Iterable<Entity> entities = Util.listEntities("Company", null, null);
    return entities;
  }

  public static Iterable<Entity> getAllCompanysforUser(String userId) {
	    Iterable<Entity> entities = Util.listEntities("Company", "origUserId", userId);
	    return entities;
  }
  
  public static void deleteCompany(String companyName) {
     	Key companyKey = Company.getCompanyKey(companyName);
     	Entity company = Util.findEntity(companyKey);
     	CompanyGlobalSubscriptionId.deleteAllIdsForCompany(companyKey.getName());
     	StickerName.deleteStickerName(company.getProperty("stickerName").toString());
     	UserCompanySubscription.deleteSingleCompanySubscription(company.getProperty("origUserId").toString()+companyKey.getName());
	    Util.deleteEntity(companyKey);
	}
  

  public static String writeJSON(Iterable<Entity> entities) {
	    StringBuilder sb = new StringBuilder();
	    int i = 0;
	    sb.append("{\"data\": [");
	    for (Entity result : entities) {
	      Map<String, Object> properties = result.getProperties();
	      sb.append("{");
	      if (result.getKey().getName() == null)
	        sb.append("\"name\" : \"" + result.getKey().getId() + "\",");
	      else
	        sb.append("\"name\" : \"" + result.getKey().getName() + "\",");

	      for (String key : properties.keySet()) {
	    	  if(!(key.equals("origUserId"))) {
	    		  sb.append("\"" + key + "\" : \"" + properties.get(key) + "\",");
	    	  }
	      }
	      sb.deleteCharAt(sb.lastIndexOf(","));
	      sb.append("},");
	      i++;
	    }
	    if (i > 0) {
	      sb.deleteCharAt(sb.lastIndexOf(","));
	    }
	    sb.append("]}");
	    return sb.toString();
	  }
  public static String writeJSON(Entity result) {
	  StringBuilder sb = new StringBuilder();
	  sb.append("{\"data\": [");
	    Map<String, Object> properties = result.getProperties();
	    sb.append("{");
	    if (result.getKey().getName() == null)
	      sb.append("\"name\" : \"" + result.getKey().getId() + "\",");
	    else
	      sb.append("\"name\" : \"" + result.getKey().getName() + "\",");

	    for (String key : properties.keySet()) {
	    	  if(!(key.equals("origUserId"))) {
	    		  sb.append("\"" + key + "\" : \"" + properties.get(key) + "\",");
	    	  }
	    }
	    sb.deleteCharAt(sb.lastIndexOf(","));
	    sb.append("}");
	  sb.append("]}");
	  return sb.toString();
	}
}