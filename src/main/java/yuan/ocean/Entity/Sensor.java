package yuan.ocean.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuan on 2017/1/15.
 */
public class Sensor {
    private String sensorID;
    private String sensorName;
    private double lat;
    private double lon;
    private int srsid;
    private List<ObservedProperty> observedProperties=new ArrayList<ObservedProperty>();

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getSrsid() {
        return srsid;
    }

    public void setSrsid(int srsid) {
        this.srsid = srsid;
    }

    public List<ObservedProperty> getObservedProperties() {
        return observedProperties;
    }

    public void setObservedProperties(List<ObservedProperty> observedProperties) {
        this.observedProperties = observedProperties;
    }
}
