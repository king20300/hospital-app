package tw.edu.fju.www.sedia.hospital.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import tw.edu.fju.www.sedia.hospital.MainActivity;
import tw.edu.fju.www.sedia.hospital.R;

public class GetNearbyUtil extends AsyncTask<double[], Integer, String> {
    private String apiKey;
    private OkHttpClient client;
    private String requestUrl;
    private WeakReference<Activity> weakReferenceContext;
    private double[] userLocation;

    public GetNearbyUtil(Activity activity) {
        this.weakReferenceContext = new WeakReference<>(activity);
    }

    @Override
    protected String doInBackground(double[]... location) {
        apiKey = "fQM9UrN1mF5EBl9Gp7GGCfxd4XWD74An";
        userLocation = location[0];
        client = new OkHttpClient();
        requestUrl = "https://api.tomtom.com/search/2/nearbySearch/.json?lat=" + location[0][0] + "&lon=" + location[0][1] + "&limit=30&countrySet=TW&radius=10000&language=zh-TW&categorySet=7321&key=" + apiKey;

        String result = null;
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();

        try {
            result = client.newCall(request).execute().body().string();
        } catch (IOException ignore) { }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        Activity activity = weakReferenceContext.get();
        Intent viewMap = new Intent(activity, MarkerMapsActivity.class);
        viewMap.putExtra("jsonData", s);
        viewMap.putExtra("userLocation", userLocation);
        activity.startActivity(viewMap);
    }
}
