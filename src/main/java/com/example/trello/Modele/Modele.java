package com.example.trello.Modele;

import com.example.trello.Vue.Observateur;
// import de repository si nécessaire (ex: com.example.trello.Data.ModeleRepository)

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Modele implements Sujet, Serializable {

    private static final long serialVersionUID = 1L;

    public static final int VUE_KANBAN = 1;
    public static final int VUE_LISTE = 2;
    public static final int VUE_GANTT = 3;

    private int type_vue;
    // On marque transient pour ne pas sérialiser les vues (qui ne sont pas sérialisables)
    private transient List<Observateur> observateurs;
    private List<Tache> taches;
    private Set<String> colonnesDisponibles;
    private Set<String> joursDisponibles;

    public Modele() {
        this.observateurs = new ArrayList<>();
        this.taches = new ArrayList<>();
        this.type_vue = VUE_KANBAN;
        this.colonnesDisponibles = new LinkedHashSet<>();
        this.joursDisponibles = new LinkedHashSet<>();

        this.colonnesDisponibles.add("Principal");
        this.colonnesDisponibles.add("En cours");
        this.colonnesDisponibles.add("Terminé");

        this.joursDisponibles.add("Lundi");
        this.joursDisponibles.add("Mardi");
        this.joursDisponibles.add("Mercredi");
        this.joursDisponibles.add("Jeudi");
        this.joursDisponibles.add("Vendredi");
        this.joursDisponibles.add("Samedi");
        this.joursDisponibles.add("Dimanche");
    }

    // Méthode pour réinitialiser les observateurs après désérialisation
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
    public void supprimerTache(Tache tache) { if (tache != null) { taches.remove(tache); notifierObservateur(); } }
    public void archiverTache(Tache tache) { if (tache != null) { tache.setEtat(Tache.ETAT_ARCHIVE); notifierObservateur(); } }

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
            notifierObservateur();
        }
    }

    public void deplacerTacheJour(Tache tache, String nouveauJour) {
        if (Tache.JOURS_AUTORISES.contains(nouveauJour)) {
            tache.setJour(nouveauJour);
            notifierObservateur();
        }
    }

    public Map<String, List<Tache>> getJours() {
        Map<String, List<Tache>> joursMap = new LinkedHashMap<>();
        for (String jour : joursDisponibles) joursMap.put(jour, new ArrayList<>());
        for (Tache tache : taches) {
            if (!tache.isArchived()) {
                String jourTache = tache.getJour();
                if (joursMap.containsKey(jourTache)) {
                    joursMap.get(jourTache).add(tache);
                }
            }
        }
        return joursMap;
    }

    // --- NOUVEAU : Méthode de Promotion Dynamique ---
    /**
     * Transforme une TacheSimple en TacheComposite, met à jour la liste
     * et notifie les observateurs.
     */
    public TacheComposite promouvoirEnComposite(TacheSimple ancienneTache) {
        // 1. Création du clone Composite
        TacheComposite nouvelleTache = new TacheComposite(ancienneTache);

        // 2. Remplacement dans la liste principale
        int index = taches.indexOf(ancienneTache);
        if (index != -1) {
            taches.set(index, nouvelleTache);
        } else {
            taches.add(nouvelleTache);
        }

        // 3. Notification pour rafraîchir les vues
        notifierObservateur();

        return nouvelleTache;
    }

    // --- MODIFIÉ : Utilisation du Pattern Composite ---
    public LinkedList<Tache> getDependance(Tache tache) {
        if (tache != null) {
            // Délègue à la tâche (Simple renvoie vide, Composite renvoie ses enfants récursivement)
            return tache.construirDependance();
        }
        return new LinkedList<>();
    }

    // Méthode de sauvegarde (si ModeleRepository est défini ailleurs)
    // J'ai laissé ton code, mais attention aux types génériques si ModeleRepository n'est pas importé
    public void exit(Object repo) { // J'ai mis Object car je n'ai pas la classe ModeleRepository, remet le bon type
        this.observateurs = new ArrayList<>();
        // ((ModeleRepository) repo).save(this); // Décommenter et adapter
    }
}