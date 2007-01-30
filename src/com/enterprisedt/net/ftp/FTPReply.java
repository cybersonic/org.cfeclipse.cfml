/**
 *
 *  Java FTP client library.
 *
 *  Copyright (C) 2000-2003 Enterprise Distributed Technologies Ltd
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
 *        $Log: FTPReply.java,v $
 *        Revision 1.1  2004/11/09 04:44:29  smilligan
 *        Uber monster FTP stuff commit.
 *
 *        This is mostly working now, but there are a few known issues:
 *
 *        External files and remote FTP files don't get a LHS ruler. That means no line numbers, no folding etc.
 *        FTP files don't correctly report when they are read only, so they appear editable, appear to save, but the changes aren't stored on the server.
 *        You can currently only create new ftp connections. There isn't an interface for managing them.
 *        The FTP stuff probably needs to be done in it's own thread with a progress monitor. Othewise it can kill your workspace if it dies.
 *
 *        Revision 1.3  2004/08/31 10:46:11  bruceb
 *        data lines added
 *
 *        Revision 1.2  2004/07/23 08:28:19  bruceb
 *        new constructor
 *
 *        Revision 1.1  2002/11/19 22:01:25  bruceb
 *        changes for 1.2
 *
 *
 */

package com.enterprisedt.net.ftp;

/**
 *  Encapsulates the FTP server reply
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1 $
 */
public class FTPReply {

    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: FTPReply.java,v 1.1 2004/11/09 04:44:29 smilligan Exp $";

    /**
     *  Reply code
     */
    private String replyCode;

    /**
     *  Reply text
     */
    private String replyText;

    /**
     * Lines of data returned, e.g. FEAT
     */
    private String[] data;

    /**
     *  Constructor. Only to be constructed
     *  by this package, hence package access
     *
     *  @param  replyCode  the server's reply code
     *  @param  replyText  the server's reply text
     */
    FTPReply(String replyCode, String replyText) {
        this.replyCode = replyCode;
        this.replyText = replyText;
    }
    
    
    /**
     *  Constructor. Only to be constructed
     *  by this package, hence package access
     *
     *  @param  replyCode  the server's reply code
     *  @param  replyText  the server's full reply text
     *  @param  data       data lines contained in reply text
     */
    FTPReply(String replyCode, String replyText, String[] data) {
        this.replyCode = replyCode;
        this.replyText = replyText;
        this.data = data;
    }
    
    
    /**
     *  Constructor. Only to be constructed
     *  by this package, hence package access
     *
     *  @param  rawReply  the server's raw reply
     */
    FTPReply(String rawReply) {        
        // all reply codes are 3 chars long
        rawReply = rawReply.trim();
        replyCode = rawReply.substring(0, 3);
        if (rawReply.length() > 3)
            replyText = rawReply.substring(4);
        else
            replyText = "";
    }

    /**
     *  Getter for reply code
     *
     *  @return server's reply code
     */
    public String getReplyCode() {
        return replyCode;
    }

    /**
     *  Getter for reply text
     * 
     *  @return server's reply text
     */
    public String getReplyText() {
        return replyText;
    }
    
    /**
     * Getter for reply data lines
     * 
     * @return array of data lines returned (if any). Null
     *          if no data lines
     */
    public String[] getReplyData() {
        return data;
    }

}
