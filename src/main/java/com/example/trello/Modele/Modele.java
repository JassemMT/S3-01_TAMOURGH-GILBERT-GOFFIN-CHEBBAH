package com.example.trello.Modele;

import com.example.trello.Vue.Observateur;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe principale de l'application (Modèle)
 * Implémente le pattern Observateur côté Sujet
 */
public class Modele implements Sujet {
    // Constantes pour les types de vue
    public static final int VUE_KANBAN = 1;
    public static final int VUE_LISTE = 2;
    public static final int VUE_GANTT = 3;

    private int type_vue;
    private List<Observateur> observateurs;
    private List<Tache> taches;
    private int prochainId;
    private Map<Integer, Tache> mapTaches; // Pour accès rapide par ID

    /**
     * Constructeur de App
     */
    public Modele() {
        this.observateurs = new ArrayList<>();
        this.taches = new ArrayList<>();
        this.mapTaches = new HashMap<>();
        this.type_vue = VUE_KANBAN; // Vue par défaut
        this.prochainId = 1;
    }

    /**
     * Ajoute un observateur
     */
    @Override
    public void ajouterObservateur(Observateur o) {
        if (o != null && !observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    /**
     * Supprime un observateur
     */
    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    /**
     * Notifie tous les observateurs
     */
    @Override
    public void notifierObservateur() {
        for (Observateur obs : observateurs) {
            obs.actualiser(this);
        }
    }

    /**
     * Définit le type de vue actif
     */
    public void setTypeVue(int type) {
        if (type >= VUE_KANBAN && type <= VUE_GANTT) {
            this.type_vue = type;
            notifierObservateur();
        }
    }

    /**
     * @return Le type de vue actuel
     */
    public int getTypeVue() {
        return type_vue;
    }

    /**
     * Retourne les tâches d'un jour spécifique
     * @param jour La date au format String (sera parsée)
     * @return Liste des tâches pour ce jour
     */
    public List<Tache> getTachesJour(String jour) {
        List<Tache> tachesJour = new ArrayList<>();

        try {
            LocalDate date = LocalDate.parse(jour);
            for (Tache tache : taches) {
                LocalDate debut = tache.getDateDebutLocal();
                LocalDate fin = tache.getDateFinLocal();

                if (debut != null && fin != null) {
                    // Vérifie si la date est dans l'intervalle
                    if (!date.isBefore(debut) && !date.isAfter(fin)) {
                        tachesJour.add(tache);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur de parsing de date: " + e.getMessage());
        }

        return tachesJour;
    }

    /**
     * Retourne les colonnes avec leurs tâches (pour vue Kanban)
     * @return Map avec nom de colonne en clé et liste de tâches en valeur
     */
    public Map<String, List<Tache>> getColonnes() {
        Map<String, List<Tache>> colonnes = new LinkedHashMap<>();

        // Colonnes standards
        colonnes.put("À faire", new ArrayList<>());
        colonnes.put("En cours", new ArrayList<>());
        colonnes.put("Terminé", new ArrayList<>());

        // Répartit les tâches dans les colonnes
        for (Tache tache : taches) {
            if (!tache.isArchived()) {
                String colonne = tache.getColonne();
                if (colonne == null || colonne.isEmpty()) {
                    colonne = "À faire";
                }

                if (!colonnes.containsKey(colonne)) {
                    colonnes.put(colonne, new ArrayList<>());
                }
                colonnes.get(colonne).add(tache);
            }
        }

        return colonnes;
    }

    /**
     * Retourne une tâche par son ID
     */
    public Tache getTache(int id) {
        return mapTaches.get(id);
    }

    /**
     * Retourne toutes les tâches non archivées
     */
    public List<Tache> getTaches() {
        return taches.stream()
                .filter(t -> !t.isArchived())
                .collect(Collectors.toList());
    }

    /**
     * Retourne toutes les tâches (incluant archivées)
     */
    public List<Tache> getToutesToches() {
        return new ArrayList<>(taches);
    }

    /**
     * Ajoute une tâche à l'application
     */
    public void ajouterTache(Tache tache) {
        if (tache != null && !taches.contains(tache)) {
            taches.add(tache);
            mapTaches.put(prochainId++, tache);
            notifierObservateur();
        }
    }

    /**
     * Supprime une tâche par son ID
     */
    public void supprimerTache(int id) {
        Tache tache = mapTaches.get(id);
        if (tache != null) {
            supprimerTache(tache);
        }
    }

    /**
     * Supprime une tâche
     */
    public void supprimerTache(Tache tache) {
        if (tache != null) {
            taches.remove(tache);
            // Retire de la map
            mapTaches.values().remove(tache);
            notifierObservateur();
        }
    }

    /**
     * Déplace une tâche vers une nouvelle colonne
     */
    public void deplacerTache(Tache tache, String nouvelleColonne) {
        if (tache != null && nouvelleColonne != null) {
            tache.setColonne(nouvelleColonne);

            // Met à jour l'état en fonction de la colonne
            switch (nouvelleColonne) {
                case "À faire":
                    tache.setEtat(Tache.ETAT_A_FAIRE);
                    break;
                case "En cours":
                    tache.setEtat(Tache.ETAT_EN_COURS);
                    break;
                case "Terminé":
                    tache.setEtat(Tache.ETAT_TERMINE);
                    break;
            }

            notifierObservateur();
        }
    }

    /**
     * Obtient les dépendances d'une tâche
     */
    public LinkedList<Tache> getDependance(Tache tache) {
        LinkedList<Tache> dependances = new LinkedList<>();

        if (tache == null) {
            return dependances;
        }

        // Si la tâche est composite, utilise sa méthode
        if (tache instanceof TacheComposite) {
            dependances.addAll(((TacheComposite) tache).construirDependance());
        }

        // Cherche dans toutes les tâches composites si la tâche en fait partie
        for (Tache t : taches) {
            if (t instanceof TacheComposite) {
                LinkedList<Tache> deps = ((TacheComposite) t).getDependance(tache);
                if (!deps.isEmpty()) {
                    dependances.addAll(deps);
                }
            }
        }

        return dependances;
    }

    /**
     * Archive une tâche
     */
    public void archiverTache(Tache tache) {
        if (tache != null) {
            tache.setEtat(Tache.ETAT_ARCHIVE);
            notifierObservateur();
        }
    }

    /**
     * Ajoute une nouvelle colonne
     */
    public void ajouterColonne(String nomColonne) {
        if (nomColonne != null && !nomColonne.trim().isEmpty()) {
            // La colonne sera créée automatiquement lors du prochain appel à getColonnes()
            notifierObservateur();
        }
    }



}