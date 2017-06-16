package yuan.ocean;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Yuan on 2016/5/10.
 */
public class SensorConfigReader {
    public static  void  reader() {
        Properties prop = new Properties();//new Property  case
        try {
            //get Config.properties from the resources
            InputStreamReader in = new InputStreamReader(ClassLoader.getSystemResourceAsStream("config.properties"),"GB2312");
            prop.load(in);//load the input stream of the property file
            SensorConfigInfo sensorConfigInfo = new SensorConfigInfo(prop);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
