/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.mdps;

public class StorageNotAvailableException extends Exception{
  private static final long serialVersionUID = 1L;
    
  private String message = "The storage entity you are accessing is not available. For example, this error can be related to a missing data file or some unavailable table in a database.";
  
  public StorageNotAvailableException() {}
  
  public StorageNotAvailableException(String message) { this.message = message; }  
  
  @Override
  public String getMessage() { return message; }
}
