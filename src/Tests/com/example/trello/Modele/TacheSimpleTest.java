package com.example.trello.Modele;

import org.junit.jupiter. api.Test;
import org. junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api. Assertions.*;

import java.util.List;

/**
 * Tests JUnit pour la classe TacheSimple
 * Validation de la gestion des données pour les tâches simples (feuilles)
 */
public class TacheSimpleTest {

    private TacheSimple tache;

    @BeforeEach
    void setUp() {
        tache = new TacheSimple("Tâche simple", "Commentaire test", "Lundi", "Principal", 120);
    }

    // ===== TESTS DES CONSTRUCTEURS =====

    @Test
    void testConstructeurComplet() {
        TacheSimple t = new TacheSimple("Ma tâche", "Mon commentaire", "Mardi", "En cours", 60);

        assertEquals("Ma tâche", t.getLibelle());
        assertEquals("Mon commentaire", t.getCommentaire());
        assertEquals("Mardi", t.getJour());
        assertEquals("En cours", t.getColonne());
        assertEquals(60, t.getDureeEstimee());
        assertEquals(Tache.ETAT_A_FAIRE, t.getEtat());
        assertEquals("#C5D3D0", t.getColor());
    }

    @Test
    void testConstructeurSimplifie() {
        TacheSimple t = new TacheSimple("Tâche simple", "Commentaire");

        assertEquals("Tâche simple", t.getLibelle());
        assertEquals("Commentaire", t.getCommentaire());
        assertEquals("Lundi", t.getJour());
        assertEquals("Principal", t.getColonne());
        assertEquals(0, t.getDureeEstimee());
        assertEquals(Tache.ETAT_A_FAIRE, t.getEtat());
    }

    @Test
    void testConstructeurParDefaut() {
        TacheSimple t = new TacheSimple();

        assertEquals("", t.getLibelle());
        assertEquals("", t.getCommentaire());
        assertEquals(Tache.ETAT_A_FAIRE, t.getEtat());
        assertEquals("#C5D3D0", t.getColor());
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
        assertEquals("Nouveau commentaire", tache. getCommentaire());
    }

    @Test
    void testGetSetEtat() {
        tache.setEtat(Tache. ETAT_EN_COURS);
        assertEquals(Tache.ETAT_EN_COURS, tache. getEtat());

        tache.setEtat(Tache.ETAT_TERMINE);
        assertEquals(Tache.ETAT_TERMINE, tache.getEtat());

        tache.setEtat(Tache.ETAT_ARCHIVE);
        assertEquals(Tache.ETAT_ARCHIVE, tache.getEtat());
    }

    @Test
    void testGetSetColonne() {
        tache.setColonne("Terminé");
        assertEquals("Terminé", tache.getColonne());
    }

    @Test
    void testGetSetDureeEstimee() {
        tache.setDureeEstimee(180);
        assertEquals(180, tache.getDureeEstimee());
    }

    @Test
    void testGetSetColor() {
        tache.setColor("#FF5733");
        assertEquals("#FF5733", tache.getColor());
    }

    // ===== TESTS DE LA GESTION DES JOURS =====

    @Test
    void testSetJourValide() {
        tache.setJour("Mercredi");
        assertEquals("Mercredi", tache.getJour());

        tache.setJour("Dimanche");
        assertEquals("Dimanche", tache.getJour());
    }

    @Test
    void testSetJourInvalide() {
        tache.setJour("Mardi");
        assertEquals("Mardi", tache. getJour());

        // Tentative d'assigner un jour invalide - devrait revenir à "Lundi"
        tache.setJour("JourInexistant");
        assertEquals("Lundi", tache.getJour());
    }

    @Test
    void testSetJourNull() {
        tache.setJour("Mardi");
        assertEquals("Mardi", tache.getJour());

        // Tentative d'assigner null - devrait revenir à "Lundi"
        tache. setJour(null);
        assertEquals("Lundi", tache.getJour());
    }

    @Test
    void testJoursAutorises() {
        List<String> joursAttendus = List.of("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche");
        assertEquals(joursAttendus, Tache. JOURS_AUTORISES);
    }

    // ===== TESTS SPÉCIFIQUES AUX TÂCHES SIMPLES (PAS D'ENFANTS) =====

    @Test
    void testGetEnfantsRetourneListeVide() {
        List<Tache> enfants = tache.getEnfants();

        assertNotNull(enfants);
        assertTrue(enfants.isEmpty());
        assertEquals(0, enfants.size());
    }

    @Test
    void testADesEnfantsRetourneFaux() {
        assertFalse(tache.aDesEnfants());
    }

    @Test
    void testAjouterEnfantNeChangePasEtat() {
        // Une tâche simple ne peut pas avoir d'enfants
        TacheSimple enfant = new TacheSimple("Enfant", "");

        tache.ajouterEnfant(enfant);

        // L'enfant ne doit pas être ajouté
        assertFalse(tache.aDesEnfants());
        assertTrue(tache.getEnfants().isEmpty());
    }

    @Test
    void testConstruirDependanceRetourneListeVide() {
        var dependances = tache.construirDependance();

        assertNotNull(dependances);
        assertTrue(dependances.isEmpty());
    }

    // ===== TESTS DE L'ÉTAT ARCHIVE =====

    @Test
    void testIsArchivedFaux() {
        assertFalse(tache.isArchived());

        tache.setEtat(Tache.ETAT_EN_COURS);
        assertFalse(tache.isArchived());

        tache.setEtat(Tache.ETAT_TERMINE);
        assertFalse(tache.isArchived());
    }

    @Test
    void testIsArchivedVrai() {
        tache.setEtat(Tache.ETAT_ARCHIVE);
        assertTrue(tache.isArchived());
    }

    // ===== TESTS DES CONSTANTES =====

    @Test
    void testConstantesEtat() {
        assertEquals(0, Tache.ETAT_A_FAIRE);
        assertEquals(1, Tache. ETAT_EN_COURS);
        assertEquals(2, Tache.ETAT_TERMINE);
        assertEquals(3, Tache.ETAT_ARCHIVE);
    }

    // ===== TESTS D'INTÉGRITÉ =====

    @Test
    void testModificationCompleteAttributs() {
        TacheSimple t = new TacheSimple("Tâche", "Commentaire");

        t.setLibelle("Nouveau libellé");
        t.setCommentaire("Nouveau commentaire");
        t.setEtat(Tache.ETAT_EN_COURS);
        t.setColonne("En cours");
        t.setJour("Vendredi");
        t.setDureeEstimee(240);
        t.setColor("#FF0000");

        assertEquals("Nouveau libellé", t. getLibelle());
        assertEquals("Nouveau commentaire", t.getCommentaire());
        assertEquals(Tache.ETAT_EN_COURS, t.getEtat());
        assertEquals("En cours", t.getColonne());
        assertEquals("Vendredi", t.getJour());
        assertEquals(240, t.getDureeEstimee());
        assertEquals("#FF0000", t. getColor());
    }

    @Test
    void testValeurParDefautColor() {
        TacheSimple t = new TacheSimple("Tâche", "Commentaire");
        assertEquals("#C5D3D0", t.getColor());
    }

    @Test
    void testValeurParDefautEtat() {
        TacheSimple t = new TacheSimple("Tâche", "Commentaire");
        assertEquals(Tache.ETAT_A_FAIRE, t.getEtat());
        assertFalse(t.isArchived());
    }

    @Test
    void testToString() {
        tache.setLibelle("Ma tâche de test");
        assertEquals("Ma tâche de test", tache.toString());
    }
}