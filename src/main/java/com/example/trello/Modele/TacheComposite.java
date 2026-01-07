package com.example.trello.Modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TacheComposite extends Tache implements Serializable {

    private static final long serialVersionUID = 1L;


    private List<Tache> enfants;

    public TacheComposite(String libelle, String commentaire, String jour, String colonne, int dureeEstimee) {
        super(libelle, commentaire, jour, colonne, dureeEstimee);
        this.enfants = new ArrayList<>();
    }

    // Constructeur utile pour la promotion (copie)
    public TacheComposite(TacheSimple t) {
        super(t.getLibelle(), t.getCommentaire(), t.getJour(), t.getColonne(), t.getDureeEstimee());
        this.enfants = new ArrayList<>();
        // On copie les autres attributs
        this.setEtat(t.getEtat());
        this.setColor(t.getColor());
    }

    public TacheComposite() {super();}

    @Override
    public void ajouterEnfant(Tache t) {
        if (t != null && !enfants.contains(t) && t != this) {
            enfants.add(t);
        }
    }
    @Override
    public void supprimerEnfant(Tache t) {
        if (enfants != null) {
            enfants.remove(t);
        }
    }

    @Override
    public List<Tache> getEnfants() {
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
            dependances.addAll(enfant.construirDependance());
        }
        return dependances;
    }
}