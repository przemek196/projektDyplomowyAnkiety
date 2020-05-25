package com.example.projektdyplomowyankiety;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> surveyNamesNames = new ArrayList<>();
    private ArrayList<String> questionCount = new ArrayList<>();
    private Context mContext;
    private LayoutInflater li;

    public RecyclerViewAdapter(Context context, ArrayList<String> surNames, ArrayList<String> quesSurCount) {
        surveyNamesNames = surNames;
        questionCount = quesSurCount;
        mContext = context;
        li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_survey_item_list_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);

        li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.survName.setText(surveyNamesNames.get(position));
        holder.countOfQuestionTv.setText(mContext.getResources().getString(R.string.countOfQues) + questionCount.get(position));


        holder.editSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edytowac ankiete

                Toast.makeText(mContext, "Kliknieto "+position, Toast.LENGTH_SHORT).show();







            }
        });

        holder.deleteSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                db.collection("Users/" + currentFirebaseUser.getUid() + "/Created_Survey/"
                                ).document(surveyNamesNames.get(position)).delete();

                                surveyNamesNames.remove(position);
                                questionCount.remove(position);

                                notifyItemRemoved(position);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getResources().getString(R.string.strDeletSurvey1)).setPositiveButton(mContext.getResources().getString(R.string.strYes), dialogClickListener)
                        .setNegativeButton(mContext.getResources().getString(R.string.strNo), dialogClickListener).show();


            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + surveyNamesNames.get(position));

                final List<BackItemFromAddQuestion> get_list = new ArrayList<>();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                //load object from firestore
                db.collection("Users/" + currentFirebaseUser.getUid() + "/Created_Survey/" + surveyNamesNames.get(position)
                        + "/questions").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            get_list.add(q.toObject(BackItemFromAddQuestion.class));
                        }
                        addToLayout(get_list, position, false);
                    }
                });
            }
        });
    }

    public void addToLayout(final List<BackItemFromAddQuestion> get_list, final int position, boolean hide) {


        final Dialog d = new Dialog(mContext);
        if (hide == true) {
            d.hide();
            return;
        }
        d.setContentView(R.layout.dialog_preview_survey);
        LinearLayout linLay = (LinearLayout) d.findViewById(R.id.layDialog);
        TextView textView = d.findViewById(R.id.a);
        textView.setText(surveyNamesNames.get(position));
        int count = 1;
        for (BackItemFromAddQuestion b : get_list) {
            final View rowView = li.inflate(R.layout.show_complete_question_layout, null);
            LinearLayout linLay1 = (LinearLayout) rowView.findViewById(R.id.layAnswers);
            TextView tvName = (TextView) rowView.findViewById(R.id.tvNameOfQuestion);
            TextView tvType = (TextView) rowView.findViewById(R.id.typeOfQuestion);
            TextView tvCount = (TextView) rowView.findViewById(R.id.tvNumberOfQuestion);
            tvCount.setText(count + ".");
            count++;
            tvName.setText(b.getNameOfQuestion());
            tvType.setText(b.getNameOfQuestionType());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(15, 0, 15, 0);
            rowView.setLayoutParams(params);

            for (String s : b.getQuestionList()) {
                TextView tv = new TextView(mContext);
                tv.setText("-" + s);
                linLay1.addView(tv);
            }

            LinearLayout l = new LinearLayout(mContext);
            l.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10));
            linLay.addView(l);
            linLay.addView(rowView, linLay.getChildCount());
        }

        d.show();
        ImageView imV = d.findViewById(R.id.imVback);
        Button agree = d.findViewById(R.id.btnAgree);

        imV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.hide();
            }
        });

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Date cDate = new Date();
                String fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
                db.collection("Users/" + currentFirebaseUser.getUid() + "/Complete_Survey/" + surveyNamesNames.get(position)
                        + "/" + fDate).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(R.string.strWarning);
                            builder.setMessage(R.string.strAgainFillSurv);

                            builder.setPositiveButton(R.string.strYes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToComplAct(get_list, position, true);
                                }
                            });
                            builder.setNegativeButton(R.string.strNo, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        } else {
                            goToComplAct(get_list, position, false);
                        }
                    }
                });
            }
        });


    }

    private void goToComplAct(List<BackItemFromAddQuestion> get_list, int position, boolean replace) {
        Intent i = new Intent(mContext, CompleteTheSurveyActivity.class);
        i.putExtra("replace", replace);
        i.putExtra("parcel", (Serializable) get_list);
        i.putExtra("survName", surveyNamesNames.get(position));
        mContext.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return surveyNamesNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView survName;
        TextView countOfQuestionTv;
        CardView parentLayout;
        ImageView deleteSurvey;
        ImageView editSurvey;

        public ViewHolder(View itemView) {
            super(itemView);
            survName = itemView.findViewById(R.id.tvsurveyName);
            countOfQuestionTv = itemView.findViewById(R.id.countOfQuestion);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            deleteSurvey = itemView.findViewById(R.id.imVDeleteSurvey);
            editSurvey = itemView.findViewById(R.id.imEditSurvey);
        }
    }
}