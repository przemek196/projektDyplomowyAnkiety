package com.example.projektdyplomowyankiety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projektdyplomowyankiety.used_classes.BackItemFromAddQuestion;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteTheSurveyActivity extends AppCompatActivity {

    List<BackItemFromAddQuestion> get_list = new ArrayList<>();
    private LinearLayout linlay;
    private String survName = "";
    private boolean replace;
Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_thesurvey);
        Button btnConfirm = (Button) findViewById(R.id.btnConfirmCompleteSurvey);
        linlay = (LinearLayout) findViewById(R.id.linearLayoutCompleteTheSurvey);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeAndWriteToDatabase();
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                get_list = null;
            } else {
                get_list = extras.getParcelableArrayList("parcel");
                survName = extras.getString("survName");
                replace = extras.getBoolean("replace");
            }
        } else {
            get_list = (List<BackItemFromAddQuestion>) savedInstanceState.getSerializable("parcel");
        }

        createSurveyToComplete();
    }

    private void completeAndWriteToDatabase() {
        List<CompleteSurvey> questionList = new ArrayList<>();

        //read from views and write to database
        final int childCount = linlay.getChildCount();
        for (int i = 0; i < childCount; i++) {

            View v = linlay.getChildAt(i);

            if (v instanceof LinearLayout) {

                int childCountInParent = ((LinearLayout) v).getChildCount();
                CompleteSurvey q = new CompleteSurvey();

                for (int j = 0; j < childCountInParent; j++) {
                    View v1 = ((LinearLayout) v).getChildAt(j);

                    if (j == 0) {
                        if (v1 instanceof LinearLayout) {
                            int count = ((LinearLayout) v1).getChildCount();
                            String name = "";
                            for (int i1 = 0; i1 < count; i1++) {
                                View v2 = ((LinearLayout) v1).getChildAt(i1);
                                if (v2 instanceof TextView) {
                                    name += ((TextView) v2).getText().toString();
                                }
                            }
                            q.setName(name);
                        }
                    }

                    List<String> answ = new ArrayList<>();
                    //few from few
                    if (v1 instanceof LinearLayout) {
                        int count = ((LinearLayout) v1).getChildCount();
                        for (int i1 = 0; i1 < count; i1++) {
                            View v2 = ((LinearLayout) v1).getChildAt(i1);
                            if (v2 instanceof CheckBox) {
                                if (((CheckBox) v2).isChecked()) {
                                    answ.add(((CheckBox) v2).getText().toString());
                                }
                            }
                        }
                        q.setAnswers(answ);
                    }
                    //one from few
                    if (v1 instanceof RadioGroup) {

                        int countRadio = ((RadioGroup) v1).getChildCount();
                        for (int i1 = 0; i1 < countRadio; i1++) {
                            View o = ((RadioGroup) v1).getChildAt(i1);
                            if (o instanceof RadioButton) {
                                if (((RadioButton) o).isChecked()) {
                                    answ.add(((RadioButton) o).getText().toString());
                                }
                            }
                        }
                        q.setAnswers(answ);
                    }
                    //description
                    if (v1 instanceof EditText) {
                      //  Toast.makeText(this, "Opis " + ((EditText) v1).getText(), Toast.LENGTH_SHORT).show();
                        answ.add(((EditText) v1).getText().toString());
                        q.setAnswers(answ);
                    }
                }
                questionList.add(q);
            }
        }

        writeToDatabase(questionList);
//przejsie do poprzedniej aktywnsci
        Intent returnIntent = new Intent();
        returnIntent.putExtra("1", "1");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();


    }

    private void writeToDatabase(final List<CompleteSurvey> questionList) {

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String path = "Users/" + currentFirebaseUser.getUid() + "/Complete_Survey";
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Map<String, Object> obj = new HashMap<>();

        Date cDate = new Date();
        String fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);

        if (replace == true) {
            db.collection(path + "/" + survName + "/" + fDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        //usunac
                        replace = false;
                        writeToDatabase(questionList);
                        //    writeDates(path, survName, null, db);
                        return;
                    } else {
                    }
                }
            });
        } else {
            StringBuilder sb = new StringBuilder();
            int coun = 0;
            for (CompleteSurvey q : questionList) {
                sb.append(coun);
                String strI = sb.toString();
                db.collection(path).document(survName).collection(fDate).document(q.getName().substring(0, 1)).set(q);
                coun++;
            }
            writeDates(path, survName, fDate, db); //dodaje nowa ankiete i date
        }
    }

    List<String> dates1 = new ArrayList<>();

    private void writeDates(String path, String survName, String fDate, FirebaseFirestore db) {

        db.collection(path).document(survName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (fDate != null) {
                    List<String> d = new ArrayList<>();
                    d = (ArrayList<String>) documentSnapshot.get("dates");
                    if (d != null) {

                        for (String s : d) {
                            if (s.equals(fDate))
                                continue;
                            dates1.add(s);
                        }

                    }
                    dates1.add(fDate); //dates 1 nie istnieje
                    Map<String, Object> ob1 = new HashMap<>();
                    ob1.put("nazwa", survName);
                    ob1.put("dates", dates1);
                    db.collection(path).document(survName).set(ob1);

                } else {
                    List<String> d = new ArrayList<>();
                    d = (ArrayList<String>) documentSnapshot.get("dates");
                    for (String s : d) {
                        if (s.equals(fDate))
                            continue;
                        dates1.add(s);
                    }
                    Map<String, Object> ob1 = new HashMap<>();
                    ob1.put("nazwa", survName);
                    ob1.put("dates", dates1);
                    db.collection(path).document(survName).set(ob1);
                }
            }
        });
    }

    private void createSurveyToComplete() {
        int count = 1;
        LinearLayout linlay = (LinearLayout) findViewById(R.id.linearLayoutCompleteTheSurvey);

        for (BackItemFromAddQuestion b : get_list) {

            LinearLayout linParent = new LinearLayout(this);
            linParent.setOrientation(LinearLayout.VERTICAL);
            linParent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView tvQuestionNumber = new TextView(this);
            TextView tvQuestionName = new TextView(this);

            tvQuestionName.setTextColor(Color.parseColor("#ffffff"));
            tvQuestionNumber.setTextColor(Color.parseColor("#ffffff"));

            tvQuestionNumber.setText(count + ".");
            count++;
            tvQuestionName.setText(b.getNameOfQuestion());

            LinearLayout linchild1 = new LinearLayout(this);
            linchild1.setOrientation(LinearLayout.HORIZONTAL);
            linchild1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linchild1.addView(tvQuestionNumber);
            linchild1.addView(tvQuestionName);

            linParent.addView(linchild1, linParent.getChildCount());

            switch (b.getNameOfQuestionType()) {
                case "Jedna z wielu":

                    RadioGroup rg = new RadioGroup(this); //create the RadioGroup
                    rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
                    for (String s : b.getQuestionList()) {
                        RadioButton r = new RadioButton(this);
                        r.setTextColor(Color.parseColor("#ffffff"));

                        r.setText(s);
                        rg.addView(r);
                    }
                    linParent.addView(rg, linParent.getChildCount());
                    linlay.addView(linParent, linlay.getChildCount());
                    break;
                case "Kilka z wielu":
                    LinearLayout linchild2 = new LinearLayout(this);
                    linchild2.setOrientation(LinearLayout.VERTICAL);
                    linchild2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    for (String s : b.getQuestionList()) {
                        CheckBox chB = new CheckBox(this);
                        chB.setTextColor(Color.parseColor("#ffffff"));
                        chB.setText(s);
                        linchild2.addView(chB);
                    }
                    linParent.addView(linchild2, linParent.getChildCount());
                    linlay.addView(linParent, linlay.getChildCount());
                    break;
                case "Opis":
                    EditText ed = new EditText(this);
                    ed.setTextColor(Color.parseColor("#ffffff"));
                    linParent.addView(ed);
                    linlay.addView(linParent);
                    break;
            }

        }
    }
}
