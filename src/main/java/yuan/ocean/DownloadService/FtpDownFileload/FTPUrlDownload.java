package yuan.ocean.DownloadService.FtpDownFileload;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import yuan.ocean.DownloadService.IDownloadService;
import yuan.ocean.SensorConfigInfo;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Yuan on 2017/6/14.
 */
public class FTPUrlDownload implements IDownloadService{

    public void download(String url, String userName, String passWord, String urlParentPath, String filterPattern, String fileName, String saveFilePath) {
       //get filePath by parameters of saveFilePath and filename

        File file=new File(saveFilePath);
        if (!file.exists()){
            file.mkdir();
        }
        String filePath= saveFilePath+"\\"+fileName;
        getFileListUrl(url, urlParentPath, userName, passWord, filterPattern, filePath);
    }
    private void getFileListUrl(String ftpUrl,String parentPath,String userName,String passWord,String filterPattern,String filePath){
        FTPClient ftpClient=new FTPClient();
        try {
            ftpClient.connect(ftpUrl,21);
            ftpClient.login(userName,passWord);
            //recursion to get all file urls in the ftp under parentPath and write it to file

            BufferedWriter urlWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
            getAllFtpFileUrl(ftpClient,ftpUrl,parentPath,filterPattern,urlWriter);
            urlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ;
    }
    private void getAllFtpFileUrl(FTPClient ftpClient,String ftpUrl,String parentPath,String filterPattern,BufferedWriter writer){
        try {
            FTPFile[] files= ftpClient.listFiles(parentPath);
            if (files!=null&&files.length>0) {
                for (FTPFile ftpFile : files) {
                    String currentFileName = ftpFile.getName();
                    if (currentFileName.equals(".") || currentFileName.equals("..")) {
                        // skip parent directory and the directory itself
                        continue;
                    }
                    if (ftpFile.isDirectory())
                        getAllFtpFileUrl(ftpClient,ftpUrl, parentPath + currentFileName+"/",filterPattern,writer);
                    if (ftpFile.isFile()) {
                        String fileUrl="ftp://" + ftpUrl + parentPath + currentFileName;
                        boolean isMatch= Pattern.matches(filterPattern,fileUrl);
                        if (isMatch) {
                            writer.write(fileUrl + "\r\n");
                            writer.flush();
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
