package com.example.projektdyplomowyankiety;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentLoginActivity extends Fragment {


    private static final String TAG = "FragmentLoginActivity";
    private Button btnNavLogIn;
    private Button btnNavQuestRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private ProgressDialog progDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentlogin_layout, container, false);
        Log.d(TAG, "onCreateView: started");
        btnNavLogIn = (Button) view.findViewById(R.id.btnLogIn);
        btnNavQuestRegister = (Button) view.findViewById(R.id.btnQuestRegister);
        editTextEmail = (EditText) view.findViewById(R.id.edTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.edTextPassword);
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        progDialog = new ProgressDialog(getActivity());

        btnNavLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_user();
            }
        });


        btnNavQuestRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Przejscie do rejestracji", Toast.LENGTH_SHORT).show();
                //navigate to fragment
                ((EntryActivity) getActivity()).setViewPager(1);
            }
        });
        return view;
    }

    private void login_user() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Podaj Email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Podaj Hasło", Toast.LENGTH_LONG).show();
            return;
        }

        //check connection with internet
        if (isConnectingToInternet(getContext()) == false) {
            Toast.makeText(getContext(), "Brak dostepu do sieci", Toast.LENGTH_LONG).show();
        } else {
            progDialog.setMessage("Logowanie Trwa...");
            progDialog.show();

            //login with email and password
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Zalogowano", Toast.LENGTH_LONG).show();
                                //go to main activity
                               Intent intent = new Intent(getActivity(), MainMenu.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(getActivity(), "Logowanie Nieudane", Toast.LENGTH_LONG).show();
                            }
                            progDialog.dismiss();
                        }
                    });
        }
    }

    //check internet connection
    private boolean isConnectingToInternet(Context applicationContext) {
        ConnectivityManager conectManager = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netwInf = conectManager.getActiveNetworkInfo();
        if (netwInf == null) {
            return false;
        } else
            return true;
    }
}
