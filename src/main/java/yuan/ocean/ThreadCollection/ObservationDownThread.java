package yuan.ocean.ThreadCollection;

import java.util.concurrent.Semaphore;

/**
 * Created by Yuan on 2017/4/17.
 */
public class ObservationDownThread implements Runnable {
    String urlFilePath;
    String url;
    String filterPattern;
    String userName;
    String passWord;
    String subFilePath;
    String fileName;
    String downloadClassName;
    DownloadInsertStorage downloadInsertStorage;
    String platCode;
    private Semaphore semaphore=null;
    public ObservationDownThread(String url,String urlFilePath,String filterPattern,String userName,String passWord,String subFilePath,String fileName,String downloadClassName,DownloadInsertStorage downloadInsertStorage){
        //get the latesttime from database and create
        this.subFilePath=subFilePath;
        this.urlFilePath=urlFilePath;
        this.url=url;
        this.filterPattern=filterPattern;
        this.userName=userName;
        this.passWord=passWord;
        this.fileName=fileName;
        this.downloadClassName=downloadClassName;
        this.downloadInsertStorage=downloadInsertStorage;
        //get the latest time of the station
    }
    public void setSemaphore(Semaphore semaphore){
        this.semaphore=semaphore;
    }
    public void run() {
        //download file
       // System.out.println("SubThread"+semaphore.hashCode());
            try {
                semaphore.acquire();
                downloadInsertStorage.downLoadFile(downloadClassName, url, urlFilePath, userName, passWord, filterPattern, subFilePath, fileName);
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }
}
