/*
 * Created on Oct 28, 2004
 *
 * The MIT License
 * Copyright (c) 2004 Stephen Milligan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package org.cfeclipse.cfml.editors.dnd;

import java.util.Iterator;

import org.cfeclipse.cfml.editors.ICFDocument;
import org.cfeclipse.cfml.parser.docitems.CfmlTagItem;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * @author Stephen Milligan
 *
 * This listener keeps track of where the mouse is relative to the currently selected text
 * and whether or not the mouse is currently down.
 */
public class SelectionCursorListener implements KeyListener, MouseListener, MouseMoveListener, MouseTrackListener, ISelectionChangedListener {
    /**
     * The text editor that the selection listener is installed on
     */
    //private ITextEditor editor = null;
    /**
     * The StyledText that belongs to the viewer
     */
    private StyledText textWidget = null;
    /**
     * The projection viewer for this editor
     */
    private ProjectionViewer fViewer = null;
    
    /**
     * This allows us to figure out where a point is in widget co-ordinate space.
     */
    private WidgetPositionTracker widgetOffsetTracker = null;
    
    /**
     * The cursor that indicates stuff can be dragged
     */
    private Cursor arrowCursor = null;
    /**
     * The regular text I-Beam cursor 
     */
    private Cursor textCursor = null;
    
    /**
     * The offset of the start of the selected text in viewer co-ordinates
     */
    public int selectionStart = -1;
    /**
     * The contents of the selection according to the viewer
     */
    public String selection = "";
    /**
     * Is the mouse currently hovering over a selected area
     */
    private boolean hovering = false;
    /**
     * Was the mouse down the last time we were notified
     */
    private boolean mouseDown = false;
    
    /**
     * Indicates whether or not the selection needs to be expanded
     * to contain folded text. This is set to true when the 
     * selection ends at the end of a line.
     */
    public boolean expandSelection = false;
    
    /**
     * This allows us to handle the case where the user clicks and releases on a selection.
     * Mouse down sets it to true
     * Mouse move sets it to false
     * Mouse up checks it's value and calls reset() if true.
     */
    private boolean downUp = false;
    
    /**
     * This class listens to the mouse position relative to any selected text 
     * and keeps track of whether or not the mouse is currently over a selection.
     */
    public SelectionCursorListener(ITextEditor editor, ProjectionViewer viewer) {
        //this.editor = editor;
        this.textWidget = viewer.getTextWidget();
        this.fViewer = viewer;
        this.arrowCursor = new Cursor(this.textWidget.getDisplay(),SWT.CURSOR_ARROW);
        this.textCursor = new Cursor(this.textWidget.getDisplay(),SWT.CURSOR_IBEAM);
        this.widgetOffsetTracker = new WidgetPositionTracker(this.textWidget);
    }
    
    /**
     * Resets the listener to a state where the mouse isn't hovering over a selection.
     *
     */
    public void reset() {
        this.hovering = false;
        this.selectionStart = -1;
        this.selection = "";
        this.textWidget.setCursor(this.textCursor);
        this.mouseDown = false;
        //System.out.println("Listener reset");
    }
    
    /**
     * Allows the drag drop listener to know if it's ok to start a drag.
     * 
     * @return
     */
    public boolean doDrag() {
        if (this.hovering && this.mouseDown) {
            return true;
        }
        return false;
    }
    
    
    /**
     * Sent when the mouse pointer passes into the area of
     * the screen covered by a control.
     *
     * @param e an event containing information about the mouse enter
     */
    public void mouseEnter(MouseEvent e) {
        // do nothing
        //reset();
    }

    /**
     * Sent when the mouse pointer passes out of the area of
     * the screen covered by a control.
     *
     * @param e an event containing information about the mouse exit
     */
    public void mouseExit(MouseEvent e) {
        //reset();
    }

    /**
     * Sent when the mouse pointer hovers (that is, stops moving
     * for an (operating system specified) period of time) over
     * a control.
     *
     * @param e an event containing information about the hover
     */
    public void mouseHover(MouseEvent e) {
        // do nothing
    }
    
    
    /**
     * Sent when the mouse moves.
     *
     * @param e an event containing information about the mouse move
     */
    public void mouseMove(MouseEvent e) {
        // If the selection is draggable we want to ignore this event.
        
        if (!this.mouseDown) {
        	
	        Point pt = new Point(e.x,e.y);
	        
	        if (pointOnSelection(pt)) {
	            this.textWidget.setCursor(this.arrowCursor);
	            this.hovering = true;
	        }
	        else {
	            this.textWidget.setCursor(this.textCursor);
	            this.hovering = false;
	        }
        }
        else {
            this.downUp = false;
        }
    }
    
