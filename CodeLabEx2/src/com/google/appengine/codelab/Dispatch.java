package com.google.appengine.codelab;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

public class Dispatch extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
    	String uploadurl=blobstoreService.createUploadUrl("/upload");
    	System.out.println("Creating an "+uploadurl);
		RequestDispatcher rd = req.getRequestDispatcher(uploadurl.substring(uploadurl.indexOf('_')));
		rd.forward(req, res);

    }
}