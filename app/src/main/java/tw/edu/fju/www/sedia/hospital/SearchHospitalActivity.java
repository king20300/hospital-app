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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;
import tw.edu.fju.www.sedia.hospital.database.SearchMode;

public class SearchHospitalActivity extends AppCompatActivity {

    private RecyclerView searchResultRecyclerView;
    private RecyclerView.Adapter<MyAdapter.MyViewHolder> searchResultAdapter;
    private DBHelper dbHelper;
    private String selectedDistrict;
    private EditText searchText;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hospital);

        getSupportActionBar().setTitle("搜索醫院");
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.action_bar_background));

        //避免鍵盤影響布局LAYOUT
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        this.selectedDistrict = sharedPref.getString("selectedDistrict", "全部地區");

        dbHelper = DBHelper.getInstance(this);

        initRecyclerView();
        getUserSelectedDistrict();
        getHospitalInfoOnTextChange();
    }

    private void initRecyclerView() {
        this.searchResultRecyclerView = findViewById(R.id.myRecyclerView);
        this.searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.searchResultRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        this.searchResultAdapter = new MyAdapter(this, new ArrayList<>());
        this.searchResultRecyclerView.setAdapter(this.searchResultAdapter);
    }

    private void getUserSelectedDistrict() {
        List<String> districtOptions = Arrays.asList(getResources().getStringArray(R.array.districts_array));
        int indexInXml = districtOptions.indexOf(selectedDistrict);

        Spinner districtsSpinner = findViewById(R.id.districts_spinner);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.districts_array, android.R.layout.simple_spinner_dropdown_item);

        districtsSpinner.setAdapter(adapter);

        districtsSpinner.setSelection(indexInXml);

        districtsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrict = (String) parent.getItemAtPosition(position);
                String queryString = searchText.getText().toString();

                if (queryString.equals("")) {
                    query("");
                } else {
                    query(queryString);
                }

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("selectedDistrict", selectedDistrict);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void query(String searchString) {
        List<String[]> hospitalInfo;

        if (searchString.length() == 0) {
            hospitalInfo = dbHelper.getResultFromSQLite(null, selectedDistrict, SearchMode.FIND_BY_ADDRESS);
            searchResultAdapter = new MyAdapter(this, hospitalInfo);
            searchResultRecyclerView.setAdapter(searchResultAdapter);
            return;
        }

        hospitalInfo = dbHelper.getResultFromSQLite(null, searchString, SearchMode.FIND_BY_ADDRESS);
        if (!selectedDistrict.equals("全部地區")) {
            hospitalInfo = hospitalInfo.stream()
                    .filter(hospital -> hospital[1].startsWith(selectedDistrict))
                    .collect(Collectors.toList());
        }

        searchResultAdapter = new MyAdapter(this, hospitalInfo);
        searchResultRecyclerView.setAdapter(searchResultAdapter);
    }

    private void getHospitalInfoOnTextChange() {
        searchText = findViewById(R.id.searchText);
        searchText.setHint("試著搜尋醫院名稱或是地址吧!");

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query(s.toString());

//                StringBuilder hospitalInfoBuilder = new StringBuilder();
//                hospitalInfo.forEach(info -> {
//                    hospitalInfoBuilder.append("hospital name: ").append(info[0]).append(", address: ").append(info[1]).append("\n");
//                });
//                hospitalTextView.setText(hospitalInfoBuilder.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, ListMyFavHospitalActivity.class));
        return super.onOptionsItemSelected(item);
    }
}