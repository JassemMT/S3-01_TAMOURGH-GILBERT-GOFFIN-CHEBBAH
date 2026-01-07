package com.example.trello.Modele;

import com.example.trello.Vue. Observateur;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api. Assertions.*;

import java.util.*;



/**
 * Tests JUnit pour la classe Modele
 * Validation de la gestion des tâches, des colonnes et du pattern Observateur
 */
public class ModeleTest {

    private Modele modele;
    private MockObservateur observateur;

    @BeforeEach
    void setUp() {
        modele = new Modele();
        observateur = new MockObservateur();
    }

    // ===== TESTS DE L'INITIALISATION =====

    @Test
    void testConstructeur() {
        assertNotNull(modele);
        assertEquals(Modele.VUE_KANBAN, modele.getTypeVue());
        assertTrue(modele.getTaches().isEmpty());

        Set<String> colonnes = modele.getColonnesDisponibles();
        assertEquals(3, colonnes.size());
        assertTrue(colonnes.contains("Principal"));
        assertTrue(colonnes. contains("En cours"));
        assertTrue(colonnes.contains("Terminé"));
    }

    // ===== TESTS DE LA GESTION DES TYPES DE VUE =====

    @Test
    void testSetTypeVueValide() {
        modele.setTypeVue(Modele.VUE_LISTE);
        assertEquals(Modele.VUE_LISTE, modele.getTypeVue());

        modele.setTypeVue(Modele. VUE_GANTT);
        assertEquals(Modele.VUE_GANTT, modele.getTypeVue());

        modele.setTypeVue(Modele.VUE_KANBAN);
        assertEquals(Modele.VUE_KANBAN, modele.getTypeVue());
    }

    @Test
    void testSetTypeVueInvalide() {
        modele.setTypeVue(Modele.VUE_KANBAN);
        int vueInitiale = modele.getTypeVue();

        modele.setTypeVue(0);
        assertEquals(vueInitiale, modele.getTypeVue());

        modele.setTypeVue(4);
        assertEquals(vueInitiale, modele.getTypeVue());

        modele.setTypeVue(-1);
        assertEquals(vueInitiale, modele. getTypeVue());
    }

    @Test
    void testConstantesVue() {
        assertEquals(1, Modele.VUE_KANBAN);
        assertEquals(2, Modele.VUE_LISTE);
        assertEquals(3, Modele.VUE_GANTT);
    }

    // ===== TESTS DE LA GESTION DES TÂCHES =====

    @Test
    void testAjouterTacheSimple() {
        TacheSimple t = new TacheSimple("Tâche simple", "Commentaire");

        modele.ajouterTache(t);

        assertEquals(1, modele.getTaches().size());
        assertTrue(modele.getTaches().contains(t));
    }

    @Test
    void testAjouterTacheComposite() {
        TacheComposite t = new TacheComposite("Tâche composite", "", "Lundi", "Principal", 0);

        modele.ajouterTache(t);

        assertEquals(1, modele.getTaches().size());
        assertTrue(modele.getTaches().contains(t));
    }

    @Test
    void testAjouterPlusieursTachesMixtes() {
        TacheSimple t1 = new TacheSimple("Tâche 1", "");
        TacheComposite t2 = new TacheComposite("Tâche 2", "", "Lundi", "Principal", 0);
        TacheSimple t3 = new TacheSimple("Tâche 3", "");

        modele. ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(t3);

        assertEquals(3, modele. getTaches().size());
    }

    @Test
    void testAjouterTacheNull() {
        int tailleInitiale = modele. getTaches().size();

        modele.ajouterTache(null);

        assertEquals(tailleInitiale, modele.getTaches().size());
    }

    @Test
    void testAjouterTacheDupliquee() {
        TacheSimple t = new TacheSimple("Tâche", "");

        modele.ajouterTache(t);
        modele.ajouterTache(t);

        assertEquals(1, modele.getTaches().size());
    }

    @Test
    void testSupprimerTacheValide() {
        TacheSimple t1 = new TacheSimple("Tâche 1", "");
        TacheSimple t2 = new TacheSimple("Tâche 2", "");

        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        assertEquals(2, modele.getTaches().size());

        modele.supprimerTache(t1);

        assertEquals(1, modele. getTaches().size());
        assertFalse(modele. getTaches().contains(t1));
        assertTrue(modele.getTaches().contains(t2));
    }

