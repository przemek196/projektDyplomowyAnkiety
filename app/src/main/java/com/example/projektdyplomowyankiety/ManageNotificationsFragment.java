package com.example.projektdyplomowyankiety;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projektdyplomowyankiety.ActivityCreateNotyfication;
import com.example.projektdyplomowyankiety.NotificationAdapter;
import com.example.projektdyplomowyankiety.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ManageNotificationsFragment extends Fragment implements IOnBackPressed {

    View view;
    private Button createNotif;
    private int LAUNCH_SECOND_ACTIVITY = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference survRef = db.collection("Users/" + currentFirebaseUser.getUid() + "/Created_Notif");
    private List<String> days = new ArrayList<>();
    private List<String> times = new ArrayList<>();
    private List<String> notificationSurveysName = new ArrayList<>();
    String timeOfNotif = "";
    private TextView textViewNoNotific;
    RecyclerView recyclerView;
    int a = 0;
    int countOfSurvey=0;
    int countOfNotif=0;
Context mContext;
    NotificationAdapter notificationAdapter;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                countOfNotif=0;
                countOfSurvey=0;
                String sname = data.getStringExtra("surName");
                String stime = data.getStringExtra("notTime");
                String days = data.getStringExtra("days");
                notificationAdapter.updateAdapter(getContext(), sname, days, stime);
                textViewNoNotific.setVisibility(View.GONE);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }//onActivityResult

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_notification, container, false);

        textViewNoNotific = (TextView) view.findViewById(R.id.tVNoNotif);
        textViewNoNotific.setVisibility(View.GONE);
        //load notifications
        loadNamesOfCreatedNotification();

        createNotif = (Button) view.findViewById(R.id.btnAddNot);
        createNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = "Users/" + currentFirebaseUser.getUid() + "/Created_Survey";
                db.collection(path).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(QueryDocumentSnapshot q:queryDocumentSnapshots)
                        {
                            countOfSurvey++;
                        }
                        checkNotif(countOfSurvey);
                    }
                });
            }
        });

        return view;
    }



    private void checkNotif(int countOfSurvey) {

        String path = "Users/" + currentFirebaseUser.getUid() + "/Created_Notif";
        db.collection(path).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot q:queryDocumentSnapshots)
                {
                    countOfNotif++;
                }
                if(countOfSurvey == countOfNotif)
                {
                    Toast.makeText(getContext(), "Brak dostępnych ankiet", Toast.LENGTH_SHORT).show();
                }else
                {
                    Intent i = new Intent(getContext(), ActivityCreateNotyfication.class);
                    startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
                }
            }
        });
    }

    private void loadNamesOfCreatedNotification() {

        survRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                timeOfNotif = "";
                int i = 0;
                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    notificationSurveysName.add(q.getId());
                    times.add(q.getString("notificationTime"));
                    days.add(q.getString("days"));
                    i++;
                }
                if (i == 0) {
                    textViewNoNotific.setVisibility(View.VISIBLE);
                    textViewNoNotific.setText("Brak aktywnych powiadomień");
                }
                writeTolist(notificationSurveysName, times, days);
            }
        });
    }

    private void writeTolist(List<String> notificationSurveysName, List<String> times, List<String> days) {
        recyclerView = view.findViewById(R.id.rVnotificationItemList);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        notificationAdapter = new NotificationAdapter(getContext(), notificationSurveysName, times, days);//tutaj dac liste
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    @Override
    public boolean onBackPressed() {

        ((MainMenu) getActivity()).goToCalendarFragment();

        return true;
}
    }
