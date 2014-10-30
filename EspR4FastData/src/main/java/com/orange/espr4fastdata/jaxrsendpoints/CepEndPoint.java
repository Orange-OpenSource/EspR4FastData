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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPException;
import com.mdps.ObjectNotFoundException;
import com.orange.espr4fastdata.cep.EventSinkUrl;
import com.orange.espr4fastdata.cep.EventSinkUrlAlreadyExistException;
import com.orange.espr4fastdata.cep.EventSinkUrlList;
import com.orange.espr4fastdata.cep.EventType;
import com.orange.espr4fastdata.cep.EventTypeAlreadyRegisteredException;
import com.orange.espr4fastdata.cep.EventTypeList;
import com.orange.espr4fastdata.cep.MissingInsertIntoClauseException;
import com.orange.espr4fastdata.cep.NoSuchEventSinkUrlException;
import com.orange.espr4fastdata.cep.NoSuchEventTypeException;
import com.orange.espr4fastdata.cep.NoSuchStatementException;
import com.orange.espr4fastdata.cep.Statement;
import com.orange.espr4fastdata.cep.StatementAlreadyExistException;
import com.orange.espr4fastdata.cep.StatementCreationException;
import com.orange.espr4fastdata.cep.StatementList;
import com.orange.espr4fastdata.cep.StatementNotFoundException;
import com.orange.espr4fastdata.cep.UndefinedXsltException;
import com.orange.espr4fastdata.commons.ApplicationWideInstances;
import com.orange.espr4fastdata.commons.ResponseMessage;

@Path("/cep")
public class CepEndPoint extends JaxRSCommons {

