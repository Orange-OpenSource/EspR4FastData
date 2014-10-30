/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.jaxrsendpoints;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.orange.espr4fastdata.commons.ApplicationWideInstances;
import com.orange.espr4fastdata.commons.Utils;
import com.orange.espr4fastdata.ngsicontext.UnacceptableSyntaxException;
import com.orange.espr4fastdata.oma.ngsidatastructures.AppendContextElementRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextAttribute;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextAttributeList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextAttributeResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElement;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementResponseList;
import com.orange.espr4fastdata.oma.ngsidatastructures.EntityId;
import com.orange.espr4fastdata.oma.ngsidatastructures.QueryContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.QueryContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.StatusCode;
import com.orange.espr4fastdata.oma.ngsidatastructures.SubscribeContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.SubscribeContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.UnsubscribeContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.UnsubscribeContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateActionType;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateContextElementRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateContextResponse;

/**
 * This class is the entry point for <a href="http://technical.openmobilealliance.org/Technical/release_program/docs/CopyrightClick.aspx?pck=NGSI&file=V1_0-20120529-A/OMA-TS-NGSI_Context_Management-V1_0-20120529-A.pdf">
 * OMA NGSI-10</a>methods.
 * @author Laurent ARTUSIO, Laurence DUPONT
 */

@Path("/NGSI10")
public class BackwardCompNgsi10EndPoint extends JaxRSCommons{

  private static Logger logger = Logger.getLogger(BackwardCompNgsi10EndPoint.class);
    
//----------------------------------------------------------------------------------------------------------------------
  public BackwardCompNgsi10EndPoint(){ super(); }
//----------------------------------------------------------------------------------------------------------------------
/**
 * @param subscribeContextRequest the object that contains the entities to subscribe to, the requested attributesand 
 * and the subscription duration. The restriction, notifyCondition and throttling parameters are not implemented
 * @return a SubscribeContextResponse object.
 */
  @Path("/subscribeContext")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)

  public SubscribeContextResponse subscribeContextEndPoint(SubscribeContextRequest subscribeContextRequest) {  	
    	
    if(!ApplicationWideInstances.isInitialized) super.init();

    logger.debug("The following payload was received:\n"+Utils.objectToFormattedXmlString(subscribeContextRequest));
    
  	SubscribeContextResponse subscribeContextResponse = 
  			ApplicationWideInstances.ngsi.subscribeContext(subscribeContextRequest);
  	
  	if(subscribeContextResponse.getSubscribeError() != null) {
      throw new 
        WebApplicationException(Response.status(subscribeContextResponse.getSubscribeError().getErrorCode().getCode()).
      	entity(subscribeContextResponse).type(MediaType.APPLICATION_XML).build());
  	}
  	
  	return subscribeContextResponse;
  }
//----------------------------------------------------------------------------------------------------------------------
  /**
   * Implementation of the NGSI-10 unsubscribeContext method. This methods is a REST entry point. It receives an 
   * UnsubscribeContextRequest payload and call the unsubscribeContext application logic method.
   * @param unsubscribeContextRequest
   * @return an UnsubscribeContextResponse object that is unmarshaled.
   */
  @Path("/unsubscribeContext")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)

  public UnsubscribeContextResponse unsubscribeContextEndPoint(UnsubscribeContextRequest unsubscribeContextRequest) {
    
  	if(!ApplicationWideInstances.isInitialized) super.init();
  	
  	logger.debug("Received the following unsubscribeContext payload:\n"+Utils.objectToFormattedXmlString(unsubscribeContextRequest));
  	
  	UnsubscribeContextResponse unsubscribeContextResponse = 
  			ApplicationWideInstances.ngsi.unsubscribeContext(unsubscribeContextRequest);
  	
  	if(unsubscribeContextResponse.getStatusCode().getCode() != Response.Status.OK.getStatusCode()) {
      throw new 
        WebApplicationException(Response.status(unsubscribeContextResponse.getStatusCode().getCode()).
      	entity(unsubscribeContextResponse).type(MediaType.APPLICATION_XML).build());
  	}
  	
  	return unsubscribeContextResponse;  	
  }
