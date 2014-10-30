/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.cep;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.mdps.ObjectNotFoundException;
import com.mdps.StorageNotAvailableException;

public class ComplexEventProcessing {
	
	private static Logger logger = Logger.getLogger(ComplexEventProcessing.class);
	
	private EPServiceProvider epServiceProvider = EPServiceProviderManager.getProvider(null, new Configuration());
	private StatementAwareUpdateListener statementAwareUpdateListener;
	
	private EventTypeList eventTypeList = null;
	private StatementList statementList = null;

//----------------------------------------------------------------------------------------------------------------------
	
	public void setEventTypeList(EventTypeList eventTypeList) { this.eventTypeList = eventTypeList; }
	
//----------------------------------------------------------------------------------------------------------------------
	
	public void setStatementList(StatementList statementList) { this.statementList = statementList; }
	
//----------------------------------------------------------------------------------------------------------------------
/**
* Remove a particular event type 
* @param eventTypeName the name of the event type
* @return an EventTypeList object that features the remaining event types, after deletion.
* @throws NoSuchEventTypeException 
* @throws ObjectNotFoundException 
*/

public synchronized EventTypeList deleteEventType( String eventTypeName ) throws NoSuchEventTypeException, 
																																					  		 ObjectNotFoundException {
	if (this.eventTypeExistsInEsper(eventTypeName)) 
		throw new NoSuchEventTypeException();

	for(int i=0; i < this.eventTypeList.getList().size() ; i++ ) {
		
		EventType eventType = (EventType)this.eventTypeList.getList().get(i);
		
		if( eventType.getName().equals(eventTypeName)) {
			eventTypeList.getList().remove(i);
			break;
		}
	}
		
	// Actually delete the eventTypeName event type within Esper.
	this.epServiceProvider.getEPAdministrator().getConfiguration().removeEventType(eventTypeName, true);

	return this.eventTypeList;
}
//----------------------------------------------------------------------------------------------------------------------
/**
* Delete a particular statement. 
* @param statementName the name of the EPL statement to be deleted.
* @return a Response object.
* @throws StatementNotFoundException 
*/
public synchronized StatementList deleteStatement(String statementName) throws StatementNotFoundException {

	if (this.epServiceProvider.getEPAdministrator().getStatement(statementName) != null) {
		
		this.epServiceProvider.getEPAdministrator().getStatement(statementName).destroy();
    
		// Remove the statement from the global statement list. 
    Statement statementToRemove = null;
		for( Object object : this.statementList.getList() ) {
			
			Statement statement = (Statement)object;
			
      if(statement.getName().equals(statementName)) 
      	statementToRemove = statement;
    }
    this.statementList.getList().remove(statementToRemove);
	} 
	else throw new StatementNotFoundException("You are trying to delete a statement that does not exist.");
		
	return this.statementList;
}
//----------------------------------------------------------------------------------------------------------------------
	public void deleteXsltTransformer(String eventTypename) throws NoSuchEventTypeException {
	
		if(this.eventTypeExistsInEsper(eventTypename) == false) // In case it was not found
			throw new NoSuchEventTypeException();
	
		this.getEventType(eventTypename).setXsltTransformer(null);
  }
//----------------------------------------------------------------------------------------------------------------------
/**
* Check if an event type has already been registered.
* @param eventTypeName the checked event type name.
*/
public synchronized boolean eventTypeExistsInEsper(String eventTypeName) {

	if(this.epServiceProvider.getEPAdministrator().getConfiguration().getEventType(eventTypeName) != null)
		return true;
	
	// The event type name was not found, so return false.
	return false;
}
//----------------------------------------------------------------------------------------------------------------------

public boolean eventTypeExistsInEventTypeList( String eventTypeName ) {
	
	for(com.orange.espr4fastdata.cep.EventType eventType : this.eventTypeList.getList() ) {
		if( eventType.getName().equals(eventTypeName)) {
			return true;
		}
	}
	return false;
}
//----------------------------------------------------------------------------------------------------------------------
/**
* Return the EventSinkUrl object matching the eventSinkUrlName value.
* @param eventSinkUrlName the name of the requested EventSinkUrl object.
* @return an EventSinkUrl object.
* @throws NoSuchStatementException 
* @throws NoSuchEventSinkUrlException 
*/
public synchronized EventSinkUrl getEventSinkUrl( String statementName, String eventSinkUrlName ) 
																																									throws NoSuchStatementException, 
		       																																							 NoSuchEventSinkUrlException {
  
  // Seek the concerned EventSinkUrl object and return it. Otherwise throw an exception, if the statement or the 
  // event sink target URL do not exist. At first, seek the statement that contains the requested EventSinkUrl.
  for(Object object : this.statementList.getList()) {
    
  	Statement statement = (Statement)object;
  	
    if(statement.getName().equals(statementName)) {

      // Nested loop: seek the requested EventSinkUrl by parsing the current statement content and return the result 
      // when it's found.
      for(EventSinkUrl eventSinkUrl : statement.getEventSinkUrlList().getList()) {
          
      	if(eventSinkUrl.getName().equals(eventSinkUrlName)) {
         	return eventSinkUrl;
        }
      }
        
      // This exception is thrown if the for statement has reached its end, which means the EventSinkUrl has not been 
      // found.
      throw new NoSuchEventSinkUrlException();  
    }
  }
  // This exception is thrown if the for statement has reached its end, which means the Statement has not been 
  // found.
  throw new NoSuchStatementException();
}
//----------------------------------------------------------------------------------------------------------------------  
/**
* Return all configured event sinks for a given statement.
* @param statementName
* @return a eventSinkUrlList object
* @throws NoSuchStatementException 
*/
public synchronized EventSinkUrlList getEventSinkUrls(String statementName) throws NoSuchStatementException {

  if (this.epServiceProvider.getEPAdministrator().getStatement(statementName) == null)        
   	throw new NoSuchStatementException();
    
  // Seek the concerned Statement object and return the related EventSinkUrls. Otherwise throw an exception, if the 
  // statement does not exist.
  for(Object object : this.statementList.getList()) {      
		Statement statement = (Statement)object;
  	if(statement.getName().equals(statementName)) 
    	return statement.getEventSinkUrlList();
  }
  throw new NoSuchStatementException();
}
//----------------------------------------------------------------------------------------------------------------------
/**
* Return properties for a given event type.
 * @param epServiceProvider Esper administrative insterface
 * @param eventTypeName The requested event type name.
* @return A PropertyList object.
*/
public synchronized PropertyList getEventTypeProperties(EPServiceProvider epServiceProvider, String eventTypeName) {
  
  PropertyList propertyList = new PropertyList();

  // Parse all deployed event types
  for(EventType eventType : epServiceProvider.getEPAdministrator().getConfiguration().getEventTypes()) {

    // If the parsed current event type is the requested one
    if(eventType.getName().equals(eventTypeName)) {

      // Retrieve all properties for this event type and push them to the propertyList object.
      for(String propertyName : eventType.getPropertyNames()) { 

        String propertyType = eventType.getPropertyType(propertyName).getSimpleName().toLowerCase();

        // Both NGSI metadata names and types are often standardized. For example, timestamps are defined as dateTime.
        // But Esper does not recognize the dateTime type. So it must be set with a if statement, based on the property
        // name. If the following line was removed, the "metadata_timestamp" property would be set as "String".

        if(propertyName.equals("metadata_timestamp")) {
        	propertyType = "dateTime";
        }
        
        propertyList.getProperty().add(new Property(propertyName, propertyType));
        logger.debug("    propertyName="+propertyName+" / type="+ propertyType);
      }
    }
  }  
  return propertyList;
}	
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * @param eventTypeName name of the seeked event type.
	 * @return the seeked event type as an EventType object.
	 * @throws NoSuchEventTypeException in case the seeked event type was not found.
	 */
	public com.orange.espr4fastdata.cep.EventType getEventType(String eventTypeName) 
			throws NoSuchEventTypeException {
		
		for(Object object : this.eventTypeList.getList()) {
			com.orange.espr4fastdata.cep.EventType eventType = (com.orange.espr4fastdata.cep.EventType)object;
			if(eventType.getName().equals(eventTypeName))
				return eventType;
		}
		throw new NoSuchEventTypeException();
	}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Return all the event type names that are currently deployed in the CEP engine. 
	 * @return an EventTypeList object.
	 */
	public synchronized EventTypeList getEventTypes() {
		return this.eventTypeList;
	}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Return the EPL statement matching the statementName value. 
	 * @param statementName EPL statement Name.
	 * @return a Statement object.
	 * @throws NoSuchStatementException 
	 */
	public synchronized Statement getStatement(String statementName) throws NoSuchStatementException {
  
		// Seek the concerned statement and return it
		for(Object object : this.statementList.getList()) {
			Statement statement = (Statement)object;
			if(statement.getName().equals(statementName)) 
				return statement;
		}
		// In case the statement was not found
		throw new NoSuchStatementException();
}
//----------------------------------------------------------------------------------------------------------------------

