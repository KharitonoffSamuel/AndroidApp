package com.example.androidapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Boutons et éléments graphiques d'interface
    private Button boutonScan, boutonAjouter, boutonRecherche;
    protected TextView nomText, codeText, emballageText;
    private ArrayList<String> poubelleJaune, poubelleVerte;
    private SearchView searchView;
    private ListView listView;
    ArrayAdapter<String > adapter;

    // Référence vers la database
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://androidapp-41f0d-default-rtdb.europe-west1.firebasedatabase.app");
    DatabaseReference databaseReferenceProduits = database.getReference().child("Produits");


    // Création de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des boutons + écouteur
        boutonScan = findViewById(R.id.buttonScan);
        boutonScan.setOnClickListener(this);

        boutonAjouter = findViewById(R.id.buttonAjouter);
        boutonAjouter.setOnClickListener(this);

        boutonRecherche = findViewById(R.id.buttonRecherche);
        boutonRecherche.setOnClickListener(this);
    }

    // Actions lors d'un clic sur un bouton
    @Override
    public void onClick(View view) {
        // Bouton ajouter un produit
        if (boutonAjouter.equals(view)) {
            //Redirection vers l'activité d'ajout de produit
            Intent intent = new Intent(this, AjoutProduit.class);
            startActivity(intent);
        // Bouton scanner un code
        } else if (boutonScan.equals(view)) {
            //Start QR Scanner
            scan();
        }
        // Bouton rechercher un produit
        else if(boutonRecherche.equals(view)){
            // Ouverture de la view pour la recherche
            showDialogSearch();
        }
    }

    //Fonction qui lance le scan
    private void scan(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.initiateScan();
    }

    // Informations sur le produit sous forme de BottomSheetView
    private void showDialogInfosProduit(Produit produit){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetdialog_layout);

        ArrayList<String> emballageFiltreJaune = new ArrayList<>();
        ArrayList<String> emballageFiltreVerte = new ArrayList<>();
        ArrayList<String> emballageFiltreNoire = new ArrayList<>();

        LinearLayout backHomeLayout = dialog.findViewById(R.id.layoutBackHome);

        nomText = dialog.findViewById(R.id.textNom);
        codeText = dialog.findViewById(R.id.textCode);

        nomText.setText(produit.getNom());
        codeText.setText(produit.getCode());

        // Identification des RV sur le layout
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

        // Bouton Retour à l'accueil
        backHomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // Paramétrage et affichage de la boite de dialogue
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    // Recherche par nom d'un produit - BottomSheetView
    protected void showDialogSearch(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetdialogsearch_layout);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        searchView = (SearchView) dialog.findViewById(R.id.searchView);
        searchView.setIconified(false);
        listView = (ListView) dialog.findViewById(R.id.lv1);

        // Ecouteur sur la zone de recherche
        searchView.setOnQueryTextListener (new SearchView.OnQueryTextListener() {

            // Après appui sur validation de la recherche
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // Pendant qu'on écrit, en temps réel....
            @Override
            public boolean onQueryTextChange(String query) {
                ArrayList<String> list = new ArrayList<>();
                ArrayList<String> listFiltre = new ArrayList<>();

                // Recherche dans la BD
                // Listener sur un OnSucces = si on peut lire dans la BD
                db.collection("Produits")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                           @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Produit produit = documentSnapshot.toObject(Produit.class);
                                    list.add(produit.getNom().toLowerCase());
                                    Collections.sort(list); //Tri par ordre alphabétique
                                }
                                    for(int i=0;i<list.size();i++) {
                                        if(list.get(i).indexOf(query) != -1){
                                            // Affichage avec la majuscule, estétique
                                            listFiltre.add(list.get(i).substring(0,1).toUpperCase()+list.get(i).substring(1));
                                        }
                                    }
                                    adapter = new ArrayAdapter<String>(dialog.getContext(), android.R.layout.simple_list_item_1, listFiltre);
                                    listView.setAdapter(adapter);

                                    // Listener de clic sur un des items de la liste
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            // Fermeture de la boite de recherche
                                            dialog.dismiss();

                                            // Recherche du produit avec le nom, en fonction de l'item cliqué
                                            searchProductName(listFiltre.get(i));
                                        }
                                    });
                            }
                        });
                return false;
            }
        });

        // Paramétrage et affichage de la Dialog View
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    // AlertBox au cas où le produit recherché n'existe pas
    private void showAlertBoxProduitInexistant(String codeLu){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Produit inexistant dans la base de données");
            alert.setMessage("Le produit n'existe pas encore dans la base de données. Souhaitez-vous l'ajouter maintenant ?");

            // Si on appuie sur Oui, on lance l'activité AjoutProduit
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

            // Affichage de la boite d'alerte
            alert.create().show();
    }

    // Résultats du scan
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

                // Vers la fonction recherche par le code
                searchProductCode(code.toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Recherche dans la base de données avec le code
    public void searchProductCode(String codeLu) {
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

    //Recherche dans la base de données avec le nom
    public void searchProductName(String nom) {
        db.collection("Produits")
                .whereEqualTo("nom", nom)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Produit produit = new Produit();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            produit = documentSnapshot.toObject(Produit.class);
                            showDialogInfosProduit(produit);
                        }
                        if(produit.getNom() == null) showAlertBoxProduitInexistant(nom);
                    }
                });
    }



    // Fonction de création des RV
    protected void setRecyclerViews(RecyclerView recyclerView, ArrayList<String> emballageFiltre){
        RVAdapterAfficheEmballages rvAdapterAfficheEmballages = new RVAdapterAfficheEmballages(emballageFiltre);
        recyclerView.setAdapter(rvAdapterAfficheEmballages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.hasFixedSize();
    }

}