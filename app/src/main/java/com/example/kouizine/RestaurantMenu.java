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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantMenu extends AppCompatActivity implements RecyclerViewInterface{
    RecyclerView recyclerView;
    ImageView addItem;
    ArrayList<Items> userArrayList;
    Dialog addItemDialog;
    EditText nameOfItem, descriptionOfItem,priceOfItem;
    MenuAdapter menuAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    CollectionReference notebookRef;
    FirebaseAuth fAuth;
    private View root ;
    FirebaseFirestore fstore;
    String restoID,restoname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

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
        menuAdapter = new MenuAdapter(RestaurantMenu.this,userArrayList,this);

        recyclerView.setAdapter(menuAdapter);
        EventChangeListener();
        progressDialog.dismiss();
        addItem = (ImageView)findViewById(R.id.imageAdd);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    private void EventChangeListener() {
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

                            menuAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }

    private void DataChanged() {
        userArrayList.clear();
        db.collection("Menus").document(restoID).collection("Mymenus")
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

                            menuAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
    }


    private void openDialog(){
        addItemDialog.setContentView(R.layout.restaurant_additemdialog);
        addItemDialog.show();
        addItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button addItem = addItemDialog.findViewById(R.id.btnAddItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameOfItem = addItemDialog.findViewById(R.id.editTextNameItem);
                descriptionOfItem = addItemDialog.findViewById(R.id.editTextDescriptionItem);
                priceOfItem = addItemDialog.findViewById(R.id.editTextPriceItem);
                String nameItem = nameOfItem.getText().toString().trim();
                String descriptionItem = descriptionOfItem.getText().toString().trim();
                String priceItem = priceOfItem.getText().toString().trim();


                if(nameItem.isEmpty()){
                    nameOfItem.setError("Name of item is required");
                    nameOfItem.requestFocus();
                    return;
                }
                if(descriptionItem.isEmpty()){
                    descriptionOfItem.setError("Description of item is required");
                    descriptionOfItem.requestFocus();
                    return;

                }

                if(priceItem.isEmpty()){
                    priceOfItem.setError("Price of item is required");
                    priceOfItem.requestFocus();
                    return;

                }

                DocumentReference docRef = fstore.collection("Restaurant").document(restoID);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                restoname=document.getString("Restaurant name");
                                Items items = new Items(restoname,nameItem,descriptionItem,priceItem,restoID);
                                CollectionReference notebookRef= db.collection("Menus").document(restoID).collection("Mymenus");
                                notebookRef.add(items).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(RestaurantMenu.this, "New item was successfully added to your menu!", Toast.LENGTH_LONG).show();
                                        addItemDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }
                });





                }
        });

        ImageView close = addItemDialog.findViewById(R.id.imageViewClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog.dismiss();
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
        String docID=userArrayList.get(position).getDocId();

        AlertDialog.Builder dialog = new AlertDialog.Builder(RestaurantMenu.this);
        dialog.setTitle("Account Managing");
        dialog.setMessage("Take a decision to dismiss or apply sanction");


        dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        dialog.setNeutralButton("Delete Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseFirestore.getInstance().collection("Menus").document(restoID).collection("Mymenus").
                        document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RestaurantMenu.this,"Menu deleted",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RestaurantMenu.this,"Menu not deleted",Toast.LENGTH_SHORT).show();


                            }
                        });
                DataChanged();
                dialog.dismiss();
            }
        });


        AlertDialog alertDialog = dialog.create();
        alertDialog.show();




    }

}