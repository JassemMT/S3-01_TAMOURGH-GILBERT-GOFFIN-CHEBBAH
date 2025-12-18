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
    private Set<String> colonnesDisponibles;

    public Modele() {
        this.observateurs = new ArrayList<>();
        this.taches = new ArrayList<>();
        this.type_vue = VUE_KANBAN;
        this.colonnesDisponibles = new LinkedHashSet<>();
        // Catégories par défaut (qui ne sont plus forcément des états)
        this.colonnesDisponibles.add("À faire");
        this.colonnesDisponibles.add("En cours");
        this.colonnesDisponibles.add("Terminé");
    }

    // ... (Méthodes Observateur et Vue inchangées) ...
    @Override public void ajouterObservateur(Observateur o) { if (o != null && !observateurs.contains(o)) observateurs.add(o); }
    @Override public void supprimerObservateur(Observateur o) { observateurs.remove(o); }
    @Override public void notifierObservateur() { for (Observateur obs : observateurs) obs.actualiser(this); }
    public void setTypeVue(int type) { if (type >= VUE_KANBAN && type <= VUE_GANTT) { this.type_vue = type; notifierObservateur(); } }
    public int getTypeVue() { return type_vue; }
    public List<Tache> getTaches() { return taches.stream().filter(t -> !t.isArchived()).collect(Collectors.toList()); }
    public void ajouterTache(Tache tache) { if (tache != null && !taches.contains(tache)) { taches.add(tache); notifierObservateur(); } }
    public void supprimerTache(Tache tache) { if (tache != null) { taches.remove(tache); notifierObservateur(); } }
    public void archiverTache(Tache tache) { if (tache != null) { tache.setEtat(Tache.ETAT_ARCHIVE); notifierObservateur(); } }

    // NOUVEAU : Accesseur pour que l'éditeur puisse lister les colonnes
    public Set<String> getColonnesDisponibles() {
        return new LinkedHashSet<>(colonnesDisponibles);
    }

    public Map<String, List<Tache>> getColonnes() {
        Map<String, List<Tache>> colonnesMap = new LinkedHashMap<>();
        for (String nomCol : colonnesDisponibles) colonnesMap.put(nomCol, new ArrayList<>());
        for (Tache tache : taches) {
            if (!tache.isArchived()) {
                String nomCol = tache.getColonne();
                // Sécurité si la colonne n'existe plus
                if (nomCol == null || !colonnesDisponibles.contains(nomCol)) { nomCol = "À faire"; tache.setColonne(nomCol); }
                colonnesMap.get(nomCol).add(tache);
            }
        }
        return colonnesMap;
    }

    public void ajouterColonne(String nc) { if (nc != null && !nc.isEmpty() && colonnesDisponibles.add(nc.trim())) notifierObservateur(); }

    public void renommerColonne(String ancienNom, String nouveauNom) {
        if (ancienNom == null || nouveauNom == null || nouveauNom.trim().isEmpty()) return;
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
        if ("À faire".equals(nomColonne)) return;
        if (colonnesDisponibles.contains(nomColonne)) {
            for (Tache t : taches) { if (nomColonne.equals(t.getColonne())) t.setColonne("À faire"); }
            colonnesDisponibles.remove(nomColonne);
            notifierObservateur();
        }
    }

    // CHANGELENT RELATIF A LA CONFUSION ÉTAT/COLONNE
    public void deplacerTache(Tache tache, String nouvelleColonne) {
        if (tache != null && nouvelleColonne != null && colonnesDisponibles.contains(nouvelleColonne)) {
            // On change SEULEMENT la colonne (Catégorie)
            tache.setColonne(nouvelleColonne);

            // SUPPRESSION de la mise à jour automatique de l'état (tache.setEtat...)
            // L'état est maintenant indépendant.

            notifierObservateur();
        }
    }

    // Stub
    public LinkedList<Tache> getDependance(Tache tache) { return new LinkedList<>(); }
}