package yuan.ocean.InsertObservationService;

import yuan.ocean.Entity.ObservedProperty;
import yuan.ocean.Entity.SOSWrapper;
import yuan.ocean.Entity.Sensor;
import yuan.ocean.Entity.Station;
import yuan.ocean.SensorConfigInfo;
import yuan.ocean.Util.Encode;
import yuan.ocean.Util.HttpRequestAndPost;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yuan on 2017/6/15.
 */
public class ModisImageDecodeFile implements IDecodeFile {
    public void decode(Map<String, String> linkedProperty, String fileName, Sensor station, String subFilePath){
        //if file Exist, read file and form soswrapper
        File file = new File(SensorConfigInfo.getDownloadpath()+"\\"+subFilePath+"\\" +fileName);
        if (file.exists()){
            List<String> files=new ArrayList<String>();
            BufferedReader bufferedReader=null;
            try {
                bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String temp;
                while ((temp=bufferedReader.readLine())!=null){
                    files.add(temp);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (bufferedReader!=null){
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            this.Insert(station,files,linkedProperty);
        }
    }
    public void Insert(Sensor sensor,List<String> files,Map<String,String> propertyPattern){
        //attach data to sensor
        List<String> pngUrl=new ArrayList<String>();
        List<String> dataUrl=new ArrayList<String>();
        //get png and br2 pair
        Pattern pngPattern=Pattern.compile(propertyPattern.get("png"));
        Pattern dataPattern=Pattern.compile(propertyPattern.get("data"));
        for (int i=0;i<files.size();i++){
            Matcher pngMatcher= pngPattern.matcher(files.get(i));
            Matcher dataMatcher=dataPattern.matcher(files.get(i));
            if (pngMatcher.find()){
                pngUrl.add(pngMatcher.group());
            }
            if (dataMatcher.find()){
                dataUrl.add(dataMatcher.group());
            }
        }
        //to add data to sensor
        Pattern pattern=Pattern.compile(propertyPattern.get("time"));
        for (int i=0;i<pngUrl.size();i++){
            //justify if png and data are matched
            if (dataUrl.contains(pngUrl.get(i))) {
                Matcher matcher = pattern.matcher(pngUrl.get(i));
                if (matcher.find()) {
                    String time = matcher.group();
                    String timeStr = this.getTime(time);
                    String polygon = getSpatialPolygon("");
                    if (timeStr != null&&polygon!=null) {
                        SOSWrapper sosWrapper = new SOSWrapper();
                        sosWrapper.setSensorID(sensor.getSensorID());
                        sosWrapper.setSimpleTime(timeStr);
                        for (ObservedProperty observedProperty : sensor.getObservedProperties()) {
                            if (observedProperty.getPropertyID().equals("urn:ogc:def:phenomenon:OGC:1.0.30:WKT")) {
                                observedProperty.setDataValue(polygon);
                            } else {
                                //image url creation
                                String urlStr = pngUrl.get(i);
                                String imageURL = urlStr + ".png|" + "http://cwic.csiss.gmu.edu:9003/CWICMetaHandler/handler?recordId=Idontkown|" + urlStr + ".br2";
                                observedProperty.setDataValue(imageURL);
                            }
                        }
                        sosWrapper.setProperties(sensor.getObservedProperties());
                        String insertXML = Encode.getInsertImageObservationXML(sosWrapper);
                        String responseXML = HttpRequestAndPost.sendPost(SensorConfigInfo.getUrl(), insertXML);
                        System.out.println(responseXML);
                    }
                }
            }
        }
    }

    public String getTime(String timeStr) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date=null;
        String finalTimeStr=null;
        try {
            date= simpleDateFormat.parse(timeStr);
            finalTimeStr= simpleDateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return finalTimeStr;
    }

    public String getSpatialPolygon(String polygon){
        String polygonArea="POLYGON((60 85,60 -155,-60 -155,-60 85,60 85))#4326";
        return polygonArea;
    }
}
