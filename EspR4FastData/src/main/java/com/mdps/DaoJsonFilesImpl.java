/* Copyright (C) 2014 Laurent Artusio	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.mdps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.orange.espr4fastdata.commons.Utils;

public class DaoJsonFilesImpl implements GenericDaoInterface {

	private static Logger logger = Logger.getLogger(DaoJsonFilesImpl.class);
	protected String targetDirectory = new String();
	
//----------------------------------------------------------------------------------------------------------------------	
	
	protected String getTargetDirectory() {	return targetDirectory; }
	public void setTargetDirectory(String targetDirectory) { this.targetDirectory = targetDirectory; }

//----------------------------------------------------------------------------------------------------------------------	
	/* (non-Javadoc)
	 * @see com.orange.esper4fastdata.applicationlogic.GenericDAO#create(java.lang.Object)
	 */
	public void create( Object object ) throws StorageNotAvailableException, 
	                                           UnsupportedFeatureException {

		try {
			String simpleClassName = object.getClass().getSimpleName();
			String directoryPath = this.targetDirectory + '/' + simpleClassName + "List";

			if ( !new File(directoryPath).exists() ) {
				 new File(directoryPath).mkdirs();
			}

			ReferenceFieldList referenceFieldList = null;
			
 		  referenceFieldList = this.getDefinedReferenceFields(object.getClass());
			
			String uniqueFileName = Integer.toString(this.getCurrentIndex(object.getClass()) ) + '_';
						
			// Parse the object's fields that are aggregated as a unique id for the file name.
			for(String fieldName : referenceFieldList.getReferenceField()) {

				// Get the object's field that is used as referenceField
				Field field = object.getClass().getDeclaredField(fieldName);
				field.setAccessible(true); // Set the field as accessible in order to be able to retrieve its type and content

				// Read the field type as a String
				String fieldType = field.get(object).getClass().getSimpleName(); 
	
				// Depending on the field type, format the referenceField appropriately
				if( fieldType.equals("Date") ) {
					Date date = (Date)field.get(object);
					uniqueFileName += Utils.formatDate(date, "yyyyMMdd'T'HHmmssSSSz") + '_';
				}
				if( fieldType.equals("String") ) {
					uniqueFileName += (String)field.get(object)+'_';
				}
			}
			
			uniqueFileName = uniqueFileName.substring(0, uniqueFileName.length()-1); // Remove the last '_'
			
			String filePath = directoryPath + '/' + uniqueFileName + ".json";
			
			// Serialize the List to a JSON String.
			String serializedContent = Utils.objectToFormattedJsonString(object);

			// Backup the serialized List to the file.
			Utils.writeTextContentToFile(filePath, serializedContent);
			
			this.incrementCurrentIndex(object.getClass());
	  }
		catch (IOException e) { logger.error(e.getMessage()); } 
		catch (SecurityException e) {	logger.error(e.getMessage());	} 
		catch (NoSuchFieldException e) { logger.error(e.getMessage()); } 
		catch (IllegalArgumentException e) { logger.error(e.getMessage()); } 
		catch (IllegalAccessException e) { logger.error(e.getMessage()); }
	}		
//----------------------------------------------------------------------------------------------------------------------	
	public List<Object> read(Class<?> cls, String searchConditions) throws StorageNotAvailableException, 
																		   																	 UnsupportedFeatureException {
		// This variable is the name of the requested object's class
		String simpleClassName = cls.getSimpleName();
		String path = this.targetDirectory + '/' + simpleClassName + "List";

		// If the storage directory does not exist, create it.
		if(!Utils.fileExists(path)) {
			new File(path).mkdirs();
		}
		
		List<String> directoryContent = null;
		List<Object> result = new ArrayList<Object>();
		
		try {
			if(searchConditions == null) { // Retrieve all records
				directoryContent = Utils.getDirectoryContent(path);
			
				for(String fileName : directoryContent) {
				
					if( !fileName.equals("ConfigTable.cfg")) {
						String fileContent = Utils.readTextContentFromFile(path + "/" + fileName);
						result.add(Utils.jsonStringToObject(fileContent, cls));
					}				
				}
			}
			else {
				throw new UnsupportedFeatureException("You can only read all records in one time. Reading particular records matching specific conditions is not supported.");
			}
		}
		catch(IOException e) { logger.error(e.getMessage()); }		
		
		return result; 
	}
