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

public class GetNearbyUtil extends AsyncTask<Double, Integer, String> {
    private String apiKey;
    private OkHttpClient client;
    private String requestUrl;
    private WeakReference<Activity> weakReferenceContext;
    private double[] userLocation = new double[2];

    public GetNearbyUtil(Activity activity) {
        this.weakReferenceContext = new WeakReference<>(activity);
    }

    @Override
    protected String doInBackground(Double... location) {
        userLocation[0] = location[0];
        userLocation[1] = location[1];

        apiKey = this.weakReferenceContext.get().getResources().getString(R.string.tomtom_api_key);
        client = new OkHttpClient();
        requestUrl = "https://api.tomtom.com/search/2/nearbySearch/.json?lat=" + location[0] + "&lon=" + location[1] + "&limit=30&countrySet=TW&radius=10000&language=zh-TW&categorySet=7321&key=" + apiKey;

        String result = null;
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();

        try {
            result = client.newCall(request).execute().body().string();

            System.out.println(result);

            FileWriter writer = new FileWriter("/data/data/tw.edu.fju.www.sedia.hospital/nearby-info.txt");
            writer.write(result);
            writer.flush();
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
