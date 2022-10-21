package com.example.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSearchButtonClic (View view){
        Intent intent = new Intent(this,ProductSearch.class);
        startActivity(intent);
    }

    public void onScanButtonClic (View view){
        Intent intent = new Intent(this,ProductScan.class);
        startActivity(intent);
    }
}