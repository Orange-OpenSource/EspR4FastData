/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.ngsicontext;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import javax.ws.rs.core.Response;
import javax.xml.datatype.Duration;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mdps.ObjectNotFoundException;
import com.orange.espr4fastdata.commons.ApplicationWideInstances;
import com.orange.espr4fastdata.commons.NgsiEsperHelper;
import com.orange.espr4fastdata.commons.UnsupportedFeatureException;
import com.orange.espr4fastdata.commons.Utils;
import com.orange.espr4fastdata.oma.ngsidatastructures.AttributeList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextAttribute;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElement;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementResponseList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextMetadata;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistration;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistrationAttribute;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistrationList;
import com.orange.espr4fastdata.oma.ngsidatastructures.EntityId;
import com.orange.espr4fastdata.oma.ngsidatastructures.QueryContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.QueryContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.RegisterContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.RegisterContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.StatusCode;
import com.orange.espr4fastdata.oma.ngsidatastructures.SubscribeContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.SubscribeContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.SubscribeError;
import com.orange.espr4fastdata.oma.ngsidatastructures.SubscribeResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.UnsubscribeContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.UnsubscribeContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateContextResponse;

/**
 * @author Laurent ARTUSIO
 * This class is a partial implementation of both NGSI-10 and NGSI-9 standards. NGSI is an open standard that is roughly 
 * used to manage "contexts" and their properties, and to propagate them to subscribers, when properties are 
 * updated. A "context" is meant to describe any kind of object, typically pertaining to sensors and various devices.
 * It can be a temperature sensor, a car with its properties(current speed, tire pressure, GPS location etc), a 
 * formal representation of an individual along with his/her properties, and so on...
 * For more information regarding this standard, please check http://technical.openmobilealliance.org/Technical/release_program/docs/NGSI/V1_0-20120529-A/OMA-TS-NGSI_Context_Management-V1_0-20120529-A.pdf
 */
public class Ngsi_9_10 extends Observable implements NGSI9Interface, NGSI10Interface {

	private static Logger logger = Logger.getLogger(Ngsi_9_10.class);

	private ContextRegistrationListList contextRegistrationListList;
	private ContextSubscriptionList contextSubscriptionList;
	private ContextElementList contexElementList;
	
//----------------------------------------------------------------------------------------------------------------------
	
