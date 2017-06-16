package yuan.ocean.Util;

import net.opengis.gml.*;
import net.opengis.gml.TimePositionType;
import net.opengis.om.x10.ObservationType;
import net.opengis.om.x10.ProcessPropertyType;
import net.opengis.ows.x11.AcceptVersionsType;
import net.opengis.ows.x11.SectionsType;
import net.opengis.ows.x11.VersionType;
import net.opengis.sampling.x10.SamplingPointDocument;
import net.opengis.sampling.x10.SamplingPointType;
import net.opengis.sampling.x10.SamplingSurfaceDocument;
import net.opengis.sampling.x10.SamplingSurfaceType;
import net.opengis.sos.x10.DescribeSensorDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetObservationDocument;
import net.opengis.sos.x10.InsertObservationDocument;
import net.opengis.swe.x101.*;
import net.opengis.swe.x101.PositionDocument;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.w3c.dom.Node;
import yuan.ocean.Entity.ObservedProperty;
import yuan.ocean.Entity.SOSWrapper;

import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuan on 2017/1/15.
 */
public class Encode {
    final static String sosUrl="http://www.opengis.net/sos/1.0";
    final static String ogc="http://www.opengis.net/ogc";
    final static String gml="http://www.opengis.net/gml";
    /**
     * create capability request xml
     * @return
     */
    public static String getCapability(){
        String capablityXML=null;
        GetCapabilitiesDocument capabilitiesDocument=GetCapabilitiesDocument.Factory.newInstance();
        GetCapabilitiesDocument.GetCapabilities capabilities= capabilitiesDocument.addNewGetCapabilities();
        capabilities.setService("SOS");
        AcceptVersionsType acceptVersionsType= capabilities.addNewAcceptVersions();
        VersionType versionType= acceptVersionsType.addNewVersion();
        versionType.setStringValue("1.0.0");

        SectionsType sectionsType= capabilities.addNewSections();
        XmlString xmlString= sectionsType.addNewSection();
        xmlString.setStringValue("Contents");
        XmlOptions options=new XmlOptions();
        Map<String,String> nameSpace=new HashMap<String,String>();
        nameSpace.put("http://www.opengis.net/sos/1.0","");
        nameSpace.put("http://www.opengis.net/ows/1.1","ows");
        nameSpace.put("http://www.opengis.net/ogc","ogc");
        nameSpace.put("http://www.w3.org/2001/XMLSchema-instance","xsi");
        options.setSaveSuggestedPrefixes(nameSpace);
        options.setSaveAggressiveNamespaces();
        options.setSavePrettyPrint();
        capablityXML=capabilitiesDocument.xmlText(options);
        return capablityXML;
    }
    /**
     * describe sensor xml creatation
     * @param sensorID
     * @return
     */
    public static String getDescribeSensorXML(String sensorID){
        //load station xml by StationID using DescribeSensor Operation
        DescribeSensorDocument dsDocument=DescribeSensorDocument.Factory.newInstance();
        DescribeSensorDocument.DescribeSensor describeSensor= dsDocument.addNewDescribeSensor();
        describeSensor.setVersion("1.0.0");
        describeSensor.setService("SOS");
//        File file=new File("DescribeSensor.xml");
//        DescribeSensorDocument describeSensorDocument=DescribeSensorDocument.Factory.parse(file);
//        DescribeSensorDocument.DescribeSensor describeSensor1=describeSensorDocument.getDescribeSensor();
//        String serivce=describeSensor1.getService();
//        String output=describeSensor1.getOutputFormat();

        //add outputFormat or it will be error
        describeSensor.setOutputFormat("text/xml;subtype=\"sensorML/1.0.1\"");
        describeSensor.setProcedure(sensorID);

        //output xml
        XmlOptions options=new XmlOptions();
        options.setSavePrettyPrint();
        String dsXML=dsDocument.xmlText(options);//get describesensor xml
        return dsXML;
    }

