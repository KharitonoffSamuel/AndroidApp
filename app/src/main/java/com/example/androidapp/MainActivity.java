package com.example.androidapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button boutonScan, boutonAjouter, boutonDialogSheet;
    protected TextView nomText, codeText, emballageText;
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

        boutonDialogSheet = (Button) findViewById(R.id.buttonTestDialogBottom);
        boutonDialogSheet.setOnClickListener((View.OnClickListener) this);

    }

    @Override
    public void onClick(View view) {
        if (boutonAjouter.equals(view)) {
            Intent intent = new Intent(this, AjoutProduit.class);
            startActivity(intent);
        } else if (boutonScan.equals(view)) {
            //Intent intent = new Intent(this, SearchProduct.class);
            //startActivity(intent);

            //Start QR Scanner
            scan();
        }
        else if (boutonDialogSheet.equals(view)){
            showDialog();
        }
    }

    private void scan(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();

        showDialog();
    }

    private void showDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetdialog_layout);

        LinearLayout nomLayout = dialog.findViewById(R.id.layoutNom);
        LinearLayout codeLayout = dialog.findViewById(R.id.layoutCode);
        LinearLayout emballageLayout = dialog.findViewById(R.id.emballageLayout);
        LinearLayout rescanLayout = dialog.findViewById(R.id.layoutReScan);
        LinearLayout backHomeLayout = dialog.findViewById(R.id.layoutBackHome);

        nomText = (TextView) findViewById(R.id.textNom);
        codeText = (TextView) findViewById(R.id.textCode);
        emballageText = (TextView) findViewById(R.id.textEmballage);


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
                Toast.makeText(MainActivity.this,"Back Home",Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
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
                    Log.d(TAG, "Nom: " + produit.getNom() + ", Code " + produit.getCode());
                    Log.d("NOM", "" + produit.getNom());
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