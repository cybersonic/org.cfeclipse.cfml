/*******************************************************************************
 * Copyright (c) 2004, 2006 John-Mason P. Shackelford and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     John-Mason P. Shackelford - initial API and implementation
 *******************************************************************************/
package org.cfeclipse.cfml.editors.formatters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cfeclipse.cfml.editors.CFDocumentSetupParticipant;
import org.cfeclipse.cfml.editors.ICFDocument;
import org.cfeclipse.cfml.editors.partitioner.scanners.CFPartitionScanner;
import org.cfeclipse.cfml.parser.CFDocument;
import org.cfeclipse.cfml.parser.docitems.DocItem;
import org.cfeclipse.cfml.templates.template.CFTemplateContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateVariable;

/**
 * Utility class for using the ant code formatter in contexts where an IDocument
 * containing the text to format is not readily available.
 */
public class CFMLFormatter {

	private static final String POS_CATEGORY= "tempAntFormatterCategory"; //$NON-NLS-1$
	
    /**
     * Format the text using the ant code formatter.
     * 
     * @param text
     *            The text to format. Must be a non-null value.
     * @param prefs
     *            Preferences to use for this format operation. If null, the
     *            preferences currently set in the plug-in's preferences store
     *            are used.
     * @return The formatted text.
     */
    public static String format(String text, FormattingPreferences prefs) {
        
      return format(text, prefs, -1);
    }
    
    private static String format(String text, FormattingPreferences prefs, int indent) {
    	 Assert.isNotNull(text);
         
         FormattingPreferences applyPrefs;
         if(prefs == null) {
             applyPrefs = new FormattingPreferences();
         } else {
             applyPrefs = prefs;
         }
         
         ICFDocument doc = new ICFDocument();
         doc.set(text);
         new CFDocumentSetupParticipant().setup(doc);

         format(applyPrefs, doc, indent);

         return doc.get();
    }

	private static void format(FormattingPreferences prefs, IDocument doc, int indent) {

		MultiPassContentFormatter formatter = new MultiPassContentFormatter(
                //CFDocumentSetupParticipant.CFML_PARTITIONING,
				IDocumentExtension3.DEFAULT_PARTITIONING,
                IDocument.DEFAULT_CONTENT_TYPE);

        formatter.setMasterStrategy(new CFMLFormattingStrategy());
        formatter.format(doc, new Region(0, doc.getLength()));
	}

	/**
     * Format the text using the ant code formatter using the preferences
     * settings in the plug-in preference store.
     * 
     * @param text
     *            The text to format. Must be a non-null value.
     * @return The formatted text.
     */
    public static String format(String text) {
        return format(text,null);
    }
    
    public static void format(TemplateBuffer templateBuffer, CFTemplateContext antContext, FormattingPreferences prefs) {	
    	String templateString= templateBuffer.getString();
    	IDocument fullDocument= new Document(antContext.getDocument().get());
    	
    	int completionOffset= antContext.getCompletionOffset();
    	try {
    		//trim any starting whitespace
			IRegion lineRegion= fullDocument.getLineInformationOfOffset(completionOffset);
			String lineString= fullDocument.get(lineRegion.getOffset(), lineRegion.getLength());
			lineString= trimBegin(lineString);
			fullDocument.replace(lineRegion.getOffset(), lineRegion.getLength(), lineString);
		} catch (BadLocationException e1) {
			return;
		}
    	TemplateVariable[] variables= templateBuffer.getVariables();
		int[] offsets= variablesToOffsets(variables, completionOffset);
		
		IDocument origTemplateDoc= new Document(fullDocument.get());
		try {
			origTemplateDoc.replace(completionOffset, antContext.getCompletionLength(), templateString);
		} catch (BadLocationException e) {
			return; // don't format if the document has changed
		}
		
    	IDocument templateDocument= createDocument(origTemplateDoc.get(), createPositions(offsets));
    	
    	//String leadingText= getLeadingText(fullDocument, antContext.getAntModel(), completionOffset);
    	String leadingText= getLeadingText(fullDocument, ((ICFDocument)antContext.getDocument()).getCFDocument(), completionOffset);
    	String newTemplateString= leadingText + templateString;
    	int indent= XmlDocumentFormatter.computeIndent(leadingText, prefs.getTabWidth());
    	
    	newTemplateString= format(newTemplateString, prefs, indent);
    	
    	try {
    		templateDocument.replace(completionOffset, templateString.length(), newTemplateString);
		} catch (BadLocationException e) {
			return;
		}
		Position[] positions= null;
		try {
			positions= templateDocument.getPositions(POS_CATEGORY);
		} catch (BadPositionCategoryException e2) {
		}
    	//offsetsToVariables(offsets, variables, completionOffset);
		positionsToVariables(positions, variables, completionOffset);
    	templateBuffer.setContent(newTemplateString, variables);
    }
    