    /**
     * This is notified when the selection is changed in the viewer.
     * 
     * If a drag is in progress or the cursor is already over a selection, 
     * the selection change is ignored.
     * 
     */
    
    public void selectionChanged(SelectionChangedEvent event) {
        if (!this.hovering) {
	        ITextSelection sel = (ITextSelection)this.fViewer.getSelection();
	        this.selectionStart = sel.getOffset();
	        this.selection = sel.getText();
        }
    }

    /**
     * Determines if the selection needs to be expanded
     * to account for a closed fold.
     * 
     * If so, it modifies the selection in the editor
     * and updates the selected text.
     * 
     */
    private void checkFolding() {
        int widgetOffset = this.fViewer.modelOffset2WidgetOffset(this.selectionStart);
        String[] lines = this.selection.split(this.textWidget.getLineDelimiter());
        int widgetLine = this.textWidget.getContent().getLineAtOffset(widgetOffset);
        int lineCount = 0;
        
        if (lines.length > 0) {
            lineCount = lines.length -1;
        }
        
        // If we've already grabbed the text inside a fold we 
        // could end up with more lines than the widget knows about.
        if (widgetLine+lineCount > this.textWidget.getLineCount()) {
            return;
        }

        String line = this.textWidget.getContent().getLine(widgetLine+lineCount);
        
        if (lines.length > 0 
                && line.equals(lines[lines.length-1])) {
            // Figure out the viewer offset for the start of the line.
            int widgetLineStart = this.textWidget.getContent().getOffsetAtLine(widgetLine + lineCount);
            int viewerLineStart = this.fViewer.widgetOffset2ModelOffset(widgetLineStart);
           
            ProjectionAnnotationModel model = this.fViewer.getProjectionAnnotationModel();
            Iterator i = model.getAnnotationIterator();
            while (i.hasNext()) {
                ProjectionAnnotation annotation = (ProjectionAnnotation)i.next();
                Position pos = model.getPosition(annotation);
                /* Check if the line is the start line of a collapsed
                 * region.
                 */
                if (pos.offset == viewerLineStart 
                        && annotation.isCollapsed()) {
                    int selectionLength = viewerLineStart - this.selectionStart + pos.length;
                    // Grab the current caret position so we can put it back after changing the selection
                    Point oldCaret = this.textWidget.getCaret().getLocation();
                    TextSelection sel = new TextSelection(this.fViewer.getDocument(),this.selectionStart,selectionLength);
                    this.fViewer.setSelection(sel,false);
                    /* Restore the caret. Using this rather than textWidget.setCaretOffset()
                     * because setCaretOffset() clears the selection
                     */ 
                    this.textWidget.getCaret().setLocation(oldCaret);
                }
            }
        }
        
    }
    
