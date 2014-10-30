/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.jaxrsendpoints;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.mdps.ObjectNotFoundException;
import com.orange.espr4fastdata.commons.ApplicationWideInstances;
import com.orange.espr4fastdata.commons.CommonParameter;
import com.orange.espr4fastdata.commons.ResponseMessage;
import com.orange.espr4fastdata.commons.Utils;
import com.orange.espr4fastdata.ngsicontext.ContextRegistrationListList;
import com.sun.jersey.spi.resource.Singleton;

@Path("/admin")
@Singleton
public class AdminEndPoint extends JaxRSCommons {
  private static final Logger logger = Logger.getLogger(JaxRSCommons.class);  
  private static AdminEndPoint INSTANCE;

//----------------------------------------------------------------------------------------------------------------------
  public AdminEndPoint() {AdminEndPoint.INSTANCE = this;} 
   
  public static AdminEndPoint getInstance() { return AdminEndPoint.INSTANCE; }
  
//----------------------------------------------------------------------------------------------------------------------
  @Path("/espr4fastdata")
  @DELETE
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public synchronized ResponseMessage deleteEspR4FastData() {
  
    if(!ApplicationWideInstances.isInitialized) super.init();
 
		try {
			ApplicationWideInstances.resetWholeApplication();
		} 
		catch (ObjectNotFoundException e) {
      throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).
					entity(new ResponseMessage()).type(MediaType.APPLICATION_XML).build());			
		}

    logger.info("OK: The whole application has been reset.");

    return new ResponseMessage(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), null);        
  }

//----------------------------------------------------------------------------------------------------------------------
  
  @Path("/backupObjects/contextRegistrationLists")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  
  public ContextRegistrationListList readContextRegistrationList() {
  	return ApplicationWideInstances.ngsi.getContextRegistrationListList();
  }
//----------------------------------------------------------------------------------------------------------------------  
  @Path("/commonParameter")
  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  
  public CommonParameter updateCommonProperties(CommonParameter commonParameter) {
    if(!ApplicationWideInstances.isInitialized) super.init();
        
    CommonParameter commonParameterBackup = (CommonParameter)ApplicationWideInstances.commonParameterList.getList().get(0);
    String resourceDirBackup = commonParameterBackup.getResourceDir();

    ApplicationWideInstances.commonParameterList.getList().clear();
    
    commonParameter.setResourceDir(resourceDirBackup);    
    ApplicationWideInstances.commonParameterList.getList().add(commonParameter);

    return (CommonParameter) ApplicationWideInstances.commonParameterList.getList().get(0);
  }  
//----------------------------------------------------------------------------------------------------------------------  
  @Path("/commonParameter")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  
  public CommonParameter getCommonProperties() {
    if(!ApplicationWideInstances.isInitialized) super.init();
    
    return (CommonParameter) ApplicationWideInstances.commonParameterList.getList().get(0);
  }
//----------------------------------------------------------------------------------------------------------------------
  @Path("/log")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  
  public String getLog() throws IOException {
    if(!ApplicationWideInstances.isInitialized) super.init();
    
    String logContent = Utils.readTextContentFromFile("/tmp/EspR4FastData.log");
    
    return logContent;
  }
}
