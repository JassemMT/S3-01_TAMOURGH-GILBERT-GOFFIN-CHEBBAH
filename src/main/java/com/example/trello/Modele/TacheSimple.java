package com.example.trello.Modele;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TacheSimple extends Tache {

    // Constructeur complet
    public TacheSimple(String libelle, String commentaire, String jour, String colonne, int dureeEstimee) {
        super(libelle, commentaire, jour, colonne, dureeEstimee);
    }

    // Constructeur simplifié
    public TacheSimple(String libelle, String commentaire) {
        super(libelle, commentaire);
    }

    @Override
    public void ajouterEnfant(Tache t) {
        // Une tâche simple ne peut pas avoir d'enfants.
        // On peut soit ne rien faire, soit lancer une exception, soit afficher un log.
        // Pour l'instant, on ignore silencieusement ou on prévient la console.
        System.out.println("Impossible d'ajouter un enfant à une Tâche Simple.");
    }

    @Override
    public List<Tache> getEnfants() {
        // Retourne une liste vide pour éviter les NullPointerException dans les vues
        return Collections.emptyList();
    }

    @Override
    public boolean aDesEnfants() {
        return false;
    }

    @Override
    public LinkedList<Tache> construirDependance() {
        // Pas d'enfants, donc pas de dépendances descendantes
        return new LinkedList<>();
    }
}