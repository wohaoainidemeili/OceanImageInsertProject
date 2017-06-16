package yuan.ocean;

import java.util.Properties;

/**
 * Created by Yuan on 2016/5/10.
 */
public class SensorConfigInfo {
    String URL="sos_url";

    String DOWNLOADCACHEPATH="file_cache_path";
    static String url;
    static String downloadpath;

    public SensorConfigInfo(Properties properties){
        setUrl(properties.getProperty(URL));
        setDownloadpath(properties.getProperty(DOWNLOADCACHEPATH));
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        SensorConfigInfo.url = url;
    }

    public static String getDownloadpath() {
        return downloadpath;
    }

    public static void setDownloadpath(String downloadpath) {
        SensorConfigInfo.downloadpath = downloadpath;
    }
}