	public ContextRegistrationListList getContextRegistrationListList() {	return this.contextRegistrationListList; }
	public void setContextRegistrationListList(ContextRegistrationListList contextRegistrationListList) {
		this.contextRegistrationListList = contextRegistrationListList;
	}
//----------------------------------------------------------------------------------------------------------------------	
	/**
	 * @param registerContextRequest the RegisterContextRequest object to check.
	 * @throws UnacceptableSyntaxException
	 * @throws EntitiesAreNotOfTheSameTypeException 
	 * @throws EntityAlreadyRegisteredException 
	 * @throws AttributeAlreadyExistsException 
	 * @throws RegistrationNotFoundException 
	 */
	protected void checkRegisterContextRequest(RegisterContextRequest registerContextRequest) 
			 throws UnacceptableSyntaxException, 
							EntitiesAreNotOfTheSameTypeException, 
							EntityAlreadyRegisteredException, 
							AttributeAlreadyExistsException, 
							RegistrationNotFoundException {
		/* TODO: a register context request is related only to a new registration OR to a registration update: different  
		 * context registrations within a register context request cannot be related to both updates and new registrations.
		 * An exception must be fired if this prerequisite is not respected.
		 */
		if(registerContextRequest == null) throw new UnacceptableSyntaxException();

		if(registerContextRequest.getContextRegistrationList() == null) 
			throw new UnacceptableSyntaxException("The ContextRegistrationList tag is mandatory.");

		if(registerContextRequest.getContextRegistrationList().getList().size() < 1) 
			throw new UnacceptableSyntaxException("There must be at least one context registration in the list.");

		if(registerContextRequest.getRegistrationId() != null && 
				this.getContextRegistrationList(registerContextRequest.getRegistrationId()) == null) 
			throw new RegistrationNotFoundException("The provided registration ID is unknown.");

		// Parse all ContextRegistration objects within the list.
		for(ContextRegistration contextRegistration :	registerContextRequest.getContextRegistrationList().getList()) {

			if(contextRegistration.getEntityIdList() == null) 
				throw new UnacceptableSyntaxException("The entity list must not be empty");

			if(contextRegistration.getEntityIdList().getEntityId() == null) 
				throw new UnacceptableSyntaxException("The entity list must not be empty");

			if(contextRegistration.getEntityIdList().getEntityId().size() < 1) 
				throw new UnacceptableSyntaxException("The entity list must contain at least one entity");

			// Parse all entities within the current ContextRegistration object.
			String referenceEntityType = contextRegistration.getEntityIdList().getEntityId().get(0).getType();

			for(EntityId entityId : contextRegistration.getEntityIdList().getEntityId()) {

				if(entityId.getId() == null) 
					throw new UnacceptableSyntaxException("All entities must have an Id attribute.");

				if(entityId.getId().equals("")) 
					throw new UnacceptableSyntaxException("All entities must have a name.");				

				// Check if the parsed entity has already been registered before. It is forbidden if the register context
				// request is about creating a brand new context. In the case of a context update (when registration id is set),
				// existing entities are allowed.
				if(this.entityIdIdIsRegistered(entityId.getId()) && registerContextRequest.getRegistrationId() == null) 
					throw new EntityAlreadyRegisteredException();				

				// Check if entities within the contextRegistrationList object are of the same type, cause they must be.       
				if(!entityId.getType().equals(referenceEntityType)) 
					throw new EntitiesAreNotOfTheSameTypeException();
			}

			if(contextRegistration.getProvidingApplication() == null)
				throw new UnacceptableSyntaxException("The providingApplication property is mandatory.");

			if(contextRegistration.getProvidingApplication().equals(""))
				throw new UnacceptableSyntaxException("The providingApplication property must be filled.");	

			// check the meta data (check the timestamp format conformance)
			if(contextRegistration.getRegistrationMetadata() != null)
				this.checkContextMetadatas(contextRegistration.getRegistrationMetadata().getContextMetadata());
		}
	}
//----------------------------------------------------------------------------------------------------------------------	
	/**
	 * Test the existence of a particular attribute (identified by its name) for a particular entity type, in a 
	 * context registration list.
	 * @param attributeName the name of the searched attribute.
	 * @param entityIdType the type name of the concerned entity.
	 * @param contextRegistrationListList the context registration list in which to seek the attribute.
	 * @return
	 */
	public boolean contextRegistrationAttributeExists(String attributeName, String entityIdType) {

		for( ContextRegistrationList contextRegistrationList : this.contextRegistrationListList.getList() ) {

			for( ContextRegistration contextRegistration : contextRegistrationList.getList()) {

				if( contextRegistration.getEntityIdType().equals(entityIdType) ) {

					for(ContextRegistrationAttribute contextRegistrationAttribute : 
						contextRegistration.getContextRegistrationAttributeList().getContextRegistrationAttribute()) {

						if( contextRegistrationAttribute.getName().equals(attributeName)) 
							return true;
					}
				}
			}
		}
		return false;
	}	
//----------------------------------------------------------------------------------------------------------------------	
	/**
	 * @param contextElement
	 * @throws ExpiredRegistrationException 
	 */
	protected void checkRegistrationDurationValidity(ContextElement contextElement) 
			throws ExpiredRegistrationException {

		String registrationId = this.getRegistrationId(contextElement.getEntityId().getType());

		ContextRegistrationList contextRegistrationList = this.getContextRegistrationList(registrationId);

		Duration duration = contextRegistrationList.getDuration();

		Date registrationDate = this.getContextRegistration(contextElement.getEntityId().getType()).getTimeStamp();

		duration.addTo(registrationDate); // Add the registration duration to the registration date time stamp.

		// If the current time is beyond the registration date, it means the registration is over.
		if(Calendar.getInstance().getTime().after(registrationDate))
			throw new ExpiredRegistrationException();
	}	
//----------------------------------------------------------------------------------------------------------------------  
  /**
   * @param registerContextRequest the RegisterContextRequest object to process. 
   * @return a RegisterContextResponse object.
   */
		
  public RegisterContextResponse registerContext(RegisterContextRequest registerContextRequest) {

    int code = Response.Status.OK.getStatusCode(); // Set the returned error code to 200 by default.  
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the error message.
    String details = null;

		// Unique Id generation for this registration
    String registrationId = null;

    try {    	
    	this.checkRegisterContextRequest(registerContextRequest);   	

      logger.info("OK: No error was found within the RegisterContextRequest object.");

  		registrationId = registerContextRequest.getRegistrationId();

  		if(registrationId != null) { // Updating an existing context registration
  			logger.info("There is a registration id ("+registrationId+") in the request. It means it's an update of an existing context registration.");
  			this.removeContextRegistrationList(registrationId);
  			logger.info("The "+registrationId+" registration has been deleted in order to allow its replacement by the current register context request.");
  		}
  		else { 
  			registrationId = Utils.getSmallUUID(); // Creating a new context registration id
  			logger.debug("There is no registration ID in the register context request. It means it's a brand new registration, not an update of an existing one.");
  		}

 			ArrayList<ContextRegistration> newContextRegistrations = new ArrayList<ContextRegistration>();
 			
 			// Parse the ContextRegistration objects featured in the RegisterContextRequest object, in order to build a
 			// context registration
 			for(ContextRegistration contextRegistrationFromRequest : 
 				registerContextRequest.getContextRegistrationList().getList()) {

 				// Set the current timestamp for the parsed context registration.
 				contextRegistrationFromRequest.setTimeStamp(Calendar.getInstance().getTime());

 				newContextRegistrations.add(contextRegistrationFromRequest);
 			}

 			// Generate the new context registration list, and add it
 			ContextRegistrationList contextRegistrationList = new ContextRegistrationList();
 			contextRegistrationList.setList(newContextRegistrations);
 			contextRegistrationList.setRegistrationId(registrationId);
 			contextRegistrationList.setDuration(registerContextRequest.getDuration());

 			this.contextRegistrationListList.getList().add(contextRegistrationList);  	
  		// Notify potential observers about the update.
 			
  		ArrayList<Object> observerArguments = new ArrayList<Object>(); // Arguments passed to the potential observer

  		observerArguments.add(contextRegistrationList);
  		
			super.setChanged();			
			super.notifyObservers(observerArguments);
    }
    catch (EntityAlreadyRegisteredException e) { 
      code = Response.Status.FORBIDDEN.getStatusCode(); 
      reasonPhrase = Response.Status.FORBIDDEN.getReasonPhrase();
      details = e.getMessage();
    }
    catch (EntitiesAreNotOfTheSameTypeException e) {
      code = Response.Status.FORBIDDEN.getStatusCode(); 
      reasonPhrase = Response.Status.FORBIDDEN.getReasonPhrase();
      details = e.getMessage();
    } 
    catch (UnacceptableSyntaxException e) {
      code = Response.Status.BAD_REQUEST.getStatusCode();
      reasonPhrase = Response.Status.BAD_REQUEST.getReasonPhrase();
      details = e.getMessage();
		} catch (AttributeAlreadyExistsException e) {
      code = Response.Status.CONFLICT.getStatusCode();
      reasonPhrase = Response.Status.CONFLICT.getReasonPhrase();
      details = e.getMessage();
		} catch (RegistrationNotFoundException e) {
      code = Response.Status.NOT_FOUND.getStatusCode();
      reasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
      details = e.getMessage();
		}

    RegisterContextResponse registerContextResponse = new RegisterContextResponse();

    if(code != Response.Status.OK.getStatusCode()) { 

      StatusCode errorCode = new StatusCode();
      errorCode.setCode(code);
      errorCode.setReasonPhrase(reasonPhrase);
      errorCode.setDetails(details);    
    	registerContextResponse.setErrorCode(errorCode);
      
    	registerContextResponse.setRegistrationId(new String());
    }
    else {
    	registerContextResponse.setDuration(registerContextRequest.getDuration());
    	registerContextResponse.setRegistrationId(registrationId);
    }
    return registerContextResponse;
	}
//----------------------------------------------------------------------------------------------------------------------
	