    private static void positionsToVariables(Position[] positions, TemplateVariable[] variables, int start) {
		for (int i= 0; i != variables.length; i++) {
		    TemplateVariable variable= variables[i];
		    
			int[] offsets= new int[variable.getOffsets().length];
			for (int j= 0; j != offsets.length; j++) {
				offsets[j]= positions[j].getOffset() - start;
			}
			
		 	variable.setOffsets(offsets);   
		}
	}	
    private static Document createDocument(String string, Position[] positions) throws IllegalArgumentException {
		Document doc= new Document(string);
		try {
			if (positions != null) {
				
				doc.addPositionCategory(POS_CATEGORY);
				doc.addPositionUpdater(new DefaultPositionUpdater(POS_CATEGORY) {
					protected boolean notDeleted() {
						if (fOffset < fPosition.offset && (fPosition.offset + fPosition.length < fOffset + fLength)) {
							fPosition.offset= fOffset + fLength; // deleted positions: set to end of remove
							return false;
						}
						return true;
					}
				});
				for (int i= 0; i < positions.length; i++) {
					try {
						doc.addPosition(POS_CATEGORY, positions[i]);
					} catch (BadLocationException e) {
						throw new IllegalArgumentException("Position outside of string. offset: " + positions[i].offset + ", length: " + positions[i].length + ", string size: " + string.length());   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					}
				}
			}
		} catch (BadPositionCategoryException cannotHappen) {
			// can not happen: category is correctly set up
		}
		return doc;
	}
    
    public static String trimBegin(String toBeTrimmed) {
		
		int i= 0;
		while ((i != toBeTrimmed.length()) && Character.isWhitespace(toBeTrimmed.charAt(i))) {
			i++;
		}

		return toBeTrimmed.substring(i);
	}
    
    private static int[] variablesToOffsets(TemplateVariable[] variables, int start) {
		List list= new ArrayList();
		for (int i= 0; i != variables.length; i++) {
		    int[] offsets= variables[i].getOffsets();
		    for (int j= 0; j != offsets.length; j++) {
				list.add(new Integer(offsets[j]));
		    }
		}
		
		int[] offsets= new int[list.size()];
		for (int i= 0; i != offsets.length; i++) {
			offsets[i]= ((Integer) list.get(i)).intValue() + start;
		}

		Arrays.sort(offsets);

		return offsets;	    
	}
	
	/**
	 * Returns the indentation level at the position of code completion.
	 */
	private static String getLeadingText(IDocument document, CFDocument model, int completionOffset) {
		DocItem project= model.getDocumentRoot();
		if (project == null) {
			return ""; //$NON-NLS-1$
		}
		/*
		 * 
		DocItem node= model.getNode(completionOffset);// - fAccumulatedChange);
		if (node == null) {
			return ""; //$NON-NLS-1$
		}
		 */
		
		StringBuffer buf= new StringBuffer();
//		buf.append(XmlDocumentFormatter.getLeadingWhitespace(node.getOffset(), document));
		buf.append(XmlDocumentFormatter.getLeadingWhitespace(completionOffset, document));
		buf.append(XmlDocumentFormatter.createIndent());
		return buf.toString();
	}
	
	 private static Position[] createPositions(int[] positions) {
    	Position[] p= null;
		
		if (positions != null) {
			p= new Position[positions.length];
			for (int i= 0; i < positions.length; i++) {
				p[i]= new Position(positions[i], 0);
			}
		}
		return p;
    }
}
