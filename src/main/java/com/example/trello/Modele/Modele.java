package com.example.trello.Modele;

import com.example.trello.Vue.Observateur;
// import com.example.trello.Data.ModeleRepository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe principale du Modèle (MVC) représentant l'état global de l'application.
 * <p>
 * Cette classe agit comme un "Sujet" dans le pattern <b>Observateur</b>. Elle centralise
 * toutes les données (tâches, colonnes, vue active) et la logique métier.
 * Elle notifie les vues (Observateurs) à chaque modification d'état.
 * </p>
 * <p>
 * Elle implémente {@link Serializable} pour permettre la sauvegarde complète du projet,
 * avec une gestion spécifique des attributs non-sérialisables (transient).
 * </p>
 */
public class Modele implements Sujet, Serializable {

    private static final long serialVersionUID = 1L;

    /** Constante pour identifier la Vue Kanban. */
    public static final int VUE_KANBAN = 1;
    /** Constante pour identifier la Vue Liste. */
    public static final int VUE_LISTE = 2;
    /** Constante pour identifier la Vue Gantt. */
    public static final int VUE_GANTT = 3;

    private int type_vue;

    /**
     * Liste des observateurs (Vues).
     * <br><b>Transient :</b> Cette liste n'est pas sauvegardée dans le fichier car les composants
     * graphiques JavaFX ne sont pas sérialisables. Elle est reconstruite au chargement.
     */
    private transient List<Observateur> observateurs;

    private List<Tache> taches;
    private Set<String> colonnesDisponibles;

    /**
     * Constructeur par défaut.
     * Initialise les listes et définit les colonnes par défaut.
     */
    public Modele() {
        this.observateurs = new ArrayList<>();
        this.taches = new ArrayList<>();
        this.type_vue = VUE_KANBAN;
        this.colonnesDisponibles = new LinkedHashSet<>();

        this.colonnesDisponibles.add("Principal");
        this.colonnesDisponibles.add("En cours");
        this.colonnesDisponibles.add("Terminé");
    }

    /**
     * Méthode spéciale appelée par la machine virtuelle Java lors de la désérialisation.
     * Elle permet de réinitialiser la liste {@code observateurs} qui était {@code null}
     * car marquée {@code transient}.
     *
     * @return L'instance du modèle restaurée.
     */
    private Object readResolve() {
        if (observateurs == null) {
            observateurs = new ArrayList<>();
        }
        return this;
    }

    /**
     * Ajoute un observateur (une Vue) à la liste de notification.
     *
     * @param o L'observateur à ajouter.
     */
    @Override public void ajouterObservateur(Observateur o) {
        if (observateurs == null) observateurs = new ArrayList<>();
        if (o != null && !observateurs.contains(o)) observateurs.add(o);
    }

    /**
     * Retire un observateur de la liste.
     * @param o L'observateur à retirer.
     */
    @Override public void supprimerObservateur(Observateur o) {
        if (observateurs != null) observateurs.remove(o);
    }

    /**
     * Déclenche la mise à jour de toutes les Vues abonnées.
     * Doit être appelée après chaque modification de données (ajout, suppression, déplacement...).
     */
    @Override public void notifierObservateur() {
        if (observateurs != null) {
            for (Observateur obs : observateurs) obs.actualiser(this);
        }
    }

    /**
     * Change la vue active (Kanban, Liste, Gantt) et notifie l'interface.
     * @param type Le code entier de la vue (voir constantes VUE_*).
     */
    public void setTypeVue(int type) { if (type >= VUE_KANBAN && type <= VUE_GANTT) { this.type_vue = type; notifierObservateur(); } }

    /** @return Le code de la vue actuellement affichée. */
    public int getTypeVue() { return type_vue; }

    /**
     * Récupère la liste des tâches actives (non archivées).
     * @return Une liste filtrée de tâches.
     */
    public List<Tache> getTaches() { return taches.stream().filter(t -> !t.isArchived()).collect(Collectors.toList()); }

