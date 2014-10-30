/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.commons;

import org.apache.log4j.Logger;

import com.mdps.DaoJsonFilesImpl;
import com.mdps.ObjectNotFoundException;
import com.mdps.ObservableList;
import com.mdps.ObservableListPersister;
import com.mdps.ReferenceFieldList;
import com.orange.espr4fastdata.cep.ComplexEventProcessing;
import com.orange.espr4fastdata.cep.EventType;
import com.orange.espr4fastdata.cep.EventTypeList;
import com.orange.espr4fastdata.cep.Statement;
import com.orange.espr4fastdata.cep.StatementList;
import com.orange.espr4fastdata.ngsicontext.ContextRegistrationListList;
import com.orange.espr4fastdata.ngsicontext.ContextSubscription;
import com.orange.espr4fastdata.ngsicontext.ContextSubscriptionList;
import com.orange.espr4fastdata.ngsicontext.Ngsi_9_10;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElement;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistrationList;

/**
 * @author Laurent ARTUSIO
 * This class features all application-wide variables with static getters and setters. *
 */
public class ApplicationWideInstances {
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ApplicationWideInstances.class);

	public static boolean isInitialized = false;
 
	// Declare the data persistence framework (for all classes that are persisted to disk and restored at run time)
	public static DaoJsonFilesImpl daoJsonFilesImpl;
	public static ObservableListPersister observableListPersister;		
	
	// Let's declare the common application properties.It should be noted that the data persistence framework only stores 
	// lists. So in order to save/restore a CommonProperties object, it is necessary to provide the storage system with
	// a dummy CommonPropertiesList object, even if it does not make sense from a programming perspective.
	public static CommonParameterList commonParameterList;
	public static ObservableList<CommonParameter> commonParameters; 
	
  // Declaration of Ngsi related instances
	public static ContextRegistrationListList contextRegistrationListList;
	public static ObservableList<ContextRegistrationList> contextRegistrationLists;
	
	public static ContextSubscriptionList contextSubscriptionList;
	public static ObservableList<ContextSubscription> contextSubscriptions;
	
	public static ContextElementList contextElementList;
	public static ObservableList<ContextElement> contextElements;
	
	public static Ngsi_9_10 ngsi;
	public static NgsiObserver ngsiObserver;
	
	// Declaration of non-standard Ngsi features
	public static NgsiExtensions ngsiExtensions;
	
	// Declaration of CEP-related objects
	public static EventTypeList eventTypeList;
	public static ObservableList<EventType> eventTypes;
	
	public static StatementList statementList;
	public static ObservableList<Statement> statements;
	
	public static ComplexEventProcessing complexEventProcessing;
	public static NgsiEventListener ngsiEventListener;
	