	public StatementAwareUpdateListener getStatementAwareUpdateListener() {	return this.statementAwareUpdateListener; }

//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Return all the running statements and their properties and event sink URLs.
	 * @return all available statements name in a StatementNameList data structure.
	 */

	public synchronized StatementList getStatements() {
		return this.statementList;
	}
//----------------------------------------------------------------------------------------------------------------------
  
  public String getXsltTransformer(String eventTypeName) throws NoSuchEventTypeException, 
  																															UndefinedXsltException {  	

  	String xsltTransformer = this.getEventType(eventTypeName).getXsltTransformer();
  	
  	if(xsltTransformer == null)
  		throw new UndefinedXsltException();
  	
  	return this.getEventType(eventTypeName).getXsltTransformer();
  }  
//----------------------------------------------------------------------------------------------------------------------

	public void restore() {

		try {	
			
  		// Remove potential virtual event types: they will be automatically generated by statement restoration
			for(com.orange.espr4fastdata.cep.EventType virtualEventType : this.getVirtualEventTypes() )
				this.eventTypeList.getList().remove(virtualEventType);
			
			// Restore non-virtual event types
			for(com.orange.espr4fastdata.cep.EventType eventTypeToRestore : this.eventTypeList.getList())
				this.createNewEventType(eventTypeToRestore.getName(), 
																eventTypeToRestore.toEplCreateSchemaStatement(), 
																eventTypeToRestore.xsltTransformer);			

			// Restore EPL statements
			for(Statement statement : this.statementList.getList()) {
				this.createNewStatement( statement, this.statementAwareUpdateListener );
			}
		}
		catch (ConfigurationException e) { logger.error(e.getMessage());	} 
		catch (EPException e) {	logger.error(e.getMessage()); } 
		catch (SAXException e) { logger.error(e.getMessage());	} 
		catch (IOException e) {	logger.error(e.getMessage()); } 
		catch (ParserConfigurationException e) { logger.error(e.getMessage());	} 
		catch (EventTypeAlreadyRegisteredException e) {	logger.error(e.getMessage()); } 
		catch (NoSuchStatementException e) { logger.error(e.getMessage());	} 
		catch (StatementAlreadyExistException e) { logger.error(e.getMessage()); } 
		catch (MissingInsertIntoClauseException e) { logger.error(e.getMessage()); } 
		catch (StatementCreationException e) { logger.error(e.getMessage()); } 
	}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Returns all virtual event types, which are event types generated automatically from a statement, as opposed to
	 * manually generated event types.
	 * @return an 
	 */
	protected ArrayList<com.orange.espr4fastdata.cep.EventType> getVirtualEventTypes() {
		
		ArrayList<com.orange.espr4fastdata.cep.EventType> virtualEventTypes = 
				new ArrayList<com.orange.espr4fastdata.cep.EventType>();
		
		for( com.orange.espr4fastdata.cep.EventType eventType : this.eventTypeList.getList() ) {
			if(eventType.isVirtual())
				virtualEventTypes.add(eventType);
		}
		return virtualEventTypes;
	}
//----------------------------------------------------------------------------------------------------------------------
/**
 * Delete an Esper instance if it is not already created.  
 * @return ResponseMessage object.
 * @throws ObjectNotFoundException  
 */
  public synchronized void destroyEsper() throws ObjectNotFoundException {

    if(this.epServiceProvider != null) {

      this.epServiceProvider.getEPAdministrator().stopAllStatements();
      this.epServiceProvider.getEPAdministrator().destroyAllStatements();

      while(this.epServiceProvider.getEPAdministrator().getStatementNames().length != 0);

      this.epServiceProvider.initialize();
      this.epServiceProvider.destroy();

      while(!this.epServiceProvider.isDestroyed()); // Wait for destruction      

      this.epServiceProvider = EPServiceProviderManager.getProvider(null, new Configuration());      												 
    }
  }
//----------------------------------------------------------------------------------------------------------------------	
  /**
   * Send an XML event (encoded as a String) to the Esper instance. The String is converted to a DOM document before 
   * being passed to the Esper instance.
   * @param xmlStringEvent A String that contains an XML event.
   * @return A serialized ResponseMessage object.
   * @throws SAXException 
   * @throws ParserConfigurationException 
   * @throws IOException    
   */
  public synchronized void sendXmlEventToEsper(String xmlStringEvent) throws SAXException, 
  																			 																		 ParserConfigurationException, 
  																			 																		 IOException {

  	logger.info("*** About to send this XML event to Esper:\n"+xmlStringEvent);
  	
  	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
  	DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
  	Document xmlDocument = documentBuilder.parse(new InputSource(new StringReader(xmlStringEvent)));

  	String eventTypeName = xmlDocument.getDocumentElement().getNodeName();
	
  	try { // Apply a XSLT transformation, if any.
    	com.orange.espr4fastdata.cep.EventType eventType = this.getEventType(eventTypeName);	    

			if(eventType.getXsltTransformer() != null) { // If a XSLT transformer is configured for this event type...
				// ...then perform a XSLT transformation of the XML source...
				xmlStringEvent = 
					ComplexEventProcessing.transformXmlStringWithXslt(xmlStringEvent, eventType.getXsltTransformer());
				
				// ... then rewrite xmlDocument with the transformed xmlStringEvent
				xmlDocument = documentBuilder.parse(new InputSource(new StringReader(xmlStringEvent))); 
			}
		} 
		catch (NoSuchEventTypeException e) {	
			logger.error(e.getMessage());	
		}

  	xmlDocument.normalize();

  	logger.debug("Event type name is " + eventTypeName);

  	NodeList nodeList = xmlDocument.getDocumentElement().getChildNodes();

  	logger.info("Converting the XML event to HashMap:");

  	HashMap<String, Object> hashMap = new HashMap<String, Object>();

   	for(int i = 0; i < nodeList.getLength(); i++) { 
   		Node node = nodeList.item(i);
   		if(node.getNodeType() == Node.ELEMENT_NODE) {

   			Object property = this.getTypedEsperEventProperty(eventTypeName, node.getNodeName(), node.getTextContent());

   			hashMap.put(node.getNodeName(), property);

   			logger.info(node.getNodeName() + " = " + hashMap.get(node.getNodeName()) + " (type "+ hashMap.get(node.getNodeName()).getClass().getSimpleName()+")");   			
   		}
  	}

    try {
    	// Actually send the XML event to the Esper library
    	this.epServiceProvider.getEPRuntime().sendEvent(hashMap, eventTypeName);
    }
    catch(EPException e) {
    	System.out.println(e.getMessage());
    }
  }
//----------------------------------------------------------------------------------------------------------------------
  
