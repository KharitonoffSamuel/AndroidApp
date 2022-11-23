package com.example.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AjoutProduit extends AppCompatActivity implements View.OnClickListener{
    String code;
    String nom;
    String matiere;
    String image;

    private RecyclerView recyclerView; // la vue
    private RecyclerView.Adapter adapter; // l'adaptateur
    private RecyclerView.LayoutManager layoutManager; // le gesdtionnaire de mise en page

    Produit produit = new Produit(code, nom, matiere, image);
    private TextView textViewCode, textViewNom, textViewMatiere;
    private EditText editTextNom, editTextCode, editTextMatiere;
    private Button boutonScannerCode, boutonValider;

    //Référence racine de la database
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference databaseReferenceProduits = database.getReference().child("Produits");

    /*   Storage the picture   */
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference imagesRef = storageRef.child("images");
    StorageReference spaceRef = storageRef.child("images/space.jpg");
    StorageReference rootRef = spaceRef.getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_produit);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //databaseReferenceProduits.child("Produits").updateChildren(produit);
        //String key = databaseReferenceProduits.push().getKey();

        boutonScannerCode = (Button) findViewById(R.id.buttonScanCode);
        boutonValider = (Button) findViewById(R.id.buttonValiderNouveauProduit);
        editTextNom = (EditText) findViewById(R.id.editTextNom);
        editTextCode = (EditText) findViewById(R.id.editTextCode);
        editTextMatiere = (EditText) findViewById(R.id.editTextMatiere);

        boutonScannerCode.setOnClickListener((View.OnClickListener) this);
        boutonValider.setOnClickListener((View.OnClickListener) this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMateriaux);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // http://tvaira.free.fr/dev/android/android-recyclerview.html Pour la Recycler View
        //List<Produits> emballages = recupererDonnees();
        //adapter = new SkieurAdapter(skieurs);
        recyclerView.setAdapter(adapter);
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

            databaseReferenceProduits.child(produit.getCode().toString()).setValue(produit);
            //sendDatabase();
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

    public void sendDatabase() {
        //Envoi du code barre
        //databaseReferenceProduits.child("Code").push().setValue(produit.getCode().toString());
        //Log.d("Code barre", "" + produit.getCode().toString());
    }

        /*FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference();

        databaseReference.child("Produits").setValue(produit.getCode());
        Log.d("Code barre", ""+produit.getInstance().getCode());
        */

        /*databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String value = snapshot.getValue(String.class);
                Log.d("Lecture", "Value is: " + snapshot.child("Produits").child("32").child("nom").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
}