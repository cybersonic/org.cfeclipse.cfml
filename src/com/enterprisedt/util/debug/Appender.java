/**
 * 
 *  Copyright (C) 2000-2004 Enterprise Distributed Technologies Ltd
 *
 *  www.enterprisedt.com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Bug fixes, suggestions and comments should be sent to bruce@enterprisedt.com
 *
 *  Change Log:
 *
 *    $Log: Appender.java,v $
 *    Revision 1.1  2004/11/09 04:44:29  smilligan
 *    Uber monster FTP stuff commit.
 *
 *    This is mostly working now, but there are a few known issues:
 *
 *    External files and remote FTP files don't get a LHS ruler. That means no line numbers, no folding etc.
 *    FTP files don't correctly report when they are read only, so they appear editable, appear to save, but the changes aren't stored on the server.
 *    You can currently only create new ftp connections. There isn't an interface for managing them.
 *    The FTP stuff probably needs to be done in it's own thread with a progress monitor. Othewise it can kill your workspace if it dies.
 *
 *    Revision 1.1  2004/05/01 16:55:41  bruceb
 *    first cut
 *
 *
 */
package com.enterprisedt.util.debug;

/**
 *  Interface for classes that output log
 *  statements 
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1 $
 */
public interface Appender {

    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: Appender.java,v 1.1 2004/11/09 04:44:29 smilligan Exp $";

    /**
     * Close this appender
     */
    public void close();
    
    /**
     * Log a message
     * 
     * @param msg  message to log
     */
    public void log(String msg);

    /**
     * Log a stack trace
     * 
     * @param t  throwable object
     */
    public void log(Throwable t);
}
