/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.oma.ngsidatastructures;

import java.util.Date;
import java.util.Observable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

//----------------------------------------------------------------------------------------------------------------------

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContextElement", propOrder = {
		"timeStamp",
    "entityId",
    "attributeDomainName",
    "contextAttributeList",
    "domainMetadata"
})
//----------------------------------------------------------------------------------------------------------------------

public class ContextElement extends Observable implements Cloneable {
        
    protected Date timeStamp;
    
    @XmlElement(required = true)
    protected EntityId entityId;
    
    protected String attributeDomainName;
    protected ContextAttributeList contextAttributeList;
    protected ContextMetadataList domainMetadata;

    public String getAttributeDomainName() { return attributeDomainName; }
    public ContextAttributeList getContextAttributeList() { return contextAttributeList; }
    public ContextMetadataList getDomainMetadata() { return this.domainMetadata; }
    public EntityId getEntityId() { return entityId; }
    public Date getTimeStamp() {	return this.timeStamp; }

    public void setAttributeDomainName(String value) { 
    	this.attributeDomainName = value; 
  		super.setChanged();
  		super.notifyObservers();
    }
    
    public void setContextAttributeList(ContextAttributeList value) { 
    	this.contextAttributeList = value; 
  		super.setChanged();
  		super.notifyObservers();
    }

    public void setDomainMetadata(ContextMetadataList value) { 
    	this.domainMetadata = value; 
  		super.setChanged();
  		super.notifyObservers();
    }

    public void setEntityId(EntityId value) { 
    	this.entityId = value; 
  		super.setChanged();
  		super.notifyObservers();
    }

		public void setTimeStamp(Date timeStamp) {	
			this.timeStamp = timeStamp;	
			super.setChanged();
			super.notifyObservers();
	  }

//----------------------------------------------------------------------------------------------------------------------
		
		public ContextElement clone() {
			ContextElement clonedContextElement = new ContextElement();
			
			// Process the entity id.
			if(this.entityId != null) {
				clonedContextElement.setEntityId(new EntityId());
				
				if(this.entityId.getId() != null) 
					clonedContextElement.getEntityId().setId( new String(this.entityId.getId()) );
				
				if(this.entityId.getType() != null) 
					clonedContextElement.getEntityId().setType( new String(this.entityId.getType()) );				
			}
			
			// Process the attribute domain name.
			if(this.attributeDomainName != null) clonedContextElement.attributeDomainName = new String(attributeDomainName);
			
			// Process the domain metadata.
			if( this.domainMetadata != null ) {
				for( ContextMetadata contextMetadata : this.domainMetadata.getContextMetadata()) {
					
					ContextMetadata clonedContextMetadata = new ContextMetadata();
					
					if(contextMetadata.getName() != null) clonedContextMetadata.setName(new String(contextMetadata.getName()));
					
					if(contextMetadata.getType() != null) clonedContextMetadata.setType(new String(contextMetadata.getType()));
					
					if(contextMetadata.getValue() != null) clonedContextMetadata.setValue(new String(contextMetadata.getValue()));
					
					clonedContextElement.setDomainMetadata(new ContextMetadataList());
					clonedContextElement.getDomainMetadata().getContextMetadata().add(clonedContextMetadata);
				}
			}
			
			if(this.contextAttributeList != null) {
				
				clonedContextElement.setContextAttributeList(new ContextAttributeList());
				
				// Process the context attribute list.
				for(ContextAttribute contextAttribute : this.contextAttributeList.getContextAttribute() ) {
				 
					ContextAttribute clonedContextAttribute = new ContextAttribute();
				
					if( contextAttribute.getContextValue() != null) 
						clonedContextAttribute.setContextValue(new String(contextAttribute.getContextValue()));
				
					if( contextAttribute.getName() != null)
						clonedContextAttribute.setName(new String(contextAttribute.getName()));
				
					if( contextAttribute.getType() != null) 
						clonedContextAttribute.setType(new String(contextAttribute.getType()));
				
					// Process the metadata inside the context attribute.
					if(contextAttribute.getMetadata() != null) {
							
						for(ContextMetadata contextMetadata : contextAttribute.getMetadata().getContextMetadata()) {
						
							ContextMetadata clonedContextMetadata = new ContextMetadata();
						
							if( contextMetadata.getName() != null) 
								clonedContextMetadata.setName(new String(contextMetadata.getName()));
							
							if( contextMetadata.getType() != null) 
								clonedContextMetadata.setType(new String(contextMetadata.getType()));	
						
							if( contextMetadata.getValue() != null)
								clonedContextMetadata.setValue(new String(contextMetadata.getValue()));	
						
							contextAttribute.setMetadata(new ContextMetadataList());
							contextAttribute.getMetadata().getContextMetadata().add(clonedContextMetadata);
						}
					}
					clonedContextElement.getContextAttributeList().getContextAttribute().add(clonedContextAttribute);
				}		
			}
			return clonedContextElement;
		}		
}