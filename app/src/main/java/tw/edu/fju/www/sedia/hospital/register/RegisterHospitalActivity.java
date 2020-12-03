package tw.edu.fju.www.sedia.hospital.register;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

import tw.edu.fju.www.sedia.hospital.AlarmReceiver;
import tw.edu.fju.www.sedia.hospital.HospitalInfoActivity;
import tw.edu.fju.www.sedia.hospital.R;
import tw.edu.fju.www.sedia.hospital.TinyDB;

public class RegisterHospitalActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView selectedDate;
    private TextView selectedTime;
    private Button selectDateBtn;
    private Button selectTimeBtn;
    private Button confirmBtn;
    private RadioGroup radioGroup;
    private Spinner divisionSpinner;
    private Calendar calendar = Calendar.getInstance();
    private TinyDB tinyDBForNotification;
    private TinyDB tinyDBForRegisterHistory;
    private AlarmManager alarmManager;
    private Toolbar toolbar;

    private String hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalTelephone;
    private String hospitalDivision;
    private String hasDivision;
    private String caller;
    private ArrayAdapter<CharSequence> divisionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_hospital);

        // 初始化Service
        alarmManager = getSystemService(AlarmManager.class);

        tinyDBForNotification = new TinyDB(getApplicationContext(), "registered_notification");
        tinyDBForRegisterHistory = new TinyDB(getApplicationContext(), "registered_history");

        getIntentExtra();
        initView();
        initActionBar();
        showSpinnerIfHasDivision();
    }

    private void initView() {
        selectedDate = findViewById(R.id.selected_date);
        selectedTime = findViewById(R.id.selected_time);

        selectDateBtn = findViewById(R.id.select_date_btn);
        selectTimeBtn = findViewById(R.id.select_time_btn);
        confirmBtn = findViewById(R.id.confirm_btn);
        radioGroup = findViewById(R.id.radio_group);
        toolbar = findViewById(R.id.toolbar_for_register_hospital);

        // 設置監聽器
        selectDateBtn.setOnClickListener(this);
        selectTimeBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        divisionSpinner = findViewById(R.id.division_spinner);
    }

    private void initActionBar() {
        toolbar.setBackgroundColor(Color.rgb(255, 165, 0));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void showSpinnerIfHasDivision() {
        // 如果該醫院有多個科別，Spinner才會顯示在畫面上，否則將Spinner Remove掉
        if (hasDivision.equals("1")) {
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.option_surgical) {
                    divisionAdapter = ArrayAdapter.createFromResource(RegisterHospitalActivity.this, R.array.surgical_department, android.R.layout.simple_spinner_dropdown_item);
                    divisionSpinner.setAdapter(divisionAdapter);
                } else {
                    divisionAdapter = ArrayAdapter.createFromResource(RegisterHospitalActivity.this, R.array.medical_department, android.R.layout.simple_spinner_dropdown_item);
                    divisionSpinner.setAdapter(divisionAdapter);
                }
            });

            divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    hospitalDivision = divisionAdapter.getItem(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            radioGroup.setVisibility(View.GONE);
            divisionSpinner.setVisibility(View.GONE);
        }
    }

    private void getIntentExtra() {
        caller = getIntent().getStringExtra("caller");
        hospitalId = getIntent().getStringExtra("hospitalId");
        hospitalName = getIntent().getStringExtra("hospitalName");
        hospitalAddress = getIntent().getStringExtra("hospitalAddress");
        hospitalTelephone = getIntent().getStringExtra("hospitalTelephone");
        hasDivision = getIntent().getStringExtra("hasDivision");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_date_btn:
                DialogFragment datePicker = new DatePickerFragment(selectedDate, calendar);
                datePicker.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.select_time_btn:
                DialogFragment timePicker = new TimePickerFragment(selectedTime, calendar);
                timePicker.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.confirm_btn:
                recordingToTinyDB();
                break;
        }
    }

    private void recordingToTinyDB() {
        calendar.set(Calendar.SECOND, 0);

        // 預約時間
        ArrayList<String> registerHospitalInfo =
                new ArrayList<>(Arrays.asList(hospitalId, hospitalName, hospitalDivision,
                        String.valueOf(calendar.get(Calendar.YEAR)), String.valueOf(calendar.get(Calendar.MONTH) + 1),
                        String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)),
                        String.valueOf(calendar.get(Calendar.MINUTE))
                ));

        // tiny db記錄預約時間
        tinyDBForNotification.putListString(hospitalId, registerHospitalInfo);
        tinyDBForRegisterHistory.putListString(hospitalId, registerHospitalInfo);
        setAlarmPendingIntent(registerHospitalInfo);
    }

    private void setAlarmPendingIntent(ArrayList<String> registerHospitalInfo) {
        // 去除醫院ID最後一碼後作為系統廣播的 Request Code
        int requestCode = Integer.parseInt(hospitalId.substring(0, hospitalId.length() - 2));

        Intent removeRegistered = new Intent(this, RemoveRegisteredAlarmReceiver.class);
        removeRegistered.putExtra("hospitalId", hospitalId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, removeRegistered, PendingIntent.FLAG_ONE_SHOT);
        setRemoveRegisteredAlarm(pendingIntent);

        // 傳送包含預約時間的Intent到警報接收器
        Intent receiveAlarm = new Intent(this, AlarmReceiver.class);
        receiveAlarm.putStringArrayListExtra("register_hospital_info", registerHospitalInfo);
        pendingIntent = PendingIntent.getBroadcast(this, requestCode, receiveAlarm, PendingIntent.FLAG_ONE_SHOT);
        setSendNotificationAlarm(pendingIntent);
    }

    private void setRemoveRegisteredAlarm(PendingIntent pendingIntent) {
        // 設置警報器 - 當系統時間超過使用者預約時間後，將該筆預約通知刪除
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void setSendNotificationAlarm(PendingIntent pendingIntent) {
        // 設定發出通知的警報時間為預約時間當天00:00
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(this, "預約成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent backToHospitalInfoPage = new Intent(this, HospitalInfoActivity.class);
        backToHospitalInfoPage.putExtra("caller", caller);
        backToHospitalInfoPage.putExtra("hospitalId", hospitalId);
        backToHospitalInfoPage.putExtra("hospitalName", hospitalName);
        backToHospitalInfoPage.putExtra("hospitalAddress", hospitalAddress);
        backToHospitalInfoPage.putExtra("hospitalTelephone", hospitalTelephone);
        backToHospitalInfoPage.putExtra("hasDivision", hasDivision);
        startActivity(backToHospitalInfoPage);
        return true;
    }
}