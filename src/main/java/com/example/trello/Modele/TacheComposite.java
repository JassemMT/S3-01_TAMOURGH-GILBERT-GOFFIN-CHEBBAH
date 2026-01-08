package com.example.trello.Modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Représente une tâche composite (ou tâche "conteneur") dans le pattern Composite.
 * <p>
 * Une TacheComposite peut contenir une liste de sous-tâches (enfants), qui peuvent elles-mêmes
 * être des tâches simples ou composites. Elle permet de structurer le projet de manière
 * hiérarchique (arborescence).
 * </p>
 */
public class TacheComposite extends Tache implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Liste contenant les sous-tâches directes de cette tâche. */
    private List<Tache> enfants;

    /**
     * Constructeur complet pour créer une nouvelle tâche composite.
     *
     * @param libelle       Le titre de la tâche.
     * @param commentaire   La description ou notes.
     * @param dateDebut     La date de début.
     * @param colonne       La colonne (état) initiale.
     * @param dureeEstimee  La durée estimée.
     */
    public TacheComposite(String libelle, String commentaire, LocalDate dateDebut, String colonne, int dureeEstimee) {
        super(libelle, commentaire, dateDebut, colonne, dureeEstimee);
        this.enfants = new ArrayList<>();
    }

    /**
     * Constructeur de "Promotion".
     * <p>
     * Permet de transformer une {@link TacheSimple} existante en {@link TacheComposite}
     * en conservant toutes ses propriétés (libellé, dates, état, couleur, etc.).
     * C'est utilisé quand on glisse une tâche sur une autre pour créer un groupe.
     * </p>
     *
     * @param t La tâche simple source à promouvoir.
     */
    public TacheComposite(TacheSimple t) {
        super(t.getLibelle(), t.getCommentaire(), t.getDateDebut(), t.getColonne(), t.getDureeEstimee());
        this.enfants = new ArrayList<>();
        this.setEtat(t.getEtat());
        this.setColor(t.getColor());
    }

    /**
     * Constructeur par défaut.
     * Initialise une liste d'enfants vide.
     */
    public TacheComposite() {
        super();
        this.enfants = new ArrayList<>();
    }

    /**
     * Ajoute une sous-tâche à la liste des enfants.
     * <p>
     * Inclut des vérifications de sécurité pour éviter les doublons
     * et l'ajout de la tâche à elle-même (cycle immédiat).
     * </p>
     *
     * @param t La tâche à ajouter.
     */
    @Override
    public void ajouterEnfant(Tache t) {
        if (t != null && !enfants.contains(t) && t != this) {
            enfants.add(t);
        }
    }

    /**
     * Récupère la liste des sous-tâches.
     *
     * @return Une nouvelle liste (copie défensive) contenant les enfants,
     * pour éviter la modification directe de la liste interne depuis l'extérieur.
     */
    @Override
    public List<Tache> getEnfants() {
        return new ArrayList<>(enfants);
    }

    /**
     * Vérifie si la tâche contient des sous-tâches.
     *
     * @return {@code true} si la liste des enfants n'est pas vide, {@code false} sinon.
     */
    @Override
    public boolean aDesEnfants() {
        return !enfants.isEmpty();
    }

    /**
     * Construit récursivement la liste plate de toutes les tâches descendantes.
     * <p>
     * Parcourt l'arbre des sous-tâches (enfants, petits-enfants, etc.) pour
     * retourner une liste linéaire. Utile pour les vues nécessitant une liste plate (Gantt, etc.).
     * </p>
     *
     * @return Une LinkedList contenant tous les descendants.
     */
    @Override
    public LinkedList<Tache> construirDependance() {
        LinkedList<Tache> dependances = new LinkedList<>();
        for (Tache enfant : enfants) {
            dependances.add(enfant);
            dependances.addAll(enfant.construirDependance());
        }
        return dependances;
    }

    /**
     * Remplace une sous-tâche spécifique par une nouvelle instance.
     * <p>
     * Cette méthode est cruciale lors de la "promotion" d'une tâche enfant :
     * le parent doit mettre à jour sa référence vers le nouvel objet composite.
     * </p>
     *
     * @param ancienne La tâche à remplacer.
     * @param nouvelle La nouvelle tâche qui prend sa place.
     */
    @Override
    public void remplacerEnfant(Tache ancienne, Tache nouvelle) {
        int index = enfants.indexOf(ancienne);
        if (index != -1) {
            enfants.set(index, nouvelle);
        }
    }

    /**
     * Définit la date de début et propage les contraintes temporelles dans les deux sens (Haut et Bas).
     *
     * <p>
     * 1. <b>Propagation vers le Haut (Parents) :</b> Si la modification repousse la date de fin au-delà
     * du début du parent, le parent est décalé.
     * <br>
     * 2. <b>Propagation vers le Bas (Enfants) :</b> Si la nouvelle date de début entre en conflit
     * avec la fin d'un enfant, l'enfant est décalé récursivement pour maintenir la cohérence.
     * </p>
     *
     * @param dateDebut La nouvelle date de début.
     * @param parent    La tâche parente (nécessaire pour la remontée car lien unilatéral).
     * @param modele    Le modèle (nécessaire pour trouver les grands-parents).
     */
    public void setDateDebut(LocalDate dateDebut, Tache parent, Modele modele) {
        if (dateDebut != null) {
            this.dateDebut = dateDebut;
            if (parent != null) {
                // Propagation montante : test la cohérence avec le parent
                if (this.getDateFin().isAfter(parent.getDateDebut())) {
                    Tache parentDuParent = modele.getParentDirect(parent);
                    parent.setDateDebut(this.getDateFin(), parentDuParent, modele);
                }
            }
            // Propagation descendante : boucle sur les enfants pour ajuster leurs dates si nécessaire
            for (Tache tache : enfants) {
                if (this.getDateDebut().isBefore(tache.getDateFin())) {
                    // Récursivité : on déplace l'enfant pour qu'il reste cohérent avec le parent
                    tache.setDateDebut(this.getDateDebut().minusDays(tache.dureeEstimee), this, modele);
                }
            }

        }
    }

    /**
     * Supprime une sous-tâche de la liste des enfants.
     *
     * @param t La tâche à retirer.
     */
    public void supprimerEnfant(Tache t) {
        if (enfants != null) {
            enfants.remove(t);
        }
    }
}