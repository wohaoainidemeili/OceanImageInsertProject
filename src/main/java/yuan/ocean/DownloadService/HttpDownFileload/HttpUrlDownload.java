package yuan.ocean.DownloadService.HttpDownFileload;

import yuan.ocean.DownloadService.IDownloadService;
import yuan.ocean.Util.HttpRequestAndPost;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yuan on 2017/6/14.
 */
public class HttpUrlDownload implements IDownloadService {

    public void download(String url, String userName, String passWord, String urlParentPath, String filterPattern, String fileName, String saveFilePath) {
        //if there is no filePath then create it
        File file=new File(saveFilePath);
        if (!file.exists()){
            file.mkdir();
        }
        String filePath=saveFilePath+"\\"+fileName;
        this.getFileListUrl(url, urlParentPath, userName, passWord, filterPattern, filePath);
    }
    public void getFileListUrl(String ftpUrl, String parentPath, String userName, String passWord,String filterPattern,String filePath) {
        //get url str

        //directoryPatternStr
        String dirPatternStr="(?<=<img src=\"/icons/folder\\.gif\" alt=\"\\[DIR\\]\"> <a href=\").*(?=\">)";
        //imge PatternStr
        String imgPatternStr="(?<=<img src=\"/icons/image2\\.gif\" alt=\"\\[IMG\\]\"> <a href=\").*(?=\">)";
        //Unkown file Pattern str
        String dataPatternStr="(?<=<img src=\"/icons/unknown\\.gif\" alt=\"\\[   \\]\"> <a href=\").*(?=\">)";

        Pattern dirPattern=Pattern.compile(dirPatternStr);
        Pattern imagePattern=Pattern.compile(imgPatternStr);
        Pattern dataPattern=Pattern.compile(dataPatternStr);

        //buffer writer for writing url into staion file
        BufferedWriter bufferedWriter=null;
        try {
            bufferedWriter =new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
            this.getAllModisFileUrl(ftpUrl, dirPattern, imagePattern, dataPattern,filterPattern,bufferedWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (bufferedWriter!=null)
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return ;
    }
    private void getAllModisFileUrl(String url,Pattern dirPattern,Pattern imagePattern,Pattern dataPattern,String filterPattern,BufferedWriter bufferedWriter){
        //get the html str of each String
        List<String> htmlStrs= HttpRequestAndPost.getHtmlRows(url);
        for (String htmlStr:htmlStrs){
            Matcher dirMatcher=dirPattern.matcher(htmlStr);
            Matcher dataMatcher=dataPattern.matcher(htmlStr);
            Matcher imageMatcher=imagePattern.matcher(htmlStr);
            if (dirMatcher.find()){
                String newUrl=url+dirMatcher.group();
                getAllModisFileUrl(newUrl,dirPattern,imagePattern,dataPattern,filterPattern,bufferedWriter);
            }
            if (dataMatcher.find()){
                String dataFileUrl=url+dataMatcher.group();
                //fileUrls.add(dataFileUrl);
                //test is matcher filterPattern or not if true, write url to file
                //else do nothing
                boolean isMatch=Pattern.matches(filterPattern,dataFileUrl);
                if (isMatch)
                try {
                    bufferedWriter.write(dataFileUrl+"\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (imageMatcher.find()){
                String pngFileUrl=url+imageMatcher.group();
                //fileUrls.add(pngFileUrl);
                boolean isMatch=Pattern.matches(filterPattern,pngFileUrl);
                if (isMatch)
                    try {
                        bufferedWriter.write(pngFileUrl+"\r\n");
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
