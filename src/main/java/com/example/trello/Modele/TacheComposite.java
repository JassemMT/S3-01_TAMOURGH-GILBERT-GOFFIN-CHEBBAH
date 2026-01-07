package com.example.trello.Modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TacheComposite extends Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Tache> enfants;

    /**
     * Constructeur standard avec LocalDate
     */
    public TacheComposite(String libelle, String commentaire, LocalDate dateDebut, String colonne, int dureeEstimee) {
        super(libelle, commentaire, dateDebut, colonne, dureeEstimee);
        this.enfants = new ArrayList<>();
    }

    /**
     * Constructeur de promotion (Copie les données d'une TacheSimple)
     */
    public TacheComposite(TacheSimple t) {
        // Modification ici : on utilise getDateDebut()
        super(t.getLibelle(), t.getCommentaire(), t.getDateDebut(), t.getColonne(), t.getDureeEstimee());
        this.enfants = new ArrayList<>();

        // Copie des attributs visuels et d'état
        this.setEtat(t.getEtat());
        this.setColor(t.getColor());
    }

    public TacheComposite() {
        super();
        // IMPORTANT : Initialiser la liste pour éviter le NullPointerException
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