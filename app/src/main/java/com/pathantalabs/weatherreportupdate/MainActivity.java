package com.pathantalabs.weatherreportupdate;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.plus.PlusOneButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Util.Utils;
import data.CityPreferences;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;


public class MainActivity extends AppCompatActivity implements LocationListener,NavigationView.OnNavigationItemSelectedListener{

    private final String APP_SHARE_LINK = "https://play.google.com/store/apps/details?id=com.pathantalabs.weatherreportupdate&ln=en";

    private LocationManager locationManager;
    private Location location;

    private ProgressDialog progressDialog;
    private static final int myPermission = 1;

    private Weather weather = new Weather();

    private ImageView iconView;
    private TextView cityName;
    private TextView temp;
    private TextView humidity;
    private TextView status;
    private TextView pressure;
    private TextView cloudiness;
    private TextView wind;
    private TextView latitude;
    private TextView longitude;
    private TextView minTemp;
    private TextView maxTemp;
    private ImageView shareBtn;
    private TextView sunrise;
    private TextView sunset;
    private ImageView micButton;
    private ImageView ttsButton;
    private ImageView searchButton;

    private Handler adHandler;

    private Runnable adRun;
    private boolean isTempFar = true;


    private boolean isActivityIsVisible = true;
    private InterstitialAd interstitialAd;

    private TextView maxTempText;
    private TextView minTempText;
    private URL locationUrl;

    private boolean permissionGrant = false;