	protected ContextRegistrationList getContextRegistrationList(String registrationId) {
		
		// Seek the context registration list that is referenced by the registration id in the register context request. 
		for( ContextRegistrationList contextRegistrationList : this.contextRegistrationListList.getList()) {
			
			if( contextRegistrationList.getRegistrationId().equals(registrationId)) {
				return contextRegistrationList;				
			}
		}
		return null;
	}	
//----------------------------------------------------------------------------------------------------------------------
	public String getRegistrationId( String entityIdType ) {
											
		for(ContextRegistrationList contextRegistrationList : this.contextRegistrationListList.getList()) {
	
			for(ContextRegistration contextRegistration : contextRegistrationList.getList()) {
				
				if(contextRegistration.getEntityIdList().getEntityId().get(0).getType().equals(entityIdType))
					return contextRegistrationList.getRegistrationId();				
			}			
		}
		return null;
	}	
//----------------------------------------------------------------------------------------------------------------------
		/**
		 * Check if a particular entity name is already registered or not.
		 * @param entityName the entity id whose registration is going to be checked.
		 * @return TRUE if already registered. FALSE otherwise.
		 */
		
  public Boolean entityIdIdIsRegistered(String entityIdId) {
			
		for( ContextRegistrationList contextRegistrationList : this.contextRegistrationListList.getList() ) {
			
			for(ContextRegistration contextRegistration : contextRegistrationList.getList() ) {				
				
				for(EntityId entityId : contextRegistration.getEntityIdList().getEntityId()) {
					if(entityId.getId().equals(entityIdId)) 
						return true;
				}	
			}
		}
			return false;
	}
//----------------------------------------------------------------------------------------------------------------------
		/**
		 * Check if a particular entity type is already registered or not.
		 * @param entityIdType the entity type whose registration is going to be checked.
		 * @return TRUE if already registered. FALSE otherwise. 
		 */
			
