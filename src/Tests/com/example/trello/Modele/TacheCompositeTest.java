package com.example.trello.Modele;

import org.junit.jupiter. api.Test;
import org. junit.jupiter.api.BeforeEach;
import static org. junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util. List;

/**
 * Tests JUnit pour la classe TacheComposite
 * Validation de la gestion des tâches composites (avec enfants)
 */
public class TacheCompositeTest {

    private TacheComposite tache;

    @BeforeEach
    void setUp() {
        tache = new TacheComposite("Tâche composite", "Commentaire test", "Lundi", "Principal", 120);
    }

    // ===== TESTS DES CONSTRUCTEURS =====

    @Test
    void testConstructeurComplet() {
        TacheComposite t = new TacheComposite("Ma tâche", "Mon commentaire", "Mardi", "En cours", 60);

        assertEquals("Ma tâche", t.getLibelle());
        assertEquals("Mon commentaire", t.getCommentaire());
        assertEquals("Mardi", t. getJour());
        assertEquals("En cours", t.getColonne());
        assertEquals(60, t.getDureeEstimee());
        assertEquals(Tache. ETAT_A_FAIRE, t.getEtat());
        assertEquals("#C5D3D0", t.getColor());
        assertFalse(t.aDesEnfants());
    }

    @Test
    void testConstructeurParDefaut() {
        TacheComposite t = new TacheComposite();

        assertEquals("", t.getLibelle());
        assertEquals("", t. getCommentaire());
        assertEquals(Tache.ETAT_A_FAIRE, t. getEtat());
        assertFalse(t.aDesEnfants());
    }

    @Test
    void testConstructeurPromotionDepuisTacheSimple() {
        TacheSimple simple = new TacheSimple("Tâche simple", "Commentaire", "Mercredi", "En cours", 90);
        simple.setEtat(Tache.ETAT_EN_COURS);
        simple.setColor("#AABBCC");

        TacheComposite composite = new TacheComposite(simple);

        // Vérification que tous les attributs ont été copiés
        assertEquals("Tâche simple", composite.getLibelle());
        assertEquals("Commentaire", composite.getCommentaire());
        assertEquals("Mercredi", composite.getJour());
        assertEquals("En cours", composite.getColonne());
        assertEquals(90, composite.getDureeEstimee());
        assertEquals(Tache.ETAT_EN_COURS, composite.getEtat());
        assertEquals("#AABBCC", composite.getColor());
        assertFalse(composite.aDesEnfants());
    }

    // ===== TESTS DES GETTERS ET SETTERS =====

    @Test
    void testGetSetLibelle() {
        tache.setLibelle("Nouveau libellé");
        assertEquals("Nouveau libellé", tache.getLibelle());
    }

    @Test
    void testGetSetCommentaire() {
        tache.setCommentaire("Nouveau commentaire");
        assertEquals("Nouveau commentaire", tache.getCommentaire());
    }

    @Test
    void testGetSetEtat() {
        tache.setEtat(Tache.ETAT_EN_COURS);
        assertEquals(Tache. ETAT_EN_COURS, tache.getEtat());

        tache.setEtat(Tache.ETAT_TERMINE);
        assertEquals(Tache.ETAT_TERMINE, tache.getEtat());
    }

    @Test
    void testGetSetColonne() {
        tache.setColonne("Terminé");
        assertEquals("Terminé", tache.getColonne());
    }

    @Test
    void testGetSetDureeEstimee() {
        tache.setDureeEstimee(180);
        assertEquals(180, tache. getDureeEstimee());
    }

    @Test
    void testGetSetColor() {
        tache.setColor("#FF5733");
        assertEquals("#FF5733", tache.getColor());
    }

    // ===== TESTS DE LA GESTION DES ENFANTS =====

    @Test
    void testAjouterEnfantSimple() {
        TacheSimple enfant = new TacheSimple("Sous-tâche", "Commentaire");

        tache.ajouterEnfant(enfant);

        assertTrue(tache.aDesEnfants());
        assertEquals(1, tache.getEnfants().size());
        assertTrue(tache.getEnfants().contains(enfant));
    }

