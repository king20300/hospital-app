package tw.edu.fju.www.sedia.hospital;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;
import tw.edu.fju.www.sedia.hospital.database.SearchMode;

import static android.Manifest.permission.CALL_PHONE;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Activity activity;
    private List<String[]> dataSet;
    private DBHelper dbHelper;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView hospitalName;
        public TextView hospitalAddress;

        public MyViewHolder(View view) {
            super(view);
            this.hospitalName = view.findViewById(R.id.hospitalName);
            this.hospitalAddress = view.findViewById(R.id.hospitalAddress);
        }
    }

    public MyAdapter(Activity activity, List<String[]> dataSet) {
        this.activity = activity;
        this.dataSet = dataSet;
        this.dbHelper = new DBHelper(activity);
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.list_item, parent, false);
        view.setOnClickListener(v -> {
//            String hospitalName = ((TextView) v.findViewById(R.id.hospitalName)).getText().toString();
            String hospitalAddress = ((TextView) v.findViewById(R.id.hospitalAddress)).getText().toString();

            List<String[]> resultFromSQLite = dbHelper.getResultFromSQLite(null, hospitalAddress, SearchMode.FIND_BY_ADDRESS); // getresultfromsqlite only returns one hospital here, be sure to get index of 0!
            String[] specificHospitalInfo = resultFromSQLite.get(0);
//            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + resultFromSQLite[2]));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (ContextCompat.checkSelfPermission(context, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                context.startActivity(intent);
//            }
//            context.startActivity(intent);

            Intent intent = new Intent(activity, HospitalInfoActivity.class);
            intent.putExtra("hospitalName", specificHospitalInfo[0]);
            intent.putExtra("hospitalAddress", specificHospitalInfo[1]);
//            System.out.println(resultFromSQLite[1]);
            intent.putExtra("hospitalTelephone", specificHospitalInfo[2]);
            intent.putExtra("hospitalId", specificHospitalInfo[3]);

//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);

        });

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.hospitalName.setText(this.dataSet.get(position)[0]);
        holder.hospitalAddress.setText(this.dataSet.get(position)[1]);
    }

    @Override
    public int getItemCount() {
        return this.dataSet.size();
    }
}
