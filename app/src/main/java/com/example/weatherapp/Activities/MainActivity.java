package com.example.weatherapp.Activities;


import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.weatherapp.Network.Network;
import com.example.weatherapp.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    String currentLocation;

    final static String TAG = "DEBUGGING";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        currentLocation = intent.getStringExtra("LATITUDE") + "," + intent.getStringExtra("LONGITUDE");

        Log.v(TAG, currentLocation);

        final OkHttpClient client = new OkHttpClient();

        //final Request request = new Request.Builder().url(Network.openWeatherAPI + "current.json?key=" + Network.getOpenWeatherAPIKey
                //+ "&aqi=no&q="+ currentLocation).build();

        final Request request = new Request.Builder()
                .url(Network.openWeatherAPI + "forecast.json?key=" + Network.getOpenWeatherAPIKey
                        + "&q=" + currentLocation + "&days=1&aqi=no&alerts=no")
                .build();

        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()){
                        return null;
                    }
                    return response.body().string();
                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                if(s != null){
                    JSONObject jsonResponses = null;
                    try {
                        jsonResponses = new JSONObject(s);
                        JSONObject locationObject = jsonResponses.getJSONObject("location");
                        JSONObject currentObject = jsonResponses.getJSONObject("current");
                        JSONObject tempObject = jsonResponses.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day");

                        binding.locationText01.setText(locationObject.getString("name"));
                        binding.currentText.setText(String.valueOf((int) Math.round(currentObject.getDouble("temp_c"))) + "\u2103");
                        binding.miniumText.setText("min: " + String.valueOf((int) Math.round(tempObject.getDouble("mintemp_c"))) + "\u2103");
                        binding.maximuntext.setText("max: " + String.valueOf((int) Math.round(tempObject.getDouble("maxtemp_c"))) + "\u2103");

                        String imageUrl = currentObject.getJSONObject("condition").getString("icon");
                        imageUrl = "https:" + imageUrl.replace("64", "128");
                        Glide.with(MainActivity.this).load(imageUrl).into(binding.weatherImage);

                    }catch (JSONException e){
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        asyncTask.execute();
    }
}