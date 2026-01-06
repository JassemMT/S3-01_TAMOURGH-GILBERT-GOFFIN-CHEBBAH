package com.example.trello.Modele;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TacheSimple extends Tache implements Serializable {


    private static final long serialVersionUID = 1L;


    public TacheSimple(String libelle, String commentaire, String jour, String colonne, int dureeEstimee) {
        super(libelle, commentaire, jour, colonne, dureeEstimee);
    }

    // Constructeur simplifié nécessaire pour la création rapide
    public TacheSimple(String libelle, String commentaire) {
        super(libelle, commentaire);
    }

    public TacheSimple() {super();}

    @Override
    public void ajouterEnfant(Tache t) {
        // Une tâche simple ne peut pas stocker d'enfants.
        // C'est le Contrôleur qui doit détecter cela et promouvoir la tâche AVANT d'appeler cette méthode.
        System.out.println("Erreur: Impossible d'ajouter un enfant à une TacheSimple directement.");
    }

    @Override
    public List<Tache> getEnfants() {
        return Collections.emptyList();
    }

    @Override
    public boolean aDesEnfants() {
        return false;
    }

    @Override
    public LinkedList<Tache> construirDependance() {
        return new LinkedList<>();
    }
}