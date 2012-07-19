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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * This class defines the methods for basic operations of create, update & retrieve
 * for user entity
 * 
 * @author 
 *
 */
public class User {

	/**
	 * Checks if the entity is existing and if it is not, it creates the entity
	 * else it updates the entity
	 * 
	 * @param id
	 *          : ID of the User
	 * @param provider
	 *          : Provider : Google, Facebook, Twitter etc
	 * @param email
	 *          : email id of user
	 * @param name
	 *          : Name of user   
	 */
  public static void createOrUpdateUser(String id, String provider,String fullName,String email,String image) {
	String key  = id.toLowerCase()+provider.toLowerCase();
    Entity user = getSingleUser(key);
    if (user == null) {
      user = new Entity("User", key);
      user.setProperty("eMail", email);
      user.setProperty("id", id);
      user.setProperty("provider", provider);
      user.setProperty("fullName", fullName);
      user.setProperty("image", image);

      //user.setProperty("userKey", user.getKey());
    } else {
    	/*
    	 * no Updation allowed for user
      if (fullName != null && !"".equals(fullName)) {
        user.setProperty("fullName", fullName);
      }
      if (email != null && !"".equals(email) && !"undefined".equals(email)) {
        user.setProperty("eMail", email);
      }
      */
        if (image != null && !"".equals(image) && !"undefined".equals(image)) {
            user.setProperty("image", image);
          }
    }
    Util.persistEntity(user);
  }

	/**
	 * List all the users available
	 * 
	 * @return an iterable list with all the users
	 */
  public static Iterable<Entity> getAllUsers() {
    Iterable<Entity> entities = Util.listEntities("User", null, null);
    return entities;
  }

  public static Key getUserKey(String userId) {
	    return KeyFactory.createKey("User", userId);
  }

	/**
	 * Searches for a user and returns the entity as an iterable The search is
	 * key based instead of query
	 * 
	 */
  public static Entity getSingleUser(String userKey) {
    Key key = KeyFactory.createKey("User", userKey);
    return Util.findEntity(key);
  }
}