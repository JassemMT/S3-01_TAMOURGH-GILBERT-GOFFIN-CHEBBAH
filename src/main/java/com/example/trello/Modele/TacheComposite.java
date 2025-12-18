package com.example.trello.Modele;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe représentant une tâche composite (conteneur du pattern Composite)
 * Une tâche composite contient des sous-tâches
 */
public class TacheComposite extends Tache {
    private List<Tache> enfants;

    /**
     * Constructeur de TacheComposite
    */
    public TacheComposite(String libelle, String commentaire, String jour, String colonne, int dureeEstimee) {
        super(libelle, commentaire, jour, colonne, dureeEstimee);
    }
    /**
     * Ajoute une sous-tâche à cette tâche composite
     */
    public void ajouterEnfant(Tache tache) {
        if (tache != null && !enfants.contains(tache)) {
            enfants.add(tache);
        }
    }

    /**
     * Supprime une sous-tâche de cette tâche composite
     */
    public void supprimerEnfant(Tache tache) {
        enfants.remove(tache);
    }

    /**
     * @return La liste des sous-tâches
     */
    public List<Tache> getEnfants() {
        return new ArrayList<>(enfants);
    }

    /**
     * Construit la liste des dépendances incluant toutes les sous-tâches
     */
    @Override
    public LinkedList<Tache> construirDependance() {
        LinkedList<Tache> dependances = new LinkedList<>();

        // Ajoute toutes les sous-tâches comme dépendances
        for (Tache enfant : enfants) {
            dependances.add(enfant);
            // Récursivement ajoute les dépendances des sous-tâches
            dependances.addAll(enfant.construirDependance());
        }

        return dependances;
    }

    /**
     * Obtient les dépendances d'une tâche spécifique
     * @param tache La tâche dont on veut les dépendances
     * @return Liste des dépendances
     */
    public LinkedList<Tache> getDependance(Tache tache) {
        LinkedList<Tache> dependances = new LinkedList<>();

        // Si la tâche est une sous-tâche directe
        if (enfants.contains(tache)) {
            // Retourne les autres sous-tâches comme dépendances potentielles
            for (Tache enfant : enfants) {
                if (!enfant.equals(tache)) {
                    dependances.add(enfant);
                }
            }
        } else {
            // Cherche récursivement dans les sous-tâches composites
            for (Tache enfant : enfants) {
                if (enfant instanceof TacheComposite) {
                    LinkedList<Tache> subDeps = ((TacheComposite) enfant).getDependance(tache);
                    if (!subDeps.isEmpty()) {
                        dependances.addAll(subDeps);
                    }
                }
            }
        }

        return dependances;
    }
}