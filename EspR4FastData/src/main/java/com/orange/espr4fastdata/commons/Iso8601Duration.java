/* Copyright (C) 2014 Orange	

This software is distributed under the terms and conditions of the 
'GNU GENERAL PUBLIC LICENSE Version 2' license which can be found 
in the file 'LICENSE.txt' in this package distribution or at 
'http://www.gnu.org/licenses/gpl-2.0-standalone.html'. 
*/

package com.orange.espr4fastdata.commons;

public class Iso8601Duration {
	
	private String iso8601FormatedDuration;
	
//----------------------------------------------------------------------------------------------------------------------	
	
	public Iso8601Duration(String iso8601FormatedDuration ) {
		this.iso8601FormatedDuration = iso8601FormatedDuration;
	}
//----------------------------------------------------------------------------------------------------------------------
	
	public int getYears() {
		return this.getFieldValue('Y');
	}
//----------------------------------------------------------------------------------------------------------------------
	public int getMonthsOrMinutes() {
		return this.getFieldValue('M');
	}
//----------------------------------------------------------------------------------------------------------------------
	public int getDays() {
		return this.getFieldValue('D');
	}
//----------------------------------------------------------------------------------------------------------------------	
	public int getHours() {
		return this.getFieldValue('H');
	}
//----------------------------------------------------------------------------------------------------------------------	
	public int getSeconds() {
		return this.getFieldValue('S');
	}
//----------------------------------------------------------------------------------------------------------------------	

	protected int seekChar(char c) {
		
		int stringSize = this.iso8601FormatedDuration.length();
		char[] iso8601FormatedDuration = this.iso8601FormatedDuration.toCharArray();
		
		for(int i = 0; i < stringSize ; i++) {
			if(iso8601FormatedDuration[i] == c) return i;
		}
		return -1;
	}	
//----------------------------------------------------------------------------------------------------------------------

	protected int seekCharBackFrom(int index) {

		char[] iso8601FormatedDuration = this.iso8601FormatedDuration.toCharArray();
		
		for(int i = index-1; i >= 0 ; i--) {
			if( iso8601FormatedDuration[i] == 'P' ||
					iso8601FormatedDuration[i] == 'Y' ||
					iso8601FormatedDuration[i] == 'M' ||
					iso8601FormatedDuration[i] == 'D' ||
					iso8601FormatedDuration[i] == 'T' ||
					iso8601FormatedDuration[i] == 'H' ||
					iso8601FormatedDuration[i] == 'M' ||
					iso8601FormatedDuration[i] == 'S' ) {
				return i;
			}
		}
		return -1;
	}
	
//----------------------------------------------------------------------------------------------------------------------
	
	protected int getFieldValue(char fieldName) {
		
		int endIndex = seekChar(fieldName);
		
		if(endIndex < 0) return 0;
		
		int beginIndex = seekCharBackFrom(endIndex);
		
		return Integer.valueOf( iso8601FormatedDuration.substring(beginIndex+1, endIndex) );
	}

}