    @Test
    void testAjouterPlusieursEnfants() {
        TacheSimple enfant1 = new TacheSimple("Sous-tâche 1", "");
        TacheSimple enfant2 = new TacheSimple("Sous-tâche 2", "");
        TacheSimple enfant3 = new TacheSimple("Sous-tâche 3", "");

        tache. ajouterEnfant(enfant1);
        tache.ajouterEnfant(enfant2);
        tache.ajouterEnfant(enfant3);

        assertTrue(tache.aDesEnfants());
        assertEquals(3, tache.getEnfants().size());
        assertTrue(tache.getEnfants().contains(enfant1));
        assertTrue(tache.getEnfants().contains(enfant2));
        assertTrue(tache. getEnfants().contains(enfant3));
    }

    @Test
    void testAjouterEnfantNull() {
        int tailleInitiale = tache.getEnfants().size();

        tache.ajouterEnfant(null);

        assertEquals(tailleInitiale, tache.getEnfants().size());
    }

    @Test
    void testAjouterEnfantDuplique() {
        TacheSimple enfant = new TacheSimple("Sous-tâche", "");

        tache.ajouterEnfant(enfant);
        tache.ajouterEnfant(enfant); // Ajout du même enfant

        // L'enfant ne doit être ajouté qu'une seule fois
        assertEquals(1, tache.getEnfants().size());
    }

    @Test
    void testAjouterEnfantSoiMeme() {
        int tailleInitiale = tache. getEnfants().size();

        tache.ajouterEnfant(tache); // Tentative d'ajout de soi-même

        // La taille ne doit pas changer
        assertEquals(tailleInitiale, tache.getEnfants().size());
        assertFalse(tache.aDesEnfants());
    }

    @Test
    void testAjouterEnfantComposite() {
        TacheComposite enfant = new TacheComposite("Sous-tâche composite", "", "Lundi", "Principal", 60);

        tache. ajouterEnfant(enfant);

        assertTrue(tache.aDesEnfants());
        assertEquals(1, tache.getEnfants().size());
        assertTrue(tache.getEnfants().contains(enfant));
    }

    @Test
    void testGetEnfantsRetourneCopie() {
        TacheSimple enfant = new TacheSimple("Enfant", "");
        tache.ajouterEnfant(enfant);

        List<Tache> enfants1 = tache.getEnfants();
        List<Tache> enfants2 = tache.getEnfants();

        // Les deux listes ne doivent pas être la même instance
        assertNotSame(enfants1, enfants2);

        // Mais elles doivent contenir les mêmes éléments
        assertEquals(enfants1, enfants2);
    }

    @Test
    void testADesEnfantsVrai() {
        assertFalse(tache.aDesEnfants());

        TacheSimple enfant = new TacheSimple("Enfant", "");
        tache.ajouterEnfant(enfant);

        assertTrue(tache.aDesEnfants());
    }

    @Test
    void testADesEnfantsFaux() {
        assertFalse(tache.aDesEnfants());
    }

    @Test
    void testImmutabiliteListeEnfants() {
        TacheSimple enfant = new TacheSimple("Enfant", "");
        tache.ajouterEnfant(enfant);

        List<Tache> enfants = tache.getEnfants();
        enfants.clear(); // Tentative de modification de la liste retournée

        // La liste interne ne doit pas être affectée
        assertEquals(1, tache.getEnfants().size());
        assertTrue(tache.aDesEnfants());
    }

    // ===== TESTS DE LA HIÉRARCHIE ET DÉPENDANCES =====

    @Test
    void testHierarchieMultiNiveaux() {
        TacheComposite parent = new TacheComposite("Parent", "", "Lundi", "Principal", 0);
        TacheComposite enfant = new TacheComposite("Enfant", "", "Lundi", "Principal", 0);
        TacheSimple petitEnfant = new TacheSimple("Petit-enfant", "");

        parent.ajouterEnfant(enfant);
        enfant.ajouterEnfant(petitEnfant);

        assertTrue(parent.aDesEnfants());
        assertTrue(enfant.aDesEnfants());
        assertFalse(petitEnfant.aDesEnfants());

        assertEquals(1, parent.getEnfants().size());
        assertEquals(1, enfant.getEnfants().size());
        assertEquals(0, petitEnfant.getEnfants().size());
    }

