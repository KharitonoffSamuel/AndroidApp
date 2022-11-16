package com.example.androidapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button boutonScan, boutonAjouter;
    Produit produit = new Produit();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference databaseReferenceProduits = database.getReference().child("Produits");

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
            Intent intent = new Intent(this, AjoutProduit.class);
            startActivity(intent);
        } else if (boutonScan.equals(view)) {
            Intent intent = new Intent(this, SearchProduct.class);
            startActivity(intent);
        }
    }
}