    /**
     * Ajoute une nouvelle tâche racine au projet.
     * @param tache La tâche à ajouter.
     */
    public void ajouterTache(Tache tache) { if (tache != null && !taches.contains(tache)) { taches.add(tache); notifierObservateur(); } }

    /**
     * Supprime définitivement une tâche et toute sa descendance.
     * <p>
     * Gère automatiquement le détachement du parent (si c'est une sous-tâche)
     * avant de lancer la suppression récursive.
     * </p>
     *
     * @param tache La tâche à supprimer.
     */
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

    /**
     * Méthode récursive pour supprimer une tâche et nettoyer ses enfants.
     */
    private void supprimerRecursif(Tache t) {
        taches.remove(t);
        if (t.aDesEnfants()) {
            List<Tache> enfants = new ArrayList<>(t.getEnfants()); // Copie pour éviter les problèmes de modification de liste
            for (Tache enfant : enfants) {
                supprimerRecursif(enfant);
            }
        }
    }

    /**
     * Archive une tâche (changement d'état sans suppression).
     * @param tache La tâche à archiver.
     */
    public void archiverTache(Tache tache) {
        if (tache != null) {
            archiverRecursif(tache);
            notifierObservateur();
        }
    }

    /**
     * Archive récursivement une tâche et tous ses enfants.
     */
    private void archiverRecursif(Tache t) {
        t.setEtat(Tache.ETAT_ARCHIVE);
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                archiverRecursif(enfant);
            }
        }
    }

    /**
     * Restaure une tâche archivée (remet l'état "A FAIRE").
     * @param tache La tâche à restaurer.
     */
    public void desarchiverTache(Tache tache) {
        if (tache != null) {
            desarchiverRecursif(tache);
            notifierObservateur();
        }
    }

    /**
     * Restaure récursivement une tâche et tous ses enfants.
     */
    private void desarchiverRecursif(Tache t) {
        t.setEtat(Tache.ETAT_A_FAIRE);
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                desarchiverRecursif(enfant);
            }
        }
    }

    /**
     * Récupère la liste de toutes les tâches ayant le statut "Archivé".
     * @return Une liste de tâches archivées.
     */
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

    /** @return Un ensemble (Set) des noms de colonnes disponibles. */
    public Set<String> getColonnesDisponibles() { return new LinkedHashSet<>(colonnesDisponibles); }

    /**
     * Organise les tâches par colonne pour la Vue Kanban.
     * <p>
     * Crée une Map où la clé est le nom de la colonne et la valeur est la liste des tâches
     * contenues dans cette colonne. Gère le cas des colonnes supprimées (renvoi vers "Principal").
     * </p>
     *
     * @return Une Map ordonnée {NomColonne -> ListeTaches}.
     */
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

    /**
     * Crée une nouvelle colonne dans le tableau.
     * @param nc Le nom de la nouvelle colonne.
     */
    public void ajouterColonne(String nc) { if (nc != null && !nc.isEmpty() && colonnesDisponibles.add(nc.trim())) notifierObservateur(); }

    /**
     * Renomme une colonne existante et met à jour toutes les tâches qui y sont liées.
     *
     * @param ancienNom Le nom actuel.
     * @param nouveauNom Le nouveau nom souhaité.
     */
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

    /**
     * Supprime une colonne.
     * <p>
     * Les tâches contenues dans cette colonne ne sont pas supprimées mais déplacées
     * vers la colonne par défaut "Principal".
     * </p>
     * @param nomColonne Le nom de la colonne à supprimer.
     */
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

    /**
     * Tente de déplacer une tâche vers une nouvelle colonne avec validation stricte.
     * <p>
     * <b>Règle Métier :</b> Une sous-tâche ne peut pas être déplacée dans une colonne différente
     * de celle de son parent. Si cette règle est violée, une exception est levée.
     * </p>
     *
     * @param tache La tâche à déplacer.
     * @param nouvelleColonne Le nom de la colonne cible.
     * @throws Exception Si le déplacement viole la contrainte parent/enfant.
     */
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

    /**
     * Applique le changement de colonne récursivement à la tâche et à tous ses enfants.
     */
    private void deplacerColonneRecursif(Tache t, String nouvelleColonne) {
        t.setColonne(nouvelleColonne);
        if (t.aDesEnfants()) {
            for (Tache enfant : t.getEnfants()) {
                deplacerColonneRecursif(enfant, nouvelleColonne);
            }
        }
    }

    /**
     * Change la date de début d'une tâche et propage les contraintes temporelles.
     *
     * @param tache La tâche concernée.
     * @param nouvelleDate La nouvelle date.
     */
    public void deplacerTacheDate(Tache tache, LocalDate nouvelleDate) {
        if (tache != null && nouvelleDate != null) {
            tache.setDateDebut(nouvelleDate, this.getParentDirect(tache), this);
            notifierObservateur();
        }
    }

    /**
     * Récupère les tâches organisées par date de début (utile pour un calendrier).
     * @return Une Map {Date -> ListeTaches} triée par ordre chronologique.
     */
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

    /**
     * Transforme une TacheSimple en TacheComposite (Pattern Promotion).
     * <p>
     * Cette méthode crée une nouvelle instance de {@link TacheComposite} avec les données
     * de l'ancienne tâche, puis remplace la référence dans la liste principale ou chez le parent.
     * </p>
     *
     * @param ancienneTache La tâche simple à promouvoir.
     * @return La nouvelle tâche composite créée.
     */
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

    /**
     * Récupère la liste linéaire de toutes les dépendances d'une tâche.
     * @param tache La tâche racine.
     * @return Une liste chaînée de tous les descendants.
     */
    public LinkedList<Tache> getDependance(Tache tache) {
        if (tache != null) {
            return tache.construirDependance();
        }
        return new LinkedList<>();
    }

    /**
     * Prépare la fermeture de l'application en sauvegardant les données.
     * Réinitialise la liste des observateurs pour éviter de sérialiser les Vues.
     *
     * @param repo Le repository responsable de la persistance (JSON/Binaire).
     */
    public void exit(Object repo) {
        this.observateurs = new ArrayList<>();
        ((ModeleRepository) repo).save(this); // Cast nécessaire selon votre architecture
    }


    /**
     * Récupère la liste de tous les ancêtres d'une tâche donnée.
     *
     * @param cible La tâche dont on cherche les ancêtres.
     * @return Une liste ordonnée [Grand-Père, Père]. Vide si racine.
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
     * Change l'ordre des colonnes dans l'affichage.
     * <p>
     * Échange la position de deux colonnes dans la liste interne.
     * </p>
     *
     * @param nomColSource La colonne déplacée.
     * @param nomColCible La colonne cible (avec qui on échange).
     */
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
        String tmp_name = nomColSource;
        listeOrdonnee.set(indexSource, nomColCible);
        listeOrdonnee.set(indexCible, tmp_name);


        // 3. Reconstruction du LinkedHashSet (qui garantit l'ordre d'itération)
        this.colonnesDisponibles = new LinkedHashSet<>(listeOrdonnee);

        // 4. Notification de la vue pour redessiner les colonnes dans le bon ordre
        notifierObservateur();
    }


    /**
     * Méthode de recherche récursive (DFS - Depth First Search).
     * Remplit la liste 'lignee' avec le chemin vers la cible.
     */
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
     * Trouve le parent direct (père) d'une tâche.
     * <p>
     * Comme la structure est unilatérale (Parent -> Enfant), nous devons parcourir
     * tout l'arbre depuis les racines pour trouver "qui possède cet enfant".
     *
     * </p>
     *
     * @param cible La tâche dont on cherche le père.
     * @return La tâche parente, ou {@code null} si la cible est une racine (pas de parent).
     */
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

    /**
     * Recherche récursive du parent direct.
     */
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