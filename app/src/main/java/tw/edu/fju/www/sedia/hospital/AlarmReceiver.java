package tw.edu.fju.www.sedia.hospital;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

import tw.edu.fju.www.sedia.hospital.register.ViewRegisterInfoActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private String hospitalId;
    private String hospitalName;
    private String registerHour;
    private String registerMinute;
    private String registerDivision;

    private void initRegisterInfo(Intent intent) {
        ArrayList<String> registerHospitalInfo = intent.getStringArrayListExtra("register_hospital_info");
        hospitalId = registerHospitalInfo.get(0);
        hospitalName = registerHospitalInfo.get(1);
        registerDivision = registerHospitalInfo.get(2);
        registerHour = registerHospitalInfo.get(6);
        registerMinute = registerHospitalInfo.get(7);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        initRegisterInfo(intent);

        String channelId = hospitalId.substring(0, hospitalId.length() - 2);
        createNotificationChannel(context, channelId);
    }

    private void createNotificationChannel(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "register_hospital_time", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        sendNotification(context, channelId);
    }

    private void sendNotification(Context context, String channelId) {
        notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("請記得前往" + hospitalName + "看診哦!")
                .setContentText("您已預約於今日前往" + hospitalName + "看診，提醒您記得攜帶健保卡前往看診!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        "您已預約於今日" + registerHour + "點" + registerMinute + "分於" +
                        (registerDivision == null ? hospitalName : hospitalName + "的" + registerDivision) +
                        "看診，提醒您記得攜帶健保卡前往看診!"
                        )
                ).setContentIntent(
                        PendingIntent.getActivity(context,
                                Integer.parseInt(channelId),
                                new Intent(context, ViewRegisterInfoActivity.class),
                                PendingIntent.FLAG_ONE_SHOT)
                );


        notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Integer.parseInt(channelId), notificationBuilder.build());
    }
}
