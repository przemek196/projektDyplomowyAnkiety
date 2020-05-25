package com.example.projektdyplomowyankiety;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddQuestion extends AppCompatActivity {

    private Button btnaddNewField;
    private Button btnconfirmQuestions;
    RadioGroup questionType;
    private EditText edTextGiveQues;
    private LinearLayout linLayQuest;
    private ScrollView scrolViewFields;
    private TextView tv;
    private RadioButton r1, r2, r3;
    // private ArrayList<BackItemFromAddQuestion> backList = new ArrayList<>();
    private BackItemFromAddQuestion backItem;
    private RadioButton checkedRadioButton;
    int count = 0;
    private int height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        initVariables();

//listener on radio button change
        //send data to fragment CreateSurveyFragment and close activity
        btnconfirmQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //send data to CreateSurveyFragment
                backItem = new BackItemFromAddQuestion();
                backItem.setNameOfQuestion(edTextGiveQues.getText().toString());

                switch (questionType.getCheckedRadioButtonId()) {
                    case R.id.rb1:
                        backItem.setNameOfQuestionType(r1.getText().toString());
                        break;
                    case R.id.rb2:

                        backItem.setNameOfQuestionType(r2.getText().toString());
                        break;
                    case R.id.rb3:
                        backItem.setNameOfQuestionType(r3.getText().toString());
                        break;
                    default:
                        Toast.makeText(AddQuestion.this, "Nie zaznaczono rodzaju odpowiedzi.", Toast.LENGTH_SHORT).show();
                        return;

                }

                if (questionType.getCheckedRadioButtonId() != R.id.rb3) {
                    List<String> helpList = new ArrayList<>();
                    ViewGroup group = (ViewGroup) findViewById(R.id.linLayFields);
                    for (int i = 0, count = group.getChildCount(); i < count; ++i) {
                        View view = group.getChildAt(i);
                        if (view instanceof LinearLayout) {

                            ViewGroup group1 = (ViewGroup) view;
                            View v1 = group1.getChildAt(0);
                            if (v1 instanceof EditText) {
                                String a = ((EditText) v1).getText().toString();

                                if (a.matches("")) {
                                    Toast.makeText(AddQuestion.this, getResources().getString(R.string.strAllAnswMust), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                helpList.add(a);
                            }
                        }
                    }

                    backItem.setQuestionList(helpList);
                }

                if (edTextGiveQues.getText().toString().matches("")) {
                    Toast.makeText(AddQuestion.this, getResources().getString(R.string.strNoQuestionName), Toast.LENGTH_SHORT).show();
                    return;
                } else if (backItem.getNameOfQuestionType().toString().matches("")) {
                    Toast.makeText(AddQuestion.this, getResources().getString(R.string.strNoQuestionType), Toast.LENGTH_SHORT).show();
                    return;
                } else if (backItem.getQuestionList().size() == 0 && questionType.getCheckedRadioButtonId() != r3.getId()) {
                    Toast.makeText(AddQuestion.this, getResources().getString(R.string.strNoaddQuest), Toast.LENGTH_SHORT).show();
                    return;
                }


                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", backItem);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        btnaddNewField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addField();
            }
        });

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupTypeQuest);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.rb1:
                        scrolViewFields.setVisibility(View.VISIBLE);
                        btnaddNewField.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb2:
                        scrolViewFields.setVisibility(View.VISIBLE);
                        btnaddNewField.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb3:
                        scrolViewFields.setVisibility(View.GONE);
                        btnaddNewField.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void initVariables() {
        btnaddNewField = (Button) findViewById(R.id.btnAddField);
        btnconfirmQuestions = (Button) findViewById(R.id.btnConfirmQuestion);
        linLayQuest = (LinearLayout) findViewById(R.id.linLayFields);
        scrolViewFields = (ScrollView) findViewById(R.id.scrollViewFields);
        edTextGiveQues = (EditText) findViewById(R.id.edTextGiveQuest);
        questionType = (RadioGroup) findViewById(R.id.radioGroupTypeQuest);
        questionType.clearCheck();
        checkedRadioButton = (RadioButton) questionType.findViewById(questionType.getCheckedRadioButtonId());
        r1 = (RadioButton) findViewById(R.id.rb1);
        r2 = (RadioButton) findViewById(R.id.rb2);
        r3 = (RadioButton) findViewById(R.id.rb3);


    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void addField() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field_layout, null);
/*
        Button b = (Button) rowView.findViewById(R.id.btnDeleteField);
        b.setId(count);
*/
        ImageView b = (ImageView) rowView.findViewById(R.id.imViewDelete);
        b.setId(count);

        int a = linLayQuest.getChildCount();
        linLayQuest.addView(rowView, linLayQuest.getChildCount());
        count++;

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteField(v);
            }
        });
    }

    public void deleteField(View v) {
        linLayQuest.removeView((View) v.getParent());
    }

}
