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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;
import tw.edu.fju.www.sedia.hospital.database.SearchMode;

public class ListMyFavHospitalActivity extends AppCompatActivity {

    private TextView myFavHospitalName;
    private SharedPreferences sharedPreferences;
    private DBHelper dbHelper;
    private RecyclerView favHospitalRecyclerView;
    private FavHospitalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_my_fav_hospital);

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
        favHospitalRecyclerView = findViewById(R.id.favHospitalRecyclerView);

        favHospitalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favHospitalRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        dbHelper = DBHelper.getInstance(this);
        myFavHospitalName = findViewById(R.id.favHospitalName);

        sharedPreferences = getSharedPreferences("HospitalInfoActivity", Context.MODE_PRIVATE);
        Map<String, ?> favHospitalIds = sharedPreferences.getAll();

        List<String[]> hospitals = new ArrayList<>();

        favHospitalIds.values().iterator()
                .forEachRemaining(id -> {
                    String[] hospital = dbHelper.getResultFromSQLite((String) id, null, SearchMode.FIND_BY_ID).get(0);
                    hospitals.add(new String[]{(String) id, hospital[0], hospital[1], hospital[2]});
                });

        if (hospitals.size() == 0) {
            Toast.makeText(this, "你還沒有加入醫院到我的最愛哦～", Toast.LENGTH_SHORT).show();
        }

        adapter = new FavHospitalAdapter(this, hospitals);
        favHospitalRecyclerView.setAdapter(adapter);

//        String[] Ids = favHospitalIds.toArray(new String[favHospitalIds.size()]);
//

//
//        for (String id : Ids) {
//            String[] hospital = dbHelper.getResultFromSQLite(id, null, SearchMode.FIND_BY_ID).get(0);
//            hospitals.add(new String[]{id, hospital[0], hospital[1], hospital[2]});
//        }
//
//        adapter = new FavHospitalAdapter(this, hospitals);
//        favHospitalRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listFavHospital();
    }
}