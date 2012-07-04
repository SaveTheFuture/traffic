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
 *  Ancestor : CompanyGlobalSubscriptionId
 * @author
 *
 */

public class UserCompanySubscription {
	
  public static void createUserCompanySubscription(String userId, String companyName) throws IOException {
	  Key companyKey = Company.getCompanyKey(companyName);
      Entity companySubscription = new Entity("UserCompanySubscription", userId+companyKey.getName());    
      companySubscription.setProperty("userId", userId);
      companySubscription.setProperty("companyName", companyName);
      Util.persistEntity(companySubscription);
  }

  /*
public static Entity getSingleCompanySubscription(String userId, String companyName) {
	if(null == companyName || "".equals(companyName)) {
		return null;
	}
    String lCompanyName = companyName.toLowerCase();
    Key key = KeyFactory.createKey("UserCompanySubscription", userId+lCompanyName);
    return Util.findEntity(key);
}
*/
  public static int deleteSingleCompanySubscription(String id,String userId) {
		Key key = KeyFactory.createKey("UserCompanySubscription", id);
		Entity userCompanySubscription = Util.findEntity(key);
		String companyName = userCompanySubscription.getProperty("companyName").toString();
		Entity company = Company.getCompany(companyName);
		if(!(company.getProperty("origUserId").toString().equals(userId))) {
			Util.deleteEntity(key);
			return 1;
		}
		return 0;
  }

  public static Iterable<Entity> getAllCompanySubscription() {
    Iterable<Entity> entities = Util.listEntities("UserCompanySubscription", null, null);
    return entities;
  }

  public static Iterable<Entity> getAllSubscriptionForUser(String userId) {
    return Util.listEntities("UserCompanySubscription", "userId",userId);
  }

  public static Iterable<Entity> getAllUserListforCompany(String companyName) {
    return Util.listEntities("UserCompanySubscription", "companyName",companyName);
  }
}
