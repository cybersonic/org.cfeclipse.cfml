/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.cfeclipse.cfml.editors.text;

import org.cfeclipse.cfml.editors.CFMLEditor;
import org.cfeclipse.cfml.editors.ICFDocument;
import org.cfeclipse.cfml.parser.CFDocument;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;

public class CFMLReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	/**
	 * How long the reconciler will wait for further text changes before
	 * reconciling
	 */
	public static final int DELAY= 500;

	private CFMLEditor fEditor;

	public CFMLReconcilingStrategy(CFMLEditor editor) {
		fEditor= editor;
	}

	private void internalReconcile() {
		try {
            CFDocument model= fEditor.getCFModel();
			if (model instanceof CFDocument) {
				((CFDocument)model).reconcile();
			}
		} catch (Exception e) {
			e.printStackTrace();
			//CFMLPlugin.log(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion partition) {
		internalReconcile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		internalReconcile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor monitor) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
	 */
	public void initialReconcile() {
		internalReconcile();
	}
}