//----------------------------------------------------------------------------------------------------------------------  
/**
 * Implementation of the NGSI-10 updateContext method. This methods is a REST entry point. It receives an 
 * updateContextRequest payload and call the updateContext application logic method.
 * @param updateContextRequest UpdateContextRequest object initially received as XML and marshaled as POJO. 
 * @return UpdateContextResponse UpdateContextResponse object that is unmarshaled to XML representation.
 */
  @Path("/updateContext")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public UpdateContextResponse updateContextEndPoint(String updateContextRequestString) {

    if(!ApplicationWideInstances.isInitialized) super.init(); 

    logger.debug("The following payload was received:\n"+updateContextRequestString);
    
    UpdateContextResponse updateContextResponse = null;
		UpdateContextResponse updateContextResponseWithError = new UpdateContextResponse();

		
   	try {
      UpdateContextRequest updateContextRequest =
      		(UpdateContextRequest) Utils.xmlStringToObject(updateContextRequestString, UpdateContextRequest.class);

   		updateContextResponse = ApplicationWideInstances.ngsi.updateContext(updateContextRequest);
		} 
   	catch (UnacceptableSyntaxException e) {
   		updateContextResponseWithError.setErrorCode(new StatusCode(Response.Status.NOT_ACCEPTABLE.getStatusCode(),
					 																	 										 Response.Status.NOT_ACCEPTABLE.getReasonPhrase(),
					 																	 										 e.getMessage()));   		
   		
      throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE.getStatusCode()).
      					entity(updateContextResponseWithError).type(MediaType.APPLICATION_XML).build());
		} 
   	catch (InstantiationException e) {
   		updateContextResponseWithError.setErrorCode(new StatusCode(Response.Status.BAD_REQUEST.getStatusCode(),
						 																			Response.Status.BAD_REQUEST.getReasonPhrase(),
						 																			e.getMessage()));   		

   		throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode()).
   							entity(updateContextResponseWithError).type(MediaType.APPLICATION_XML).build());
		} 
   	catch (IllegalAccessException e) {
   		updateContextResponseWithError.setErrorCode(new StatusCode(Response.Status.BAD_REQUEST.getStatusCode(),
   																								Response.Status.BAD_REQUEST.getReasonPhrase(),
   																								e.getMessage()));   		

   		throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode()).
   																			entity(updateContextResponseWithError).type(MediaType.APPLICATION_XML).build());
		} 
   	catch (JAXBException e) {
   		updateContextResponseWithError.setErrorCode(new StatusCode(Response.Status.BAD_REQUEST.getStatusCode(),
   																								Response.Status.BAD_REQUEST.getReasonPhrase(),
   																								e.getMessage()));   		

   		throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode()).
   							entity(updateContextResponseWithError).type(MediaType.APPLICATION_XML).build());
		}
   	
   return updateContextResponse;
  }
//----------------------------------------------------------------------------------------------------------------------
/**
 * @param queryContextRequest the request object that features requested entities, requested attribute names, 
 * 				and restrictions. 
 * @return a QueryContextResponse object featuring the requested entities and attributes values.
 */
  @Path("/queryContext")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public QueryContextResponse queryContextEndPoint(String queryContextRequestString) {

    if(!ApplicationWideInstances.isInitialized) super.init();

    logger.info("The following payload was received:\n"+queryContextRequestString);
        
    int code = Response.Status.OK.getStatusCode(); // Set the error code to 200 (OK) by default.
    String reasonPhrase = Response.Status.OK.getReasonPhrase(); // Same as above, but for the error message
    String details = null;
    QueryContextResponse queryContextResponse = new QueryContextResponse();

    QueryContextRequest queryContextRequest = null;
    QueryContextResponse queryContextResponseError = new QueryContextResponse();
    
    try {
			queryContextRequest = (QueryContextRequest) Utils.xmlStringToObject(queryContextRequestString, QueryContextRequest.class);

			queryContextResponse = ApplicationWideInstances.ngsi.queryContext(queryContextRequest);

	    if(code != Response.Status.OK.getStatusCode()) {
	      StatusCode errorCode = new StatusCode();
	      queryContextResponse.setErrorCode(errorCode);
	    	queryContextResponse.setContextResponseList(new ContextElementResponseList());
	      errorCode.setCode(code);
	      errorCode.setReasonPhrase(reasonPhrase);
	      errorCode.setDetails(details);
	      
	      throw new WebApplicationException(Response.status(code).entity(queryContextResponse)
	      		.type(MediaType.APPLICATION_XML).build());
	    }	    
	    return queryContextResponse;			
		} 
    catch (InstantiationException e) {
    	queryContextResponseError.setErrorCode(new StatusCode(Response.Status.BAD_REQUEST.getStatusCode(),
						 																 								Response.Status.BAD_REQUEST.getReasonPhrase(),
						 																 								e.getMessage()));   		

    	throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode()).
    						entity(queryContextResponseError).type(MediaType.APPLICATION_XML).build());
		} 
    catch (IllegalAccessException e) {
    	queryContextResponseError.setErrorCode(new StatusCode(Response.Status.BAD_REQUEST.getStatusCode(),
    																												Response.Status.BAD_REQUEST.getReasonPhrase(),
    																												e.getMessage()));   		

    	throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode()).
    						entity(queryContextResponseError).type(MediaType.APPLICATION_XML).build());
		} 
    catch (JAXBException e) {
    	queryContextResponseError.setErrorCode(new StatusCode(Response.Status.BAD_REQUEST.getStatusCode(),
    																												Response.Status.BAD_REQUEST.getReasonPhrase(),
    																												e.getMessage()));   		

    	throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST.getStatusCode()).
    						entity(queryContextResponseError).type(MediaType.APPLICATION_XML).build());
		}   
  }
