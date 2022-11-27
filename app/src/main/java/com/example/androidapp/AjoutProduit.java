package com.example.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class AjoutProduit extends AppCompatActivity implements View.OnClickListener, CheckboxListener {
    String code, nom;
    ArrayList<String> matiere;

    Produit produit = new Produit(code, nom, matiere);
    CheckboxListener checkboxListener;
    private TextView textViewCode, textViewNom, textViewMatiere;
    private EditText editTextNom, editTextCode, editTextMatiere;
    private Button boutonScannerCode, boutonValider;
    private CheckBox checkBoxBouteillePlastique, checkBoxPotVerre;

    //Référence racine de la database
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference databaseReferenceProduits = database.getReference().child("Produits");

    RecyclerView recyclerView;
    ArrayList<String> emballages;
    RVAdapter rvAdapter;

    void initData() {
        emballages = new ArrayList<>();
        emballages.add("Bouteille en plastique");
        emballages.add("Pot en verre");
        emballages.add("Bouchon en plastique");
        emballages.add("Bouchon de liège");
        emballages.add("Carton");
        emballages.add("Papier");
        emballages.add("Bouteille en verre");
        emballages.add("Couvercle en aluminium");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_produit);

        boutonScannerCode = findViewById(R.id.buttonScanCode);
        boutonValider = findViewById(R.id.buttonValiderNouveauProduit);
        editTextNom = findViewById(R.id.editTextNom);
        editTextCode = findViewById(R.id.editTextCode);
        recyclerView = findViewById(R.id.recyclerViewEmballages);

        boutonScannerCode.setOnClickListener(this);
        boutonValider.setOnClickListener(this);

        setRecyclerView();
    }

    @Override
    public void onClick(View view) {
        if (boutonScannerCode.equals(view)) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);
            intentIntegrator.setPrompt("Scan a barcode or QR Code");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        }
        else if (boutonValider.equals(view)) {
            // Si tous les champs ont été remplis
            if(editTextNom.getText().toString().length() != 0 && editTextCode.getText().toString().length() != 0) {
                //On envoie les champs dans les zones et la database
                produit.setNom(editTextNom.getText().toString());
                produit.setCode(editTextCode.getText().toString());
            }
            databaseReferenceProduits.child(produit.getCode()).setValue(produit);
        }
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
                editTextCode.setText(code);
                produit.setCode(code.toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCheckboxChange(ArrayList<String> arrayList) {
        Toast.makeText(this,arrayList.toString(),Toast.LENGTH_SHORT).show();
        produit.setMatiere(arrayList);
    }

    private void setRecyclerView(){
        // RECYCLER VIEW - LISTE EMBALLAGES
        initData();
        rvAdapter = new RVAdapter(this.emballages,this,this::onCheckboxChange);
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.hasFixedSize();
    }
}