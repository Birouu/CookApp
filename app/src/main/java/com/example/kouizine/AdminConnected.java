package com.example.kouizine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminConnected extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView recyclerView;
    ArrayList<Suit> userArrayList;
    MyAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    FirebaseAuth fAuth;
    private View root ;
    FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();

        recyclerView=findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db= FirebaseFirestore.getInstance();
        userArrayList=new ArrayList<Suit>();
        myAdapter = new MyAdapter(AdminConnected.this,userArrayList,this);

        recyclerView.setAdapter(myAdapter);
        EventChangeListener();

    }

    private void EventChangeListener() {
        db.collection("Plaintes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){

                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                    Log.e("firestore error",error.getMessage());
                    return;
                }

                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType()==DocumentChange.Type.ADDED){
                        userArrayList.add(dc.getDocument().toObject(Suit.class));
                    }

                    myAdapter.notifyDataSetChanged();
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }

    private void DataChanged() {
        userArrayList.clear();
        db.collection("Plaintes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!= null){

                            if(progressDialog.isShowing())
                                progressDialog.dismiss();

                            Log.e("firestore error",error.getMessage());
                            return;
                        }

                        for(DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType()==DocumentChange.Type.ADDED){
                                userArrayList.add(dc.getDocument().toObject(Suit.class));
                            }

                            myAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }


    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void refresh(View view){
        DataChanged();
    }

    public void suspendUser(String userID){

        fstore=FirebaseFirestore.getInstance();
        DocumentReference documentReference = fstore.collection("Restaurant").document(userID);

        documentReference.update("Status", "inactive").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminConnected.this,"user suspended for 10 days",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AdminConnected.this,"error occur suspending",Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    public void unSuspendUser(String userID){

        fstore=FirebaseFirestore.getInstance();
        DocumentReference documentReference = fstore.collection("Restaurant").document(userID);

        documentReference.update("Status", "active").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminConnected.this,"user is no more suspended",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(AdminConnected.this,"error occur suspending",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onItemClick(int position) {
       /* Intent intent= new Intent(AdminConnected.this,Passplaintes.class);
        intent.putExtra("ncuisinier",userArrayList.get(position).getCuisinier());
        intent.putExtra("nplaintes",userArrayList.get(position).getPlaintes());
        intent.putExtra("nIDC",userArrayList.get(position).getIDC());
        startActivity(intent);*/

        db= FirebaseFirestore.getInstance();
        String idUser=userArrayList.get(position).getIDC();
        String docID=userArrayList.get(position).getDocId();

        AlertDialog.Builder dialog = new AlertDialog.Builder(AdminConnected.this);
        dialog.setTitle("Account Managing");
        dialog.setMessage("Take a decision to dismiss or apply sanction");
        dialog.setPositiveButton("Suspend User", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                suspendUser(idUser );
                FirebaseFirestore.getInstance().collection("Plaintes").
                        document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AdminConnected.this,"Account Suspended",Toast.LENGTH_SHORT).show();
                                DataChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminConnected.this,"Account NOt suspended",Toast.LENGTH_SHORT).show();

                            }
                        });
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        dialog.setNeutralButton("Delete User", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                FirebaseFirestore.getInstance().collection("Plaintes").
                        document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AdminConnected.this,"Account deleted",Toast.LENGTH_SHORT).show();
                                DataChanged();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminConnected.this,"Account NOt deleted",Toast.LENGTH_SHORT).show();


                            }
                        });
                        dialog.dismiss();
                    }
                });


                AlertDialog alertDialog = dialog.create();
        alertDialog.show();




    }

}