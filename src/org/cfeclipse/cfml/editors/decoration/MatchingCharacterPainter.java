/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.cfeclipse.cfml.editors.decoration;


import org.cfeclipse.cfml.CFMLPlugin;
import org.cfeclipse.cfml.preferences.EditorPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

/**
 * Highlights the peer character matching the character near the caret position.
 * This painter can be configured with an
 * {@link org.eclipse.jface.text.source.ICharacterPairMatcher}.
 * <p>
 * Clients instantiate and configure object of this class.
 * 
 * @since 2.1
 */
public final class MatchingCharacterPainter implements IPainter, PaintListener {
	
	/** Indicates whether this painter is active */
	private boolean fIsActive= false;
	/** The source viewer this painter is associated with */
	private ISourceViewer fSourceViewer;
	/** The viewer's widget */
	private StyledText fTextWidget;
	/** The color in which to highlight the peer character */
	private Color fColor;
	/** The paint position manager */
	private IPaintPositionManager fPaintPositionManager;
	/** The strategy for finding matching characters */
	private ICharacterPairMatcher fMatcher;
	/** The position tracking the matching characters */
	private Position fPairPosition= new Position(0, 0);
	/** The anchor indicating whether the character is left or right of the caret */
	private int fAnchor;

	
	/**
	 * Creates a new MatchingCharacterPainter for the given source viewer using
	 * the given character pair matcher. The character matcher is not adopted by
	 * this painter. Thus,  it is not disposed. However, this painter requires
	 * exclusive access to the given pair matcher.
	 * 
	 * @param sourceViewer
	 * @param matcher
	 */
	public MatchingCharacterPainter(ISourceViewer sourceViewer, ICharacterPairMatcher matcher) {
		fSourceViewer= sourceViewer;
		fMatcher= matcher;
		fTextWidget= sourceViewer.getTextWidget();
	}
	
	/**
	 * Sets the color in which to highlight the match character.
	 * 
	 * @param color the color
	 */
	public void setColor(Color color) {
		fColor= color;
	}
				
	/*
	 * @see org.eclipse.jface.text.IPainter#dispose()
	 */
	public void dispose() {
		if (fMatcher != null) {
			fMatcher.clear();
			fMatcher= null;
		}
		
		fColor= null;
		fTextWidget= null;
	}
				
	/*
	 * @see org.eclipse.jface.text.IPainter#deactivate(boolean)
	 */
	public void deactivate(boolean redraw) {
		if (fIsActive) {
			fIsActive= false;
			fTextWidget.removePaintListener(this);
			if (fPaintPositionManager != null)
				fPaintPositionManager.unmanagePosition(fPairPosition);
			if (redraw)
				handleDrawRequest(null);
		}
	}
		
	/*
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent event) {
		if (fTextWidget != null)
			handleDrawRequest(event.gc);
	}
	
	/**
	 * Handles a redraw request.
	 * 
	 * @param gc the GC to draw into.
	 */
	private void handleDrawRequest(GC gc) {
		
		if (fPairPosition.isDeleted)
			return;
			
		int offset= fPairPosition.getOffset();
		int length= fPairPosition.getLength();
		if (length < 1)
			return;
		
		if (fSourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension= (ITextViewerExtension5) fSourceViewer;
			IRegion widgetRange= extension.modelRange2WidgetRange(new Region(offset, length));
			
			if (widgetRange == null)
				return;
			
			try {
				// don't draw if the pair position is really hidden and widgetRange just
				// marks the coverage around it.
				IDocument doc= fSourceViewer.getDocument();
				int startLine= doc.getLineOfOffset(offset);
				int endLine= doc.getLineOfOffset(offset + length);
				if (extension.modelLine2WidgetLine(startLine) == -1 || extension.modelLine2WidgetLine(endLine) == -1)
					return;
			} catch (BadLocationException e) {
				return;
			}
				
			offset= widgetRange.getOffset();
			length= widgetRange.getLength();
			
		} else {
			IRegion region= fSourceViewer.getVisibleRegion();
			if (region.getOffset() > offset || region.getOffset() + region.getLength() < offset + length)
				return;
			offset -= region.getOffset();
		}
			
		int endOffset = offset + length-1;
		draw(gc, offset, 1);
		draw(gc, endOffset, 1);
		
	}
	
