package tw.edu.fju.www.sedia.hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;
import tw.edu.fju.www.sedia.hospital.database.SearchMode;

public class ListMyFavHospitalActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private DBHelper dbHelper;
    private FavHospitalAdapter adapter;
    private GridView favHospitalGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_my_fav_hospital);

        favHospitalGridView = findViewById(R.id.fav_hospital_gridview);

        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.action_bar_background));
        getSupportActionBar().setTitle("我的最愛");

//        List<String[]> resultFromSQLite = dbHelper.getResultFromSQLite((String)favHospitalIds[0], "", SearchMode.FIND_BY_ID);

//        myFavHospitalName.setText(resultFromSQLite.get(0)[0]);
        listFavHospital();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private void listFavHospital() {
        dbHelper = DBHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("favorite_hospital", Context.MODE_PRIVATE);
        Map<String, ?> favHospitalIds = sharedPreferences.getAll();

        List<String[]> hospitals = new ArrayList<>();

        favHospitalIds.values().iterator()
                .forEachRemaining(id -> {
                    String[] hospital = dbHelper.getResultFromSQLite((String) id, null, SearchMode.FIND_BY_ID).get(0);
                    hospitals.add(new String[]{(String) id, hospital[0], hospital[1], hospital[2], hospital[3]});
                });

        if (hospitals.size() == 0) {
            Toast.makeText(this, "你還沒有加入醫院到我的最愛哦～", Toast.LENGTH_SHORT).show();
        }

        adapter = new FavHospitalAdapter(this, hospitals);
        this.favHospitalGridView.setAdapter(adapter);
        this.favHospitalGridView.setOnItemClickListener((parent, view, position, id) -> {
            String[] hospital = hospitals.get(position);
            Intent viewHospitalInfo = new Intent(this, HospitalInfoActivity.class);
            viewHospitalInfo.putExtra("hospitalId", hospital[0]);
            viewHospitalInfo.putExtra("hospitalName", hospital[1]);
            viewHospitalInfo.putExtra("hospitalAddress", hospital[2]);
            viewHospitalInfo.putExtra("hospitalTelephone", hospital[3]);
            viewHospitalInfo.putExtra("hasDivision", hospital[4]);

            startActivity(viewHospitalInfo);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listFavHospital();
    }
}