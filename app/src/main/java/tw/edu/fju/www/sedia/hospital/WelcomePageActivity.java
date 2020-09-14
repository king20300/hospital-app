package tw.edu.fju.www.sedia.hospital;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;

public class WelcomePageActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FetchHospitalInfoUtil fetchHospitalInfoUtil;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        progressBar = findViewById(R.id.loadingProgressBar);
        progressBar.setMax(100);
        progressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

        dbHelper = new DBHelper(this);

        if (dbHelper.getInsertedDataQuantity() == 0) {
            initHospitalData();
        } else {
            progressBar.setVisibility(View.GONE);
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Intent viewMainPage = new Intent(this, MainActivity.class);
                    startActivity(viewMainPage);

                    finish(); // 銷毀歡迎畫面，避免使用者重新回到此頁面
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void initHospitalData() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnected();
        if (!isConnected) {
            Toast.makeText(this, "請先開啟wifi哦", Toast.LENGTH_SHORT).show();
        }

        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                fetchHospitalInfoUtil = new FetchHospitalInfoUtil(WelcomePageActivity.this, progressBar);
                fetchHospitalInfoUtil.execute("https://www.mohw.gov.tw/dl-61786-4bd02cd5-3d91-4f99-9556-363233aa8059.html");
            }
        });
    }
}
