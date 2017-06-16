package yuan.ocean.Entity;

import java.io.Serializable;

/**
 * Created by Yuan on 2017/1/15.
 */
public class ObservedProperty implements Serializable {

    private String propertyID;
    private String propertyName;
    private String unit;
    private String dataValue;
    public double tempSumValue=0;
    private String typeOfProperty;
    public int count=0;

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getTempSumValue() {
        return tempSumValue;
    }

    public void setTempSumValue(double tempSumValue) {
        this.tempSumValue = tempSumValue;
    }

    public String getTypeOfProperty() {
        return typeOfProperty;
    }

    public void setTypeOfProperty(String typeOfProperty) {
        this.typeOfProperty = typeOfProperty;
    }
}
