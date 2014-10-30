/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/


// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.6 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2013.01.04 � 05:59:51 PM CET 
//


package com.orange.espr4fastdata.oma.ngsidatastructures;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour NotifyConditionType.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="NotifyConditionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ONTIMEINTERVAL"/>
 *     &lt;enumeration value="ONVALUE"/>
 *     &lt;enumeration value="ONCHANGE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlRootElement
@XmlType(name = "NotifyConditionType")
@XmlEnum
public enum NotifyConditionType {

    ONTIMEINTERVAL,
    ONVALUE,
    ONCHANGE;

    public String value() {
        return name();
    }

    public static NotifyConditionType fromValue(String v) {
        return valueOf(v);
    }

}
