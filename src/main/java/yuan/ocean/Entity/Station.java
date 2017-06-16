package yuan.ocean.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yuan on 2017/4/17.
 */
public class Station {
    public String stationID;
    public List<Sensor> sensors=new ArrayList<Sensor>();

    public String getStationID() {
        return stationID;
    }

    public Date lastTime;

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
