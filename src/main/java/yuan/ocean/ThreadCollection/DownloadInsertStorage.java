package yuan.ocean.ThreadCollection;

import org.apache.log4j.Logger;
import yuan.ocean.Entity.Sensor;
import yuan.ocean.Entity.Station;
import yuan.ocean.SensorConfigInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Yuan on 2017/4/18.
 */
public class DownloadInsertStorage {
    private static final Logger log=Logger.getLogger(DownloadInsertStorage.class);
    private int attemptDownLoadFileCount;
    private volatile AtomicInteger currentDownLoadFileCount=new AtomicInteger(0);
    public DownloadInsertStorage(int attemptDownLoadFileCount){
        this.attemptDownLoadFileCount=attemptDownLoadFileCount;
    }
    public void downLoadFile(String downloadClassName,String hostUrl,String parentFilePathOrProperty,String userName,String passWord,String filterPattern,String subFilePath,String filename){

        try {
            Class downlaodClass= Class.forName(downloadClassName);
            Object downloadObject=downlaodClass.newInstance();
            Method downloadMethod=downlaodClass.getMethod("download", String.class, String.class, String.class, String.class, String.class, String.class,String.class);
            //save file path;
            String saveFilePath=SensorConfigInfo.getDownloadpath()+"\\"+subFilePath;
            downloadMethod.invoke(downloadObject,hostUrl,userName,passWord,parentFilePathOrProperty,filterPattern,filename,saveFilePath);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //finished download
        synchronized(currentDownLoadFileCount) {
            int count= currentDownLoadFileCount.getAndAdd(1);
            currentDownLoadFileCount.notifyAll();
        }
    }
    public  void insertObservation(Sensor station,String subFilePath,String fileName,String fileDecodeClassName,Map<String,Integer> linkedProperty) {
        log.info("Start to insert Observation");
        synchronized (currentDownLoadFileCount) {
            while (currentDownLoadFileCount.get() != attemptDownLoadFileCount) {
                try {
                    System.out.println(Thread.currentThread().getName() + "is waiting...");
                    currentDownLoadFileCount.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // System.out.println(station.getStationID());

            // decode file
            //decode file using different method
            try {
                Class<?> fileDecodeClass = Class.forName(fileDecodeClassName);
                Object object = fileDecodeClass.newInstance();
                Method method = fileDecodeClass.getMethod("decode", Map.class, String.class, Station.class, String.class);
                method.invoke(object, linkedProperty, fileName, station, subFilePath);
            } catch (ClassNotFoundException e) {
                log.error("can not load class" + fileDecodeClassName);
            } catch (InstantiationException e) {
                log.error("can not instance class" + fileDecodeClassName);
            } catch (IllegalAccessException e) {
                log.error("illegal access class" + fileDecodeClassName);
            } catch (NoSuchMethodException e) {
                log.error("there is no decode method, please check your class" + fileDecodeClassName);
            } catch (InvocationTargetException e) {
                log.error("can not invoke to the method");
            }
//            IDecodeFile decodeFile=new ERDDAPDecodeFile();
//            decodeFile.decode(linkedProperty,paltCode,station,subFilePath);
        }
    }

}
