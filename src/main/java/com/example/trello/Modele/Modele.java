package com.example.trello.Modele;

import com.example.trello.Vue.Observateur;
import java.util.*;
import java.util.stream.Collectors;

public class Modele implements Sujet {
    public static final int VUE_KANBAN = 1;
    public static final int VUE_LISTE = 2;
    public static final int VUE_GANTT = 3;

    private int type_vue;
    private List<Observateur> observateurs;
    private List<Tache> taches;
    private Set<String> colonnesDisponibles; // Gestion unique des noms de colonnes

    public Modele() {
        this.observateurs = new ArrayList<>();
        this.taches = new ArrayList<>();
        this.type_vue = VUE_KANBAN;

        // Initialisation des colonnes par défaut
        this.colonnesDisponibles = new LinkedHashSet<>();
        this.colonnesDisponibles.add("À faire");
        this.colonnesDisponibles.add("En cours");
        this.colonnesDisponibles.add("Terminé");
    }

    @Override
    public void ajouterObservateur(Observateur o) {
        if (o != null && !observateurs.contains(o)) observateurs.add(o);
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierObservateur() {
        for (Observateur obs : observateurs) obs.actualiser(this);
    }

    public void setTypeVue(int type) {
        if (type >= VUE_KANBAN && type <= VUE_GANTT) {
            this.type_vue = type;
            notifierObservateur();
        }
    }

    public int getTypeVue() { return type_vue; }

    // MODIFIÉ : Comparaison simple de String pour les jours
    public List<Tache> getTachesJour(String jour) {
        List<Tache> tachesJour = new ArrayList<>();
        for (Tache tache : taches) {
            if (tache.getJour() != null && tache.getJour().equals(jour)) {
                tachesJour.add(tache);
            }
        }
        return tachesJour;
    }

    public Map<String, List<Tache>> getColonnes() {
        Map<String, List<Tache>> colonnesMap = new LinkedHashMap<>();

        // 1. Initialiser toutes les colonnes à vide
        for (String nomCol : colonnesDisponibles) {
            colonnesMap.put(nomCol, new ArrayList<>());
        }

        // 2. Remplir avec les tâches
        for (Tache tache : taches) {
            if (!tache.isArchived()) {
                String nomCol = tache.getColonne();

                // Si colonne inconnue, repli vers "À faire"
                if (nomCol == null || !colonnesDisponibles.contains(nomCol)) {
                    nomCol = "À faire";
                    tache.setColonne(nomCol);
                }
                colonnesMap.get(nomCol).add(tache);
            }
        }
        return colonnesMap;
    }

    public List<Tache> getTaches() {
        return taches.stream().filter(t -> !t.isArchived()).collect(Collectors.toList());
    }

    public void ajouterTache(Tache tache) {
        if (tache != null && !taches.contains(tache)) {
            taches.add(tache);
            notifierObservateur();
        }
    }

    public void supprimerTache(Tache tache) {
        if (tache != null) {
            taches.remove(tache);
            notifierObservateur();
        }
    }

    public void deplacerTache(Tache tache, String nouvelleColonne) {
        if (tache != null && nouvelleColonne != null && colonnesDisponibles.contains(nouvelleColonne)) {
            tache.setColonne(nouvelleColonne);

            if ("À faire".equals(nouvelleColonne)) tache.setEtat(Tache.ETAT_A_FAIRE);
            else if ("En cours".equals(nouvelleColonne)) tache.setEtat(Tache.ETAT_EN_COURS);
            else if ("Terminé".equals(nouvelleColonne)) tache.setEtat(Tache.ETAT_TERMINE);

            notifierObservateur();
        }
    }

    public void archiverTache(Tache tache) {
        if (tache != null) {
            tache.setEtat(Tache.ETAT_ARCHIVE);
            notifierObservateur();
        }
    }

    public void ajouterColonne(String nomColonne) {
        if (nomColonne != null && !nomColonne.trim().isEmpty()) {
            if (colonnesDisponibles.add(nomColonne.trim())) {
                notifierObservateur();
            }
        }
    }

    // Stub pour compatibilité
    public LinkedList<Tache> getDependance(Tache tache) { return new LinkedList<>(); }
}