package com.example.kouizine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signuptabfrag extends Fragment {
    EditText mNom , mPrenom ,mEmail ,mPassword,mPassredo;
    Button mRegisterBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    private View root ;
    String userID,AdminID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle saveInstanceStace){
        super.onCreate(saveInstanceStace);
        root =(ViewGroup)inflater.inflate(R.layout.signfrag,container,false);
        init();
        return root;
    }

    private void init(){
        mPrenom = root.findViewById(R.id.prenom);
        mNom = root.findViewById(R.id.nom);
        mEmail = root.findViewById(R.id.email);
        mPassword = root.findViewById(R.id.password);
        mPassredo = root.findViewById(R.id.redopassword);
        mRegisterBtn = root.findViewById(R.id.signBTN);
        progressBar = root.findViewById(R.id.progressbar);
        fAuth= FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        AdminID="igCDJ72Ku8QjVpSp4oteIWpNwxY2";

        if(fAuth.getCurrentUser()!=null){
            userID=fAuth.getCurrentUser().getUid();
            fstore.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists() && userID.equals(AdminID)){
                        Intent intent1 = new Intent(getActivity(), AdminConnected.class);
                        ((MainActivity) getActivity()).startActivity(intent1);
                        getActivity().finish();
                    }
                    else if (task.getResult().exists() && userID!=AdminID){
                        Intent intent1 = new Intent(getActivity(), ClientInterface.class);
                        ((MainActivity) getActivity()).startActivity(intent1);
                        getActivity().finish();
                    }
                    else{

                        Intent intent2 = new Intent(getActivity(), RestaurantConnected.class);
                        ((MainActivity) getActivity()).startActivity(intent2);
                        getActivity().finish();
                    }
                }
            });
        }



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passredo = mPassredo.getText().toString().trim();
                String prenom = mPrenom.getText().toString().trim();
                String nom = mNom.getText().toString().trim();

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
                if (password.length()< 5) {
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
                            Toast.makeText(getActivity(),"user created",Toast.LENGTH_SHORT).show();
                            userID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String ,Object> user=new HashMap<>();
                            user.put("Prenom",prenom);
                            user.put("Nom",nom);
                            user.put("Email",email);
                            documentReference.set(user);


                            Intent intent = new Intent(getActivity(), ClientInterface.class);
                            ((MainActivity) getActivity()).startActivity(intent);
                        }else{
                            Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });










    }
}
