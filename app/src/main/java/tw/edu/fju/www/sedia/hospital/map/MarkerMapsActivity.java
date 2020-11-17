package tw.edu.fju.www.sedia.hospital.map;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tw.edu.fju.www.sedia.hospital.R;

public class MarkerMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        String jsonData = getIntent().getStringExtra("jsonData");
        double[] userLocation = getIntent().getDoubleArrayExtra("userLocation");

        JsonElement jsonelement = JsonParser.parseString(jsonData);
        JsonArray resultArray = jsonelement.getAsJsonObject().get("results").getAsJsonArray();
        resultArray.iterator().forEachRemaining(this::resolveJsonData);

        assert userLocation != null;
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(userLocation[0], userLocation[1])));
        map.setMinZoomPreference(12);
//        double lat = getIntent().getDoubleExtra("lat", 0);
//        double lon = getIntent().getDoubleExtra("lon", 0);
//        System.out.println(lat + " " + lon);
//        Marker fjuRes = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("輔園餐廳"));
//        fjuRes.setTag(0);
//        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));


    }

    private void resolveJsonData(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();
        String hospitalName = jsonObject.getAsJsonObject("poi").get("name").getAsString();

        double[] position = new double[2];
        JsonObject jsonPositionData = jsonObject.getAsJsonObject("position");
        position[0] = jsonPositionData.get("lat").getAsDouble();
        position[1] = jsonPositionData.get("lon").getAsDouble();

        map.addMarker(new MarkerOptions().title(hospitalName).position(new LatLng(position[0], position[1])));
    }
}