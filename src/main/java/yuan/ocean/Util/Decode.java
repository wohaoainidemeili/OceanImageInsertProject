package yuan.ocean.Util;

import net.opengis.gml.ReferenceType;
import net.opengis.sensorML.x101.*;
import net.opengis.sos.x10.CapabilitiesDocument;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.VectorType;
import org.apache.xmlbeans.XmlException;
import yuan.ocean.Entity.ObservedProperty;
import yuan.ocean.Entity.Sensor;
import yuan.ocean.Entity.Station;
import yuan.ocean.SensorConfigInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuan on 2017/1/15.
 */
public class Decode {
    public static Station parseSensorML(String StationID) throws IOException, XmlException {
        Station spoutParams=new Station();
        spoutParams.stationID=StationID;
        String dsStationXML= Encode.getDescribeSensorXML(StationID);
        String url= SensorConfigInfo.getUrl();
        String stationXML= HttpRequestAndPost.sendPost(url, dsStationXML);

        //parse the station sensorML to get the sensor name under the station
        SensorMLDocument parseSationDocument=SensorMLDocument.Factory.parse(stationXML);
        SensorMLDocument.SensorML sensorML1=parseSationDocument.getSensorML();
        AbstractProcessType xb_prc= parseSationDocument.getSensorML().getMemberArray(0).getProcess();
        SystemType staSystem=(SystemType)xb_prc;

        //get component list for getting sensorID to get and parse sensors in this station
        ComponentsDocument.Components.ComponentList.Component[] components= staSystem.getComponents().getComponentList().getComponentArray();
        for (ComponentsDocument.Components.ComponentList.Component component:components){
            //initial sensorParams for obtaining the parameters of the sensor defined by the sensor makeup language
            Sensor sensorParams=new Sensor();
            String sensorID=component.getHref();
            sensorParams.setSensorID(sensorID);
            String dsSensorXML= Encode.getDescribeSensorXML(sensorID);
            String sensorXML= HttpRequestAndPost.sendPost(url, dsSensorXML);//get the sensorML by sensorID
            //parse sensor makeup language
            SensorMLDocument parseSensorDocument=SensorMLDocument.Factory.parse(sensorXML);
            SystemType senSystem=(SystemType)parseSensorDocument.getSensorML().getMemberArray(0).getProcess();

            //get sensor position/ latitude and longtitude
            PositionType positionType= senSystem.getPosition().getPosition();

            //get spatial reference ID
            String srsName= positionType.getReferenceFrame();
            String srsNamePrefix="urn:ogc:def:crs:EPSG:";
            int srid=Integer.valueOf(srsName.replace(srsNamePrefix,"")).intValue();
            sensorParams.setSrsid(srid);

            //get latitude and longtitude
            VectorType.Coordinate[] coordinates= positionType.getLocation().getVector().getCoordinateArray();
            for (VectorType.Coordinate coordinate:coordinates){
                String axisID=coordinate.getQuantity().getAxisID();
                if (axisID.equalsIgnoreCase("x")) sensorParams.setLon(coordinate.getQuantity().getValue());
                else if (axisID.equalsIgnoreCase("y")) sensorParams.setLat(coordinate.getQuantity().getValue());
            }

            //get the sensor property and every property is set by quantity class
            IoComponentPropertyType[] properties =senSystem.getOutputs().getOutputList().getOutputArray();
            for (IoComponentPropertyType property:properties){
                ObservedProperty obsProperty=new ObservedProperty();
                //get the property name
                String propertyName= property.getName();
                String propertyID=null;
                //get the type of obs
                String typeOfProperty=null;
                //if the property value is type of double\
                if (property.isSetQuantity()){
                    typeOfProperty="Quantity";
                    propertyID=property.getQuantity().getDefinition();
                    String propertyUnit=property.getQuantity().getUom().getCode();
                    obsProperty.setUnit(propertyUnit);
                }
                //if the property value is type of string
                else if (property.isSetText()){
                    typeOfProperty="Text";
                    propertyID=property.getText().getDefinition();
                }
                //if the property value is type of wkt like pooint(0,0)
                else if (property.isSetAbstractDataRecord()){
                    typeOfProperty="Position";
                    propertyID=property.getAbstractDataRecord().getDefinition();
                }
                //get property unit
                obsProperty.setPropertyID(propertyID);
                obsProperty.setPropertyName(propertyName);
                obsProperty.setTypeOfProperty(typeOfProperty);
                sensorParams.getObservedProperties().add(obsProperty);
            }
            spoutParams.sensors.add(sensorParams);
        }

        return spoutParams;
    }

    /**
     * get sensorID by analysis capability xml
     * @param capabilityXML
     * @return sensorIDs
     * @throws Exception
     */
    public static List<String> decodeCapability(String capabilityXML) throws Exception {
        List<String> sensorIDs=new ArrayList<String>();
        try {
            CapabilitiesDocument capabilitiesDocument=CapabilitiesDocument.Factory.parse(capabilityXML);
            CapabilitiesDocument.Capabilities capabilities= capabilitiesDocument.getCapabilities();
            ReferenceType[] procedures= capabilities.getContents().getObservationOfferingList().getObservationOfferingArray(0).getProcedureArray();
            for (int i=0;i<procedures.length;i++){
               String sensorID= procedures[i].getHref();
                sensorIDs.add(sensorID);
            }
        }
        catch (Exception e){
           throw new Exception("��������");
        }

        return sensorIDs;
    }

