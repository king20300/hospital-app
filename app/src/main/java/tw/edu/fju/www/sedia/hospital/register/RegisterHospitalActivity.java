package tw.edu.fju.www.sedia.hospital.register;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

import tw.edu.fju.www.sedia.hospital.AlarmReceiver;
import tw.edu.fju.www.sedia.hospital.R;
import tw.edu.fju.www.sedia.hospital.TinyDB;

public class RegisterHospitalActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView selectedDate;
    private TextView selectedTime;
    private Button selectDateBtn;
    private Button selectTimeBtn;
    private Button confirmBtn;
    private Spinner divisionSpinner;
    private Calendar calendar = Calendar.getInstance();
    private TinyDB tinyDB;

    private String hospitalId;
    private String hospitalName;
    private String hospitalDivision = "無";
    private boolean hasDivision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_hospital);

        // 設置action bar 樣式 / 標題
        getSupportActionBar().setTitle("預約醫院/診所");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.action_bar_background));

        tinyDB = new TinyDB(getApplicationContext());

        getIntentExtra();
        initView();
    }

    private void initView() {
        selectedDate = findViewById(R.id.selected_date);
        selectedTime = findViewById(R.id.selected_time);

        selectDateBtn = findViewById(R.id.select_date_btn);
        selectTimeBtn = findViewById(R.id.select_time_btn);
        confirmBtn = findViewById(R.id.confirm_btn);

        // 設置監聽器
        selectDateBtn.setOnClickListener(this);
        selectTimeBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        divisionSpinner = findViewById(R.id.division_spinner);

        // 如果該醫院有多個科別，Spinner才會顯示在畫面上，否則將Spinner Remove掉
        if (hasDivision) {
            ArrayAdapter<CharSequence> divisionAdapter = ArrayAdapter.createFromResource(this, R.array.divisions_array, android.R.layout.simple_spinner_dropdown_item);
            divisionSpinner.setAdapter(divisionAdapter);

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
            divisionSpinner.setVisibility(View.GONE);
        }
    }

    private void getIntentExtra() {
        hospitalId = getIntent().getStringExtra("hospitalId");
        hospitalName = getIntent().getStringExtra("hospitalName");
        hasDivision = getIntent().getBooleanExtra("hasDivision", false);
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
        // 去除醫院ID最後一碼後作為系統廣播的 Request Code
        int requestCode = Integer.parseInt(hospitalId.substring(0, hospitalId.length() - 2));

        // 預約時間
        ArrayList<String> registerHospitalInfo =
                new ArrayList<>(Arrays.asList(hospitalId, hospitalName, hospitalDivision,
                        String.valueOf(calendar.get(Calendar.YEAR)), String.valueOf(calendar.get(Calendar.MONTH) + 1),
                        String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)),
                        String.valueOf(calendar.get(Calendar.MINUTE))
                ));

        // tiny db記錄預約時間
        tinyDB.putListString(hospitalId, registerHospitalInfo);

        // 傳送包含預約時間的Intent到警報接收器
        Intent receiveAlarm = new Intent(this, AlarmReceiver.class);
        receiveAlarm.putStringArrayListExtra("register_hospital_info", registerHospitalInfo);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, receiveAlarm, PendingIntent.FLAG_ONE_SHOT);

        // 設置警報
        setAlarm(pendingIntent);
    }

    private void setAlarm(PendingIntent pendingIntent) {
        AlarmManager alarmManager = getSystemService(AlarmManager.class);
        // 設定警報時間為預約時間當天00:00
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(this, "預約成功", Toast.LENGTH_SHORT).show();
    }
}