package com.example.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button boutonScan, boutonAjouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boutonScan = (Button) findViewById(R.id.buttonScan);
        boutonScan.setOnClickListener((View.OnClickListener) this);

        boutonAjouter = (Button) findViewById(R.id.buttonAjouter);
        boutonAjouter.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View view) {
        if (boutonAjouter.equals(view)) {
            Intent intent = new Intent(this, ProductInfo.class);
            startActivity(intent);
        }
        else if(boutonScan.equals(view)){
            Intent intent = new Intent(this, SearchProduct.class);
            startActivity(intent);
        }
    }

}