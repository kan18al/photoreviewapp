package com.example.photoreviewapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.photoreviewapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button reg, login, logout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser cUser;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        cUser = mAuth.getCurrentUser();

        etEmail = view.findViewById(R.id.editTextTextPersonEmail);
        etPassword = view.findViewById(R.id.editTextTextPersonPassword);
        reg = view.findViewById(R.id.buttonReg);
        login = view.findViewById(R.id.buttonLogin);
        logout = view.findViewById(R.id.buttonLogout);

        mAuthListener = firebaseAuth -> {
            cUser = mAuth.getCurrentUser();
            if (cUser != null) {
                etEmail.setText(cUser.getEmail());
                Toast.makeText(getActivity(), "onAuthStateChanged:signed_in:" + cUser.getUid(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();
                etEmail.setText("");
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        reg.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etEmail.getText().toString()) && !TextUtils.isEmpty(etPassword.getText().toString())) {
                mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "new user registered!", Toast.LENGTH_SHORT).show();
                            } else Toast.makeText(getContext(), "new user registered failed!", Toast.LENGTH_SHORT).show();
                        });
            } else Toast.makeText(getContext(), "Please enter Email and Password...", Toast.LENGTH_SHORT).show();

        });

        login.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etEmail.getText().toString()) && !TextUtils.isEmpty(etPassword.getText().toString())) {
                mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "login successful!", Toast.LENGTH_SHORT).show();
                            } else Toast.makeText(getContext(), "login failed!", Toast.LENGTH_SHORT).show();
                        });
            } else Toast.makeText(getContext(), "Please enter Email and Password...", Toast.LENGTH_SHORT).show();
        });

        logout.setOnClickListener(v -> {
            try {
                mAuth.signOut();
                cUser = null;
                etEmail.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//        if (cUser != null) {
//            Toast.makeText(getContext(), "User not null", Toast.LENGTH_SHORT).show();
//        } else Toast.makeText(getContext(), "User null", Toast.LENGTH_SHORT).show();
//    }
}