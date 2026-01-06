package com.example.trello.Modele;

import java.io.Serializable;
import java.util.*;

public abstract class Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String libelle;
    protected String commentaire;
    protected int etat;
    protected String colonne;
    protected String jour;
    protected int dureeEstimee;
    protected String color;

    // Constantes d'état
    public static final int ETAT_A_FAIRE = 0;
    public static final int ETAT_EN_COURS = 1;
    public static final int ETAT_TERMINE = 2;
    public static final int ETAT_ARCHIVE = 3;

    public static final List<String> JOURS_AUTORISES = Arrays.asList(
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    );

    public Tache(String libelle, String commentaire, String jour, String colonne, int dureeEstimee) {
        this.libelle = libelle;
        this.commentaire = commentaire;
        this.colonne = colonne;
        this.dureeEstimee = dureeEstimee;
        this.etat = ETAT_A_FAIRE;
        this.color = "#C5D3D0";
        setJour(jour);
    }

    public Tache(String libelle, String commentaire) {
        this(libelle, commentaire, "Lundi", "Principal", 0);
    }

    protected Tache() {
        this.libelle = "";
        this.commentaire = "";
        this.etat = this.ETAT_A_FAIRE;
        this.colonne = "";
        this.jour = "";
        this.dureeEstimee = 0;
        this.color = "#C5D3D0";

    }

    // --- MÉTHODES ABSTRAITES (Le cœur du Composite) ---
    public abstract void ajouterEnfant(Tache t);
    public abstract List<Tache> getEnfants();
    public abstract boolean aDesEnfants();
    public abstract LinkedList<Tache> construirDependance();

    // --- GETTERS ET SETTERS COMMUNS ---
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }

    public String getColonne() { return colonne; }
    public void setColonne(String colonne) { this.colonne = colonne; }

    public int getDureeEstimee() { return dureeEstimee; }
    public void setDureeEstimee(int dureeEstimee) { this.dureeEstimee = dureeEstimee; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getJour() { return jour; }
    public void setJour(String jour) {
        if (JOURS_AUTORISES.contains(jour)) this.jour = jour;
        else this.jour = "Lundi";
    }

    public boolean isArchived() { return etat == ETAT_ARCHIVE; }

    @Override
    public String toString() { return libelle; }
}