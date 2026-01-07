package com.example.trello.Modele;

import com.example.trello.Vue.Observateur;
// import com.example.trello.Data.ModeleRepository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Modele implements Sujet, Serializable {

    private static final long serialVersionUID = 1L;

    public static final int VUE_KANBAN = 1;
    public static final int VUE_LISTE = 2;
    public static final int VUE_GANTT = 3;

    private int type_vue;
    private transient List<Observateur> observateurs;
    private List<Tache> taches;
    private Set<String> colonnesDisponibles;

    public Modele() {
        this.observateurs = new ArrayList<>();
        this.taches = new ArrayList<>();
        this.type_vue = VUE_KANBAN;
        this.colonnesDisponibles = new LinkedHashSet<>();

        this.colonnesDisponibles.add("Principal");
        this.colonnesDisponibles.add("En cours");
        this.colonnesDisponibles.add("Terminé");
    }

    private Object readResolve() {
        if (observateurs == null) {
            observateurs = new ArrayList<>();
        }
        return this;
    }

    @Override public void ajouterObservateur(Observateur o) {
        if (observateurs == null) observateurs = new ArrayList<>();
        if (o != null && !observateurs.contains(o)) observateurs.add(o);
    }
    @Override public void supprimerObservateur(Observateur o) {
        if (observateurs != null) observateurs.remove(o);
    }
    @Override public void notifierObservateur() {
        if (observateurs != null) {
            for (Observateur obs : observateurs) obs.actualiser(this);
        }
    }

    public void setTypeVue(int type) { if (type >= VUE_KANBAN && type <= VUE_GANTT) { this.type_vue = type; notifierObservateur(); } }
    public int getTypeVue() { return type_vue; }

    public List<Tache> getTaches() { return taches.stream().filter(t -> !t.isArchived()).collect(Collectors.toList()); }

    public void ajouterTache(Tache tache) { if (tache != null && !taches.contains(tache)) { taches.add(tache); notifierObservateur(); } }

    // --- SUPPRESSION RÉCURSIVE ---
    public void supprimerTache(Tache tache) {
        if (tache != null) {
            supprimerRecursif(tache);
            notifierObservateur();
        }
    }

    private void supprimerRecursif(Tache t) {
        taches.remove(t);
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                supprimerRecursif(enfant);
            }
        }
    }

    // --- ARCHIVAGE RÉCURSIF ---
    public void archiverTache(Tache tache) {
        if (tache != null) {
            archiverRecursif(tache);
            notifierObservateur();
        }
    }

    private void archiverRecursif(Tache t) {
        t.setEtat(Tache.ETAT_ARCHIVE);
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                archiverRecursif(enfant);
            }
        }
    }

    public Set<String> getColonnesDisponibles() { return new LinkedHashSet<>(colonnesDisponibles); }

    public Map<String, List<Tache>> getColonnes() {
        Map<String, List<Tache>> colonnesMap = new LinkedHashMap<>();
        for (String nomCol : colonnesDisponibles) colonnesMap.put(nomCol, new ArrayList<>());
        for (Tache tache : taches) {
            if (!tache.isArchived()) {
                String nomCol = tache.getColonne();
                if (nomCol == null || !colonnesDisponibles.contains(nomCol)) {
                    nomCol = "Principal";
                    tache.setColonne(nomCol);
                }
                colonnesMap.get(nomCol).add(tache);
            }
        }
        return colonnesMap;
    }

    public void ajouterColonne(String nc) { if (nc != null && !nc.isEmpty() && colonnesDisponibles.add(nc.trim())) notifierObservateur(); }

    public void renommerColonne(String ancienNom, String nouveauNom) {
        if (ancienNom == null || nouveauNom == null || nouveauNom.trim().isEmpty()) return;
        if ("Principal".equals(ancienNom)) return;
        if (!colonnesDisponibles.contains(ancienNom) || colonnesDisponibles.contains(nouveauNom)) return;

        Set<String> nouveauSet = new LinkedHashSet<>();
        for (String col : colonnesDisponibles) {
            if (col.equals(ancienNom)) nouveauSet.add(nouveauNom);
            else nouveauSet.add(col);
        }
        this.colonnesDisponibles = nouveauSet;
        for (Tache t : taches) { if (ancienNom.equals(t.getColonne())) t.setColonne(nouveauNom); }
        notifierObservateur();
    }

    public void supprimerColonne(String nomColonne) {
        if ("Principal".equals(nomColonne)) return;
        if (colonnesDisponibles.contains(nomColonne)) {
            for (Tache t : taches) {
                if (nomColonne.equals(t.getColonne())) {
                    t.setColonne("Principal");
                }
            }
            colonnesDisponibles.remove(nomColonne);
            notifierObservateur();
        }
    }

    // --- DÉPLACEMENT RÉCURSIF ---
    public void deplacerTacheColonne(Tache tache, String nouvelleColonne) {
        if (tache != null && nouvelleColonne != null && colonnesDisponibles.contains(nouvelleColonne)) {
            deplacerColonneRecursif(tache, nouvelleColonne);
            notifierObservateur();
        }
    }

    private void deplacerColonneRecursif(Tache t, String nouvelleColonne) {
        t.setColonne(nouvelleColonne);
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                deplacerColonneRecursif(enfant, nouvelleColonne);
            }
        }
    }

    public void deplacerTacheDate(Tache tache, LocalDate nouvelleDate) {
        if (tache != null && nouvelleDate != null) {
            tache.setDateDebut(nouvelleDate);
            notifierObservateur();
        }
    }

    public Map<LocalDate, List<Tache>> getJours() {
        Map<LocalDate, List<Tache>> joursMap = new TreeMap<>();
        for (Tache tache : taches) {
            if (!tache.isArchived()) {
                LocalDate d = tache.getDateDebut();
                joursMap.putIfAbsent(d, new ArrayList<>());
                joursMap.get(d).add(tache);
            }
        }
        return joursMap;
    }

    // --- PROMOTION CORRIGÉE (Gère les références parentes) ---
    public TacheComposite promouvoirEnComposite(TacheSimple ancienneTache) {
        // 1. Clonage
        TacheComposite nouvelleTache = new TacheComposite(ancienneTache);

        // 2. Remplacement dans la liste principale
        int index = taches.indexOf(ancienneTache);
        if (index != -1) {
            taches.set(index, nouvelleTache);
        } else {
            taches.add(nouvelleTache);
        }

        // Mise à jour des parents potentiels
        // On cherche si un parent contient l'ancienne tâche et on la remplace
        for (Tache parentPotentiel : taches) {
            if (parentPotentiel.aDesEnfants()) {
                parentPotentiel.remplacerEnfant(ancienneTache, nouvelleTache);
            }
        }

        notifierObservateur();
        return nouvelleTache;
    }

    public LinkedList<Tache> getDependance(Tache tache) {
        if (tache != null) {
            return tache.construirDependance();
        }
        return new LinkedList<>();
    }

    public void exit(Object repo) {
        this.observateurs = new ArrayList<>();
        ((ModeleRepository) repo).save(this);
    }

    /**
     * Récupère la liste de tous les ancêtres d'une tâche (Parents, Grands-parents...).
     * @param cible La tâche dont on cherche les parents.
     * @return Une liste ordonnée : [Grand-Grand-Père, Grand-Père, Père]. Vide si racine.
     */
    public List<Tache> getParents(Tache cible) {
        List<Tache> lignee = new ArrayList<>();
        if (cible == null) return lignee;

        // On parcourt toutes les tâches racines du modèle
        for (Tache racine : taches) {
            // Si la tâche est une racine elle-même, elle n'a pas de parents
            if (racine == cible) {
                return lignee;
            }

            // On lance la recherche récursive
            if (rechercheRecursiveParents(racine, cible, lignee)) {
                return lignee;
            }
        }
        return lignee; // Retourne vide si non trouvé
    }

    /**
     * Méthode privée récursive pour construire le chemin
     */
    private boolean rechercheRecursiveParents(Tache parentActuel, Tache cible, List<Tache> lignee) {
        // 1. Si le parent actuel contient directement la cible dans ses enfants immédiats
        if (parentActuel.aDesEnfants() && parentActuel.getEnfants().contains(cible)) {
            lignee.add(parentActuel); // On ajoute le père
            return true; // On dit "C'est bon, on a trouvé le bas de la chaîne"
        }

        // 2. Sinon, on creuse dans les enfants du parent actuel
        if (parentActuel.aDesEnfants()) {
            for (Tache enfant : parentActuel.getEnfants()) {
                // Appel récursif
                if (rechercheRecursiveParents(enfant, cible, lignee)) {
                    // Si on remonte true, c'est que la cible est quelque part sous cet enfant.
                    // Donc 'parentActuel' est un Grand-Père (ou arrière-grand-père).
                    // On l'insère au DÉBUT de la liste pour respecter l'ordre chronologique (Haut -> Bas)
                    lignee.add(0, parentActuel);
                    return true;
                }
            }
        }

        return false; // Pas trouvé dans cette branche
    }

    private Tache getFirstParent() {

    }
}