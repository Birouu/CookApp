package com.example.kouizine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import java.util.concurrent.atomic.AtomicLong;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Logintabfrag extends Fragment {
    EditText mEmail ,mPassword;
    TextView sustatut;
    Button mLoginBtn,mLoginBtnRES;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    private View root ;
    FirebaseFirestore fstore;
    String userID,AdminID;
    String stat;
    boolean verif;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle saveInstanceStace){
        root =(ViewGroup) inflater.inflate(R.layout.logfrag,container,false);
        init();
        return root;
    }


    private void init(){
        mEmail = root.findViewById(R.id.email);
        mPassword = root.findViewById(R.id.password);
        progressBar = root.findViewById(R.id.progressbar);
        mLoginBtn = root.findViewById(R.id.Logbut);
        fstore= FirebaseFirestore.getInstance();
        fAuth= FirebaseAuth.getInstance();
        AdminID="igCDJ72Ku8QjVpSp4oteIWpNwxY2";
        sustatut = root.findViewById(R.id.toupie);

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


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userID=fAuth.getCurrentUser().getUid();
                            boolean[] bol = {false};

                            fstore.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.getResult().exists() && userID.equals(AdminID)){
                                        Toast.makeText(getActivity(),"Login successful",Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(getActivity(), AdminConnected.class);
                                        ((MainActivity) getActivity()).startActivity(intent1);
                                    }
                                    else if (task.getResult().exists() && userID!=AdminID){
                                        Toast.makeText(getActivity(),"Login successful",Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(getActivity(), ClientInterface.class);
                                        ((MainActivity) getActivity()).startActivity(intent1);
                                    }
                                    else{
                                        DocumentReference docRef = fstore.collection("Restaurant").document(userID);
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                boolean susp=false;
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        if (document.getString("Status").equals("active")) {
                                                            susp=true;
                                                        } else
                                                            susp=false;

                                                    } else {
                                                    }
                                                    if(susp){
                                                        Intent intent2 = new Intent(getActivity(), RestaurantConnected.class);
                                                        ((MainActivity) getActivity()).startActivity(intent2);}
                                                    else{
                                                        Toast.makeText(getActivity(),"Account have been suspended for 10 days",Toast.LENGTH_SHORT).show();
                                                        FirebaseAuth.getInstance().signOut();
                                                        progressBar.setVisibility(View.GONE);
                                                    }


                                                }
                                            }
                                        });}
                                }
                            });

                        }else{
                            Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    private void isNOTSuspended (String userID, final StatusCallback statusCallback){
        fstore= FirebaseFirestore.getInstance();
        String active="active";
        final AtomicLong stat = new AtomicLong(1);
        DocumentReference docRef = fstore.collection("Restaurant").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getString("Status").equals("active")) {
                            statusCallback.isCityExist(true);
                        } else
                            statusCallback.isCityExist(false);

                    } else {
                    }
                }
            }
        });
    }
}





