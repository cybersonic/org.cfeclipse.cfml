/*
 * Created on Nov 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.cfeclipse.cfml.views.explorer;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cfeclipse.cfml.net.RemoteFile;
import org.cfeclipse.cfml.util.CFPluginImages;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;


class FileLabelProvider extends LabelProvider implements ITableLabelProvider {
    
    Pattern p = Pattern.compile("(.)+(htm|html|cfc|cfm|cfml)$");
    
    public String getColumnText(Object element, int columnIndex) {
        
	        if (element instanceof RemoteFile) {
	            return ((RemoteFile)element).getName();
	        }
	        
	        String[] fullpath = element.toString().split("[\\\\/]");
	        return fullpath[fullpath.length-1];
        
    }
    
    
    
    public Image getColumnImage(Object element, int columnIndex) {
      
	        String filename = element.toString();
	        if (element instanceof RemoteFile) {
	            filename = ((RemoteFile)element).getName();
	        }
	        Matcher m = p.matcher(filename);
	        if (m.matches()) {
	            return addPermissionIcon(element,CFPluginImages.get(CFPluginImages.ICON_DEFAULT));
	        }
	        else {
	            return addPermissionIcon(element,CFPluginImages.get(CFPluginImages.ICON_DOCUMENT));
	        }
        
    }
    
    private Image addPermissionIcon(Object element, Image image) {
        int redPixel = image.getImageData().palette.getPixel(new RGB(255,0,0));
        int greenPixel = image.getImageData().palette.getPixel(new RGB(0,255,0));
        boolean canRead = true;
        boolean canWrite = true;
        if (element instanceof RemoteFile) {
            RemoteFile file = (RemoteFile)element;
            canRead = file.canRead();
            canWrite = file.canWrite();
        }
        else if (element instanceof File) {
            File file = (File)element;
            canRead = file.canRead();
            canWrite = file.canWrite();
        }
        else {
            return image;
        }
        
        if (!canRead) {
            return CFPluginImages.addOverlay(image,new RGB(255,0,0));
        }
        else if (!canWrite) {
            return CFPluginImages.addOverlay(image,new RGB(0,255,0));
        }
        else {
            return image;
        }
    }
    
    
    
}