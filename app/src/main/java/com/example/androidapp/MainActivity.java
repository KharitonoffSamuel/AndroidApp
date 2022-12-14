package com.example.androidapp;

import static androidx.appcompat.widget.ResourceManagerInternal.get;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button boutonScan, boutonAjouter, boutonAjout;
    protected TextView nomText, codeText, emballageText;
    private ArrayList<String> poubelleJaune, poubelleVerte;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference databaseReferenceProduits = database.getReference().child("Produits");

    SearchView searchView;
    ListView listView;
    ArrayList<String> list;
    ArrayAdapter<String > adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boutonScan = findViewById(R.id.buttonScan);
        boutonScan.setOnClickListener(this);

        boutonAjouter = findViewById(R.id.buttonAjouter);
        boutonAjouter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (boutonAjouter.equals(view)) {
            Intent intent = new Intent(this, AjoutProduit.class);
            startActivity(intent);
            //showDialogSearch();
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

    private void showDialogInfosProduit(Produit produit){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetdialog_layout);

        ArrayList<String> emballageFiltreJaune = new ArrayList<>();
        ArrayList<String> emballageFiltreVerte = new ArrayList<>();
        ArrayList<String> emballageFiltreNoire = new ArrayList<>();

        LinearLayout rescanLayout = dialog.findViewById(R.id.layoutReScan);
        LinearLayout backHomeLayout = dialog.findViewById(R.id.layoutBackHome);
        LinearLayout ajoutProduit = dialog.findViewById(R.id.layoutAjout);

        nomText = dialog.findViewById(R.id.textNom);
        codeText = dialog.findViewById(R.id.textCode);

        nomText.setText(produit.getNom());
        codeText.setText(produit.getCode());


        RecyclerView recyclerViewJaune = dialog.findViewById(R.id.recyclerViewPoubelleJaune);
        RecyclerView recyclerViewVerte = dialog.findViewById(R.id.recyclerViewPoubelleVerte);
        RecyclerView recyclerViewNoire = dialog.findViewById(R.id.recyclerViewPoubelleNoire);


        // Filtrage des emballages avec "Papier", "Carton", "Plastique", ...
        for (int i = 0;i<produit.getMatiere().size();i++){
            String emballageTest = produit.getMatiere().get(i).toUpperCase(); // Pour la comparaison
            String emballage = produit.getMatiere().get(i); // Pour l'affichage plus propre

            // POUBELLE JAUNE
            if(emballageTest.contains(("PLASTIQUE")) || emballageTest.contains("PAPIER") || emballageTest.contains("CARTON") || emballageTest.contains("ALUMINIUM")) {
                emballageFiltreJaune.add(emballage);
            }
            // POUBELLE VERTE
            else if (emballageTest.contains("VERRE")) emballageFiltreVerte.add(emballage);
            //POUBELLE ORDURES MENAGERES
            else emballageFiltreNoire.add(emballage);
        }

        setRecyclerViews(recyclerViewJaune,emballageFiltreJaune);
        setRecyclerViews(recyclerViewVerte,emballageFiltreVerte);
        setRecyclerViews(recyclerViewNoire,emballageFiltreNoire);


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

    private void showDialogSearch(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetdialogsearch_layout);

        searchView = (SearchView) dialog.findViewById(R.id.searchView);
        listView = (ListView) dialog.findViewById(R.id.lv1);

        list = new ArrayList<>();
        list.add("Apple");
        list.add("Banana");
        list.add("Pineapple");
        list.add("Orange");
        list.add("Lychee");
        list.add("Gavava");
        list.add("Peech");
        list.add("Melon");
        list.add("Watermelon");
        list.add("Papaya");


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);


        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //    adapter.getFilter().filter(newText);
                return false;
            }
        });*/

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showAlertBoxProduitInexistant(String codeLu){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Produit inexistant dans la base de données");
            alert.setMessage("Le produit n'existe pas encore dans la base de données. Souhaitez-vous l'ajouter maintenant ?");
            alert.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this,AjoutProduit.class);
                    intent.putExtra("Code", codeLu);
                    startActivity(intent);
                }
            });
            alert.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alert.create().show();
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
                searchProduct(code.toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void searchProduct(String codeLu) {
        db.collection("Produits")
                .whereEqualTo("code", codeLu)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Produit produit = new Produit();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            produit = documentSnapshot.toObject(Produit.class);
                            showDialogInfosProduit(produit);
                        }
                        if(produit.getNom() == null) showAlertBoxProduitInexistant(codeLu);
                    }
                });
    }



    protected void setRecyclerViews(RecyclerView recyclerView, ArrayList<String> emballageFiltre){
        RVAdapterAfficheEmballages rvAdapterAfficheEmballages = new RVAdapterAfficheEmballages(emballageFiltre);
        recyclerView.setAdapter(rvAdapterAfficheEmballages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.hasFixedSize();
    }

}