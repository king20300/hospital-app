package tw.edu.fju.www.sedia.hospital.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tw.edu.fju.www.sedia.hospital.MainActivity;
import tw.edu.fju.www.sedia.hospital.R;
import tw.edu.fju.www.sedia.hospital.TinyDB;

public class ViewRegisterInfoActivity extends AppCompatActivity {

    private TinyDB tinyDB;
    private ActionBar actionBar;
    private ListView registerNotificationList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_register_info);

        registerNotificationList = findViewById(R.id.register_hospital_listview);

        List<ArrayList<String>> registeredHospitalInfo = new ArrayList<>();
        tinyDB = new TinyDB(this, "registered_notification");
        tinyDB.getAll().keySet().forEach(key -> {
            ArrayList<String> registeredHospital = tinyDB.getListString(key);
            registeredHospitalInfo.add(registeredHospital);
        });

        initActionBar();
        showRegisteredNotification(registeredHospitalInfo);
    }

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar_for_register_history);
        toolbar.setBackgroundColor(Color.rgb(255, 165, 0));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void showRegisteredNotification(List<ArrayList<String>> registeredHospitalInfo) {
        RegisteredHospitalAdapter adapter = new RegisteredHospitalAdapter(this, registeredHospitalInfo);
        registerNotificationList.setAdapter(adapter);
    }

    private static class RegisteredHospitalAdapter extends BaseAdapter {
        private Context context;
        private List<ArrayList<String>> registerHospital;

        public RegisteredHospitalAdapter(Context context, List<ArrayList<String>> registerHospital) {
            this.context = context;
            this.registerHospital = registerHospital;
        }

        private class ViewHolder {
            TextView registerHospitalName;
            TextView registerHospitalDate;
            TextView registerHospitalTime;
        }


        @Override
        public int getCount() {
            return this.registerHospital.size();
        }

        @Override
        public Object getItem(int position) {
            return this.registerHospital.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(this.registerHospital.get(position).get(0));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(this.context).inflate(R.layout.register_notification_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.registerHospitalName = convertView.findViewById(R.id.reg_hospital_name);
                viewHolder.registerHospitalDate = convertView.findViewById(R.id.reg_hospital_date);
                viewHolder.registerHospitalTime = convertView.findViewById(R.id.reg_hospital_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ArrayList<String> registerNotificationInfo = (ArrayList<String>) getItem(position);
            viewHolder.registerHospitalName.setText(registerNotificationInfo.get(1));
            viewHolder.registerHospitalDate.setText(registerNotificationInfo.get(3) + "年" + registerNotificationInfo.get(4) + "月" + registerNotificationInfo.get(5) + "日");
            viewHolder.registerHospitalTime.setText(registerNotificationInfo.get(6) + "點" + registerNotificationInfo.get(7) + "分");

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        return super.onOptionsItemSelected(item);
    }
}