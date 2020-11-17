package tw.edu.fju.www.sedia.hospital;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;

import static android.content.ContentValues.TAG;

public class WelcomePageActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FetchHospitalInfoUtil fetchHospitalInfoUtil;
    private DBHelper dbHelper;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        progressBar = findViewById(R.id.loadingProgressBar);
        progressBar.setMax(100);
        progressBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

        dbHelper = DBHelper.getInstance(this);

        System.out.println(dbHelper.getInsertedDataQuantity());
        if (dbHelper.getInsertedDataQuantity() < 20000) {
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
        AlertDialog.Builder builder = null;

        if (!isConnected) {
            builder = new AlertDialog.Builder(this)
                    .setTitle("您似乎尚未連接網路哦")
                    .setNegativeButton("否，我不需要", (dialog, id) -> {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });
        }

        if (builder != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                builder
                        .setMessage("要幫您開啟WIFI嗎?")
                        .setPositiveButton("是，請幫我開啟", (dialog, id) -> {
                            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            wifiManager.setWifiEnabled(true);
                        });
            } else {
                builder.setMessage("您要開啟WIFI嗎?")
                        .setPositiveButton("是，我要開啟", (dialog, id) -> {
                            startActivity(new Intent(Settings.Panel.ACTION_WIFI));
                        });
            }
            alertDialog = builder.show();
        }

        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                if (alertDialog != null) alertDialog.dismiss();
                fetchHospitalInfoUtil = new FetchHospitalInfoUtil(WelcomePageActivity.this, progressBar);
                fetchHospitalInfoUtil.execute("https://www.mohw.gov.tw/dl-61786-4bd02cd5-3d91-4f99-9556-363233aa8059.html");
            }
        });
    }
}
