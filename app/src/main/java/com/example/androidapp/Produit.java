package com.example.androidapp;

public class Produit {
    private String Code;
    private String Nom;
    private String Matiere;
    private String Image;
    public Produit() {
    }

    public Produit(String code, String nom, String matiere,String image){
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

    public String getMatiere() {
        return Matiere;
    }

    public void setMatiere(String matiere) {
        this.Matiere = matiere;
    }
}
