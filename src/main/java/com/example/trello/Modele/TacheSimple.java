package com.example.trello.Modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TacheSimple extends Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    public TacheSimple(String libelle, String commentaire, LocalDate dateDebut, String colonne, int dureeEstimee) {
        super(libelle, commentaire, dateDebut, colonne, dureeEstimee);
    }

    public TacheSimple(String libelle, String commentaire) {
        super(libelle, commentaire);
    }

    public TacheSimple() { super(); }

    @Override
    public void ajouterEnfant(Tache t) {
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