    @Test
    void testSupprimerTacheNull() {
        TacheSimple t = new TacheSimple("Tâche", "");
        modele.ajouterTache(t);
        int tailleInitiale = modele. getTaches().size();

        modele.supprimerTache(null);

        assertEquals(tailleInitiale, modele.getTaches().size());
    }

    @Test
    void testSupprimerTacheNonPresente() {
        TacheSimple t1 = new TacheSimple("Tâche 1", "");
        TacheSimple t2 = new TacheSimple("Tâche 2", "");

        modele.ajouterTache(t1);
        int tailleInitiale = modele. getTaches().size();

        modele.supprimerTache(t2);

        assertEquals(tailleInitiale, modele.getTaches().size());
    }

    // ===== TESTS DE L'ARCHIVAGE =====

    @Test
    void testArchiverTacheSimple() {
        TacheSimple t = new TacheSimple("Tâche", "");
        modele.ajouterTache(t);

        assertEquals(1, modele.getTaches().size());
        assertFalse(t.isArchived());

        modele. archiverTache(t);

        assertTrue(t.isArchived());
        assertEquals(Tache.ETAT_ARCHIVE, t.getEtat());
        assertEquals(0, modele.getTaches().size());
    }

    @Test
    void testArchiverTacheComposite() {
        TacheComposite t = new TacheComposite("Tâche", "", "Lundi", "Principal", 0);
        modele.ajouterTache(t);

        modele.archiverTache(t);

        assertTrue(t.isArchived());
        assertEquals(0, modele.getTaches().size());
    }

    @Test
    void testArchiverTacheNull() {
        TacheSimple t = new TacheSimple("Tâche", "");
        modele.ajouterTache(t);

        modele.archiverTache(null);

        assertFalse(t.isArchived());
    }

    @Test
    void testGetTachesExcluArchivees() {
        TacheSimple t1 = new TacheSimple("Tâche 1", "");
        TacheSimple t2 = new TacheSimple("Tâche 2", "");
        TacheSimple t3 = new TacheSimple("Tâche 3", "");

        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(t3);

        assertEquals(3, modele.getTaches().size());

        modele.archiverTache(t2);

        assertEquals(2, modele.getTaches().size());
        assertTrue(modele.getTaches().contains(t1));
        assertFalse(modele.getTaches().contains(t2));
        assertTrue(modele. getTaches().contains(t3));
    }

    // ===== TESTS DE LA GESTION DES COLONNES =====

    @Test
    void testGetColonnesDisponibles() {
        Set<String> colonnes = modele.getColonnesDisponibles();

        assertNotNull(colonnes);
        assertEquals(3, colonnes.size());
        assertTrue(colonnes.contains("Principal"));
        assertTrue(colonnes.contains("En cours"));
        assertTrue(colonnes.contains("Terminé"));
    }

    @Test
    void testGetColonnesDisponiblesRetourneCopie() {
        Set<String> colonnes1 = modele.getColonnesDisponibles();
        Set<String> colonnes2 = modele.getColonnesDisponibles();

        assertNotSame(colonnes1, colonnes2);
        assertEquals(colonnes1, colonnes2);
    }

    @Test
    void testGetColonnesRepartition() {
        TacheSimple t1 = new TacheSimple("Tâche 1", "", "Lundi", "Principal", 60);
        TacheSimple t2 = new TacheSimple("Tâche 2", "", "Lundi", "En cours", 60);
        TacheComposite t3 = new TacheComposite("Tâche 3", "", "Lundi", "Principal", 60);

        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.ajouterTache(t3);

        Map<String, List<Tache>> colonnes = modele.getColonnes();

        assertNotNull(colonnes);
        assertEquals(3, colonnes.size());

        assertEquals(2, colonnes.get("Principal").size());
        assertEquals(1, colonnes.get("En cours").size());
        assertEquals(0, colonnes.get("Terminé").size());

        assertTrue(colonnes.get("Principal").contains(t1));
        assertTrue(colonnes.get("Principal").contains(t3));
        assertTrue(colonnes. get("En cours").contains(t2));
    }

    @Test
    void testGetColonnesAvecTacheArchivee() {
        TacheSimple t1 = new TacheSimple("Tâche 1", "", "Lundi", "Principal", 60);
        TacheSimple t2 = new TacheSimple("Tâche 2", "", "Lundi", "Principal", 60);

        modele.ajouterTache(t1);
        modele.ajouterTache(t2);
        modele.archiverTache(t2);

        Map<String, List<Tache>> colonnes = modele.getColonnes();

        assertEquals(1, colonnes.get("Principal").size());
        assertTrue(colonnes.get("Principal").contains(t1));
        assertFalse(colonnes.get("Principal").contains(t2));
    }

