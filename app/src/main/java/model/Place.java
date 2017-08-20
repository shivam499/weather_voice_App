package model;

public class Place {

    private float lon;
    private float lat;
    private long sunrise;
    private long sunset;
    private String city;
    private String country;
    private long lastUpdate;

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

// --Commented out by Inspection START (03-08-2017 01:14 PM):
//    public long getLastUpdate() {
//        return lastUpdate;
//    }
// --Commented out by Inspection STOP (03-08-2017 01:14 PM)

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