  protected Object getTypedEsperEventProperty( String eventTypeName, String propertyName, String propertyValue ) {
  	
  	String eventPropertyType = this.epServiceProvider.getEPAdministrator().getConfiguration().
  														 	 getEventType(eventTypeName).getPropertyType(propertyName).getSimpleName();  

  	Object typedProperty = null;
  	
  	if(eventPropertyType.equals("Long")) {
			typedProperty = Long.valueOf(propertyValue);
		}
		
		if(eventPropertyType.equals("Double")) {
			typedProperty = Double.valueOf(propertyValue);
		}
		
		if(eventPropertyType.equals("Boolean")) {
			typedProperty = Boolean.valueOf(propertyValue);
		}

		if(eventPropertyType.equals("String")) {
			typedProperty = propertyValue;
		}

  	return typedProperty;
  }
  
//----------------------------------------------------------------------------------------------------------------------
/**
* Create a target URL to be called when a particular statement is triggered.
* @param statementName the related existing statement's name.
* @param EventSinkUrlName the new name that is given to the target event sink URL.
* @param EventSinkUrl an XML-serialized TargetEventSinkUrl object that features the target URI and the HTTP method to
*        use.
* @throws NoSuchEventSinkUrlException
* @return a ResponseMessage XML-serialized object. 
* @throws NoSuchStatementException 
* @throws EventSinkUrlAlreadyExistException 
* @throws StorageNotAvailableException 
*/
public synchronized EventSinkUrl createNewEventSinkUrl( String statementName, 
																												String eventSinkUrlName, 
																												EventSinkUrl newEventSinkUrl)
throws NoSuchEventSinkUrlException, 
	   	 NoSuchStatementException, 
	   	 EventSinkUrlAlreadyExistException { 

  if (this.epServiceProvider.getEPAdministrator().getStatement(statementName) == null) 
   	throw new NoSuchStatementException();

  newEventSinkUrl.setName(eventSinkUrlName);
    
  Statement statement = this.getStatement(statementName);
  
  for(EventSinkUrl eventSinkUrl : statement.getEventSinkUrlList().getList()) {          
  	
  	if(eventSinkUrl.getName().equals(newEventSinkUrl.getName())) 
  		throw new EventSinkUrlAlreadyExistException();
  }
  
  EventSinkUrlList eventSinkUrlList = statement.getEventSinkUrlList();
  eventSinkUrlList.getList().add(newEventSinkUrl);
  
  statement.setEventSinkUrlList(eventSinkUrlList);

  return this.getEventSinkUrl(statementName, newEventSinkUrl.getName());
}
//----------------------------------------------------------------------------------------------------------------------  
/**
* Create a statement into the running Esper instance. The statement name is ideally unique. If a statement with the 
* same name has already been created, the engine appends a "--0" postfix to create a unique statement name.
* @param statementName The EPL statement Name.
* @param statement XML HTTP payload that is mapped to a Statement object that contains the statement.
* @return A StatementList object.
* @throws NoSuchStatementException 
* @throws StatementAlreadyExistException 
* @throws MissingInsertIntoClauseException 
* @throws StatementCreationException 
*/
	public synchronized Statement createNewStatement(Statement statement, 
													 												 StatementAwareUpdateListener statementAwareUpdateListener) 
	