    /**
     * decode describesensor xml to get sensor
     * @param sensorXML
     * @return
     * @throws XmlException
     */
    public static Sensor decodeDescribeSensor(String sensorXML) throws XmlException {
        Sensor sensor=new Sensor();
        //parse the station sensorML to get the sensor name under the station
        SensorMLDocument parseSationDocument=SensorMLDocument.Factory.parse(sensorXML);
        SensorMLDocument.SensorML sensorML1=parseSationDocument.getSensorML();
        AbstractProcessType xb_prc=parseSationDocument.getSensorML().getMemberArray(0).getProcess();
        SystemType staSystem=(SystemType)xb_prc;

        //get sensor id and sensor name
        IdentificationDocument.Identification.IdentifierList.Identifier[] identifiers= staSystem.getIdentificationArray(0).getIdentifierList().getIdentifierArray();
        for (int i=0;i<identifiers.length;i++){
            if (identifiers[i].getTerm().getDefinition().equalsIgnoreCase("urn:ogc:def:identifier:OGC:1.0:uniqueID")
                    ||identifiers[i].getTerm().getDefinition().equalsIgnoreCase("urn:ogc:def:identifier:OGC:uniqueID")){
                sensor.setSensorID(identifiers[i].getTerm().getValue());
            }else if (identifiers[i].getTerm().getDefinition().equalsIgnoreCase("urn:ogc:def:identifier:OGC:1.0:longName")){
                sensor.setSensorName(identifiers[i].getTerm().getValue());
            }
        }

        //get lat and lon
        if (staSystem.getPosition()!=null) {
            PositionType positionType = staSystem.getPosition().getPosition();
            //get latitude and longtitude
            VectorType.Coordinate[] coordinates = positionType.getLocation().getVector().getCoordinateArray();
            for (VectorType.Coordinate coordinate : coordinates) {
                String axisID = coordinate.getQuantity().getAxisID();
                if (axisID.equalsIgnoreCase("x")) sensor.setLon(coordinate.getQuantity().getValue());
                else if (axisID.equalsIgnoreCase("y")) sensor.setLat(coordinate.getQuantity().getValue());
            }
        }

        //get ObservationProperties
        //get the sensor property and every property is set by quantity class

        IoComponentPropertyType[] properties =staSystem.getOutputs().getOutputList().getOutputArray();
        List<ObservedProperty> observedProperties=new ArrayList<ObservedProperty>();
        for (IoComponentPropertyType property:properties){
            ObservedProperty obsProperty=new ObservedProperty();
            //get the property name
            String propertyName= property.getName();
            //get the property ID
            String propertyID=property.getQuantity().getDefinition();
            //get property unit
            String propertyUnit=property.getQuantity().getUom().getCode();
            obsProperty.setPropertyID(propertyID);
            obsProperty.setPropertyName(propertyName);
            obsProperty.setUnit(propertyUnit);
            observedProperties.add(obsProperty);
            sensor.setObservedProperties(observedProperties);
        }

        return sensor;
    }
    /**
     * decode describesensor xml to get sensor
     * @param sensorXML
     * @return
     * @throws XmlException
     */
    public static Sensor decodeDescribeImageSensor(String sensorXML) throws XmlException {
        Sensor sensor=new Sensor();
        //parse the station sensorML to get the sensor name under the station
        SensorMLDocument parseSationDocument=SensorMLDocument.Factory.parse(sensorXML);
        SensorMLDocument.SensorML sensorML1=parseSationDocument.getSensorML();
        AbstractProcessType xb_prc=parseSationDocument.getSensorML().getMemberArray(0).getProcess();
        SystemType staSystem=(SystemType)xb_prc;

        //get sensor id and sensor name
        IdentificationDocument.Identification.IdentifierList.Identifier[] identifiers= staSystem.getIdentificationArray(0).getIdentifierList().getIdentifierArray();
        for (int i=0;i<identifiers.length;i++){
            if (identifiers[i].getTerm().getDefinition().equalsIgnoreCase("urn:ogc:def:identifier:OGC:1.0:uniqueID")
                    ||identifiers[i].getTerm().getDefinition().equalsIgnoreCase("urn:ogc:def:identifier:OGC:uniqueID")){
                sensor.setSensorID(identifiers[i].getTerm().getValue());
            }else if (identifiers[i].getTerm().getDefinition().equalsIgnoreCase("urn:ogc:def:identifier:OGC:1.0:longName")){
                sensor.setSensorName(identifiers[i].getTerm().getValue());
            }
        }

        //get lat and lon
        if (staSystem.getPosition()!=null) {
            PositionType positionType = staSystem.getPosition().getPosition();
            //get latitude and longtitude
            VectorType.Coordinate[] coordinates = positionType.getLocation().getVector().getCoordinateArray();
            for (VectorType.Coordinate coordinate : coordinates) {
                String axisID = coordinate.getQuantity().getAxisID();
                if (axisID.equalsIgnoreCase("x")) sensor.setLon(coordinate.getQuantity().getValue());
                else if (axisID.equalsIgnoreCase("y")) sensor.setLat(coordinate.getQuantity().getValue());
            }
        }

        //get ObservationProperties
        //get the sensor property and every property is set by quantity class

        IoComponentPropertyType[] properties =staSystem.getOutputs().getOutputList().getOutputArray();
        List<ObservedProperty> observedProperties=new ArrayList<ObservedProperty>();
        for (IoComponentPropertyType property:properties){
            ObservedProperty obsProperty=new ObservedProperty();
            //get the property name
            String propertyName= property.getName();
            //get the property ID
            String propertyID=property.getText().getDefinition();
            obsProperty.setPropertyID(propertyID);
            obsProperty.setPropertyName(propertyName);
            observedProperties.add(obsProperty);
            sensor.setObservedProperties(observedProperties);
        }

        return sensor;
    }


}
