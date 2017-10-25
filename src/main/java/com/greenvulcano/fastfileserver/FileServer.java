/*
 * Copyright (c) 2017, GreenVulcano Open Source Project. All rights reserved.
 *
 * This file is part of the GreenVulcano Fast File-Server tool.
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package com.greenvulcano.fastfileserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class FileServer {
	
	 private static String resourceBaseDir = ".";
	
	 public static void main(String[] args) throws Exception {

		    int serverPort = 8080;
		    resourceBaseDir = ".";
		 
		    try {
		    	if (args!=null && args.length>1) {
		    		serverPort = Integer.valueOf(args[0]);
		    		resourceBaseDir = args[1];
		    	}
		    } catch (Exception e) {
				System.err.println("Failed to process arguments");
				e.printStackTrace();
			}		    
		    
		    System.out.println("Starting Fast File Server for path "+resourceBaseDir+" on port"+serverPort);
		    
	        Server server = new Server(serverPort);

	        ResourceHandler resource_handler = new ResourceHandler();
	       
	        resource_handler.setDirectoriesListed(true);
	        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
	        resource_handler.setResourceBase(resourceBaseDir);

	        ServletHandler servletHandler = new ServletHandler();
	        servletHandler.addServletWithMapping(FileServlet.class, "/media");
	        
	        HandlerList handlers = new HandlerList();
	        handlers.setHandlers(new Handler[] { resource_handler, servletHandler });
	        server.setHandler(handlers);

	        server.start();
	        server.join();
	    }
	 
	 
	 public static class FileServlet extends HttpServlet {
		private static final long serialVersionUID = -2448576110248502999L;

		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			
			String contentType = req.getContentType();
			String userId = req.getParameter("userid");
			
			String mediaName = UUID.randomUUID().toString();
			
			if (userId==null) {
				resp.setStatus(HttpStatus.BAD_REQUEST_400);
			} else if ("application/json".equalsIgnoreCase(contentType)) {
				
				try {
					String jsondata = req.getReader().lines().collect(Collectors.joining("\n"));				
					
					JSONObject media = new JSONObject(jsondata.trim());
										
					
					Path mediaPath = Paths.get(resourceBaseDir, userId, mediaName.concat(".json"));				
					Files.createDirectories(mediaPath.getParent());
					
					Files.write(mediaPath, media.toString().getBytes(), StandardOpenOption.CREATE_NEW);
					resp.addHeader("Location", Paths.get("/",userId, mediaName.concat(".json")).toString());
				} catch (JSONException e) {
					e.printStackTrace();
					resp.setStatus(HttpStatus.UNPROCESSABLE_ENTITY_422);
				}
			} else {
				resp.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE_415);
			}
		}
		
		
		 
	 }

}
