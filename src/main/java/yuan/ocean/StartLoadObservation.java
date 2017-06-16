package yuan.ocean;

import yuan.ocean.InitialTask.InitialAllTask;

/**
 * Created by Yuan on 2017/4/17.
 */
public class StartLoadObservation {
    public static void main(String[] args){
        //read config.properies
        SensorConfigReader.reader();
        //initial the read stationIDs and start creating download operation task(Timer task)
        InitialAllTask.startTask();
    }
}
