package yuan.ocean.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * the sensor information for insert into database by sos
 * Created by Yuan on 2016/5/10.
 */
public class SOSWrapper implements Serializable {
    //the sensorID
    String sensorID;
    //the properties of the sensor
    public List<ObservedProperty> properties=new ArrayList<ObservedProperty>();
    String simpleTime;//the observation time of the observation
    int srid;//the spatial reference ID ,such as epsg:4326 refers to WGS84
    Double lon;//sensor longtitude
    Double lat;//sensor latitude
    String sosAddress;//sos url
    public SOSWrapper(){}
    public SOSWrapper(String sensorID, String simpleTime, Double lon, Double lat, String sosAddress, List<ObservedProperty> properties){
        this.sensorID=sensorID;
        this.simpleTime=simpleTime;
        this.lon=lon;
        this.lat=lat;
        this.sosAddress=sosAddress;
        this.properties=properties;
    }

    public int getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public List<ObservedProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<ObservedProperty> properties) {
        this.properties.addAll( properties);
    }

    public String getSimpleTime() {
        return simpleTime;
    }

    public void setSimpleTime(String simpleTime) {
        this.simpleTime = simpleTime;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getSosAddress() {
        return sosAddress;
    }

    public void setSosAddress(String sosAddress) {
        this.sosAddress = sosAddress;
    }

}