	/**
	 * Highlights the given widget region.
	 * 
	 * @param gc the GC to draw into
	 * @param offset the offset of the widget region
	 * @param length the length of the widget region
	 */
	private void draw(GC gc, int offset, int length) {
		if (gc != null) {
			Point left= fTextWidget.getLocationAtOffset(offset);
			Point right= fTextWidget.getLocationAtOffset(offset + length);
			
			IPreferenceStore store = CFMLPlugin.getDefault().getPreferenceStore();
			int style = store.getInt(EditorPreferenceConstants.P_BRACKET_MATCHING_STYLE);
			
			switch (style) {
				case EditorPreferenceConstants.BRACKET_MATCHING_OUTLINE: 
				{
					//draw box around character
					gc.setForeground(fColor);
					gc.drawRectangle(left.x, left.y, right.x - left.x - 1, fTextWidget.getLineHeight() - 1);
					break;
				}
				case EditorPreferenceConstants.BRACKET_MATCHING_BACKGROUND: 
				{
					//Paint a background on the character
					gc.setBackground(fColor);
					gc.drawText(fTextWidget.getText(offset,offset),left.x,left.y+1);
					break;
				}
				case EditorPreferenceConstants.BRACKET_MATCHING_BOLD: 
				{
				    int caret= fTextWidget.getCaretOffset();
				    int lineIndex = fTextWidget.getLineAtOffset(caret);
				    int lineStart = fTextWidget.getOffsetAtLine(lineIndex);
				    int lineEnd = -1;
				    if (lineIndex == fTextWidget.getLineCount()) {
				        lineEnd = fTextWidget.getText().length()-1;
				    } else {
				        lineEnd = fTextWidget.getText().indexOf(fTextWidget.getLineDelimiter(),lineStart);
				    }
				    if (offset >= lineStart && offset <= lineEnd) {
				        RGB rgb= PreferenceConverter.getColor(store, EditorPreferenceConstants.P_CURRENT_LINE_COLOR);
				        Color c = EditorsPlugin.getDefault().getSharedTextColors().getColor(rgb);
				        gc.setBackground(c);    
				    } else {
				        gc.setBackground(fTextWidget.getBackground());
				    }
				    
					gc.setForeground(fColor);
					Font oldFont = gc.getFont();
					FontData[] data = gc.getFont().getFontData();
					data[0].setStyle(SWT.BOLD);
					
					Font font = new Font(fTextWidget.getDisplay(),data);
					gc.setFont(font);
					gc.drawText(fTextWidget.getText(offset,offset),left.x,left.y+1);
					gc.setFont(oldFont);
					font.dispose();
					break;
				}
			}
			
			
			
							
		} else {
			fTextWidget.redrawRange(offset, length, true);
		}
	}
	
	/*
	 * @see org.eclipse.jface.text.IPainter#paint(int)
	 */
	public void paint(int reason) {

		IDocument document= fSourceViewer.getDocument();
		if (document == null) {
			deactivate(false);
			return;
		}

		Point selection= fSourceViewer.getSelectedRange();
		if (selection.y > 0) {
			deactivate(true);
			return;
		}
		
		IRegion pair= fMatcher.match(document, selection.x);
		if (pair == null) {
			deactivate(true);
			return;
		}
		
		if (fIsActive) {
			
			if (IPainter.CONFIGURATION == reason) {
				
				// redraw current highlighting
				handleDrawRequest(null);
			
			} else if (pair.getOffset() != fPairPosition.getOffset() || 
					pair.getLength() != fPairPosition.getLength() || 
					fMatcher.getAnchor() != fAnchor) {
				
				// otherwise only do something if position is different
				
				// remove old highlighting
				handleDrawRequest(null);
				// update position
				fPairPosition.isDeleted= false;
				fPairPosition.offset= pair.getOffset();
				fPairPosition.length= pair.getLength();
				fAnchor= fMatcher.getAnchor();
				// apply new highlighting
				handleDrawRequest(null);
			
			}
		} else {
			
			fIsActive= true;
			
			fPairPosition.isDeleted= false;
			fPairPosition.offset= pair.getOffset();
			fPairPosition.length= pair.getLength();
			fAnchor= fMatcher.getAnchor();
			
			fTextWidget.addPaintListener(this);
			fPaintPositionManager.managePosition(fPairPosition);
			handleDrawRequest(null);
		}
	}
	
	/*
	 * @see org.eclipse.jface.text.IPainter#setPositionManager(org.eclipse.jface.text.IPaintPositionManager)
	 */
	public void setPositionManager(IPaintPositionManager manager) {
		fPaintPositionManager= manager;
	}
}
