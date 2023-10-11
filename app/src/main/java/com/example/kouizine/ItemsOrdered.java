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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ItemsOrdered extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView recyclerView;
    ImageView addItem;
    ArrayList<Items> userArrayList;
    Dialog addItemDialog;
    EditText nameOfItem, descriptionOfItem,priceOfItem;
    ItemOrderAdapter itemOrderAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    CollectionReference notebookRef;
    FirebaseAuth fAuth;
    private View root ;
    FirebaseFirestore fstore;
    String restoID,restoname,ClientID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemsordered_layout);

        progressDialog = new ProgressDialog(this);
        addItemDialog = new Dialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();
        fAuth= FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        restoID=fAuth.getCurrentUser().getUid();
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db= FirebaseFirestore.getInstance();
        userArrayList=new ArrayList<Items>();
        itemOrderAdapter = new ItemOrderAdapter(ItemsOrdered.this,userArrayList,this);

        recyclerView.setAdapter(itemOrderAdapter);
        EventChangeListener();
        progressDialog.dismiss();

    }

    private void EventChangeListener() {
        CollectionReference notebookRef= db.collection("Menus").document(restoID).collection("ItemsOrdered");
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

                    itemOrderAdapter.notifyDataSetChanged();
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }

    private void DataChanged() {
        userArrayList.clear();
        db.collection("Menus").document(restoID).collection("ItemsOrdered")
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
                                userArrayList.add(dc.getDocument().toObject(Items.class));
                            }

                            itemOrderAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
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
        String docID =userArrayList.get(position).getDocId();
        ClientID= userArrayList.get(position).getClientID();



        AlertDialog.Builder dialog = new AlertDialog.Builder(ItemsOrdered.this);
        dialog.setTitle("Account Managing");
        dialog.setMessage("Take a decision");
        dialog.setPositiveButton("Accept Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance().collection("Menus").document(restoID).collection("ItemsOrdered")
                        .document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ItemsOrdered.this,"Order Accepted",Toast.LENGTH_SHORT).show();
                                DataChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ItemsOrdered.this,"Order Not Accepted",Toast.LENGTH_SHORT).show();

                            }
                        });

                // send a notification to the client like a status

                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        dialog.setNeutralButton("Refuse Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseFirestore.getInstance().collection("Menus").document(restoID).collection("ItemsOrdered")
                                .document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ItemsOrdered.this,"Order Not Accepted",Toast.LENGTH_SHORT).show();

                            }
                        });

                FirebaseFirestore.getInstance().collection("users").document(ClientID).collection("CartClient")
                        .document(docID).delete();
                DataChanged();
                dialog.dismiss();
            }
        });


        AlertDialog alertDialog = dialog.create();
        alertDialog.show();




    }

}