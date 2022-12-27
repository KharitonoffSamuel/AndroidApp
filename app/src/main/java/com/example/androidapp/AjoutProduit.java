package com.example.androidapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class AjoutProduit extends AppCompatActivity implements View.OnClickListener, CheckboxListener {
    String code, nom;
    ArrayList<String> matiere;

    Produit produit = new Produit(code, nom, matiere);
    private EditText editTextNom, editTextCode;
    private Button boutonScannerCode, boutonValider;

    //Référence racine de la database
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference databaseReferenceProduits = database.getReference().child("Produits");

    RecyclerView recyclerView;
    ArrayList<String> emballages;
    RVAdapter rvAdapter;

    // Tableau des emballages pour la RV
    void initData() {
        emballages = new ArrayList<>();
        emballages.add("[Plastique] Bouteille");
        emballages.add("[Plastique] Bouchon");
        emballages.add("[Plastique] Emballage");
        emballages.add("[Plastique] Barquette");
        emballages.add("[Plastique] Flacon");

        emballages.add("[Carton] Boite");
        emballages.add("[Carton] Brique");
        emballages.add("[Carton] Barquette");
        emballages.add("[Carton] Autres");
        emballages.add("[Papier] Emballage");
        emballages.add("[Papier] Feuille");

        emballages.add("[Aluminium] Canette");
        emballages.add("[Aluminium] Opercule");
        emballages.add("[Métal] Couvercle");
        emballages.add("[Métal] Boite de conserve");
        emballages.add("[Aérosol] Aérosol");
        emballages.add("[Aluminium] Barquette");

        emballages.add("[Verre] Pot ou bocal");
        emballages.add("[Verre] Bouteille");

        emballages.add("Bouchon de liège");
        emballages.add("Emballage alimentaire non recyclable");
        emballages.add("Autre emballage non recyclable");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_produit);

        boutonScannerCode = findViewById(R.id.buttonScanCode);
        boutonValider = findViewById(R.id.buttonValiderNouveauProduit);
        editTextNom = findViewById(R.id.editTextNom);
        editTextCode = findViewById(R.id.editTextCode);
        recyclerView = findViewById(R.id.recyclerViewEmballagesPlastique);

        boutonScannerCode.setOnClickListener(this);
        boutonValider.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String codeBarre = extras.getString("Code");
            editTextCode.setText(codeBarre);
        }

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

        // Si on appuie sur valider, on va envoyer les infos sur la database
        else if (boutonValider.equals(view)) {
            // Si tous les champs ont été remplis
            if(editTextNom.getText().toString().length() != 0 && editTextCode.getText().toString().length() != 0) {
                //On envoie les champs dans les zones et la database
                produit.setNom(editTextNom.getText().toString());
                produit.setCode(editTextCode.getText().toString());

                // Envoi dans la base de données
                db.collection("Produits")
                        .add(produit)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("TEST", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TEST", "Error adding document", e);
                            }
                        });

                databaseReferenceProduits.child(produit.getCode()).setValue(produit);
                Toast.makeText(getBaseContext(), "Le produit est enregistré !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else{
                // Alerte en cas de champs vides
                showAlertBoxChampsVides();
            }
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

    // On met à jour le tableau des matières en temps réel
    @Override
    public void onCheckboxChange(ArrayList<String> arrayList) {
        produit.setMatiere(arrayList);
    }

    private void setRecyclerView(){
        // RECYCLER VIEW - LISTE EMBALLAGES
        initData();
        rvAdapter = new RVAdapter(this.emballages,this,this::onCheckboxChange);
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.hasFixedSize();
    }

    // Fonction d'affichage de l'alerte en cas de champs vides lors de la création de produit
    private void showAlertBoxChampsVides(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Champs vides");
        alert.setMessage("Certains champs sont vides : merci de renseigner toutes les informations");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.create().show();
    }
}