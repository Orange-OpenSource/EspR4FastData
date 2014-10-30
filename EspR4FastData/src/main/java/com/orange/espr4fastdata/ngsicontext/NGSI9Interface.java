/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.ngsicontext;

import com.orange.espr4fastdata.oma.ngsidatastructures.RegisterContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.RegisterContextResponse;

public interface NGSI9Interface {

	public Boolean entityIdIdIsRegistered(String entityIdId);
	public Boolean entityIdTypeIsRegistered(String entityIdType);
  public RegisterContextResponse registerContext(RegisterContextRequest registerContextRequest);
}