//----------------------------------------------------------------------------------------------------------------------
	public void update(Object updatedObject) {
		
		// Get the storage file that features the searched reference fields values.
		String storageFilePath = this.getStorageFilePathForObject( updatedObject );
		
		if(storageFilePath != null) {
			
			if(updatedObject.getClass().getSimpleName().contains("Statement")) { // TODO remove after debug
				System.out.println();
			}
			
			// Serialize the updated object to a JSON String.
			String serializedContent = Utils.objectToFormattedJsonString( updatedObject );

			// Backup the serialized List to the existing file.
			try {
				Utils.writeTextContentToFile(storageFilePath, serializedContent);
			} 
			catch (FileNotFoundException e) {	e.printStackTrace(); } 
			catch (IOException e) {	e.printStackTrace(); }
		}
	}	
//----------------------------------------------------------------------------------------------------------------------
  public void delete(Class<?> cls, Object objectToDelete) throws StorageNotAvailableException, 
                                                                 UnsupportedFeatureException,
                                                                 ObjectNotFoundException {	                                         																				
  	// This variable is the name of the requested object's class
  	String simpleClassName = cls.getSimpleName();
  	String path = this.targetDirectory + '/' + simpleClassName + "List";

  	List<String> directoryContent = Utils.getDirectoryContent(path);
  	try {
  		// If objectToDelete = null, delete all saved records for the cls class.
  		if(objectToDelete == null) { 
 
  			for(String fileName : directoryContent) {
  			
  				// Delete all files except "ConfigTable.cfg"
  				if( !fileName.equals("ConfigTable.cfg")) {
  					File file = new File(path + "/" + fileName);
  					file.delete();
  				}
  			}
  		}
  		else { // Delete a record that match the "updatedObject" object
  			
  			String fileToDelete = this.getStorageFilePathForObject( objectToDelete );
  			
  			if( fileToDelete != null ) {
  				Utils.deleteFile(fileToDelete);
  			}
  		}
  	}
  	catch (IllegalArgumentException e) { logger.error(e.getMessage()); } 
  	catch (SecurityException e) {logger.error(e.getMessage()); } 
  }
//----------------------------------------------------------------------------------------------------------------------  
  /**
   * Return the actual content of a given field for a given object, using java's reflection API.
   * @param object that feature the wanted field content.
   * @param fieldName the field name
   * @return the actual field content as a String object.
   */
  protected String getFieldContentFromObject(Object object, String fieldName) {
  	
  	try {
  		// Read field as a Field object.
			Field field = object.getClass().getDeclaredField(fieldName);
			
			// Set the field as accessible, otherwise it might not be readable, depending on its declaration: public,
			// protected, private...
			field.setAccessible(true);
			
			Object fieldContent = field.get(object); // Get the actual field content as a generic object
			
			String fieldContentString = null; // This will be the final result to be returned.
			
			// the final returned result will be a String: we need to process differently each field, depending on their
			// types.
			if(fieldContent.getClass().getSimpleName().equals("Date")) {
				Date date = (Date)fieldContent;
		  	
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		  	String result = simpleDateFormat.format(date);

				fieldContentString = result;
			}
			
			if(fieldContent.getClass().getSimpleName().equals("String")) {
				fieldContentString = (String)fieldContent;
			}
			
			return fieldContentString;
		} 
  	catch (SecurityException e) {	e.printStackTrace(); } 
  	catch (NoSuchFieldException e) { e.printStackTrace();	}
  	catch (IllegalArgumentException e) { e.printStackTrace(); } 
  	catch (IllegalAccessException e) { e.printStackTrace();	} 	
  	
  	return null;
  }
//----------------------------------------------------------------------------------------------------------------------  
  protected List<String> getReferenceFieldValues( String storageFileName ) {

  	ArrayList<String> result = new ArrayList<String>( Arrays.asList(storageFileName.replace(".json", "").split("_")) );
  	result.remove(0); // Remove the unique id at the beginning of the file name.  						

  	return result;
  }
