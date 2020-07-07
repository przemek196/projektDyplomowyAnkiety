package com.example.projektdyplomowyankiety;

import android.Manifest;
import android.app.DatePickerDialog;

import android.content.Context;
import android.content.DialogInterface;

import android.content.pm.PackageManager;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnSuccessListener;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVWriter;

import java.io.File;

import java.io.FileWriter;

import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class FragmentToCSV extends Fragment implements DatePickerDialog.OnDateSetListener, IOnBackPressed {


    private TextView csvSurvName;
    private TextView csvDateFrom;
    private TextView csvDateTo;
    private TextView dateError;
    private CardView cvFrom;
    private CardView cvTo;
    private CardView cvSName;
    private Button btnCSV;
    Context context;

    Calendar calendar;
    DatePickerDialog datePickerDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_csv, container, false);

        csvSurvName = (TextView) view.findViewById(R.id.csv_surv_name);
        csvDateFrom = (TextView) view.findViewById(R.id.date_from);
        csvDateTo = (TextView) view.findViewById(R.id.date_to);
        dateError = (TextView) view.findViewById(R.id.dateError);
        cvFrom = (CardView) view.findViewById(R.id.cardViewDateFrom);
        cvTo = (CardView) view.findViewById(R.id.cardViewDateTo);
        cvSName = (CardView) view.findViewById(R.id.cvSurName);
        dateError.setVisibility(View.GONE);
        btnCSV = (Button) view.findViewById(R.id.btnToCSV);

        csvSurvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all survey names and chose
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String path = "Users/" + currentFirebaseUser.getUid() + "/Complete_Survey";
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(path).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String[] surNames = new String[queryDocumentSnapshots.size()];
                        int i = 0;
                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                            surNames[i] = q.getId();
                            i++;
                        }
                        choseSurey(surNames);
                    }
                });

            }
        });

        csvDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialogAndGetDate(0);
            }
        });

        csvDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialogAndGetDate(1);
            }
        });

        btnCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check variables and add open dialog with file name

                if (checkCorrectData()) {

                    AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
                    LayoutInflater inflater = getLayoutInflater(); //tutaj layout inflater
                    View dialogView = inflater.inflate(R.layout.dialog_csv, null);

                    final EditText editText = (EditText) dialogView.findViewById(R.id.edt_comment);
                    Button button1 = (Button) dialogView.findViewById(R.id.buttonSubmit);
                    Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);

                    File folder = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "CSV files");
                    boolean success = true;

                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogBuilder.dismiss();
                        }
                    });
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //get all files from firestore and write to csv
                            if (editText.length() <= 0) {
                                Toast.makeText(getContext(), getResources().getString(R.string.completeCSVname), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    getAllSurveyNameAndGenereteCSV(editText.getText().toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }


                            dialogBuilder.dismiss();
                        }
                    });
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.show();
                }
            }
        });
        context = getContext();
        isStoragePermissionGranted();
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String path = "Users/" + currentFirebaseUser.getUid() + "/Complete_Survey";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void getAllSurveyNameAndGenereteCSV(String s) throws ParseException {
        //wpisac do csv

        Date datefrom = new SimpleDateFormat("dd-MM-yyyy").parse(csvDateFrom.getText().toString());
        Date dateTo = new SimpleDateFormat("dd-MM-yyyy").parse(csvDateTo.getText().toString());

        db.collection(path).document(csvSurvName.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> dates = new ArrayList<>();
                Map<String, Object> obj;
                List<String> datesBetween = new ArrayList<>();
                List<String> d = new ArrayList<>();
                d = (ArrayList<String>) documentSnapshot.get("dates");

                for (String date : d) {
                    try {
                        Date compareDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                        if (compareDate.after(datefrom) && compareDate.before(dateTo)) {
                            datesBetween.add(date);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                writeToCSV(datesBetween, s);
            }
        });
    }

    List<String> datesList = new ArrayList<>();
    private int dateListCount = 0;
    boolean repl = true;

    private void writeToCSV(List<String> datesBetween, String s) {

        List<String[]> stringSurv = new ArrayList<>();
        stringSurv.add(new String[]{"Nazwa ankiety: " + csvSurvName.getText().toString()});
        stringSurv.add(new String[]{""});
        datesList = new ArrayList<>(datesBetween);

        for (String date : datesBetween) {
            db.collection(path + "/" + csvSurvName.getText().toString() + "/" + date).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                        CompleteSurvey c = q.toObject(CompleteSurvey.class);
                        if (repl == true) {
                            stringSurv.add(new String[]{datesList.get(dateListCount).replace("-", " ")});
                            repl = false;
                        }
                        stringSurv.add(new String[]{c.getName()});
                        for (String ans : c.getAnswers()) {
                            stringSurv.add(new String[]{ans});
                        }
                    }
                    stringSurv.add(new String[]{""});
                    saveCSVFile(stringSurv, s, datesList);
                }
            });
        }
    }

    private void saveCSVFile(List<String[]> survString, String s, List<String> datesList) {
        if (dateListCount == datesList.size() - 1) {
            CSVWriter writer = null;
            String Filename = "CSV files/" + s + ".csv";
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Filename);
            try {
                int a = 0;
                writer = new CSVWriter(new FileWriter(file));
                writer.writeAll(survString);
                writer.close();
                Toast.makeText(context, "Zapisano plik.", Toast.LENGTH_SHORT).show();
                ((MainMenu) getActivity()).goToCalendarFragment();
            } catch (IOException e) {
                Toast.makeText(context, "Nie zapisano csv", Toast.LENGTH_SHORT).show();
            }
        } else {
            dateListCount++;
            repl = true;
        }
    }

    private boolean checkCorrectData() {
        if (dateError.getCurrentTextColor() != Color.RED && !csvSurvName.getText().equals("Nazwa ankiety") && !csvDateFrom.getText().equals("Data...")
                && !csvDateTo.getText().equals("Data...")) {
            return true;
        }
        return false;
    }

    private void buildDialogAndGetDate(int i) {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog =
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String zero = "";
                        String zero1 = "";
                        if (month < 10) {
                            zero = "0";
                        }
                        if (dayOfMonth < 10) {
                            zero1 = "0";
                        }

                        if (i == 0) {
                            csvDateFrom.setText(zero1 + dayOfMonth + "-" + zero + (month + 1) + "-" + year);
                        } else if (i == 1) {
                            csvDateTo.setText(zero1 + dayOfMonth + "-" + zero + (month + 1) + "-" + year);
                        }
                        try {
                            checkCorrectDate();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
        dialog.show();

    }

    private void checkCorrectDate() throws ParseException {

        Date datefrom = new SimpleDateFormat("dd-MM-yyyy").parse(csvDateFrom.getText().toString());
        Date dateTo = new SimpleDateFormat("dd-MM-yyyy").parse(csvDateTo.getText().toString());

        if (datefrom.after(dateTo)) {
            dateError.setText("Podany przedział jest nieprawidłowy.");
            dateError.setTextColor(Color.RED);
            dateError.setVisibility(View.VISIBLE);
            cvFrom.setBackgroundColor(Color.RED);
            cvTo.setBackgroundColor(Color.RED);
        } else {
            dateError.setVisibility(View.GONE);
            cvFrom.setBackgroundColor(Color.GREEN);
            cvTo.setBackgroundColor(Color.GREEN);
            dateError.setTextColor(Color.GREEN);
        }

    }

    @Override
    public boolean onBackPressed() {

        ((MainMenu) getActivity()).goToCalendarFragment();

        return true;
    }

    private void choseSurey(String[] surNames) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Wybierz ankietę.");
        builder.setItems(surNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cvSName.setBackgroundColor(Color.GREEN);
                csvSurvName.setText(surNames[which]);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String dateString = DateFormat.getDateInstance().format(calendar.getTime());
    }
}
