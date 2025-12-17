package com.example.trello;

import com. example.trello.Modele.Modele;
import com.example.trello. Modele.Tache;
import com.example.trello.Modele.TacheSimple;
import com.example.trello. Vue.VueKanban;
import com.example.trello.Vue.VueListe;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale qui lance l'application
 * Respecte le pattern MVC
 */
public class App extends Application {

    private Modele modele;

    @Override
    public void start(Stage primaryStage) {
        // Initialise le modèle
        modele = new Modele();

        // Ajoute quelques tâches d'exemple
        ajouterTachesExempleVueListe();

        // Initialise la vue Kanban
        VueListe vueListe = new VueListe(modele);

        modele.setTypeVue(Modele.VUE_LISTE);
        modele.ajouterObservateur(vueListe);

        Scene scene = new Scene(vueListe, 900, 600);

        // Configure la fenêtre principale
        primaryStage.setTitle("Trello - Vue Liste");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    /**
     * Ajoute quelques tâches d'exemple pour démonstration
     */
    private void ajouterTachesExempleVueKanban() {
        // Tâches "À faire"
        Tache tache1 = new TacheSimple("Tâche 1", "Première tâche à faire", "lundi", "À faire", 4);
        tache1.setEtat(Tache.ETAT_A_FAIRE);
        modele.ajouterTache(tache1);

        Tache tache2 = new TacheSimple("Tâche 2", "Deuxième tâche à faire", "samedi", "À faire", 2);
        tache2.setEtat(Tache.ETAT_A_FAIRE);
        modele.ajouterTache(tache2);

        // Tâche "En cours"
        Tache tache3 = new TacheSimple("Tâche 3", "Tâche en cours de réalisation", "mercredi", "En cours",4);
        tache3.setEtat(Tache.ETAT_EN_COURS);
        modele.ajouterTache(tache3);

        // Tâche "Terminé"
        Tache tache4 = new TacheSimple("Tâche 4", "Tâche terminée", "mercredi", "Terminé",6);
        tache4.setEtat(Tache.ETAT_TERMINE);
        modele.ajouterTache(tache4);
    }

    /**
     * Ajoute quelques tâches de test pour démonstration
     */
    private void ajouterTachesExempleVueListe() {
        // Tâches pour lundi
        TacheSimple t1 = new TacheSimple("Réunion d'équipe", "lundi", "Réunion hebdomadaire avec l'équipe projet");
        modele.ajouterTache(t1);

        TacheSimple t2 = new TacheSimple("Planification sprint", "lundi","Définir les objectifs de la semaine");
        modele.ajouterTache(t2);

        // Tâches pour mardi
        TacheSimple t3 = new TacheSimple("tâche 1", "mardi", "10/01/2026");
        modele.ajouterTache(t3);

        // Tâches pour mercredi
        TacheSimple t4 = new TacheSimple("Code review", "mercredi", "Relire les PRs en attente");
        modele.ajouterTache(t4);

        TacheSimple t5 = new TacheSimple("Tests unitaires", "mercredi", "Compléter la couverture de tests");
        modele.ajouterTache(t5);

        // Tâche pour jeudi
        TacheSimple t6 = new TacheSimple("Démo client", "jeudi", "Présenter les nouvelles fonctionnalités");
        modele.ajouterTache(t6);

        // Tâches pour vendredi
        TacheSimple t7 = new TacheSimple("tâche 2", "vendredi", "15/01/2026");
        modele.ajouterTache(t7);

        TacheSimple t8 = new TacheSimple("Rétrospective", "vendredi","Bilan de la semaine écoulée");
        modele.ajouterTache(t8);

        // Tâche pour samedi
        TacheSimple t9 = new TacheSimple("tâche 3", "samedie", "17/01/2026");
        modele.ajouterTache(t9);
    }

    /**
     * Point d'entrée de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
}