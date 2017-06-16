package yuan.ocean.InitialTask;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yuan on 2017/4/18.
 */
public class InitialAllTask {
    private static final Logger log=Logger.getLogger(InitialAllTask.class);
    private static ScheduledExecutorService executorService= Executors.newSingleThreadScheduledExecutor();
    public static void startTask(){
        //the file has 6 columns
        //1.the url of the observation 2.the root url of your http or ftp url(normally it is ftp prefix root url,http is null)
        // 3.username for ftp(if it is http,it is null) 4.password for ftp(if it is http,it is null)
        // 5.the stationID config file for filtering or downloading station observation
        // 6.the property config file for bonding data dynamically to sensor propertyID
        //7.the class name for downloading data
        //8.the class name for decoding file and inserting data into SOS
        //9.subfile name for storing download data to avoid the same station name for different sensors
        //###//10.not used now, also can be used for differing execute model,execute once or at rate of n seconds or days.
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("observationdownloadconfig.txt")));
        String tempStr;
        try {
            while ((tempStr=bufferedReader.readLine())!=null){
               String[] eles=tempStr.split("#");
                log.info("start to load ObservationDownInsertTask for"+eles[0]);
                ObservationDownInsertTask downInsertTask=new ObservationDownInsertTask(eles[0],eles[1],eles[2],eles[3],eles[4],eles[5],eles[6],eles[7],eles[8]);
                executorService.scheduleWithFixedDelay(downInsertTask,1, 30 * 24 * 3600, TimeUnit.SECONDS);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
