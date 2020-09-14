package tw.edu.fju.www.sedia.hospital;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;
import tw.edu.fju.www.sedia.hospital.database.SearchMode;

public class FavHospitalAdapter extends RecyclerView.Adapter<FavHospitalAdapter.FavHospitalViewHolder> {

    private List<String[]> data;
    private Activity activity;
    private DBHelper dbHelper;
    private View view;

    public FavHospitalAdapter(Activity activity, List<String[]> data) {
        this.data = data;
        this.activity = activity;
        this.dbHelper = new DBHelper(this.activity);
    }

    public class FavHospitalViewHolder extends RecyclerView.ViewHolder {
        private TextView favHospitalName;

        public FavHospitalViewHolder(@NonNull View view) {
            super(view);
            this.favHospitalName = view.findViewById(R.id.favHospitalName);
        }
    }

    @NonNull
    @Override
    public FavHospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_hospital, parent, false);

        return new FavHospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavHospitalViewHolder holder, int position) {
        holder.favHospitalName.setText(this.data.get(position)[1]); // get the hospital name

        view.setOnClickListener(v -> {
            String[] resultFromSQLite = dbHelper.getResultFromSQLite(this.data.get(position)[0], null, SearchMode.FIND_BY_ID).get(0);

            Intent viewHospitalInfo = new Intent(this.activity, HospitalInfoActivity.class);
            viewHospitalInfo.putExtra("hospitalId", this.data.get(position)[0]);
            viewHospitalInfo.putExtra("hospitalName", resultFromSQLite[0]);
            viewHospitalInfo.putExtra("hospitalAddress", resultFromSQLite[1]);
            viewHospitalInfo.putExtra("hospitalTelephone", resultFromSQLite[2]);

            this.activity.startActivity(viewHospitalInfo);
        });
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
