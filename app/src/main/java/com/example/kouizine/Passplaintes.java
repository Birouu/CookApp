 package com.example.kouizine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

 public class Passplaintes extends AppCompatActivity {
     TextView cuisinier , plaintes ,IDC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plaintestataaa);

        String ncuisiner =getIntent().getStringExtra("ncuisinier");
        String nplaintes =getIntent().getStringExtra("nplaintes");
        String nIDC =getIntent().getStringExtra("nIDC");

        cuisinier=findViewById(R.id.cuisi);
        plaintes=findViewById(R.id.plain);
        IDC=findViewById(R.id.iden);

        cuisinier.setText(ncuisiner);
        plaintes.setText(nplaintes);
        IDC.setText(nIDC);



    }
}