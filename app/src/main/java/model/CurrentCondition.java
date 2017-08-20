package model;

public class CurrentCondition {

    private String condition;
    private String icon;
    private float pressure;
    private float humidity;
    private float maxTemp;
    private float minTemp;
    private double temperature;
    // --Commented out by Inspection (03-08-2017 01:14 PM):private int visibility;

    public void setWeatherId(int weatherId) {
        int weatherId1 = weatherId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

// --Commented out by Inspection START (03-08-2017 02:55 AM):
//    public String getDescription() {
//        return description;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:55 AM)

    public void setDescription(String description) {
        String description1 = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
