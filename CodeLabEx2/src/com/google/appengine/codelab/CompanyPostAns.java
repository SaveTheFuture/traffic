package com.google.appengine.codelab;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 * This class is used to search company within Vehicle POst. So it key is Vehicle Post Key and ancentor is compoany
 *  
 * @author
 *
 */
public class CompanyPostAns {

	/**
	 * Create an entity if it does not exist, else update the existing entity.
	 * The order has header and line item. Both needs to be added in a single transaction.
	 * 
	 * @param comments
	 * @param eMail
	 *          : 
	 */
  public static void createCompanyPostAns(Key postKey, String company) throws IOException {
		if(null == company || "".equals(company)) {
			return ;
		}

	  String lcompany  = company.replace('-', ' ').trim();
    Entity CompanyPostAns = new Entity("CompanyPostAns",KeyFactory.createKey("Company", lcompany));
    CompanyPostAns.setProperty("postKey",postKey.getId());
    Util.persistEntity(CompanyPostAns);
  }

  	/**
	 * Get all the posts
	 * 
	 * @return : list of all posts
	 */
  public static Iterable<Entity> getAllCompanyPostAns(String  company) {
	  String lcompany  = company.replace('-', ' ').trim();
	  
	Key ancestorKey = KeyFactory.createKey("Company", lcompany);
	return Util.listChildren("CompanyPostAns", ancestorKey);
  }
}