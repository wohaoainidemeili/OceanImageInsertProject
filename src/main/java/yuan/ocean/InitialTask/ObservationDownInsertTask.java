package yuan.ocean.InitialTask;

import yuan.ocean.ThreadCollection.DownloadInsertStorage;
import yuan.ocean.ThreadCollection.ObservationDownThread;
import yuan.ocean.ThreadCollection.ObservationInsertThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by Yuan on 2017/4/17.
 */
public class ObservationDownInsertTask extends Thread {
    private Map<String,String> stationIDURL=new HashMap<String,String>();
    private Map<String,String> stationIDFileNames=new HashMap<String, String>();
    private String url;
    private String parentUrlPath;
    private String userName;
    private String passWord;
    private  String downloadClassName;
    private String decodeFileClassName;//the class for ObservationInsertThread to decode download file
    private String subFilePath;//download path for subpath
    private Map<String,String> linkedProperty= new HashMap<String, String>();

    private ExecutorService downloadExecutorService= Executors.newSingleThreadExecutor();
    private ExecutorService insertExecutorService=Executors.newFixedThreadPool(10);
    DownloadInsertStorage downloadInsertStorage=null;

    //using the semaphore to control the executor count,must static, due to different ObservationDownloadInsertTask create different semaphore
    private final static Semaphore semaphoreForDownload=new Semaphore(10);
    //using semaphore to avoid too much memory usage
    private final static Semaphore semaphoreForInsert=new Semaphore(5);
    //initial the parameters
    public ObservationDownInsertTask(String url,String parentUrlPath,String userName,String passWord,String stationIDFile,String linkedFile,
                                     String downloadClassName,String decodeFileClassName,String subFilePath){
        this.url=url;
        this.parentUrlPath=parentUrlPath;
        this.userName=userName;
        this.passWord=passWord;
        this.downloadClassName=downloadClassName;
        this.decodeFileClassName=decodeFileClassName;
        this.subFilePath=subFilePath;
        //read IDs
        BufferedReader bufferedReader= null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(stationIDFile),"gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //#the 0th is stationID;1th is the url path of the station;2th is the filename in system
        String tempID=null;
        try {
            while ((tempID=bufferedReader.readLine())!=null){
                String[] eles=tempID.split(",");
                stationIDURL.put(eles[0],eles[1]);
                stationIDFileNames.put(eles[0],eles[2]);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //read linked properties and store it in hashmap
        BufferedReader bufferedReader1=new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(linkedFile)));
        try {
            while ((tempID=bufferedReader1.readLine())!=null){
                String[] eles=tempID.split(",");
                linkedProperty.put(eles[0],eles[1]);
            }
            bufferedReader1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    @Override
    public void run() {
        //download executor
       // System.out.println(semaphore.hashCode());
        downloadInsertStorage=new DownloadInsertStorage(stationIDURL.size());//initial downloadInsertStorage
        for (Map.Entry entry:stationIDURL.entrySet()){
            ObservationDownThread observationDownThread=new ObservationDownThread(url,parentUrlPath,(String)entry.getValue(),userName,passWord,subFilePath,stationIDFileNames.get(entry.getKey()),downloadClassName,downloadInsertStorage);
            observationDownThread.setSemaphore(semaphoreForDownload);
            if (!downloadExecutorService.isShutdown()){
                downloadExecutorService.execute(observationDownThread);
            }
        }
        //insert executor
        for (Map.Entry entry:stationIDFileNames.entrySet()){
            ObservationInsertThread observationInsertThread=new ObservationInsertThread((String)entry.getKey(),linkedProperty,subFilePath,(String)entry.getValue(),decodeFileClassName,downloadInsertStorage);
           observationInsertThread.setSemaphore(semaphoreForInsert);
            if (!insertExecutorService.isShutdown())
                insertExecutorService.execute(observationInsertThread);
        }
    }
}
