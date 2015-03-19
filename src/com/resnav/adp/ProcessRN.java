/**
 * 
 */
package com.resnav.adp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

/**
 * @author Gennady
 *
 */
public class ProcessRN {
	public static String createCSVOutput(String fileName, String folder, ArrayList<ArrayList<Object>> alist) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateStr = sdf.format(System.currentTimeMillis());
		String fileNameBase = fileName.substring(0, fileName.indexOf("."));
		String fileExt = fileName.substring(fileName.indexOf(".")+1);
		String fullFileName = folder+"/"+dateStr+ "_"+fileName;
		File f = new File(fullFileName);
		int suffix = 0;
		while (f.exists()) {
			suffix++;
			//fileNameBase += "#";
			fullFileName = folder+"/"+dateStr+ "_"+fileNameBase+"_"+suffix+"."+fileExt;
			f = new File(fullFileName);
		}
		FileWriter fw = new FileWriter(fullFileName);
        for (ArrayList<Object> row: alist) {
        	for (int i=0; i< row.size(); i++) {
        		fw.write(row.get(i).toString());
        		if (i< row.size()-1) fw.write(",");
        	}
        	fw.write("\r\n");
        }
        fw.close();
        System.out.println("Created file: "+fullFileName);
        return fullFileName;
	}
	
	/**
	 * 
	 * @param props
	 * @param fileToFTP
	 * @return
	 */
	public static boolean sendToSFTP(Properties props, String fileToFTP){
	    boolean result = false;
		try {
		   //props.load(new FileInputStream("properties/" + propertiesFilename));
		   String serverAddress = props.getProperty("serverAddress").trim();
		   String userId = props.getProperty("userId").trim();
		   String password = props.getProperty("password").trim();
		   String remoteDirectory = props.getProperty("remoteDirectory").trim();
		   String prefix = props.getProperty("outputFilePrefix");
		   result = sendToSFTP(serverAddress,userId, password, remoteDirectory, fileToFTP, prefix);
		 
		}
		catch (Exception ex) {
		   ex.printStackTrace();
		   return false;
		}
		return result;
	 }
	
	public static boolean sendToSFTP(String serverAddress, String userId, String password, String remoteDirectory, String fileToFTP, String prefix){
		 
		//Properties props = new Properties();
		StandardFileSystemManager manager = new StandardFileSystemManager();
	 
		try {
		   String fileName = fileToFTP.substring(fileToFTP.lastIndexOf("/")+1);
		   //check if the file exists
		   File file = new File(fileToFTP);
		   if (!file.exists())
			   throw new RuntimeException("Error. Local file not found");
		 
		   //Initializes the file manager
		   manager.init();
		   String prefixUnderscore = (prefix != null && prefix.length() > 1) ? prefix +"_" : "";
		   //Setup our SFTP configuration
		   FileSystemOptions opts = new FileSystemOptions();
		   SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
		     opts, "no");
		   SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
		   SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);
		   
		   //Create the SFTP URI using the host name, userid, password,  remote path and file name
		   String sftpUri = "sftp://" + userId + ":" + password +  "@" + serverAddress + "/" +
				   			remoteDirectory + prefixUnderscore + fileName;
		    
		   // Create local file object
		   FileObject localFile = manager.resolveFile(file.getAbsolutePath());
		 
		   // Create remote file object
		   FileObject remoteFile = manager.resolveFile(sftpUri, opts);
		 
		   // Copy local file to sftp server
		   remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
		   System.out.println("File upload successful");
		 
		}
		catch (Exception ex) {
		   ex.printStackTrace();
		   return false;
		}
		finally {
			manager.close();
		}
	 
		return true;
	 }
	
	/**
	 * when we need to download input file for processing
	 * @param props
	 * @param suffix
	 * @return
	 */
	public static String downloadFileFromFTP(Properties props, String suffix) {
		String result = null;
		String ftpHost = props.getProperty("ftpHost"+suffix).trim();
	    String ftpUser = props.getProperty("ftpUser"+suffix).trim();
	    String ftpPassword = props.getProperty("ftpPassword"+suffix).trim();
	    String ftpDirectory = props.getProperty("ftpDirectory"+suffix).trim();
	    String ftpFile = props.getProperty("ftpFile"+suffix).trim();
	    String downloadPath = props.getProperty("inputPath"+suffix).trim();
	    FTPClient ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        try {
	        ftp.connect(ftpHost);
	        reply = ftp.getReplyCode();
	        if (!FTPReply.isPositiveCompletion(reply)) {
	            ftp.disconnect();
	            throw new Exception("Exception in connecting to FTP Server");
	        }
	        ftp.login(ftpUser, ftpPassword);
	        ftp.setFileType(FTP.BINARY_FILE_TYPE);
	        ftp.enterLocalPassiveMode();
        } catch (IOException ex) {
        	ex.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        result = ftpFile;
        try (FileOutputStream fos = new FileOutputStream(downloadPath+"/"+ftpFile)) {
        	String remoteFilePath = "";
        	if (ftpDirectory.length() > 0) remoteFilePath += ftpDirectory + "/";
        	remoteFilePath += ftpFile;
            ftp.retrieveFile(remoteFilePath, fos);
            ftp.logout();
            ftp.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return result;
	}

}
