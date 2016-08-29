package com.example.wuwei.eweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AutoCompleteTextView autoCompleteTextView;
    ImageView day_icon,night_icon;
    CardView day_cardview,night_cardview;
    TextView textview_day_temp,textview_night_temp;
    LocationListener locationListener;
    Location currLocation;
    String consumerKey = "1d2d53747713e542748740c909e33590";
    String forecastEndpoint = "https://api.forecast.io/forecast/";
    String reverseGeoEndpoint = "https://maps.googleapis.com/maps/api/geocode/json?latlng=LATLONG&location_type=ROOFTOP&result_type=street_address&key="+consumerKey;
    final int requestFineLocation = 26;
    TextView tv;
    String city_name = null;
    String province_code = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        day_icon = (ImageView) findViewById(R.id.imageview_day_icon);
        night_icon = (ImageView) findViewById(R.id.imageview_night_icon);
        textview_day_temp= (TextView) findViewById(R.id.textview_day_temp);
        textview_night_temp = (TextView) findViewById(R.id.textview_night_temp);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.searchbar_autocomplete);
        day_cardview = (CardView) findViewById(R.id.card_view_day);
        day_cardview.setOnTouchListener(new View.OnTouchListener() {
            float y1 = 0,y2=0;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int MIN_DISTANCE = 240;
                switch(motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        y1 = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        y2 = motionEvent.getY();

                        float deltaX = y2 - y1;

                        if (Math.abs(deltaX) > MIN_DISTANCE)
                        {
                            // Left to Right swipe action
                            if (y2 > y1)
                            {
                               System.err.println("swiped down"+y1+"  "+y2);
                            }


                        }

                }
                return true;


            }
        });
        night_cardview = (CardView) findViewById(R.id.card_view_night);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                getAutoComplete getAutocompleteCity = new getAutoComplete();
                String city = editable.toString();
                getAutocompleteCity.execute(city.replace(" ","%20d"));
            }
        });
        autoCompleteTextView.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tv = (TextView) findViewById(R.id.textView);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},requestFineLocation);

            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);

        if (currLocation==null){
            currLocation = (locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER));
            System.err.println(currLocation+" current location");
        }
        if (currLocation!=null) {
            new getWeatherForecast().execute();
        }





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestFineLocation &&permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(getBaseContext(),"permission granted"+" "+ permissions[0],Toast.LENGTH_SHORT).show();
        }
        else if (requestCode== requestFineLocation&&grantResults.length < 1){
            Toast.makeText(getBaseContext(),"Need location permission",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},requestFineLocation);

        }
    }
    public class getCityName extends AsyncTask<Location,Void,String> {

        @Override
        protected String doInBackground(Location... locations) {
            String retVal = null;

            String tempEndpoint = reverseGeoEndpoint.replace("LATLONG",locations[0].getLatitude()+","+locations[0].getLongitude());
            try {
                URL url = new URL(tempEndpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();

                JSONArray jsonArray = getResponseBody(is).optJSONArray("results").optJSONObject(0).optJSONArray("address_components");
                int size = jsonArray.length();
                for (int i = 0 ; i< size;i++){
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    JSONArray type = jsonObject.optJSONArray("types");

                    if (type.length()==2){
                        if ((type.get(0).equals("locality")&&type.get(1).equals("political"))||(type.get(0).equals("political")&&type.get(1).equals("locality"))){
                            city_name = jsonObject.optString("long_name");
                        }else if (type.get(0).equals("administrative_area_level_1")&&type.get(1).equals("political")){
                            province_code = jsonObject.optString("short_name");
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(String s) {


            tv.setText(city_name + " ," + province_code);



        }
    }

    public class getWeatherForecast extends AsyncTask<Void,Void,ResponseBody>{

        @Override
        protected ResponseBody doInBackground(Void... voids) {
            String endpoint = forecastEndpoint+consumerKey+"/"+currLocation.getLatitude()+","+currLocation.getLongitude()+"?exclude=minutely,flags";
            ResponseBody responseBody = null;
            System.err.println(endpoint);
            try {
                URL url = new URL(endpoint);
                InputStream is = url.openStream();
                JSONObject jsonObject;
                if (is == null)
                    return null;
                else
                    jsonObject = getResponseBody(is);

                JSONObject currentWeather = jsonObject.optJSONObject("currently");
                HourlyWeatherInfo weatherInfo = new HourlyWeatherInfo(currentWeather.optLong("time"),currentWeather.optString("summary"),currentWeather.optString("icon"),
                        currentWeather.optDouble("precipIntensity"),currentWeather.getDouble("precipProbability"),currentWeather.optDouble("temperature"),
                        currentWeather.optDouble("feels_like"),currentWeather.optDouble("humidity"),currentWeather.optDouble("windSpeed"),currentWeather.optDouble("visibility"),
                        currentWeather.optDouble("cloud_cover"),currentWeather.optDouble("pressure"),currentWeather.optDouble("ozone"));
                ArrayList<HourlyWeatherInfo> hourlyWeatherInfos;
                ArrayList<DailyWeather> dailyWeatherInfos;
                JSONObject hourlyWeather = jsonObject.optJSONObject("hourly");
                JSONArray hourlyDataArray = hourlyWeather.optJSONArray("data");
                hourlyWeatherInfos = getWeatherInfoArrayList(hourlyDataArray);
                JSONObject dailyWeather = jsonObject.optJSONObject("daily");
                JSONArray dailyWeatherArray = dailyWeather.optJSONArray("data");
                dailyWeatherInfos = getDailyWeatherInfoArrayList(dailyWeatherArray);
                responseBody = new ResponseBody(hourlyWeatherInfos,dailyWeatherInfos,hourlyWeather.optString("summary"),weatherInfo);






            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(ResponseBody responseBody) {
            int drawable_id  = getResources().getIdentifier(responseBody.currentWeather.icon,"drawble",getPackageName());
            Bitmap tempbitmap = BitmapFactory.decodeResource(getResources(),R.drawable.clear_day);
            day_icon.setImageBitmap(Bitmap.createScaledBitmap(tempbitmap, (int) (tempbitmap.getWidth()*2.8), (int) (tempbitmap.getHeight()*2.8),false));
            night_icon.setImageBitmap(Bitmap.createScaledBitmap(tempbitmap, (int) (tempbitmap.getWidth()*2.8), (int) (tempbitmap.getHeight()*2.8),false));




            textview_day_temp.setText(responseBody.currentWeather.temperature+" F");
        }
    }

    public class getAutoComplete extends AsyncTask<String,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            String autoCompleteEndpoint = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=citynameAutoComplete&types=(cities)&language=en&key=AIzaSyD9p1Tsx6QcPvep5Bz2HAc3EnfpgoZydJU";
            ArrayList<String> cities = new ArrayList<String>();
            try {
                URL url = new URL(autoCompleteEndpoint.replace("citynameAutoComplete",strings[0]));
                JSONObject jsonObject = getResponseBody(url.openStream());
                if (jsonObject!=null){
                    JSONArray jsonArray = jsonObject.optJSONArray("predictions");
                    int size = jsonArray.length();
                    for (int i = 0 ; i < size;i++){
                        JSONObject cityNameJSonObj = jsonArray.optJSONObject(i);
                        cities.add(cityNameJSonObj.optString("description"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cities;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            String[] cities = strings.toArray(new String[strings.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),R.layout.support_simple_spinner_dropdown_item,cities);
            autoCompleteTextView.setAdapter(adapter);

        }
    }
    public JSONObject getResponseBody(InputStream is ) throws Exception {
        BufferedReader bfr = new BufferedReader(new InputStreamReader(is));
        String body="";
        String temp;
        while (null!=(temp=bfr.readLine())){
            body+=temp;
        }
        System.err.println(body);
        JSONObject js = new JSONObject(body);
        return js;
    }
    public void setLocationListener(){
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currLocation= location;
                Toast.makeText(getBaseContext(),"location changed "+location.getLatitude(),Toast.LENGTH_LONG).show();

                // new getCityName().execute(currLocation);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Toast.makeText(getBaseContext(),"location changed "+s,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(getBaseContext(),"location changed "+s,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getBaseContext(),"location changed "+s,Toast.LENGTH_LONG).show();
            }
        };
    }

    public ArrayList<HourlyWeatherInfo> getWeatherInfoArrayList(JSONArray jsonarray) throws JSONException {
        int size = jsonarray.length();
        ArrayList<HourlyWeatherInfo> retArray = new  ArrayList<HourlyWeatherInfo>();
        for (int i = 0 ; i < size;i++){
            JSONObject weatherAtTime = jsonarray.getJSONObject(i);
            retArray.add(new HourlyWeatherInfo(weatherAtTime.optLong("time"),weatherAtTime.optString("summary"),weatherAtTime.optString("icon"),
                    weatherAtTime.optDouble("precipIntensity"),weatherAtTime.getDouble("precipProbability"),weatherAtTime.optDouble("temperature"),
                    weatherAtTime.optDouble("feels_like"),weatherAtTime.optDouble("humidity"),weatherAtTime.optDouble("windSpeed"),weatherAtTime.optDouble("visibility"),
                    weatherAtTime.optDouble("cloud_cover"),weatherAtTime.optDouble("pressure"),weatherAtTime.optDouble("ozone")));

        }
        return  retArray;
    }
    public ArrayList<DailyWeather> getDailyWeatherInfoArrayList(JSONArray jsonarray) throws JSONException {
        int size = jsonarray.length();
        ArrayList<DailyWeather> retArray = new  ArrayList<DailyWeather>();
        for (int i = 0 ; i < size;i++){
            JSONObject weatherAtTime = jsonarray.getJSONObject(i);
            retArray.add(new DailyWeather(weatherAtTime.optLong("time"),weatherAtTime.optString("summary"),weatherAtTime.optString("icon"),
                    weatherAtTime.optLong("sunriseTime"),weatherAtTime.optLong("sunsetTime"),
                    weatherAtTime.optDouble("precipIntensity"),weatherAtTime.getDouble("precipProbability"),weatherAtTime.optDouble("temperature"),
                    weatherAtTime.optDouble("temperatureMin"),weatherAtTime.optDouble("temperatureMax"),
                    weatherAtTime.optDouble("feels_like"),weatherAtTime.optDouble("humidity"),weatherAtTime.optDouble("windSpeed"),weatherAtTime.optDouble("visibility"),
                    weatherAtTime.optDouble("cloud_cover"),weatherAtTime.optDouble("pressure"),weatherAtTime.optDouble("ozone")));

        }
        return retArray;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            autoCompleteTextView.bringToFront();
            if (autoCompleteTextView.getVisibility()==View.INVISIBLE)
                autoCompleteTextView.setVisibility(View.VISIBLE);
            else autoCompleteTextView.setVisibility(View.INVISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //need to implement this method
    public String getIconFileName(String icon_name){
        String[] planets = getResources().getStringArray(R.array.icon_array);
        int size = planets.length;
        for (int i = 0 ; i < size;i++){

        }
        return null;
    }
}
