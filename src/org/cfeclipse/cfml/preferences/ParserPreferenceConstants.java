/*
 * Created on Nov 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.cfeclipse.cfml.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Stephen Milligan
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ParserPreferenceConstants extends AbstractPreferenceConstants {
	
	/** Should we parse cfscript blocks */
	public static final String P_PARSE_DOCFSCRIPT = "__parseCFScript";

	/** Should we parse CFML blocks */
	public static final String P_PARSE_DOCFML = "__parseCFML";

	/** Should we report parse errors */
	public static final String P_PARSE_REPORT_ERRORS = "__parseReportErrors";



	/**
	 * Sets up the default values for preferences managed by {@link FoldingPreferencePage} .
	 * <ul>
	 * <li>P_PARSE_DOCFSCRIPT - false</li>
	 * <li>P_PARSE_DOCFSCRIPT - true</li>
	 * <li>P_PARSE_REPORT_ERRORS - true</li>
	 * </ul> 
	 */
	
	public static void setDefaults(IPreferenceStore store) { 
		store.setDefault(P_PARSE_DOCFSCRIPT,false); 
		store.setDefault(P_PARSE_DOCFML,true); 
		store.setDefault(P_PARSE_REPORT_ERRORS,true); 
	}

}