//----------------------------------------------------------------------------------------------------------------------
	
	public ApplicationWideInstances(String targetDirectory) {

		// Instantiate the data access object
		ApplicationWideInstances.daoJsonFilesImpl = new DaoJsonFilesImpl();
		ApplicationWideInstances.daoJsonFilesImpl.setTargetDirectory(targetDirectory);
		
		// Configure reference fields for all persisted objects. Reference fields are used to generate the storage file
		// names.
		ReferenceFieldList referenceFieldList = new ReferenceFieldList();
		referenceFieldList.getReferenceField().add("appName");
		ApplicationWideInstances.daoJsonFilesImpl.setReferenceFieldList(CommonParameter.class, referenceFieldList);
		
		referenceFieldList.getReferenceField().clear(); 
		referenceFieldList.getReferenceField().add("registrationId");
		ApplicationWideInstances.daoJsonFilesImpl.setReferenceFieldList(ContextRegistrationList.class, referenceFieldList);
		
		referenceFieldList.getReferenceField().clear(); 
		referenceFieldList.getReferenceField().add("subscriptionId");
		ApplicationWideInstances.daoJsonFilesImpl.setReferenceFieldList(ContextSubscription.class, referenceFieldList);
		
		referenceFieldList.getReferenceField().clear(); 
		referenceFieldList.getReferenceField().add("timeStamp");
		ApplicationWideInstances.daoJsonFilesImpl.setReferenceFieldList(ContextElement.class, referenceFieldList);

		referenceFieldList.getReferenceField().clear(); 
		referenceFieldList.getReferenceField().add("name");
		ApplicationWideInstances.daoJsonFilesImpl.setReferenceFieldList(com.orange.espr4fastdata.cep.EventType.class, 
																																		referenceFieldList);
		referenceFieldList.getReferenceField().clear(); 
		referenceFieldList.getReferenceField().add("name");
		ApplicationWideInstances.daoJsonFilesImpl.setReferenceFieldList(Statement.class, referenceFieldList);
		
		// Instantiate the ObservableListPersister object that provide "List" objects with persistence features.------------		
		ApplicationWideInstances.observableListPersister = new ObservableListPersister();		
		ApplicationWideInstances.observableListPersister.setDao(ApplicationWideInstances.daoJsonFilesImpl);	
		
		// Instantiate all persisted List objects with a reference to the persistence object.-------------------------------
		ApplicationWideInstances.commonParameterList = new CommonParameterList();
		ApplicationWideInstances.commonParameters = 
				new ObservableList<CommonParameter>(ApplicationWideInstances.observableListPersister);
		ApplicationWideInstances.commonParameterList.setList(commonParameters);
				
		ApplicationWideInstances.contextRegistrationListList = new ContextRegistrationListList();
		ApplicationWideInstances.contextRegistrationLists = 
				new ObservableList<ContextRegistrationList>(ApplicationWideInstances.observableListPersister);
		ApplicationWideInstances.contextRegistrationListList.setList(contextRegistrationLists);
				
		ApplicationWideInstances.contextSubscriptionList = new ContextSubscriptionList();
		ApplicationWideInstances.contextSubscriptions = 
				new ObservableList<ContextSubscription>(ApplicationWideInstances.observableListPersister);
		ApplicationWideInstances.contextSubscriptionList.setList(contextSubscriptions);
		
		ApplicationWideInstances.contextElementList = new ContextElementList();
		ApplicationWideInstances.contextElements = 
				new ObservableList<ContextElement>(ApplicationWideInstances.observableListPersister);
		ApplicationWideInstances.contextElementList.setList(contextElements);
		
		ApplicationWideInstances.eventTypeList = new EventTypeList();
		ApplicationWideInstances.eventTypes = 
				new ObservableList<EventType>(ApplicationWideInstances.observableListPersister);
		ApplicationWideInstances.eventTypeList.setList(eventTypes);
		
		ApplicationWideInstances.statementList = new StatementList();
		ApplicationWideInstances.statements = 
				new ObservableList<Statement>(ApplicationWideInstances.observableListPersister);
		ApplicationWideInstances.statementList.setList(statements); 
		
		// Add all "List" persisted objects to the ListObserverPersistance object, so the lists are monitored and persisted.
		ApplicationWideInstances.observableListPersister.addObservableList(commonParameters, CommonParameter.class);
		ApplicationWideInstances.observableListPersister.addObservableList(contextRegistrationLists, ContextRegistrationList.class);
		ApplicationWideInstances.observableListPersister.addObservableList(contextElements, ContextElement.class);
		ApplicationWideInstances.observableListPersister.addObservableList(contextSubscriptions, ContextSubscription.class);
		ApplicationWideInstances.observableListPersister.addObservableList(eventTypes, EventType.class);
		ApplicationWideInstances.observableListPersister.addObservableList(statements, Statement.class);

		// Instantiate + init main classes ---------------------------------------------------------------------------------
		
		ApplicationWideInstances.ngsiExtensions = new NgsiExtensions();
		ApplicationWideInstances.ngsiEventListener = new NgsiEventListener();
		ApplicationWideInstances.complexEventProcessing = new ComplexEventProcessing();
		ApplicationWideInstances.ngsiObserver = new NgsiObserver();
		ApplicationWideInstances.ngsi = new Ngsi_9_10();
		
		ApplicationWideInstances.complexEventProcessing.setEventTypeList(ApplicationWideInstances.eventTypeList);
		ApplicationWideInstances.complexEventProcessing.setStatementList(ApplicationWideInstances.statementList);
		ApplicationWideInstances.complexEventProcessing.setStatementAwareUpdateListener(ngsiEventListener);

		ApplicationWideInstances.ngsiObserver.complexEventProcessing = ApplicationWideInstances.complexEventProcessing;

		ApplicationWideInstances.ngsi.setContextRegistrationListList(ApplicationWideInstances.contextRegistrationListList);
		ApplicationWideInstances.ngsi.setContexElementList(ApplicationWideInstances.contextElementList);
		ApplicationWideInstances.ngsi.setContextSubscriptionList(ApplicationWideInstances.contextSubscriptionList);
		ApplicationWideInstances.ngsi.addObserver(ngsiObserver);
		
		ApplicationWideInstances.ngsiEventListener.complexEventProcessing = ApplicationWideInstances.complexEventProcessing;
		ApplicationWideInstances.ngsiEventListener.ngsi = ApplicationWideInstances.ngsi;
	}	
//----------------------------------------------------------------------------------------------------------------------

	public static synchronized void resetWholeApplication() throws ObjectNotFoundException {
		
		// Cleanly destroy the Esper library
		ApplicationWideInstances.complexEventProcessing.destroyEsper();
		
		// Backup the common parameters before clearing the lists
		CommonParameter commonParameterBackup = ((CommonParameter)ApplicationWideInstances.commonParameters.get(0)).clone();
				
		// Clear all persisted lists. The deletion is propagated to the storage.
		ApplicationWideInstances.observableListPersister.clearLists();
		
		ApplicationWideInstances.commonParameters.add(commonParameterBackup);
	}	
}