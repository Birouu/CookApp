package com.example.kouizine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText mNom , mPrenom ,mEmail ,mPassword,mPassredo;
    Button mRegisterBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signfrag);

        mPrenom = findViewById(R.id.prenom);
        mNom = findViewById(R.id.nom);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPassredo = findViewById(R.id.redopassword);
        mRegisterBtn = findViewById(R.id.signBTN);
        progressBar = findViewById(R.id.progressbar);
        fAuth= FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), ClientConnected.class));
            finish();
        }



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passredo = mPassredo.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassredo.setError("Confirmation Password is required");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Password is too short");
                }
                if (!(password.equals(passredo))) {
                    mPassword.setError("Password is too short");
                }

                progressBar.setVisibility(View.VISIBLE);



                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"user created",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ClientConnected.class));
                        }else{
                            Toast.makeText(Register.this,"Error",Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });


    }

}