	throws NoSuchStatementException,
	 	   	 StatementAlreadyExistException, 
	 	   	 MissingInsertIntoClauseException, 
	 	   	 StatementCreationException {
		
    // Check if the statement's name does not already exist.
    if( this.statementExistsInEsper(statement.getName()) )
    	throw new StatementAlreadyExistException();
    
    // Extract the object model of the EPL statement.
    EPStatementObjectModel epStatementObjectModel = 
    		this.epServiceProvider.getEPAdministrator().compileEPL(statement.getEpl());

    // Actually create the statement in Esper.
    EPStatement epStatement = 
    		this.epServiceProvider.getEPAdministrator().create(epStatementObjectModel, statement.getName());

    // Check if the statement has been correctly deployed.
    if (this.epServiceProvider.getEPAdministrator().getStatement(epStatement.getName()) == null)
    	throw new StatementCreationException();

    // Create a new listener that will be called each time the new statement is triggered.
    this.epServiceProvider.getEPAdministrator().getStatement(epStatement.getName()).
    addListener(statementAwareUpdateListener);

    Statement newStatement =  new Statement(epStatement.getName(), statement.getEpl());
    String newEventTypeName = null;
    
    if(epStatementObjectModel.getInsertInto() != null) {
    	newEventTypeName = epStatementObjectModel.getInsertInto().getStreamName();    	
    }
    if(epStatementObjectModel.getCreateWindow() != null) {
    	newEventTypeName = epStatementObjectModel.getCreateWindow().getWindowName();
    }
    newStatement.setTargetEntityIdType(newEventTypeName);
    
    logger.debug("New event type \""+newEventTypeName+"\" has been extracted from the statement.");

    // Get the properties for the new event type and assign them to a newStatement object.
    PropertyList propertyList = this.getEventTypeProperties(this.epServiceProvider, newEventTypeName);

    newStatement.setPropertyList(propertyList);
    	
    // In case some target URL event sinks are already defined in the passed statement
    if(statement.getEventSinkUrlList() != null) 
    	newStatement.setEventSinkUrlList(statement.getEventSinkUrlList());

    if( !this.statementExistsInStatementList(newStatement.getName()) )   
    	this.statementList.getList().add(newStatement);

    // Add the new event type that is related to the created statement into the eventTypeList static variable.
    if( !this.eventTypeExistsInEventTypeList(newEventTypeName)) {
    	com.orange.espr4fastdata.cep.EventType eventType = new com.orange.espr4fastdata.cep.EventType();
    	eventType.setName(newEventTypeName);
    	eventType.setPropertyList(propertyList);
    	// A virtual event type is one that is automatically generated by an EPL clause in a statement. It means the
    	// event type has not been explicitly generated by a manual event type creation. In EspR4FastData, each new 
    	// statement instantiation is associated with an automated event type generation.
    	eventType.setVirtual(true);

    	this.eventTypeList.getList().add(eventType);
    }

    return this.getStatement(statement.getName());
}
//----------------------------------------------------------------------------------------------------------------------
	
