package com.example.trello.Modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Représente une tâche simple (ou tâche "feuille") dans le pattern Composite.
 * <p>
 * Une TacheSimple est une unité de travail indivisible qui ne peut pas contenir
 * de sous-tâches. Toutes les opérations liées à la gestion des enfants
 * (ajout, suppression, récupération) sont ici inopérantes ou renvoient des listes vides.
 * </p>
 */
public class TacheSimple extends Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Constructeur complet pour initialiser une tâche avec toutes ses propriétés.
     *
     * @param libelle       Le titre ou nom de la tâche.
     * @param commentaire   Une description détaillée ou des notes.
     * @param dateDebut     La date de début planifiée.
     * @param colonne       Le nom de la colonne (état) dans laquelle se trouve la tâche.
     * @param dureeEstimee  La durée estimée en jours.
     */
    public TacheSimple(String libelle, String commentaire, LocalDate dateDebut, String colonne, int dureeEstimee) {
        super(libelle, commentaire, dateDebut, colonne, dureeEstimee);
    }

    /**
     * Constructeur simplifié.
     * Initialise la tâche avec des valeurs par défaut pour la date (aujourd'hui),
     * la colonne ("Principal") et la durée (selon l'implémentation parente).
     *
     * @param libelle     Le titre de la tâche.
     * @param commentaire La description de la tâche.
     */
    public TacheSimple(String libelle, String commentaire) {
        super(libelle, commentaire);
    }

    /**
     * Constructeur par défaut.
     * Crée une tâche vide avec des valeurs par défaut.
     */
    public TacheSimple() { super(); }

    /**
     * Tente d'ajouter une sous-tâche.
     * <p>
     * <b>Attention :</b> Cette opération est invalide pour une TacheSimple.
     * Elle affiche un message d'erreur dans la console et n'effectue aucune action.
     * </p>
     *
     * @param t La tâche à ajouter (ignorée).
     */
    @Override
    public void ajouterEnfant(Tache t) {
        System.out.println("Erreur: Impossible d'ajouter un enfant à une TacheSimple directement.");
    }

    /**
     * Récupère la liste des enfants.
     *
     * @return Une liste immuable et toujours vide, car une tâche simple n'a pas d'enfants.
     */
    @Override
    public List<Tache> getEnfants() {
        return Collections.emptyList();
    }

    /**
     * Vérifie si la tâche possède des sous-tâches.
     *
     * @return {@code false} systématiquement.
     */
    @Override
    public boolean aDesEnfants() {
        return false;
    }

    /**
     * Construit la liste des dépendances pour l'affichage (Gantt/Arbre).
     *
     * @return Une liste chaînée vide.
     */
    @Override
    public LinkedList<Tache> construirDependance() {
        return new LinkedList<>();
    }

    /**
     * Tente de remplacer une sous-tâche par une autre.
     * Cette méthode ne fait rien car il n'y a pas d'enfants à remplacer.
     *
     * @param ancienne La tâche à remplacer.
     * @param nouvelle La nouvelle tâche.
     */
    @Override
    public void remplacerEnfant(Tache ancienne, Tache nouvelle) {
        // Rien à faire pour une tâche simple
    }

    /**
     * Définit la date de début de la tâche et propage les contraintes temporelles vers le haut.
     * <p>
     * Si la nouvelle date de fin de cette tâche dépasse la date de début de son parent,
     * la méthode déclenche une mise à jour récursive du parent via le modèle.
     * </p>
     *
     * @param dateDebut La nouvelle date de début.
     * @param parent    La tâche parente (peut être null si racine).
     * @param modele    Le modèle de données permettant l'accès aux méthodes de propagation.
     */
    @Override
    public void setDateDebut(LocalDate dateDebut, Tache parent, Modele modele) {
        if (dateDebut != null) {
            this.dateDebut = dateDebut;
            if (parent != null) {
                if (this.getDateFin().isAfter(parent.getDateDebut())) {
                    Tache parentDuParent = modele.getParentDirect(parent);
                    parent.setDateDebut(this.getDateFin(), parentDuParent, modele);
                }
            }
        }
    }

    /**
     * Tente de supprimer une sous-tâche.
     * Cette méthode ne fait rien car une TacheSimple n'a pas d'enfants.
     *
     * @param t La tâche à supprimer.
     */
    @Override
    public void supprimerEnfant(Tache t) {
        // Rien à faire
    }
}