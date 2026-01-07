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

    // permet d'ajouter un enfant à la tache courante
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

    // permet de récupérer la liste des enfants de la tache courante
    @Override
    public List<Tache> getEnfants() {
        return new ArrayList<>(enfants);
    }

    // permet de vérifier si la tache courante possède des enfants
    @Override
    public boolean aDesEnfants() {
        return !enfants.isEmpty();
    }

    // permet de construire les dépendances de la tache courante
    // en faisant une boucle sur les enfants de la tache
    @Override
    public LinkedList<Tache> construirDependance() {
        LinkedList<Tache> dependances = new LinkedList<>();
        for (Tache enfant : enfants) {
            dependances.add(enfant);
            dependances.addAll(enfant.construirDependance());
        }
        return dependances;
    }

    // Remplace l'ancienne référence par la nouvelle
    @Override
    public void remplacerEnfant(Tache ancienne, Tache nouvelle) {
        int index = enfants.indexOf(ancienne);
        if (index != -1) {
            enfants.set(index, nouvelle);
        }
    }

    // permet de changer la dateDebut d'une tache ainsi que la dateDebut de son parent si cela est nécessaire et de ses enfants
    public void setDateDebut(LocalDate dateDebut, Tache parent, Modele modele) {
        if (dateDebut != null) {
            this.dateDebut = dateDebut;
            if (parent != null) {
                // test la cohérence entre la date de fin de la tache et la date de début de son parent
                if (this.getDateFin().isAfter(parent.getDateDebut())) {
                    Tache parentDuParent = modele.getParentDirect(parent);
                    parent.setDateDebut(this.getDateFin(), parentDuParent, modele);
                }
            }
            // boucle sur les enfants de la tache courante vérifiant dateDebut de la tache et DateFin de son enfant
            for (Tache tache : enfants) {
                if (this.getDateDebut().isBefore(tache.getDateFin())) {
                    tache.setDateDebut(this.getDateDebut().minusDays(tache.dureeEstimee), this, modele); //récursivité pour que toute la chaine des tâches interdépendantes se déplace
                }
            }

        }
    }


    // permet de supprimer une tache donnée en la supprimant de liste enfants
    public void supprimerEnfant(Tache t) {
        if (enfants != null) {
            enfants.remove(t);
        }
    }


}