//----------------------------------------------------------------------------------------------------------------------
	public void setReferenceFieldList( Class<?> cls, ReferenceFieldList referenceFieldList ) {
		
		String simpleClassName = cls.getSimpleName();
		String directoryPath = this.targetDirectory + '/' + simpleClassName + "List";
		
		// If the table directory does not exist then create it.
		if ( !new File(directoryPath).exists() ) {
			 new File(directoryPath).mkdirs();			   
		}
		// If the ConfigTable file does not exist, create it.
		try {
			if ( !new File(directoryPath + "/ConfigTable.cfg").exists() ) {
				
				ConfigTable configTable = new ConfigTable();
				configTable.currentIndex = 1;
				
				Utils.writeTextContentToFile(directoryPath+"/ConfigTable.cfg", 
																		 Utils.objectToFormattedJsonString(configTable) );				
			}
			// Load the ConfigTable.cfg file, no matter if it was just created in the code above or if it already existed before.
			String configTableAsString = Utils.readTextContentFromFile(directoryPath + "/ConfigTable.cfg");
			ConfigTable configTable = (ConfigTable) Utils.jsonStringToObject(configTableAsString, ConfigTable.class);
			
			configTable.referenceFieldList = referenceFieldList; // Update the referenceFieldList object with the new value
			
			configTableAsString = Utils.objectToFormattedJsonString(configTable);
			Utils.writeTextContentToFile(directoryPath + "/ConfigTable.cfg", configTableAsString);			
		}
		catch (FileNotFoundException e) { logger.error(e.getMessage());	} 
		catch (IOException e) {	logger.error(e.getMessage());	}
	}
//----------------------------------------------------------------------------------------------------------------------
	
	public void setPartition( Class<?> cls, String partitionFieldName ) {
		String simpleClassName = cls.getSimpleName();
		String directoryPath = this.targetDirectory + '/' + simpleClassName + "List";
		
		// If the table directory does not exist then create it.
		if ( !new File(directoryPath).exists() ) {
			 new File(directoryPath).mkdirs();			   
		}

		try {
			if ( !new File(directoryPath + "/ConfigTable.cfg").exists() ) {

				ConfigTable configTable = new ConfigTable();
				configTable.currentIndex = 1;

				Utils.writeTextContentToFile(directoryPath+"/ConfigTable.cfg", Utils.objectToFormattedJsonString(configTable) );				
			}
			
			String configTableAsString = Utils.readTextContentFromFile(directoryPath + "/ConfigTable.cfg");
			ConfigTable configTable = (ConfigTable) Utils.jsonStringToObject(configTableAsString, ConfigTable.class);
			
			configTable.partition = partitionFieldName;
			
			configTableAsString = Utils.objectToFormattedJsonString(configTable);
			Utils.writeTextContentToFile(directoryPath + "/ConfigTable.cfg", configTableAsString);			
		}
		catch (FileNotFoundException e) { logger.error(e.getMessage());	} 
		catch (IOException e) {	logger.error(e.getMessage());	}
		
	}
	
