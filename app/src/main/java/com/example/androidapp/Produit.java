package com.example.androidapp;

import java.util.ArrayList;

public class Produit {
    private String Code;
    private String Nom;
    private String Image;

    public Produit() {
    }


    private ArrayList<String> Matiere;

    public Produit(String code, String nom, ArrayList<String> matiere, String image) {
        Code = code;
        Nom = nom;
        Matiere = matiere;
        Image = image;
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

    public void setImage(String image){this.Image = image;}

    public ArrayList<String> getMatiere() {
        return Matiere;
    }

    public void setMatiere(ArrayList<String> matiere) {
        Matiere = matiere;
    }
}
