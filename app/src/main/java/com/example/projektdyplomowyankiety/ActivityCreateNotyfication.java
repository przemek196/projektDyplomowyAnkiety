package com.example.projektdyplomowyankiety;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ActivityCreateNotyfication extends AppCompatActivity {


    private RelativeLayout relativeLayoutSurvey;
    private RelativeLayout relativeLayoutTime;
    private RelativeLayout relativeLayoutDays;
    private Button btnSetNotific;
    private List<String> daysToFire = new ArrayList<>();
    private List<String> requestCodeOfNotif;

    private FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;
    TextView tvSurvName;
    TextView tvSetTime;
    TextView tvsetdays;
    Context mContext;
    Calendar timeOfReminder = Calendar.getInstance();


    String[] days = new String[]{
            "Poniedziałek",
            "Wtorek",
            "Sroda",
            "Czwartek",
            "Piątek",
            "Sobota",
            "Niedziela"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notyfication);

        relativeLayoutSurvey = (RelativeLayout) findViewById(R.id.surveyNameLay);
        relativeLayoutTime = (RelativeLayout) findViewById(R.id.timeRelLay);
        relativeLayoutDays = (RelativeLayout) findViewById(R.id.daysRelLay);
        tvSurvName = (TextView) findViewById(R.id.set_surv_not_name);
        tvSetTime = (TextView) findViewById(R.id.set_time);
        tvsetdays = (TextView) findViewById(R.id.set_days_of_week);
        btnSetNotific = (Button) findViewById(R.id.btnCreateNotif);
        mContext = this;
        createNotificationChannel();

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        relativeLayoutSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get all survey names and chose
                String path = "Users/" + currentFirebaseUser.getUid() + "/Created_Survey";

                db.collection(path).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String[] surNames = new String[queryDocumentSnapshots.size()];
                        int i = 0;
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            surNames[i] = q.getId();
                            i++;
                        }

                        deleteNoNecesarry(surNames);


                        // choseSurey(surNames);
                    }
                });

            }
        });
        relativeLayoutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tvSetTime.setText(selectedHour + ":" + selectedMinute);

                        timeOfReminder.set(Calendar.HOUR_OF_DAY, selectedHour);
                        timeOfReminder.set(Calendar.MINUTE, selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        relativeLayoutDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                // String array for alert dialog multi choice items
                final boolean[] checkedDays = new boolean[7];

                List<String> dayList = Arrays.asList(days);
                builder.setMultiChoiceItems(days, checkedDays, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        checkedDays[which] = isChecked;
                        String currentItem = dayList.get(which);
                    }
                });

                builder.setCancelable(false);
                builder.setTitle("Wybierz dni tygodnia.");

                // Set the positive/yes button click listener
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click positive button
                        String s = "";
                        daysToFire.clear();
                        for (int i = 0; i < checkedDays.length; i++) {

                            if (checkedDays[i] == true) {
                                if (s.matches("")) {
                                    s += days[i];
                                    daysToFire.add(Integer.toString(i));
                                    continue;
                                }
                                s += "," + days[i];
                                daysToFire.add(Integer.toString(i));
                            }
                        }
                        tvsetdays.setText(s);
                    }
                });

                builder.setNeutralButton("Wróc", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //add notification and save data to database
        btnSetNotific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvsetdays.getText().toString().matches("") || tvSurvName.getText().toString().matches("")
                        || tvSetTime.getText().toString().matches("")) {
                    Toast.makeText(mContext, "Podaj wszystkie dane.", Toast.LENGTH_SHORT).show();
                    return;
                }


                String path = "Users/" + currentFirebaseUser.getUid() + "/Created_Notif";
                Map<String, Object> obj = new HashMap<>();

                obj.put("days", tvsetdays.getText());
                obj.put("notificationTime", tvSetTime.getText().toString());
                db.collection(path).document(tvSurvName.getText().toString()).set(obj);

                Random r = new Random();

                for (String s : daysToFire) {

                    Date today = Calendar.getInstance().getTime();
                    Map<String, Object> hashCode = new HashMap<>();
                    int requestCode = r.nextInt(1000000 - 1) + 1;
                    hashCode.put("hashCode", requestCode);
                    db.collection(path).document(tvSurvName.getText().toString()).collection("days").document(days[Integer.parseInt(s)]).set(hashCode);

                    Date setTime = Calendar.getInstance().getTime();
                    setTime.setHours(timeOfReminder.getTime().getHours());
                    setTime.setMinutes(timeOfReminder.getTime().getMinutes());

                    if (today.getDay() > Integer.valueOf(s) + 1) {
                        setTime.setDate((today.getDate() - (Integer.parseInt(s) + 1)) + 7);
                    }
                    if (today.getDay() < Integer.valueOf(s) + 1) {
                        int a = (Integer.parseInt(s) + 1) - today.getDay();
                        setTime.setDate(today.getDate() + a);
                    }

                    long timeBetweenDates = setTime.getTime() - today.getTime();

                    Intent intent = new Intent(mContext, ReminderBroadcast.class);
                    intent.putExtra("requestCode", requestCode);
                    intent.putExtra("surveyName", tvSurvName.getText().toString());
                    //intent.putExtra("dayOfWeek", dayOfWeek);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    long timeAtButtonClick = System.currentTimeMillis();

                    alarmManager.setRepeating(alarmManager.RTC_WAKEUP, timeAtButtonClick + timeBetweenDates, alarmManager.INTERVAL_DAY * 7, pendingIntent);
                }

           //wrocic do ManageNotificationFragment i zaladowac nowe dane





            }
        });
    }

    private void deleteNoNecesarry(String[] allSurNames) {


        String path = "Users/" + currentFirebaseUser.getUid() + "/Created_Notif";

        db.collection(path).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                String[] surNames = new String[allSurNames.length - queryDocumentSnapshots.size()];
                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    for (int j = 0; j < allSurNames.length; j++) {
                        if (allSurNames[j].matches(q.getId())) {
                            allSurNames[j] = "0";
                            break;
                        }
                    }
                }
                int a = 0;
                for (int i = 0; i < allSurNames.length; i++) {
                    if (!allSurNames[i].matches("0")) {
                        surNames[a] = allSurNames[i];
                        a++;
                    }
                }
                choseSurey(surNames);
            }
        });


    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LemubitReminderChannel";
            String description = "Channel for Lembuit Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notfyLembuit", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }


    private void choseSurey(String[] surNames) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Wybierz ankietę.");
        builder.setItems(surNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvSurvName.setText(surNames[which]);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
