package com.example.kouizine;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class RestoConnected extends AppCompatActivity {
    TextView nom , prenom ,resto,info;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String restoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resto_layout);

        prenom=findViewById(R.id.prenom);
        nom=findViewById(R.id.nom);
        resto=findViewById(R.id.resto);
        info=findViewById(R.id.info);
        fAuth= FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        restoID=fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fstore.collection("Restaurant").document(restoID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot!=null){
                prenom.setText(documentSnapshot.getString("Prenom"));
                nom.setText(documentSnapshot.getString("Nom"));
                resto.setText(documentSnapshot.getString("Restaurant name"));
                info.setText(documentSnapshot.getString("Information"));}
            }
        });
    }


    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}