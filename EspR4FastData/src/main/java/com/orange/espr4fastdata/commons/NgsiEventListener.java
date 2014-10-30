/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.commons;

import java.util.Calendar;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.mdps.ObservableList;
import com.orange.espr4fastdata.cep.ComplexEventProcessing;
import com.orange.espr4fastdata.cep.EventSinkUrl;
import com.orange.espr4fastdata.cep.EventSinkUrlList;
import com.orange.espr4fastdata.ngsicontext.Ngsi_9_10;
import com.orange.espr4fastdata.ngsicontext.NotifyRecipient;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElement;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementResponseList;
import com.orange.espr4fastdata.oma.ngsidatastructures.EntityId;
import com.orange.espr4fastdata.oma.ngsidatastructures.NotifyContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.RegisterContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.StatusCode;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateActionType;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateContextRequest;

/**
 * This event listener is dedicated to NGSI events.
 * @author Osvaldo Cocucci, Laurent Artusio
 */
public class NgsiEventListener implements StatementAwareUpdateListener {

	private static Logger logger = Logger.getLogger(NgsiEventListener.class);

	public CommonParameter commonParameter = null;
	
	public Ngsi_9_10 ngsi = null;
	public ComplexEventProcessing complexEventProcessing = null;
	
//----------------------------------------------------------------------------------------------------------------------  
	/**
	 * Build a flat XML event data structure from CEP output, attribute by attribute. 
	 * @param eventBean the EventBean instance to convert to an XML String.
	 * @return the XML-converted output event. 
	 */
  @SuppressWarnings("unused")
	private String getProcessedEventAsXmlString(EventBean eventBean) {

    String xmlEvent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    xmlEvent+="<"+eventBean.getEventType().getName()+">\n";      

    for (String s : eventBean.getEventType().getPropertyNames()) 
      xmlEvent += "  <"+s+">"+eventBean.get(s)+"</"+s+">\n";      
   
    xmlEvent+="</"+eventBean.getEventType().getName()+">\n";

    logger.info("Converted event from event bean:\n"+xmlEvent);
    
    return xmlEvent;
  }
//----------------------------------------------------------------------------------------------------------------------
  /**
   * Check if a processed event is an NGSI one.
   * @param eventBean
   * @return true if the event is an NGSI event, false otherwise.
   */
  boolean isNgsi(EventBean eventBean) {

    logger.info("---- BEGIN / About to check if processed event whose type is \""+eventBean.getEventType().getName()+"\" is NGSI " +
                 "or not.");
    
    // Seek for a property that is named "EntityIdId"
    for (String propertyName : eventBean.getEventType().getPropertyNames()) {
      logger.info("Found propertyName \""+propertyName+"\".");
      if(propertyName.equals("entityIdId")) {
        logger.info("Yes, it's NGSI.");
        return true; // Return true when found.
      }
    }
    logger.info("No, it's not NGSI.");
    return false;  // Otherwise return false if not found.
  }  
//----------------------------------------------------------------------------------------------------------------------

