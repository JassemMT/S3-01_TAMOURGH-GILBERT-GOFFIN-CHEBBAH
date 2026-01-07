package com.example.trello.Modele;

import com.example.trello.Vue.Observateur;
// import com.example.trello.Data.ModeleRepository;

import java.io.Serializable;
import java.time.LocalDate; // <--- IMPORTANT
import java.util.*;
import java.util.stream.Collectors;

public class Modele implements Sujet, Serializable {

    private static final long serialVersionUID = 1L;

    public static final int VUE_KANBAN = 1;
    public static final int VUE_LISTE = 2;
    public static final int VUE_GANTT = 3;

    private int type_vue;

    private List<Observateur> observateurs;
    private List<Tache> taches;
    private Set<String> colonnesDisponibles;

    public Modele() {
        this.observateurs = new ArrayList<>();
        this.taches = new ArrayList<>();
        this.type_vue = VUE_KANBAN;
        this.colonnesDisponibles = new LinkedHashSet<>();

        // Initialisation des colonnes
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

    // --- GESTION VUES ---
    public void setTypeVue(int type) { if (type >= VUE_KANBAN && type <= VUE_GANTT) { this.type_vue = type; notifierObservateur(); } }
    public int getTypeVue() { return type_vue; }

    // --- GESTION TACHES ---
    public List<Tache> getTaches() { return taches.stream().filter(t -> !t.isArchived()).collect(Collectors.toList()); }

    public void ajouterTache(Tache tache) { if (tache != null && !taches.contains(tache)) { taches.add(tache); notifierObservateur(); } }
    public void supprimerTache(Tache tache) { if (tache != null) { taches.remove(tache); notifierObservateur(); } }
    public void archiverTache(Tache tache) { if (tache != null) { tache.setEtat(Tache.ETAT_ARCHIVE); notifierObservateur(); } }

    // --- GESTION COLONNES ---
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

    public void deplacerTacheColonne(Tache tache, String nouvelleColonne) {
        if (tache != null && nouvelleColonne != null && colonnesDisponibles.contains(nouvelleColonne)) {
            tache.setColonne(nouvelleColonne);
            if (tache instanceof TacheComposite){
                for (Tache t : tache.getEnfants()) {
                    t.setColonne(nouvelleColonne);
                }
            }
            notifierObservateur();
        }
    }

    // --- GESTION DATES (Remplacement de la gestion "Jours String") ---

    // Ancienne méthode : deplacerTacheJour(String) -> Supprimée
    // Nouvelle méthode :
    public void deplacerTacheDate(Tache tache, LocalDate nouvelleDate) {
        if (tache != null && nouvelleDate != null) {
            tache.setDateDebut(nouvelleDate); // Utilise la nouvelle méthode de Tache
            notifierObservateur();
        }
    }

    // Ancienne méthode : getJours() avec String -> Remplacée par LocalDate
    public Map<LocalDate, List<Tache>> getJours() {
        // TreeMap pour que les dates soient automatiquement triées
        Map<LocalDate, List<Tache>> joursMap = new TreeMap<>();

        for (Tache tache : taches) {
            if (!tache.isArchived()) {
                LocalDate d = tache.getDateDebut();
                // Si la date n'existe pas encore dans la map, on crée la liste
                joursMap.putIfAbsent(d, new ArrayList<>());
                // On ajoute la tâche
                joursMap.get(d).add(tache);
            }
        }
        return joursMap;
    }

    // --- PROMOTION & COMPOSITE ---

    public TacheComposite promouvoirEnComposite(TacheSimple ancienneTache) {
        TacheComposite nouvelleTache = new TacheComposite(ancienneTache);
        int index = taches.indexOf(ancienneTache);
        if (index != -1) {
            taches.set(index, nouvelleTache);
        } else {
            taches.add(nouvelleTache);
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
}