/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.mdps;

public class ObjectNotFoundException extends Exception{
  private static final long serialVersionUID = 1L;
    
  private String message = "The requested object was not found in the storage.";
  
  public ObjectNotFoundException() {}
  
  public ObjectNotFoundException(String message) { this.message = message; }  
  
  @Override
  public String getMessage() { return message; }
}