    @Test
    void testGetColonnesAvecColonneInvalide() {
        TacheSimple t = new TacheSimple("Tâche", "", "Lundi", "ColonneInexistante", 60);
        modele.ajouterTache(t);

        Map<String, List<Tache>> colonnes = modele.getColonnes();

        assertEquals("Principal", t.getColonne());
        assertEquals(1, colonnes.get("Principal").size());
        assertTrue(colonnes.get("Principal").contains(t));
    }

    // ===== TESTS DU PATTERN OBSERVATEUR =====

    @Test
    void testAjouterObservateur() {
        modele.ajouterObservateur(observateur);

        modele.ajouterTache(new TacheSimple("Tâche", ""));

        assertTrue(observateur.estNotifie());
        assertEquals(1, observateur.getNombreNotifications());
    }

    @Test
    void testAjouterObservateurNull() {
        modele.ajouterObservateur(null);

        modele.ajouterTache(new TacheSimple("Tâche", ""));
        // Aucune exception ne doit être levée
    }

    @Test
    void testAjouterObservateurDuplique() {
        modele.ajouterObservateur(observateur);
        modele.ajouterObservateur(observateur);

        modele.ajouterTache(new TacheSimple("Tâche", ""));

        assertEquals(1, observateur.getNombreNotifications());
    }

    @Test
    void testSupprimerObservateur() {
        modele.ajouterObservateur(observateur);
        modele.supprimerObservateur(observateur);

        modele.ajouterTache(new TacheSimple("Tâche", ""));

        assertFalse(observateur.estNotifie());
    }

    @Test
    void testNotificationAjoutTache() {
        modele.ajouterObservateur(observateur);

        modele.ajouterTache(new TacheSimple("Tâche", ""));

        assertTrue(observateur.estNotifie());
    }

    @Test
    void testNotificationSuppressionTache() {
        modele.ajouterObservateur(observateur);
        TacheSimple t = new TacheSimple("Tâche", "");
        modele.ajouterTache(t);

        observateur.reinitialiser();
        modele.supprimerTache(t);

        assertTrue(observateur.estNotifie());
    }

    @Test
    void testNotificationArchivageTache() {
        modele.ajouterObservateur(observateur);
        TacheSimple t = new TacheSimple("Tâche", "");
        modele.ajouterTache(t);

        observateur.reinitialiser();
        modele.archiverTache(t);

        assertTrue(observateur.estNotifie());
    }

    @Test
    void testNotificationChangementVue() {
        modele.ajouterObservateur(observateur);

        modele.setTypeVue(Modele. VUE_LISTE);

        assertTrue(observateur.estNotifie());
    }

    @Test
    void testPlusieursObservateurs() {
        MockObservateur obs1 = new MockObservateur();
        MockObservateur obs2 = new MockObservateur();
        MockObservateur obs3 = new MockObservateur();

        modele.ajouterObservateur(obs1);
        modele.ajouterObservateur(obs2);
        modele.ajouterObservateur(obs3);

        modele.ajouterTache(new TacheSimple("Tâche", ""));

        assertTrue(obs1.estNotifie());
        assertTrue(obs2.estNotifie());
        assertTrue(obs3.estNotifie());
    }

    // ===== TESTS SPÉCIFIQUES AVEC HIÉRARCHIE =====

    @Test
    void testAjouterTacheCompositeAvecEnfants() {
        TacheComposite parent = new TacheComposite("Parent", "", "Lundi", "Principal", 0);
        TacheSimple enfant = new TacheSimple("Enfant", "");
        parent.ajouterEnfant(enfant);

        modele.ajouterTache(parent);

        assertEquals(1, modele. getTaches().size());
        assertTrue(parent.aDesEnfants());
    }

    // ===== CLASSE MOCK POUR LES TESTS =====

    /**
     * Classe Mock d'un Observateur pour les tests
     */
    private static class MockObservateur implements Observateur {
        private int nombreNotifications = 0;

        @Override
        public void actualiser(Sujet s) {
            nombreNotifications++;
        }

        public boolean estNotifie() {
            return nombreNotifications > 0;
        }

        public int getNombreNotifications() {
            return nombreNotifications;
        }

        public void reinitialiser() {
            nombreNotifications = 0;
        }
    }
}