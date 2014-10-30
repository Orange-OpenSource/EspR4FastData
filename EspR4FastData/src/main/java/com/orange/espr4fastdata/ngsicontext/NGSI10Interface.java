/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.ngsicontext;

import java.sql.SQLException;

import com.orange.espr4fastdata.oma.ngsidatastructures.QueryContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.QueryContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.SubscribeContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.SubscribeContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.UnsubscribeContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.UnsubscribeContextResponse;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateContextRequest;
import com.orange.espr4fastdata.oma.ngsidatastructures.UpdateContextResponse;

public interface NGSI10Interface {
  
	public QueryContextResponse queryContext(QueryContextRequest queryContextRequest) 
																																												 throws SQLException, 
  																																												 			ClassNotFoundException;

  public SubscribeContextResponse subscribeContext(SubscribeContextRequest subscribeContextRequest);
  
  public UnsubscribeContextResponse unsubscribeContext(UnsubscribeContextRequest unsubscribeContextRequest);
  
  public UpdateContextResponse updateContext(UpdateContextRequest updateContextRequest) 
  																																									throws SQLException, 
  																																												 UnacceptableSyntaxException, 
  																																												 UnkownEntityIdException;	
}
