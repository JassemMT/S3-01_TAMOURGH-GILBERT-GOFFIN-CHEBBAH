package com.example.trello.Modele;

import com.example.trello.Vue.Observateur;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Modele implements Sujet {
    public static final int VUE_KANBAN = 1;
    public static final int VUE_LISTE = 2;
    public static final int VUE_GANTT = 3;

    private int type_vue;
    private List<Observateur> observateurs;
    private List<Tache> taches;

    // NOUVEAU : On stocke les noms de colonnes ici pour gérer l'unicité et l'ordre
    private Set<String> colonnesDisponibles;

    public Modele() {
        this.observateurs = new ArrayList<>();
        this.taches = new ArrayList<>();
        this.type_vue = VUE_KANBAN;

        // Initialisation avec LinkedHashSet pour garder l'ordre d'ajout
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

    // ... (Méthode getTachesJour inchangée) ...
    public List<Tache> getTachesJour(String jour) {
        // (Garder le code précédent pour cette méthode)
        return new ArrayList<>(); // Stub pour l'exemple
    }

    /**
     * Construit la Map des colonnes en se basant sur les colonnes définies
     */
    public Map<String, List<Tache>> getColonnes() {
        // 1. On prépare la Map avec l'ordre défini dans le Set
        Map<String, List<Tache>> colonnesMap = new LinkedHashMap<>();

        // On initialise chaque colonne avec une liste vide
        // Cela permet d'afficher même les colonnes vides dans la Vue
        for (String nomCol : colonnesDisponibles) {
            colonnesMap.put(nomCol, new ArrayList<>());
        }

        // 2. On répartit les tâches
        for (Tache tache : taches) {
            if (!tache.isArchived()) {
                String nomCol = tache.getColonne();

                // Sécurité : si la colonne de la tâche n'existe plus ou est null
                if (nomCol == null || !colonnesDisponibles.contains(nomCol)) {
                    nomCol = "À faire"; // Repli par défaut
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

            // Mise à jour de l'état (logique métier)
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

    public LinkedList<Tache> getDependance(Tache tache) {
        // (Garder le code précédent, identique à la version précédente)
        return new LinkedList<>();
    }

    /**
     * Ajoute une colonne de manière unique
     */
    public void ajouterColonne(String nomColonne) {
        if (nomColonne != null && !nomColonne.trim().isEmpty()) {
            // .add() retourne 'true' si l'élément n'existait pas, 'false' si doublon
            if (colonnesDisponibles.add(nomColonne.trim())) {
                notifierObservateur();
            } else {
                System.out.println("Colonne déjà existante : " + nomColonne);
            }
        }
    }

    // Optionnel : Méthode pour supprimer une colonne
    public void supprimerColonne(String nomColonne) {
        if(colonnesDisponibles.contains(nomColonne)) {
            for(Tache t : taches) {
                if(nomColonne.equals(t.getColonne())) {
                    t.setColonne("À faire");
                }
            }
            colonnesDisponibles.remove(nomColonne);
            notifierObservateur();
        }
    }
}