/*
 * Created on Nov 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.cfeclipse.cfml.views.explorer;

import org.apache.commons.vfs.FileObject;

/**
 * @author Stephen Milligan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileSystemRoot {

	private FileObject fObject;
	private String name = "";
	private String path = "";
	private String type = "";
	/**
	 * 
	 */
	public FileSystemRoot(String name) {
		this.name = name;
	}
	
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String toString() {
		return this.name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public FileObject getFileObject() {
		return fObject;
	}


	public void setFileObject(FileObject object) {
		fObject = object;
	}

}
