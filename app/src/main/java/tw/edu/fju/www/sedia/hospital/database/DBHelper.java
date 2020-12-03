package tw.edu.fju.www.sedia.hospital.database;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DBVersion = 1;
    public static final String DBName = "hospital_info";
    public static SQLiteDatabase wdb;
    public static SQLiteDatabase rdb;
    private static DBHelper instance;
    private double currentLength;

//    public static final String tableName = "hospital";

    private DBHelper(Activity activity) {
        super(activity, DBName, null, DBVersion);
        wdb = this.getWritableDatabase();
        rdb = this.getReadableDatabase();
    }

    public static DBHelper getInstance(Activity activity) {
        if (instance == null) {
            instance = new DBHelper(activity);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        List<String[]> hospitals = this.getData();

        final String sql = "CREATE TABLE hospital (_id Text PRIMARY KEY,hospital_name Text,hospital_belong Text," +
                "hospital_type Text,location Text,telephone Numeric,address Text,medical_department Text,physician INTEGER," +
                "traditional_chinese_physician INTEGER,dentist INTEGER,pharmacist INTEGER,pharmacy_intern INTEGER," +
                "registered_professional_nurse INTEGER,registered_nurses INTEGER,midwife_intern INTEGER,midwife INTEGER," +
                "medical_examiner INTEGER,medical_examiner_intern INTEGER,physical_pherapist INTEGER,occupational_pherapist INTEGER," +
                "medical_radiologist INTEGER,medical_radiologist_intern INTEGER,physical_pherapist_intern INTEGER," +
                "occupational_pherapist_intern INTEGER,respiratory_therapist INTEGER,counseling_psychologist INTEGER," +
                "clinical_psychologist INTEGER,nutritionist INTEGER,speech_therapist INTEGER,dental_technician INTEGER," +
                "audiologist INTEGER,dental_technician_intern INTEGER,optometrist INTEGER,optometrist_intern INTEGER, hasDivision INTEGER)";
        db.execSQL("DROP TABLE IF EXISTS hospital");
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertData(String[] hospital, boolean hasDivision) {


        StringBuilder sql = new StringBuilder("INSERT INTO hospital VALUES (");

        Stream.of(hospital).forEach(columnValue -> {
            if (columnValue.equals("")) {
                sql.append("'無'").append(",");
            } else {
                sql.append("'").append(columnValue).append("'").append(",");
            }
        });

        String result = sql.substring(0, sql.length() - 1);
        try {
//            wdb.execSQL(result + ");");
            if (hasDivision) {
                wdb.execSQL(result + ", 1" + ");");
            } else {
                wdb.execSQL(result + ", 0" + ");");
            }
        } catch (SQLiteException exc) {
            System.out.println(exc.getMessage());
        }
        ++currentLength;
    }

//    public void updateData() {
//        wdb.delete("hospital", null, null);
//        this.insertData();
//    }

    public List<String[]> getResultFromSQLite(@Nullable String id, @Nullable String searchString, SearchMode searchMode) {
        List<String[]> results = null;
        Cursor cursor = null;

        switch(searchMode) {
            case FIND_BY_ADDRESS:
                assert searchString != null;

                cursor = rdb.rawQuery("SELECT _id, hospital_name, address, telephone, hasDivision from hospital WHERE hospital_name LIKE '%" + searchString + "%' OR address LIKE '%" + searchString + "%';",
                        null);

                if (cursor.getColumnCount() != 0) {
                    results = new ArrayList<>();
                    while(cursor.moveToNext()) {
                        String[] hospitalInfo = new String[5];
                        hospitalInfo[0] = cursor.getString(1); // hospital name
                        hospitalInfo[1] = cursor.getString(2); // hospital address
                        hospitalInfo[2] = cursor.getString(3); // hospital telephone
                        hospitalInfo[3] = cursor.getString(0); // hospital id
                        hospitalInfo[4] = String.valueOf(cursor.getInt(4));
                        results.add(hospitalInfo);
                    }
                }
                break;
            case FIND_BY_ID:
                assert id != null;

                results = new ArrayList<>();
                cursor = rdb.rawQuery("SELECT hospital_name, address, telephone, hasDivision from hospital WHERE _id IS " + id + ";", null);
                while(cursor.moveToNext()) {
                    String[] fullInfo = new String[4];
                    fullInfo[0] = cursor.getString(0);
                    fullInfo[1] = cursor.getString(1);
                    fullInfo[2] = cursor.getString(2);
                    fullInfo[3] = String.valueOf(cursor.getInt(3));
                    results.add(fullInfo);
                }
        }

        cursor.close();
        return results;
    }

//    public void getDataFromSQLite() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("select * from hospital", null);
//        cursor.move(3);
//        String result = cursor.getString(1);
//        System.out.println(result);
//        cursor.close();
//    }

    public double getCurrentLength() {
        return currentLength;
    }

    public int getInsertedDataQuantity() {
        return rdb.rawQuery("select * from hospital", null).getCount();
    }
}