		public Boolean entityIdTypeIsRegistered(String entityIdType) {
			
			for( ContextRegistrationList contextRegistrationList : this.contextRegistrationListList.getList() ) {
				
				for(ContextRegistration contextRegistration : contextRegistrationList.getList() ) {				
					
					for(EntityId entityId : contextRegistration.getEntityIdList().getEntityId()) {
						if(entityId.getType().equals(entityIdType)) 
							return true;
					}	
				}
			}
				return false;
		}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Returns the ContextRegistration object reference that belongs to a particular entity type.
	 * @param entityIdType
	 * @return the proper ContextRegistration object reference.
	 */
	public ContextRegistration getContextRegistration(String entityIdType) {
					
		for(ContextRegistrationList contextRegistrationList : this.contextRegistrationListList.getList()) {
			
			for(ContextRegistration contextRegistration : contextRegistrationList.getList()) {
				
				for(EntityId entityId : contextRegistration.getEntityIdList().getEntityId()) {
					if(entityId.getType().equals(entityIdType)) return contextRegistration;
				}	
			}
		}
		return null;
	}			
//----------------------------------------------------------------------------------------------------------------------
		/**
		 * Test if a particular attribute (identified by its name) is featured in the provided attribute list.
		 * @param attributeList the attribute list that might contain the searched attribute.
		 * @param searchedAttributeName the name of the searched attribute.
		 * @return true if the attribute was found, false otherwise.
		 */
		protected static boolean attributeExists(AttributeList attributeList, String searchedAttributeName) {
			
			for(String attributeName : attributeList.getAttribute()) {
				
				if(searchedAttributeName.equals(attributeName)) return true;
			}
			return false;
		}
//----------------------------------------------------------------------------------------------------------------------	
		protected void checkContextElement(ContextElement contextElement) throws UnacceptableSyntaxException, 
																																					 	 UnkownEntityIdException, 
																																					 	 UnknownEntityTypeException, 
																																					 	 UnsupportedFeatureException {
			if(contextElement.getEntityId() == null)
				throw new UnacceptableSyntaxException("Missing EntityId:exactly one EntityId is required in a ContextElement.");

			if(contextElement.getEntityId().getId() == null)
				throw new UnacceptableSyntaxException("An EntityId must have an id property. Please provide it.");
				
			if(contextElement.getEntityId().getType() == null)
				throw new UnacceptableSyntaxException("The OMA-NGSI standard does not define the \"type\" property as mandatory.\nHowever it is mandatory in the EspR4FastData particular context.");

	 	  if(contextElement.getEntityId().isIsPattern() == null) contextElement.getEntityId().setIsPattern(false);
			
			if( (contextElement.getEntityId().isIsPattern() != null) && (contextElement.getEntityId().isIsPattern() == true) )
				throw new UnsupportedFeatureException("EspR4FastData does not support the pattern feature. Please change the pattern property to false or remove it.");
			
			if(!this.entityIdIdIsRegistered(contextElement.getEntityId().getId()) ) 
				throw new UnkownEntityIdException();
			
			if(!this.entityIdTypeIsRegistered(contextElement.getEntityId().getType()) ) 
				throw new UnknownEntityTypeException();
			
			// Parse meta data in the context element (check the timestamp format conformance)
			if(contextElement.getDomainMetadata() != null)
				this.checkContextMetadatas(contextElement.getDomainMetadata().getContextMetadata());
		}
//----------------------------------------------------------------------------------------------------------------------
	  protected void checkContextMetadatas( List<ContextMetadata> contextMetadatas) throws UnacceptableSyntaxException {
	  	
			// Parse the metadata to check timestamp formats
			for(ContextMetadata contextMetadata : contextMetadatas) {
				
				if(contextMetadata.getName().equals("timestamp") && !contextMetadata.getType().equals("dateTime")) 
					throw new UnacceptableSyntaxException("\"timestamp\" is a reserved keyword. Its type must be \"dateTime\".");
						 
					// Check time stamps syntax
					try {
						if( contextMetadata.getName().equals("timestamp"))
							
							// Check the format conformity
							Utils.getDateFromString(contextMetadata.getValue(), "yyyy-MM-dd'T'HH:mm:ss.SSSz");						
					} 
					catch (ParseException e) {
						throw new UnacceptableSyntaxException("Wrong timestamp format.\nThe requested format is extended iso8601 that conforms the \"yyyy-MM-ddTHH:mm:ss.SSSz\" pattern.\nValid timestamp example: 2014-05-16T15:23:12.234+0200");
					}
				}
	  }
//----------------------------------------------------------------------------------------------------------------------
		protected void checkQueryContextRequest(QueryContextRequest queryContextRequest) 
		throws UnacceptableSyntaxException, UnimplementedFeatureException {
			
			if(queryContextRequest == null) 
				throw new UnacceptableSyntaxException("The queryContextRequest is null");
			
			if(queryContextRequest.getRestriction() != null)
				throw new UnimplementedFeatureException("The \"Restriction\" feature is defined in the NGSI specification, but has not been implemented in EspR4FastData yet.");
						
			if(queryContextRequest.getEntityIdList() == null)
				throw new UnacceptableSyntaxException("You must provide an entity id list, with at least one entity.");
			
			if(queryContextRequest.getEntityIdList().getEntityId().size() == 0)
				throw new UnacceptableSyntaxException("You must provide at least one entity.");		
		}
//----------------------------------------------------------------------------------------------------------------------
		protected boolean contextElementExistsForEntity(String entityIdId) {
			for(Object object : this.contexElementList.getList()) {		
				
				ContextElement contextElement = (ContextElement)object;
				
				if(contextElement.getEntityId().getId().equals(entityIdId)) return true;
			}
			
			return false;
		}
//----------------------------------------------------------------------------------------------------------------------		

	protected void deleteRegistration(String registrationId) {
		
		if( this.contextRegistrationListList != null ) {
			
			for( int index = 0; index < this.contextRegistrationListList.getList().size(); index++) {
				
				ContextRegistrationList contextRegistrationList = 
						(ContextRegistrationList)contextRegistrationListList.getList().get(index);		
				
				if(contextRegistrationList.getRegistrationId().equals(registrationId)) {
					
					contextRegistrationListList.getList().remove(index);		
					
					break;
				}
			}
		}
	}		
//----------------------------------------------------------------------------------------------------------------------
		protected ContextElement getContextElement(String entityIdId) {
			ArrayList<ContextElement> contextElements = new ArrayList<ContextElement>();
			
			for(Object object : this.contexElementList.getList()) {
				
				ContextElement contextElement = (ContextElement)object;
				
				if(contextElement.getEntityId().getId().equals(entityIdId))
					contextElements.add(contextElement);
			}
			
			return contextElements.get(contextElements.size()-1);
		}
//----------------------------------------------------------------------------------------------------------------------	
		
