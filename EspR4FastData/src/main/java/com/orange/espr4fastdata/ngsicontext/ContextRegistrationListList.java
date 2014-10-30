package com.orange.espr4fastdata.ngsicontext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mdps.MdpsList;
import com.mdps.ObservableList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementList;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextRegistrationList;

//----------------------------------------------------------------------------------------------------------------------
/**
 * @author Laurent Artusio
 * This class is a list of ContextRegistrationList. A complete NGSI-9 registration can feature many ContextRegistration
 * at once, and there will be a single registration id for all the context registrations.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContextRegistrationListList", propOrder = { "contextRegistrationList" })
public class ContextRegistrationListList implements MdpsList {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ContextElementList.class);

	@JsonProperty("contextRegistrationList")
	ObservableList<ContextRegistrationList> contextRegistrationList;
	
//----------------------------------------------------------------------------------------------------------------------	

	public void setList(ObservableList<ContextRegistrationList> contextRegistrationLists ) {
		this.contextRegistrationList = contextRegistrationLists;
	}		
	
	
	@JsonProperty("contextRegistrationList")
	public ObservableList<ContextRegistrationList> getList() {
		return this.contextRegistrationList;
	}
}