//----------------------------------------------------------------------------------------------------------------------
	public ReferenceFieldList getDefinedReferenceFields( Class<?> cls ) {
		
		String simpleClassName = cls.getSimpleName();
		String directoryPath = this.targetDirectory + '/' + simpleClassName + "List";
		
		// If the table directory does not exist then create it.
		if ( !new File(directoryPath).exists() ) {
			 new File(directoryPath).mkdirs();			   
		}
		
		try {
			if ( !new File(directoryPath + "/ConfigTable.cfg").exists() ) 
				throw new UnsupportedFeatureException("It is not possible to create a record before defining the reference field(s). Please call setReferenceFieldList before.");
			
			String fileContent = Utils.readTextContentFromFile(directoryPath + "/ConfigTable.cfg");
			ConfigTable configTable = (ConfigTable) Utils.jsonStringToObject(fileContent, ConfigTable.class);
			return configTable.referenceFieldList;			
		} 
		catch (IOException e) {e.printStackTrace();} 
		catch (UnsupportedFeatureException e) {	e.printStackTrace(); }
				
		return null;		
	}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Return the actual complete file path for the searchedObject object. Only reference fields are used to seek the file.
	 * @param searchedObject the persisted object that is associated with a storage file.
	 * @return the complete path (including the file name) of the persisted object. Return null if the file is not found.
	 */
	protected String getStorageFilePathForObject(Object searchedObject) {
		try {
	  	// This variable is the name of the requested object's class
	  	String simpleClassName = searchedObject.getClass().getSimpleName();
	  	String path = this.targetDirectory + '/' + simpleClassName + "List";

	  	List<String> directoryContent = Utils.getDirectoryContent(path);
	  	  			
  	  // Parse the directory, file by file.
  		for(String fileName : directoryContent) {			

  			// Read all files except "ConfigTable.cfg", which is a special file.
  			if( !fileName.equals("ConfigTable.cfg")) {
	  						  
  				// Get the reference field values from the file name, given that each record file contains reference field 
  				// values within its name. The file content is not read, only the file name is used
  				List<String> referenceFieldsValues = this.getReferenceFieldValues( fileName );
	  						
  				// Now let's get the reference field names for the cls Class 
  				ReferenceFieldList referenceFieldList = this.getDefinedReferenceFields(searchedObject.getClass());
  				int matches = 0;
	  						
  				// Parse reference fields one by one in the current parsed fileName(there can't be less than one reference 
  				// field in a storage file name)
  				for(int i = 0; i < referenceFieldList.getReferenceField().size(); i++ ) {		
	  						
  					String parsedReferenceFieldName = referenceFieldList.getReferenceField().get(i);  							
  							
   					String fieldContentOfObjectToDelete = 
   						this.getFieldContentFromObject(searchedObject, parsedReferenceFieldName);
	  							
  					if(fieldContentOfObjectToDelete.equals(referenceFieldsValues.get(i)))
  						matches++;
  				}
  				
  				if(matches == referenceFieldList.getReferenceField().size()) {
  					return path + "/" + fileName;
  				}
  			}
  		}  			  		
  	}
  	catch (IllegalArgumentException e) { logger.error(e.getMessage()); } 
  	catch (SecurityException e) {logger.error(e.getMessage()); }
		
		return null;
	}
//----------------------------------------------------------------------------------------------------------------------
	public int getCurrentIndex( Class<?> cls ) {
		
		String simpleClassName = cls.getSimpleName();
		String directoryPath = this.targetDirectory + '/' + simpleClassName + "List";
		
		// If the table directory does not exist then create it.
		if ( !new File(directoryPath).exists() ) {
			 new File(directoryPath).mkdirs();			   
		}
		
		try {
			if ( new File(directoryPath + "/ConfigTable.cfg").exists() ) { 
			
				String fileContent = Utils.readTextContentFromFile(directoryPath + "/ConfigTable.cfg");
				ConfigTable configTable = (ConfigTable) Utils.jsonStringToObject(fileContent, ConfigTable.class);
				return configTable.currentIndex;			
			}
		}
		catch (IOException e) {e.printStackTrace();} 
				
		return 0;		
	}
//----------------------------------------------------------------------------------------------------------------------
	public void incrementCurrentIndex( Class<?> cls ) {
		
		String simpleClassName = cls.getSimpleName();
		String directoryPath = this.targetDirectory + '/' + simpleClassName + "List";
		
		// If the table directory does not exist then create it.
		if ( !new File(directoryPath).exists() ) {
			 new File(directoryPath).mkdirs();			   
		}
		// If the ConfigTable file does not exist, create it.
		try {
			if ( !new File(directoryPath + "/ConfigTable.cfg").exists() ) {
				
				ConfigTable configTable = new ConfigTable();
				configTable.currentIndex = 1;
				
				Utils.writeTextContentToFile(directoryPath+"/ConfigTable.cfg", Utils.objectToFormattedJsonString(configTable) );				
			}
			
			// Load the ConfigTable.cfg file, no matter if it was just created in the code above or if it already existed before.
			String configTableAsString = Utils.readTextContentFromFile(directoryPath + "/ConfigTable.cfg");
			ConfigTable configTable = (ConfigTable) Utils.jsonStringToObject(configTableAsString, ConfigTable.class);
			
			configTable.currentIndex++;
			
			configTableAsString = Utils.objectToFormattedJsonString(configTable);
			Utils.writeTextContentToFile(directoryPath + "/ConfigTable.cfg", configTableAsString);			
		}
		catch (FileNotFoundException e) { logger.error(e.getMessage());	} 
		catch (IOException e) {	logger.error(e.getMessage());	}
	}
}