	public boolean statementExistsInStatementList(String searchedStatementName) {
		
		for(Statement statement : this.statementList.getList() ) {
			if( statement.getName().equals(searchedStatementName))
				return true;
		}
		
		return false;
	}
	
//----------------------------------------------------------------------------------------------------------------------

	public boolean statementExistsInEsper( String searchedStatementName ) {
		
		for(String statementName : this.epServiceProvider.getEPAdministrator().getStatementNames()) {
			if( statementName.equals(searchedStatementName))
				return true;
		}		
		return false;		
	}
	
//----------------------------------------------------------------------------------------------------------------------
	public void setStatementAwareUpdateListener(StatementAwareUpdateListener statementAwareUpdateListener) {
		this.statementAwareUpdateListener = statementAwareUpdateListener;		
	}	
//----------------------------------------------------------------------------------------------------------------------
/**
 * Create a new event type within Esper.
 * @param eventTypeName the event type name.
 * @param createSchemaStatement the statement that contains the EPL syntax to create a new schema (event type).
 * @param epServiceProvider 
 * @return com.orange.espr4fastdata.commons.jaxbmodels.EventType object.
 * @throws SAXException
 * @throws IOException
 * @throws ParserConfigurationException
 * @throws ConfigurationException
 * @throws EPException
 * @throws EventTypeAlreadyRegisteredException  
 */
	public synchronized void createNewEventType( String eventTypeName, 
																							 String createSchemaStatement,
																							 String optionnalXsltTransformer )
	                                          
