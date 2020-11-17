package tw.edu.fju.www.sedia.hospital;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.transition.Visibility;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import tw.edu.fju.www.sedia.hospital.database.DBHelper;

import static tw.edu.fju.www.sedia.hospital.database.DBHelper.wdb;

public class FetchHospitalInfoUtil extends AsyncTask<String, Integer, Void> {

    private DBHelper dbHelper;
    private ProgressBar progressBar;
    private Activity activity;

    public FetchHospitalInfoUtil(Activity activity, ProgressBar progressBar) {
        this.activity = activity;
        this.dbHelper = DBHelper.getInstance(this.activity);
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setProgress(0);
    }

    @Override
    protected Void doInBackground(String... strings) {
        URL url = null;

        // hospital rows
        try {
            url = new URL(strings[0]);
            Reader csvReader = new InputStreamReader(url.openConnection().getInputStream(), "BIG5");
            StringBuilder csvDataBuilder = new StringBuilder();
            char[] chars = new char[1024];
            int length;
            while((length = csvReader.read(chars)) != -1) {
                csvDataBuilder.append(chars, 0, length);
            }

            String csvData = csvDataBuilder.toString();
            String[] dataRows = csvData.split("\n");

            wdb.beginTransaction();

            Stream.of(dataRows)
                    .skip(1)
                    .forEach(hospital -> {
                        System.out.println(hospital);
                        
                        String[] hospitalColumnValues = hospital.split(",");
                        if (hospitalColumnValues[3].contains("醫院")) {
                            dbHelper.insertData(hospitalColumnValues, true);
                        } else {
                            dbHelper.insertData(hospitalColumnValues, false);
                        }
                        publishProgress((int) ((dbHelper.getCurrentLength() / (double) dataRows.length) * 100));
                    });

            wdb.setTransactionSuccessful();
            wdb.endTransaction();


//            System.out.println(firstRowValues[0]);
//            System.out.println(hospitals.get(0)[1]);
        } catch (IOException urlExc) {
            urlExc.printStackTrace();
        }
//        hospitals.forEach(hospital -> Stream.of(hospital).forEach(System.out::println));
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Intent viewMainPage = new Intent(this.activity, MainActivity.class);
        this.activity.startActivity(viewMainPage);
        this.activity.finish();
    }
}
