package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Utils;
import model.Place;
import model.Weather;


public class JSONWeatherParser {

    public static Weather getWeather(String data){

        Weather weather = new Weather();

        try {
            JSONObject jsonObject = new JSONObject(data);

            //get the lat and lon
            Place place = new Place();
            JSONObject coordObj = Utils.getObject("coord",jsonObject);
            place.setLat(Utils.getFloat("lat",coordObj));
            place.setLon(Utils.getFloat("lon",coordObj));

            //get the sys data
            JSONObject sysObj = Utils.getObject("sys",jsonObject);

            if (sysObj.has("country")){
                place.setCountry(Utils.getString("country",sysObj));
            }else{
                place.setCountry(null);
            }
            place.setSunrise(Utils.getInt("sunrise",sysObj));
            place.setSunset(Utils.getInt("sunset",sysObj));
            place.setLastUpdate(Utils.getInt("dt",jsonObject));
            place.setCity(Utils.getString("name",jsonObject));
            weather.place = place;

            //get the weather info
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherId(Utils.getInt("id",jsonWeather));
            weather.currentCondition.setIcon(Utils.getString("icon",jsonWeather));
            weather.currentCondition.setCondition(Utils.getString("main",jsonWeather));
            weather.currentCondition.setDescription(Utils.getString("description",jsonWeather));

            //get wind data
            JSONObject windObj = Utils.getObject("wind",jsonObject);
            weather.wind.setSpeed(Utils.getFloat("speed",windObj));
            if(windObj.has("deg")){
                weather.wind.setDeg(Utils.getFloat("deg",windObj));
            }else{
                weather.wind.setDeg(0.0f);
            }

            //get Cloud data
            JSONObject cloudObj = Utils.getObject("clouds",jsonObject);
            weather.clouds.setPrecipitation(Utils.getInt("all",cloudObj));

            //get temp and main data

            JSONObject mainObj = Utils.getObject("main",jsonObject);
            weather.currentCondition.setHumidity(Utils.getInt("humidity",mainObj));
            weather.currentCondition.setTemperature(Utils.getDouble("temp",mainObj));
            weather.currentCondition.setPressure(Utils.getInt("pressure",mainObj));
            weather.currentCondition.setMinTemp(Utils.getFloat("temp_min",mainObj));
            weather.currentCondition.setMaxTemp(Utils.getFloat("temp_max",mainObj));

            return weather;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
