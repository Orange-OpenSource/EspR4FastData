/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.mdps;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Laurent ARTUSIO
 * This generic DAO interface defines methods allowing the implementation of the CRUD pattern to access recorded objects 
 * in any kind of storage resource. The meaning of "storage resource" is wide: it can be files, databases, objects in 
 * memory, or anything else.
 * 
 * Identifier objects are used to identify and address the stored objects. The ids have been defined as objects, in 
 * order to allow potential complex ids. However, in most cases, ids might simply be strings or integers. For example, 
 * in the integer case, one can use the Integer class as an identifier object.
 */
public interface GenericDaoInterface {
		
	/**
	 * Creation of (a) new object(s) in a storage resource. 
	 * @param object the object(s) to be created. If the object class name ends by "List" then the whole storage is 
	 *        replaced by the objects within the List.
	 * @param referenceField the object's property that is used as a unique id for the created object. It should be the name
	 * 				of one of the object's fields.
	 */
	public void create( Object object ) throws StorageNotAvailableException, UnsupportedFeatureException;
	
	/**
	 * Read one identified existing object, or read all objects from any storage resource.
	 * @param searchCondition anything that helps in seeking particular objects. Up to the implementer. In many cases, 
	 *        searchCondition might be a string or an integer. For example, in the integer case, one can use the Integer 
	 *        class as an object id. 
	 * @param cls the Class of the object to be read. If the object class name ends by "List", then all the objects in
	 *        the related storage resource will be returned, regardless of the idObject content. 
	 * @return the seeked object.
	 * @throws StorageNotAvailableException 
	 * @throws FileNotFoundException 
	 */
	public List<Object> read(Class<?> cls, String searchConditions) throws StorageNotAvailableException, 
	                                                                	   	 UnsupportedFeatureException,
	                                                                	   	 ObjectNotFoundException;
	/**
	 * Update an existing object into a storage resource.
	 * @param cls the class of the object to update.
	 * @param updatedObject the updated object that is about to replace the existing object.  
	 */
	public void update(Object updatedObject) throws StorageNotAvailableException, 
	                                                              UnsupportedFeatureException,
	                                                              ObjectNotFoundException;
	
	/**
	 * Delete an existing object from the storage resource. 
	 * @param cls Class of the seeked object. If the class name ends with "List", then all objects in the storage will be 
	 *        deleted regardless of the idObject value.
	 * @param idObject the identifier that is used as a criteria to find the object to be deleted. 
	 */
	public void delete(Class<?> cls, Object searchConditions) throws StorageNotAvailableException, 
	                                                                 UnsupportedFeatureException,
	                                                                 ObjectNotFoundException;
}
