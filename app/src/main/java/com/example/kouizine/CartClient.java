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

public class CartClient extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView recyclerView;
    ImageView addItem;
    ArrayList<Items> userArrayList;
    Dialog addItemDialog;
    EditText nameOfItem, descriptionOfItem,priceOfItem;
    ClientAdapter clientAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    CollectionReference notebookRef;
    FirebaseAuth fAuth;
    private View root ;
    FirebaseFirestore fstore;
    String clientId,restoname,restoID,docID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clientcart_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data....");
        progressDialog.show();
        fAuth= FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        clientId=fAuth.getCurrentUser().getUid();
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db= FirebaseFirestore.getInstance();
        userArrayList=new ArrayList<Items>();
        clientAdapter = new ClientAdapter(CartClient.this,userArrayList,this);

        recyclerView.setAdapter(clientAdapter);
        EventChangeListener();
        progressDialog.dismiss();

    }

    private void EventChangeListener() {
        CollectionReference notebookRef= db.collection("users").document(clientId).collection("CartClient");
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

    private void DataChanged() {
        userArrayList.clear();
        db.collection("users").document(clientId).collection("CartClient")
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

                            clientAdapter.notifyDataSetChanged();
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
        docID=userArrayList.get(position).getDocId();
        restoID=userArrayList.get(position).getRestoID();

        AlertDialog.Builder dialog = new AlertDialog.Builder(CartClient.this);
        dialog.setTitle("Account Managing");
        dialog.setMessage("Take a decision");
        dialog.setPositiveButton("Cancel Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               FirebaseFirestore.getInstance().collection("users").document(clientId).collection("CartClient")
                        .document(docID).delete();

                db.collection("Menus").document(restoID).collection("ItemsOrdered")
                        .document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CartClient.this,"Order Cancel",Toast.LENGTH_SHORT).show();

                            }
                        });

                DataChanged();
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