/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.ngsicontext;

public class EntitiesAreNotOfTheSameTypeException extends Exception {
  private static final long serialVersionUID = 1L;
  
  @Override
  public String getMessage() { return "When registering multiple entities, they must be of the same type."; }     

}
