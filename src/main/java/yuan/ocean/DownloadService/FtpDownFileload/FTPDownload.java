package yuan.ocean.DownloadService.FtpDownFileload;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import yuan.ocean.DownloadService.IDownloadService;

import java.io.*;

public class FTPDownload implements IDownloadService {
 
    public  void downloadDirectory(FTPClient ftpClient, String parentDir,
            String currentDir, String saveDir) throws IOException {
 
    	String dirToList = parentDir;
        if (!currentDir.equals("")) {
            dirToList += "/" + currentDir;
        }
       
        FTPFile[] subFiles = ftpClient.listFiles(dirToList);
     
        if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = parentDir + "/" + currentDir + "/"
                        + currentFileName;
                if (currentDir.equals("")) {
                    filePath = parentDir + "/" + currentFileName;
                }
     
                String newDirPath = saveDir + parentDir + File.separator
                        + currentDir + File.separator + currentFileName;
                if (currentDir.equals("")) {
                    newDirPath = saveDir + parentDir + File.separator
                              + currentFileName;
                }
            
                if (aFile.isDirectory()) {
                    // create the directory in saveDir
                    File newDir = new File(newDirPath);
                    boolean created = newDir.mkdirs();
                    if (created) {
                        System.out.println("CREATED the directory: " + newDirPath);
                    } else {
                        System.out.println("COULD NOT create the directory: " + newDirPath);
                    }
     
                    // download the sub directory
                    downloadDirectory(ftpClient, dirToList, currentFileName,
                            saveDir);
                } else {
                    // download the file
                    boolean success = downloadSingleFile(ftpClient, filePath,
                            newDirPath);
                    if (success) {
                        System.out.println("DOWNLOADED the file: " + filePath);
                    } else {
                        System.out.println("COULD NOT download the file: "
                                + filePath);
                    }
                }
            }
        }
    }
 
    
 
    public  boolean downloadSingleFile(FTPClient ftpClient,
            String remoteFilePath, String savePath) throws IOException {
 
    	File downloadFile = new File(savePath);
        
        File parentDir = downloadFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdir();
        }
             
        OutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(downloadFile));
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remoteFilePath, outputStream);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
 
    }


    public void download(String url, String userName, String passWord,String urlParentPath,String filterPattern ,String fileName, String saveFilePath) {
        int port = 21;;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(url, port);
            ftpClient.enterLocalPassiveMode();
            ftpClient.login(userName, passWord);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            downloadSingleFile(ftpClient,urlParentPath,saveFilePath+"\\"+fileName);
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}