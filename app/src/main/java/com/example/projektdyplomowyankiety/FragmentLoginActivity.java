package com.example.projektdyplomowyankiety;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projektdyplomowyankiety.EntryActivity;
import com.example.projektdyplomowyankiety.MainMenu;
import com.example.projektdyplomowyankiety.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.text.ParseException;

public class FragmentLoginActivity extends Fragment {


    private static final String TAG = "FragmentLoginActivity";
    private Button btnNavLogIn;
    // private Button btnNavQuestRegister;
    private TextView tvNavQuestRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private ProgressDialog progDialog;
    private TextView passwordReminder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentlogin_layout, container, false);
        Log.d(TAG, "onCreateView: started");
        btnNavLogIn = (Button) view.findViewById(R.id.btnLogIn);
        tvNavQuestRegister = (TextView) view.findViewById(R.id.tvQuestRegister);
        editTextEmail = (EditText) view.findViewById(R.id.edTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.edTextPassword);
        passwordReminder = (TextView) view.findViewById(R.id.passwordRemeber);
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        progDialog = new ProgressDialog(getActivity());


        btnNavLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_user();
            }
        });


        passwordReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectingToInternet(getContext()) == false) {
                    Toast.makeText(getContext(), "Brak dostepu do sieci", Toast.LENGTH_LONG).show();
                }
                else
                {
                AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_pass_reminder, null);

                final EditText email = (EditText) dialogView.findViewById(R.id.edt_email_pass_remind);
                Button button1 = (Button) dialogView.findViewById(R.id.btnSendRemindPassEmial);

                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!email.getText().toString().matches("")) {

                            mAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                                Toast.makeText(getContext(), "Wiadomość została wysłana", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    switch (e.getMessage()) {
                                        case "The email address is badly formatted.":
                                            Toast.makeText(getContext(), "Błędnie sformatowany adres email.", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "There is no user record corresponding to this identifier. The user may have been deleted.":
                                            Toast.makeText(getContext(), R.string.strNoRegistredUserwithThisEEmail, Toast.LENGTH_SHORT).show();

                                            break;
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(getContext(), "Błedny adres e-mail.", Toast.LENGTH_SHORT).show();

                        }


                        dialogBuilder.dismiss();
                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();


            }
        }
        });


        tvNavQuestRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
