/*
 * Created on Nov 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.cfeclipse.cfml.preferences;

import org.cfeclipse.cfml.CFMLPlugin;
import org.eclipse.ui.internal.editors.text.AnnotationsPreferencePage;

/**
 * @author Stephen Milligan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AnnotationPreferencePage extends AnnotationsPreferencePage {

	/**
	 * 
	 */
	public AnnotationPreferencePage() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected void setPreferenceStore() {
		setPreferenceStore(CFMLPlugin.getDefault().getPreferenceStore());
	}

}