																																					throws SAXException,
																																								 IOException,
																																								 ParserConfigurationException,
																																								 ConfigurationException,
																																								 EPException, 
																																								 EventTypeAlreadyRegisteredException { 
		logger.debug("*** About to create the "+eventTypeName+" event type");
    // Extract the object model of the EPL statement.
    EPStatementObjectModel epStatementObjectModel = 
    		this.epServiceProvider.getEPAdministrator().compileEPL(createSchemaStatement);

    // Actually create the statement in Esper.
 		this.epServiceProvider.getEPAdministrator().create(epStatementObjectModel);

    // Read the event type that has just been created and add it to the eventTypeList static variable for backup.
    PropertyList propertyList = this.getEventTypeProperties(epServiceProvider, eventTypeName);
    com.orange.espr4fastdata.cep.EventType eventType = new com.orange.espr4fastdata.cep.EventType();
    eventType.setName(eventTypeName);
    eventType.setPropertyList(propertyList);
    // A virtual event type is one that is automatically generated by an INSERT INTO clause in a statement. It means the
    // event type has not been explicitly generated by a manual event type creation. In EspR4FastData, each new 
    // statement instantiation is associated with an automated event type generation.
    eventType.setVirtual(false); // This one is not virtual because the setXsdEventType method explicitly create a new event type.
    eventType.setXsltTransformer(optionnalXsltTransformer);
    
    // If the new event type already exists in the event type list that mirrors the event types in Esper, it means
    // it's a restore (typically at application startup): event types have been loaded from storage, and we restore them
    // in the Esper library, one by one. In such case, we must prevent the new event type to be added to the event type
    // list, because it would be duplicated.
    if(!this.eventTypeExistsInEventTypeList(eventTypeName)) {
    	eventTypeList.getList().add(eventType);
    }
    
    logger.debug("OK: The \""+eventTypeName+"\" event type has been successfully created.");      
	}
//----------------------------------------------------------------------------------------------------------------------
	
