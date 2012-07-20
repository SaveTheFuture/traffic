package com.google.appengine.codelab;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.activation.DataHandler;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/**
 * This class defines the methods for basic operations of create, update & retrieve
 * for order entity
 *  
 * @author
 *
 */
public class VehiclePost {

	/**
	 * Create an entity if it does not exist, else update the existing entity.
	 * The order has header and line item. Both needs to be added in a single transaction.
	 * @param blobkey 
	 * 
	 * @param companyName
	 *          : 
	 * @param leasedToCompany
	 *          : 	 *          
	 * @param vehicleRegNumber
	 *          : 
	 * @param State
	 *          : 
	 * @param userImg
	 * 
	 * @param numComments
	 * 
	 * @throws IOException 
	 */
  public static String createOrUpdateVehiclePost(String userId, String uniqueId, String date, String errorDetails, String blobkey) throws IOException {
    Entity vehiclePost = null;
    /* Setting the postID to Null for now.
     * No support for Editing/Deleting the Post
     */
    /*
    if(!postId.equals("null")) {
    	vehiclePost = getSingleVehiclePost(postId);
    }

    if(null == vehiclePost) {
    	vehiclePost = new Entity("VehiclePost");
        //Company.createCompany(companyName);
        //Company.createCompany(leasedToCompany);
        //Vehicle.createVehicle(vehicleRegNumber,null,null);
        State.createState(state);
        District.createDistrict(district);
    }

    else {
    	if(0 == CheckVehiclePostUser(vehiclePost,userKey)) {
    		return;
    	}
    }
	
    vehiclePost.setProperty("leasedToCompany", leasedToCompany);
    vehiclePost.setProperty("vehicleRegNumber", vehicleRegNumber);
    vehiclePost.setProperty("VehicleDetails", VehicleDetails);
    vehiclePost.setProperty("state", state);
    vehiclePost.setProperty("district", district);
    vehiclePost.setProperty("location", location);
    
    CompanyPostAns.createCompanyPostAns(key,companyName);
    CompanyPostAns.createCompanyPostAns(key,leasedToCompany);
    VehiclePostAns.createVehiclePostAns(key,vehicleRegNumber);

*/
    vehiclePost = new Entity("VehiclePost");
    Entity companyGlobalSubscriptionId = CompanyGlobalSubscriptionId.getCompanyGlobalSubscriptionId(uniqueId);
    String companyName = companyGlobalSubscriptionId.getProperty("companyName").toString();

    vehiclePost.setProperty("companyName", companyName);
    vehiclePost.setProperty("errorDetails", errorDetails);
    vehiclePost.setProperty("userId", userId);
    vehiclePost.setProperty("numComments", 0);
    vehiclePost.setProperty("userImg",User.getSingleUser(userId).getProperty("image"));
    vehiclePost.setProperty("userName", User.getSingleUser(userId).getProperty("fullName"));
    vehiclePost.setProperty("agreeCount", 0);
    vehiclePost.setProperty("totalCount", 0);
    vehiclePost.setProperty("uniqueId", uniqueId);
    vehiclePost.setProperty("date",date);
    vehiclePost.setProperty("blobkey",blobkey);
    Util.persistEntity(vehiclePost);
    
    //Key key = vehiclePost.getKey();
    
    try {
		sendMail(UserCompanySubscription.getAllUserListforCompany(companyName),vehiclePost);
	} catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    /*
    try {
		sendMail(UserVehicleSubscription.getAllUserListforVehicle(vehicleRegNumber),vehiclePost);
	} catch (MessagingException e) {
		
		e.printStackTrace();
	}
    */
    return "Thanks For Sharing " + User.getSingleUser(userId).getProperty("fullName").toString() + "!"; 
  }

  
  private static String createMailMessage(Entity vehiclePost) {
	  String companyName = vehiclePost.getProperty("companyName").toString();
	  String mailBody = "";
	  mailBody += "<tr>";
	  mailBody += "<td><label> Company Name : " + companyName + "</label></td>";
	  mailBody += "<td> <label> Posted By " + vehiclePost.getProperty("userName").toString() + " </label> " ;
	  mailBody += " <img src=" + vehiclePost.getProperty("userImg").toString() + "></img> </td>";
	  mailBody += "<td> <label>" + vehiclePost.getProperty("errorDetails").toString() + "</label></td>";
	  mailBody += "</tr>";
	  return mailBody;
  }
  
  private static void sendMail(Iterable<Entity> entities,Entity vehiclePost) throws MessagingException
  {
	  if(null == entities) {
		  return;
	  }
	  String companyName = vehiclePost.getProperty("companyName").toString();
	  String htmlBody  = createMailMessage(vehiclePost);  
      Multipart mp = new MimeMultipart();
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      int count = 0;
      MimeBodyPart htmlPart = new MimeBodyPart();
      try {
		htmlPart.setContent(htmlBody, "text/html");
	} catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      try {
          mp.addBodyPart(htmlPart);
	} catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     try {
          Message msg = new MimeMessage(session);
          try {
			msg.setFrom(new InternetAddress("admin@savethefuture.in", "Save The Future Team"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	  for (Entity result : entities) {
    		  Entity user = User.getSingleUser(result.getProperty("userId").toString());    		  
    		  try {
    			  if(user != null) {
    				  count++;  
    				  msg.addRecipient(Message.RecipientType.TO,
				          new InternetAddress(user.getProperty("eMail").toString(), user.getProperty("fullName").toString()));
    			  }
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	  }
    	  if(count!= 0) {
    		  msg.setSubject("A Traffic Violation has been Reported for " + companyName);
    		  msg.setContent(mp);
    		  Transport.send(msg);
    	  }

      } catch (AddressException e) {
          // ...
      } catch (MessagingException e) {
          // ...
      }
  }
  
  public static Integer CheckVehiclePostUser(Entity vehiclePost,String userKey) {
		// TODO Auto-generated method stub
		if(userKey.equalsIgnoreCase(vehiclePost.getProperty("userKey").toString())) {
			return 1;
		}
		return 0;
	}

public static Iterable<Entity> getVehiclePostforCompany(FetchOptions fetchOptions,Integer offset,String companyName) {
	  Iterable<Entity> entities = Util.listEntitieswithPagination("VehiclePost", "companyName", companyName.toLowerCase(),fetchOptions,offset);
	  return entities;

  }

  public static Iterable<Entity> getVehiclePostforUser(String userKey) {
	  Iterable<Entity> entities = Util.listEntities("VehiclePost", "userKey", userKey);
	  return entities;

  }

  public static Iterable<Entity> getVehiclePostforRegNum(String vehicleRegNumber) {
	  Iterable<Entity> entities = Util.listEntities("VehiclePost", "vehicleRegNumber", vehicleRegNumber.toLowerCase());
	  return entities;

  }
	/**
	 * Get all the posts
	 * 
	 * @return : list of all posts
	 */
  public static Iterable<Entity> getAllVehiclePosts() {
    Iterable<Entity> entities = Util.listEntities("VehiclePost", null, null);
    //Util.deleteEntities(entities);      
    return entities;
  }
  
  public static Entity getSingleVehiclePost(String Id) {
	    if(null == Id || Id.equals("")) {
	    	return null;
	    }
	    Key key = KeyFactory.createKey("VehiclePost", Integer.parseInt(Id));
	    return Util.findEntity(key);
  }
}