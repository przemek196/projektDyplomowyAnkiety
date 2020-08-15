package com.example.projektdyplomowyankiety;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        // Intent i = new Intent(context, ActivityCreateNotyfication.class);
        int code = intent.getIntExtra("requestCode", 1);
        String dayOfWeek = intent.getStringExtra("dayOfWeek");
        String surveyName = intent.getStringExtra("surveyName");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notfyLembuit")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Przyponienie ")
                .setContentText("Wypełnij dzisiaj ankietę o nazwie " + surveyName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());

        Toast.makeText(context, "30 sekund", Toast.LENGTH_SHORT).show();
    }
}







