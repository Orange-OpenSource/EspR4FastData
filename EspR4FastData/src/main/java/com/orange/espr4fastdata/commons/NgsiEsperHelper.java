/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.commons;

import java.io.IOException;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.ProtocolException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.espertech.esper.client.EventBean;
import com.mdps.StorageNotAvailableException;
import com.orange.espr4fastdata.cep.EventSinkUrl;
import com.orange.espr4fastdata.cep.EventSinkUrlList;
import com.orange.espr4fastdata.cep.Property;
import com.orange.espr4fastdata.cep.PropertyList;
import com.orange.espr4fastdata.cep.Statement;
import com.orange.espr4fastdata.cep.StatementList;
import com.orange.espr4fastdata.ngsicontext.ContextRegistrationListList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextAttribute;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextAttributeList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElement;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextMetadata;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextMetadataList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistration;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistrationAttribute;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistrationAttributeList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistrationList;
import com.orange.espr4fastdata.oma.ngsidatastructures.EntityId;
import com.orange.espr4fastdata.oma.ngsidatastructures.EntityIdList;
import com.orange.espr4fastdata.oma.ngsidatastructures.RegisterContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.RegistrationMetadata;

public class NgsiEsperHelper {
	
  private static Logger logger = Logger.getLogger(NgsiEsperHelper.class);

//----------------------------------------------------------------------------------------------------------------------
 /** Builds then returns an EntityId object by using an event bean content.
   * @param eventBean
   * @return
   */
	public static EntityId buildEntityIdFromEventBean(EventBean eventBean) {
    // Create the EntityId object.
    EntityId entityId = new EntityId();
    for(String propertyName : eventBean.getEventType().getPropertyNames()) {
      if(propertyName.equals("entityIdId")) {
      	entityId.setId(eventBean.get(propertyName).toString());
      	entityId.setType(eventBean.getEventType().getName());
      	entityId.setIsPattern(false);
      }
    }
    return entityId; 
  }
//----------------------------------------------------------------------------------------------------------------------
  /**
   * Builds then returns a ContextElement object by using an event bean content.
   * @param eventBean the current event bean.
   * @return a ContextElement object
   * 
   */
  public static ContextElement buildContextElementFromEventBean(EventBean eventBean) {
    // Parse the processed event properties, and build an NGSI-10 ContextAttributeList and a ContextMetadataList
    // objects.
  	EntityId entityId = NgsiEsperHelper.buildEntityIdFromEventBean(eventBean);

    ContextMetadataList contextMetadataList = new ContextMetadataList();
    ContextAttributeList contextAttributeList = new ContextAttributeList();

    for (String propertyName : eventBean.getEventType().getPropertyNames()) { 
      if(propertyName.contains("metadata_") && (eventBean.get(propertyName) != null) ) {
        ContextMetadata contextMetadata = new ContextMetadata();
        contextMetadata.setName(propertyName.substring(9));

        if(eventBean.get(propertyName) != null)
        	contextMetadata.setValue(eventBean.get(propertyName).toString());
        
        String propertyType = eventBean.getEventType().getPropertyType(propertyName).getSimpleName().toLowerCase();

        // Both NGSI metadata names and types are often standardized. For example, timestamps are defined as dateTime.
        // But Esper does not recognize the dateTime type. So it must be set with a if statement, based on the property
        // name. If the following line was removed, the "metadata_timestamp" property would be set as "String".
        if(propertyName.equals("metadata_timestamp")) {
        	propertyType = "dateTime";      	
        }

        contextMetadata.setType(propertyType);
        contextMetadataList.getContextMetadata().add(contextMetadata);

        logger.debug("    Found the following metadata name/type/value : "+propertyName+" / "+
                      contextMetadata.getType()+" / "+contextMetadata.getValue());
      }
      else if( !propertyName.equals("entityIdId") && (eventBean.get(propertyName) != null) ) {
        ContextAttribute contextAttribute = new ContextAttribute();
        contextAttribute.setName(propertyName);
        
       	contextAttribute.setContextValue(eventBean.get(propertyName).toString());
        
        String propertyType = eventBean.getEventType().getPropertyType(propertyName).getSimpleName().toLowerCase();
        contextAttribute.setType(propertyType);
        contextAttributeList.getContextAttribute().add(contextAttribute);

        logger.debug("    Found the following attribute name/type/value : "+propertyName+" / "+
                      contextAttribute.getType()+" / "+contextAttribute.getContextValue());	          
      }
    }

    ContextElement contextElement = new ContextElement();

    contextElement.setEntityId(entityId);
    contextElement.setContextAttributeList(contextAttributeList);
    contextElement.setDomainMetadata(contextMetadataList);

    return contextElement;
  }
//----------------------------------------------------------------------------------------------------------------------
  /**
   * Builds then returns a PropertyList object by using an event bean content.
   * @param eventBean the current event bean.
   * @return a PropertyList object
   */
  public static PropertyList getPropertyListFromEventBean(EventBean eventBean) {
    // Parse the processed event properties, and build an NGSI-10 ContextAttributeList and a ContextMetadataList
    // objects.
  	//EntityId entityId = NgsiEsperHelper.buildEntityIdFromEventBean(eventBean);

    PropertyList propertyList = new PropertyList();
    //propertyList.getProperty().add(new Property(entityId.getId(),"string"));

    for (String propertyName : eventBean.getEventType().getPropertyNames()) { 
      if(propertyName.contains("metadata_")) {	        
        String propertyType = eventBean.getEventType().getPropertyType(propertyName).getSimpleName().toLowerCase();
        propertyList.getProperty().add(new Property(propertyName, propertyType));        
      }
      else if(!propertyName.equals("entityIdId")){
        String propertyType = eventBean.getEventType().getPropertyType(propertyName).getSimpleName().toLowerCase();
        propertyList.getProperty().add(new Property(propertyName, propertyType));        
      }
    }
    return propertyList;  	
  }	
//----------------------------------------------------------------------------------------------------------------------
  /**
   * Create simplified NGSI event type for Esper instance. Each NGSI-9 registerContext call implies the creation of an
   * event type within the Esper instance. Esper has the capability to understand XSD event type definitions, among 
   * other possibilities. The relevant attributes and interesting fields within the raw NGSI data structures are 
   * too deeply embedded in the XML hierarchy to allow simple EPL processing rule writing. This method "flatten" the
   * registerContextRequest XML data structure to keep only relevant fields and to make them easily addressable when
   * writing an EPL rule statement. This implies of course a potential NGSI structure rebuild at Esper output.
   * @param eventTypeList TODO
   * @param contextRegistrationList the ContextRegistration data structure that contains entities to be registered as event
   *        types.
   * @return the full event types list.
   *  
   * @throws SAXException 
   * @throws IOException 
   * @throws ParserConfigurationException 
   */  
  public static String createXSDEsperEventTypeFromNgsiRegistration(ContextRegistration contextRegistration) {

    String ngsiEntityTypeName = contextRegistration.getEntityIdList().getEntityId().get(0).getType();
    ContextRegistrationAttributeList contextRegistrationAttributeList = contextRegistration.getContextRegistrationAttributeList();
    RegistrationMetadata registrationMetadata = contextRegistration.getRegistrationMetadata(); 
    
    String eventTypeXsd = 
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
      "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"unqualified\">\n"+
      "  <xs:element name=\""+ngsiEntityTypeName+"\">\n"+
      "    <xs:complexType>\n"+
      "      <xs:sequence>\n"+
      "        <xs:element name=\"entityIdId\" type=\"xs:string\" />\n"+
      "        <xs:element name=\"attributeDomainName\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"1\"/>\n";

    // Generate attributes
    for(ContextRegistrationAttribute contextRegistrationAttribute : 
        contextRegistrationAttributeList.getContextRegistrationAttribute()) {

      eventTypeXsd += "        <xs:element name=\""+contextRegistrationAttribute.getName()+"\" type=\"xs:"+
                      contextRegistrationAttribute.getType()+"\" />\n";        
    }

    // Generate Metadatas for ContextElement level informations
    //logger.debug("*****************************************"+registrationMetadata);
    if(registrationMetadata != null) {
    	for(ContextMetadata contextMetadata : registrationMetadata.getContextMetadata()) {
    		eventTypeXsd += "        <xs:element name=\"metadata_"+contextMetadata.getName()+"\" type=\"xs:"+
    				contextMetadata.getType()+"\" />\n";        
    	}
    }

    eventTypeXsd += 
      "      </xs:sequence>\n"+
      "    </xs:complexType>\n"+
      "  </xs:element>\n"+
      "</xs:schema>";
    
    logger.info("\""+ngsiEntityTypeName+"\" has been converted to XSD:\n\n"+eventTypeXsd);
    
    return eventTypeXsd;
  }
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * transform NGSI event format into "flattened" event format to make EPL statement writing simpler.
	 * @param contextElement NGSI ContextElement
	 * @return Response data structure
	 */

