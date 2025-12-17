package com.example.trello.Modele;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Set;

/**
 * Classe abstraite représentant une tâche
 * Pattern Composite : composant de base
 */
public abstract class Tache {
    protected String libelle;
    protected int etat;
    protected String commentaire;
    protected LocalDate dateDebut;
    protected LocalDate dateFin;
    protected String colonne;
    protected int dureeEstimee;
    protected String color;
    protected String jour;

    // États possibles
    public static final int ETAT_A_FAIRE = 0;
    public static final int ETAT_EN_COURS = 1;
    public static final int ETAT_TERMINE = 2;
    public static final int ETAT_ARCHIVE = 3;






    Set<String> JOURS_AUTORISES = Set.of("Lundi", "Mardi", "Mercredi","Jeudi","Vendredi","Samedi","Dimanche");

    /**
     * Constructeur de Tache
     */
    public Tache(String libelle, String commentaire,String jour, String colonne, int dureeEstimee) {
        this.libelle = libelle;
        this.etat = ETAT_A_FAIRE;
        this.commentaire = commentaire;
        if (JOURS_AUTORISES.contains(jour)) {
            this.jour=jour;
        } else {this.jour="Lundi";}
        this.colonne = colonne;
        this.dureeEstimee = dureeEstimee;
        this.color = "#C5D3D0";
    }

    public Tache(String libelle ,String jour, String commentaire) {
        this.libelle = libelle;
        if (JOURS_AUTORISES.contains(jour)) {
            this.jour=jour;
        } else {this.jour="Lundi";}
        this.commentaire = commentaire;
        this.etat = ETAT_EN_COURS;
        this.color = "#C5D3D0";
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        if (JOURS_AUTORISES.contains(jour)) {
            this.jour = jour;
        } else {
            this.jour = "Lundi"; // Valeur par défaut de sécurité
        }
    }

        // --- Méthodes à SUPPRIMER ---
        // remove: getDateDebut(), getDateFin(), getDateDebutLocal(), getDateFinLocal(), setDates()

    /**
     * @return Le libellé de la tâche
     */
    public String getLibelle() {
        return libelle;
    }

    /**
     * @return true si la tâche est archivée
     */
    public boolean isArchived() {
        return etat == ETAT_ARCHIVE;
    }

    /**
     * @return L'état de la tâche sous forme de chaîne
     */
    public String getEtat() {
        switch(etat) {
            case ETAT_A_FAIRE: return "À faire";
            case ETAT_EN_COURS: return "En cours";
            case ETAT_TERMINE: return "Terminé";
            case ETAT_ARCHIVE: return "Archivé";
            default: return "Inconnu";
        }
    }

    /**
     * @return Le commentaire de la tâche
     */
    public String getCommentaire() {
        return commentaire;
    }


    /**
     * @return La colonne actuelle de la tâche
     */
    public String getColonne() {
        return colonne;
    }

    /**
     * Définit la colonne de la tâche
     */
    public void setColonne(String colonne) {
        this.colonne = colonne;
    }

    /**
     * Définit l'état de la tâche
     */
    public void setEtat(int etat) {
        this.etat = etat;
    }

    /**
     * @return La durée estimée en jours
     */
    public int getDureeEstimee() {
        return dureeEstimee;
    }

    /**
     * Construit la liste des dépendances de cette tâche
     * @return Liste des tâches dont dépend cette tâche
     */
    public abstract LinkedList<Tache> construirDependance();

    public String getColor() {
        return this.color;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }


    /**
     * Setter pour la durée
     */
    public void setDureeEstimee(int dureeEstimee) {
        this.dureeEstimee = dureeEstimee;
    }

    /**
     * Setter pour la couleur
     */
    public void setColor(String color) {
        this.color = color;
    }

    public String toString() {
        return  "==========TACHE==========\nLibelle : "+libelle+"\nCommentaire : "+commentaire+"\nColonne : "+colonne+"\nJour : "+jour+"\n";
    }

}