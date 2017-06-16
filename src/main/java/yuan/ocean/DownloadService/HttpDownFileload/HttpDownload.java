package yuan.ocean.DownloadService.HttpDownFileload;

import yuan.ocean.DownloadService.IDownloadService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yuan on 2017/4/16.
 */
public class HttpDownload implements IDownloadService {
    public void downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+ File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
        conn.disconnect();


        System.out.println("info:"+url+" download success");

    }



    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public void download(String url, String userName, String passWord,String urlParentFile,String filterPattern, String fileName, String saveFilePath) {
        try {
            downLoadFromUrl(url,fileName,saveFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        String url="http://osmc.noaa.gov/erddap/tabledap/OSMC_30day.csv?platform_code,time,latitude,longitude,observation_depth,ztmp,zsal&platform_code=%222902503%22&time%3E=now-30days&orderBy(%22time,observation_depth%22)";
//        try{
//            downLoadFromUrl(url,
//                    "resutl.csv","d:");
//        }catch (Exception e) {
//            // TODO: handle exception
//        }
//    }

}
