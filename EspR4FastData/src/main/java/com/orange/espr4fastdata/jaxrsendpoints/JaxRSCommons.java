/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.jaxrsendpoints;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.orange.espr4fastdata.commons.ApplicationWideInstances;
import com.orange.espr4fastdata.commons.CommonParameter;
import com.orange.espr4fastdata.commons.ResponseMessage;
import com.orange.espr4fastdata.commons.Utils;

public class JaxRSCommons {
  @Context
  UriInfo uriInfo;
  
  @Context
  ServletContext servletContext;
  
	private static Logger logger = Logger.getLogger(JaxRSCommons.class);

//----------------------------------------------------------------------------------------------------------------------
	
  public void init() {
  	
    String resourceDirectory = servletContext.getRealPath("/")+"WEB-INF/resource";
  	new ApplicationWideInstances(resourceDirectory);    

 		String appUrl = uriInfo.getBaseUri().toASCIIString();        
 		String splittedURL[]=appUrl.split("/");    		
 		String appName = splittedURL[splittedURL.length-1];
  	
    // At first we load from backup files the basic application properties: application name, application URI as seen 
    // from outside, proxy URL and proxy port if any. If these properties have been purposely updated by an 
  	// administrator and backed up, we try at first to load them.
    ApplicationWideInstances.observableListPersister.loadLists();
    
    CommonParameter commonParameter;
    
    // If nothing was loaded... create a common properties object then set its values by default, from servlet context
    if(ApplicationWideInstances.commonParameterList.getList().size() == 0) {
    	logger.info("No common parameter data were found in the storage.");
    	commonParameter = new CommonParameter();

    	commonParameter.setAppName(appName);
    	commonParameter.setAppUrl(appUrl);
    	
    	logger.info("The following default parameters have been set from application context:");
    	logger.info("  commonParameter.appName = "+commonParameter.getAppName());
    	logger.info("  commonParameter.appUrl = "+commonParameter.getAppUrl());
    	
    	// Save the common properties to storage
    	ApplicationWideInstances.commonParameterList.getList().add(commonParameter);
    }
    else { // If something was loaded, fetch the loaded common properties.
    	commonParameter = (CommonParameter) ApplicationWideInstances.commonParameterList.getList().get(0);
    	logger.info("The following common properties parameters have been loaded from storage: ");
    	logger.info("  commonParameter.appName = "+commonParameter.getAppName());
    	logger.info("  commonParameter.appUrl = "+commonParameter.getAppUrl());
    }

    // Following the previous step, if the loaded configuration is empty, then load default app parameters using the 
    // servlet context. These default params often fit the expectations, except the application URL, which can be wrong. 
    // In such case, it will be necessary to manually update it using the EspR4FastData API.    
   	if( commonParameter.getAppName() != null &&
   			commonParameter.getAppUrl() != null &&	
   		 !commonParameter.getAppName().equals("") &&
   		 !commonParameter.getAppUrl().equals("") 
 		 ) {
    		 logger.info("OK: both commonParameter.appName and commonParameter.appUrl are set.");
    	} 
   	else {
   		logger.info("It appears that at least one of the application parameters is still empty.");
   		logger.info("It possibly means that application params were not properly saved before: applications params will be automatically generated from application context.");

   		commonParameter.setAppName(appName);
   		commonParameter.setAppUrl(appUrl);
   		
   		logger.info("commonParameter.appName = "+commonParameter.getAppName()+" and commonParameter.appUrl = "+commonParameter.getAppUrl());
    		
   		logger.info("OK: Successfully created default application params from application context:");
   	}

   	// In all cases, the resource directory is not configurable, and must be persisted to storage.
   	logger.info("In all cases, automatically configure resourceDir, which is not customizable:");
   	commonParameter.setResourceDir(resourceDirectory);   	
 		logger.info("commonParameter.resourceDir = "+commonParameter.getResourceDir());

 		// Init the event listener with the common parameter object. This cannot be done in the ApplicationWideInstances
 		// constructor, because common parameter can be only initialized in the JaxRSCommons class.
 		ApplicationWideInstances.ngsiEventListener.commonParameter = ApplicationWideInstances.commonParameters.get(0);
 		
    // Initialization is now done. Set the global init flag.
    ApplicationWideInstances.isInitialized = true;
    
		// Restore the Esper library state (statements, event types...)
		ApplicationWideInstances.complexEventProcessing.restore();		
  }
//----------------------------------------------------------------------------------------------------------------------

  public Response buildHttpInformationMessageResponse(Response.Status status, String msg) {
    return Response.status(status).entity(Utils.objectToFormattedXmlString(new ResponseMessage(status.getStatusCode(), msg, null))).
           type(MediaType.APPLICATION_XML).build();
  }
}
