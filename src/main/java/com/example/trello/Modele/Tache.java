package com.example.trello.Modele;

import java.io.Serializable;
import java.util.*;

/**
 * Classe unique représentant une tâche.
 * Gère les données, l'état, la position (colonne/jour) et la hiérarchie (enfants).
 */
public class Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String libelle;
    protected String commentaire;
    protected int etat;           // Avancement (À faire, Terminé...)
    protected String colonne;     // Catégorie visuelle
    protected String jour;        // Planification (Lundi, Mardi...)
    protected int dureeEstimee;
    protected String color;

    // Hiérarchie : Liste des sous-tâches
    private List<Tache> enfants;

    // Constantes d'état
    public static final int ETAT_A_FAIRE = 0;
    public static final int ETAT_EN_COURS = 1;
    public static final int ETAT_TERMINE = 2;
    public static final int ETAT_ARCHIVE = 3;

    // Jours autorisés
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
        this.enfants = new ArrayList<>(); // Initialisation de la liste

        setJour(jour); // Vérification du jour
    }

    /**
     * Constructeur simplifié
     */
    public Tache(String libelle, String commentaire) {
        this(libelle, commentaire, "Lundi", "Principal", 0);
    }

    // --- GESTION DES DÉPENDANCES (Enfants) ---

    /**
     * Ajoute une sous-tâche
     */
    public void ajouterEnfant(Tache t) {
        if (t != null && !enfants.contains(t) && t != this) {
            enfants.add(t);
        }
    }

    /**
     * Retourne une copie de la liste des enfants
     */
    public List<Tache> getEnfants() {
        return new ArrayList<>(enfants);
    }

    /**
     * Vérifie si la tâche a des enfants (utile pour l'affichage)
     */
    public boolean aDesEnfants() {
        return enfants != null && !enfants.isEmpty();
    }

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

    public String getJour() { return jour; }
    public void setJour(String jour) {
        if (JOURS_AUTORISES.contains(jour)) this.jour = jour;
        else this.jour = "Lundi";
    }

    public boolean isArchived() { return etat == ETAT_ARCHIVE; }

    /**
     * Récupère récursivement toutes les dépendances
     */
    public LinkedList<Tache> construirDependance() {
        LinkedList<Tache> dependances = new LinkedList<>();
        for (Tache enfant : enfants) {
            dependances.add(enfant);
            dependances.addAll(enfant.construirDependance());
        }
        return dependances;
    }

    @Override
    public String toString() {
        return libelle; // Utile pour l'affichage dans les ComboBox
    }
}