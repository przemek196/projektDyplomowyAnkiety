package com.example.projektdyplomowyankiety;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CompleteSurveyFragment extends Fragment implements IOnBackPressed {


    View view;

    private List<BackItemFromAddQuestion> loadedSurvey = new ArrayList<>();
    private List<CountAndNameSurvey> nameAndCount = new ArrayList<>();
    private ArrayList<String> surNames = new ArrayList<>();
    private ArrayList<String> quesSurCount = new ArrayList<String>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference surveyColRef;
    private DocumentReference surveyDocRef;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference survRef = db.collection("Users/" + currentFirebaseUser.getUid() + "/Created_Survey");
    private String collection_path;
    private int count = 0;
    private String a;
    private static final String TAG = "Complete Survey Frag";
    RecyclerViewAdapter recyclerViewAdapter;


    private int LAUNCH_SECOND_ACTIVITY = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getContext(), "asdasd", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }//onActivityResult


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_completesurvey_layout, container, false);

        collection_path = "Users/" + currentFirebaseUser.getUid() + "/Created_Survey";
        surveyColRef = db.collection(collection_path);
        surveyDocRef = surveyColRef.getParent();
        loadNamesOfSurveys();

        return view;
    }


    @Override
    public boolean onBackPressed() {
        ((MainMenu) getActivity()).goToCalendarFragment();
        return true;
    }


    private void writeTolist(ArrayList<String> surNames, ArrayList<String> quesSurCount) {

        RecyclerView recyclerView = view.findViewById(R.id.rVitemlist);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), surNames, quesSurCount);//tutaj dac liste
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadNamesOfSurveys() {

        survRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                    surNames.add(q.getId());
                    quesSurCount.add(q.getString("liczbaPytan"));
                }
                writeTolist(surNames, quesSurCount);
            }
        });
    }
}
