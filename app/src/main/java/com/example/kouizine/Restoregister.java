package com.example.kouizine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Restoregister extends AppCompatActivity {
    EditText mNom , mPrenom ,mEmail ,mPassword,mPassredo,mResto,mInfo,IDC;
    Button mRegisterBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;
    String restoID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restosignfrag);

        mPrenom = findViewById(R.id.prenom);
        mNom = findViewById(R.id.nom);
        mResto = findViewById(R.id.restoNom);
        mInfo = findViewById(R.id.restoInfo);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPassredo = findViewById(R.id.redopassword);
        mRegisterBtn = findViewById(R.id.signBTN);
        progressBar = findViewById(R.id.progressbar);
        fAuth= FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();


        if(fAuth.getCurrentUser()!=null){
            Intent intent = new Intent(this, RestaurantConnected.class);
            startActivity(intent);
            finish();        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passredo = mPassredo.getText().toString().trim();
                String prenom = mPrenom.getText().toString().trim();
                String nom = mNom.getText().toString().trim();
                String restoNom = mResto.getText().toString().trim();
                String infonom = mInfo.getText().toString().trim();
                String status= "active";

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
                if (password.length() < 5) {
                    mPassword.setError("Password is too short");
                    return;
                }
                if (!(password.equals(passredo))) {
                    mPassword.setError("Passwords do not match");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Restoregister.this,"user created",Toast.LENGTH_SHORT).show();

                            restoID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("Restaurant").document(restoID);
                            Map<String ,Object> user=new HashMap<>();
                            user.put("Prenom",prenom);
                            user.put("Nom",nom);
                            user.put("Restaurant name",restoNom);
                            user.put("Information",infonom);
                            user.put("Email",email);
                            user.put("Status",status);
                            user.put("IDC",restoID);
                            documentReference.set(user);

                            startActivity(new Intent(getApplicationContext(), RestoConnected.class));
                        }else{
                            Toast.makeText(Restoregister.this,"Error",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                });
            }

        });


    }

}