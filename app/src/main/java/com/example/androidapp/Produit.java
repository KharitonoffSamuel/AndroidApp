package com.example.androidapp;

public class Produit {
    private CharSequence code;
    private String nom;
    private String matiere;

    public Produit(CharSequence code, String nom, String matiere) {
        this.code = code;
        this.nom = nom;
        this.matiere = matiere;
    }

    public CharSequence getCode() {
        return code;
    }

    public void setCode(CharSequence code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }
}
