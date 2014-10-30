/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.jaxrsendpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mdps.ObservableList;
import com.mdps.ObservableListPersister;
import com.orange.espr4fastdata.commons.ApplicationWideInstances;
import com.orange.espr4fastdata.commons.Utils;
import com.orange.espr4fastdata.ngsicontext.ContextSubscriptionList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElement;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementList;
import com.orange.espr4fastdata.oma.ngsidatastructures.EntityId;

@Path("/debug")
public class Debug extends JaxRSCommons {
  //private static Logger logger = Logger.getLogger(Debug.class);
  
  @Path("/contextSubscriptionList")
  @GET
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  
  public ContextSubscriptionList getContextSubscriptionList() {
    if(!ApplicationWideInstances.isInitialized) super.init();
    
    return ApplicationWideInstances.ngsi.getContextSubscriptionList();
  }
  
  @Path("/debug")
  @GET  
  public void debug() {

  	ContextElement contextElement = new ContextElement();

  	EntityId entityId = new EntityId();
  	entityId.setId("tryEntity");
  	entityId.setType("tryType");

  	contextElement.setEntityId(entityId);

  	ContextElementList contextElementList = new ContextElementList();

  	ObservableListPersister observableListPersister = new ObservableListPersister();
  	
  	ObservableList<ContextElement> contextElements = new ObservableList<ContextElement>(observableListPersister);
  	
  	contextElements.add(contextElement);
  	
  	contextElementList.setList(contextElements);  	
  	
  	String serializedContextElement = Utils.objectToFormattedXmlString(contextElementList);
  	serializedContextElement = Utils.objectToFormattedJsonString(contextElementList);  	
  }
}
