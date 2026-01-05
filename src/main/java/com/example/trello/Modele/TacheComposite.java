package com.example.trello.Modele;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TacheComposite extends Tache {

    // Liste spécifique au composite
    private List<Tache> enfants;

    public TacheComposite(String libelle, String commentaire, String jour, String colonne, int dureeEstimee) {
        super(libelle, commentaire, jour, colonne, dureeEstimee);
        this.enfants = new ArrayList<>();
    }

    public TacheComposite(String libelle, String commentaire) {
        super(libelle, commentaire);
        this.enfants = new ArrayList<>();
    }

    @Override
    public void ajouterEnfant(Tache t) {
        if (t != null && !enfants.contains(t) && t != this) {
            enfants.add(t);
        }
    }

    @Override
    public List<Tache> getEnfants() {
        // On retourne une copie pour protéger la liste interne
        return new ArrayList<>(enfants);
    }

    @Override
    public boolean aDesEnfants() {
        return !enfants.isEmpty();
    }

    @Override
    public LinkedList<Tache> construirDependance() {
        LinkedList<Tache> dependances = new LinkedList<>();
        for (Tache enfant : enfants) {
            dependances.add(enfant);
            // Récursivité : on récupère aussi les enfants de l'enfant (si c'est aussi un composite)
            dependances.addAll(enfant.construirDependance());
        }
        return dependances;
    }
}