  private static final Logger logger = Logger.getLogger(CepEndPoint.class);
  
//----------------------------------------------------------------------------------------------------------------------
/**
 * Create a target URI to be called when a particular statement triggers.
 * @param statementName the related existing statement's name.
 * @param EventSinkUrlName the new name that is given to the target event sink URL.
 * @param EventSinkUrl an XML-serialized TargetEventSinkUrl object that features the target URI and the HTTP method to
 *        use.
 * @return a ResponseMessage XML-serialized object.
 */
	@POST
	@Path("/statements/{statementName}/eventsinkurls/{eventSinkUrlName}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public EventSinkUrl createEventSinkUrl(
      																	 @PathParam("statementName") String statementName,
      																	 @PathParam("eventSinkUrlName") String eventSinkUrlName,
      																	 EventSinkUrl eventSinkUrl) {
	  
	  if(!ApplicationWideInstances.isInitialized) super.init();
	  
    int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
    String details = null;

	  try {
	  	return ApplicationWideInstances.complexEventProcessing.createNewEventSinkUrl(statementName, 
	  																																				 			 eventSinkUrlName, 
	  																																				 			 eventSinkUrl);
    }
	  catch(NoSuchStatementException e) {
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage();
    }
	  catch (EventSinkUrlAlreadyExistException e) {
      code = Response.Status.CONFLICT.getStatusCode();
      reasonPhrase = Response.Status.CONFLICT.getReasonPhrase();
      details = e.getMessage();
    } 
	  catch (NoSuchEventSinkUrlException e) { 
	  	// This should never happen. This exception is caught because the readEventSinkUrl method is called to return
	  	// the event sink that has just been created. And readEventSinkUrl fires a NoSuchEventSinkUrlException.
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage();
	  }

    // This code is reachable only when an error has occurred and has been caught. 
    throw new WebApplicationException(Response.status(code).
        entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());	
	}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Create an XSD event type into the Esper instance.
	 * @param eventTypeName eventTypeName name of this event type.
	 * @param eventTypeXsdAsString the xml description of the event type.
	 * @return a com.orange.espr4fastdata.cep.EventType object.
	 */
	@Path("/eventTypes/{name}")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public com.orange.espr4fastdata.cep.EventType 
	       createEventType( @PathParam("name") String eventTypeName, String eventTypeXsdAsString) {
    if(!ApplicationWideInstances.isInitialized) super.init();
    
    int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
    String details = null;
    
		try {
		  ApplicationWideInstances.complexEventProcessing.createNewEventType(eventTypeName, eventTypeXsdAsString, null);
		  return (EventType) ApplicationWideInstances.complexEventProcessing.getEventType(eventTypeName);
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
		catch (ConfigurationException e) {
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
    } 
		catch (NoSuchEventTypeException e) {
      code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
      reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
      details = e.getMessage();
		}

    throw new WebApplicationException(Response.status(code).
    		entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());    
	}
//----------------------------------------------------------------------------------------------------------------------
/**
 * Create a statement into the running Esper instance. The statement name is unique.
 * @param statementName The EPL statement Name.
 * @param statement XML HTTP payload that is mapped to a Statement object that contains the statement.
 * @return XML-serialized StatementList structure. 
 */
	
	@Path("/statements/{statementName}")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public  Statement createStatement(@PathParam("statementName") String statementName, Statement statement) {
		
    if(!ApplicationWideInstances.isInitialized) super.init();

    int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = null; // Same as above, but for the info/error messages.
    String details = null;

	  try {
	  	statement.setName(statementName);
	  	return ApplicationWideInstances.complexEventProcessing.createNewStatement( 
	  																																 			statement, 
	  																																 			ApplicationWideInstances.ngsiEventListener);
	  }
	  catch(EPException e) {
      code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
      reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();			 
      details = e.getMessage();
		}
	  catch (StatementCreationException e) {
      code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
      reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
      details = e.getMessage();
    } 
	  catch (MissingInsertIntoClauseException e) {
      code = Response.Status.UNAUTHORIZED.getStatusCode();
      reasonPhrase = Response.Status.UNAUTHORIZED.getReasonPhrase();
      details = e.getMessage();
    } 
	  catch (StatementAlreadyExistException e) {
      code = Response.Status.CONFLICT.getStatusCode();
      reasonPhrase = Response.Status.CONFLICT.getReasonPhrase();
      details = e.getMessage();
    } 
	  catch (NoSuchStatementException e) {
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage();     
		} 

	  // If this code is reached, it means that an exception was fired.
    throw new WebApplicationException(Response.status(code).
          entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());
	}
//----------------------------------------------------------------------------------------------------------------------	
  /**
   * Send an XML event (encoded as a String) to the Esper instance. The String is converted to a DOM document before 
   * being passed to the Esper instance.
   * @param xmlStringEvent A String that contains an XML event.
   * @return A serialized ResponseMessage object.
   */
  @Path("/events")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public ResponseMessage createXmlEvent(String xmlStringEvent) {
    logger.debug("---- BEGIN / createXmlEvent");
    if(!ApplicationWideInstances.isInitialized) super.init();

    int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
    String details = null;

    try {
    	ApplicationWideInstances.complexEventProcessing.sendXmlEventToEsper(xmlStringEvent);
    	return new ResponseMessage(code, reasonPhrase, details); // If ok
    } 
    catch (SAXException e) {
      code = Response.Status.BAD_REQUEST.getStatusCode();
      reasonPhrase = Response.Status.BAD_REQUEST.getReasonPhrase();
      details = e.getMessage();
    } 
    catch (ParserConfigurationException e) {
      code = Response.Status.BAD_REQUEST.getStatusCode();
      reasonPhrase = Response.Status.BAD_REQUEST.getReasonPhrase();
      details = e.getMessage();
    } 
    catch (IOException e) {
      code = Response.Status.BAD_REQUEST.getStatusCode();
      reasonPhrase = Response.Status.BAD_REQUEST.getReasonPhrase();
      details = e.getMessage();
    } 
    catch (EPException e) {
      code = Response.Status.BAD_REQUEST.getStatusCode();
      reasonPhrase = Response.Status.BAD_REQUEST.getReasonPhrase();
      details = e.getMessage();
    } 

    // If this code is executed, it means that an exception was fired
    throw new WebApplicationException(Response.status(code).
    		entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());    
  }
//----------------------------------------------------------------------------------------------------------------------  
  @Path("/eventTypes/{eventTypeName}/xsltTransformer")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public EventType createXsltTransformer(@PathParam("eventTypeName") String eventTypeName, String xsltTransformer) {
	  if(!ApplicationWideInstances.isInitialized) super.init();

	  int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
    String details = null;    

    try {
			ApplicationWideInstances.complexEventProcessing.createNewXsltTransformer(eventTypeName, xsltTransformer);
			return ApplicationWideInstances.complexEventProcessing.getEventType(eventTypeName);
		} 
    catch (NoSuchEventTypeException e) {
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage();
		}

    // If this code is executed, it means that an exception was fired
    throw new WebApplicationException(Response.status(code).
    		entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());    
  }
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Remove a particular event type 
	 * @param eventTypeName the name of the event type
	 * @return an XML serialized Response object
	 */
	
	// TODO: Also remove the NGSI data when this operation is run.
	@Path("/eventTypes/{eventTypeName}")
	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	public EventTypeList deleteEventType(@PathParam("eventTypeName") String eventTypeName) {
	  if(!ApplicationWideInstances.isInitialized) super.init();

	  int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
    String details = null;
    
    try {
    	return ApplicationWideInstances.complexEventProcessing.deleteEventType(eventTypeName);
    }
    catch (NoSuchEventTypeException e) {
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage();
    } 
    catch (ConfigurationException e) {
      code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
      reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();      
      details = e.getMessage();
    } 
    catch (ObjectNotFoundException e) {
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();      
      details = e.getMessage();
		}

    // This code is reachable only when an error has occurred and has been caught. 
    throw new WebApplicationException(Response.status(code).
        entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());
	}
//----------------------------------------------------------------------------------------------------------------------
/**
 * Delete an Esper instance if it is not already created.  
 * @return ResponseMessage object.
 */
  @Path("/instance")
  @DELETE
  @Produces(MediaType.APPLICATION_XML)
  public ResponseMessage deleteInstance() {

    int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
    String details = null;

    if(!ApplicationWideInstances.isInitialized) super.init();    

   	try {
			ApplicationWideInstances.complexEventProcessing.destroyEsper();
		} 
   	catch (ObjectNotFoundException e) {
      code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
      reasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();      
      details = "An error occurred while resetting ComplexEventProcessing object.";
		}    
    return new ResponseMessage(code, reasonPhrase, details); 		   
  }
//----------------------------------------------------------------------------------------------------------------------
/**
 * Delete a particular statement. 
 * @param statementName the name of the EPL statement to be deleted.
 * @return a Response object.
 */
	@Path("/statements/{name}")
	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	public StatementList deleteStatement(@PathParam("name") String statementName) {
    if(!ApplicationWideInstances.isInitialized) super.init();

    int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
    String details = null;

    try {
			return ApplicationWideInstances.complexEventProcessing.deleteStatement(statementName);
		} 
    catch (StatementNotFoundException e) {
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase(); 
      details = e.getMessage();
    }
    throw new WebApplicationException(Response.status(code).
    		entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());    
	}
//----------------------------------------------------------------------------------------------------------------------  
  @Path("/eventTypes/{eventTypeName}/xsltTransformer")
  @DELETE
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public EventType deleteXsltTransformer(@PathParam("eventTypeName") String eventTypeName) {
	  if(!ApplicationWideInstances.isInitialized) super.init();

	  int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
    String details = null;    

    try {
			ApplicationWideInstances.complexEventProcessing.deleteXsltTransformer(eventTypeName);
			return ApplicationWideInstances.complexEventProcessing.getEventType(eventTypeName);
		} 
    catch (NoSuchEventTypeException e) {
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage();
		}

    // If this code is executed, it means that an exception was fired
    throw new WebApplicationException(Response.status(code).
    		entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());    
  }	
//----------------------------------------------------------------------------------------------------------------------
  /**
   * Return the EventSinkUrl object matching the eventSinkUrlName value.
   * @param eventSinkUrlName the name of the requested EventSinkUrl object.
   * @return an EventSinkUrl object.
   */
  @Path("/statements/{statementName}/eventsinkurls/{eventSinkUrlName}")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  public EventSinkUrl readEventSinkUrl(@PathParam("statementName") String statementName,
                                       @PathParam("eventSinkUrlName") String eventSinkUrlName) {
  	if(!ApplicationWideInstances.isInitialized) super.init();
    
    int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 by default.  
    String reasonPhrase = null; // Same as above, but for the error message.
    String details = null;
    
    try {
    	return ApplicationWideInstances.complexEventProcessing.getEventSinkUrl(statementName, eventSinkUrlName);
    }
    catch (NoSuchStatementException e) {
      code = Response.Status.NOT_FOUND.getStatusCode(); 
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage();
    }
    catch (NoSuchEventSinkUrlException e) {
      code = Response.Status.NOT_FOUND.getStatusCode(); 
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage(); 
    }

    // If this code is reached, it means that an exception was fired.
    throw new WebApplicationException(Response.status(code).
              entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());
  }	
//----------------------------------------------------------------------------------------------------------------------
  /**
   * Return all the event sink URLs which are related to a particular EPL statement name.
   * @param statementName the related existing statement's name.
   * @return a StatementActionList XML-serialized object.
   */
  @GET
  @Path("/statements/{name}/eventsinkurls")
  @Produces(MediaType.APPLICATION_XML)
  public EventSinkUrlList readEventSinkUrls(@PathParam("name") String statementName) {

  	if(!ApplicationWideInstances.isInitialized) super.init();

  	int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
  	String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
  	String details = null;

  	try {
  		return ApplicationWideInstances.complexEventProcessing.getEventSinkUrls(statementName);
  	}
  	catch(NoSuchStatementException e) {
  		code = Response.Status.NOT_FOUND.getStatusCode();
  		reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
  		details = e.getMessage();
  	}
  	// If we are here, it means an exception was fired
  	throw new WebApplicationException(Response.status(code).
      entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());
  }
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Return the requested event type. 
	 * @return an EventType object. 
	 */
	@Path("/eventTypes/{name}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public EventType readEventType(@PathParam("name") String name) {
		if(!ApplicationWideInstances.isInitialized) super.init();
  
	  int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
	  String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
	  String details = null;

	  try {
	    return ApplicationWideInstances.complexEventProcessing.getEventType(name);
	  }
	  catch (NoSuchEventTypeException e) {
	    code = Response.Status.NOT_FOUND.getStatusCode();
	    reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
	    details = e.getMessage();
		}
	  // If we are here, it means an exception was fired
	  throw new WebApplicationException(Response.status(code).
	      entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build()); 
	}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Return all the event type names that are currently deployed in the CEP engine. 
	 * @return an EventTypeList object.
	 */
@Path("/eventTypes")
@GET
@Produces(MediaType.APPLICATION_XML)
public EventTypeList readEventTypes() {
  if(!ApplicationWideInstances.isInitialized) super.init();
    
 	return ApplicationWideInstances.complexEventProcessing.getEventTypes(); 
}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Return the EPL statement matching the statementName value. 
	 * @param statementName EPL statement Name.
	 * @return an XML-serialized Statement object.
	 */
@Path("/statements/{statementName}")
@GET
@Produces(MediaType.APPLICATION_XML)
public  Statement readStatement(@PathParam("statementName") String statementName) {
  if(!ApplicationWideInstances.isInitialized) super.init(); 
  
  int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 by default.  
  String reasonPhrase = null; // Same as above, but for the error message.
  String details = null;
  
  try {
  	return ApplicationWideInstances.complexEventProcessing.getStatement(statementName);
  }
  catch (NoSuchStatementException e) {
    code = Response.Status.NOT_FOUND.getStatusCode(); 
    reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
    details = e.getMessage();
  }
  
  // If this code is reached, it means that an exception was fired.   
  throw new WebApplicationException(Response.status(code).
            entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());    
}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Return all the running statements and their properties and event sink URLs.
	 * @return all available statements name in a StatementNameList data structure.
	 */
	@Path("/statements")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public StatementList readStatements() {

	  if(!ApplicationWideInstances.isInitialized) super.init();
    
   	return ApplicationWideInstances.complexEventProcessing.getStatements();
	}
//----------------------------------------------------------------------------------------------------------------------
  @Path("/eventTypes/{eventTypeName}/xsltTransformer")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String readXsltTransformer(@PathParam("eventTypeName") String eventTypeName) {
	  if(!ApplicationWideInstances.isInitialized) super.init();

	  int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the info/error messages.
    String details = null;    

    try {    		
			return ApplicationWideInstances.complexEventProcessing.getXsltTransformer(eventTypeName);
		} 
    catch (NoSuchEventTypeException e) {
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage();
		} 
    catch (UndefinedXsltException e) {
      code = Response.Status.NO_CONTENT.getStatusCode();
      reasonPhrase = Response.Status.NO_CONTENT.getReasonPhrase();
      details = e.getMessage();
		} 
    
    // If this code is executed, it means that an exception was fired
    throw new WebApplicationException(Response.status(code).
    		entity(new ResponseMessage(code, reasonPhrase, details)).type(MediaType.APPLICATION_XML).build());    
  }	
//----------------------------------------------------------------------------------------------------------------------
}

