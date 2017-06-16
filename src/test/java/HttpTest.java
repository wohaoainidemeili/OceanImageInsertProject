import yuan.ocean.DownloadService.HttpDownFileload.HttpUrlDownload;

/**
 * Created by Yuan on 2017/6/15.
 */
public class HttpTest {
    public static void main(String[] args){
       for (int i=0;i<20;i++){
           TestThread testThread=new TestThread("test"+i+".txt");
           testThread.start();
       }
    }
    static class TestThread extends Thread{
        String fileName;
        public TestThread(String fileName){
            this.fileName=fileName;
        }
        public void run() {
            HttpUrlDownload httpUrlDownload=new HttpUrlDownload();
            httpUrlDownload.download("https://e4ftl01.cr.usgs.gov/MOLA/MYD09A1.005/","","","",".*\\/MOLA\\/.*",fileName,"E:\\test");
        }
    }
}
