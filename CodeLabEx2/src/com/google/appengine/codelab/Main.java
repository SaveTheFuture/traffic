package com.google.appengine.codelab;
import com.google.appengine.api.datastore.Key;

import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Blob;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class Main {

	public static class Id implements Serializable {

		public Id() {
		}

		public Id(java.lang.String str) {
			java.util.StringTokenizer token = new java.util.StringTokenizer(
					str, "::");
		}

		public java.lang.String toString() {
			java.lang.String str = "";
			return str;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null) {
				return false;
			}
			if (o.getClass() != getClass()) {
				return false;
			}
			Id objToCompare = (Id) o;
			return true;
		}

	}

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String description;
	
	@Persistent
	private String   details;
	
	@Persistent
	private Date   entryDate ;
	
	@Persistent
	private int    yesCount;
	
	@Persistent
	private int    totalCount;
	
	public Main(String description,String   details) {
		this.description = description;
		this.details = details;
		this.entryDate = new Date();
		this.yesCount = 0;
		this.totalCount=0;
	}
	public String getDetails() {
		return this.details;
	}
}
