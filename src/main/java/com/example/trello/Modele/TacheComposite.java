package com.example.trello.Modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TacheComposite extends Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Tache> enfants;

    public TacheComposite(String libelle, String commentaire, LocalDate dateDebut, String colonne, int dureeEstimee) {
        super(libelle, commentaire, dateDebut, colonne, dureeEstimee);
        this.enfants = new ArrayList<>();
    }

    public TacheComposite(TacheSimple t) {
        super(t.getLibelle(), t.getCommentaire(), t.getDateDebut(), t.getColonne(), t.getDureeEstimee());
        this.enfants = new ArrayList<>();
        this.setEtat(t.getEtat());
        this.setColor(t.getColor());
    }

    public TacheComposite() {
        super();
        this.enfants = new ArrayList<>();
    }

    @Override
    public void ajouterEnfant(Tache t) {
        if (t != null && !enfants.contains(t) && t != this) {
            if (t.dateDebut.isAfter(this.dateDebut)) {
                throw new RuntimeException("la tâche fille ne peut pas commencer après la tâche mère");
            } else if (t.getDateFin().isAfter(this.dateDebut)) {
                t.setDureeEstimee(t.calculerComplement(this.dateDebut));
            }
            enfants.add(t);
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

    // --- Remplace l'ancienne référence par la nouvelle ---
    @Override
    public void remplacerEnfant(Tache ancienne, Tache nouvelle) {
        int index = enfants.indexOf(ancienne);
        if (index != -1) {
            enfants.set(index, nouvelle);
        }
    }

    public void setDateDebut(LocalDate dateDebut, Tache parent, Modele modele) {
        if (dateDebut != null) {
            this.dateDebut = dateDebut;
            if (parent != null) {
                if (this.getDateFin().isAfter(parent.getDateDebut())) {
                    Tache parentDuParent = modele.getParentDirect(parent);
                    parent.setDateDebut(this.getDateFin(), parentDuParent, modele);
                }
            }
            for (Tache tache : enfants) {
                if (this.getDateDebut().isBefore(tache.getDateFin())) {
                    tache.setDateDebut(this.getDateDebut().minusDays(tache.dureeEstimee), this, modele); //récursivité pour que toute la chaine des tâches interdépendantes se déplace
                }
            }

        }
    }


    public void supprimerEnfant(Tache t) {
        if (enfants != null) {
            enfants.remove(t);
        }
    }


}