package tw.edu.fju.www.sedia.hospital;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

import tw.edu.fju.www.sedia.hospital.map.GetNearbyUtil;
import tw.edu.fju.www.sedia.hospital.map.MarkerMapsActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private Button searchBtn;
    private Button favBtn;
    private Button nearbyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBtn = findViewById(R.id.search_btn);
        favBtn = findViewById(R.id.favorites_btn);
        nearbyBtn = findViewById(R.id.nearby_btn);

//        requestPermission();
//        handleListHospitalBtnClick();
//        searchHospitalBtnClicked();
        searchBtnClicked();
        favBtnClicked();
        nearbyBtnClicked();
    }

    private void searchBtnClicked() {
        this.searchBtn.setOnClickListener(view -> startActivity(new Intent(this, SearchHospitalActivity.class)));
    }

    private void favBtnClicked() {
        this.favBtn.setOnClickListener(view -> startActivity(new Intent(this, ListMyFavHospitalActivity.class)));
    }

    private void nearbyBtnClicked() {
        this.nearbyBtn.setOnClickListener(view -> requestPermission());
    }

    private void requestPermission() {
        // request for user location
        int i = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);

        if (i == PackageManager.PERMISSION_GRANTED) {
            // get user location
            getUserLocation();
        } else {
            ActivityResultLauncher<String> requestPermissionLauncher = this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getUserLocation();
                } else {
                    Toast.makeText(this, "搜尋附近醫院需要同意取用定位哦", Toast.LENGTH_SHORT).show();
                }
            });
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void getUserLocation() {
        double[] userLocation = new double[2];

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLocation[0] = location.getLatitude();
                        userLocation[1] = location.getLongitude();

                        GetNearbyUtil getNearbyUtil = new GetNearbyUtil(this);
                        getNearbyUtil.execute(userLocation[0], userLocation[1]);
                        try {
                            List<Address> addresses = new Geocoder(this).getFromLocation(userLocation[0], userLocation[1], 1);
                            addresses.forEach(address -> {
                                Toast.makeText(this, address.getAddressLine(0), Toast.LENGTH_SHORT).show();
                            });
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
}