    private TextToSpeech ttsObj;
    private Handler handler;
    private long timeRemain = 4000;
    private ProgressDialog pd;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case myPermission:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGrant = true;
                    getWeatherByLocation();
                } else {
                    permissionGrant = false;
                    Toast.makeText(this, "This App requires Location Permission Granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReplaceFont.ReplaceDefaultFont(this, "SERIF", "roboto_slab_light.ttf");
        setContentView(R.layout.activity_drawer);

        MobileAds.initialize(this, getResources().getString(R.string.app_Id));
        iconView = (ImageView) findViewById(R.id.weather_icon);
        cityName = (TextView) findViewById(R.id.city_name);
        temp = (TextView) findViewById(R.id.temp_city);
        humidity = (TextView) findViewById(R.id.humidity);
        pressure = (TextView) findViewById(R.id.pressure);
        cloudiness = (TextView) findViewById(R.id.cloud);
        wind = (TextView) findViewById(R.id.windText);
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        status = (TextView) findViewById(R.id.status);
        minTemp = (TextView) findViewById(R.id.min_temp);
        maxTemp = (TextView) findViewById(R.id.max_temp);
        shareBtn = (ImageView) findViewById(R.id.share_weather);
        maxTempText = (TextView) findViewById(R.id.max_temp_text);
        minTempText = (TextView) findViewById(R.id.min_temp_text);
        sunrise = (TextView) findViewById(R.id.sunriseValue);
        sunset = (TextView) findViewById(R.id.sunsetValue);
        micButton = (ImageView) findViewById(R.id.mic_Button);
        ttsButton = (ImageView) findViewById(R.id.tts_Button);
        searchButton = (ImageView) findViewById(R.id.search_Button);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        final CardView adCardView = (CardView) findViewById(R.id.nativeAdCardView);

        PlusOneButton googleButton = (PlusOneButton) findViewById(R.id.googlePlus);
        cityName.setTextColor(getResources().getColor(R.color.colorPrimary));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_navi);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle
                (this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        minTemp.setVisibility(View.GONE);
        maxTemp.setVisibility(View.GONE);
        minTempText.setVisibility(View.GONE);
        maxTempText.setVisibility(View.GONE);
        iconView.setVisibility(View.GONE);
        shareBtn.setVisibility(View.GONE);

        setTitle("Weather Report");

        googleButton.initialize(APP_SHARE_LINK,0);

        //start native Add description
        final NativeExpressAdView nativeExpressAdView = new NativeExpressAdView(this);
        nativeExpressAdView.setAdUnitId(getResources().getString(R.string.native_ad_Unit_Id));

        adCardView.post(new Runnable() {
            @Override
            public void run() {
                float density = MainActivity.this.getResources().getDisplayMetrics().density;
                AdSize adSize = new AdSize((int) (adCardView.getWidth() / density), 132);
                nativeExpressAdView.setAdSize(adSize);
                adCardView.addView(nativeExpressAdView);
                nativeExpressAdView.loadAd(new AdRequest.Builder().build());
            }
        });

        //end of native ad Description
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interst_ad_Unit_Id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                }, myPermission);
                return;
            }
        }

        //using Saved Instance States
        if (savedInstanceState != null) {
            String locUrl = savedInstanceState.getString("LOCATION_URL_VALUE");
            try {
                URL cordsUrl = new URL(locUrl);
                callingUrls(cordsUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            if (!permissionGrant) {
                if (isNetworkAvailable()) {
                    Log.v("this 1 called", "1");
                    getWeatherByLocation();
                } else {
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.hide();
                    }
                    setContentView(R.layout.no_internet_layout);
                }

            }

        }
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWeatherCondition();
            }
        });

        Log.v("Create", "onCreate");

        //show ad ad every 50 seconds
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            if (interstitialAd.isLoaded()){
                interstitialAd.show();
            }
        }
    }

    private void buildUrlByLocation(String city) {
        String placeString = Utils.BASE_URL + city + "&units=metric" + Utils.APP_ID;
        URL placeUrl = null;
        try {
            placeUrl = new URL(placeString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        callingUrls(placeUrl);
    }

    private void buildUrlByCords(Double lat, Double lon) {

        String latLonApi = Utils.LOCATION_URL + "&lat=" + lat + "&lon=" + lon + "&units=metric" + Utils.APP_ID;
        locationUrl = null;
        try {
            locationUrl = new URL(latLonApi);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        callingUrls(locationUrl);
    }

    private void callingUrls(URL urls) {
        if (isNetworkAvailable()) {
            new WeatherTask().execute(urls);
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            setContentView(R.layout.no_internet_layout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.changeTemp:
                convertTemp();
                break;
            case R.id.refreshButton:
                getWeatherByLocation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void convertTemp() {
        if (weather!=null){
            float tempC = (float) weather.currentCondition.getTemperature();
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            String tempFormat = decimalFormat.format(tempC);
            float tempF;
            if (isTempFar) {
                tempF = tempC * 9 / 5 + 32;
                String tempFaren = decimalFormat.format(tempF);
                temp.setText("" + tempFaren + "°F");
                isTempFar = false;
            } else  {
                temp.setText("" + tempFormat + "°C");
                isTempFar = true;
            }
        }else{
            Toast.makeText(this, "No Weather Details", Toast.LENGTH_SHORT).show();
        }

    }

    private void shareWeatherApp() {
        Intent chooserShare;
        String shareAppString = "Weather Report App is Lightweight, convenient App." +
                " It provides the Accurate Weather Details by your Current Location and as well as " +
                "By Searching the Location by Name.\n For more Get it from Google Play Store:\n" + APP_SHARE_LINK;

        Intent shareAppIntent = new Intent(Intent.ACTION_SEND);
        shareAppIntent.putExtra(Intent.EXTRA_TEXT, shareAppString.trim());
        shareAppIntent.setType("text/plain");
        chooserShare = Intent.createChooser(shareAppIntent, "Share the App");
        startActivity(chooserShare);
    }


    private void showAboutDialog() {

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = null;
        if (pInfo != null) {
            version = pInfo.versionName;
        }
        String aboutText = "A Fast, Lightweight and Accurate Weather app." + "\nGPS Permission is taken to give the Weather Details of your Current Location" +
                "\nNow you can get the Input Through Voice" + "\nData Provided by OpenWeatherMap" + "\n\nVersion : " + version + "";
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
        aboutDialog.setTitle("Weather Report");
        aboutDialog.setMessage(aboutText);
        /*final WebView webView = new WebView(this);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData(aboutText, "text/html", "UTF-8");*/
        /*aboutDialog.setView(webView);*/

        aboutDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        aboutDialog.show();
    }

    //Share Current Weather Condition using Intent
    private void shareWeatherCondition() {

        if (weather!=null){
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());

            String shareString = "Current Weather Report of \n" + weather.place.getCity() + ","
                    + weather.place.getCountry() + "\n Temperature : " + tempFormat + " °C ,\n Condition :" +
                    weather.currentCondition.getCondition() + ",\n Full details get it From Google Play Store" + APP_SHARE_LINK;

            Intent shareWeatherIntent = new Intent(Intent.ACTION_SEND);
            shareWeatherIntent.putExtra(Intent.EXTRA_TEXT, shareString.trim());
            shareWeatherIntent.setType("text/plain");
            Intent chooser = Intent.createChooser(shareWeatherIntent, "Share Current Weather");
            startActivity(chooser);
        }else{
            Toast.makeText(this, "Problem in Sending Weather Condition!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.v("LocationChanged","Exception in Changed Location.");
        }
        double latChange = location.getLatitude();
        double longChange = location.getLongitude();

        buildUrlByCords(latChange, longChange);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    getWeatherByLocation();
                }
            }
        });
        timer.start();
        Toast.makeText(MainActivity.this, "GPS Enabled.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                return DownloadImage(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setVisibility(View.VISIBLE);
            iconView.setImageBitmap(bitmap);
        }

        private Bitmap DownloadImage(String code) throws IOException {
            final DefaultHttpClient client = new DefaultHttpClient();
            final HttpGet getRequest = new HttpGet(Utils.ICON_URL + code + ".png");

            try {
                HttpResponse response = client.execute(getRequest);
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    return null;
                }

                final HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream inputStream;
                    // getting contents from the stream
                    inputStream = entity.getContent();
                    // decoding stream data back into image Bitmap
                    return BitmapFactory.decodeStream(inputStream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class WeatherTask extends AsyncTask<URL, Void, Weather> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Weather doInBackground(URL... urls) {
            //Log.v("URL", "URL is: " + urls[0]);

            String data = WeatherHttpClient.getWeatherData(urls[0]);
            //Log.v("Data", "Fetch Data" + data);
            if (data !=null){
                weather = JSONWeatherParser.getWeather(data);
                return weather;
            }else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            progressDialog.dismiss();

            if (weather == null){
                AlertDialog.Builder showError = new AlertDialog.Builder(MainActivity.this);
                showError.setMessage("Error in Weather data Fetch.\n Please try Again !");
                showError.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                showError.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getWeatherByLocation();
                    }
                });
                showError.show();
            }else{
                new DownloadImageTask().execute(weather.currentCondition.getIcon());

                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());

                if (weather.currentCondition.getMaxTemp() == weather.currentCondition.getMinTemp()) {
                    minTemp.setVisibility(View.GONE);
                    maxTemp.setVisibility(View.GONE);
                    minTempText.setVisibility(View.GONE);
                    maxTempText.setVisibility(View.GONE);
                } else {
                    maxTempText.setVisibility(View.VISIBLE);
                    maxTemp.setVisibility(View.VISIBLE);
                    minTempText.setVisibility(View.VISIBLE);
                    minTemp.setVisibility(View.VISIBLE);
                    minTemp.setText("" + weather.currentCondition.getMinTemp() + " °C");
                    maxTemp.setText("" + weather.currentCondition.getMaxTemp() + " °C");
                }

                if (weather.place.getCountry() == null) {
                    cityName.setText(weather.place.getCity());
                } else {
                    cityName.setText(weather.place.getCity() + ", " + weather.place.getCountry());
                }

                temp.setText("" + tempFormat + "°C");
                wind.setText("WIND : " + weather.wind.getSpeed() + " m/s, " + Utils.getFormattedWindDegree(weather.wind.getDeg()));
                pressure.setText("PRESSURE : " + weather.currentCondition.getPressure() + " hPa");
                humidity.setText("HUMIDITY : " + weather.currentCondition.getHumidity() + " %");
                status.setText(weather.currentCondition.getCondition());
                cloudiness.setText("CLOUDINESS : " + weather.clouds.getPrecipitation() + " %");
                latitude.setText("" + weather.place.getLat() + "");
                longitude.setText("" + weather.place.getLon() + "");
                sunrise.setText(Utils.timeFormat(weather.place.getSunrise()));
                sunset.setText(Utils.timeFormat(weather.place.getSunset()));

                shareBtn.setVisibility(View.VISIBLE);
            }

            Handler adShow = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                        if (interstitialAd.isLoaded()){
                            interstitialAd.show();
                        }
                }
            };
            adShow.postDelayed(runnable, 4000);
        }
    }

    //check for active network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnected());
    }


    private void getWeatherByLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();

        for (String provider : providers) {
            Log.v("Provider", "Provider Name " + provider);

            if (provider.contains("gps")) {
                boolean providerValue = locationManager.isProviderEnabled(provider);
                if (!providerValue) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Enable GPS");
                    alertDialog.setMessage("GPS is not Enabled.\nEnable it Now from Settings.\nTo Get more Accurate Weather Updates.");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            //add to open the enable the GPS dialog
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }

            location = locationManager.getLastKnownLocation(provider);
            Log.v("Location", "Location End Value 1 : " + location);
        }


        if (location == null) {
            for (String provider : providers) {
                locationManager.requestLocationUpdates(provider, 500, 0, this);
            }
            Log.v("Location", "No Location : " + null);
        } else {
            Log.v("Location", "Location Value " + location);
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                buildUrlByCords(lat, lon);
            }
        }
    }

    long adTime = 0;
    @Override
    protected void onResume() {
        super.onResume();
        isActivityIsVisible = true;
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                adHandler = new Handler();

                adRun = new Runnable() {
                    @Override
                    public void run() {
                            if (isActivityIsVisible) {
                                if(adTime%25000 == 0){
                                    interstitialAd.show();
                                }
                                adTime = adTime+1000;
                               adHandler.postDelayed(this,1000);
                            }
                    }
                };
                adHandler.postDelayed(adRun,25000);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogInput();
            }
        });

        ttsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (weather!=null){
                    startMainTTSMethod();
                }else{
                    AlertDialog.Builder ttsError = new AlertDialog.Builder(getApplication());
                    ttsError.setTitle("TTS Error");
                    ttsError.setMessage("Error in Weather or Converting Text-to-Speech.\nTry Again!");
                    ttsError.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getWeatherByLocation();
                        }
                    });
                    ttsError.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ttsError.show();
                }

            }
        });

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tap to Cancel Search");
                startActivityForResult(voiceIntent, 200);
            }
        });
    }

    private void startMainTTSMethod() {
        ttsStart();
        pd = new ProgressDialog(MainActivity.this);
        pd.show();
        pd.setMessage("Weather to Speech...");

        handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (timeRemain > 0) {
                    timeRemain = timeRemain - 1000;
                    handler.postDelayed(this, 1000);
                } else {
                    pd.dismiss();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String keyword = results.get(0);
                searchCityByData(keyword);
            }
        }
    }

    private void ttsStart() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String tempSpeak = decimalFormat.format(weather.currentCondition.getTemperature());

        DecimalFormat humidityFormat = new DecimalFormat("0");
        String humiditySpeak = humidityFormat.format(weather.currentCondition.getHumidity());

        final String ttsString = "Weather Report of " + weather.place.getCity() + " is, Temperature " +
                tempSpeak + "°C, Condition " + weather.currentCondition.getCondition() + ", Humidity is "
                + humiditySpeak + "%, Thank you for Using Weather Voice Report App , Have a Nice day";

        ttsObj = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    ttsObj.setSpeechRate(0.9f);
                    ttsObj.setPitch(0.9f);
                    if (ttsObj.getDefaultEngine() == null) {
                        Intent install = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        startActivity(install);
                    }
                    ttsObj.setLanguage(Locale.getDefault());
                    ttsObj.speak(ttsString, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.searchName:
                showDialogInput();
                break;
            case R.id.searchVoice:
                Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tap to Cancel Search");
                startActivityForResult(voiceIntent, 200);
                break;
            case R.id.changeTemp:
                convertTemp();
                break;
            case R.id.about:
                showAboutDialog();
                break;
            case R.id.share_App:
                shareWeatherApp();
                break;
            case R.id.rateNow:
                Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_SHARE_LINK));
                startActivity(rateIntent);
                break;
            case R.id.listenWeather:
                startMainTTSMethod();
                break;
            case R.id.shareweather:
                shareWeatherCondition();
                break;
            case R.id.contactUs:
                mailToDeveloper();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mailToDeveloper() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:shivamgupta10097@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Regards Weather Voice Report App");
        Intent chooser = Intent.createChooser(intent,"Mail to developer");
        startActivity(chooser);
    }


    private void showDialogInput() {
        AlertDialog.Builder searchCity = new AlertDialog.Builder(this);
        searchCity.setTitle("Change City");
        searchCity.setMessage("Now you can Search city by City name and Postal Code/ZIP Code.");
        final EditText enterCity = new EditText(this);
        enterCity.setPadding(40, 40, 40, 40);
        enterCity.setMaxLines(1);
        enterCity.setSingleLine(true);
        enterCity.setInputType(InputType.TYPE_CLASS_TEXT);
        enterCity.setHint("Enter City/ZIP Code");
        searchCity.setView(enterCity);
        searchCity.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String cityName = enterCity.getText().toString().trim();
                if (cityName.equals("")) {
                    Toast.makeText(MainActivity.this, "Enter City Name", Toast.LENGTH_SHORT).show();
                } else {
                    searchCityByData(cityName);
                }
            }
        });
        searchCity.show();
        if (interstitialAd.isLoaded())
            interstitialAd.show();
    }

    private void searchCityByData(String city) {
        CityPreferences cityPreferences = new CityPreferences(MainActivity.this);
        cityPreferences.setCity(city);
        String newCity = cityPreferences.getCity().trim();

        buildUrlByLocation(newCity);
    }


    @Override
    protected void onPause() {
        if (ttsObj != null) {
            ttsObj.stop();
            ttsObj.shutdown();
        }
        super.onPause();
       isActivityIsVisible = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("LOCATION_URL_VALUE", String.valueOf(locationUrl));
        super.onSaveInstanceState(outState);
    }
}