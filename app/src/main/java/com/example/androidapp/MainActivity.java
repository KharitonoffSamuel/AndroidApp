package com.example.androidapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button boutonScan, boutonAjouter, boutonDialogSheet, boutonAjout;
    protected TextView nomText, codeText, emballageText;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference databaseReferenceProduits = database.getReference().child("Produits");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boutonScan = findViewById(R.id.buttonScan);
        boutonScan.setOnClickListener(this);

        boutonAjouter = findViewById(R.id.buttonAjouter);
        boutonAjouter.setOnClickListener(this);

        boutonAjout = findViewById(R.id.buttonAjouter);

    }

    @Override
    public void onClick(View view) {
        if (boutonAjouter.equals(view)) {
            Intent intent = new Intent(this, AjoutProduit.class);
            startActivity(intent);
        } else if (boutonScan.equals(view)) {
            //Start QR Scanner
            scan();
        }
    }

    //Fonction qui lance le scan
    private void scan(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }

    private void showDialog(Produit produit){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetdialog_layout);

        LinearLayout nomLayout = dialog.findViewById(R.id.layoutNom);
        LinearLayout codeLayout = dialog.findViewById(R.id.layoutCode);
        LinearLayout emballageLayout = dialog.findViewById(R.id.emballageLayout);
        LinearLayout rescanLayout = dialog.findViewById(R.id.layoutReScan);
        LinearLayout backHomeLayout = dialog.findViewById(R.id.layoutBackHome);
        LinearLayout ajoutProduit = dialog.findViewById(R.id.layoutAjout);

        nomText = dialog.findViewById(R.id.textNom);
        codeText = dialog.findViewById(R.id.textCode);

        nomText.setText(produit.getNom());
        codeText.setText(produit.getCode());
        Log.d("SHEET", ""+ produit.getNom());

        rescanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Rescan", Toast.LENGTH_SHORT).show();
                scan();
            }
        });

        backHomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ajoutProduit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AjoutProduit.class);
                startActivity(intent);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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
                //editTextCode.setText(code);
                searchProduct(code.toString());
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
                    showDialog(produit);
                } else {
                    Toast.makeText(getBaseContext(), "Le produit n'existe pas dans la base de données", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}