    /**
     * get observation information when sensorID and propertyID is given
     * @param sensorID the sensorID for get observation information
     * @param propertyID the propertyID for get observation information
     * @return the GetObservationXML
     */
    public static String getGetObservationXML(String sensorID,String propertyID){
        String getObservationXML=null;
        GetObservationDocument getObservationDocument=GetObservationDocument.Factory.newInstance();
        //get the data in past time
        GetObservationDocument.GetObservation observation= getObservationDocument.addNewGetObservation();
        observation.setService("SOS");
        observation.setVersion("1.0.0");
        XmlCursor cursor=observation.newCursor();
        cursor.toFirstContentToken();
        cursor.insertElementWithText(new QName(sosUrl, "offering"), "LIESMARS");
        cursor.toEndToken();
        cursor.insertElementWithText(new QName(sosUrl, "procedure"), sensorID);
        cursor.toEndToken();
        cursor.insertElementWithText(new QName(sosUrl, "observedProperty"), propertyID);
        cursor.toEndToken();
        cursor.insertElementWithText(new QName(sosUrl,"responseFormat"),"text/xml;subtype=\"om/1.0.0\"");
        cursor.dispose();;
        getObservationXML=getObservationDocument.xmlText();
        return getObservationXML;
    }

    public static String getGetLatestObservationXML(String sensorID,String propertyID){
        String getObservationXML=null;
        GetObservationDocument getObservationDocument=GetObservationDocument.Factory.newInstance();
        //get the data in past time
        GetObservationDocument.GetObservation observation= getObservationDocument.addNewGetObservation();
        observation.setService("SOS");
        observation.setVersion("1.0.0");
        XmlCursor cursor=observation.newCursor();
        cursor.toFirstContentToken();
        cursor.insertElementWithText(new QName(sosUrl, "offering"), "LIESMARS");
        cursor.toEndToken();
        cursor.addToSelection();
        cursor.beginElement(new QName(sosUrl,"eventTime"));
        cursor.beginElement(new QName(ogc,"TM_Equals"));
        cursor.insertElementWithText(new QName(ogc,"PropertyName"),"om:samplingTime");
        cursor.toEndToken();
        cursor.beginElement(new QName(gml,"TimeInstant"));
        cursor.insertElementWithText(new QName(gml,"timePosition"),"latest");
        cursor.toSelection(0);
        cursor.toEndToken();
        cursor.insertElementWithText(new QName(sosUrl, "procedure"), sensorID);
        cursor.toEndToken();
        cursor.insertElementWithText(new QName(sosUrl, "observedProperty"), propertyID);
        cursor.toEndToken();
        cursor.insertElementWithText(new QName(sosUrl,"responseFormat"),"text/xml;subtype=\"om/1.0.0\"");
        cursor.dispose();;
        getObservationXML=getObservationDocument.xmlText();
        return getObservationXML;
    }

