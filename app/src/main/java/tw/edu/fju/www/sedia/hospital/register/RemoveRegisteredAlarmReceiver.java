package tw.edu.fju.www.sedia.hospital.register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import tw.edu.fju.www.sedia.hospital.AlarmReceiver;

public class RemoveRegisteredAlarmReceiver extends BroadcastReceiver {

    private SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        editor = context.getSharedPreferences("registered_notification", Context.MODE_PRIVATE).edit();
        editor.remove(intent.getStringExtra("hospitalId"));
        editor.apply();
    }
}
