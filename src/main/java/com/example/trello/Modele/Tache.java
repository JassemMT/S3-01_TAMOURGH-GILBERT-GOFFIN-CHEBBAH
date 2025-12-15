package com.example.trello.Modele;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

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

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // États possibles
    public static final int ETAT_A_FAIRE = 0;
    public static final int ETAT_EN_COURS = 1;
    public static final int ETAT_TERMINE = 2;
    public static final int ETAT_ARCHIVE = 3;

    /**
     * Constructeur de Tache
     */
    public Tache(String libelle, String commentaire, LocalDate dateDebut,
                 LocalDate dateFin, String colonne, int dureeEstimee) {
        this.libelle = libelle;
        this.etat = ETAT_A_FAIRE;
        this.commentaire = commentaire;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.colonne = colonne;
        this.dureeEstimee = dureeEstimee;
        this.color = "#C5D3D0";
    }

    public Tache(String libelle, String commentaire) {
        this.libelle = libelle;
        this.commentaire = commentaire;
        this.etat = ETAT_EN_COURS;
        this.color = "#C5D3D0";
    }

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
     * @return La date de début formatée
     */
    public String getDateDebut() {
        return dateDebut != null ? dateDebut.format(FORMATTER) : "";
    }

    /**
     * @return La date de fin formatée
     */
    public String getDateFin() {
        return dateFin != null ? dateFin.format(FORMATTER) : "";
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
     * @return La date de début (LocalDate)
     */
    public LocalDate getDateDebutLocal() {
        return dateDebut;
    }

    /**
     * @return La date de fin (LocalDate)
     */
    public LocalDate getDateFinLocal() {
        return dateFin;
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
}