		public ContextElementList getContexElementList() { return contexElementList; }
		public void setContexElementList(ContextElementList contexElementList) { this.contexElementList = contexElementList; }

//----------------------------------------------------------------------------------------------------------------------
		
	  public ContextSubscriptionList getContextSubscriptionList() {	return this.contextSubscriptionList;	}
		public void setContextSubscriptionList(ContextSubscriptionList contextSubscriptionList) {
			this.contextSubscriptionList = contextSubscriptionList;
		}

//----------------------------------------------------------------------------------------------------------------------	
		/**
		 * Check the validity of a SubscribeContextRequest.
		 * @param subscribeContextRequest the request to process. 
		 * @throws NoEntityException 
		 * @throws UnsupportedFeatureException
		 */ 
		// TODO: check that requested entities and attributes exist.
		protected void checkSubscribeContextRequest(SubscribeContextRequest subscribeContextRequest) 
				throws NoEntityException, UnsupportedFeatureException {
			
			if(subscribeContextRequest.getEntityIdList() == null) throw new NoEntityException();
			if(subscribeContextRequest.getEntityIdList().getEntityId() != null ) {
				if(subscribeContextRequest.getEntityIdList().getEntityId().size() == 0) throw new NoEntityException();
			}
			
			if(subscribeContextRequest.getDuration() != null) throw new UnsupportedFeatureException(
					"WARNING: The duration feature is not supported yet. It will not be taken into account.");
			
			if(subscribeContextRequest.getNotifyConditions() != null) throw new UnsupportedFeatureException(
					"WARNING: The notifyConditions feature is not supported yet. It will not be taken into account.");
			
			if(subscribeContextRequest.getRestriction() != null) throw new UnsupportedFeatureException(
					"WARNING: The restriction feature is not supported yet. It will not be taken into account.");
			
			if(subscribeContextRequest.getThrottling() != null) throw new UnsupportedFeatureException(
					"WARNING: The throttling feature is not supported yet. It will not be taken into account.");
		}	
		
//----------------------------------------------------------------------------------------------------------------------	
		/**
		 * @param updateContextRequest the UpdateContextRequest instance whose syntax is checked. High level objects are
		 * 				checked (ContextElementList, UpdateAction), whereas individual context elements are not.
		 * @throws UnacceptableSyntaxException in case an error is found while parsing.
		 * @throws UnkownEntityIdException 
		 */
		protected void checkUpdateContextRequest(UpdateContextRequest updateContextRequest) 
				throws UnacceptableSyntaxException { 
		
			if(updateContextRequest == null) throw new UnacceptableSyntaxException("The UpdateContextRequest is empty.");
			
			if(updateContextRequest.getContextElementList() == null) 
				throw new UnacceptableSyntaxException("There are syntax errors in sub-objects beneath contextElementList. It could also be that the contextElementList tag is missing.");
			
			if(updateContextRequest.getContextElementList().getList().size() < 1)
				throw new UnacceptableSyntaxException("The ContextElementList must feature at least one ContextElement");
			
			if(updateContextRequest.getUpdateAction() == null) 
				throw new UnacceptableSyntaxException("The updateAction property is missing and mandatory.");			
		}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * NGSI-10 subscribeContext.
	 * @param subscribeContextRequest the SubscribeContextRequest object that describes the subscription.
	 * @return a SubscribeContextResponse object.
	 */
	  public SubscribeContextResponse subscribeContext(SubscribeContextRequest subscribeContextRequest) {  	

	  	logger.debug("**** BEGIN / NGSI10.subscribeContext");
	  	
	  	int code = Response.Status.OK.getStatusCode();
	  	String reasonPhrase = Response.Status.OK.getReasonPhrase();
	  	String details = null;

	  	try {
				this.checkSubscribeContextRequest(subscribeContextRequest);
			}
	  	catch (NoEntityException e) {
	  		code = Response.Status.BAD_REQUEST.getStatusCode();
	  		reasonPhrase = Response.Status.BAD_REQUEST.getReasonPhrase();
	  		details = e.getMessage();
			}
	  	catch (UnsupportedFeatureException e) {
	  		code = Response.Status.OK.getStatusCode();
	  		reasonPhrase = Response.Status.OK.getReasonPhrase();
	  		details = e.getMessage();
			}

	  	SubscribeContextResponse subscribeContextResponse = new SubscribeContextResponse();

	  	if(code == Response.Status.OK.getStatusCode()) {
	  		String subscriptionId = Utils.getSmallUUID();  		

				ContextSubscription contextSubscription = new ContextSubscription();

				contextSubscription.setAttributeList(subscribeContextRequest.getAttributeList());
				contextSubscription.setEntityIdList(subscribeContextRequest.getEntityIdList());
				contextSubscription.setReference(subscribeContextRequest.getReference());
				contextSubscription.setSubscriptionId(subscriptionId);

				this.contextSubscriptionList.getList().add(contextSubscription);
				subscribeContextResponse.setSubscribeResponse(new SubscribeResponse());
				subscribeContextResponse.getSubscribeResponse().setSubscriptionId(subscriptionId);
	  	}
	  	else { // If ok
	  		subscribeContextResponse.setSubscribeError(new SubscribeError());
	  		subscribeContextResponse.getSubscribeError().setErrorCode(new StatusCode());
	  		subscribeContextResponse.getSubscribeError().getErrorCode().setCode(code);
	  		subscribeContextResponse.getSubscribeError().getErrorCode().setReasonPhrase(reasonPhrase);
	  		subscribeContextResponse.getSubscribeError().getErrorCode().setDetails(details);
	  	}
	  	logger.debug("---- END / NGSI10.subscribeContext");

	  	return subscribeContextResponse;
	  }  
//----------------------------------------------------------------------------------------------------------------------  
	  /**
	   * @param unsubscribeContextRequest the unsubscribe NGSI request.
	   * @param contextSubscription the list of subscription in which to delete the subscription. 
	   * @return an UnsubscribeContextResponse.
	   */
	  public UnsubscribeContextResponse unsubscribeContext(UnsubscribeContextRequest unsubscribeContextRequest) { 
	  	logger.debug("---- BEGIN / Ngsi_9_10&10.unsubscribeContext");
	  	
	  	int code = Response.Status.OK.getStatusCode();
	  	String reasonPhrase = Response.Status.OK.getReasonPhrase();
	  	String details = null;
	  	
	  	// The UnsubscribeContextRequest structure is so simple that there is no need for some dedicated "checkSyntax" 
	  	// method (see the checkSubscribeContextRequest method, which features such checking)

	  	if((unsubscribeContextRequest.getSubscriptionId() == null) || 
	  			(unsubscribeContextRequest.getSubscriptionId().equals(""))) {
	  		
	  		code = Response.Status.BAD_REQUEST.getStatusCode();
	  		reasonPhrase = Response.Status.BAD_REQUEST.getReasonPhrase();
	  		details = "The subscriptionId parameter is mandatory. Please provide it in your request.";
	  	} 
	  	else {
	  		// Seek the context subscription and delete it
	  		for(int i=0; i < contextSubscriptionList.getList().size();i++) {
	  			
	  			ContextSubscription contextSubscription = (ContextSubscription)contextSubscriptionList.getList().get(i);
	  			
					if(contextSubscription.getSubscriptionId().equals(unsubscribeContextRequest.getSubscriptionId()) ) {
						contextSubscriptionList.getList().remove(i);
						break;
					}
				}
	  	}

	  	UnsubscribeContextResponse unsubscribeContextResponse = new UnsubscribeContextResponse();

	  	if(code == Response.Status.OK.getStatusCode()) {
	  		unsubscribeContextResponse.setSubscriptionId(unsubscribeContextRequest.getSubscriptionId());
	  	}

	  	unsubscribeContextResponse.setStatusCode(new StatusCode());
	  	unsubscribeContextResponse.getStatusCode().setCode(code);
	  	unsubscribeContextResponse.getStatusCode().setDetails(details);
	  	unsubscribeContextResponse.getStatusCode().setReasonPhrase(reasonPhrase);

	  	return unsubscribeContextResponse;  	
	  }  
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Returns the recipients that subscribed to a particular entity. 
	 * @param entityIdId entity name.
	 * @param contextSubscription a ContextSubscriptionList object in which to seek recipients.
	 * @return the list of recipient URLs
	 */
	public List<NotifyRecipient> getNotifyRecipientList(String entityIdId) {
				
		List<NotifyRecipient> notifyRecipientList = new ArrayList<NotifyRecipient>();
				
		for(Object object : this.contextSubscriptionList.getList() ) {
			
			ContextSubscription contextSubscription = (ContextSubscription)object;
			
			for(EntityId entityId : contextSubscription.getEntityIdList().getEntityId()) {
				if(entityId.getId().equals(entityIdId)) {
					NotifyRecipient notifyRecipient = new NotifyRecipient();
					notifyRecipient.setReference(contextSubscription.getReference());
					notifyRecipient.setSubscriptionId(contextSubscription.getSubscriptionId());
					notifyRecipientList.add(notifyRecipient);
					return notifyRecipientList;
				}
			}
		}
		return null;
	}
//----------------------------------------------------------------------------------------------------------------------  
	/**
	 * @param queryContextRequest the request object that features requested entities, requested attribute names, 
	 * 				and restrictions. 
	 * @return a QueryContextResponse object featuring the requested entities and attributes values.
	 */
	  public QueryContextResponse queryContext(QueryContextRequest queryContextRequest) {

	    int queryContextResponseCode = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
	    String queryContextResponseReasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the error message
	    String queryContextResponseDetails = null;
	    
	    QueryContextResponse queryContextResponse = new QueryContextResponse();
	    
	    try {
	    	// Check the overall request validity
				this.checkQueryContextRequest(queryContextRequest);

		    ContextElementResponseList contextElementResponseList = new ContextElementResponseList();
		    queryContextResponse.setContextResponseList(contextElementResponseList);
				
		    // Parse the requested entities in the request
				for(EntityId entityId : queryContextRequest.getEntityIdList().getEntityId()) {
					ContextElementResponse contextElementResponse = new ContextElementResponse();
					
					if(!this.entityIdIdIsRegistered(entityId.getId())) {
						
						contextElementResponse.setStatusCode(new StatusCode(Response.Status.NOT_FOUND.getStatusCode(), 
																																Response.Status.NOT_FOUND.getReasonPhrase(), 
																																"The requested entity has not been registered."+
																																"It does not exist."));					
					} 
					else 
						
						if(!this.contextElementExistsForEntity(entityId.getId())) {
						contextElementResponse.setStatusCode(new StatusCode(Response.Status.NO_CONTENT.getStatusCode(), 
																																Response.Status.NO_CONTENT.getReasonPhrase(), 
																																"The requested entity has been registered but, never "+
																																"updated with actual attribute values.\nIt means that no"+
																																"updateContextRequest has been received yet for this "+
																																"entity following its registration."));					
						}
						else {
							// Clone the original context element, not to alter it, with all its context attributes.
							ContextElement seekedContextElement = this.getContextElement(entityId.getId());
							ContextElement returnedContextElement = seekedContextElement.clone();
							contextElementResponse.setContextElement(returnedContextElement);

							// If particular attributes are requested, let's filter them.
							if((queryContextRequest.getAttributeList() != null) && 
									(queryContextRequest.getAttributeList().getAttribute().size() > 0)  ) {						
							
								contextElementResponse.getContextElement().getContextAttributeList().getContextAttribute().clear();
							
								// Let's parse the attributes of the context element to only keep the ones that are required.
								for(ContextAttribute contextAttribute :	
									seekedContextElement.getContextAttributeList().getContextAttribute()) { // Nested for loop.
								
									if( Ngsi_9_10.attributeExists(queryContextRequest.getAttributeList(), contextAttribute.getName()) ) {
										returnedContextElement.getContextAttributeList().getContextAttribute().add(contextAttribute);
									}				
								} // End of the nested for loop
							} 
						
							contextElementResponse.setStatusCode(new StatusCode(Response.Status.OK.getStatusCode(), 
																									 Response.Status.OK.getReasonPhrase(), 
																								   null));
						}
					
					contextElementResponseList.getContextElementResponse().add(contextElementResponse);
				} // End of the global for loop
			} 
	    catch (UnacceptableSyntaxException e) {
	    	queryContextResponseCode = Response.Status.NOT_ACCEPTABLE.getStatusCode();
	    	queryContextResponseReasonPhrase = Response.Status.NOT_ACCEPTABLE.getReasonPhrase();
	    	queryContextResponseDetails = e.getMessage();
			} 
	    catch (UnimplementedFeatureException e) {
	    	queryContextResponseCode = Response.Status.UNAUTHORIZED.getStatusCode();
	    	queryContextResponseReasonPhrase = Response.Status.UNAUTHORIZED.getReasonPhrase();
	    	queryContextResponseDetails = e.getMessage();
			}
	    
	    if(queryContextResponseCode != Response.Status.OK.getStatusCode()) {
	    	
	    	StatusCode statusCode = new StatusCode();
	    	queryContextResponse.setErrorCode(statusCode);
	    	
	    	statusCode.setCode(queryContextResponseCode);
	    	statusCode.setDetails(queryContextResponseDetails);
	    	statusCode.setReasonPhrase(queryContextResponseReasonPhrase);
	    }
	    
	    return queryContextResponse;
	  }
//----------------------------------------------------------------------------------------------------------------------
	  public void reset() throws ObjectNotFoundException {

	  	this.contexElementList.getList().clear(); // Delete all context elements.
	  	
	  	this.contextSubscriptionList.getList().clear(); // Delete all context subscriptions.
	  	
	  	this.contextRegistrationListList.getList().clear(); // Delete all context registrations.

	  	super.deleteObservers(); // Delete potential observers.
	  }  
//----------------------------------------------------------------------------------------------------------------------
		protected boolean containsAttribute(AttributeList attributeList, String attribute) {

	  	for(String attributeName : attributeList.getAttribute()) {

	  		if(attributeName.equals(attribute)) return true;
	  	}
	  	return false;
	  }
//----------------------------------------------------------------------------------------------------------------------  
	  /**
	   * Implementation of the NGSI-10 updateContext method. It checks for entities existence, then flattens the 
	   * underlying ContextElements one by one, before sending them to CEP processing.
	   * @param updateContextRequest the object containing the updateContext request to process.
	   * @return UpdateContextResponse
	   * @throws SQLException
	   * @throws ClassNotFoundException
	   * @throws UnacceptableSyntaxException 
	   * @throws UnkownEntityIdException 
	   */
	  public UpdateContextResponse updateContext(UpdateContextRequest updateContextRequest) 
	  throws UnacceptableSyntaxException {
	  	
	  	this.checkUpdateContextRequest(updateContextRequest);
	  	
	  	ContextElementResponseList contextElementResponseList = new ContextElementResponseList();

	  	for(Object object : updateContextRequest.getContextElementList().getList()) {
	  		
	  		ContextElement contextElement = (ContextElement)object;
	  		
	  		logger.debug("Found ContextElement containing entity "+contextElement.getEntityId().getId()+" in the request");

	  		int contextElementResponseCode = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
	    	String contextElementResponseReasonPhrase = Response.Status.OK.getReasonPhrase(); 
	    	String contextElementResponseDetails = null;

	  		try { 
	  			this.checkContextElement(contextElement);
	  			contextElement.setTimeStamp(Calendar.getInstance().getTime());
	  			
	  			this.checkRegistrationDurationValidity(contextElement);

	  			// Event backup	  			  			
	  			this.contexElementList.getList().add(contextElement);

	  			// "Flatten" the ContextElement that is being parsed. "Flattening" consists in simplifying the deep (4 levels) 
	  			// ContextElement data structure by mapping its fields to a simpler 2-levels data structure. This allows 
	  			// simpler CEP EPL statement writing, without the need to have a knowledge of the NGSI data structures.
	  			String flattenedContextElement = NgsiEsperHelper.getFlattenedContextElement(contextElement);
	  			logger.debug("Flattened ContextElement:\n"+flattenedContextElement);

	  			// Send the flattened ContextElement as an event to the Esper library.
	  			ApplicationWideInstances.complexEventProcessing.sendXmlEventToEsper(flattenedContextElement);
	  		}
	  		catch (SAXException e) { 
	  			contextElementResponseCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	  			contextElementResponseReasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
	  			contextElementResponseDetails = e.getMessage();
	  		} 
	  		catch (ParserConfigurationException e) { 
	  			contextElementResponseCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	  			contextElementResponseReasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
	  			contextElementResponseDetails = e.getMessage();
	  		} 
	  		catch (IOException e) { 
	  			contextElementResponseCode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
	  			contextElementResponseReasonPhrase = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
	  			contextElementResponseDetails = e.getMessage();
	  		} 
	  		catch (UnkownEntityIdException e) {
	  			contextElementResponseCode = Response.Status.NOT_FOUND.getStatusCode();
	  			contextElementResponseReasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
	  			contextElementResponseDetails = e.getMessage();
				}
	  		catch(UnacceptableSyntaxException e) {
	  			contextElementResponseCode = Response.Status.NOT_ACCEPTABLE.getStatusCode();
	  			contextElementResponseReasonPhrase = Response.Status.NOT_ACCEPTABLE.getReasonPhrase();
	  			contextElementResponseDetails = e.getMessage();
	  		} 
	  		catch (UnknownEntityTypeException e) {
	  			contextElementResponseCode = Response.Status.NOT_FOUND.getStatusCode();
	  			contextElementResponseReasonPhrase = Response.Status.NOT_FOUND.getReasonPhrase();
	  			contextElementResponseDetails = "You are trying to update a context whose entity type does not exist. Please register the entity type you are referring to.";
				} 
	  		catch (UnsupportedFeatureException e) {
	  			contextElementResponseCode = Response.Status.UNAUTHORIZED.getStatusCode();
	  			contextElementResponseReasonPhrase = Response.Status.UNAUTHORIZED.getReasonPhrase();
	  			contextElementResponseDetails = e.getMessage();
				} 
	  		catch (ExpiredRegistrationException e) {
	  			contextElementResponseCode = Response.Status.GONE.getStatusCode();
	  			contextElementResponseReasonPhrase = Response.Status.GONE.getReasonPhrase();
	  			contextElementResponseDetails = "You are trying to update an entity ("+contextElement.getEntityId().getId()+") whose type registration ("+contextElement.getEntityId().getType()+") has expired.";
	  			
	  			logger.info("It appears that entity type \""+contextElement.getEntityId().getType()+"\" has expired.\nIt means it will not be possible to update the related \""+contextElement.getEntityId().getId()+"\" entity anymore.");
				}
	 
	  		// Build response for the current parsed ContextElement
	  		ContextElementResponse contextElementResponse = new ContextElementResponse();
	  		StatusCode statusCode = new StatusCode();

	  		statusCode.setCode(contextElementResponseCode);
	  		statusCode.setReasonPhrase(contextElementResponseReasonPhrase);
	  		statusCode.setDetails(contextElementResponseDetails);

	  		contextElementResponse.setStatusCode(statusCode);
	  		
	  		// Clone the resulting context element: it is necessary to hide the non-standard timeStamp property without
	  		// altering the original object.
	  		ContextElement contextElementResult = contextElement.clone();
	  		contextElementResponse.setContextElement(contextElementResult);
	    
	  		contextElementResponseList.getContextElementResponse().add(contextElementResponse);
	  	} // End of the for loop

	  	UpdateContextResponse updateContextResponse = new UpdateContextResponse();
	   	updateContextResponse.setContextResponseList(contextElementResponseList);  		

	  	return updateContextResponse;
	  }
//----------------------------------------------------------------------------------------------------------------------
		
		protected boolean removeContextRegistrationList( String registrationId ) {

			for( ContextRegistrationList contextRegistrationList : this.contextRegistrationListList.getList() ) {
				if(contextRegistrationList.getRegistrationId().equals(registrationId)) {
					this.contextRegistrationListList.getList().remove(contextRegistrationList);

					return true;
				}
			}
			return false;
		}		
}
