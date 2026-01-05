package com.example.trello.Modele;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe ABSTRAITE représentant le composant de base du pattern Composite.
 * Elle contient les données communes mais délègue la gestion des enfants aux sous-classes.
 */
public abstract class Tache {

    // Attributs communs à toutes les tâches (Simples ou Composites)
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

    // Liste ordonnée pour l'affichage correct dans l'éditeur
    public static final List<String> JOURS_AUTORISES = Arrays.asList(
            "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"
    );

    /**
     * Constructeur complet
     */
    public Tache(String libelle, String commentaire, String jour, String colonne, int dureeEstimee) {
        this.libelle = libelle;
        this.commentaire = commentaire;
        this.colonne = colonne;
        this.dureeEstimee = dureeEstimee;

        // Valeurs par défaut
        this.etat = ETAT_A_FAIRE;
        this.color = "#C5D3D0";

        // NOTE : On n'initialise plus 'enfants' ici, car TacheSimple n'en a pas.

        setJour(jour); // Vérification du jour
    }

    /**
     * Constructeur simplifié
     */
    public Tache(String libelle, String commentaire) {
        this(libelle, commentaire, "Lundi", "Principal", 0);
    }

    // --- MÉTHODES ABSTRAITES (Le cœur du Composite) ---
    // Ces méthodes doivent être implémentées différemment selon si c'est une feuille ou un noeud.

    /**
     * Tente d'ajouter une sous-tâche.
     */
    public abstract void ajouterEnfant(Tache t);

    /**
     * Retourne la liste des enfants (vide ou remplie).
     */
    public abstract List<Tache> getEnfants();

    /**
     * Vérifie si la tâche a des enfants (utile pour l'affichage).
     */
    public abstract boolean aDesEnfants();

    /**
     * Récupère récursivement toutes les dépendances.
     */
    public abstract LinkedList<Tache> construirDependance();


    // --- GETTERS ET SETTERS COMMUNS (Concrets) ---

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
    public String toString() {
        return libelle;
    }
}