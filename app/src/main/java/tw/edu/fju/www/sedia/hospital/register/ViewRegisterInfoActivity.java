package tw.edu.fju.www.sedia.hospital.register;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tw.edu.fju.www.sedia.hospital.HospitalInfoActivity;
import tw.edu.fju.www.sedia.hospital.R;
import tw.edu.fju.www.sedia.hospital.TinyDB;

public class ViewRegisterInfoActivity extends AppCompatActivity {

    private TinyDB tinyDB;
    private ListView registerHospitalListView;

    private static class RegisterHospitalsAdapter extends BaseAdapter {
        private Context context;
        private List<ArrayList<String>> registerHospitalInfo;

        private static class ViewHolder {
            TextView registerHospitalName;
            TextView registerHospitalDate;
            TextView registerHospitalTime;
        }

        public RegisterHospitalsAdapter(Context context, List<ArrayList<String>> registerHospitalInfo) {
            this.context = context;
            this.registerHospitalInfo = registerHospitalInfo;
        }

        @Override
        public int getCount() {
            return this.registerHospitalInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return this.registerHospitalInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(this.registerHospitalInfo.get(position).get(0));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.register_hospital_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.registerHospitalName = convertView.findViewById(R.id.register_hospital_name);
                viewHolder.registerHospitalTime = convertView.findViewById(R.id.register_hospital_time);
                viewHolder.registerHospitalDate = convertView.findViewById(R.id.register_hospital_date);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ArrayList<String> registerInfo = (ArrayList<String>) getItem(position);

            viewHolder.registerHospitalName.setText(registerInfo.get(1));
            viewHolder.registerHospitalDate.setText("您預約的日期為: " + registerInfo.get(2) + "年" + registerInfo.get(3) + "月" + registerInfo.get(4) + "日");
            viewHolder.registerHospitalTime.setText("您預約的時間為: " + registerInfo.get(5) + "點" + registerInfo.get(6) + "分");

            return convertView;
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_register_info);

        registerHospitalListView = findViewById(R.id.register_hospital_listview);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("我的通知");
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.action_bar_background));

        List<ArrayList<String>> registerHospitalsInfo = new ArrayList<>();

        tinyDB = new TinyDB(this);
        tinyDB.getAll().keySet().forEach(key -> {
            ArrayList<String> registerInfo = tinyDB.getListString(key);
            registerHospitalsInfo.add(registerInfo);
        });

        RegisterHospitalsAdapter adapter = new RegisterHospitalsAdapter(this, registerHospitalsInfo);
        registerHospitalListView.setAdapter(adapter);
    }


}