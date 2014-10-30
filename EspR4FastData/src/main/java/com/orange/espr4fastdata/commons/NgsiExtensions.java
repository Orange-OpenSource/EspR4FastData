/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.commons;


import java.util.ArrayList;
import java.util.Date;

import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;

import com.mdps.ObjectNotFoundException;
import com.mdps.ObservableList;
import com.orange.espr4fastdata.ngsicontext.Ngsi_9_10;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElement;
import com.orange.espr4fastdata.oma.ngsidatastructures.ContextElementList;

//----------------------------------------------------------------------------------------------------------------------

/**
 * @author elag7340
 * This class implements convenient features that are not compliant with the original OMA specification. Don't use them
 * if you want to retain true NGSI compliance.
 */
public class NgsiExtensions {
	
	private static Logger logger = Logger.getLogger(NgsiExtensions.class);
	
	protected Ngsi_9_10 ngsi;
	
//----------------------------------------------------------------------------------------------------------------------

	public Ngsi_9_10 getNgsi() {	return ngsi; }
	
//----------------------------------------------------------------------------------------------------------------------
	
  public ContextElementList readContextElements() {
  	return ApplicationWideInstances.ngsi.getContexElementList();
  }
  
//----------------------------------------------------------------------------------------------------------------------
  /**
   * Returns all stored context elements that have their timestamp comprised between "timeStampInMs" and now.
   * @param timeStamp the timestamp from which to return the requested context elements.
   * @return a ContextElementList object.
   * @throws ObjectNotFoundException
   */
  public ContextElementList getContextElements( @PathParam("timeStamp") Date timeStamp) 
  		throws ObjectNotFoundException {
  	
  	int lastContextElementIndex = 
  			ApplicationWideInstances.ngsi.getContexElementList().getList().size() - 1;

  	if(lastContextElementIndex < 0) 
  		return new ContextElementList();
  	
  	// Most recent timestamp of all context elements.
  	Date latestTimeStamp = ApplicationWideInstances.ngsi.getContexElementList().getList().get(lastContextElementIndex).
  												 getTimeStamp();
  	
  	ArrayList<Date> timeStampParams = new ArrayList<Date>(); // Parameter for the following read method.
  	timeStampParams.add(timeStamp); // Requested time stamp.
  	timeStampParams.add(latestTimeStamp); // Most recent timestamp of all context elements.

  	ContextElementList contextElementListResult = new ContextElementList();
  	ObservableList<ContextElement> contextElements = new ObservableList<ContextElement>();
  	contextElementListResult.setList(contextElements);
  	
  	for(ContextElement contextElement : ApplicationWideInstances.ngsi.getContexElementList().getList() ) {
  		
  		if(contextElement.getTimeStamp().after(timeStamp))
  			contextElementListResult.getList().add(contextElement);
  	}

  	
  	logger.info("About to return "+contextElementListResult.getList().size()+" context elements.");

  	return contextElementListResult;
  }
//----------------------------------------------------------------------------------------------------------------------

	public void setNgsi(Ngsi_9_10 ngsi) { this.ngsi = ngsi; }
	
//----------------------------------------------------------------------------------------------------------------------	
}
