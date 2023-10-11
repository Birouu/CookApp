package com.example.kouizine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientMenu extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView recyclerView;
    ImageView addItem;
    ArrayList<Items> userArrayList;
    EditText nameOfItem, descriptionOfItem,priceOfItem;
    ClientAdapter clientAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    CollectionReference notebookRef,notebookRefgeneral;
    FirebaseAuth fAuth;
    private View root ;
    FirebaseFirestore fstore;
    String restoID,restoname,ClientID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientmenu_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();
        fAuth= FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        ClientID=fAuth.getCurrentUser().getUid();
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db= FirebaseFirestore.getInstance();
        userArrayList=new ArrayList<Items>();
        clientAdapter = new ClientAdapter(ClientMenu.this,userArrayList,this);

        recyclerView.setAdapter(clientAdapter);
        EventChangeListener();
        progressDialog.dismiss();

    }

    private void EventChangeListener() {
        CollectionReference notebookRefgeneral= db.collection("Menus");
        notebookRefgeneral.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){

                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                    Log.e("firestore error",error.getMessage());
                    return;
                }

                for(QueryDocumentSnapshot documentSnapshot : value){
                    restoID=documentSnapshot.getId();
                    CollectionReference notebookRef= db.collection("Menus").document(restoID).collection("Mymenus");
                    notebookRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                    userArrayList.add(dc.getDocument().toObject(Items.class));
                                }

                                clientAdapter.notifyDataSetChanged();
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        }
                    });

                }

            }
        });


    }

    private void DataChanged() {
        userArrayList.clear();
        CollectionReference notebookRefgeneral= db.collection("Menus");
        notebookRefgeneral.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){

                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                    Log.e("firestore error",error.getMessage());
                    return;
                }

                for(QueryDocumentSnapshot documentSnapshot : value){
                    restoID=documentSnapshot.getId();
                    CollectionReference notebookRef= db.collection("Menus").document(restoID).collection("Mymenus");
                    notebookRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                    userArrayList.add(dc.getDocument().toObject(Items.class));
                                }

                                clientAdapter.notifyDataSetChanged();
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        }
                    });

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
        restoID=userArrayList.get(position).getRestoID();
        Items items= userArrayList.get(position);
        items.setClientID(ClientID);

        AlertDialog.Builder dialog = new AlertDialog.Builder(ClientMenu.this);
        dialog.setTitle("Account Managing");
        dialog.setMessage("Make a decision");
        dialog.setPositiveButton("Order Item", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CollectionReference notebookRef= db.collection("Menus").document(restoID).collection("ItemsOrdered");
                notebookRef.add(items).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String dochelper;
                        dochelper= documentReference.getId();

                        CollectionReference notebookRefClient= db.collection("users").document(ClientID).collection("CartClient");
                        notebookRefClient.document(dochelper).set(items);
                        Toast.makeText(ClientMenu.this, "New item was successfully added to your Cart!", Toast.LENGTH_LONG).show();
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



        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }

}