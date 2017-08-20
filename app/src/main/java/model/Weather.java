package model;

public class Weather {

    public Place place;
    // --Commented out by Inspection (03-08-2017 01:14 PM):private String iconData;
    public final Clouds clouds = new Clouds();
    public final CurrentCondition currentCondition = new CurrentCondition();
    // --Commented out by Inspection (03-08-2017 01:14 PM):private Temperature temperature = new Temperature();
    public final Wind wind = new Wind();

    public Weather() {
    }

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public Weather(Place place, String iconData, Clouds clouds, CurrentCondition currentCondition, Temperature temperature, Wind wind) {
//        this.place = place;
//        this.iconData = iconData;
//        this.clouds = clouds;
//        this.currentCondition = currentCondition;
//        this.snow = snow;
//        this.temperature = temperature;
//        this.wind = wind;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)


// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public Place getPlace() {
//        return place;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public void setPlace(Place place) {
//        this.place = place;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public String getIconData() {
//        return iconData;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public void setIconData(String iconData) {
//        this.iconData = iconData;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public Clouds getClouds() {
//        return clouds;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public void setClouds(Clouds clouds) {
//        this.clouds = clouds;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public CurrentCondition getCurrentCondition() {
//        return currentCondition;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public void setCurrentCondition(CurrentCondition currentCondition) {
//        this.currentCondition = currentCondition;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)


// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public Temperature getTemperature() {
//        return temperature;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public void setTemperature(Temperature temperature) {
//        this.temperature = temperature;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public Wind getWind() {
//        return wind;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

// --Commented out by Inspection START (03-08-2017 02:57 AM):
//    public void setWind(Wind wind) {
//        this.wind = wind;
//    }
// --Commented out by Inspection STOP (03-08-2017 02:57 AM)

}
