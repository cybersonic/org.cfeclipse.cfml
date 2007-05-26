package org.cfeclipse.cfml.editors.decoration;

import org.cfeclipse.cfml.util.CFPluginImages;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public class URLDecoratorImageDescriptor extends CompositeImageDescriptor {

	private Image baseImage;
	private Image overlayImage;

	public URLDecoratorImageDescriptor(Image baseImage, Image overlayImage) {
		super();
		this.baseImage = baseImage;
		this.overlayImage = overlayImage;
	}

	protected void drawCompositeImage(int width, int height) {
		drawImage(baseImage.getImageData(), 0, 0); 
		ImageData overlayImageData = CFPluginImages.get(CFPluginImages.ICON_DECORATOR_LINK).getImageData(); 

		   // Overlaying the icon in the top left corner i.e. x and y  
		   // coordinates are both zero 
		   int xValue = 0; 
		   int yValue = 0; 
		drawImage (overlayImageData, xValue, yValue);
	}

	protected Point getSize() {
		// TODO Auto-generated method stub
		return null;
	}

}
