package yuan.ocean.ThreadCollection;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import yuan.ocean.Entity.Sensor;
import yuan.ocean.Entity.Station;
import yuan.ocean.SensorConfigInfo;
import yuan.ocean.Util.Decode;
import yuan.ocean.Util.Encode;
import yuan.ocean.Util.HttpRequestAndPost;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by Yuan on 2017/4/17.
 */
public class ObservationInsertThread implements Runnable {
    private static Logger log=Logger.getLogger(ObservationInsertThread.class);
    Sensor station=null;
    String subFilePath;
    String fileDecodeClassName;
    String fileName;
    DownloadInsertStorage downloadInsertStorage;
    Map linkedProperty=null;
    Semaphore semaphore=null;
    public ObservationInsertThread(String stationID,Map linkedProperty,String subFilePath,String fileName,String fileDecodeClassName,DownloadInsertStorage downloadInsertStorage){
        this.downloadInsertStorage=downloadInsertStorage;
        this.linkedProperty=linkedProperty;
        this.fileName=fileName;
        this.subFilePath=subFilePath;
        this.fileDecodeClassName=fileDecodeClassName;
        //get station data structure form sos
        String requestSensorML= Encode.getDescribeSensorXML(stationID);
        String responseSensorML= HttpRequestAndPost.sendPost(SensorConfigInfo.getUrl(), requestSensorML);
        try {
            station= Decode.decodeDescribeImageSensor(responseSensorML);
        } catch (XmlException e) {
            System.out.println("This sensor has problem:"+stationID);
            log.error("This sensor has problem:"+stationID);
            e.printStackTrace();
        }
    }
    public void setSemaphore(Semaphore semaphore){
        this.semaphore=semaphore;
    }
    public void run() {
        try {
            semaphore.acquire();
            downloadInsertStorage.insertObservation(station,subFilePath,fileName,fileDecodeClassName,linkedProperty);
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