	public void replaceExistingEventType( String eventTypeName, 
																	 			String createSchemaStatement, 
																	 			String optionnalXsltTransformer ) throws SAXException,
																	 																					IOException,
																	 																					ParserConfigurationException,
																	 																					ConfigurationException,
																	 																					EPException, 
																	 																					EventTypeAlreadyRegisteredException, 
																	 																					VirtualEventException { 
		try {
			// Remove the updated event type from the event type list...
			com.orange.espr4fastdata.cep.EventType eventTypeTobeReplaced = this.getEventType(eventTypeName);		

			if( eventTypeTobeReplaced.isVirtual() ) {
				throw new VirtualEventException("Virtual events can't be updated: they depend on EPL rule statements.");
			}

			epServiceProvider.getEPAdministrator().getConfiguration().removeEventType(eventTypeName, true);
		
	    // Extract the object model of the EPL statement.
	    EPStatementObjectModel epStatementObjectModel = 
	    		this.epServiceProvider.getEPAdministrator().compileEPL(createSchemaStatement);

	    // Actually create the statement in Esper.
	    this.epServiceProvider.getEPAdministrator().create(epStatementObjectModel);

			// Read the event type that has just been created and add it to the eventTypeList static variable for backup.
			PropertyList propertyList = this.getEventTypeProperties(epServiceProvider, eventTypeName);
		
			com.orange.espr4fastdata.cep.EventType updatedEventType = new com.orange.espr4fastdata.cep.EventType();
			updatedEventType.setName(eventTypeName);
			updatedEventType.setPropertyList(propertyList);
		
			// A virtual event type is one that is automatically generated by an INSERT INTO clause in a statement. It means the
			// event type has not been explicitly generated by a manual event type creation. In EspR4FastData, each new 
			// statement instantiation is associated with an automated event type generation.
			updatedEventType.setVirtual(false); // This one is not virtual because the replaceXsdEventType method explicitly 
			// update a manually created event type.
		
			updatedEventType.setXsltTransformer(optionnalXsltTransformer);
		
			this.eventTypeList.getList().remove( eventTypeTobeReplaced ); 
			this.eventTypeList.getList().add(updatedEventType);
		} 
		catch (NoSuchEventTypeException e) { 
			logger.error(e.getMessage()); 
		}
		
  	logger.debug("OK: The \""+eventTypeName+"\" event type has been successfully updated.");
	}     
//----------------------------------------------------------------------------------------------------------------------
/**
 * Perform the actuel XSLT transformation according to the constructor parameters.
 * @param sourceXmlAsString Source XML to transform.
 * @param xslt XSLT transformer to be used.
 * @return the transformed XML string.
 */
  public static String transformXmlStringWithXslt(String sourceXmlAsString, String xslt) {

  	StringWriter stringWriter = null; // Set a method level StringWriter object for final output string

  	logger.info("Source XML to be transformed:\n"+sourceXmlAsString);
  	
    try{
      // Set and configure a transformer (XML indentation and character set)
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8" + "" );      

      // Convert source XML String to DOM representation.
      DOMResult xmlSourceAsDom = new DOMResult(); // The recipient DOM object for the source XML to be transformed
      transformer.transform(new StreamSource(new StringReader(sourceXmlAsString)), xmlSourceAsDom); 

      // Configure a xslt by passing the XSLT String to a new transformer.
      Transformer xsltTransformer = 
      		TransformerFactory.newInstance().newTransformer( new StreamSource(new java.io.StringReader(xslt)) ); 

      // Perform the actual XSLT transformation to a resulting DOM object.
      DOMResult domResult = new DOMResult(); // Set a DOMResult obj that is recipient for the transformed XML document
      xsltTransformer.transform(new DOMSource(xmlSourceAsDom.getNode()), domResult); 

      // Convert resulting DOM object to StringWriter representation.
      stringWriter = new StringWriter(); // Set a StringWriter Object that will be assigned the resulting String value 
      transformer.transform( new DOMSource(domResult.getNode()), new StreamResult(stringWriter));
    }
    catch(TransformerConfigurationException e) {logger.error(e.getMessage());}
    catch(TransformerException e) {logger.error(e.getMessage());}

    String transformedResult = stringWriter.toString();

    logger.info("Transformed result:\n"+transformedResult);

    return transformedResult;
  }	
//----------------------------------------------------------------------------------------------------------------------
  
  public void createNewXsltTransformer(String eventTypeName, String xslt) throws NoSuchEventTypeException {
  																																				   	
  	// At first, check the eventType existence.
  	if(!this.eventTypeExistsInEsper(eventTypeName))
  		throw new NoSuchEventTypeException();
 	    	
  	// Create and store a new transformer
  	this.getEventType(eventTypeName).setXsltTransformer(xslt);
  } 
//----------------------------------------------------------------------------------------------------------------------
}