package tw.edu.fju.www.sedia.hospital;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;

public class FavHospitalAdapter extends BaseAdapter {

    private List<String[]> data;
    private Activity activity;
    private DBHelper dbHelper;
    private View view;

    public FavHospitalAdapter(Activity activity, List<String[]> data) {
        this.data = data;
        this.activity = activity;
        this.dbHelper = DBHelper.getInstance(this.activity);
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = this.activity.getLayoutInflater();
        convertView = layoutInflater.inflate(R.layout.favorite_hospital, null);


        ImageView myFavEdit = convertView.findViewById(R.id.my_fav_edit_image_view);
        TextView hospitalName = convertView.findViewById(R.id.hospital_name);
        hospitalName.setText(this.data.get(position)[1]);
        return convertView;
    }




//    @NonNull
//    @Override
//    public FavHospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.favorite_hospital, parent, false);
//
//        return new FavHospitalViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull FavHospitalViewHolder holder, int position) {
//        holder.favHospitalName.setText(this.data.get(position)[1]); // get the hospital name
//
//        view.setOnClickListener(v -> {
//            String[] resultFromSQLite = dbHelper.getResultFromSQLite(this.data.get(position)[0], null, SearchMode.FIND_BY_ID).get(0);
//
//            Intent viewHospitalInfo = new Intent(this.activity, HospitalInfoActivity.class);
//            viewHospitalInfo.putExtra("hospitalId", this.data.get(position)[0]);
//            viewHospitalInfo.putExtra("hospitalName", resultFromSQLite[0]);
//            viewHospitalInfo.putExtra("hospitalAddress", resultFromSQLite[1]);
//            viewHospitalInfo.putExtra("hospitalTelephone", resultFromSQLite[2]);
//
//            this.activity.startActivity(viewHospitalInfo);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return this.data.size();
//    }
}
