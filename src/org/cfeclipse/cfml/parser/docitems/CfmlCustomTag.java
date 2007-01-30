/*
 * Created on Apr 20, 2004
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
package org.cfeclipse.cfml.parser.docitems;

/**
 * @author Oliver Tupman
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CfmlCustomTag extends TagItem {
	
	public boolean hasCloser = true;
	/**
	 * 
	 * @see org.cfeclipse.cfml.parser.docitems.DocItem#validChildAddition(org.cfeclipse.cfml.parser.DocItem)
	 */
	public boolean validChildAddition(DocItem parentItem) {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.cfeclipse.cfml.parser.TagItem#hasClosingTag()
	 */
	public boolean hasClosingTag() throws NullPointerException {
		return hasCloser;
	}
	/* (non-Javadoc)
	 * @see org.cfeclipse.cfml.parser.TagItem#hasClosingTag()
	 */
	public boolean isHybrid() throws NullPointerException {
		return true;
	}
	/**
	 * @param line
	 * @param startDocPos
	 * @param endDocPos
	 * @param name
	 */
	public CfmlCustomTag(int line, int startDocPos, int endDocPos, String name) {
		super(line, startDocPos, endDocPos, name);
	}
	
	
}
