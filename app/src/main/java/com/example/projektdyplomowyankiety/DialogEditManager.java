package com.example.projektdyplomowyankiety;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.projektdyplomowyankiety.used_classes.BackItemFromAddQuestion;

import java.util.ArrayList;
import java.util.List;


public class DialogEditManager {

    AlertDialog.Builder mBuilder;
    Context mContext;
    ArrayList<String> questionCount;
    Boolean b;
    List<BackItemFromAddQuestion> get_list;
    int position;
    int surveyNamePosition;

    public DialogEditManager(Context mContext, ArrayList<String> questionCount, ArrayList<String> surveyNamesNames, boolean b, List<BackItemFromAddQuestion> get_list, int position, int srvNamePos) {
        this.mContext = mContext;
        this.questionCount = questionCount;
        this.surveyNamesNames = surveyNamesNames;
        this.b = b;
        this.get_list = get_list;
        this.position = position;
        this.position = position;
        this.surveyNamePosition = srvNamePos;
    }

    ArrayList<String> surveyNamesNames;
    LayoutInflater inflater;
    View mView;
    AlertDialog dialog;

    public void cancelDialog(){dialog.cancel();}
    public void createDialog() {
        //dialog to edit survey
        if (get_list.size() == Integer.parseInt(questionCount.get(position))) {
            mBuilder = new AlertDialog.Builder(mContext);
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = inflater.inflate(R.layout.dialog_edit, null);
            ListView listView = (ListView) mView.findViewById(R.id.edSurvListView);
            Button addQuestion = (Button) mView.findViewById(R.id.edSurvAddQues);
            ImageView backDialog = (ImageView) mView.findViewById(R.id.imVieBackDialog);
            mBuilder.setView(mView);
           dialog = mBuilder.create();

            addQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add question to exist ssurvey
                    Intent intent = new Intent(mContext, AddQuestion.class);
                    intent.putExtra("addNew", true);
                    intent.putExtra("questCount", questionCount.get(surveyNamePosition));
                    intent.putExtra("surveyName", surveyNamesNames.get(surveyNamePosition));
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);

                }
            });

            backDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            String[] items = new String[get_list.size()];
            int counter = 0;
            for (BackItemFromAddQuestion bitem : get_list) {
                items[counter] = bitem.getNameOfQuestion();
                counter++;
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_expandable_list_item_1, items);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //click edit
                    Intent intent = new Intent(mContext, AddQuestion.class);
                    intent.putExtra("question", get_list.get(position));
                    intent.putExtra("surveyName", surveyNamesNames.get(surveyNamePosition));
                    mContext.startActivity(intent);
                }
            });


            dialog.show();

        }
    }


}
