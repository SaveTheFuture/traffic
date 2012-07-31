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
	 * @param address1 
	 * @param address2 
	 * @param city 
	 * @param state 
	 * @param phone 
	 * @param email 
	 * @throws IOException 
	 */
	
  public static Entity createCompany(String companyName, String subscriptionCount, String userId, String address1, 
		  String address2, String city, String state, String phone, String email) throws IOException {
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
      company.setProperty("validated","false");
      company.setProperty("address1",address1);
      company.setProperty("address2",address2);
      company.setProperty("city",city);
      company.setProperty("state",state);
      company.setProperty("phone",phone);
      company.setProperty("email",email);

      Util.persistEntity(company);
      return company;
    }
    else {
    	return null;
    } 
  }

  public Entity  validateCompany(String companyName) throws IOException {
	  Entity company = getCompany(companyName);
	  if(null == company) {
		  return null;
	  }
	  Integer i = 0;	
	  Integer count = Integer.parseInt(company.getProperty("vehicleCount").toString());	
      String stickerName = company.getProperty("stickerName").toString();
      String userId = company.getProperty("origUserId").toString();
      String lcompanyName = company.getProperty("lcompanyName").toString();

      for (i=1;i<count+1;i++) {
    	  CompanyGlobalSubscriptionId.createCompanyGlobalSubscriptionId(lcompanyName, stickerName.concat("-"+i));
      }
      
      UserCompanySubscription.createUserCompanySubscription(userId, companyName);
      company.setProperty("validated","true");
      Util.persistEntity(company);
      return company;
      
  /*
  else if(company.getProperty("origUserId").toString().equals(userId)){
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
*/
  }

  public Entity  updateCompany(String companyName,String additionCount) throws IOException {
	  Entity company = getCompany(companyName);
	  if(null == company) {
		  return null;
	  }
	  
  	Integer oldCount = Integer.parseInt(company.getProperty("vehicleCount").toString());
  	Integer newCount  = Integer.parseInt(additionCount) + oldCount;
  	Integer i = 0;	
    String lcompanyName = company.getProperty("lcompanyName").toString();
  	
    String stickerName = company.getProperty("stickerName").toString();
    company.setProperty("vehicleCount",newCount.toString());
    Util.persistEntity(company);
    for (i=oldCount;i<newCount+1;i++) {
    	CompanyGlobalSubscriptionId.createCompanyGlobalSubscriptionId(lcompanyName, stickerName.concat("-"+i));
    }
    return company;
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
    Iterable<Entity> entities = Util.listEntities("Company", "validated", "true");
    return entities;
  }

  public static Iterable<Entity> getAllCompanysforUser(String userId) {
	    Iterable<Entity> entities = Util.listEntities("Company", "origUserId", userId,
	    		"validated", "true");
	    return entities;
  }
  
  public static void deleteCompany(String companyName) {
     	Key companyKey = Company.getCompanyKey(companyName);
     	Entity company = Util.findEntity(companyKey);
     	CompanyGlobalSubscriptionId.deleteAllIdsForCompany(companyKey.getName());
     	StickerName.deleteStickerName(company.getProperty("stickerName").toString());
     	UserCompanySubscription.deleteSingleCompanySubscription(company.getProperty("origUserId").toString()+companyKey.getName(),
     			company.getProperty("origUserId").toString());
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