package tw.edu.fju.www.sedia.hospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import tw.edu.fju.www.sedia.hospital.register.RegisterHospitalActivity;

public class HospitalInfoActivity extends AppCompatActivity {

    private TextView hospitalNameTextView;
    private TextView hospitalAddressTextView;
    private TextView hospitalTelephoneTextView;
    private Button addToMyFavBtn;
    private Button registerHospitalBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;

    private String hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalTelephone;
    private String caller;
    private String hasDivision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_info);

        caller = getIntent().getStringExtra("caller");

        sharedPreferences = getSharedPreferences("favorite_hospital", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        getIntentExtra();
        initView();
        initActionBar();
        onInitHospitalInfo();
        handleAddToFavBtnClicked();
        openRegisterActivity();
    }

    private void initActionBar() {
        toolbar.setBackgroundColor(Color.rgb(255, 165, 0));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void getIntentExtra() {
        hospitalId = getIntent().getStringExtra("hospitalId");
        hospitalName = getIntent().getStringExtra("hospitalName");
        hospitalAddress = getIntent().getStringExtra("hospitalAddress");
        hospitalTelephone = getIntent().getStringExtra("hospitalTelephone");
        hasDivision = getIntent().getStringExtra("hasDivision");
    }

    private void initView() {
        hospitalNameTextView = findViewById(R.id.hospitalName);
        hospitalAddressTextView = findViewById(R.id.hospitalAddress);
        hospitalTelephoneTextView = findViewById(R.id.hospitalTelephone);
        addToMyFavBtn = findViewById(R.id.addToMyFavBtn);
        registerHospitalBtn = findViewById(R.id.register_hospital);
        toolbar = findViewById(R.id.toolbar_for_hospital_info_activity);
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterHospitalActivity.class);
        intent.putExtra("caller", caller);
        intent.putExtra("hospitalId", hospitalId);
        intent.putExtra("hospitalName", hospitalName);
        intent.putExtra("hospitalAddress", hospitalAddress);
        intent.putExtra("hospitalTelephone", hospitalTelephone);
        intent.putExtra("hasDivision", hasDivision);
        registerHospitalBtn.setOnClickListener(view -> startActivity(intent));
    }

    public void onInitHospitalInfo() {
        Intent intent = getIntent();
        this.hospitalNameTextView.setText(this.hospitalName);
        this.hospitalAddressTextView.setText(hospitalAddress);

        // 設置醫院電話顏色&底線
        this.hospitalTelephoneTextView.setTextColor(Color.BLUE);
        SpannableString content = new SpannableString(hospitalTelephone);
        content.setSpan(new UnderlineSpan(), 0, hospitalTelephone.length(), 0);
        this.hospitalTelephoneTextView.setText(content);

        // 設置醫院地址顏色
        this.hospitalAddressTextView.setTextColor(Color.DKGRAY);
    }

    private void openDefaultPhoneBook() {
        this.hospitalTelephoneTextView.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + this.hospitalTelephoneTextView.getText().toString()));
            this.startActivity(i);
        });
    }

    private void openGooleMaps() {
        this.hospitalAddressTextView.setOnClickListener(view -> {
            Uri googleMapUri = Uri.parse("google.navigation:q=" + this.hospitalAddressTextView.getText().toString());
            Intent viewMap = new Intent(Intent.ACTION_VIEW, googleMapUri);
            viewMap.setPackage("com.google.android.apps.maps");
            startActivity(viewMap);
        });
    }

    private void handleAddToFavBtnClicked() {

        boolean exist = sharedPreferences.getString(hospitalId, null) != null;

        if (exist) {
            addToMyFavBtn.setText("從我的最愛中移除");
        } else {
            addToMyFavBtn.setText("加入我的最愛!");
        }

        addToMyFavBtn.setOnClickListener(view -> {
            if (addToMyFavBtn.getText().toString().equals("從我的最愛中移除")) {
                editor.remove(hospitalId);
                editor.commit();

                Toast.makeText(this, "已移除", Toast.LENGTH_SHORT).show();
                addToMyFavBtn.setText("加入我的最愛!");
            } else {
                editor.putString(hospitalId, hospitalId);
                editor.commit();

                Toast.makeText(this, "已加入我的最愛", Toast.LENGTH_SHORT).show();
                addToMyFavBtn.setText("從我的最愛中移除");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (caller != null) {
            if (caller.equals("search_hospital_activity")) {
                startActivity(new Intent(this, SearchHospitalActivity.class));
            } else {  // come from list my fav hospital activity
                startActivity(new Intent(this, ListMyFavHospitalActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}