/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.mdps;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ObservableListPersister implements Observer {
	
	protected GenericDaoInterface dao;
	protected ArrayList<MonitoredList> monitoredLists = new ArrayList<MonitoredList>();
	
//----------------------------------------------------------------------------------------------------------------------
	public GenericDaoInterface getDao() {
		return dao;
	}
//----------------------------------------------------------------------------------------------------------------------
	public void setDao(GenericDaoInterface dao) {
		this.dao = dao;
	}
	
//----------------------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	public void addObservableList( ObservableList observableList, Class<?> elementsType ) {
		
		observableList.addObserver(this);
		
		MonitoredList monitoredList = new MonitoredList();
		monitoredList.observableList = observableList;
		monitoredList.elementsType = elementsType;
		
		this.monitoredLists.add(monitoredList);
	}	
//----------------------------------------------------------------------------------------------------------------------
	
	@SuppressWarnings("rawtypes")
	
	public void update(Observable observable, Object params) {

		try {
			if(this.dao == null)
				throw new UndefinedDaoInterfaceException("An instance of GenericDaoInterface must be configured.");

			// If param is null, it means a list element is being updated 
  		if(params != null && !params.getClass().getSimpleName().equals("ArrayList"))
				throw new UnsupportedFeatureException("The observable class must provide an ArrayList as an argument.");

  		// If the originator object is an ObservableList ...
  		if(params != null && observable.getClass().getSimpleName().equals("ObservableList")) {

  			List listParams = (List)params; 
  			String action = (String)listParams.get(0);
  			Object object = (Object)listParams.get(1); // Object can be an object to create or a search condition.

  			if(action.equals("create")) 
  				this.dao.create(object);

  			if(action.equals("delete")) {
  				// If object is a String, it means we want to delete a whole List, and that object contains the canonical
  				// class name (as a String) of the List from which we want to delete all elements ...
  				if(object.getClass().getSimpleName().equals("String")) {  				
  					this.dao.delete(Class.forName((String) object), null);
  				}
  				else { // otherwise it means we want to delete a single particular element in the List.
  					this.dao.delete(object.getClass(), object);
  				}
  			}
  		}
  		else { // Otherwise, it means it is an element from the list that is being updated
  			this.dao.update(observable);
  		}
		} 
		catch (UndefinedDaoInterfaceException e) { e.printStackTrace();	}
		catch (UnsupportedFeatureException e) {	e.printStackTrace(); } 
		catch (StorageNotAvailableException e) { e.printStackTrace();	} 
		catch (ObjectNotFoundException e) {	e.printStackTrace(); } 
		catch (ClassNotFoundException e) { e.printStackTrace();	}		
	}	
//----------------------------------------------------------------------------------------------------------------------
	@SuppressWarnings({ "unchecked" })
	public void loadLists() {
		
		for(MonitoredList monitoredList : this.monitoredLists) {
			
			// Temporarily delete the observer during the list load 
			// (otherwise each add in the list would trigger the observer)
			monitoredList.observableList.deleteObserver(this);

			try {
				List<Object> result = this.dao.read(monitoredList.elementsType, null);
				if(monitoredList.elementsType.getSimpleName().contains("Statement")) { // TODO remove after debug
					System.out.println();
				}
				for(Object object : result) {
					monitoredList.observableList.add(object);
				}
			} 
			catch (StorageNotAvailableException e) { e.printStackTrace();	} 
			catch (UnsupportedFeatureException e) {	e.printStackTrace(); } 
			catch (ObjectNotFoundException e) {	e.printStackTrace(); }
			
			monitoredList.observableList.addObserver(this);
		}
	}
//----------------------------------------------------------------------------------------------------------------------
	/**
	 * Delete all list one by one. The corresponding storage files are cleared as well.
	 */
	public void clearLists() {
		for(MonitoredList monitoredList : this.monitoredLists) {
			monitoredList.observableList.clear();
		}
	}
}
