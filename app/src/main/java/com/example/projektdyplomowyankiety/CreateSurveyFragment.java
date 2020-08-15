package com.example.projektdyplomowyankiety;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateSurveyFragment extends Fragment implements IOnBackPressed {


    View view;
    private List<BackItemFromAddQuestion> completeSurvey = new ArrayList<>();
    private LinearLayout linLayInScroll;
    private CardView cardViewQuestion;
    private Button btnAddQuest;
    private Button btnAddSurvey;
    private TextView tVCount;
    private FirebaseFirestore db;
    private EditText survName;
    private int LAUNCH_SECOND_ACTIVITY = 1;
    public int counter = 1;
    private static final String TAG = "Calendar Fragment";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                BackItemFromAddQuestion backItem = new BackItemFromAddQuestion();
                backItem = data.getParcelableExtra("result");
                completeSurvey.add(backItem);

                addQuestionToShowList(backItem);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }//onActivityResult

    //add question to show list
    private void addQuestionToShowList(BackItemFromAddQuestion backItem) {

        LayoutInflater inflater = getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.show_complete_question_layout, null);

        TextView tNumber = (TextView) rowView.findViewById(R.id.tvNumberOfQuestion);
        tNumber.setText(counter + ".");
        counter++;
        TextView tName = (TextView) rowView.findViewById(R.id.tvNameOfQuestion);
        tName.setText(backItem.getNameOfQuestion());
        TextView tvQuesTyp = (TextView) rowView.findViewById(R.id.typeOfQuestion);
        tvQuesTyp.setText(backItem.getNameOfQuestionType());

        LinearLayout linLay = (LinearLayout) rowView.findViewById(R.id.layAnswers);

        for (String s : backItem.getQuestionList()) {
            TextView tv = new TextView(getContext());
            tv.setText("-" + s);
            linLay.addView(tv);
        }
        LinearLayout l = new LinearLayout(getContext());
        l.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10));
        linLayInScroll.addView(l);
        linLayInScroll.addView(rowView, linLayInScroll.getChildCount());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_createsurvey_layout, container, false);
        //scrolViewQuestion = (ScrollView) view.findViewById(R.id.scrolViewQuestion);

        linLayInScroll = (LinearLayout) view.findViewById(R.id.linlayInScroll); //tutaj chce dodawac cardview
        btnAddQuest = (Button) view.findViewById(R.id.btnAddQuestion);
        btnAddSurvey = (Button) view.findViewById(R.id.btnCreateSurvey);
        cardViewQuestion = (CardView) view.findViewById(R.id.cardViewShowQuestion);
        survName = (EditText) view.findViewById(R.id.edTextSurveyName);

        btnAddSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSurvey();
            }
        });
        btnAddQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewQuestions();
            }
        });


        return view;
    }

    @Override
    public boolean onBackPressed() {


        if (completeSurvey.size() == 0) {
            goToCalendarFragment();

        } else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            goToCalendarFragment();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Powrót spowoduje utratę danych. Czy na pewno chcesz wrócić?").setPositiveButton("Tak", dialogClickListener)
                    .setNegativeButton("Nie", dialogClickListener).show();

        }

        return true;
    }


    private void createSurvey() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String path = "Users/" + currentFirebaseUser.getUid() + "/Created_Survey";
        db = FirebaseFirestore.getInstance();
        //check if the name is empty and if a question is not added
        //add new survey to database


        if (completeSurvey.size() == 0 && survName.getText().toString().matches("")) {
            Toast.makeText(getContext(), getResources().getString(R.string.strCreateSurvEmptyName), Toast.LENGTH_SHORT).show();
        } else if (completeSurvey.size() == 0) {
            Toast.makeText(getContext(), getResources().getString(R.string.strNotAddQuestion), Toast.LENGTH_SHORT).show();
        } else if (survName.getText().toString().matches("")) {
            Toast.makeText(getContext(), getResources().getString(R.string.strNotAddQuestionAndemptyName), Toast.LENGTH_SHORT).show();
        } else {

            db.collection(path).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(document.getId().equals(survName.getText().toString()))
                            {
                                Toast.makeText(getContext(), "Ankieta o podanej nazwie już istnieje.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        saveNewSurvey(path);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });




        }
    }

    private void saveNewSurvey(String path) {

        Map<String, Object> obj = new HashMap<>();

        obj.put("liczbaPytan", Integer.toString(completeSurvey.size()));
        obj.put("nazwa", survName.getText().toString());
        db.collection(path).document(survName.getText().toString()).set(obj);

        for (BackItemFromAddQuestion b : completeSurvey) {
            db.collection(path).document(survName.getText().toString()).collection("questions").document(b.getNameOfQuestion()).set(b);
        }

        //toast and go to calendar fragment
        Toast.makeText(getContext(), getResources().getString(R.string.strSurvCreated), Toast.LENGTH_LONG).show();
        goToCalendarFragment();

    }


    private void goToCalendarFragment() {
        CalendarFragment nextFrag = new CalendarFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();

    }

    public void addNewQuestions() {
        Intent i = new Intent(getContext(), AddQuestion.class);
        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
    }
}
