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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.projektdyplomowyankiety.EntryActivity;
import com.example.projektdyplomowyankiety.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class FragmentChangePassword extends Fragment {

    View view;
    private static final String TAG = "FragmentRegisActivity";

    EditText oldPass;
    EditText newPass;
    TextView emailUser;
    Button btnChange;
    TextView deleteAcount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_password, container, false);
        Log.d(TAG, "onCreateView: started");

        oldPass = view.findViewById(R.id.edTextOldPassword);
        newPass = view.findViewById(R.id.edTextnewPass);
        emailUser = view.findViewById(R.id.textViewEmailAdress);
        btnChange = view.findViewById(R.id.changepssword);
        deleteAcount = view.findViewById(R.id.tvDeleteAccount);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        emailUser.setText(user.getEmail());


        deleteAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_delete_account, null);

                final EditText editTextpassword = (EditText) dialogView.findViewById(R.id.edt_comment);
                Button button1 = (Button) dialogView.findViewById(R.id.buttonSubmit);
                Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //delete account

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), editTextpassword.getText().toString());

                        deletAllData(user);

                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User account deleted.");
                                                        }
                                                    }
                                                });

                                    }
                                });
                        dialogBuilder.dismiss();

                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(), EntryActivity.class);
                        startActivity(intent);
                        Toast.makeText(getContext(), "Wylogowano", Toast.LENGTH_SHORT).show();

                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();


            }
        });


        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), oldPass.getText().toString());


                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Hasło zostało zmienione.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Hasło nie zostało zmienione.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Błąd autoryzacji.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });


        return view;
    }

    private void deletAllData(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //delete all data from firestore


             /*
                        db.collection("Users").document(user.getUid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
*/
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