    public void mouseDoubleClick(MouseEvent e) {    	
    	//see comment below
    }
/*
 * 
 * Commented for now because denny is using doubleClick in MarkOccurrencesListener
 * 
    /**
     * Sent when a mouse button is pressed twice within the 
     * (operating system specified) double click period.
     *
     * @param e an event containing information about the mouse double click
     *
     * @see org.eclipse.swt.widgets.Display#getDoubleClickTime()
     *//*
    public void mouseDoubleClick(MouseEvent e) {
        

        TextSelection sel = (TextSelection)this.fViewer.getSelection();
        
        int startpos = sel.getOffset() + sel.getLength();
        
        if ((e.stateMask & SWT.MOD1) != 0) { 
                //|| (e.stateMask & SWT.COMMAND) != 0
                //|| (e.stateMask & SWT.CONTROL) != 0) {
                
        //if ((e.stateMask & SWT.CONTROL) != 0 ) {
            ICFDocument cfd = (ICFDocument) this.fViewer.getDocument();
    		CfmlTagItem cti = cfd.getTagAt(startpos, startpos, true);
    		
    		
            int start = 0;
            int length = 0;
    		if (cti != null) {

	            if ((e.stateMask & SWT.SHIFT) != 0 
	                    && cti.matchingItem != null) {
	                
	                if (cti.matchingItem.getStartPosition() < cti.getStartPosition()) {
	                    start = cti.matchingItem.getStartPosition();
	                    length = cti.getEndPosition()-cti.matchingItem.getStartPosition()+1;
	                }
	                else {
	                    start = cti.getStartPosition();
	                    length = cti.matchingItem.getEndPosition()-cti.getStartPosition()+1;
	                }

	            }
	            else {
	               if (cti.matchingItem != null 
	                       && cti.matchingItem.getStartPosition() <= startpos
	                       && cti.matchingItem.getEndPosition() >= startpos) {
	                   start = cti.matchingItem.getStartPosition();
	                   length = cti.matchingItem.getEndPosition()-cti.matchingItem.getStartPosition()+1;
	               }
	               else {
	                   start = cti.getStartPosition();
	                   length = cti.getEndPosition()-cti.getStartPosition()+1;
	               }
	            }

	            TextSelection newSel = new TextSelection(cfd,start,length);
	            this.fViewer.setSelection(newSel);
                
    		}
        }
        else {

            startpos = this.fViewer.getSelectedRange().x;
            selectWord(startpos, e);
        }
        

    }

    
	protected boolean selectWord(int caretPos, MouseEvent e) {

		IDocument doc = this.fViewer.getDocument();
		int startPos, endPos;
		String normalWordChars = "-";
		String breakWordChars = "_";
		String wordChars = normalWordChars;
		String altWordChars = "-.";
		String shiftWordChars = "-_";
		try {
			if ((e.stateMask == SWT.ALT || e.stateMask == SWT.SHIFT + SWT.ALT)) {
				wordChars = wordChars + altWordChars;
			}
			if ((e.stateMask == SWT.SHIFT || e.stateMask == SWT.SHIFT + SWT.ALT)) {
				wordChars = wordChars + shiftWordChars;
				breakWordChars = "";
			}

			int pos = caretPos;
			char c;

			while (pos >= 0) {
				c = doc.getChar(pos);
				if (breakWordChars.indexOf(c) >= 0 || !Character.isJavaIdentifierPart(c) && wordChars.indexOf(c) < 0)
					break;
				--pos;
			}

			startPos = pos;

			pos = caretPos;
			int length = doc.getLength();

			while (pos < length) {
				c = doc.getChar(pos);
				if (breakWordChars.indexOf(c) >= 0 || !Character.isJavaIdentifierPart(c) && wordChars.indexOf(c) < 0)
					break;
				++pos;
			}

			endPos = pos;
			selectRange(startPos, endPos);
			return true;

		} catch (BadLocationException x) {
			// ?
		}

		return false;
	}


	private void selectRange(int startPos, int stopPos) 
	{
		int offset = startPos + 1;
		int length = stopPos - offset;
		this.fViewer.setSelectedRange(offset, length);
	}

    
*/    
    
    
    
    /**
     * Sent when a mouse button is pressed.
     *
     * @param e an event containing information about the mouse button press
     */
    public void mouseDown(MouseEvent e) {
        if ((e.stateMask & SWT.CONTROL) == 0) {
            this.mouseDown = true;
            this.downUp = true;
        }
    }

    /**
     * Sent when a mouse button is released.
     *
     * @param e an event containing information about the mouse button release
     */
    public void mouseUp(MouseEvent e) {
        
        if ((e.stateMask & SWT.CONTROL) == 0) {
            this.mouseDown = false;
	        if (this.downUp) {
	            reset();
	        }
	        if (this.selectionStart >= 0) {
	            checkFolding();
	        }
	        
        }
    }

	
	/**
	 * Returns true if the given point is on top of a selection.
	 * 
	 * @param pt - Point in widget co-ordinates
	 * @return
	 */
	private boolean pointOnSelection(Point pt) {
	    try {
	        if (this.selectionStart >= 0) {
	            int offset = this.widgetOffsetTracker.getWidgetOffset(pt);
	            // Convert to viewer co-ordinates
	            offset = this.fViewer.widgetOffset2ModelOffset(offset);
	            
	            if(this.selectionStart <= offset 
	                    && this.selectionStart + this.selection.length() > offset) {
	                 return true;
	            }
	        }
	    }
        catch (Exception ex) {
            // do nothing
        }
        return false;
	}
	
	
	/*
	 * TEMPORARY debugging stuff
	 * 
	 */

	/**
	 * Sent when a key is pressed on the system keyboard.
	 *
	 * @param e an event containing information about the key press
	 */
	public void keyPressed(KeyEvent e) {
	    //System.out.println("Key Pressed " + e.keyCode);
	}

	/**
	 * Sent when a key is released on the system keyboard.
	 *
	 * @param e an event containing information about the key release
	 */
	public void keyReleased(KeyEvent e) {

	    //System.out.println("Key Released " + e.keyCode);
	
	}
	
	
}