    @Test
    void testConstruirDependanceUnNiveau() {
        TacheSimple enfant1 = new TacheSimple("Enfant 1", "");
        TacheSimple enfant2 = new TacheSimple("Enfant 2", "");

        tache.ajouterEnfant(enfant1);
        tache.ajouterEnfant(enfant2);

        LinkedList<Tache> dependances = tache.construirDependance();

        assertEquals(2, dependances.size());
        assertTrue(dependances.contains(enfant1));
        assertTrue(dependances.contains(enfant2));
    }

    @Test
    void testConstruirDependanceMultiNiveaux() {
        TacheComposite enfant = new TacheComposite("Enfant", "", "Lundi", "Principal", 0);
        TacheSimple petitEnfant1 = new TacheSimple("Petit-enfant 1", "");
        TacheSimple petitEnfant2 = new TacheSimple("Petit-enfant 2", "");

        tache.ajouterEnfant(enfant);
        enfant.ajouterEnfant(petitEnfant1);
        enfant.ajouterEnfant(petitEnfant2);

        LinkedList<Tache> dependances = tache.construirDependance();

        // Doit contenir :  enfant, petitEnfant1, petitEnfant2
        assertEquals(3, dependances.size());
        assertTrue(dependances.contains(enfant));
        assertTrue(dependances.contains(petitEnfant1));
        assertTrue(dependances.contains(petitEnfant2));
    }

    @Test
    void testConstruirDependanceVide() {
        LinkedList<Tache> dependances = tache.construirDependance();

        assertNotNull(dependances);
        assertTrue(dependances.isEmpty());
    }

    @Test
    void testConstruirDependanceOrdre() {
        TacheComposite enfant1 = new TacheComposite("Enfant 1", "", "Lundi", "Principal", 0);
        TacheSimple sousEnfant1 = new TacheSimple("Sous-enfant 1", "");
        TacheSimple enfant2 = new TacheSimple("Enfant 2", "");

        tache.ajouterEnfant(enfant1);
        enfant1.ajouterEnfant(sousEnfant1);
        tache.ajouterEnfant(enfant2);

        LinkedList<Tache> dependances = tache.construirDependance();

        // Ordre attendu : enfant1, sousEnfant1, enfant2
        assertEquals(3, dependances.size());
        assertEquals(enfant1, dependances. get(0));
        assertEquals(sousEnfant1, dependances.get(1));
        assertEquals(enfant2, dependances.get(2));
    }

    // ===== TESTS D'INTÉGRITÉ =====

    @Test
    void testModificationCompleteAttributs() {
        TacheComposite t = new TacheComposite("Tâche", "", "Lundi", "Principal", 0);

        t.setLibelle("Nouveau libellé");
        t.setCommentaire("Nouveau commentaire");
        t.setEtat(Tache.ETAT_EN_COURS);
        t.setColonne("En cours");
        t.setJour("Vendredi");
        t.setDureeEstimee(240);
        t.setColor("#FF0000");

        assertEquals("Nouveau libellé", t.getLibelle());
        assertEquals("Nouveau commentaire", t. getCommentaire());
        assertEquals(Tache.ETAT_EN_COURS, t.getEtat());
        assertEquals("En cours", t.getColonne());
        assertEquals("Vendredi", t.getJour());
        assertEquals(240, t.getDureeEstimee());
        assertEquals("#FF0000", t. getColor());
    }

    @Test
    void testAjoutEnfantsApresModification() {
        tache.setLibelle("Modifié");
        tache.setEtat(Tache.ETAT_EN_COURS);

        TacheSimple enfant = new TacheSimple("Enfant", "");
        tache.ajouterEnfant(enfant);

        assertTrue(tache. aDesEnfants());
        assertEquals("Modifié", tache. getLibelle());
        assertEquals(Tache.ETAT_EN_COURS, tache. getEtat());
    }

    @Test
    void testToString() {
        tache.setLibelle("Ma tâche composite");
        assertEquals("Ma tâche composite", tache.toString());
    }
}