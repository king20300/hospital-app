package tw.edu.fju.www.sedia.hospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class HospitalInfoActivity extends AppCompatActivity {

    private TextView hospitalNameTextView;
    private TextView hospitalAddressTextView;
    private TextView hospitalTelephoneTextView;
    private Button addToMyFavBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_info);

        hospitalNameTextView = findViewById(R.id.hospitalName);
        hospitalAddressTextView = findViewById(R.id.hospitalAddress);
        hospitalTelephoneTextView = findViewById(R.id.hospitalTelephone);
        addToMyFavBtn = findViewById(R.id.addToMyFavBtn);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        onInitHospitalInfo();
        handleFavBtnClicked();
    }

    public void onInitHospitalInfo() {
        Intent intent = getIntent();
        this.hospitalNameTextView.setText(intent.getStringExtra("hospitalName"));
        this.hospitalAddressTextView.setText(intent.getStringExtra("hospitalAddress"));

        this.hospitalTelephoneTextView.setTextColor(Color.BLUE);
        String telephone = intent.getStringExtra("hospitalTelephone");
        SpannableString content = new SpannableString(telephone);
        content.setSpan(new UnderlineSpan(), 0, telephone.length(), 0);
        this.hospitalTelephoneTextView.setText(content);

        this.hospitalTelephoneTextView.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + this.hospitalTelephoneTextView.getText().toString()));
            this.startActivity(i);
        });

        this.hospitalAddressTextView.setTextColor(Color.DKGRAY);
        this.hospitalAddressTextView.setOnClickListener(view -> {
            Uri googleMapUri = Uri.parse("google.navigation:q=" + this.hospitalAddressTextView.getText().toString());
            Intent viewMap = new Intent(Intent.ACTION_VIEW, googleMapUri);
            viewMap.setPackage("com.google.android.apps.maps");
            startActivity(viewMap);
        });
    }

    private void handleFavBtnClicked() {

        String id = getIntent().getStringExtra("hospitalId");
        boolean exist = sharedPreferences.getString(id, null) != null;

        System.out.println(id);
        System.out.println(sharedPreferences.getString(id, null));

        if (exist) {
            addToMyFavBtn.setText("從我的最愛中移除");
        } else {
            addToMyFavBtn.setText("加入我的最愛!");
        }

        addToMyFavBtn.setOnClickListener(view -> {
            if (addToMyFavBtn.getText().toString().equals("從我的最愛中移除")) {
                editor.remove(id);
                editor.commit();

                Toast.makeText(this, "已移除", Toast.LENGTH_SHORT).show();
                addToMyFavBtn.setText("加入我的最愛!");
            } else {
                editor.putString(id, id);
                editor.commit();

                Toast.makeText(this, "已加入我的最愛", Toast.LENGTH_SHORT).show();
                addToMyFavBtn.setText("從我的最愛中移除");
            }
        });
    }
}