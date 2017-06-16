package yuan.ocean.Util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * use http to post information and get SOS service
 * Created by Yuan on 2016/4/20.
 */
public class HttpRequestAndPost {
    /**
     * use http to get information
     * @param url the sos url
     * @param param the post xml
     * @return the return result xml
     */
    public static String sendPost(String url,String param){
        StringBuffer result=new StringBuffer();
        BufferedReader in=null;
        PrintWriter out=null;
        //http post
        try {
            URL realURL=new URL(url);//new URL
            URLConnection connection=realURL.openConnection();
            connection.setRequestProperty("accept","*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0(compatible;MSIE 6.0;Windows NT 5.1;SV1)");

            connection.setDoOutput(true);
            connection.setDoInput(true);

            //write xml into post stream
            out=new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"gb2312"));
            out.print(param);
            out.flush();//flush the operation

            //use bufferreader to read the returned result
            in=new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line;
            while ((line=in.readLine())!=null){
                result.append(line);
            }
            String encode=connection.getContentEncoding();
            String contentType=connection.getContentType();
            return result.toString();
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }finally {
            if (in!=null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (out!=null)
                out.close();
        }
        return  result.toString();
    }
    public static List<String> getHtmlRows(String ftpUrl) {

        List<String> htmlList=new ArrayList<String>();
        try{
            URL url=new URL(ftpUrl);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //设置超时间为3秒
            conn.setConnectTimeout(3*100000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String temp;
            while ((temp=bufferedReader.readLine())!=null){
                htmlList.add(temp);
            }
            inputStream.close();
            bufferedReader.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlList;
    }
}
