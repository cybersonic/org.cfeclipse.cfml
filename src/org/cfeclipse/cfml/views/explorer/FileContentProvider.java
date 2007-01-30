/*
 * Created on Nov 7, 2004
 *
 */
package org.cfeclipse.cfml.views.explorer;

import java.io.File;

import org.cfeclipse.cfml.net.FTPConnectionProperties;
import org.cfeclipse.cfml.net.ftp.FTPConnection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.ViewPart;

class FileContentProvider implements IStructuredContentProvider {
    
    File[] contents = new File[] {};
    IFileProvider fileProvider;

    private FileNameFilter fileFilter = new FileNameFilter();
    private ViewPart viewpart = null;
    
    public FileContentProvider(ViewPart viewpart) {
        this.viewpart = viewpart;
        fileFilter.allowDirectories(false);
    }
    
    public Object[] getElements(Object inputElement) {

    	try {
	        if (fileProvider == null) {
	            return new Object[] {IFileProvider.INVALID_FILESYSTEM};
	        }
	        if (inputElement != null) {
	            
	            if (inputElement instanceof LocalFileSystem
	                    || inputElement instanceof FTPConnectionProperties
	                    || inputElement instanceof FTPConnection) {
		            return new String[0];
	            }
	            
	            String directoryName = inputElement.toString();
	            if (directoryName.indexOf("[") == 0) {
	                directoryName = directoryName.substring(1,directoryName.length()-1);
	            }
	            //System.out.println("Getting children of  " + inputElement.getClass().getName());
	            Object[] files = fileProvider.getChildren(directoryName,fileFilter);
	            return files;
	            
	        }
	        else {
	            return new String[] {"Null input element"};
	        }
    	}
        catch (Exception e) {
        	e.printStackTrace();
            return new Object[] {"Error! " + e.getMessage()};
        }
    }
    public void dispose() {
    	if (fileProvider != null) {
    		fileProvider.dispose();
    	}
    }
    
    
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        try {
	    	//System.out.println("File viewer input changed to ." + newInput.getClass().getName());
	        if (newInput instanceof IFileProvider) {
	            fileProvider = (IFileProvider)newInput;
	            fileProvider.connect();
	        }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public IEditorInput getEditorInput(String filename) {
        if (filename.indexOf("[") == 0) {
            filename = filename.substring(1,filename.length()-1);
        }
        
        return fileProvider.getEditorInput(filename);
        
    }
    
}