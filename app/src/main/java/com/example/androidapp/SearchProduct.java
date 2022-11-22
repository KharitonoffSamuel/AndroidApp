package com.example.androidapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBBlackValueNode;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SearchProduct extends AppCompatActivity implements View.OnClickListener {

    private Button buttonScanSearch;
    protected TextView textViewNomSearch, textViewCodeSearch, textViewMatiereSearch;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference databaseReferenceProduits = database.getReference().child("Produits");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        buttonScanSearch = (Button) findViewById(R.id.buttonSearchScan);
        buttonScanSearch.setOnClickListener((View.OnClickListener) this);
        textViewCodeSearch = (TextView) findViewById(R.id.textViewCodeSearch);
        textViewNomSearch = (TextView) findViewById(R.id.textViewNomSearch);
        textViewMatiereSearch = (TextView) findViewById(R.id.textViewMatiereSearch);
    }

    @Override
    public void onClick(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        // Si résultat invalide, toast "annulé"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Annulé", Toast.LENGTH_SHORT).show();
            } else {
                // Résultat non nul
                CharSequence code = intentResult.getContents();
                //searchProduct(code.toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    protected void searchProduct(String codeLu){
        databaseReferenceProduits.child(codeLu).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Produit produit = dataSnapshot.getValue(Produit.class);
                        Log.d(TAG, "Nom: " + produit.getNom() + ", Code " + produit.getCode());
                        Log.d("NOM", "" + produit.getNom());
                        textViewCodeSearch.setText(produit.getCode());
                        //textViewMatiereSearch.setText(produit.getMatiere());
                        textViewNomSearch.setText(produit.getNom());
                    } else {
                        textViewCodeSearch.setText("");
                        textViewMatiereSearch.setText("");
                        textViewNomSearch.setText("");
                        Toast.makeText(getBaseContext(), "Le produit n'existe pas dans la base de données", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    /*protected void searchProduct1(String codeLu){
        databaseReferenceProduits.child(codeLu).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Produit produit = dataSnapshot.getValue(Produit.class);
                Log.d(TAG, "Nom: " + produit.getNom() + ", Code " + produit.getCode());
                Log.d("NOM", "" + produit.getNom());
                textViewCodeSearch.setText(produit.getCode());
                textViewMatiereSearch.setText(produit.getMatiere());
                textViewNomSearch.setText(produit.getNom());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ERROR","SEARCH DATABASE");
            }
        });
    }*/
}
