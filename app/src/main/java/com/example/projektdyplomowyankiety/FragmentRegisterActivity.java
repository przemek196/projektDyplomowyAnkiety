package com.example.projektdyplomowyankiety;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentRegisterActivity extends Fragment {

    View view;
    private static final String TAG = "FragmentRegisActivity";
    private Button btnRegister;
    //  private Button btnNavQuestLogin;
    private TextView tvNavQuestLogin;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentregister_layout, container, false);
        Log.d(TAG, "onCreateView: started");
        btnRegister = (Button) view.findViewById(R.id.btnRegister);
       tvNavQuestLogin = (TextView) view.findViewById(R.id.tvQuestLogIn);
        editTextEmail = (EditText) view.findViewById(R.id.edTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.edTextPassword);
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //hide password
        progDialog = new ProgressDialog(getActivity());

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });



        tvNavQuestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Przejscie do logowania", Toast.LENGTH_SHORT).show();
                //navigate to fragment
                ((EntryActivity) getActivity()).setViewPager(0);
            }
        });


        return view;
    }

    private void registerNewUser() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getActivity(), "Podaj Prawidłowy Adres Email.", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 4 || password.length() > 10) {
            Toast.makeText(getActivity(), "Podaj Prawidłowe Hasło.", Toast.LENGTH_LONG).show();
            return;
        }

        if (isConnectingToInternet(getContext()) == false) {
            Toast.makeText(getContext(), "Brak dostepu do sieci", Toast.LENGTH_LONG).show();
        } else {
            progDialog.setMessage("Rejestracja trwa ...");
            progDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Rejestracja udana.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(), MainMenu.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "Błąd rejestracji.", Toast.LENGTH_LONG).show();
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
