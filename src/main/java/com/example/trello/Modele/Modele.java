package com.example.trello.Modele;

import com.example.trello. Vue.Observateur;
import java.util.*;
import java. util.stream.Collectors;

/**
 * Classe principale de l'application (Modèle)
 * Implémente le pattern Observateur côté Sujet
 * Gère à la fois la vue Kanban (colonnes) et la vue Liste (jours)
 */
public class Modele implements Sujet {
    // Constantes pour les types de vue
    public static final int VUE_KANBAN = 1;
    public static final int VUE_LISTE = 2;
    public static final int VUE_GANTT = 3;

    private int type_vue;
    private List<Observateur> observateurs;
    private List<Tache> taches;
    private Map<String, List<Tache>> colonnes;
    private Map<String, List<Tache>> jours;

    /**
     * Constructeur de Modele
     */
    public Modele() {
        this.observateurs = new ArrayList<>();
        this.taches = new ArrayList<>();
        this.colonnes = new HashMap<>();
        this.jours = new HashMap<>();
        this.type_vue = VUE_LISTE; // Vue par défaut

        // Initialisation des colonnes par défaut pour la vue Kanban
        this.colonnes.put("À faire", new ArrayList<>());
        this.colonnes.put("En cours", new ArrayList<>());
        this.colonnes.put("Terminé", new ArrayList<>());

        //initialisation des jours pour la vue liste
        this.jours.put("lundi", new ArrayList<>());
        this.jours.put("mardi", new ArrayList<>());
        this.jours.put("mercredi", new ArrayList<>());
        this.jours.put("jeudi", new ArrayList<>());
        this.jours.put("vendredi", new ArrayList<>());
        this.jours.put("samedi", new ArrayList<>());
        this.jours.put("dimanche", new ArrayList<>());
    }

    /**
     * Ajoute un observateur
     */
    @Override
    public void ajouterObservateur(Observateur o) {
        if (o != null && ! observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    /**
     * Supprime un observateur
     */
    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs. remove(o);
    }

    /**
     * Notifie tous les observateurs
     */
    @Override
    public void notifierObservateur() {
        for (Observateur obs :  observateurs) {
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
     * Retourne les tâches d'un jour spécifique (pour vue Liste)
     * @param jour Le nom du jour (lundi, mardi, etc.)
     * @return Liste des tâches pour ce jour
     */
    public List<Tache> getTachesJour(String jour) {
        /*List<Tache> tachesJour = new ArrayList<>();
        for (Tache tache : taches) {
            if (! tache.isArchived() && tache.getJour() != null && tache.getJour().equals(jour)) {
                tachesJour.add(tache);
            }
        }
        return tachesJour;*/
        return jours.get(jour);
    }

    /**
     * Retourne les tâches organisées par jour pour la semaine (pour vue Liste)
     * @return Map avec nom du jour en clé et liste de tâches en valeur
     */
    public Map<String, List<Tache>> getTachesSemaine() {
        /*Map<String, List<Tache>> tachesSemaine = new LinkedHashMap<>();

        String[] jours = {"lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"};

        for (String jour : jours) {
            tachesSemaine.put(jour, getTachesJour(jour));
        }

        return tachesSemaine;*/
        return jours;
    }

    /**
     * Retourne les colonnes avec leurs tâches (pour vue Kanban)
     * @return Map avec nom de colonne en clé et liste de tâches en valeur
     */
    public Map<String, List<Tache>> getColonnes() {
        /*Map<String, List<Tache>> colonnesMap = new LinkedHashMap<>();

        // 1. Initialiser toutes les colonnes à vide
        for (String nomCol : colonnesDisponibles) {
            colonnesMap.put(nomCol, new ArrayList<>());
        }

        // 2. Remplir avec les tâches
        for (Tache tache : taches) {
            if (!tache.isArchived()) {
                String nomCol = tache.getColonne();

                // Si colonne inconnue, repli vers "À faire"
                if (nomCol == null || !colonnesDisponibles. contains(nomCol)) {
                    nomCol = "À faire";
                    tache.setColonne(nomCol);
                }
                colonnesMap.get(nomCol).add(tache);
            }
        }
        return colonnesMap;*/
        return colonnes;
    }

    /**
     * Retourne toutes les tâches non archivées
     */
    public List<Tache> getTaches() {
        return taches.stream()
                .filter(t -> ! t.isArchived())
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
            if (tache.getColonne() != null) {
                if (colonnes.containsKey(tache.getColonne())) colonnes.put(tache.getColonne(), new ArrayList<>());
                colonnes.get(tache.getColonne()).add(tache);
            }
            if (tache.getJour() != null) {
                String jour = tache.getJour();
                if (jour.equals("lundi") || jour.equals("mardi") || jour.equals("mercredi") || jour.equals("jeudi") || jour.equals("vendredi") || jour.equals("samedi") || jour.equals("dimanche")) {
                    jours.get(tache.getJour()).add(tache);
                }
            }
            notifierObservateur();
            System.out.println("ajout d'une tâche dans le modèle");
            System.out.println(tache);
        }
    }

    /**
     * Modifie une tâche existante
     */
    public void modifierTache(Tache tache) {
        if (tache != null && taches.contains(tache)) {
            notifierObservateur();
        }
    }


    /**
     * Supprime une tâche
     */
    public void supprimerTache(Tache tache) {
        if (tache != null) {
            taches.remove(tache);
            if (tache.getColonne() != null) {
                colonnes.get(tache.getColonne()).remove(tache);
            }
            if (tache.getJour() != null) {
                jours.get(tache.getJour()).remove(tache);
            }
            notifierObservateur();
        }
    }

    /**
     * Déplace une tâche vers une nouvelle colonne (pour vue Kanban)
     */
    public void deplacerTacheColonne(Tache tache, String nouvelleColonne) {
        if (tache != null && nouvelleColonne != null && colonnes.containsKey(nouvelleColonne)) {

            System.out.println(tache.getColonne()+" --> "+nouvelleColonne);

            colonnes.get(tache.getColonne()).remove(tache);
            tache.setColonne(nouvelleColonne);
            colonnes.get(nouvelleColonne).add(tache);

            System.out.println(tache);

            // Met à jour l'état en fonction de la colonne
            //TODO réfléchir à la distinction entre état et colonne
            if ("À faire".equals(nouvelleColonne)) {
                tache.setEtat(Tache.ETAT_A_FAIRE);
            } else if ("En cours".equals(nouvelleColonne)) {
                tache.setEtat(Tache.ETAT_EN_COURS);
            } else if ("Terminé".equals(nouvelleColonne)) {
                tache.setEtat(Tache. ETAT_TERMINE);
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
            tache.setEtat(Tache. ETAT_ARCHIVE);
            notifierObservateur();
        }
    }

    /**
     * Ajoute une nouvelle colonne (pour vue Kanban)
     */
    public void ajouterColonne(String nomColonne) {
        if (nomColonne != null && !nomColonne.trim().isEmpty()) {
            colonnes.put(nomColonne, new ArrayList<>());
            System.out.println("ajout d'une colonne : "+nomColonne);
        }
    }

    /**
     * @return L'ensemble des colonnes disponibles
     */
    public Set<String> getColonnesDisponibles() {
        return colonnes.keySet();
    }
}