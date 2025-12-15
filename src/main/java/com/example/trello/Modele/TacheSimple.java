package com.example.trello.Modele;

import java.time.LocalDate;
import java.util.LinkedList;

/**
 * Classe représentant une tâche simple (feuille du pattern Composite)
 * Une tâche simple n'a pas de sous-tâches
 */
public class TacheSimple extends Tache {

    /**
     * Constructeur de TacheSimple
     */
    public TacheSimple(String libelle, String commentaire, LocalDate dateDebut,
                       LocalDate dateFin, String colonne, int dureeEstimee) {
        super(libelle, commentaire, dateDebut, dateFin, colonne, dureeEstimee);
    }

    public TacheSimple(String libelle, String commentaire) {
        super(libelle, commentaire);
    }

    /**
     * Retourne les dépendances de cette tâche
     * Pour une tâche simple, on peut avoir des dépendances externes
     * mais pas de sous-tâches
     */
    @Override
    public LinkedList<Tache> construirDependance() {
        // Une tâche simple n'a pas de sous-tâches
        // Les dépendances sont gérées au niveau de l'application
        return new LinkedList<>();
    }

    /**
     * Méthode helper pour obtenir les dépendances d'une tâche
     * (utilisée par App pour construire le graphe de dépendances)
     */
    public LinkedList<Tache> getDependance(Tache tache) {
        // Retourne une liste vide pour une tâche simple
        return new LinkedList<>();
    }
}