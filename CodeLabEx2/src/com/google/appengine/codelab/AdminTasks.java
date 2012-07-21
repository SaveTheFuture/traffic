package com.google.appengine.codelab;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class AdminTasks extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {

		if (req.getParameter("secret-key").toString().equalsIgnoreCase("taskUnwantedBlobDelete")) {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

			ArrayList<String> reqblobs = new ArrayList<String>();

			for (Entity result : VehiclePost.getAllVehiclePosts()) {
				Map<String, Object> mp = result.getProperties();
				for (String key : mp.keySet()) {
					System.out.println(key + ":" + mp.get(key));
					if (key.equalsIgnoreCase("blobkey")) {
						reqblobs.add(mp.get(key).toString().replaceAll("\\r\\n|\\r|\\n", " ").trim());
					}
				}
			}
			System.out.println(reqblobs);
			Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
			while (iterator.hasNext()) {
				BlobKey bk = iterator.next().getBlobKey();
				if (!reqblobs.contains(bk.getKeyString())) {
					blobstoreService.delete(bk);
					System.out.println("Deleted "+bk.getKeyString());
				}
			}
		}

	}
}