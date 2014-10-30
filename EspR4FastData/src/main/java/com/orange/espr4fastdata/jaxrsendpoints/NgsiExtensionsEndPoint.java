/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.jaxrsendpoints;

import java.text.ParseException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mdps.ObjectNotFoundException;
import com.orange.espr4fastdata.commons.ApplicationWideInstances;
import com.orange.espr4fastdata.commons.ResponseMessage;
import com.orange.espr4fastdata.commons.Utils;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementList;

@Path("/NgsiExtensions")

public class NgsiExtensionsEndPoint extends JaxRSCommons{

//----------------------------------------------------------------------------------------------------------------------
  
  @Path("/contextElements")
  @GET
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public ContextElementList readContextElements() {
	  if(!ApplicationWideInstances.isInitialized) super.init();
	  
  	return ApplicationWideInstances.ngsiExtensions.readContextElements();
  }
//----------------------------------------------------------------------------------------------------------------------  
  /**
   * Returns all stored context elements that have their timestamp comprised between "stringIso8601TimeStamp" and now.
   * @param stringTimeStamp the timestamp from which to return the requested context elements.
   * @return a ContextElementList object.
   * @throws ObjectNotFoundException
   */
  @Path("/contextElements/{timeStamp}") // All context elements between "timestamp" and now will be returned.
  @GET
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public ContextElementList readContextElements( @PathParam("timeStamp") String stringTimeStamp) {
	  if(!ApplicationWideInstances.isInitialized) super.init();

  	int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the error messages.
    String details = null;

  	try {
  		Utils.getDateFromString(stringTimeStamp, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"); // Check the format conformity
  		
  		Date timeStamp = Utils.getDateFromString(stringTimeStamp, "yyyy-MM-dd'T'HH:mm:ss.SSSz" );
  		
			return ApplicationWideInstances.ngsiExtensions.getContextElements(timeStamp);
		} 
  	catch (ObjectNotFoundException e) {
	    code = Response.Status.NOT_FOUND.getStatusCode(); // Set the error code to 200 (OK) by default.
	    reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase(); // Same as above, but for the error messages.
	    details = e.getMessage();
		}
  	catch (ParseException e) {
	    code = Response.Status.BAD_REQUEST.getStatusCode(); // Set the error code to 200 (OK) by default.
	    reasonPhrase = Response.Status.BAD_REQUEST.getReasonPhrase(); // Same as above, but for the error messages.
	    details = "Wrong timestamp format.\nThe requested format is extended iso8601 that conforms the \"yyyy-MM-dd'T'HH:mm:ss.SSSz\" pattern.\nValid timestamp example: 20140516T152312234+0200";
		}
  	
    // If this code is executed, it means that an exception was fired within the try section.
    throw new WebApplicationException(Response.status(code).
    		entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());    
  }
//----------------------------------------------------------------------------------------------------------------------
}
