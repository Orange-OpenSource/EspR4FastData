/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.ngsicontext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notifyRecipients", propOrder ={"subscriptionId", "reference" })

public class NotifyRecipient {
	
	protected String reference;
	protected String subscriptionId;

	public String getReference() { return reference; }
	public void setReference(String reference) { this.reference = reference; }	

	public String getSubscriptionId() {	return subscriptionId; }
	public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }	
}