package yuan.ocean.InsertObservationService;

import yuan.ocean.Entity.Sensor;
import yuan.ocean.Entity.Station;

import java.util.Map;

/**
 * Created by Yuan on 2017/4/20.
 */
public interface IDecodeFile {
    public void decode(Map<String, String> linkedProperty, String fileName, Sensor station, String subFilePath);
}