  // This method is automatically called when new events are available. It is defined in the Esper UpdateListener 
  // Interface.

  
	public void update(EventBean[] newEvents, 
	                   EventBean[] oldEvents, 
	                   EPStatement epStatement, 
	                   EPServiceProvider epServiceProvider)  {

    logger.info("******** LISTENER TRIGGERED !!! ********");
    logger.info("The concerned statement is \"" + epStatement.getName()+"\".");
    
    try{
      for(EventBean eventBean : newEvents) {

        String updateContextRequestPayload = null;
	      // If the processed event is an NGSI structured event.
	      if(this.isNgsi(eventBean)) {

	        logger.info("  The processed event is an NGSI event.");

   	      // Build the EntityId object that is related to the current event.
  	      EntityId entityId = NgsiEsperHelper.buildEntityIdFromEventBean(eventBean); 

	        logger.info("  Processed event type is \""+eventBean.getEventType().getName()+"\".");

	        logger.info("---------------------------------------------------------------------------------------------");

	        logger.info("  STEP 1: register CEP output entity towards event sink target URLs, if necessary.");          

          logger.info("  Setting the entityId type to \""+eventBean.getEventType().getName()+"\".");
          entityId.setType(eventBean.getEventType().getName());

          logger.info("  About to call \"registerEntityWithEventSinkUrls\".");
          try{

          	String registrationId = null;
          	if(this.ngsi.entityIdTypeIsRegistered(entityId.getId())) {
          		registrationId = this.ngsi.getRegistrationId(entityId.getType());
          	}
          	// Register entity entityId.getId()
            NgsiEsperHelper.registerEntityToEventSinkUrls(
            																			entityId.getId(), 
            																			epStatement.getName(), 
            																			ApplicationWideInstances.complexEventProcessing.getStatements(), 
            																			this.commonParameter.appUrl,
            																			registrationId, 
            																			ngsi.getContextRegistrationListList(),
            																			this.commonParameter.proxyUrl, 
            																			this.commonParameter.proxyPort);
          }
          catch(Exception e) { logger.error(e.getMessage()); }

          logger.info("---------------------------------------------------------------------------------------------");

          logger.info("  STEP 2: create an updateContextRequest String from the raw event bean.");

          ContextElement contextElement = NgsiEsperHelper.buildContextElementFromEventBean(eventBean);

	        ContextElementList contextElementList = new ContextElementList();
	        contextElementList.setList(new ObservableList<ContextElement>());	        
	        contextElementList.getList().add(contextElement);

	        UpdateContextRequest updateContextRequest = new UpdateContextRequest();
	        updateContextRequest.setContextElementList(contextElementList);
	        updateContextRequest.setUpdateAction(UpdateActionType.APPEND);

	        // Get the event sink URL list from the statement from which originate the event that is being processed.
	        EventSinkUrlList eventSinkUrlList = ApplicationWideInstances.complexEventProcessing.
	        		getEventSinkUrls(epStatement.getName());

	        // XML serialization of updateContextRequest before it is sent to target event sink URLs.
          updateContextRequestPayload = Utils.objectToFormattedXmlString(updateContextRequest);
          logger.info("  Built updateContextRequestPayload:\n"+updateContextRequestPayload);
          
          logger.info("---------------------------------------------------------------------------------------------");
          
          logger.info("  STEP 3: send updateContextRequests XML-serialized objects to NGSI-registered event sink target URLs, if there are some.");         

          for(EventSinkUrl eventSinkUrl : eventSinkUrlList.getList()) {
	          logger.info("    Parsing check \""+eventSinkUrl.getTarget()+"\" URI.");

	          if(eventSinkUrl.isRegistered()) { 
	            logger.info("    It appears that \""+eventSinkUrl.getTarget()+"\" URL is a registered event sink target URLs.");	              

              try {
	              // Actually call the target event sink URL.
                Utils.callURL(eventSinkUrl.getTarget() + "/updateContext", 
                							"application/xml;charset=UTF-8",
                		          eventSinkUrl.getHttpMethod(), 
                              updateContextRequestPayload,
                              new StringBuilder(),
                              null,
                              this.commonParameter.proxyUrl, this.commonParameter.proxyPort
                             );
              } 
              catch(Exception e) {logger.info(e.getMessage());}
  	        }
	          else {
	            logger.info("    It appears that \""+eventSinkUrl.getTarget()+
	                         "\" URI is an unregistered event sink target URLs: nothing to be done.");
	          }
	        }
          if(eventSinkUrlList.getList().isEmpty()) logger.info("  No event sink target URLs have been defined: nothing to be done.");
          
          logger.info("---------------------------------------------------------------------------------------------");          
          logger.info("  STEP 4: notify the possible subscribers of the processed entity.");

          List<NotifyRecipient> notifyRecipientList = 
          		ApplicationWideInstances.ngsi.getNotifyRecipientList(entityId.getId());

          if(notifyRecipientList != null) {
          	logger.info("Found "+notifyRecipientList.size()+" recipients that have subscribed to \""+entityId.getId()+"\" entity.");
          	logger.info("About to send a notify context payload to each subscriber, if any.");
          	
          	for(NotifyRecipient notifyRecipient : notifyRecipientList) {
          		NotifyContextRequest notifyContextRequest = new NotifyContextRequest();
          		notifyContextRequest.setSubscriptionId(notifyRecipient.getSubscriptionId());
          		notifyContextRequest.setOriginator( this.commonParameter.appUrl );
          		
          		// Set the ContextElement object
          		notifyContextRequest.setContextResponseList(new ContextElementResponseList()); 
          		notifyContextRequest.getContextResponseList().getContextElementResponse().add(new ContextElementResponse());
          		notifyContextRequest.getContextResponseList().getContextElementResponse().get(0).setContextElement(contextElement);

          		notifyContextRequest.getContextResponseList().getContextElementResponse().get(0).setStatusCode(new StatusCode());
          		notifyContextRequest.getContextResponseList().getContextElementResponse().get(0).getStatusCode().setCode(Response.Status.OK.getStatusCode());
          		notifyContextRequest.getContextResponseList().getContextElementResponse().get(0).getStatusCode().setReasonPhrase(Response.Status.OK.getReasonPhrase());
          		
              try {
	              // Actually call the target event sink URL.
                Utils.callURL(notifyRecipient.getReference(), 
                							"application/xml;charset=UTF-8",
                		          "POST",
                		          Utils.objectToFormattedXmlString(notifyContextRequest),
                		          new StringBuilder(),
                              null,
                              this.commonParameter.proxyUrl, this.commonParameter.proxyPort
                             );
              }
              catch(Exception e) {logger.info(e.getMessage());}
          	}
          }
          else {
          	logger.info("There are no subscribers: nothing to do.");
          }
          logger.info("---------------------------------------------------------------------------------------------");

          logger.info("  STEP 5: locally register the \""+entityId.getId()+"\" output entity along with corresponding \""+entityId.getType()+"\" entity type.");

	        logger.info("  About to check if \""+entityId.getId()+"\" output entity was already registered locally.");
	        
	        if( !this.ngsi.entityIdIdIsRegistered(entityId.getId())) {
	        	
	          logger.info("  NO: \""+entityId.getId()+"\" output entity does not exist. About to create it.");
	          
	          String registrationId = null;
	          if(this.ngsi.entityIdTypeIsRegistered(entityId.getType())) {
	          	registrationId = this.ngsi.getRegistrationId(entityId.getType()); 
	          }

	          RegisterContextRequest registerContextRequest = 
	          		   NgsiEsperHelper.buildRegisterContextRequest(entityId.getId(),	                                                           
	                                                             entityId.getType(), 
	                                                             NgsiEsperHelper.getPropertyListFromEventBean(eventBean),
	                                                             DatatypeFactory.newInstance().newDuration("P1Y"),
	                                                             this.commonParameter.appUrl,
	                                                             registrationId,
	                                                             this.ngsi.getContextRegistrationListList());
    
	          this.ngsi.registerContext(registerContextRequest);
	        }
	        else logger.info("  YES: \""+entityId.getId()+"\" output entity was already registered locally.");

	        logger.info("---------------------------------------------------------------------------------------------");

	        logger.info("  STEP 6: locally backup the generated ContextElement object that is related to the \""+entityId.getId()+"\" entity, whose type is \""+entityId.getType()+"\".");
	        
	        contextElement.setTimeStamp( Calendar.getInstance().getTime() );
	        
	        ApplicationWideInstances.ngsi.getContexElementList().getList().add(contextElement);	        
	      }//----------------------------- NON-NGSI Event ----------------------------------------------------------------
	      else { // TODO
	      	logger.info("Processed event is not a NGSI one:");
	        logger.warn("This feature has not been implemented yet. No action will be performed.");
	      }
	    }
    }
    catch(Exception e) {
    	e.printStackTrace();
    }

    logger.info("---- END update.");
	}  
}