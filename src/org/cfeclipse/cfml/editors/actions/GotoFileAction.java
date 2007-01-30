/*
 * Created on Jun 24, 2004
 *
 * The MIT License
 * Copyright (c) 2004 Oliver Tupman
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
package org.cfeclipse.cfml.editors.actions;

import java.util.Iterator;

import org.cfeclipse.cfml.editors.ICFDocument;
import org.cfeclipse.cfml.parser.CFDocument;
import org.cfeclipse.cfml.parser.CFNodeList;
import org.cfeclipse.cfml.parser.docitems.CfmlTagItem;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * @author OLIVER
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GotoFileAction implements IEditorActionDelegate {

	ITextEditor editor = null;
	protected Shell shell;
    protected IPath newfilepath;
    protected IPath currfilepath;
	/**
	 * 
	 */
	public GotoFileAction() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if( targetEditor instanceof ITextEditor )
		{
			editor = (ITextEditor)targetEditor;
		}	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) 
	{
		//try
		//{
			if(editor != null)
			{	
				IDocument doc =  editor.getDocumentProvider().getDocument(editor.getEditorInput()); 
				ISelection sel = editor.getSelectionProvider().getSelection();
				
				int docOffset = ((ITextSelection)sel).getOffset();
				//String parttype = doc.getPartition(((ITextSelection)sel).getOffset()).getType();
				//String start="";
				
				//if(doc instanceof ICFDocument)
				//{
					//System.out.println("test");
					ICFDocument cfDoc = (ICFDocument)doc;
					CFDocument docRoot = cfDoc.getCFDocument();
					String attrString = "[#startpos<" + docOffset + " and #endpos>" + docOffset + "]";
					
					CFNodeList matchingNodes = docRoot.getDocumentRoot().selectNodes("//cfinclude" + attrString);
					Iterator nodeIter = matchingNodes.iterator();
					
					CfmlTagItem currItem = null;
					
					if(nodeIter.hasNext())
					{
						currItem = (CfmlTagItem)nodeIter.next(); 
					}
					else
					{
						matchingNodes = docRoot.getDocumentRoot().selectNodes("//cfmodule" + attrString);
						nodeIter = matchingNodes.iterator();
						if(nodeIter.hasNext()) {
							currItem = (CfmlTagItem)nodeIter.next();
						}
						else
							return;
					}
						
					String template = currItem.getAttributeValue("template");
					IEditorPart iep = this.editor.getSite().getPage().getActiveEditor();
					ITextEditor editor = (ITextEditor)iep;
					
					String pth = (
						(IResource)((FileEditorInput)editor.getEditorInput()
					).getFile()).getProject().toString();

					String currentpath = ( (IResource) ((FileEditorInput)editor.getEditorInput()).getFile() ).getFullPath().toString();
					String currentfile = ( (IResource) ((FileEditorInput)editor.getEditorInput()).getFile() ).getName();
					
					//first lets get where we really are in the project
					currentpath = currentpath.replaceFirst(currentfile,"");			
					
					if(template.startsWith("/"))
					{
						currentpath = pth.replaceFirst("P/","") + template;
					}
					else
					{
						currentpath += template;
					}
					
						// System.out.println("about to open the file");
						GenericOpenFileAction openFileAction;
						openFileAction = new GenericOpenFileAction();
						openFileAction.setFilename(currentpath);
						openFileAction.run();
					
					if(!openFileAction.isSuccess()){
					    
					    String projectName = pth.replaceFirst("P/","");
					    currentpath = currentpath.replaceFirst(projectName, "");
					    currentpath = currentpath.replaceFirst("/", "");
// System.out.println(currentpath);
					    
					    InputDialog confirmDialog = new InputDialog(shell,"Create File","Filename:",currentpath,null);
						
					    if (confirmDialog.open() == org.eclipse.jface.window.Window.OK) {
							String newname = confirmDialog.getValue(); 
							GenericNewFileAction newFileAction = new GenericNewFileAction();
							newFileAction.setFilename(projectName + newname);
							newFileAction.run();
							if(!newFileAction.isCreated()){ 
							     MessageBox msg = new MessageBox(editor.getEditorSite().getShell());   
							     msg.setText("Error!");  
							     msg.setMessage( "There was a problem trying to create the file \"" + newname + "\" make sure all directories in the path exist and that the path you typed is correct");   
							     msg.open();  
							}
					    }
					}
					
					
				//}
		
				//if(parttype == CFPartitionScanner.CF_TAG) 
				//{
					//System.out.println("10 before & after: \'"+ doc.get(docOffset - 10, docOffset) + "\'");
				//}
			}
		//}
		//catch(BadLocationException e)
		//{
			//e.printStackTrace(System.err);
			// System.out.println("no file found" + System.err);
		//}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
