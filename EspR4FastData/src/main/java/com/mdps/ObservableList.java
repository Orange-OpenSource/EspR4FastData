/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.mdps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.RandomAccess;

@SuppressWarnings("rawtypes")
public class ObservableList<E> extends Observable implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	protected ArrayList<E> list = new ArrayList<E>();
	protected Observer observer;
	
	public ObservableList() { super(); }
	
	public ObservableList( Observer observer ) {
		super();
		this.observer = observer;
	}
	
	
	public int size() {	return list.size(); }

	
	public boolean isEmpty() { return list.isEmpty(); }

	
	public boolean contains(Object o) {	return list.contains(o); }
	
	
	public Iterator<E> iterator() { return (Iterator<E>) list.iterator(); }

	
	public Object[] toArray() { return list.toArray(); }

	
	public <T> T[] toArray(T[] a) { return list.toArray(a);	}

	@SuppressWarnings("unchecked")
	
	public boolean add(E object) {
		
		boolean result = false;
		
		if( this.observer != null) {
			
			Observable observable = (Observable)object;
			observable.addObserver(this.observer);
		
			result = this.list.add((E) observable);
			
			if(result == true) {
				super.setChanged();
				super.notifyObservers(Arrays.asList("create",observable));
			}			
		}
		else {
			result = this.list.add(object);
		}
		return result;
	}

	
	public boolean remove(Object object) {
		
		boolean result = list.remove(object);
		
		if(result == true) {
			super.setChanged();
			super.notifyObservers(Arrays.asList("delete", object));
		}
		
		return result;
	}

	
	public boolean containsAll(Collection c) { return list.containsAll(c); }

	
	public boolean addAll(Collection<? extends E> c) { return list.addAll(c); }

	
	public boolean addAll(int index, Collection<? extends E> c) { return list.addAll(index, c); }		

	
	public boolean removeAll(Collection<?> c) { return list.removeAll(c); }

	
	public boolean retainAll(Collection<?> c) { return list.retainAll(c); }

	
	public void clear() { 
		if(!this.list.isEmpty()) {
			String canonicalClassName = this.list.get(0).getClass().getCanonicalName();
			this.list.clear();
			super.setChanged();
			super.notifyObservers(Arrays.asList("delete", canonicalClassName));
		}
	}

	
	public E get(int index) { return (E) list.get(index); }

	
	public E set(int index, E element) { return (E) list.set(index, element); }

	@SuppressWarnings("unchecked")
	
	public E remove(int index) {
		Object object = this.list.remove(index);
		super.setChanged();
		super.notifyObservers(Arrays.asList("delete", object));
		return (E) object;
	}

	
	public int indexOf(Object o) { return list.indexOf(o); }

	
	public int lastIndexOf(Object o) { return list.lastIndexOf(o); }

	
	public ListIterator<E> listIterator() { return (ListIterator<E>) list.listIterator(); }

	
	public ListIterator<E> listIterator(int index) { return (ListIterator<E>) list.listIterator(index); }

	
	public List<E> subList(int fromIndex, int toIndex) { return (List<E>) list.subList(fromIndex, toIndex); }

	@SuppressWarnings("unchecked")
	
	public void add(int index, E object) {
		
		if( this.observer != null) {
			
			Observable observable = (Observable)object;
			observable.addObserver(this.observer);
			
			list.add(index, (E) observable);
			
			super.setChanged();
			super.notifyObservers(Arrays.asList("create",observable));
		}
		else {
			this.list.add(object);
		}
	}	
}
