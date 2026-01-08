package com.example.trello.Modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.time.Period;

/**
 * Classe abstraite représentant le composant de base du Pattern Composite.
 * <p>
 * Elle définit le contrat commun pour toutes les tâches, qu'elles soient simples (feuilles)
 * ou composites (conteneurs). Elle gère les attributs communs (libellé, dates, états)
 * et déclare les méthodes abstraites pour la gestion de la hiérarchie.
 * </p>
 *
 * @see TacheSimple
 * @see TacheComposite
 */
public abstract class Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String libelle;
    protected String commentaire;
    /** État d'avancement (0: À faire, 1: En cours, 2: Terminé, 3: Archivé). */
    protected int etat;
    protected String colonne;
    protected LocalDate dateDebut;
    protected int dureeEstimee;
    /** Code couleur hexadécimal pour l'affichage (ex: "#FFFFFF"). */
    protected String color;

    // --- Constantes d'état ---
    public static final int ETAT_A_FAIRE = 0;
    public static final int ETAT_EN_COURS = 1;
    public static final int ETAT_TERMINE = 2;
    public static final int ETAT_ARCHIVE = 3;

    /**
     * Constructeur complet.
     *
     * @param libelle       Le titre de la tâche.
     * @param commentaire   La description.
     * @param dateDebut     La date de début souhaitée.
     * @param colonne       Le nom de la colonne Kanban initiale.
     * @param dureeEstimee  La durée en jours (minimum 1 jour).
     */
    public Tache(String libelle, String commentaire, LocalDate dateDebut, String colonne, int dureeEstimee) {
        this.libelle = libelle;
        this.commentaire = commentaire;
        this.colonne = colonne;
        // Validation : une tâche dure au moins 1 jour
        if (dureeEstimee < 1) dureeEstimee = 1;
        this.dureeEstimee = dureeEstimee;
        this.etat = ETAT_A_FAIRE;
        this.color = "#C5D3D0";
        this.dateDebut = (dateDebut != null) ? dateDebut : LocalDate.now();
    }

    /**
     * Constructeur simplifié.
     * Initialise la tâche à la date du jour, dans la colonne "Principal" et avec une durée par défaut.
     *
     * @param libelle     Le titre de la tâche.
     * @param commentaire La description.
     */
    public Tache(String libelle, String commentaire) {
        this(libelle, commentaire, LocalDate.now(), "Principal", 0);
    }

    /**
     * Constructeur protégé par défaut (utilisé par la sérialisation ou les classes filles).
     */
    protected Tache() {
        this.libelle = "";
        this.commentaire = "";
        this.etat = ETAT_A_FAIRE;
        this.colonne = "";
        this.dateDebut = LocalDate.now();
        this.dureeEstimee = 1;
        this.color = "#C5D3D0";
    }

    // --- MÉTHODES ABSTRAITES (Pattern Composite) ---

    /**
     * Ajoute une sous-tâche à cette tâche.
     * @param t La tâche enfant à ajouter.
     */
    public abstract void ajouterEnfant(Tache t);

    /**
     * Retourne la liste des sous-tâches.
     * @return Une liste de tâches (vide si TacheSimple).
     */
    public abstract List<Tache> getEnfants();

    /**
     * Indique si la tâche est composée d'autres tâches.
     * @return true si Composite avec enfants, false sinon.
     */
    public abstract boolean aDesEnfants();

    /**
     * Construit une liste plate de toutes les dépendances (enfants, petits-enfants...) de manière récursive.
     * @return Une liste chaînée de toutes les tâches descendantes.
     */
    public abstract LinkedList<Tache> construirDependance();

    /**
     * Remplace une sous-tâche par une autre (utilisé pour la promotion d'objet).
     * @param ancienne La référence de l'ancienne tâche.
     * @param nouvelle La référence de la nouvelle tâche.
     */
    public abstract void remplacerEnfant(Tache ancienne, Tache nouvelle);

    /**
     * Supprime une sous-tâche spécifique.
     * @param t La tâche enfant à supprimer.
     */
    public abstract void supprimerEnfant(Tache t);

    /**
     * Méthode abstraite pour définir la date de début avec propagation des contraintes.
     * Chaque implémentation (Simple ou Composite) gère différemment la propagation aux parents/enfants.
     *
     * @param dateDebut La nouvelle date.
     * @param parent    Le parent direct (pour vérifier les conflits).
     * @param modele    Le modèle (pour l'accès global et la recherche d'ancêtres).
     */
    public abstract void setDateDebut(LocalDate dateDebut, Tache parent, Modele modele);


    // --- GETTERS & SETTERS ---

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public int getEtat() { return etat; }
    public void setEtat(int etat) { this.etat = etat; }

    public String getColonne() { return colonne; }
    public void setColonne(String colonne) { this.colonne = colonne; }

    public int getDureeEstimee() { return dureeEstimee; }

    /**
     * Définit la durée estimée (en jours).
     * Si la valeur est inférieure à 1, elle est forcée à 1.
     * @param dureeEstimee Nombre de jours.
     */
    public void setDureeEstimee(int dureeEstimee) {
        if (dureeEstimee < 1) dureeEstimee = 1;
        else{this.dureeEstimee = dureeEstimee;}
    }

    /**
     * Définit la durée estimée en tenant compte d'une contrainte parentale.
     * Ajuste la durée si la fin de la tâche dépasse le début du parent.
     *
     * @param dureeEstimee La durée souhaitée.
     * @param parent       La tâche parente pour vérification temporelle.
     */
    public void setDureeEstimee(int dureeEstimee, Tache parent) {
        this.setDureeEstimee(dureeEstimee);
        if (parent != null) {
            if (this.getDateFin().isAfter(parent.getDateDebut())) {
                this.setDureeEstimee(this.calculerComplement(parent.getDateDebut()));
            }
        }
    }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public LocalDate getDateDebut() { return dateDebut; }

    /**
     * Setter simple pour la date de début (sans propagation complexe).
     * @param dateDebut La date de début.
     */
    public void setDateDebut(LocalDate dateDebut) {
        if (dateDebut != null) this.dateDebut = dateDebut;
    }

    /**
     * Calcule la date de fin théorique.
     * @return Date de début + Durée estimée.
     */
    public LocalDate getDateFin() { return dateDebut.plusDays(dureeEstimee); }

    /**
     * Retourne le nom du jour de la semaine formatté (ex: "Lundi").
     * @return Le jour en toutes lettres, première lettre majuscule.
     */
    public String getNomJour() {
        String jour = dateDebut.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        return jour.substring(0, 1).toUpperCase() + jour.substring(1);
    }

    /**
     * Vérifie si la tâche est en état "Archivé".
     * @return true si archivée.
     */
    public boolean isArchived() { return etat == ETAT_ARCHIVE; }

    @Override
    public String toString() { return libelle; }

    /**
     * Calcule le nombre de jours séparant la date de début de cette tâche d'une date donnée.
     * Utilisé pour ajuster la durée dynamiquement.
     *
     * @param d La date cible.
     * @return Le nombre de jours (Période).
     */
    public int calculerComplement(LocalDate d) {
        Period periode = Period.between(this.dateDebut, d);
        return periode.getDays();
    }
}