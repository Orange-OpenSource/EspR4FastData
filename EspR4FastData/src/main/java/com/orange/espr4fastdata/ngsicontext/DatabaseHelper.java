/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.ngsicontext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class DatabaseHelper {
  private static Logger logger = Logger.getLogger(DatabaseHelper.class);

  protected Statement s;
  protected Connection c;
  //------------------------------------------------------------------------------------------------------  
  public DatabaseHelper(Connection c) throws SQLException {
    try { 
      this.c=c;
      c.setAutoCommit(false);
      this.s = c.createStatement(); 
      s.setQueryTimeout(30); } 
    catch (SQLException e) { throw new SQLException(e); }
  }
  //------------------------------------------------------------------------------------------------------
  // Return the database row id of the named entity
  public int getEntityRowId(String entityName) throws SQLException {
    ResultSet rs;

    try { 
      String stmt = "select EI_ID from EntityIds where id='"+entityName+"';"; //logger.debug("about to execute \""+stmt+"\"");
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getInt("EI_ID");
    }
    catch(SQLException e) { throw new SQLException(e); }
    return 0;
  }
  //------------------------------------------------------------------------------------------------------
  // Return the database row id of the most recent ContextElement, associated with a particular Entity
  // TODO: sort by timestamp and return the most recent ContextElement
  public int getContextElementRowId(String entityName) throws SQLException {
    ResultSet rs;
    
    int ei_rowid = getEntityRowId(entityName);

    try { 
      String stmt = "select CE_ID from ContextElements where EI_ID='"+ei_rowid+"' ORDER BY timestamp DESC LIMIT 0,1;"; logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getInt("CE_ID");
    }
    catch(SQLException e) { throw new SQLException(e); }
    return 0;
  }
  //------------------------------------------------------------------------------------------------------
  // Return the id attribute of the specified EntityId
  public String getEntityIdId(int EI_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select id from EntityIds where EI_ID='"+EI_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getString("id");
    }
    catch(SQLException e) { throw new SQLException(e); }
    return null;
  }  
  //------------------------------------------------------------------------------------------------------
  // Return the type attribute of the specified EntityId
  public String getEntityIdType(int EI_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select type from EntityIds where EI_ID='"+EI_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getString("type");
    }
    catch(SQLException e) { throw new SQLException(e); }
    return null;
  }  
  //------------------------------------------------------------------------------------------------------
  // Return the isPattern attribute of the specified EntityId
  public Boolean getEntityIdIsPattern(int EI_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select isPattern from EntityIds where EI_ID='"+EI_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return new Boolean(rs.getString("isPattern")); 
    }
    catch(SQLException e) { throw new SQLException(e); }
    return false;
  }  
  //------------------------------------------------------------------------------------------------------  
  /** 
   * @param id            Entity id name (like vehicle1, vehicle2, room1, room2...)
   * @param type          Entity type (like Car, Room, WeatherStation...)
   * @return              true if the entity exists, or false if it does not
   * @throws SQLException
   */
  
  public boolean entityExists(String id, String type) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select * from EntityIds where id='"+id+"' and type='"+type+"';"; 
      
      logger.debug("about to execute \""+stmt+"\"");      
      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return true;
    }
    catch(SQLException e) { throw new SQLException(e); }    
    return false; 
  }
  //------------------------------------------------------------------------------------------------------
  // Return the attributeDomainName attribute for a ContextElement
  public String getContextElementAttributeDomainName(int CE_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select attributeDomainName from ContextElements where CE_ID='"+CE_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getString("attributedomainName"); 
    }
    catch(SQLException e) { throw new SQLException(e); }
    return null;
  }
  //------------------------------------------------------------------------------------------------------
  // Return the row ids for ContextAttributes belonging to a particular ContextElement
  public ArrayList<Integer> getContextElementContextAttributeIds(int CE_ID) throws SQLException {
    ResultSet rs;
    ArrayList<Integer> result = new ArrayList<Integer>(); 
    
    try { 
      String stmt = "select CA_ID from ContextAttributes where CE_ID='"+CE_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      
      while(rs.next()){ result.add(rs.getInt("CA_ID")); }
      return result; 
    }
    catch(SQLException e) { throw new SQLException(e); }
  }  
  //------------------------------------------------------------------------------------------------------
  // Return the name attribute for a particular ContextAttribute
  public String getContextAttributeName(int CA_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select name from ContextAttributes where CA_ID='"+CA_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getString("name"); 
    }
    catch(SQLException e) { throw new SQLException(e); }
    return null; 
  }
  //------------------------------------------------------------------------------------------------------
  // Return the type attribute for a particular ContextAttribute
  public String getContextAttributeType(int CA_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select type from ContextAttributes where CA_ID='"+CA_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getString("type"); 
    }
    catch(SQLException e) { throw new SQLException(e); }
    return null; 
  }
  //------------------------------------------------------------------------------------------------------
  // Return the contextValue attribute for a particular ContextAttribute
  public String getContextAttributeContextValue(int CA_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select contextValue from ContextAttributes where CA_ID='"+CA_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getString("contextValue"); 
    }
    catch(SQLException e) { throw new SQLException(e); }
    return null; 
  }
  //------------------------------------------------------------------------------------------------------
  // Return the row ids for ContextMetadatas belonging to a particular ContextAttribute
  public ArrayList<Integer> getContextAttributeContextMetadataIds(int CA_ID) throws SQLException {
    ResultSet rs;
    ArrayList<Integer> result = new ArrayList<Integer>(); 
    
    try { 
      String stmt = "select CM_ID from ContextMetadatas where CA_ID='"+CA_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      
      while(rs.next()){ result.add(rs.getInt("CM_ID")); }
      return result; 
    }
    catch(SQLException e) { throw new SQLException(e); } 
  }
  //------------------------------------------------------------------------------------------------------
  // Return the row ids for ContextMetadatas belonging to a particular ContextAttribute
  public ArrayList<Integer> getContextElementContextMetadataIds(int CE_ID) throws SQLException {
    ResultSet rs;
    ArrayList<Integer> result = new ArrayList<Integer>(); 
    
    try { 
      String stmt = "select CM_ID from ContextMetadatas where CE_ID='"+CE_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      
      while(rs.next()){ result.add(rs.getInt("CM_ID")); }
      return result; 
    }
    catch(SQLException e) { throw new SQLException(e); } 
  }    
  //------------------------------------------------------------------------------------------------------
  // Return the name attribute for a particular ContextMetadata
  public String getContextMetadataName(int CM_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select name from ContextMetadatas where CM_ID='"+CM_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getString("name"); 
    }
    catch(SQLException e) { throw new SQLException(e); }
    return null; 
  }
  //------------------------------------------------------------------------------------------------------
  // Return the type attribute for a particular ContextMetadata
  public String getContextMetadataType(int CM_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select type from ContextMetadatas where CM_ID='"+CM_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getString("type"); 
    }
    catch(SQLException e) { throw new SQLException(e); }
    return null; 
  }
  //------------------------------------------------------------------------------------------------------
  // Return the value attribute for a ContextMetadata
  public String getContextMetadataValue(int CM_ID) throws SQLException {
    ResultSet rs;
    
    try { 
      String stmt = "select value from ContextMetadatas where CM_ID='"+CM_ID+"';"; //logger.debug("about to execute \""+stmt+"\"");      
      rs = this.s.executeQuery(stmt); 
      if(rs.next()) return rs.getString("value"); 
    }
    catch(SQLException e) { throw new SQLException(e); }
    return null; 
  }
  
  //------------------------------------------------------------------------------------------------------
  // Create a new entity
  public void setEntityId(String id, String type, Boolean isPattern, Integer CR_ID) throws SQLException {

    if(isPattern == null) isPattern = false;
    String stmt="insert into EntityIds(id, type, isPattern, CR_ID) " +
    		        "values ('"+id+"','"+type+"','"+isPattern.toString()+"','"+CR_ID+"');"; //logger.debug("about to execute \""+stmt+"\"");
    
    try { this.s.executeUpdate(stmt); this.c.commit();} catch (SQLException e) { throw new SQLException(e); }          
  }
  //------------------------------------------------------------------------------------------------------
  // Create a new ContextRegistration
  public int setContextRegistration(String providingApplication) throws SQLException {
    ResultSet rs;
    
    String stmt="insert into ContextRegistrations(providingApplication) " +
    		        "values ('"+providingApplication+"');"; //logger.debug("about to execute \""+stmt+"\"");

    try {      
      this.s.executeUpdate(stmt);
      rs = this.s.getGeneratedKeys();
      int rowId = rs.getInt("last_insert_rowid()");
      this.c.commit();
      return rowId;
    } 
    catch (SQLException e) { throw new SQLException(e); }
  }
  //------------------------------------------------------------------------------------------------------
  // Create a new ContextRegistrationAttribute
  public int setContextRegistrationAttribute(String name, String type, boolean isDomain, Integer EI_ID) throws SQLException {
    ResultSet rs;
    
    String stmt="insert into ContextRegistrationAttributes(name, type, isDomain, EI_ID ) "+
                "values ('"+name+"','"+type+"','"+new Boolean(isDomain).toString()+"','"+EI_ID+"');"; //logger.debug("about to execute \""+stmt+"\"");
    try {      
      this.s.executeUpdate(stmt);
      rs = this.s.getGeneratedKeys();
      int rowId = rs.getInt("last_insert_rowid()");
      this.c.commit();
      return rowId;
    } 
    catch (SQLException e) { throw new SQLException(e); }
  }
  //------------------------------------------------------------------------------------------------------
  // Create a new ContextElement
  public int setContextElement(String adn, int EI_ID) throws SQLException {
    ResultSet rs;
    if(adn==null) adn="";
    
    String stmt="insert into ContextElements(attributeDomainName, EI_ID,timestamp) "+
                "values ('"+adn+"','"+EI_ID+"','"+getTimeStamp()+"');"; //logger.debug("about to execute \""+stmt+"\"");
    try {      
      this.s.executeUpdate(stmt);
      rs = this.s.getGeneratedKeys();
      int rowId = rs.getInt("last_insert_rowid()");
      this.c.commit();
      return rowId;
    } 
    catch (SQLException e) { throw new SQLException(e); }
  } 
  //------------------------------------------------------------------------------------------------------
  // Create a new ContextAttribute
  public int setContextAttribute(String name, String type, String contextValue, Integer CE_ID) throws SQLException {
  
    ResultSet rs;
    
    String stmt="insert into ContextAttributes(name, type, contextValue, CE_ID) "+
                "values ('"+name+"','"+type+"','"+contextValue+"','"+CE_ID+"');"; //logger.debug("about to execute \""+stmt+"\"");
    try {      
      this.s.executeUpdate(stmt);
      rs = this.s.getGeneratedKeys();
      int rowId = rs.getInt("last_insert_rowid()");
      this.c.commit();
      return rowId;
    }
    catch (SQLException e) { throw new SQLException(e); }
  }
  //------------------------------------------------------------------------------------------------------
  // Create a new metadata. MetaDatas are part of : ContextAttributes, ContextElements, ContextRegistrations and
  // ContextRegistrationAttributes
  public void setContextMetadata(String name, String type, String value, Integer CE_ID, Integer CA_ID) throws SQLException {

    String stmt="insert into ContextMetadatas(name, type, value, CE_ID, CA_ID) "+
                "values ('"+name+"','"+type+"','"+value+"','"+CE_ID+"','"+CA_ID+"');"; //logger.debug("about to execute \""+stmt+"\"");
    
    try { this.s.executeUpdate(stmt); this.c.commit();} catch (SQLException e) { throw new SQLException(e); }          
  }
  //------------------------------------------------------------------------------------------------------
  // Create a new registrationMetadata. A registrationMetadata is part of ContextRegistrations and 
  // ContextRegistrationAttributes
  public void setRegistrationMetadata(String name, String type, String value, Integer EI_ID, Integer CRA_ID) throws SQLException {

    String stmt="insert into registrationMetadatas(name, type, EI_ID, CRA_ID) "+
                "values ('"+name+"','"+type+"','"+EI_ID+"','"+CRA_ID+"');"; //logger.debug("about to execute \""+stmt+"\"");
    
    try { this.s.executeUpdate(stmt); this.c.commit();} catch (SQLException e) { throw new SQLException(e); }          
  }  
  //------------------------------------------------------------------------------------------------------  
  // Get the current time in ISO8601 format, with milliseconds
  private String getTimeStamp(){
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSSz");
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    return sdf.format(new Date());
  }
}
