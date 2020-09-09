package com.example.projektdyplomowyankiety;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

        Intent goToComplete = new Intent(context, MainMenu.class);
        Bundle b =new Bundle();
        b.putString("surNotName", surveyName);
        goToComplete.putExtras(b);



        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                100,
                goToComplete,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notfyLembuit")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Przyponienie ")
                .setContentText("Wypełnij dzisiaj ankietę o nazwie " + surveyName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());


    }
}







