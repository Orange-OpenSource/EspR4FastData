/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.commons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPException;
import com.orange.espr4fastdata.cep.ComplexEventProcessing;
import com.orange.espr4fastdata.cep.EventTypeAlreadyRegisteredException;
import com.orange.espr4fastdata.cep.NoSuchEventTypeException;
import com.orange.espr4fastdata.cep.VirtualEventException;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistration;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistrationList;

public class NgsiObserver implements Observer {
	
	private static Logger logger = Logger.getLogger(Observer.class);
	
	public ComplexEventProcessing complexEventProcessing;
	
//----------------------------------------------------------------------------------------------------------------------	

	/* The goal of this observer callback method is to provide a clear separation of concern between the Ngsi_9_10 class
	 * and the ComplexEventProcessing Class. When a context registration occurs in an Ngsi_9_10 instance, an Esper event
	 * type must be generated from this registration. But it is not the role of the Ngsi_9_10 class to directly provide 
	 * means to create such event type within a ComplexEventProcessing instance. So the Ngsi_9_10 class is observed, and 
	 * when a context registration occurs, this method is called to generate a XSD event type from registration, and 
	 * then provides Esper with this new XSD event type.
	 */

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	
	public void update(Observable observable, Object argument) {
    
  	@SuppressWarnings("unchecked")
		ArrayList<Object> arrayList = (ArrayList<Object>)argument;
  	Object genericObject = arrayList.get(0);
  	String className = genericObject.getClass().getName();

  	Integer code = Response.Status.OK.getStatusCode();
  	String reasonPhrase = Response.Status.OK.getReasonPhrase();
  	String details = null;

  	if(className.contains("ContextRegistrationList")) {

  		ContextRegistrationList contextRegistrationList = (ContextRegistrationList) genericObject;
  		
  		try {  		  			
  			
  			String createSchemaStatement = null;
  			
  			for(ContextRegistration contextRegistration : contextRegistrationList.getList() ) {
  				
    			createSchemaStatement = NgsiEsperHelper.buildCreateShemaStatement(contextRegistration);
    			
    			String eventTypeName = contextRegistration.getEntityIdList().getEntityId().get(0).getType();
    			
    			// If the event type already exists, and if it's not virtual, it's an update for a previous context registration
    			if( complexEventProcessing.eventTypeExistsInEsper(eventTypeName) && 
    					!complexEventProcessing.getEventType(eventTypeName).isVirtual()) {
    				
    				complexEventProcessing.replaceExistingEventType(eventTypeName, createSchemaStatement, null);
    			}
    			
    			// If the event type does not exist, create it
    			if( !complexEventProcessing.eventTypeExistsInEsper(eventTypeName) ) {
    				
    	      ApplicationWideInstances.complexEventProcessing.createNewEventType(
          			eventTypeName, createSchemaStatement, null);
    			}	
  			}
			} 
      catch (ConfigurationException e) {
      	code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
				reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
				details = e.getMessage();
			} 
      catch (SAXException e) {
      	code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
				reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
				details = e.getMessage();
			} 
      catch (IOException e) {
      	code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
				reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
				details = e.getMessage();
			} 
      catch (ParserConfigurationException e) {
      	code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
				reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
				details = e.getMessage();
			} 
      catch (EPException e) {
      	code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
				reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
				details = e.getMessage();
			} 
      catch (EventTypeAlreadyRegisteredException e) {
      	code = Response.Status.CONFLICT.getStatusCode();
				reasonPhrase = Response.Status.CONFLICT.getReasonPhrase();
				details = e.getMessage();
			} catch (VirtualEventException e) {
      	code = Response.Status.FORBIDDEN.getStatusCode();
				reasonPhrase = Response.Status.FORBIDDEN.getReasonPhrase();
				details = e.getMessage();
			} catch (NoSuchEventTypeException e) {
				logger.error(e.getMessage());			
			}  		
  	}
		arrayList.add(code);
		arrayList.add(reasonPhrase);
		arrayList.add(details);
	}
//----------------------------------------------------------------------------------------------------------------------	
}