//----------------------------------------------------------------------------------------------------------------------
// TODO:javadoc
  @Path("/contextEntities/{entityId}/{type}")
  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public UpdateContextResponse updateEntityIdContext(UpdateContextElementRequest acer, 
		  						@PathParam("entityId") String entityId,
		  						@PathParam("type") String type,
		  						@DefaultValue("true") @QueryParam("sendEvent") boolean inCEP) {
    
	if(!ApplicationWideInstances.isInitialized) super.init();
    logger.debug("PUT contextEntities/entityId="+entityId);
    logger.debug("payload attdomainname"+acer.getAttributeDomainName());
    UpdateContextRequest upReq = new UpdateContextRequest();
    //LD
    //create ContextElement with 
    //id in PathParam entityId
    //attributeDomainName, contextAttributeList and domainMetadata in payload
    ContextElementList cel = new ContextElementList();

    ContextElement ce = new ContextElement();
    EntityId eId = new EntityId();
    eId.setId(entityId);
    eId.setType(type);
    ce.setEntityId(eId);
    ce.setAttributeDomainName(acer.getAttributeDomainName());
    ce.setContextAttributeList(acer.getContextAttributeList());
    ce.setDomainMetadata(acer.getDomainMetadata());
      
    //ContextElement mis en List
    cel.getList().add(ce);
      
    upReq.setContextElementList(cel);
    upReq.setUpdateAction(UpdateActionType.UPDATE);
    logger.debug("call updateContext with request"+upReq.toString());

    UpdateContextResponse ucr = null;
		try {
			ucr = ApplicationWideInstances.ngsi.updateContext(upReq);
		} 
		 
		catch (UnacceptableSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    // TODO must return a UpdateContextElementResponse
    // ContextElementResponseList ceUcr = ucr.getContextResponseList().getContextElementResponse().get(0).getContextElement();
    // UpdateContextElementResponse ucer = new UpdateContextElementResponse();
    // ucr.setContextResponseList(ceUcr.);
    ucr.setErrorCode(ucr.getErrorCode());
    return ucr;
  }
//----------------------------------------------------------------------------------------------------------------------
// TODO:javadoc
  @Path("/contextEntities/{entityId}/{type}")
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public UpdateContextResponse updateEntityIdContext(AppendContextElementRequest acer, 
		  						                                   @PathParam("entityId") String entityId,
		  						                                   @PathParam("type") String type,
		  						                                   @DefaultValue("true") @QueryParam("sendEvent") boolean inCEP) {
    
	  if(!ApplicationWideInstances.isInitialized) super.init();
    logger.debug("POST contextEntities/entityId="+entityId);
    logger.debug("payload attdomainname"+acer.getAttributeDomainName());
    UpdateContextRequest upReq = new UpdateContextRequest();
      //LD
      //create ContextElement with 
      //id in PathParam entityId
      //attributeDomainName, contextAttributeList and domainMetadata in payload
      ContextElementList cel = new ContextElementList();
      
      ContextElement ce = new ContextElement();
      EntityId eId = new EntityId();
      eId.setId(entityId);
      eId.setType(type);
      ce.setEntityId(eId);
      ce.setAttributeDomainName(acer.getAttributeDomainName());
      ce.setContextAttributeList(acer.getContextAttributeList());
      ce.setDomainMetadata(acer.getDomainMetadata());
      
      //ContextElement mis en List
      cel.getList().add(ce);
      
      upReq.setContextElementList(cel);
      upReq.setUpdateAction(UpdateActionType.APPEND);
      logger.debug("call updateContext with request"+upReq.toString());
      //TO DO must return a AppendContextElementResponse
      try {
				return ApplicationWideInstances.ngsi.updateContext(upReq);
			} 
			catch (UnacceptableSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      return new UpdateContextResponse();
  }
  
//----------------------------------------------------------------------------------------------------------------------
// TODO:javadoc
  @Path("/contextEntities/{entityId}")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  public QueryContextResponse queryEntityIdContext(@PathParam("entityId") String entityId) {
    logger.debug("---- BEGIN QUERYCONTEXT");
    if(!ApplicationWideInstances.isInitialized) super.init();

    ContextElementResponseList cerl = new ContextElementResponseList();
    try {
      logger.debug("Ngsi10.queryContext:BEGIN / About to parse requested entities");
      
      EntityId ei = new EntityId();
        
      logger.debug("Ngsi10.queryContext:requested entity is"+entityId);
      ContextElementResponse cer = new ContextElementResponse();
      ContextElement ce = new ContextElement(); 
      cer.setContextElement(ce);
      cer.setStatusCode(new StatusCode());
      cerl.getContextElementResponse().add(cer);
        
      //ContextElementResponse attcer = cerl.getContextElementResponse().get(0);
      //ContextAttributeList myresp = attcer.getContextElement().getContextAttributeList();
    }
    catch (Exception e) { System.err.println(e.getMessage()); }

    QueryContextResponse qcr = new QueryContextResponse();
    qcr.setContextResponseList(cerl);
    qcr.setErrorCode(new StatusCode());
    
    return qcr;
  }
//----------------------------------------------------------------------------------------------------------------------
// TODO:javadoc
  @Path("/contextEntities/{entityId}/attributes")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  public ContextAttributeResponse queryAttributeContext(@PathParam("entityId") String entityId) {
    logger.debug("---- BEGIN QUERYCONTEXT");
    if(!ApplicationWideInstances.isInitialized) super.init();
    
    ContextAttributeResponse contextAttributeResponse = new ContextAttributeResponse();
    try {
      logger.debug("Ngsi10.queryContext:BEGIN / About to parse requested entities");
      
      EntityId ei = new EntityId();
        
      logger.debug("Ngsi10.queryContext:requested entity is"+entityId);
      ContextElementResponse cer = new ContextElementResponse();
      ContextElement ce = new ContextElement(); 
      cer.setContextElement(ce);

      contextAttributeResponse.setStatusCode(new StatusCode());
      contextAttributeResponse.setContextAttributeList(cer.getContextElement().getContextAttributeList());
    }
    catch (Exception e) { System.err.println(e.getMessage()); }

    return contextAttributeResponse;
  }
//----------------------------------------------------------------------------------------------------------------------
// TODO:javadoc
  @Path("/contextEntities/{entityId}/attributes/{attributeName}")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  public ContextAttributeResponse queryAttributeNameContext(@PathParam("entityId") String entityId,
		  			@PathParam("attributeName") String attName) {
    logger.debug("---- BEGIN QUERYCONTEXT");
    if(!ApplicationWideInstances.isInitialized) super.init();

    //ContextElementResponseList cerl = new ContextElementResponseList();
    ContextAttributeResponse qcar = new ContextAttributeResponse();
    try {
      logger.debug("Ngsi10.queryContext:BEGIN / About to parse requested entities");

      EntityId ei = new EntityId();
        
      logger.debug("Ngsi10.queryContext:requested entity is"+entityId);
      ContextElementResponse cer = new ContextElementResponse();
      ContextElement ce = new ContextElement(); 
      cer.setContextElement(ce);
      List<ContextAttribute> listAtt= cer.getContextElement().getContextAttributeList().getContextAttribute();
        
      //find attName specified in param
      logger.debug("Ngsi10.queryContext:requested attName is"+attName);
      ContextAttributeList resAtt= new ContextAttributeList();

      for (ContextAttribute ca:listAtt) {
     		if (ca.getName().equals(attName)) {
     			logger.debug("Ngsi10.queryContext:att is"+ca.getName());
     			resAtt.getContextAttribute().add(ca);
     		}   		
      }
        qcar.setStatusCode(new StatusCode());
        qcar.setContextAttributeList(resAtt);
    }
    catch (Exception e) { System.err.println(e.getMessage()); }
    
    return qcar;
  }
}
