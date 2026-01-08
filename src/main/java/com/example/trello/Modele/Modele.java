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

    // permet de recréer la liste de observateurs après une deserialisation
    private Object readResolve() {
        if (observateurs == null) {
            observateurs = new ArrayList<>();
        }
        return this;
    }

    // permet d'ajouter un observateur à la liste des observateurs du modèle
    @Override public void ajouterObservateur(Observateur o) {
        if (observateurs == null) observateurs = new ArrayList<>();
        if (o != null && !observateurs.contains(o)) observateurs.add(o);
    }
    // permet de supprimer un observateur donné présent dans la liste des observateurs
    @Override public void supprimerObservateur(Observateur o) {
        if (observateurs != null) observateurs.remove(o);
    }

    // permet d'actualiser tous les observateurs
    @Override public void notifierObservateur() {
        if (observateurs != null) {
            for (Observateur obs : observateurs) obs.actualiser(this);
        }
    }
    // Setter & Getter pour le type de la vue en cours
    public void setTypeVue(int type) { if (type >= VUE_KANBAN && type <= VUE_GANTT) { this.type_vue = type; notifierObservateur(); } }
    public int getTypeVue() { return type_vue; }

    // permet de récupérer toutes les taches sans les taches archivées
    public List<Tache> getTaches() { return taches.stream().filter(t -> !t.isArchived()).collect(Collectors.toList()); }

    // ajoute une tache donnée à la liste des taches et actualise les observateurs
    public void ajouterTache(Tache tache) { if (tache != null && !taches.contains(tache)) { taches.add(tache); notifierObservateur(); } }

    // permet de supprimer une tache donnée
    public void supprimerTache(Tache tache) {
        if (tache != null) {
            // on détache la tache de son pere
            // On utilise la méthode suivante pour trouver le père
            Tache parent = getParentDirect(tache);
            if (parent != null) {
                parent.supprimerEnfant(tache);
                }

            // supprime de façon récursive sa présence dans la liste des taches
            supprimerRecursif(tache);

            // actualise les observateurs
            notifierObservateur();
        }
    }

    // méthode pour supprimer récursivement une tache ainsi que ses enfants
    private void supprimerRecursif(Tache t) {
        taches.remove(t);
        if (t.aDesEnfants()) {
            List<Tache> enfants = new ArrayList<>(t.getEnfants()); // Copie pour éviter les problèmes de modification de liste
            for (Tache enfant : enfants) {
                supprimerRecursif(enfant);
            }
        }
    }

    // permet d'archiver une tache
    public void archiverTache(Tache tache) {
        if (tache != null) {
            archiverRecursif(tache);
            notifierObservateur();
        }
    }

    // permet d'archiver une tache ainsi que ses enfants
    private void archiverRecursif(Tache t) {
        t.setEtat(Tache.ETAT_ARCHIVE);
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                archiverRecursif(enfant);
            }
        }
    }

    // permet de désarchiver une tache
    public void desarchiverTache(Tache tache) {
        if (tache != null) {
            desarchiverRecursif(tache);
            notifierObservateur();
        }
    }
    // désarchive une tache donnée ainsi que ses enfants de façon récursive
    private void desarchiverRecursif(Tache t) {
        t.setEtat(Tache.ETAT_A_FAIRE);
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                desarchiverRecursif(enfant);
            }
        }
    }

    // permet de récupérer toutes les taches archivées sous forme de liste
    public List<Tache> getTachesArchives() {
        //autre syntaxe possible avec boucle et conditionnelle simplement :
        List<Tache> archives = new ArrayList<>();
        for (Tache t : taches) {
            if (t.isArchived()) {
                archives.add(t);
            }
        }
        return archives;

    }

    // retourne la liste des colonnes existantes et disponibles
    public Set<String> getColonnesDisponibles() { return new LinkedHashSet<>(colonnesDisponibles); }

    // permet de récupérer sous forme de dictionnaire les colonnes ainsi que les taches attribuées à celles-ci
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

    // permet d'ajouter une colonne donnée à la liste des colonnes
    public void ajouterColonne(String nc) { if (nc != null && !nc.isEmpty() && colonnesDisponibles.add(nc.trim())) notifierObservateur(); }

    // permet de renommer une colonne en renseignant son nom courant ainsi que le nouveau
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

    // permet de supprimer une colonne donnée
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

    // déplacement avec contrainte forte (Sous-tâche bloquée par le parent)
    // permet de déplacer une tache donnée dans une colonne donnée, cela en vérifiant la validité de ce déplacement selon l'emplacement du parent
    public void deplacerTacheColonne(Tache tache, String nouvelleColonne) throws Exception {

        if (tache == null || nouvelleColonne == null || !colonnesDisponibles.contains(nouvelleColonne)) {
            return;
        }

        // Récupérer le parent direct de la tache passée en paramètre
        Tache parent = getParentDirect(tache);

        // on vérifie l'existance d'un parent direct
        if (parent != null) {
            // Cela signifie que c'est une sous-tâche, la tache a un parent
            // On regarde si la colonne cible est différente de la colonne actuel du parent
            if (!parent.getColonne().equals(nouvelleColonne)) {
                throw new Exception("Interdit : Une sous-tâche doit rester dans la colonne de son parent (" + parent.getColonne() + ").");
            }
        }

        // Si tout va bien (soit c'est une racine, soit la colonne est valide), on déplace
        deplacerColonneRecursif(tache, nouvelleColonne);
        notifierObservateur();
    }

    // permet de deplacer une tache ainsi que ses enfants dans la colonne cible de façon récursive
    private void deplacerColonneRecursif(Tache t, String nouvelleColonne) {
        t.setColonne(nouvelleColonne);
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                deplacerColonneRecursif(enfant, nouvelleColonne);
            }
        }
    }

    // permet de changer la date d'une tache
    public void deplacerTacheDate(Tache tache, LocalDate nouvelleDate) {
        if (tache != null && nouvelleDate != null) {
            tache.setDateDebut(nouvelleDate, this.getParentDirect(tache), this);
            notifierObservateur();
        }
    }

    // permet de récupérer un dictionnaire indiquant les taches inscrites pour chaque jours
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

    // permet de convertir une tache simple en une composite (gère les références parentes)
    public TacheComposite promouvoirEnComposite(TacheSimple ancienneTache) {
        // Clonage de la tache simple
        TacheComposite nouvelleTache = new TacheComposite(ancienneTache);

        // Remplacement dans la liste principale
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

    // permet d'obtenir une liste de dépendances d'une tache donnée
    public LinkedList<Tache> getDependance(Tache tache) {
        if (tache != null) {
            return tache.construirDependance();
        }
        return new LinkedList<>();
    }

    // méthode utilisé pour la serialization, réinitialisant la liste des observateurs et sauvegardant les données
    public void exit(Object repo) {
        this.observateurs = new ArrayList<>();
        ((ModeleRepository) repo).save(this);
    }


    // Récupère la liste de tous les ancêtres d'une tâche donnée (Parents, Grands-parents...)
    // Renvoie une liste ordonnée : [Grand-Grand-Père, Grand-Père, Père]. Vide si racine.
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

    // --- NOUVELLE MÉTHODE POUR LE DRAG & DROP DES COLONNES ---
    public void deplacerColonneOrdre(String nomColSource, String nomColCible) {
        // Vérifications de sécurité
        if (nomColSource == null || nomColCible == null || nomColSource.equals(nomColCible)) return;
        if (!colonnesDisponibles.contains(nomColSource) || !colonnesDisponibles.contains(nomColCible)) return;

        // 1. Conversion en Liste pour manipuler l'ordre
        List<String> listeOrdonnee = new ArrayList<>(colonnesDisponibles);

        int indexSource = listeOrdonnee.indexOf(nomColSource);
        int indexCible = listeOrdonnee.indexOf(nomColCible);

        // 2. Déplacement
        // On retire l'élément source
        listeOrdonnee.remove(indexSource);

        // Petite subtilité : si l'élément source était avant la cible,
        // l'index de la cible a reculé de 1 suite à la suppression.
        if (indexSource < indexCible) {
            indexCible--;
        }

        // On insère à la nouvelle position
        listeOrdonnee.add(indexCible, nomColSource);

        // 3. Reconstruction du LinkedHashSet (qui garantit l'ordre d'itération)
        this.colonnesDisponibles = new LinkedHashSet<>(listeOrdonnee);

        // 4. Notification de la vue pour redessiner les colonnes dans le bon ordre
        notifierObservateur();
    }


    // méthode privée permettant de construire le chemin de façon récursive
    private boolean rechercheRecursiveParents(Tache parentActuel, Tache cible, List<Tache> lignee) {
        // Si le parent actuel contient directement la cible dans ses enfants immédiats
        if (parentActuel.aDesEnfants() && parentActuel.getEnfants().contains(cible)) {
            lignee.add(parentActuel); // On ajoute le père
            return true; // On dit "C'est bon, on a trouvé le bas de la chaîne"
        }

        // Sinon, on creuse dans les enfants du parent actuel
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

    /**

     Trouve le parent direct (père) d'une tâche.
     @param cible La tâche dont on cherche le père.
     @return La tâche parente, ou null si la cible est une racine (pas de parent).*/
    public Tache getParentDirect(Tache cible) {
        if (cible == null) return null;

        for (Tache racine : taches) {
            // Si la racine est la cible, elle n'a pas de parent
            if (racine == cible) return null;

            // Sinon on cherche dans ses descendants
            Tache parentTrouve = chercherParentDirectRecursif(racine, cible);
            if (parentTrouve != null) {
                return parentTrouve;
            }
        }
        return null; // Pas trouvé (cas bizarre ou tâche orpheline)
    }

    private Tache chercherParentDirectRecursif(Tache parentActuel, Tache cible) {
        // Vérification directe : Est-ce que JE suis ton père ?
        if (parentActuel.aDesEnfants()) {
            // On regarde si la liste de mes enfants contient la cible
            if (parentActuel.getEnfants().contains(cible)) {
                return parentActuel;
            }

            // Sinon, on demande à mes enfants de chercher dans leurs propres enfants
            for (Tache enfant : parentActuel.getEnfants()) {
                Tache res = chercherParentDirectRecursif(enfant, cible);
                if (res != null) return res;
            }
        }
        return null;
    }

}