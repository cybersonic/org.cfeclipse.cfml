/*
 * Created on Nov 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.cfeclipse.cfml.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.cfeclipse.cfml.CFMLPlugin;


/**
 * @author Stephen Milligan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class FTPConnectionProperties {

	private Properties connectionProperties;
	
	private static String ftype = "type";
	private static String fHost = "host";
	private static String fPath = "path";
	private static String fUsername = "username";
	private static String fPassword = "password";
	private static String fConnectionid = "connectionid";
	private static String fPort = "port";
	private static String fPassive = "passive";
	private static String fSecure = "secure";
	
	
	File storageDirectory;
	
	

    
    
    public static String[] getConnectionIds() {
    	File storageDirectory = new File(CFMLPlugin.getDefault().getStateLocation().toString() + "/ftpconnections");
		if (!storageDirectory.exists()) {
			storageDirectory.mkdir();
		}
		
		
		
		String connections[] = storageDirectory.list();
		
		if (connections != null) {
			return connections;
		}
		
		return new String[0];
		
    }


    public static void deleteConnection(String connectionid) {
    	File storageFile = new File(CFMLPlugin.getDefault().getStateLocation().toString() + "/ftpconnections/" + connectionid);

    	if (storageFile.exists()) {
    		storageFile.delete();
    	}
    	
    	
    }
	
	
	
	
    /**
     * 
     */
    public FTPConnectionProperties(String connectionId) {
    	storageDirectory = new File(CFMLPlugin.getDefault().getStateLocation().toString() + "/ftpconnections");
    	connectionProperties = new Properties();
    	
    	
    	if (connectionId != null) {
    		String[] connections = getConnectionIds();
	    	for (int i=0;i<connections.length;i++) {
				if (connections[i].equalsIgnoreCase(connectionId)) {
					File connectionFile = new File(storageDirectory.toString() + "/" + connections[i]);
					try {
						FileInputStream input = new FileInputStream(connectionFile);
						connectionProperties.load(input);
						input.close();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}    
    	}
    }
    
    public void save() {
    	File outputFile = new File(storageDirectory + "/" + connectionProperties.getProperty(fConnectionid));
    	try {
	    	FileOutputStream outputStream = new FileOutputStream(outputFile);
	    	connectionProperties.store(outputStream,"FTP Connection details.");
	    	outputStream.close();
	    	System.out.println("Ftp connection details saved to " + outputFile.toString());
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    public String getConnectionid() {
    	return connectionProperties.getProperty(fConnectionid,"");
    }

    public void setConnectionid(String connectionid) {
    	connectionProperties.setProperty(fConnectionid,connectionid);
    }

    public String getType() {
    	String property = connectionProperties.getProperty(ftype,"");
    	if(property == null || property.length() == 0){		//Backwards compatability
    		return "ftp";
    	}
    	return connectionProperties.getProperty(ftype,"");
    }

    public void setType(String type) {
    	connectionProperties.setProperty(ftype,type);
    }

    public String getHost() {
    	return connectionProperties.getProperty(fHost,"");
    }

    public void setHost(String host) {
    	connectionProperties.setProperty(fHost,host);
    }


    public int getPort() {
    	return Integer.parseInt(connectionProperties.getProperty(fPort,"21"));
    }

    public void setPort(int port) {
    	connectionProperties.setProperty(fPort,String.valueOf(port));
    }


    public boolean getPassive() {
    	return new Boolean(connectionProperties.getProperty(fPassive,"true")).booleanValue() ;
    }

    public void setPassive(boolean passive) {
    	connectionProperties.setProperty(fPassive,String.valueOf(passive));
    }


    public boolean getSecure() {
        Boolean secure = new Boolean(connectionProperties.getProperty(fSecure,"false"));
    	return secure.booleanValue();
    }

    public void setSecure(boolean secure) {
    	connectionProperties.setProperty(fSecure,String.valueOf(secure));
    }
    

    public String getPath() {
    	return connectionProperties.getProperty(fPath,"/");
    }

    public void setPath(String path) {
    	connectionProperties.setProperty(fPath,path);
    }

    

    public String getUsername() {
    	return connectionProperties.getProperty(fUsername,"");
    }

    public void setUsername(String username) {
    	connectionProperties.setProperty(fUsername,username);
    }

    

    public String getPassword() {
    	return connectionProperties.getProperty(fPassword,"");
    }

    public void setPassword(String password) {
    	connectionProperties.setProperty(fPassword,password);
    }
    
    
    public String toString() {
    	return connectionProperties.getProperty(fConnectionid);
    }
    
    
    
}
