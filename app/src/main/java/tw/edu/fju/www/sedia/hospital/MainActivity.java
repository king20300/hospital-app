package tw.edu.fju.www.sedia.hospital;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

import tw.edu.fju.www.sedia.hospital.map.GetNearbyUtil;
import tw.edu.fju.www.sedia.hospital.register.ViewRegisterHistoryActivity;
import tw.edu.fju.www.sedia.hospital.register.ViewRegisterInfoActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button searchBtn;
    private Button favBtn;
    private Button nearbyBtn;
    private Button myRegisterHistoryBtn;
    private Button myNotificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (searchBtn = findViewById(R.id.search_btn)).setOnClickListener(this);
        (favBtn = findViewById(R.id.favorites_btn)).setOnClickListener(this);
        (nearbyBtn = findViewById(R.id.nearby_btn)).setOnClickListener(this);
        (myRegisterHistoryBtn = findViewById(R.id.my_register_history_btn)).setOnClickListener(this);
        (myNotificationBtn = findViewById(R.id.my_notification_btn)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                Intent intent = new Intent(this, SearchHospitalActivity.class);
                intent.putExtra("caller", "main_activity");
                startActivity(intent);
                break;
            case R.id.favorites_btn:
                startActivity(new Intent(this, ListMyFavHospitalActivity.class));
                break;
            case R.id.nearby_btn:
                requestPermission();
                break;
            case R.id.my_register_history_btn:
                startActivity(new Intent(this, ViewRegisterHistoryActivity.class));
                break;
            case R.id.my_notification_btn:
                startActivity(new Intent(this, ViewRegisterInfoActivity.class));
                break;
        }
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