package com.example.projektdyplomowyankiety;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.projektdyplomowyankiety.R;
import com.example.projektdyplomowyankiety.IOnBackPressed;
import com.example.projektdyplomowyankiety.used_classes.CompleteSurvey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalendarFragment extends Fragment implements IOnBackPressed {

    View view;
    private static final String TAG = "Calendar Fragment";
    private CalendarView calendar;
    List<CompleteSurvey> completeSurvey = new ArrayList<>();
    private LinearLayout mainLinLay;
    private Button btnBack;
    private LinearLayout layAnime;
    private TextView textviewDate;
    private Button btnClearDay;
    private static CalendarFragment mInstance;
    private TextView isNetwork;
    private LinearLayout LinLayAnim;

    @Override
    public boolean onBackPressed() {

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

        return true;
    }


    public static synchronized CalendarFragment getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConectionReciver.ContectivityRecListener listener) {
        ConectionReciver.contectivityRecListener = listener;
    }

    public boolean networkConnection() {
        ConnectivityManager conectivymanager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = conectivymanager.getActiveNetworkInfo();
        if (networkinfo == null) {
            return false;
        } else
            return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Kalendarz ankiet");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar_layout, container, false);
        isNetwork = (TextView) view.findViewById(R.id.Internet);
        calendar = (CalendarView) view.findViewById(R.id.calendar);
        LinLayAnim = (LinearLayout) view.findViewById(R.id.linLayAnim);
        getActivity().setTitle("Kalendarz ankiet");

        if (!networkConnection()) {
            isNetwork.setVisibility(View.VISIBLE);
            calendar.setVisibility(View.GONE);
            LinLayAnim.setVisibility(View.GONE);
        } else {
            isNetwork.setVisibility(View.GONE);
            calendar.setVisibility(View.VISIBLE);
            LinLayAnim.setVisibility(View.VISIBLE);
            mInstance = this;
            mainLinLay = (LinearLayout) view.findViewById(R.id.linlayCal);
            btnBack = (Button) view.findViewById(R.id.backBtn);
            btnClearDay = (Button) view.findViewById(R.id.btnClearDaySurveys);
            textviewDate = (TextView) view.findViewById(R.id.tvDate);

            if (!((MainMenu) getActivity()).isNetworkConnected()) {
                Toast.makeText(getContext(), "Brak sieci. Dane nie zostaną zaktualizowane.", Toast.LENGTH_SHORT).show();
            } else {
                getSurveyNames();
            }


            layAnime = (LinearLayout) view.findViewById(R.id.linLayAnim);
            layAnime.setVisibility(View.GONE);

            btnClearDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (survNamesToDelete.size() == 0) {
                        Toast.makeText(getContext(), getResources().getString(R.string.noSurvToDelete), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    for (String csurv : survNamesToDelete) {

                                        db.collection(path).document(csurv).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                List<String> dates = new ArrayList<>();
                                                List<String> dates1 = new ArrayList<>();
                                                dates = (ArrayList<String>) documentSnapshot.get("dates");

                                                if (dates != null) {

                                                    for (String s : dates) {
                                                        if (s.equals(curDate))
                                                            continue;
                                                        dates1.add(s);
                                                    }

                                                }
                                                Map<String, Object> ob1 = new HashMap<>();
                                                ob1.put("nazwa", csurv);
                                                ob1.put("dates", dates1);
                                                db.collection(path).document(csurv).set(ob1);

                                            }
                                        });

                                        db.collection(path + "/" + csurv + "/" + curDate).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                                                    q.getReference().delete();
                                                }
                                            }
                                        });
                                    }

                                    Toast.makeText(getContext(), getResources().getString(R.string.deleteComplete), Toast.LENGTH_SHORT).show();
                                    getSurveyNames();
                                    showCalendar();

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Czy usunąć ankiety z tego dnia?").setPositiveButton("Tak", dialogClickListener)
                            .setNegativeButton("Nie", dialogClickListener).show();


                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    survNamesToDelete.clear();
                    showCalendar();
                }
            });

            calendar.setOnDayClickListener(new OnDayClickListener() {
                @Override
                public void onDayClick(EventDay eventDay) {
                    Calendar clickedDayCalendar = eventDay.getCalendar();

                    Date cDate = clickedDayCalendar.getTime();
                    String fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);

                    //pobieranie nazw ankiet
                    db.collection(path).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<String> list = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    list.add(document.getId());
                                }
                                if (list.size() > 0) {
                                    setSurvey(list, fDate);
                                }

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }
            });

        }

        return view;
    }

    private void setSurvey(List<String> list, String fDate) {

        for (String survName : list) {
            readSurvey(survName, fDate);
        }
    }

    private void readSurvey(String survName, String fDate) {

        db.collection("Users/" + currentFirebaseUser.getUid() + "/Complete_Survey/" + survName + "/" + fDate)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                completeSurvey = new ArrayList<>();
                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    completeSurvey.add(q.toObject(CompleteSurvey.class));
                }
                if (completeSurvey.size() > 0) {
                    writeSurveyToScroll(completeSurvey, survName, fDate);
                }
            }
        });
    }

    private List<String> survNamesToDelete = new ArrayList<>();
    private String curDate;

    private void writeSurveyToScroll(List<CompleteSurvey> completeSurvey, String survName, String fDate) {

        survNamesToDelete.add(survName);
        curDate = fDate;

        LinearLayout linLay = new LinearLayout(getContext());
        linLay.setOrientation(LinearLayout.VERTICAL);
        linLay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView sName = new TextView(getContext());
        sName.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textviewDate.setTextColor(Color.WHITE);
        textviewDate.setText(fDate);
        nameParams.setMargins(30, 10, 10, 0);
        sName.setTextSize(20);
        sName.setTypeface(null, Typeface.BOLD);
        sName.setText(getResources().getString(R.string.surName) + survName);
        linLay.addView(sName, nameParams);

        LinearLayout linQuest = new LinearLayout(getContext());
        linQuest.setOrientation(LinearLayout.VERTICAL);
        //   linQuest.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams linLayParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linLayParams.setMargins(40, 20, 10, 0);

        LinearLayout.LayoutParams paramsAnsw = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsAnsw.setMargins(40, 5, 5, 0);
        LinearLayout.LayoutParams quesTv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        quesTv.setMargins(0, 10, 5, 0);


        for (CompleteSurvey cp : completeSurvey) {
            TextView question = new TextView(getContext());
            question.setTextColor(Color.WHITE);
            question.setText(cp.getName());
            question.setTextSize(14);
            question.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);

            //   String s = c.getName();
            linQuest.addView(question);

            for (String ans : cp.getAnswers()) {
                if (ans.matches("")) {
                    ans = getResources().getString(R.string.strNoAnswer);
                }

                TextView tvAns = new TextView(getContext());
                tvAns.setTextColor(Color.WHITE);
                tvAns.setText(ans);
                linQuest.addView(tvAns, paramsAnsw);
            }


        }
        linLay.addView(linQuest, linLayParams);
        mainLinLay.addView(linLay, mainLinLay.getChildCount() - 1);

        if (calendar.getVisibility() == View.VISIBLE) {
            hideCalendar();
        }

    }

    private void hideCalendar() {

        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -calendar.getHeight());
        animation.setRepeatMode(0);
        animation.setDuration(800);
        animation.setFillAfter(true);
        calendar.startAnimation(animation);
        calendar.setVisibility(View.GONE);

        TranslateAnimation animeLay = new TranslateAnimation(0, 0, 2000, 0);
        animeLay.setRepeatMode(0);
        animeLay.setDuration(800);
        layAnime.setVisibility(View.VISIBLE);
        layAnime.setAnimation(animeLay);
    }

    private void showCalendar() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, -calendar.getHeight(), 0);
        animation.setRepeatMode(0);
        animation.setDuration(800);
        animation.setFillAfter(true);
        calendar.startAnimation(animation);
        calendar.setVisibility(View.VISIBLE);

        TranslateAnimation animeLay = new TranslateAnimation(0, 0, 0, 2000);
        animeLay.setRepeatMode(0);
        animeLay.setDuration(600);
        layAnime.setAnimation(animeLay);
        layAnime.setVisibility(View.GONE);
        mainLinLay.removeAllViews();
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String path = "Users/" + currentFirebaseUser.getUid() + "/" + "Complete_Survey";
    private Typeface typeface = Typeface.DEFAULT_BOLD;
    private String strCalIcon = "";
    private int color = R.color.colblack;
    private int size = 10;


    private void getSurveyNames() {

        Typeface typeface = Typeface.DEFAULT_BOLD;
        String strCalIcon = "";
        int color = R.color.colblack;
        int size = 10;
        List<EventDay> events = new ArrayList<>();

        db.collection(path).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> surveyNames = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        surveyNames.add(document.getId());
                    }
                    setEventsToCalendar(surveyNames);
                } else {
                }
            }

            private void setEventsToCalendar(List<String> surveyNames) {

                for (String srName : surveyNames) {

                    db.collection(path).document(srName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            List<String> dates = new ArrayList<>();
                            dates = (ArrayList<String>) documentSnapshot.get("dates");
                            setEvents(srName, dates);
                        }

                        private void setEvents(String srName, List<String> dates) {

                            if (dates != null)
                                for (String date : dates) {
                                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                    try {
                                        Date formatDate = format.parse(date);
                                        Calendar calendar = Calendar.getInstance();
                                        String s1 = srName;
                                        //   calendar.set(formatDate.getYear(), formatDate.getMonth(), formatDate.getDate());
                                        calendar.set(Integer.parseInt(date.substring(6, 10)), Integer.parseInt(date.substring(3, 5)) - 1
                                                , Integer.parseInt(date.substring(0, 2)));
                                        //cut three or four signs
                                        //  Drawable calUt = CalendarUtils.getDrawableText(getContext(), "2", typeface, color, size);
                                        events.add(new EventDay(calendar, R.drawable.srv_icon));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                            CalendarView calendarView1 = (CalendarView) view.findViewById(R.id.calendar);
                            calendarView1.setEvents(events);
                        }
                    });
                }
                //set events to calendar
            }
        });
    }
}
