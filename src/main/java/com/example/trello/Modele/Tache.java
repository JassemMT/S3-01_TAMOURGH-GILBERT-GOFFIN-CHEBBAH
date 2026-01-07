package com.example.trello.Modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public abstract class Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String libelle;
    protected String commentaire;
    protected int etat;
    protected String colonne;
    protected LocalDate dateDebut;
    protected int dureeEstimee;
    protected String color;

    // Constantes d'état
    public static final int ETAT_A_FAIRE = 0;
    public static final int ETAT_EN_COURS = 1;
    public static final int ETAT_TERMINE = 2;
    public static final int ETAT_ARCHIVE = 3;

    public Tache(String libelle, String commentaire, LocalDate dateDebut, String colonne, int dureeEstimee) {
        this.libelle = libelle;
        this.commentaire = commentaire;
        this.colonne = colonne;
        this.dureeEstimee = dureeEstimee;
        this.etat = ETAT_A_FAIRE;
        this.color = "#C5D3D0";
        this.dateDebut = (dateDebut != null) ? dateDebut : LocalDate.now();
    }

    public Tache(String libelle, String commentaire) {
        this(libelle, commentaire, LocalDate.now(), "Principal", 0);
    }

    protected Tache() {
        this.libelle = "";
        this.commentaire = "";
        this.etat = ETAT_A_FAIRE;
        this.colonne = "";
        this.dateDebut = LocalDate.now();
        this.dureeEstimee = 0;
        this.color = "#C5D3D0";
    }

    // --- MÉTHODES ABSTRAITES ---
    public abstract void ajouterEnfant(Tache t);
    public abstract List<Tache> getEnfants();
    public abstract boolean aDesEnfants();
    public abstract LinkedList<Tache> construirDependance();

    // --- NOUVEAU : Méthode nécessaire pour corriger la référence parent lors de la promotion ---
    public abstract void remplacerEnfant(Tache ancienne, Tache nouvelle);

    // --- GETTERS ET SETTERS ---
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

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) {
        if (dateDebut != null) this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() { return dateDebut.plusDays(dureeEstimee); }

    public String getNomJour() {
        String jour = dateDebut.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        return jour.substring(0, 1).toUpperCase() + jour.substring(1);
    }

    public boolean isArchived() { return etat == ETAT_ARCHIVE; }

    @Override
    public String toString() { return libelle; }
}