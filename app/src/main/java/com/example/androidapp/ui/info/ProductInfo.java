package com.example.androidapp.ui.info;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.Produit;
import com.example.androidapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ProductInfo extends AppCompatActivity {

    String code;
    String nom = "Volvic";
    String matiere = "Plastique";

    Produit produit = new Produit(code, nom, matiere);
    TextView messageText;

    //Référence racine de la database
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference databaseReferenceProduits = database.getReference().child("Produits");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        //databaseReferenceProduits.child("Produits").updateChildren(produit);
        //String key = databaseReferenceProduits.push().getKey();

        EditText codeEditText = (EditText) findViewById(R.id.editTextCode);
        Button codeButton = (Button) findViewById(R.id.buttonCode);

        //Test du bouton pour créer des catégories - OK, on pourra récupérer le code barre
        codeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        code = codeEditText.getText().toString();
                        databaseReferenceProduits.child(code).setValue(produit);
                    }
                }
        );

        // referencing and initializing
        // the button and textviews
        messageText = findViewById(R.id.textContent);

        // we need to create the object
        // of IntentIntegrator class
        // which is the class of QR library
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                CharSequence code = intentResult.getContents();
                messageText.setText(code);
                produit.setCode(code);

                sendDatabase();

                //databaseReference.child("Produits").produit.getCode().toString());
                //Log.d("Code barre", "" + produit.getCode().toString());
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