package com.example.trello.Modele;
import com.example.trello.Vue.Observateur;

/**
 * Interface Sujet du pattern Observateur
 * Permet aux objets observés de notifier leurs observateurs
 */
public interface Sujet {
    /**
     * Ajoute un observateur à la liste des observateurs
     * @param o L'observateur à ajouter
     */
    void ajouterObservateur(Observateur o);

    /**
     * Supprime un observateur de la liste
     * @param o L'observateur à supprimer
     */
    void supprimerObservateur(Observateur o);

    /**
     * Notifie tous les observateurs d'un changement
     */
    void notifierObservateur();
}

