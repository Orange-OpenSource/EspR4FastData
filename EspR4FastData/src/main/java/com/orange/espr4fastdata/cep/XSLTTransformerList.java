/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.cep;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XSLTTransformerList", propOrder = { "xsltTransformer" })

public class XSLTTransformerList {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(XSLTTransformerList.class);
	
  protected List<XSLTTransformer> xsltTransformer;
//----------------------------------------------------------------------------------------------------------------------
  public List<XSLTTransformer> getList() {
    if (this.xsltTransformer == null) this.xsltTransformer = new ArrayList<XSLTTransformer>();
    
    return this.xsltTransformer;
  }
}