	public static String getFlattenedContextElement(ContextElement contextElement) {
    String eventType;
    if(contextElement.getEntityId().getType() != null) eventType = contextElement.getEntityId().getType();
    else eventType = contextElement.getEntityId().getId();
    
    String flattenedXmlEvent =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
      "  <"+eventType+">\n"+
      "    <entityIdId>"+contextElement.getEntityId().getId()+"</entityIdId>\n";
    if(contextElement.getAttributeDomainName() != null) flattenedXmlEvent +=
      "    <attributeDomainName>"+contextElement.getEntityId().getType()+"</attributeDomainName>\n";
    for(ContextAttribute contextAttribute : contextElement.getContextAttributeList().getContextAttribute()) {
      flattenedXmlEvent +=
      "    <"+contextAttribute.getName()+">"+contextAttribute.getContextValue()+"</"+contextAttribute.getName()+">\n";
    }
    
    // If domain metadata are present ...
    if(contextElement.getDomainMetadata() != null) {
    	
    	// ... then parse the metadata and add them one by one.
    	for(ContextMetadata contextMetadata : contextElement.getDomainMetadata().getContextMetadata()) {
    		flattenedXmlEvent +=
    				"    <metadata_"+contextMetadata.getName()+">"+contextMetadata.getValue()+
    				"</metadata_"+contextMetadata.getName()+">\n";
    	}    
    }
  	flattenedXmlEvent +=
  		"  </"+eventType+">\n";  

    return flattenedXmlEvent;
	}
//----------------------------------------------------------------------------------------------------------------------
/**
 * Register a new NGSI type which is derived from a new Esper event type. Registration can be achieved only if some 
 * event sink URLs have been defined AND if the related statement is NGSI-tagged.
 * @param statementName name of the statement whose properties and type must be registered towards event sink URLs.
 * 
 * @return an eventSinkUrlList object.
 * @throws IOException
 * @throws InterruptedException 
 * @throws StorageNotAvailableException 
 * @throws UnknownServiceException
 */
	public static EventSinkUrlList registerEntityToEventSinkUrls(String entityIdId,
																															 String statementName,
																															 StatementList statementList,
																															 String providingApplication,
																															 String registrationId,
																															 ContextRegistrationListList contextRegistrationListList,
																															 String proxyUrl, 
																															 Integer proxyPort)
																																	 								throws IOException,
						 																																						 ProtocolException, 
						 																																						 IllegalArgumentException, 
						 																																						 IllegalStateException,
	           																																						 DatatypeConfigurationException, 
	           																																						 InterruptedException,
	           																																						 StorageNotAvailableException {
    
    if(!NgsiEsperHelper.hasUnregisteredEventSinkUrl(null, statementName)) 
    	return null;

    // Seek the "statementName" statement by parsing the static var statementList.
	  for(Statement statement : (List<Statement>)statementList.getList()) {
	    if(statement.getName().equals(statementName)) { // If requested statement name found        

        RegisterContextRequest registerContextRequest = null;

        String registerContextRequestPayload = null;

        logger.debug("About to parse the event sinks which are related to \""+statementName+"\" statement.");
	      for(EventSinkUrl eventSinkUrl : statement.getEventSinkUrlList().getList()) {
	        if(!eventSinkUrl.isRegistered()) { // If the parsed event sink URL was not registered before:
               // If the forcedEntityType is set in this target event sink URL, set the entityId accordingly.
	        	
	        	Duration duration = eventSinkUrl.getRegistrationDuration();
	        	
	        	if( eventSinkUrl.getRegistrationDuration() == null )
	        		duration = DatatypeFactory.newInstance().newDuration("PT1H");	        	

            registerContextRequest = NgsiEsperHelper.buildRegisterContextRequest(entityIdId, 
            																																		 statement.getTargetEntityIdType(), 
             		 													  																		 statement.getPropertyList(), 
             		 													  																		 duration,
             		 													  																		 providingApplication,
             		 													  																		 registrationId,
             		 													  																		 contextRegistrationListList
             		 													  																		 );

            registerContextRequestPayload = Utils.objectToFormattedXmlString(registerContextRequest);

            logger.debug("  About to check if the \""+eventSinkUrl.getName()+"\" EventSinkUrl defines a delay before registering or not.");

            if( eventSinkUrl.getAfterRegistrationDelay() != null ) {
             	logger.debug("  Yes: waiting "+eventSinkUrl.getAfterRegistrationDelay()+" ms.");
             	Thread.sleep(eventSinkUrl.getAfterRegistrationDelay());
             }
            else logger.debug("  No: no delay defined.");

            logger.debug("  About to send the RegisterContextRequest serialized object towards "+eventSinkUrl.getRegistrationURL());

            int httpCode = Utils.callURL(
             														 eventSinkUrl.getRegistrationURL()+"/registerContext", 
             														 "application/xml;charset=UTF-8",
                                         "POST", 
                                         registerContextRequestPayload, 
                                         new StringBuilder(),
                                         eventSinkUrl.getxAuthToken(),
                                         proxyUrl, proxyPort
                                        );

            eventSinkUrl.setHttpRegistrationCode(httpCode);

            if(httpCode != Response.Status.OK.getStatusCode()) eventSinkUrl.setRegistered(false);              
            else eventSinkUrl.setRegistered(true);

            statement.setEventSinkUrlList(statement.getEventSinkUrlList());

            logger.debug("  Returned HTTP status code : "+httpCode);
	        }
	      } // End of the for loop
	      return statement.getEventSinkUrlList();
	    }
	  }
	  return null;
	}	
//----------------------------------------------------------------------------------------------------------------------
/**
 * Check if a given statement has at least one unregistered EventSinkUrls.
 * @param statementList TODO
 * @param statementName the concerned statement name that is potentially unregistered towards a target event sink URL. 
 * @return true if the statement has an NGSI unregistered event sink URL. Return false otherwise.
 */
	public static boolean hasUnregisteredEventSinkUrl( StatementList statementList, String statementName ) {
    logger.debug("About to check if statement \""+statementName+"\" has at least an unregistered event sink URL.");

    // Seek the "statementName" statement
    for(Statement statement : (List<Statement>)ApplicationWideInstances.complexEventProcessing.getStatements().getList())
      if(statement.getName().equals(statementName)) // Requested statement found   
        for(EventSinkUrl EventSinkUrl : statement.getEventSinkUrlList().getList())
          if(!EventSinkUrl.isRegistered()) { 
            logger.debug("Statement \""+statementName+"\" is not registered towards EventSinkUrl \""+EventSinkUrl.getName()+
                         "\" ("+EventSinkUrl.getTarget()+"). (Possibly among other URIs that have not been checked.)");
            return true;
          }
    
    logger.debug("Statement \""+statementName+"\" is registered towards all needed event sink URLs, or does not feature "+
    		         "any event sink URL at all.");
    return false;    
  }
//----------------------------------------------------------------------------------------------------------------------
/**
 * Generate a RegisterContextRequest object from a PropertyList object.
 * @param propertyList the list of properties from which to generate a RegisterContextRequest.
 * @param duration register duration.
 * @return a RegisterContextRequest object.
 */
	  public static RegisterContextRequest buildRegisterContextRequest(String entityIdId, 
	                                                                   String entityIdType, 
	                                                                   PropertyList propertyList, 
	                                                                   Duration duration,
	                                                                   String providingApplication,
	                                                                   String registrationId,
	                                                                   ContextRegistrationListList contextRegistrationListList) 
	                                                                  		 				throws DatatypeConfigurationException {
	    RegisterContextRequest registerContextRequest = new RegisterContextRequest();
	    
	    // TODO ***************** Add an entity to an existing context registration: to do so, check if the entityIdType
	    // exists or not. If it exists, retrieve its registrationId and produce a simple registerContextRequest with the
	    // new entity + registration ID in order to update the existing context registration.
	    
	    // Build the registerContextRequest
			ContextRegistrationAttributeList contextRegistrationAttributeList = new ContextRegistrationAttributeList();
			RegistrationMetadata registrationMetadata = new RegistrationMetadata();
			ContextRegistrationList newContextRegistrationList = new ContextRegistrationList();
			newContextRegistrationList.setList(new ArrayList<ContextRegistration>());
			
			/* There are two important cases: registrationId is null or not. If it's null, it means we are creating a brand 
			 * new context registration that will feature all properties from the propertyList variable. If registrationId is 
			 * set, it means we are  building a register context request that is meant to update an EXISTING registration.
			*/
			if(registrationId != null) {
			
				// Seek the related context registration: that is, the one that feature the registrationId
				for(ContextRegistrationList contextRegistrationList : contextRegistrationListList.getList()) {
					
					if(contextRegistrationList.getRegistrationId().equals(registrationId)) { // If the registrationId is found...
												
						// Then parse the associated context registrations...
						for(ContextRegistration contextRegistration : contextRegistrationList.getList()) {
							
							newContextRegistrationList.getList().add(contextRegistration);
							
							// ...until the sought entity type is found
							if(contextRegistration.getEntityIdList().getEntityId().get(0).getType().equals(entityIdType)) {
								EntityId entityId = new EntityId();
								entityId.setId(entityIdId);
								entityId.setType(entityIdType);
								
								contextRegistration.getEntityIdList().getEntityId().add(entityId);								
							}
						}		
						registerContextRequest.setContextRegistrationList(newContextRegistrationList);
					}								
				}
				registerContextRequest.setDuration(duration);
				registerContextRequest.setRegistrationId(registrationId);
			}	else { // In this case, registration ID is null: we create a brand new RegisterContext request.
			
				for(Property property : propertyList.getProperty()) {
					logger.debug("  Found property \""+property.getName()+"\".");
					if(property.getName().contains("metadata_")) {
						ContextMetadata contextMetadata = new ContextMetadata();
						contextMetadata.setName(property.getName().substring(9)); // Remove the "metadata_" prefix.
						contextMetadata.setType(property.getType().toLowerCase());

						if(contextMetadata.getName().equals("timestamp")) {
							contextMetadata.setValue( Utils.getFormattedTime("yyyy-MM-dd'T'HH:mm:ss.SSSZ") );

							contextMetadata.setType("dateTime");
						}

						registrationMetadata.getContextMetadata().add(contextMetadata);

					}
					else if(!property.getName().equals("entityIdId")) {
						ContextRegistrationAttribute contextRegistrationAttribute = new ContextRegistrationAttribute();
						contextRegistrationAttribute.setName(property.getName());
						contextRegistrationAttribute.setType(property.getType());
						contextRegistrationAttributeList.getContextRegistrationAttribute().add(contextRegistrationAttribute);
					}
				}			

				EntityId entityId = new EntityId();
				entityId.setId(entityIdId);
				entityId.setType(entityIdType);
				EntityIdList entityIdList = new EntityIdList();
				entityIdList.getEntityId().add(entityId);

				ContextRegistration contextRegistration = new ContextRegistration();
				contextRegistration.setEntityIdList(entityIdList);
				contextRegistration.setContextRegistrationAttributeList(contextRegistrationAttributeList);
				contextRegistration.setRegistrationMetadata(registrationMetadata);
				contextRegistration.setProvidingApplication(providingApplication);

				ContextRegistrationList contextRegistrationList = new ContextRegistrationList();
				contextRegistrationList.setList(new ArrayList<ContextRegistration>());
				contextRegistrationList.getList().add(contextRegistration);

				registerContextRequest.setContextRegistrationList(contextRegistrationList);

				registerContextRequest.setDuration(duration);

			}
			return registerContextRequest;
	  }
//----------------------------------------------------------------------------------------------------------------------
	 public static String buildCreateShemaStatement(ContextRegistration contextRegistration) {
		 
     ContextRegistrationAttributeList contextRegistrationAttributeList = contextRegistration.getContextRegistrationAttributeList();
	   RegistrationMetadata registrationMetadata = contextRegistration.getRegistrationMetadata(); 
		 
		 String entityIdType = contextRegistration.getEntityIdList().getEntityId().get(0).getType();
		 
		 String eplDeclareSchema = "create schema "+entityIdType+" \n(entityIdId string, attributeDomainName string, ";
		 
	    // Generate attributes
	    for(ContextRegistrationAttribute contextRegistrationAttribute :  
	    	contextRegistrationAttributeList.getContextRegistrationAttribute() ) {

	    	eplDeclareSchema += contextRegistrationAttribute.getName()+" "+contextRegistrationAttribute.getType()+", ";        
	    }

	    if(registrationMetadata != null) {
	    	for(ContextMetadata contextMetadata : registrationMetadata.getContextMetadata()) {
	    		
	    		String contextMetadataType = contextMetadata.getType();
	    		
	    		// XML dateTime types are converted to string because Esper does not understand the dateTime type
	    		if(contextMetadataType.equals("dateTime")) { 
	    			contextMetadataType = "string";
	    		}	    		
	    			    		
	    		eplDeclareSchema += "metadata_"+contextMetadata.getName()+" "+contextMetadataType+", ";        
	    	}
	    }
	    
	    eplDeclareSchema = eplDeclareSchema.substring(0, eplDeclareSchema.length()-2); // Remove the last comma
	    eplDeclareSchema += ")";	    
		 
		return eplDeclareSchema;		 
	}
}
