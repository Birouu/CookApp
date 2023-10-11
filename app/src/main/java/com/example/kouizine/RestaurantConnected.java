package com.example.kouizine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantConnected extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String restoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_layout);

        CardView recommendedDishes;
        recommendedDishes = findViewById(R.id.cardViewRecommended);
        recommendedDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RestaurantConnected.this,ItemsOrdered.class));            }
        });


        CardView menu;
        menu = (CardView)findViewById(R.id.cardViewMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RestaurantConnected.this,RestaurantMenu.class));

            }
        });

        CardView info;
        info = (CardView)findViewById(R.id.cardViewInfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RestaurantConnected.this,RestoConnected.class));

            }
        });

        CardView logOut;
        logOut = (CardView) findViewById(R.id.cardViewLogout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(RestaurantConnected.this,MainActivity.class));
            }
        });

    }
}