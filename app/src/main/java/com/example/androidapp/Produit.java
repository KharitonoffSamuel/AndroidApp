package com.example.androidapp;

import java.util.ArrayList;

public class Produit {
    private String Code;
    private String Nom;
    private ArrayList<String> Matiere;

    public Produit(String code, String nom, ArrayList<String> matiere) {
        Code = code;
        Nom = nom;
        Matiere = matiere;
    }

    public Produit() {}

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        this.Nom = nom;
    }

    public ArrayList<String> getMatiere() {
        return Matiere;
    }

    public void setMatiere(ArrayList<String> matiere) {
        Matiere = matiere;
    }
}
