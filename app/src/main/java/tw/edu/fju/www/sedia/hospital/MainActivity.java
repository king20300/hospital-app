package tw.edu.fju.www.sedia.hospital;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;
import tw.edu.fju.www.sedia.hospital.database.SearchMode;
import tw.edu.fju.www.sedia.hospital.map.GetNearbyUtil;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<MyAdapter.MyViewHolder> adapter;
    private DBHelper dbHelper;
    private Button listMyFavHospitalBtn;
    private String selectedDistrict;
    private EditText searchText;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        this.selectedDistrict = sharedPref.getString("selectedDistrict", "全部地區");

        dbHelper = new DBHelper(this);


        //避免鍵盤影響布局LAYOUT
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        getHospitalInfoOnTextChange();
        initRecyclerView();
        requestPermission();
        getUserSelectedDistrict();
        handleListHospitalBtnClick();
    }

    private void handleListHospitalBtnClick() {
        listMyFavHospitalBtn = findViewById(R.id.myFavHospital);

        listMyFavHospitalBtn.setOnClickListener(view -> {
            Intent viewMyFavHospital = new Intent(this, ListMyFavHospitalActivity.class);
            startActivity(viewMyFavHospital);
        });
    }

    private void getUserSelectedDistrict() {
        List<String> districtOptions = Arrays.asList(getResources().getStringArray(R.array.districts_array));
        int indexInXml = districtOptions.indexOf(selectedDistrict);

        Spinner districtsSpinner = findViewById(R.id.districts_spinner);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.districts_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtsSpinner.setAdapter(adapter);

        districtsSpinner.setSelection(indexInXml);

        districtsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrict = (String) parent.getItemAtPosition(position);
                String queryString = searchText.getText().toString();

                if (queryString.equals("")) {
                    query("");
                } else {
                    query(queryString);
                }

                Editor editor = sharedPref.edit();
                editor.putString("selectedDistrict", selectedDistrict);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void requestPermission() {
        // request for user location
        Button getUserLocationBtn = findViewById(R.id.getLocationBtn);

        getUserLocationBtn.setOnClickListener(view -> {
            int i = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);

            if (i == PackageManager.PERMISSION_GRANTED) {
                // get user location
                getUserLocation();
            } else {
                ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new RequestPermission(), isGranted -> {
                    double[] latAndLon = isGranted ? getUserLocation() : new double[0];
                });
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public double[] getUserLocation() {
        double[] userLocation = new double[2];

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLocation[0] = location.getLatitude();
                        userLocation[1] = location.getLongitude();

                        GetNearbyUtil getNearbyUtil = new GetNearbyUtil(this);
                        getNearbyUtil.execute(userLocation);

                        try {
                            List<Address> addresses = new Geocoder(this).getFromLocation(userLocation[0], userLocation[1], 1);
                            addresses.forEach(address -> {
                                Toast.makeText(this, address.getAddressLine(0), Toast.LENGTH_SHORT).show();
                            });
                        } catch (IOException ioe) {

                        }
                    }
                });
        return userLocation;
    }

    private void initRecyclerView() {
        this.recyclerView = findViewById(R.id.myRecyclerView);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.super.getApplicationContext(), DividerItemDecoration.VERTICAL));

        this.adapter = new MyAdapter(this, new ArrayList<>());
        this.recyclerView.setAdapter(adapter);
    }

    private void getHospitalInfoOnTextChange() {
        searchText = findViewById(R.id.searchText);
        searchText.setHint("試著搜尋醫院名稱或是地址吧!");

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query(s.toString());

//                StringBuilder hospitalInfoBuilder = new StringBuilder();
//                hospitalInfo.forEach(info -> {
//                    hospitalInfoBuilder.append("hospital name: ").append(info[0]).append(", address: ").append(info[1]).append("\n");
//                });
//                hospitalTextView.setText(hospitalInfoBuilder.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void query(String searchString) {
        List<String[]> hospitalInfo;

        if (searchString.length() == 0) {
            hospitalInfo = dbHelper.getResultFromSQLite(null, selectedDistrict, SearchMode.FIND_BY_ADDRESS);
            adapter = new MyAdapter(this, hospitalInfo);
            recyclerView.setAdapter(adapter);
            return;
        }

        hospitalInfo = dbHelper.getResultFromSQLite(null, searchString, SearchMode.FIND_BY_ADDRESS);
        if (!selectedDistrict.equals("全部地區")) {
            hospitalInfo = hospitalInfo.stream()
                    .filter(hospital -> hospital[1].startsWith(selectedDistrict))
                    .collect(Collectors.toList());
        }

        adapter = new MyAdapter(this, hospitalInfo);
        recyclerView.setAdapter(adapter);
    }
}