package com.google.appengine.codelab;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class Serve extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws IOException {
		String blob_key = req.getParameter("blob-key");
		if("undefined".equals(blob_key)) {
			return;
		}
        BlobKey blobKey = new BlobKey(blob_key);
        blobstoreService.serve(blobKey, res);
    }
}