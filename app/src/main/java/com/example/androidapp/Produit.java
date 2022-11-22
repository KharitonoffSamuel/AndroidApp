package com.example.androidapp;

public class Produit {
    private String Code;
    private String Nom;
    private String[] Matiere;

    public Produit(String code, String nom, String[] matiere) {
        Code = code;
        Nom = nom;
        Matiere = matiere;
    }

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

    public String[] getMatiere() {
        return Matiere;
    }

    public void setMatiere(String[] matiere) {
        Matiere = matiere;
    }
}
