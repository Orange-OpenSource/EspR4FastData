/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.jaxrsendpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.orange.espr4fastdata.commons.ApplicationWideInstances;
import com.orange.espr4fastdata.commons.Utils;
import com.orange.espr4fastdata.oma.ngsidatastructures.RegisterContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.RegisterContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.StatusCode;

@Path("/NGSI9")
public class BackwardCompNgsi9EndPoint extends JaxRSCommons{

  private static Logger logger = Logger.getLogger(BackwardCompNgsi9EndPoint.class);

//----------------------------------------------------------------------------------------------------------------------
  @Path("/registerContext")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public RegisterContextResponse registerContextEndPoint(String registerContextRequestString) {
  	if(!ApplicationWideInstances.isInitialized) super.init();

  	logger.debug("The following payload was received: \n"+registerContextRequestString);
  	
  	RegisterContextRequest registerContextRequest = null;
  	RegisterContextResponse registerContextResponseError = new RegisterContextResponse();
  	
		try {
			registerContextRequest = (RegisterContextRequest) Utils.xmlStringToObject(registerContextRequestString, 
																																								RegisterContextRequest.class);
		} 
		catch (InstantiationException e) {
			registerContextResponseError.setErrorCode(new StatusCode(Response.Status.BAD_REQUEST.getStatusCode(),
						 																									 Response.Status.BAD_REQUEST.getReasonPhrase(),
						 																									 e.getMessage()));   		

   		throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode()).
   							entity(registerContextResponseError).type(MediaType.APPLICATION_XML).build());
		} 
		catch (IllegalAccessException e) {
			registerContextResponseError.setErrorCode(new StatusCode(Response.Status.BAD_REQUEST.getStatusCode(),
   																														 Response.Status.BAD_REQUEST.getReasonPhrase(),
   																														 e.getMessage()));   		

   		throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode()).
   							entity(registerContextResponseError).type(MediaType.APPLICATION_XML).build());
		} 
		catch (JAXBException e) {
			registerContextResponseError.setErrorCode(new StatusCode(Response.Status.BAD_REQUEST.getStatusCode(),
						 																									 Response.Status.BAD_REQUEST.getReasonPhrase(),
						 																									 e.getMessage()));   		

   		throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode()).
   							entity(registerContextResponseError).type(MediaType.APPLICATION_XML).build());
		}
  	
  	RegisterContextResponse registerContextResponse =
    		ApplicationWideInstances.ngsi.registerContext(registerContextRequest); 

   	return registerContextResponse;    	
  }
//----------------------------------------------------------------------------------------------------------------------
  @Path("/contextEntities/{entityId}")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public RegisterContextResponse registerEntityIdContext(RegisterContextRequest registerContextRequest, 
                                                         @PathParam("entityId") String entityId) {   
	  if(!ApplicationWideInstances.isInitialized) super.init();
	  logger.debug("POST REGISTERCONTEXT entityId convenience function"+entityId);
	  // TODO input and return structure required
	  // registerProviderRequest - registerProviderResponse
	  return ApplicationWideInstances.ngsi.registerContext(registerContextRequest);
  } 
//----------------------------------------------------------------------------------------------------------------------
  @Path("/contextEntities/{entityId}")
  @GET
  //@Produces(MediaType.APPLICATION_XML)
  @Produces(MediaType.TEXT_PLAIN)
  public String registerEntityIdContext(@PathParam("entityId") String entityId) {
    logger.debug("GET REGISTERCONTEXT entityId convenience function");
    if(!ApplicationWideInstances.isInitialized) super.init();
	  // TODO input and return structure required
	  // registerProviderRequest - registerProviderResponse
    String msg = Response.Status.OK.getReasonPhrase();
    return msg;
  }
//----------------------------------------------------------------------------------------------------------------------
  @Path("/contextEntities/{entityId}/attributes")
  @GET
  //@Produces(MediaType.APPLICATION_XML)
  @Produces(MediaType.TEXT_PLAIN)
  public String registerAttributeContext(@PathParam("entityId") String entityId) {
	    logger.debug("GET REGISTERCONTEXT entityId/attributes convenience function");
	    if(!ApplicationWideInstances.isInitialized) super.init();
		  // TODO input and return structure required
		  // registerProviderRequest - registerProviderResponse
	    String msg = Response.Status.OK.getReasonPhrase();
	    return msg;
  }
//----------------------------------------------------------------------------------------------------------------------
  @Path("/contextEntities/{entityId}/attributes/{attributeName}")
  @GET
  //@Produces(MediaType.APPLICATION_XML)
  @Produces(MediaType.TEXT_PLAIN)
  public String queryAttributeNameContext(@PathParam("entityId") String entityId,
		  			@PathParam("attributeName") String attName) {
	    logger.debug("GET REGISTERCONTEXT entityId/attributes convenience function");
	    if(!ApplicationWideInstances.isInitialized) super.init();
		  // TODO input and return structure required
		  // registerProviderRequest - registerProviderResponse
	    String msg = Response.Status.OK.getReasonPhrase();
	    return msg;
  }
}