package com.google.appengine.codelab;


import java.io.IOException;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 *  
 * @author
 *
 */

public class StickerName {

	/**
	 * Create an entity if it does not exist 
	 * @param stickerName
	 *          : Registration Number of Company
	 * @throws IOException 
	 */
  public static Entity createStickerName(String companyName) throws IOException {
	  String lcompanyName = companyName.toLowerCase().replace('-', ' ').trim();  
	  String stickerName = lcompanyName;
      Entity sticker = new Entity("StickerName", stickerName);
      sticker.setProperty("companyName", companyName);
      Util.persistEntity(sticker);
	  //CompanyGlobalSubscriptionId.createCompanyGlobalSubscriptionId(lcompanyName, lcompanyName.concat("All"));
    return sticker;
  }


  public static Entity getStickerName(String sticker) {
	Key stickerKey = getStickerKey(sticker);
	return Util.findEntity(stickerKey);
  }
  
  public static void deleteStickerName(String sticker) {
	Key stickerKey = getStickerKey(sticker);
    Util.deleteEntity(stickerKey);
  }

  public static Key getStickerKey(String sticker) {
    return KeyFactory.createKey("StickerName", sticker);
  }

  public static Iterable<Entity> getAllStickers() {
    Iterable<Entity> entities = Util.listEntities("StickerName", null, null);
    return entities;
  }
}