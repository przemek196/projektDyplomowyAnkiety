package com.example.projektdyplomowyankiety;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private CardView card1;
    private CardView card2;
    private CardView card3;
    private LinearLayout linLaycheck;
    private ImageView im1;

    private Button btnSetNotific;
    private List<String> daysToFire = new ArrayList<>();
    private List<String> requestCodeOfNotif;

    private FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;
    TextView tvSurvName;
    TextView tvSetTime;
    Context mContext;
    Calendar timeOfReminder = Calendar.getInstance();
    String stringDays = "";
  //  TextView TvSetTimeG;
    String[] days = new String[]{
            "Poniedziałek",
            "Wtorek",
            "Środa",
            "Czwartek",
            "Piątek",
            "Sobota",
            "Niedziela"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notyfication);

     //   getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolbar)));
        tvSurvName = (TextView) findViewById(R.id.set_surv_not_name);
        tvSetTime = (TextView) findViewById(R.id.set_time);
        card1 = (CardView) findViewById(R.id.cardView1);
        card2 = (CardView) findViewById(R.id.cardView2);
        card3 = (CardView) findViewById(R.id.cardView3);
        linLaycheck = (LinearLayout) findViewById(R.id.linlaycheck);
       // im1 = (ImageView) findViewById(R.id.im1);
        //TvSetTimeG = new TextView(this);

        //tvsetdays = (TextView) findViewById(R.id.set_days_of_week);
        btnSetNotific = (Button) findViewById(R.id.btnCreateNotif);
        mContext = this;
        createNotificationChannel();

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

/*
        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
*/
        card1.setOnClickListener(new View.OnClickListener() {
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
                    }
                });
            }
        });


        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        tvSetTime.setText(convertDate(selectedHour) + ":" + convertDate(selectedMinute));
                        //TvSetTimeG.setText(selectedHour + ":" + selectedMinute);
                        timeOfReminder.set(Calendar.HOUR_OF_DAY, selectedHour);
                        timeOfReminder.set(Calendar.MINUTE, selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        btnSetNotific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (tvSurvName.getText().toString().matches("Kliknij, aby wybrać")
                        || tvSetTime.getText().toString().matches("Kliknij, aby wybrać")) {
                    Toast.makeText(mContext, "Podaj wszystkie dane.", Toast.LENGTH_SHORT).show();
                    return;
                }


                final int childCount = linLaycheck.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View v_check = linLaycheck.getChildAt(i);

                    if (v_check instanceof CheckBox) {

                        if (((CheckBox) v_check).isChecked()) {
                            switch (((CheckBox) v_check).getText().toString()) {
                                case "Poniedziałek":
                                    daysToFire.add("0");
                                    stringDays += days[0] + ",";
                                    break;
                                case "Wtorek":
                                    daysToFire.add("1");
                                    stringDays += days[1] + ",";
                                    break;
                                case "Środa":
                                    daysToFire.add("2");
                                    stringDays += days[2] + ",";
                                    break;
                                case "Czwartek":
                                    daysToFire.add("3");
                                    stringDays += days[3] + ",";
                                    break;
                                case "Piątek":
                                    daysToFire.add("4");
                                    stringDays += days[4] + ",";
                                    break;
                                case "Sobota":
                                    daysToFire.add("5");
                                    stringDays += days[5] + ",";
                                    break;
                                case "Niedziela":
                                    daysToFire.add("6");
                                    stringDays += days[6] + ",";
                                    break;
                            }
                        }
                    }
                }

                if(daysToFire.size() == 0)
                {
                    Toast.makeText(mContext, "Wybierz przynajmniej jeden dzień", Toast.LENGTH_SHORT).show();
                    return;
                }

                stringDays = removeLastCharacter(stringDays);
                String path = "Users/" + currentFirebaseUser.getUid() + "/Created_Notif";
                Map<String, Object> obj = new HashMap<>();

                obj.put("days", stringDays);
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

                Intent returnIntent = new Intent();
                returnIntent.putExtra("surName", tvSurvName.getText().toString());
                returnIntent.putExtra("notTime", tvSetTime.getText().toString());
                returnIntent.putExtra("days", stringDays);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }
        });
    }

    public String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    public static String removeLastCharacter(String str) {
        String result = null;
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 1);
        }
        return result;
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

    AlertDialog.Builder builder;

    private void choseSurey(String[] surNames) {


            builder = new AlertDialog.Builder(mContext);
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