    public static String getGetObservationXMLByIDAndTime(String sensorID,String propertyID,String startTime,String endTime){
        String getObservationXML=null;
        GetObservationDocument getObservationDocument=GetObservationDocument.Factory.newInstance();
        //get the data in past time
        GetObservationDocument.GetObservation observation= getObservationDocument.addNewGetObservation();
        observation.setService("SOS");
        observation.setVersion("1.0.0");
        XmlCursor cursor=observation.newCursor();
        cursor.toFirstContentToken();
        cursor.insertElementWithText(new QName(sosUrl, "offering"), "LIESMARS");
        cursor.toEndToken();
        cursor.addToSelection();
        cursor.beginElement(new QName(sosUrl,"eventTime"));
        cursor.beginElement(new QName(ogc,"TM_Equals"));
        cursor.insertElementWithText(new QName(ogc,"PropertyName"),"om:samplingTime");
        cursor.toEndToken();
        cursor.beginElement(new QName(gml,"TimePeriod"));
        cursor.insertElementWithText(new QName(gml,"beginPosition"),startTime);
        cursor.insertElementWithText(new QName(gml,"endPosition"),endTime);
        cursor.toSelection(0);
        cursor.toEndToken();
        cursor.insertElementWithText(new QName(sosUrl, "procedure"), sensorID);
        cursor.toEndToken();
        cursor.insertElementWithText(new QName(sosUrl, "observedProperty"), propertyID);
        cursor.toEndToken();
        cursor.insertElementWithText(new QName(sosUrl,"responseFormat"),"text/xml;subtype=\"om/1.0.0\"");
        cursor.dispose();;
        getObservationXML=getObservationDocument.xmlText();
        return getObservationXML;
    }
    public static String getInserObservationXML(SOSWrapper sosWrapper){
        String insertSensorML="";
        //create insertObservation xml
        //new an insertobservationdocument for creating xml
        InsertObservationDocument insertObservationDocument=InsertObservationDocument.Factory.newInstance();
        InsertObservationDocument.InsertObservation insertObservation =insertObservationDocument.addNewInsertObservation();
        insertObservation.setVersion("1.0.0");
        insertObservation.setService("SOS");

        insertObservation.setAssignedSensorId(sosWrapper.getSensorID());
        ObservationType observationType= insertObservation.addNewObservation();

        //create time info xml
        //create timeInstance
        TimeObjectPropertyType timeSample=observationType.addNewSamplingTime();
        //create timeinstance
        TimeInstantDocument timeInstantDocument=TimeInstantDocument.Factory.newInstance();
        TimeInstantType timeInstantType= timeInstantDocument.addNewTimeInstant();
        //create timepostion
        TimePositionType timePositionType=TimePositionType.Factory.newInstance();
        timePositionType.set(sosWrapper.getSimpleTime());
        timeInstantType.setTimePosition(timePositionType);

        timeSample.set(timeInstantDocument);
        //create procedure
        ProcessPropertyType procedureType= observationType.addNewProcedure();
        procedureType.setHref(sosWrapper.getSensorID());

        //create observationProperty
        PhenomenonPropertyType phenomenonPropertyType= observationType.addNewObservedProperty();
        //create compositePhenomenon
        CompositePhenomenonDocument compositePhenomenonDocument=CompositePhenomenonDocument.Factory.newInstance();
        CompositePhenomenonType compositePhenomenonType=compositePhenomenonDocument.addNewCompositePhenomenon();
        compositePhenomenonType.setId("cpid0");
        //compositePhenomenonType.setDimension(BigInteger.ONE);
        CodeType name= compositePhenomenonType.addNewName();
        name.set("resultComponents");
        //add gregorian time property
        PhenomenonPropertyType timeComponent= compositePhenomenonType.addNewComponent();
        timeComponent.setHref("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");
        //add the property defined in SOSWrapper
        for (ObservedProperty property:sosWrapper.properties){
            PhenomenonPropertyType propertyComponent= compositePhenomenonType.addNewComponent();
            propertyComponent.setHref(property.getPropertyID());
        }
        compositePhenomenonType.setDimension(BigInteger.valueOf(1+sosWrapper.properties.size()));
        phenomenonPropertyType.set(compositePhenomenonDocument);

        //create feauture of Interest by lat and lon
        String latlonStr=sosWrapper.getLat()+" "+sosWrapper.getLon();
        FeaturePropertyType featureOfInterest= observationType.addNewFeatureOfInterest();
        SamplingPointDocument samplingPointDocument= SamplingPointDocument.Factory.newInstance();
        SamplingPointType samplingPointType=samplingPointDocument.addNewSamplingPoint();
        samplingPointType.setId(latlonStr);
        CodeType samplePointName=samplingPointType.addNewName();
        samplePointName.set(latlonStr);
        PointPropertyType position= samplingPointType.addNewPosition();
        PointType pointType=position.addNewPoint();
        pointType.setSrsName("urn:ogc:def:crs:EPSG::" + sosWrapper.getSrid());
        DirectPositionType pos= pointType.addNewPos();
        pos.set(latlonStr);
        featureOfInterest.set(samplingPointDocument);
        //Create Feature of Interest
        //String latlonStr=sosWrapper.getLat()+" "+sosWrapper.getLon();
//        FeaturePropertyType featureOfInterest= observationType.addNewFeatureOfInterest();
//        featureOfInterest.setTitle("eastsea");
//        SamplingSurfaceDocument samplingSurfaceDocument=SamplingSurfaceDocument.Factory.newInstance();
//        SamplingSurfaceType samplingSurfaceType=samplingSurfaceDocument.addNewSamplingSurface();
//        samplingSurfaceType.setId("eastsea");
//        CodeType foiName= samplingSurfaceType.addNewName();
//        foiName.set("donghai");
//        FeaturePropertyType featurePropertyType= samplingSurfaceType.addNewSampledFeature();
//        featurePropertyType.setHref("urn:ogc:def:nil:OGC:unknown");
//        SurfacePropertyType  shape= samplingSurfaceType.addNewShape();
//
//        PolygonDocument polygonDocument=PolygonDocument.Factory.newInstance();
//        PolygonType polygonType=  polygonDocument.addNewPolygon();
//        polygonType.setId("polygon_sf_0");
//        polygonType.setSrsName("urn:ogc:def:crs:EPSG::4326");
//        AbstractRingPropertyType abstractRingPropertyType= polygonType.addNewExterior();
//        LinearRingDocument linearRingDocument=LinearRingDocument.Factory.newInstance();
//        LinearRingType linearRingType= linearRingDocument.addNewLinearRing();
//        DirectPositionListType pointListType= linearRingType.addNewPosList();
////        pointListType.setSrsName("urn:ogc:def:crs:EPSG::4326");
//        //fixed location of donghai range
//        pointListType.set("21.5 117.0 21.5 131.1 33.2 131.1 33.2 117.0 21.5 117.0");
//        abstractRingPropertyType.set(linearRingDocument);
//        shape.set(polygonDocument);
//        featureOfInterest.set(samplingSurfaceDocument);
        
//        SamplingPointDocument samplingPointDocument=SamplingPointDocument.Factory.newInstance();
//        SamplingPointType samplingPointType=samplingPointDocument.addNewSamplingPoint();
//        samplingPointType.setId(latlonStr);
//        CodeType samplePointName=samplingPointType.addNewName();
//        samplePointName.set(latlonStr);
//        PointPropertyType position= samplingPointType.addNewPosition();
//        PointType pointType=position.addNewPoint();
//        pointType.setSrsName("urn:ogc:def:crs:EPSG::" + sosWrapper.getSrid());
//        DirectPositionType pos= pointType.addNewPos();
//        pos.set(latlonStr);
//        featureOfInterest.set(samplingPointDocument);
        //create result
        XmlObject resultType= observationType.addNewResult();

        //create element under data record
        DataArrayDocument dataArrayDocument=DataArrayDocument.Factory.newInstance();
        DataArrayType dataArrayType=dataArrayDocument.addNewDataArray1();
        AbstractDataArrayType.ElementCount elementCount=dataArrayType.addNewElementCount();
        net.opengis.swe.x101.CountDocument.Count count= elementCount.addNewCount();
        count.setValue(BigInteger.valueOf(1));
        DataComponentPropertyType dataComponentPropertyType= dataArrayType.addNewElementType();
        //elementType dataRecordDocument
        DataRecordDocument eleDataRecordDocument=DataRecordDocument.Factory.newInstance();
        DataRecordType propertyDataRecord=eleDataRecordDocument.addNewDataRecord();
        //create time field
        DataComponentPropertyType timeField= propertyDataRecord.addNewField();
        timeField.setName("Time");
        timeField.addNewTime().setDefinition("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");
        //create other property field
        for (ObservedProperty property:sosWrapper.properties){
            DataComponentPropertyType propertyField = propertyDataRecord.addNewField();
            if (property.getTypeOfProperty().equals("Position")) {
                PositionDocument positionDocument=PositionDocument.Factory.newInstance();
                PositionType dataRecordType= positionDocument.addNewPosition();
                dataRecordType.setDefinition(property.getPropertyID());
                propertyField.set(positionDocument);
                propertyField.setName(property.getPropertyName());
            }else if (property.getTypeOfProperty().equals("Quantity")){
                propertyField.setName(property.getPropertyName().trim());
                // the default property is demicial
                net.opengis.swe.x101.QuantityDocument.Quantity quantity = propertyField.addNewQuantity();
                quantity.setDefinition(property.getPropertyID());
                quantity.addNewUom().setCode(property.getUnit());
            }else if (property.getTypeOfProperty().equals("Text")){
                propertyField.setName(property.getPropertyName().trim());
                // the default property is demicia
                TextDocument.Text text= propertyField.addNewText();
                text.setDefinition(property.getPropertyID());
            }
        }
        dataComponentPropertyType.set(eleDataRecordDocument);
        dataComponentPropertyType.setName("Components");
        BlockEncodingPropertyType encodingPropertyType= dataArrayType.addNewEncoding();
        TextBlockDocument.TextBlock textBlock= encodingPropertyType.addNewTextBlock();
        textBlock.setDecimalSeparator(".");
        textBlock.setTokenSeparator(",");
        textBlock.setBlockSeparator(";");

        DataValuePropertyType valuePropertyType=dataArrayType.addNewValues();
        //create observation value string
        String values=sosWrapper.getSimpleTime();
        for (ObservedProperty property: sosWrapper.properties){
            values=values+","+property.getDataValue();
        }

        Node pNode= valuePropertyType.getDomNode();
        Node node= pNode.getOwnerDocument().createTextNode(values+";");
        pNode.appendChild(node);
        //valuePropertyType.getDomNode().getFirstChild().setNodeValue(values + ";");
        resultType.set(dataArrayDocument);


        //print xml as string
        //output xml
        XmlOptions options=new XmlOptions();
        Map<String,String> nameSpace=new HashMap<String,String>();
        nameSpace.put("http://www.opengis.net/sos/1.0","");
        nameSpace.put("http://www.opengis.net/ows/1.1","ows");
        nameSpace.put("http://www.opengis.net/ogc","ogc");
        nameSpace.put("http://www.opengis.net/om/1.0","om");
        nameSpace.put("http://www.opengis.net/sos/1.0","sos");
        nameSpace.put("http://www.opengis.net/sampling/1.0","sa");
        nameSpace.put("http://www.opengis.net/gml","gml");
        nameSpace.put("http://www.opengis.net/swe/1.0.1","swe");
        nameSpace.put("http://www.w3.org/1999/xlink","xlink");
        nameSpace.put("http://www.w3.org/2001/XMLSchema-instance","xsi");
        options.setSaveSuggestedPrefixes(nameSpace);
        options.setSaveAggressiveNamespaces();
        options.setSavePrettyPrint();
        //options.setCharacterEncoding("UTF-8");
        insertSensorML= insertObservationDocument.xmlText(options);//get insertobservation xml
        return insertSensorML;
    }
    public static String getInsertImageObservationXML(SOSWrapper sosWrapper){
        String insertSensorML="";
        //create insertObservation xml
        //new an insertobservationdocument for creating xml
        InsertObservationDocument insertObservationDocument=InsertObservationDocument.Factory.newInstance();
        InsertObservationDocument.InsertObservation insertObservation =insertObservationDocument.addNewInsertObservation();
        insertObservation.setVersion("1.0.0");
        insertObservation.setService("SOS");

        insertObservation.setAssignedSensorId(sosWrapper.getSensorID());
        ObservationType observationType= insertObservation.addNewObservation();

        //create time info xml
        //create timeInstance
        TimeObjectPropertyType timeSample=observationType.addNewSamplingTime();
        //create timeinstance
        TimeInstantDocument timeInstantDocument=TimeInstantDocument.Factory.newInstance();
        TimeInstantType timeInstantType= timeInstantDocument.addNewTimeInstant();
        //create timepostion
        TimePositionType timePositionType=TimePositionType.Factory.newInstance();
        timePositionType.set(sosWrapper.getSimpleTime());
        timeInstantType.setTimePosition(timePositionType);

        timeSample.set(timeInstantDocument);
        //create procedure
        ProcessPropertyType procedureType= observationType.addNewProcedure();
        procedureType.setHref(sosWrapper.getSensorID());

        //create observationProperty
        PhenomenonPropertyType phenomenonPropertyType= observationType.addNewObservedProperty();
        //create compositePhenomenon
        CompositePhenomenonDocument compositePhenomenonDocument=CompositePhenomenonDocument.Factory.newInstance();
        CompositePhenomenonType compositePhenomenonType=compositePhenomenonDocument.addNewCompositePhenomenon();
        compositePhenomenonType.setId("cpid0");

        CodeType name= compositePhenomenonType.addNewName();
        name.set("resultComponents");
        //add gregorian time property
        PhenomenonPropertyType timeComponent= compositePhenomenonType.addNewComponent();
        timeComponent.setHref("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");
        //add the property defined in SOSWrapper
        for (ObservedProperty property:sosWrapper.properties){
            PhenomenonPropertyType propertyComponent= compositePhenomenonType.addNewComponent();
            propertyComponent.setHref(property.getPropertyID());
        }
        compositePhenomenonType.setDimension(BigInteger.valueOf(1+sosWrapper.properties.size()));
        phenomenonPropertyType.set(compositePhenomenonDocument);

        //Create Feature of Interest
        //String latlonStr=sosWrapper.getLat()+" "+sosWrapper.getLon();
        FeaturePropertyType featureOfInterest= observationType.addNewFeatureOfInterest();
        featureOfInterest.setTitle("Aglobal_epsg4326");
        SamplingSurfaceDocument samplingSurfaceDocument=SamplingSurfaceDocument.Factory.newInstance();
        SamplingSurfaceType samplingSurfaceType=samplingSurfaceDocument.addNewSamplingSurface();
        samplingSurfaceType.setId("Aglobal_epsg4326");
        CodeType foiName= samplingSurfaceType.addNewName();
        foiName.set("earth");
        FeaturePropertyType featurePropertyType= samplingSurfaceType.addNewSampledFeature();
        featurePropertyType.setHref("urn:ogc:def:nil:OGC:unknown");
        SurfacePropertyType  shape= samplingSurfaceType.addNewShape();

        PolygonDocument polygonDocument=PolygonDocument.Factory.newInstance();
        PolygonType polygonType=  polygonDocument.addNewPolygon();
        polygonType.setId("polygon_sf_1");
        polygonType.setSrsName("urn:ogc:def:crs:EPSG::4326");
        AbstractRingPropertyType abstractRingPropertyType= polygonType.addNewExterior();
        LinearRingDocument linearRingDocument=LinearRingDocument.Factory.newInstance();
        LinearRingType linearRingType= linearRingDocument.addNewLinearRing();
        DirectPositionListType pointListType= linearRingType.addNewPosList();
//        pointListType.setSrsName("urn:ogc:def:crs:EPSG::4326");
        //fixed location of donghai range
        pointListType.set("90.0 -180.0 -90.0 -180.0 -90.0 180.0 90.0 180.0 90.0 -180.0");
        abstractRingPropertyType.set(linearRingDocument);
        shape.set(polygonDocument);
        featureOfInterest.set(samplingSurfaceDocument);

//        SamplingPointDocument samplingPointDocument=SamplingPointDocument.Factory.newInstance();
//        SamplingPointType samplingPointType=samplingPointDocument.addNewSamplingPoint();
//        samplingPointType.setId(latlonStr);
//        CodeType samplePointName=samplingPointType.addNewName();
//        samplePointName.set(latlonStr);
//        PointPropertyType position= samplingPointType.addNewPosition();
//        PointType pointType=position.addNewPoint();
//        pointType.setSrsName("urn:ogc:def:crs:EPSG::" + sosWrapper.getSrid());
//        DirectPositionType pos= pointType.addNewPos();
//        pos.set(latlonStr);
//        featureOfInterest.set(samplingPointDocument);
        //create result
        XmlObject resultType= observationType.addNewResult();

        //create element under data record
        DataArrayDocument dataArrayDocument=DataArrayDocument.Factory.newInstance();
        DataArrayType dataArrayType=dataArrayDocument.addNewDataArray1();
        AbstractDataArrayType.ElementCount elementCount=dataArrayType.addNewElementCount();
        net.opengis.swe.x101.CountDocument.Count count= elementCount.addNewCount();
        count.setValue(BigInteger.valueOf(1));
        DataComponentPropertyType dataComponentPropertyType= dataArrayType.addNewElementType();
        //elementType dataRecordDocument
        DataRecordDocument eleDataRecordDocument=DataRecordDocument.Factory.newInstance();
        DataRecordType propertyDataRecord=eleDataRecordDocument.addNewDataRecord();
        //create time field
        DataComponentPropertyType timeField= propertyDataRecord.addNewField();
        timeField.setName("Time");
        timeField.addNewTime().setDefinition("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");
        //create other property field
        for (ObservedProperty property:sosWrapper.properties){
            DataComponentPropertyType propertyField = propertyDataRecord.addNewField();
            if (property.getTypeOfProperty().equals("Text")) {
                //insert spatial wkt rect
                if (property.getPropertyID().equals("urn:ogc:def:phenomenon:OGC:1.0.30:WKT")) {
                    PositionDocument positionDocument = PositionDocument.Factory.newInstance();
                    PositionType dataRecordType = positionDocument.addNewPosition();
                    dataRecordType.setDefinition(property.getPropertyID());
                    propertyField.set(positionDocument);
                    propertyField.setName(property.getPropertyName());
                } else {
                    propertyField.setName(property.getPropertyName().trim());
                    // the default property is demicia
                    TextDocument.Text text = propertyField.addNewText();
                    text.setDefinition(property.getPropertyID());
                }
            }
        }
        dataComponentPropertyType.set(eleDataRecordDocument);
        dataComponentPropertyType.setName("Components");
        BlockEncodingPropertyType encodingPropertyType= dataArrayType.addNewEncoding();
        TextBlockDocument.TextBlock textBlock= encodingPropertyType.addNewTextBlock();
        textBlock.setDecimalSeparator(".");
        textBlock.setTokenSeparator(",_,");
        textBlock.setBlockSeparator(";");

        DataValuePropertyType valuePropertyType=dataArrayType.addNewValues();
        //create observation value string
        String values=sosWrapper.getSimpleTime();
        for (ObservedProperty property: sosWrapper.properties){
            values=values+",_,"+property.getDataValue();
        }

        Node pNode= valuePropertyType.getDomNode();
        Node node= pNode.getOwnerDocument().createTextNode(values+";");
        pNode.appendChild(node);
        //valuePropertyType.getDomNode().getFirstChild().setNodeValue(values + ";");
        resultType.set(dataArrayDocument);


        //print xml as string
        //output xml
        XmlOptions options=new XmlOptions();
        Map<String,String> nameSpace=new HashMap<String,String>();
        nameSpace.put("http://www.opengis.net/sos/1.0","");
        nameSpace.put("http://www.opengis.net/ows/1.1","ows");
        nameSpace.put("http://www.opengis.net/ogc","ogc");
        nameSpace.put("http://www.opengis.net/om/1.0","om");
        nameSpace.put("http://www.opengis.net/sos/1.0","sos");
        nameSpace.put("http://www.opengis.net/sampling/1.0","sa");
        nameSpace.put("http://www.opengis.net/gml","gml");
        nameSpace.put("http://www.opengis.net/swe/1.0.1","swe");
        nameSpace.put("http://www.w3.org/1999/xlink","xlink");
        nameSpace.put("http://www.w3.org/2001/XMLSchema-instance","xsi");
        options.setSaveSuggestedPrefixes(nameSpace);
        options.setSaveAggressiveNamespaces();
        options.setSavePrettyPrint();
        //options.setCharacterEncoding("UTF-8");
        insertSensorML= insertObservationDocument.xmlText(options);//get insertobservation xml
        return insertSensorML;
    }

}
