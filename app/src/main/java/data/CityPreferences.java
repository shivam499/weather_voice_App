package data;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreferences {
    private final SharedPreferences sharedPreferences;

    public CityPreferences(Activity activity){
        sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity(){
        return sharedPreferences.getString("city","Raebareli,IN");
    }
    public void setCity(String city){
        sharedPreferences.edit().putString("city",city).apply();
    }
}
